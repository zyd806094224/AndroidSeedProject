package com.demo.html.htmlcleaner;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DomBuilder implements XmlVisitor {
	
	private Document document;
	private Element destinationElement;
	private CleanerProperties props;
	
    protected boolean escapeXml = true;
    protected boolean deserializeCdataEntities = false;
    protected boolean strictErrorChecking = true;
    
    private static final String CSS_COMMENT_START = "/*";
	
	public DomBuilder(CleanerProperties props, boolean escapeXml, boolean deserializeCdataEntities, boolean strictErrorChecking){
		this.props = props;
		this.escapeXml = escapeXml;
		this.deserializeCdataEntities = deserializeCdataEntities;
		this.strictErrorChecking = strictErrorChecking;
	}
	
	public Document getDocument(){
		return this.document;
	}
	
	private boolean shouldEscapeOrTranslateEntities() {
		return escapeXml || props.isRecognizeUnicodeChars() || props.isTranslateSpecialEntities();
	}

	public void head(HtmlNode node, int depth) {
	
    	//
    	// For script and style nodes, check if we're set to use CDATA
    	//
    	CDATASection cdata = null;
    	if (node instanceof TagNode && props.isUseCdataFor(((TagNode)node).getName())){
    		cdata = document.createCDATASection("");
			destinationElement.appendChild(document.createTextNode(CSS_COMMENT_START));
			destinationElement.appendChild(cdata); 
    	}
    	
		if (node instanceof CommentNode) {

			CommentNode commentNode = (CommentNode) node;
			Comment comment = document.createComment( commentNode.getContent() );
			destinationElement.appendChild(comment);

		} else if (node instanceof ContentNode) {

			ContentNode contentNode = (ContentNode) node;
			String content = contentNode.getContent();
			boolean specialCase = props.isUseCdataFor(node.getParent().getName());

			if (shouldEscapeOrTranslateEntities() && !specialCase) {
				content = Utils.escapeXml(content, props, true);
			}

			if (specialCase && node instanceof CData){
				//
				// For CDATA sections we don't want to return the start and
				// end tokens. See issue #106.
				//
				content = ((CData)node).getContentWithoutStartAndEndTokens();
			}
			
			if (specialCase && deserializeCdataEntities){
				content = this.deserializeCdataEntities(content);
			}

        	if (cdata != null){
        		cdata.appendData(content);
        	} else {
				destinationElement.appendChild(document.createTextNode(content) ); 
        	}


		} else if (node instanceof TagNode) {
			
			TagNode subTagNode = (TagNode) node;
			
			//
			// XML element names are more strict in their definition
			// than  HTML tag identifiers.
			// See https://www.w3.org/TR/xml/#NT-Name
			// vs. https://html.spec.whatwg.org/multipage/parsing.html#tag-name-state
			//
			String name = Utils.sanitizeXmlIdentifier(subTagNode.getName(), props.getInvalidXmlAttributeNamePrefix());
			
			//
			// If the element name is completely invalid, treat it as text
			//
			if (name == null){
				ContentNode contentNode = new ContentNode(subTagNode.getName() + subTagNode.getText().toString());
				String content = contentNode.getContent();
				content = Utils.escapeXml(content, props, true);
				destinationElement.appendChild(document.createTextNode(content) ); 

			} else {

				if (document == null){
					try {
						document = this.createDocument(subTagNode);
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Element element = document.createElement( name );
				
				//
				// Create attributes
				//
				Map<String, String> attributes =  subTagNode.getAttributes();
				Iterator<Map.Entry<String, String>> entryIterator = attributes.entrySet().iterator();
				while (entryIterator.hasNext()) {
					Map.Entry<String, String> entry = entryIterator.next();
					String attrName = entry.getKey();
					String attrValue = entry.getValue();
					if (escapeXml) {
	        			attrValue = Utils.deserializeEntities(attrValue, props.isRecognizeUnicodeChars());
						attrValue = Utils.escapeXml(attrValue, props, true);
					}

					//
					// Fix any invalid attribute names by adding a prefix
					//
					if (!props.isAllowInvalidAttributeNames()){
						attrName = Utils.sanitizeXmlIdentifier(attrName, props.getInvalidXmlAttributeNamePrefix());
					}

					if (attrName != null && (Utils.isValidXmlIdentifier(attrName) || props.isAllowInvalidAttributeNames())){
						element.setAttribute(attrName, attrValue);

						//
						// Flag the attribute as an ID attribute if appropriate. Thanks to Chris173
						//
						if (attrName.equalsIgnoreCase("id")) {
							element.setIdAttribute(attrName, true);
						}
					}
				}
				if (destinationElement == null){
					destinationElement = document.getDocumentElement();
				} else {
					destinationElement.appendChild(element);
					destinationElement = element;
				}
				
				//
				// Hack for now, we need a better way to do this in future
				//
				for (Object token: subTagNode.getAllChildren()){
					if (token instanceof ContentNode){
						((ContentNode)token).setParent(subTagNode);
					}
				}

			}
		}

	}
	
    protected String deserializeCdataEntities(String input){
    	return Utils.deserializeEntities(input, props.isRecognizeUnicodeChars());
    }

	public void tail(HtmlNode node, int depth) {
        if (node instanceof TagNode && destinationElement.getParentNode() instanceof Element) {
            destinationElement = (Element) destinationElement.getParentNode();
        }
	}
	
    //
    // Allow overriding of serialization for implementations. See bug #167.
    //
    protected Document createDocument(TagNode rootNode) throws ParserConfigurationException{

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        
        Document document;
        
        //
        // Where a DOCTYPE is supplied in the input, ensure that this is in the output DOM. See issue #27
        //
        // Note that we may want to fix incorrect DOCTYPEs in future; there are some fairly
        // common patterns for errors with the older HTML4 doctypes.
        //
        if (rootNode.getDocType() != null){
        	String qualifiedName = rootNode.getDocType().getPart1();
        	String publicId = rootNode.getDocType().getPublicId();
        	String systemId = rootNode.getDocType().getSystemId();
        	
        	//
        	// If there is no qualified name, set it to html. See bug #153.
        	//
        	if (qualifiedName == null) qualifiedName = "html";
        	
            DocumentType documentType = impl.createDocumentType(qualifiedName, publicId, systemId);
            
            //
            // While the qualified name is "HTML" for some DocTypes, we want the actual document root name to be "html". See bug #116
            //
            if (qualifiedName.equals("HTML")) qualifiedName = "html";
            document = impl.createDocument(rootNode.getNamespaceURIOnPath(""), qualifiedName, documentType);
        } else {
        	document = builder.newDocument();
        	Element rootElement = document.createElement(rootNode.getName());
        	document.appendChild(rootElement);
        }
        
        //
        // Turn off error checking if we're allowing invalid attribute names, or if we've chosen to turn it off
        //
        if (props.isAllowInvalidAttributeNames() || strictErrorChecking == false){
        	document.setStrictErrorChecking(false);
        }
        
        
        //
        // Copy across root node attributes - see issue 127. Thanks to rasifiel for the patch
        //
        Map<String, String> attributes =  rootNode.getAttributes();
        Iterator<Map.Entry<String, String>> entryIterator = attributes.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<String, String> entry = entryIterator.next();
            String attrName = entry.getKey();
            String attrValue = entry.getValue();
            
            //
            // Fix any invalid attribute names
            //
            if (!props.isAllowInvalidAttributeNames()){
            	attrName = Utils.sanitizeXmlIdentifier(attrName, props.getInvalidXmlAttributeNamePrefix());
            }
        	
        	if (attrName != null && (Utils.isValidXmlIdentifier(attrName) || props.isAllowInvalidAttributeNames())){

        		if (escapeXml) {
        			attrValue = Utils.deserializeEntities(attrValue, props.isRecognizeUnicodeChars());
        			attrValue = Utils.escapeXml(attrValue, props, true);
        		}

        		document.getDocumentElement().setAttribute(attrName, attrValue);

        		//
        		// Flag the attribute as an ID attribute if appropriate. Thanks to Chris173
        		//
        		if (attrName.equalsIgnoreCase("id")) {
        			document.getDocumentElement().setIdAttribute(attrName, true);
        		}
        	}

        }
        
        return document;
    }

}

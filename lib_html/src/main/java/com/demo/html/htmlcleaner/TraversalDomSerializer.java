/*  Copyright (c) 2006-2019, the HtmlCleaner Project
    All rights reserved.

    Redistribution and use of this software in source and binary forms,
    with or without modification, are permitted provided that the following
    conditions are met:

    * Redistributions of source code must retain the above
      copyright notice, this list of conditions and the
      following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the
      following disclaimer in the documentation and/or other
      materials provided with the distribution.

    * The name of HtmlCleaner may not be used to endorse or promote
      products derived from this software without specific prior
      written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/

package com.demo.html.htmlcleaner;

import org.w3c.dom.Document;

import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * A traversal-based serializer for DOM; used to avoid recursion and stack overflow for large
 * HTML documents.
 */
public class TraversalDomSerializer {

	private CleanerProperties props;
	
    /**
     * Whether XML entities should be escaped or not.
     */
    protected boolean escapeXml = true;
    protected boolean deserializeCdataEntities = false;
    protected boolean strictErrorChecking = true;
    
    /**
     * @param props the HTML Cleaner properties set by the user to control the HTML cleaning.
     * @param escapeXml if true then escape XML entities
     * @param deserializeCdataEntities if true then deserialize entities in CData sections
     * @param strictErrorChecking if false then Document strict error checking is turned off
     */
    public TraversalDomSerializer(CleanerProperties props, boolean escapeXml, boolean deserializeCdataEntities, boolean strictErrorChecking){
        this.props = props;
        this.escapeXml = escapeXml;
        this.deserializeCdataEntities = deserializeCdataEntities;
        this.strictErrorChecking = strictErrorChecking;
    }

    /**
     * @param props the HTML Cleaner properties set by the user to control the HTML cleaning.
     * @param escapeXml if true then escape XML entities
     * @param deserializeCdataEntities if true then deserialize entities in CData sections
     */
    public TraversalDomSerializer(CleanerProperties props, boolean escapeXml, boolean deserializeCdataEntities) {
        this.props = props;
        this.escapeXml = escapeXml;
        this.deserializeCdataEntities = deserializeCdataEntities;
    }

    /**
     * @param props the HTML Cleaner properties set by the user to control the HTML cleaning.
     * @param escapeXml if true then escape XML entities
     */
    public TraversalDomSerializer(CleanerProperties props, boolean escapeXml) {
        this.props = props;
        this.escapeXml = escapeXml;
    }

    /**
     * @param props the HTML Cleaner properties set by the user to control the HTML cleaning.
     */
    public TraversalDomSerializer(CleanerProperties props) {
        this.props = props;
    }
    
    /**
     * @param rootNode the HTML Cleaner root node to serialize
     * @return the W3C Document object
     * @throws ParserConfigurationException if there's an error during serialization
     */
    public Document createDOM(TagNode rootNode) throws ParserConfigurationException {
    	DomBuilder builder = new DomBuilder(props, escapeXml, deserializeCdataEntities, strictErrorChecking);
    	XmlTraversor.traverse(builder, rootNode);
        return builder.getDocument();
    }
    
    public static String toString(Document doc) throws TransformerException, ParserConfigurationException{
    	DOMSource domSource = new DOMSource(doc);
    	StringWriter writer = new StringWriter();
    	StreamResult result = new StreamResult(writer);
    	TransformerFactory tf = TransformerFactory.newInstance();
    	Transformer transformer = tf.newTransformer();
    	transformer.transform(domSource, result);
    	return writer.toString();
    }
	
}

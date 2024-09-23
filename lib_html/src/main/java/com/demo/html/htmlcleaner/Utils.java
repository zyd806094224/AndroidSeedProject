/*  Copyright (c) 2006-2019, the HtmlCleaner project
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Common utilities.</p>
 *
 * Created by: Vladimir Nikic<br/>
 * Date: November, 2006.
 */
public class Utils {
	
	static final String VALID_XML_IDENTIFIER_START_CHAR_REGEX = "^[:A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d"
			+ "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff"
			+ "\\uf900-\\ufdcf\\ufdf0-\\ufffd\\x{10000}-\\x{EFFFF}]";
	static final Pattern VALID_XML_IDENTIFIER_START_CHAR_PATTERN = 
			compileUnicodePattern(VALID_XML_IDENTIFIER_START_CHAR_REGEX);
	
	/*
		The relevant production from the spec is http://www.w3.org/TR/xml/#NT-Name
		Name ::== NameStartChar NameChar *
		NameStartChar ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
		NameChar ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
	 */
	static final String VALID_XML_IDENTIFIER_CHAR_REGEX = 
			  "^[:A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6\\u00F8-\\u02ff\\u0370-\\u037d"
			+ "\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f\\u2c00-\\u2fef\\u3001-\\ud7ff"
			+ "\\uf900-\\ufdcf\\ufdf0-\\ufffd\\x{10000}-\\x{EFFFF}]"
			+ "[:A-Z_a-z\\u00C0-\\u00D6\\u00D8-\\u00F6"
			+ "\\u00F8-\\u02ff\\u0370-\\u037d\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f"
			+ "\\u2c00-\\u2fef\\u3001-\\udfff\\uf900-\\ufdcf\\ufdf0-\\ufffd\\-\\.0-9"
			+ "\\u00b7\\u0300-\\u036f\\u203f-\\u2040]*\\Z";
	static final Pattern VALID_XML_IDENTIFIER_CHAR_PATTERN = 
			compileUnicodePattern(VALID_XML_IDENTIFIER_CHAR_REGEX);

	
	/**
	 * Removes the first newline and last newline (if present) of a string
	 * @param str
	 * @return
	 */
	static String bchomp(final String str){
		return chomp(lchomp(str));
	}
	
	/**
	 * Removes the last newline (if present) of a string
	 * @param str
	 * @return
	 */
	static String chomp(final String str){
		if (str.length() ==0) {
			return str;
		}

		if (str.length() == 1) {
			final char ch = str.charAt(0);
			if (ch == '\r' || ch == '\n') {
				return "";
			}
			return str;
		}

		int lastIdx = str.length() - 1;
		final char last = str.charAt(lastIdx);

		if (last == '\n') {
			if (str.charAt(lastIdx - 1) == '\r') {
				lastIdx--;
			}
		} else if (last != '\r') {
			lastIdx++;
		}
		return str.substring(0, lastIdx);
	}
	
	/**
	 * Removes the first newline (if present) of a string
	 * @param str
	 * @return
	 */
	static String lchomp(final String str){
		if (str == null) return null;
		if (str.length() == 0) {
			return str;
		}

		if (str.length() == 1) {
			final char ch = str.charAt(0);
			if (ch == '\r' || ch == '\n') {
				return "";
			}
			return str;
		}
		
		int firstIndex = 0;
		
		final char first = str.charAt(0);
		if (first == '\n'){
			firstIndex++;
			if (str.charAt(1) == '\r') {
				firstIndex++ ;
			}
		} else if (first != '\r') {
			firstIndex = 0;
		}
		return str.substring(firstIndex, str.length());
	}
	

    /**
     * Reads content from the specified URL with specified charset into string
     * @param url
     * @param charset
     * @throws IOException
     */
    @Deprecated // Removing network I/O will make htmlcleaner better suited to a server environment which needs managed connections
    static CharSequence readUrl(URL url, String charset) throws IOException {
        StringBuilder buffer = new StringBuilder(1024);
        InputStream inputStream = url.openStream();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, charset);
            char[] charArray = new char[1024];

            int charsRead = 0;
            do {
                charsRead = reader.read(charArray);
                if (charsRead >= 0) {
                    buffer.append(charArray, 0, charsRead);
                }
            } while (charsRead > 0);
        } finally {
            inputStream.close();
        }

        return buffer;
    }
    
    /**
     * Checks if specified link is full URL.
     *
     * @param link
     * @return True, if full URl, false otherwise.
     */
    public static boolean isFullUrl(String link) {
        if (link == null) {
            return false;
        }
        link = link.trim().toLowerCase();
        return link.startsWith("http://") || link.startsWith("https://") || link.startsWith("file://");
    }
    
    /**
     * Calculates full URL for specified page URL and link
     * which could be full, absolute or relative like there can
     * be found in A or IMG tags. (Reinstated as per user request in bug 159)
     */
    public static String fullUrl(String pageUrl, String link) {
        if (isFullUrl(link)) {
            return link;
        } else if (link != null && link.startsWith("?")) {
            int qindex = pageUrl.indexOf('?');
            int len = pageUrl.length();
            if (qindex < 0) {
                return pageUrl + link;
            } else if (qindex == len - 1) {
                return pageUrl.substring(0, len - 1) + link;
            } else {
                return pageUrl + "&" + link.substring(1);
            }
        }

        boolean isLinkAbsolute = link.startsWith("/");

        if (!isFullUrl(pageUrl)) {
            pageUrl = "http://" + pageUrl;
        }

        int slashIndex = isLinkAbsolute ? pageUrl.indexOf("/", 8) : pageUrl.lastIndexOf("/");
        if (slashIndex <= 8) {
            pageUrl += "/";
        } else {
            pageUrl = pageUrl.substring(0, slashIndex + 1);
        }

        return isLinkAbsolute ? pageUrl + link.substring(1) : pageUrl + link;
    }

    /**
     * Escapes HTML string
     * @param s String to be escaped
     * @param props Cleaner properties affects escaping behaviour
     * @return
     */
    public static String escapeHtml(String s, CleanerProperties props) {
        boolean advanced = props.isAdvancedXmlEscape();
        boolean recognizeUnicodeChars = props.isRecognizeUnicodeChars();
        boolean translateSpecialEntities = props.isTranslateSpecialEntities();
        boolean transResCharsToNCR = props.isTransResCharsToNCR();
        boolean transSpecialEntitiesToNCR = props.isTransSpecialEntitiesToNCR();
        return escapeXml(s, advanced, recognizeUnicodeChars, translateSpecialEntities, false, transResCharsToNCR, transSpecialEntitiesToNCR, true);    	
    }

    /**
     * Escapes XML string.
     * @param s String to be escaped
     * @param props Cleaner properties affects escaping behaviour
     * @param isDomCreation Tells if escaped content will be part of the DOM
     */
    public static String escapeXml(String s, CleanerProperties props, boolean isDomCreation) {
        boolean advanced = props.isAdvancedXmlEscape();
        boolean recognizeUnicodeChars = props.isRecognizeUnicodeChars();
        boolean translateSpecialEntities = props.isTranslateSpecialEntities();
        boolean transResCharsToNCR = props.isTransResCharsToNCR();
        boolean transSpecialEntitiesToNCR = props.isTransSpecialEntitiesToNCR();
        return escapeXml(s, advanced, recognizeUnicodeChars, translateSpecialEntities, isDomCreation, transResCharsToNCR, transSpecialEntitiesToNCR, false);
    }
    
    /**
     * change notes:
     * 1) convert ascii characters encoded using &#xx; format to the ascii characters -- may be an attempt to slip in malicious html
     * 2) convert &#xxx; format characters to &quot; style representation if available for the character.
     * 3) convert html special entities to xml &#xxx; when outputing in xml
     * @param s
     * @param advanced
     * @param recognizeUnicodeChars
     * @param translateSpecialEntities
     * @param isDomCreation
     * @return
     * TODO Consider moving to CleanerProperties since a long list of params is misleading.
     */
    public static String escapeXml(String s, boolean advanced, boolean recognizeUnicodeChars, boolean translateSpecialEntities, 
                                   boolean isDomCreation, boolean transResCharsToNCR, boolean translateSpecialEntitiesToNCR) {
    	return escapeXml(s,advanced,recognizeUnicodeChars,translateSpecialEntities,isDomCreation,transResCharsToNCR,translateSpecialEntitiesToNCR,false);
    }
    
    /**
     * change notes:
     * 1) convert ascii characters encoded using &#xx; format to the ascii characters -- may be an attempt to slip in malicious html
     * 2) convert &#xxx; format characters to &quot; style representation if available for the character.
     * 3) convert html special entities to xml &#xxx; when outputing in xml
     * @param s
     * @param advanced
     * @param recognizeUnicodeChars
     * @param translateSpecialEntities
     * @param isDomCreation
     * @param isHtmlOutput
     * @return
     * TODO Consider moving to CleanerProperties since a long list of params is misleading.
     */
    public static String escapeXml(String s, boolean advanced, boolean recognizeUnicodeChars, boolean translateSpecialEntities, 
                                   boolean isDomCreation, boolean transResCharsToNCR, boolean translateSpecialEntitiesToNCR, boolean isHtmlOutput) {
        if (s != null) {
    		int len = s.length();
    		StringBuilder result = new StringBuilder(len);

    		for (int i = 0; i < len; i++) {
    			char ch = s.charAt(i);

    			SpecialEntity code;
    			if (ch == '&') {
    				if ( (advanced || recognizeUnicodeChars) && (i < len-1) && (s.charAt(i+1) == '#') ) {

    					i = convertToUnicode(s, isDomCreation, recognizeUnicodeChars, translateSpecialEntitiesToNCR, result, i+2);
    				} else if ((translateSpecialEntities || advanced) &&
				        (code = SpecialEntities.INSTANCE.getSpecialEntity(s.substring(i, i+Math.min(10, len-i)))) != null) {
			            if (translateSpecialEntities && code.isHtmlSpecialEntity()) {
                            if (recognizeUnicodeChars) {
                                result.append( (char)code.intValue() );
                            } else {
                                result.append( code.getDecimalNCR() );
                            }
							i += code.getKey().length() + 1;
    				    } else if (advanced ) {
    				    	//
    				    	// If we are creating a HTML DOM or outputting to the HtmlSerializer, use HTML special entities;
    				    	// otherwise we get their XML escaped version (see bug #118).
    				    	//
					        result.append(transResCharsToNCR ? code.getDecimalNCR() : code.getEscaped(isHtmlOutput || isDomCreation));
		                    i += code.getKey().length()+1;
			            } else {
			                result.append(transResCharsToNCR ? getAmpNcr() : "&amp;");
			            }
					} 

			        //
			        // If the serializer used to output is HTML rather than XML, and we have a match to a
			        // known HTML entity such as &nbsp;, we output it as-is (see bug #118)
			        //

					else if (isHtmlOutput)
					{
						// we have an ampersand and that's all we know so far
					
						code = SpecialEntities.INSTANCE.getSpecialEntity(s.substring(i, i+Math.min(10, len-i)));
					
						if ( code != null )
						{
							// It is a special entity like &nbsp; - leave it in place.
					
							result.append(code.getEscapedValue());
					
							// advance i by the length of the entity so we won't process each following character
							// key length excludes & and ; and we add 1 to skip the ;
							i += code.getKey().length()+1;
						}
						else if ( (i < len-1) && (s.charAt(i+1) == '#') )
						{
							// if the next char is a # then convert entity number to entity name (if possible)
					
							i = convert_To_Entity_Name(s, false, false, false, result, i+2);
					
							// assuming 'i' is being incremented correctly... not verified.
						}
						else
						{
							// html output but not an entity name or number
					
							result.append(transResCharsToNCR ? getAmpNcr() : "&amp;");
						}
					} else {
    				    result.append(transResCharsToNCR ? getAmpNcr() : "&amp;");
    				}
    			} else if ((code = SpecialEntities.INSTANCE.getSpecialEntityByUnicode(ch)) != null ) {

					// It's a special entity character itself
					
					if ( isHtmlOutput )
					{
						if ( "apos".equals(code.getKey()) )
						{
							// leave the apostrophes alone for html output
							// this is a cheap hack to avoid removing apostrophe from the special entities list for html output
							result.append(ch);
						}
						else
						{
							// output as entity name, or as literal character if isDomCreation
							result.append(isDomCreation? code.getHtmlString() : code.getEscapedValue());
						}
					}
					else
					{
						// if we have one of the XML reserved characters, get escaped version, otherwise, 
						// output the literal characters.
				    	if (isDomCreation && !isXmlReservedCharacter(String.valueOf(ch))){
				    		result.append(ch);
				    	} else {
							// output as entity number, or as literal character if isDomCreation
							result.append(transResCharsToNCR ? code.getDecimalNCR() : code.getEscaped(isDomCreation));
				    	}
					}

    			} else {
    				result.append(ch);
    			}
    		}

    		return result.toString();
    	}

    	return null;
    }

    private static String ampNcr;

    private static String getAmpNcr() {
        if (ampNcr == null) {
            ampNcr = SpecialEntities.INSTANCE.getSpecialEntityByUnicode('&').getDecimalNCR();
        }

        return ampNcr;
    }

    private static final Pattern ASCII_CHAR = Pattern.compile("\\p{Print}");

    /**
     * @param s
     * @param domCreation
     * @param recognizeUnicodeChars
     * @param translateSpecialEntitiesToNCR 
     * @param result
     * @param i
     * @return
     */

	// Converts Numeric Character References (NCRs) (Dec or Hex) to Character Entity References
	// ie. &#8364;	to &euro; 
	// This is almost a copy of convertToUnicode
	// only called in the case of isHtmlOutput when we see &# in the input stream

    private static int convert_To_Entity_Name(String s, boolean domCreation, boolean recognizeUnicodeChars, boolean translateSpecialEntitiesToNCR, StringBuilder result, int i) {
        StringBuilder unicode = new StringBuilder();
        int charIndex = extractCharCode(s, i, true, unicode);
        if (unicode.length() > 0) {
        	try {
        	    boolean isHex = unicode.substring(0,1).equals("x");
        	    
        	    //
        	    // Get the unicode character and code point
        	    //
        	    int codePoint = -1;
        	    char[] unicodeChar = null;    
        	    if (isHex){
        	    	codePoint = Integer.parseInt(unicode.substring(1), 16);
        	    	unicodeChar = Character.toChars(codePoint);
        	    } else {
        	    	codePoint = Integer.parseInt(unicode.toString());
        	    	unicodeChar =  Character.toChars(codePoint);
        	    }
        	    
        	    SpecialEntity specialEntity = SpecialEntities.INSTANCE.getSpecialEntityByUnicode(codePoint);
                if (unicodeChar.length == 1 && unicodeChar[0] == 0) {
                    // null character &#0Peanut for example
                    // just consume character &
                    result.append("&amp;");
                } 
				else if ( specialEntity != null )
				{
					if ( specialEntity.isHtmlSpecialEntity() )
					{
						result.append( domCreation? specialEntity.getHtmlString() : specialEntity.getEscapedValue() );
					}
					else
					{
						result.append(domCreation? specialEntity.getHtmlString():
						(translateSpecialEntitiesToNCR? (isHex? specialEntity.getHexNCR(): specialEntity.getDecimalNCR()) : 
						specialEntity.getHtmlString()));
					}
                } else if ( recognizeUnicodeChars ) {
                    // output unicode characters as their actual byte code with the exception of characters that have special xml meaning.
                    result.append( String.valueOf(unicodeChar));
                } else if ( ASCII_CHAR.matcher(new String(unicodeChar)).find()) {
                    // ascii printable character. this fancy escaping might be an attempt to slip in dangerous characters (i.e. spelling out <script> )
                    // by converting to printable characters we can more easily detect such attacks.
                    result.append(String.valueOf(unicodeChar));
                } else {
                    // unknown unicode value - output as-is
        			result.append( "&#").append(unicode).append(";" );
        		}
        	} catch (NumberFormatException e) {
        	    // should never happen now
        		result.append("&amp;#").append(unicode).append(";" );
        	}
        } else {
        	result.append("&amp;");
        }
        return charIndex;
    }


    /**
     * @param s
     * @param domCreation
     * @param recognizeUnicodeChars
     * @param translateSpecialEntitiesToNCR 
     * @param result
     * @param i
     * @return
     */
    private static int convertToUnicode(String s, boolean domCreation, boolean recognizeUnicodeChars, boolean translateSpecialEntitiesToNCR, StringBuilder result, int i) {
        StringBuilder unicode = new StringBuilder();
        int charIndex = extractCharCode(s, i, true, unicode);
        if (unicode.length() > 0) {
        	try {
        	    boolean isHex = unicode.substring(0,1).equals("x");
        	    
        	    //
        	    // Get the unicode character and code point
        	    //
        	    int codePoint = -1;
        	    char[] unicodeChar = null;    
        	    if (isHex){
        	    	codePoint = Integer.parseInt(unicode.substring(1), 16);
        	    } else {
        	    	codePoint = Integer.parseInt(unicode.toString());
        	    }
    	    	
        	    unicodeChar = Character.toChars(codePoint);
        	    
        	    SpecialEntity specialEntity = SpecialEntities.INSTANCE.getSpecialEntityByUnicode(codePoint);
                if (unicodeChar.length == 1 && unicodeChar[0] == 0) {
                    // null character &#0Peanut for example
                    // just consume character &
                    result.append("&amp;");
                } else if ( specialEntity != null &&
                        // special characters that are always escaped.
                        (!specialEntity.isHtmlSpecialEntity()
                                // OR we are not outputting unicode characters as the characters ( they are staying escaped )
                                || !recognizeUnicodeChars)) {
                    result.append(domCreation? specialEntity.getHtmlString():
                        (translateSpecialEntitiesToNCR? (isHex? specialEntity.getHexNCR(): specialEntity.getDecimalNCR()) : 
                            specialEntity.getEscapedXmlString()));
                } else if ( recognizeUnicodeChars ) {
                    // output unicode characters as their actual byte code with the exception of characters that have special xml meaning.
                    result.append( String.valueOf(unicodeChar));
                } else if ( ASCII_CHAR.matcher(new String(unicodeChar)).find()) {
                    // ascii printable character. this fancy escaping might be an attempt to slip in dangerous characters (i.e. spelling out <script> )
                    // by converting to printable characters we can more easily detect such attacks.
                    result.append(String.valueOf(unicodeChar));
                } else {
        			result.append( "&#").append(unicode).append(";" );
        		}
        	} catch (NumberFormatException e) {
        	    // should never happen now
        		result.append("&amp;#").append(unicode).append(";" );
        	}
        	catch (IllegalArgumentException e) {
        	    // code point is not a legal unicode character
        		result.append("&amp;#").append(unicode).append(";" );
        	}
        } else {
        	result.append("&amp;");
        }
        return charIndex;
    }

    // TODO have pattern consume leading 0's and discard.
    public static Pattern HEX_STRICT = Pattern.compile("^([x|X][\\p{XDigit}]+)(;?)");
    public static Pattern HEX_RELAXED = Pattern.compile("^0*([x|X][\\p{XDigit}]+)(;?)");
    public static Pattern DECIMAL = Pattern.compile("^([\\p{Digit}]+)(;?)");
    /**
     * <ul>
     * <li>(earlier code was failing on this) - &#138A; is converted by FF to 3 characters: &#138; + 'A' + ';'</li>
     * <li>&#0x138A; is converted by FF to 6? 7? characters: &#0 'x'+'1'+'3'+ '8' + 'A' + ';'
     * #0 is displayed kind of weird</li>
     * <li>&#x138A; is a single character</li>
     * </ul>
     *
     * @param s
     * @param charIndex
     * @param relaxedUnicode '&#0x138;' is treated like '&#x138;'
     * @param unicode
     * @return the index to continue scanning the source string -1 so normal loop incrementing skips the ';'
     */
    private static int extractCharCode(String s, int charIndex, boolean relaxedUnicode, StringBuilder unicode) {
        int len = s.length();
        CharSequence subSequence = s.subSequence(charIndex, Math.min(len,charIndex+15));
        Matcher matcher;
        if( relaxedUnicode ) {
            matcher = HEX_RELAXED.matcher(subSequence);
        } else {
            matcher = HEX_STRICT.matcher(subSequence);
        }
        // silly note: remember calling find() twice finds second match :-)
        if (matcher.find() || ((matcher = DECIMAL.matcher(subSequence)).find())) {
            // -1 so normal loop incrementing skips the ';'
            charIndex += matcher.end() -1;
            unicode.append(matcher.group(1));
        }
        return charIndex;
    }
    
    public static String sanitizeXmlIdentifier(String attName){
    	return sanitizeXmlIdentifier(attName, "hc-generated-","");
    }
    
    public static String sanitizeXmlIdentifier(String attName, String prefix){
    	return sanitizeXmlIdentifier(attName, prefix,"");
    }

    public static String sanitizeHtmlAttributeName(String name){
    	// Attribute names must consist of one or more characters other than controls, 
    	// U+0020 SPACE, U+0022 ("), U+0027 ('), U+003E (>), U+002F (/), U+003D (=), and noncharacters.
    	String regex = "[\\u0000\\u0020\\u0022\\u0027\\u003E\\u002F\\u003d]";
    	Pattern pattern = compileUnicodePattern(regex);
    	final Matcher matcher = pattern.matcher(name);
    	name = matcher.replaceAll("");
    	return name;
    }
    
    public static boolean isValidHtmlAttributeName(String name){
    	String regex = "^[^\\u0000\\u0020\\u0022\\u0027\\u003E\\u002F\\u003d]+$";
    	Pattern pattern = compileUnicodePattern(regex);
    	final Matcher matcher = pattern.matcher(name);
    	return matcher.find();
    }
    
    /**
     * Attempts to replace invalid attribute names with valid ones.
     * @param attName the attribute name to fix
     * @param prefix the prefix to use to indicate an attribute name has been altered
     * @return
     */
    public static String sanitizeXmlIdentifier(String attName, String prefix, String replacementCharacter){
    	if (Utils.isValidXmlIdentifier(attName)) return attName;

    	//
    	// Prepend with "hc-generated-" or similar prefix. Useful for
    	// identifiers that are valid apart from the start character, e.g "1a"
    	//
    	if (!Utils.isValidXmlIdentifierStartChar(attName.substring(0,1))){
    		if (!prefix.isEmpty()){
    			String generatedAttName = prefix + attName;
    			if (Utils.isValidXmlIdentifier(generatedAttName)) return generatedAttName;
    		} else {
    			//
    			// If not, strip out first character
    			//
    			String generatedAttName = attName.substring(1);
    			if (Utils.isValidXmlIdentifier(generatedAttName)) return generatedAttName;
    		}
    	}

    	//
    	// otherwise, replace or strip out invalid characters
    	//
    	String generatedAttName = Utils.replaceInvalidXmlIdentifierCharacters(attName,"");
    	if (Utils.isValidXmlIdentifier(generatedAttName)) return generatedAttName;
    	
    	//
    	// If we still have something invalid - for example none of the characters in
    	// it are valid - then return null
    	//
    	return null;
    }

    /**
     * Checks whether specified string can be valid tag name or attribute name in xml.
     * @param s String to be checked
     * @return True if string is valid xml identifier, false otherwise
     */
    public static boolean isValidXmlIdentifier(String s) {
    	if (s == null) return false;
    	Matcher matcher = VALID_XML_IDENTIFIER_CHAR_PATTERN.matcher(s);
		if (matcher.find()){
			s = null;
			matcher = null;
			return true;
		}
		return false;
    }

    /**
     * @param o
     * @return True if specified string is null of contains only whitespace characters
     */
    public static boolean isEmptyString(Object o) {
        if ( o == null ) {
            return true;
        }
        String s = o.toString();
        String text = escapeXml(s, true, false, false, false, false, false, false);
        // TODO: doesn't escapeXml handle this?
        String last = text.replace(SpecialEntities.NON_BREAKABLE_SPACE, ' ').trim();
        return last.length() == 0;
    }

    public static String[] tokenize(String s, String delimiters) {
        if (s == null) {
            return new String[] {};
        }

        StringTokenizer tokenizer = new StringTokenizer(s, delimiters);
        String result[] = new String[tokenizer.countTokens()];
        int index = 0;
        while (tokenizer.hasMoreTokens()) {
            result[index++] = tokenizer.nextToken();
        }

        return result;
    }
    
    public static boolean isXmlReservedCharacter(String c){
    	final String XML_CHARS="'\"<>&";
    	return XML_CHARS.contains(c);
    }

    /**
     * @param name
     * @return For xml element name or attribute name returns prefix (part before :) or null if there is no prefix
     */
    public static String getXmlNSPrefix(String name) {
        int colIndex = name.indexOf(':');
        if (colIndex > 0) {
            return name.substring(0, colIndex);
        }

        return null;
    }

    /**
     * @param name
     * @return For xml element name or attribute name returns name after prefix (part after :)
     */
    public static String getXmlName(String name) {
        int colIndex = name.indexOf(':');
        if (colIndex > 0 && colIndex < name.length() - 1) {
            return name.substring(colIndex + 1);
        }

        return name;
    }
    
    static boolean isValidInt(String s, int radix) {
        try {
            Integer.parseInt(s, radix);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Trims specified string from left.
     * @param s
     */
    public static String ltrim(String s) {
        if (s == null) {
            return null;
        }

        int index = 0;
        int len = s.length();

        while ( index < len && Character.isWhitespace(s.charAt(index)) ) {
            index++;
        }

        return (index >= len) ? "" : s.substring(index);
    }

    /**
     * Trims specified string from right.
     * @param s
     */
    public static String rtrim(String s) {
        if (s == null) {
            return null;
        }

        int len = s.length();
        int index = len;

        while ( index > 0 && Character.isWhitespace(s.charAt(index-1)) ) {
            index--;
        }

        return (index <= 0) ? "" : s.substring(0, index);
    }
    
    /**
     * Checks whether specified object's string representation is empty string (containing of only whitespaces).
     * @param object Object whose string representation is checked
     * @return true, if empty string, false otherwise
     */
    public static boolean isWhitespaceString(Object object) {
        if (object != null) {
            String s = object.toString();
            return s != null && "".equals(s.trim());
        }
        return false;
    }
    
    //
    // Replaces entities with actual characters
    //
    public static String deserializeEntities(String str, boolean recognizeUnicodeChars) {
    	StringBuffer buf = new StringBuffer(str);
        SpecialEntities entities = SpecialEntities.INSTANCE;
        int entityStart = -1;
        boolean numericEntity = false;
        boolean hexEntity = false;
        int maxEntityLength = entities.getMaxEntityLength();
        int i = 0;
        int length = buf.length();
        while (i < length) {
            if (buf.charAt(i) == '&') {
                entityStart = i;
                numericEntity = false;
                hexEntity = false;
                ++i;
            } else if (entityStart != -1) {
                if (buf.charAt(i) == ';') {
                    int entityValue = -1;
                    if (numericEntity) {
                        try {
                            entityValue = Integer.parseInt(
                            		buf.substring(
                                            entityStart + (hexEntity ? 3 : 2),
                                            i
                                    ),
                                    hexEntity ? 16 : 10
                            );
                        } catch (NumberFormatException e) {
                            entityValue = -1;
                        }
                        
                    	SpecialEntity entity = entities.getSpecialEntityByUnicode(entityValue);
                    	if(entity != null)
                    		entityValue = entity.intValue();
                    	else if(!recognizeUnicodeChars)
                    		entityValue = -1;
                    } else {
                    	SpecialEntity entity = entities.getSpecialEntity(buf.substring(entityStart + 1, i));
                    	if(entity != null)
                    		entityValue = entity.intValue();
                    }
                    
                    if (entityValue >= 0) {
                        char[] decodedEntity = Character.toChars(entityValue);
                        buf.replace(entityStart, i + 1, new String(decodedEntity));
                        length = buf.length();
                        i = entityStart + decodedEntity.length;
                    } else {
                        ++i;
                    }
                    entityStart = -1;
                } else {
                    if (i == entityStart + 1 && buf.charAt(i) == '#') {
                        numericEntity = true;
                    } else if (i == entityStart + 2 && numericEntity && buf.charAt(i) == 'x') {
                        hexEntity = true;
                    } else if (i - entityStart > maxEntityLength) {
                        entityStart = -1;
                    }
                    ++i;
                }
            } else {
                ++i;
            }
        }
        return buf.toString();
    }
    
    /**
     * Determines whether the initial character of an identifier is valid for XML
     * @param identifier
     * @return
     */
    public static boolean isValidXmlIdentifierStartChar(String identifier){
    	final Matcher matcher = VALID_XML_IDENTIFIER_START_CHAR_PATTERN.matcher(identifier);
    	return matcher.find();
    }

    /**
     * Strips out invalid characters from names used for XML Elements and replaces them with the specified
     * character.
     * 
     * For example, "<p%>" becomes "<p_>"
     * @param name
     * @return valid XML name
     */
    public static String replaceInvalidXmlIdentifierCharacters(String name, String replacement){
    	final String regex_repl = ""
    			+ "[^:A-Z_a-z\\u00C0\\u00D6\\u00D8-\\u00F6"
    			+ "\\u00F8-\\u02ff\\u0370-\\u037d\\u037f-\\u1fff\\u200c\\u200d\\u2070-\\u218f"
    			+ "\\u2c00-\\u2fef\\u3001-\\udfff\\uf900-\\ufdcf\\ufdf0-\\ufffd\\-\\.0-9"
    			+ "\\u00b7\\u0300-\\u036f\\u203f-\\u2040]";
    	final Pattern pattern = compileUnicodePattern(regex_repl);
    	final Matcher matcher = pattern.matcher(name);
    	name = matcher.replaceAll(replacement);

    	return name;
    }
    
    
    private static Pattern compileUnicodePattern(String pattern){
    	try {
    		return Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
    	} catch(IllegalArgumentException ex) {
    		return Pattern.compile(pattern);
    	}
    	
    }


    
}
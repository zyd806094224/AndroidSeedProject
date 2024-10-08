/*  
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

    You can contact Vladimir Nikic by sending e-mail to
    nikic_vladimir@yahoo.com. Please include the word "HtmlCleaner" in the
    subject line.
*/
package com.demo.html.htmlcleaner;

/**
 * @author patmoore
 *
 */
public enum ContentType {
    all("all"),
    /**
     * elements that have no children or content ( for example <img> ). For these elements, the check for null elements must be more than must a children/ content check.
     */
    none("none"),
    text("text");
    private final String dbCode;
    private ContentType(String dbCode) {
        this.dbCode =dbCode;
    }
    
    /**
     * @return the dbCode
     */
    public String getDbCode() {
        return dbCode;
    }
    
    public static ContentType toValue(Object value) {
        ContentType result = null;
        if ( value instanceof ContentType) {
            result = (ContentType) value;
        } else if ( value != null ) {
            String dbCode = value.toString().trim(); 
            for(ContentType contentType: ContentType.values()) {
                if ( contentType.getDbCode().equalsIgnoreCase(dbCode) || contentType.name().equalsIgnoreCase(dbCode)) {
                    result = contentType;
                    break;
                }
            }
        }
        
        return result;
    }
}

/*
 * FreeGuide
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class FreeGuideSAXHandler extends DefaultHandler {

    public FreeGuideSAXHandler(FreeGuideViewer freeGuide) {
	// Remember who called us
	this.freeGuide=freeGuide;
    }
    
    public void startDocument() throws SAXException {
	freeGuide.startDocument();
    }

    public void endDocument() throws SAXException {
	freeGuide.endDocument();
    }

    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
        freeGuide.startElement(qName, attrs);
    }

    public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
	freeGuide.endElement(qName);
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
	freeGuide.characters(new String(ch, start, length));
    }

    private FreeGuideViewer freeGuide;

}

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

    public FreeGuideSAXHandler(FreeGuideSAXInterface fgSAXInterface) {
		// Remember who called us
		this.fgSAXInterface=fgSAXInterface;
    }
    
	public void startDocument() {// throws SAXException
		fgSAXInterface.startDocument();
    }

	public void endDocument() { //throws SAXException
		fgSAXInterface.endDocument();
    }

    public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) {//throws SAXException {
		//FreeGuide.log.writeLine(qName);
        fgSAXInterface.startElement(qName, attrs);
    }

    public void endElement(String namespaceURI, String sName, String qName) {// throws SAXException
		//FreeGuide.log.writeLine("/"+qName);
		fgSAXInterface.endElement(qName);
    }

    public void characters(char[] ch, int start, int length) {//throws SAXException
		//FreeGuide.log.writeLine(" "+new String(ch, start, length));
		fgSAXInterface.characters(new String(ch, start, length));
    }

    private FreeGuideSAXInterface fgSAXInterface;

}

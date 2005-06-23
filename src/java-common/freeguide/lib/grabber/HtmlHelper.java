package freeguide.lib.grabber;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Helper for parse html.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class HtmlHelper
{

    /*    public static Element getFirstElement(final Segment src, final String elementTag, String attributeName, String attributeValue) {


    List elems = src.findAllElements(elementTag);


    for (int i = 0; i < elems.size(); i++) {


    Element elem = (Element) elems.get(i);


    if (attributeValue.equalsIgnoreCase(elem.getAttributes().getValue(attributeName))) {


    return elem;


    }


    }


    return null;


    }


    public static List<Element> getAllElements(final Segment src, final String elementTag, String attributeName, String attributeValue) {


    List<Element> result = new ArrayList<Element>();


    List elems = src.findAllElements(elementTag);


    for (int i = 0; i < elems.size(); i++) {


    Element elem = (Element) elems.get(i);


    if (attributeValue.equalsIgnoreCase(elem.getAttributes().getValue(attributeName))) {


    result.add(elem);


    }


    }


    return result;


    }


    public static List<Element> getAllElements(final Segment src, final String elementTag) {


    List<Element> result = new ArrayList<Element>();


    List elems = src.findAllElements(elementTag);


    for (int i = 0; i < elems.size(); i++) {


    Element elem = (Element) elems.get(i);


    result.add(elem);


    }


    return result;


    }*/
    public static String strongTrim( final String in )
    {

        int len = in.length(  );

        int st = 0;

        while( 
            ( st < len )
                && ( Character.isWhitespace( in.charAt( st ) )
                || Character.isSpaceChar( in.charAt( st ) ) ) )
        {
            st++;

        }

        while( 
            ( st < len )
                && ( Character.isWhitespace( in.charAt( len - 1 ) )
                || Character.isSpaceChar( in.charAt( len - 1 ) ) ) )
        {
            len--;

        }

        return ( ( st > 0 ) || ( len < in.length(  ) ) )
        ? in.substring( st, len ) : in;

    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class DefaultContentHandler implements ContentHandler
    {

        /**
         * DOCUMENT_ME!
         *
         * @param locator DOCUMENT_ME!
         */
        public void setDocumentLocator( Locator locator )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void startDocument(  ) throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void endDocument(  ) throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param prefix DOCUMENT_ME!
         * @param uri DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void startPrefixMapping( String prefix, String uri )
            throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param prefix DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void endPrefixMapping( String prefix ) throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param ch DOCUMENT_ME!
         * @param start DOCUMENT_ME!
         * @param length DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void ignorableWhitespace( char[] ch, int start, int length )
            throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param target DOCUMENT_ME!
         * @param data DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void processingInstruction( String target, String data )
            throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param name DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void skippedEntity( String name ) throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param uri DOCUMENT_ME!
         * @param localName DOCUMENT_ME!
         * @param qName DOCUMENT_ME!
         * @param atts DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void startElement( 
            String uri, String localName, String qName, Attributes atts )
            throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param uri DOCUMENT_ME!
         * @param localName DOCUMENT_ME!
         * @param qName DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void endElement( String uri, String localName, String qName )
            throws SAXException
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param ch DOCUMENT_ME!
         * @param start DOCUMENT_ME!
         * @param length DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void characters( char[] ch, int start, int length )
            throws SAXException
        {
        }
    }
}

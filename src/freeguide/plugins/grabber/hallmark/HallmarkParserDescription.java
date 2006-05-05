package freeguide.plugins.grabber.hallmark;

import freeguide.common.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HallmarkParserDescription extends HtmlHelper.DefaultContentHandler
{
    protected boolean parse = false;
    protected StringBuffer text = new StringBuffer(  );

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
        if( 
            "span".equals( qName )
                && "generalText".equals( atts.getValue( "class" ) ) )
        {
            parse = true;
            text.setLength( 0 );
        }
        else if( parse && "br".equals( qName ) )
        {
            text.append( '\n' );
        }
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
        if( "span".equals( qName ) )
        {
            parse = false;
        }
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
        if( parse )
        {
            text.append( ch, start, length );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getResult(  )
    {
        return text.toString(  );
    }
}

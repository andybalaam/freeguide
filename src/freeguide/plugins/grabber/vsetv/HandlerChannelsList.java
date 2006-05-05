package freeguide.plugins.grabber.vsetv;

import freeguide.common.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerChannelsList extends HtmlHelper.DefaultContentHandler
{
    protected boolean grab;
    protected boolean grabChannelName;
    protected List result = new ArrayList(  );

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
        if( "select".equals( qName ) )
        {
            if( "selectchannels".equalsIgnoreCase( atts.getValue( "name" ) ) )
            {
                grab = true;

            }
        }

        else if( grab && "option".equals( qName ) )
        {
            String value = atts.getValue( "value" );

            if( ( value != null ) && value.toLowerCase(  ).startsWith( "ch" ) )
            {
                grabChannelName = true;

            }
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
        if( grabChannelName )
        {
            result.add( new String( ch, start, length ) );

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
        if( "select".equals( qName ) )
        {
            grab = false;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getResult(  )
    {
        return (String[])result.toArray( new String[result.size(  )] );

    }
}

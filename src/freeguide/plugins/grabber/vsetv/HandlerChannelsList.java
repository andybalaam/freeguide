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
    protected static final String TAG_SELECT = "select";
    protected static final String TAG_OPTION = "option";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_VALUE = "value";
    protected static final String PREFIX_CH = "ch";
    protected static final String VALUE_SELECT_CHANNELS = "selectchannels";
    protected boolean grab;
    protected boolean grabChannelName;
    protected List<String> result = new ArrayList<String>(  );

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
        if( TAG_SELECT.equals( qName ) )
        {
            if( 
                VALUE_SELECT_CHANNELS.equalsIgnoreCase( 
                        atts.getValue( ATTR_NAME ) ) )
            {
                grab = true;

            }
        }

        else if( grab && TAG_OPTION.equals( qName ) )
        {
            String value = atts.getValue( ATTR_VALUE );

            if( 
                ( value != null )
                    && value.toLowerCase(  ).startsWith( PREFIX_CH ) )
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
        if( TAG_SELECT.equals( qName ) )
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
        return result.toArray( new String[result.size(  )] );

    }
}

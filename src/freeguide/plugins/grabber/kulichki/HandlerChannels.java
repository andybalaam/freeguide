package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Map;
import java.util.TreeMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerChannels extends HtmlHelper.DefaultContentHandler
{
    protected static final String TAG_INPUT = "input";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_VALUE = "value";
    protected static final String CHANNEL_ATTR_VALUE = "chanel";
    protected Map<String, String> channelList =
        new TreeMap<String, String>(  );
    protected String lastChannelID;

    /**
     * DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void startDocument(  ) throws SAXException
    {
        channelList.clear(  );

        lastChannelID = null;

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
        if( 
            TAG_INPUT.equals( qName )
                && CHANNEL_ATTR_VALUE.equals( atts.getValue( ATTR_NAME ) ) )
        {
            String value = atts.getValue( ATTR_VALUE );

            if( value != null )
            {
                lastChannelID = value.trim(  );

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
        if( lastChannelID != null )
        {
            channelList.put( lastChannelID, new String( ch, start, length ) );

            lastChannelID = null;

        }
    }
}

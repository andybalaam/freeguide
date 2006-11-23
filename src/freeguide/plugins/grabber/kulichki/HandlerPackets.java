package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Handler for packets list parsing.
 *
 * @author Alex Buloichik
 */
public class HandlerPackets extends HtmlHelper.DefaultContentHandler
{
    protected static final String TAG_FORM = "form";
    protected static final String TAG_TABLE = "table";
    protected static final String TAG_INPUT = "input";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_VALUE = "value";
    protected static final String ATTR_ACTION = "action";
    protected static final String PARAM_WEEK = "week";
    protected static final String PARAM_PACKET = "pakets";
    protected static final String ANONS_ATTR_VALUE = "anons";
    protected static final String ACTION_NAME = "cgi-bin/gpack.cgi";
    protected boolean grab;
    protected String lastPacketID;
    protected Set<String> weekList = new TreeSet<String>(  );
    protected Map<String, String> packetList = new TreeMap<String, String>(  );

    /**
     * DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void startDocument(  ) throws SAXException
    {
        grab = false;

        lastPacketID = null;

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
        if( TAG_FORM.equals( qName ) )
        {
            if( ACTION_NAME.equals( atts.getValue( ATTR_ACTION ) ) )
            {
                grab = true;

            }
        }

        else if( grab && TAG_INPUT.equals( qName ) )
        {
            String name = atts.getValue( ATTR_NAME );

            String value = atts.getValue( ATTR_VALUE );

            if( PARAM_WEEK.equals( name ) )
            {
                weekList.add( value );

            }

            else if( PARAM_PACKET.equals( name ) )
            {
                if( !ANONS_ATTR_VALUE.equals( value ) )
                {
                    lastPacketID = value;

                }
            }
        }

        else if( TAG_TABLE.equals( qName ) )
        {
            grab = false;

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
        if( TAG_TABLE.equals( qName ) )
        {
            grab = false;

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
        if( lastPacketID != null )
        {
            packetList.put( lastPacketID, new String( ch, start, length ) );

            lastPacketID = null;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getWeeks(  )
    {
        return weekList.toArray( new String[weekList.size(  )] );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Collection<String> getPacketIDs(  )
    {
        return packetList.keySet(  );
    }
}

package freeguide.plugins.grabber.kulichki;

import freeguide.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerPackets extends HtmlHelper.DefaultContentHandler
{

    protected boolean grab;
    protected String lastPacketID;
    protected Set weekList = new TreeSet(  );
    protected Map packetList = new TreeMap(  );

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

        if( "form".equals( qName ) )
        {

            if( "cgi-bin/gpack.cgi".equals( atts.getValue( "action" ) ) )
            {
                grab = true;

            }
        }

        else if( grab && "input".equals( qName ) )
        {

            String name = atts.getValue( "name" );

            String value = atts.getValue( "value" );

            if( "week".equals( name ) )
            {
                weekList.add( value );

            }

            else if( "pakets".equals( name ) )
            {

                if( !"anons".equals( value ) )
                {
                    lastPacketID = value;

                }
            }
        }

        else if( "table".equals( qName ) )
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

        if( "table".equals( qName ) )
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

        return (String[])weekList.toArray( new String[weekList.size(  )] );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getPacketIDs(  )
    {

        return (String[])packetList.keySet(  ).toArray( 
            new String[packetList.size(  )] );

    }
}

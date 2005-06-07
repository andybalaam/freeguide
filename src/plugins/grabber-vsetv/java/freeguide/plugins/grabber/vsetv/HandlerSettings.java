package freeguide.plugins.grabber.vsetv;

import freeguide.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerSettings extends HtmlHelper.DefaultContentHandler
{

    protected static final int MODES_NONE = 0;
    protected static final int MODES_MERIDIAN = 1;
    int meridianValue;
    boolean needUpdateCheckboxes;
    List channelIDs;
    protected int mode;

    /**
     * Creates a new HandlerSettings object.
     */
    public HandlerSettings(  )
    {
        meridianValue = -100;

        needUpdateCheckboxes = false;

        channelIDs = new ArrayList(  );

        mode = MODES_NONE;

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

        if( "select".equals( qName ) )
        {

            if( "MERIDIAN".equalsIgnoreCase( atts.getValue( "name" ) ) )
            {
                mode = MODES_MERIDIAN;

                meridianValue = -100;

            }
        }

        else if( ( mode == MODES_MERIDIAN ) && "option".equals( qName ) )
        {

            if( atts.getValue( "selected" ) != null )
            {
                meridianValue = Integer.parseInt( atts.getValue( "value" ) );

            }
        }

        else if( 
            "input".equals( qName )
                && "checkbox".equalsIgnoreCase( atts.getValue( "type" ) ) )
        {

            String name = atts.getValue( "name" );

            boolean checked = atts.getValue( "checked" ) != null;

            if( name != null )
            {

                if( name.startsWith( "pgch" ) && checked )
                {
                    needUpdateCheckboxes = true;

                }

                else if( name.startsWith( "gch" ) && checked )
                {
                    needUpdateCheckboxes = true;

                }

                else
                {

                    if( name.startsWith( "cch" ) && !checked )
                    {
                        needUpdateCheckboxes = true;

                    }
                }

                if( name.startsWith( "cch" ) )
                {
                    channelIDs.add( name );

                }
            }
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
            mode = MODES_NONE;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isNeedUpdate(  )
    {

        return needUpdateCheckboxes;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getChannelIDs(  )
    {

        return (String[])channelIDs.toArray( new String[channelIDs.size(  )] );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param TIMEZONES DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public TimeZone getTimeZone( final Properties TIMEZONES )
    {

        String tz =
            TIMEZONES.getProperty( Integer.toString( meridianValue ), null );

        if( tz == null )
        {

            StringBuffer tzName = new StringBuffer( 10 );

            tzName.append( "GMT" );

            if( meridianValue >= 0 )
            {
                tzName.append( '+' );

            }

            tzName.append( Integer.toString( meridianValue ) );
            tz = tzName.toString(  );
        }

        return TimeZone.getTimeZone( tz );

    }
}

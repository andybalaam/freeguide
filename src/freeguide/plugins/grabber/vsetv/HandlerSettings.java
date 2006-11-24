package freeguide.plugins.grabber.vsetv;

import freeguide.common.lib.grabber.HtmlHelper;

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
    protected static final String TIMEZONE_PREFIX = "GMT";
    protected static final String TAG_SELECT = "select";
    protected static final String TAG_INPUT = "input";
    protected static final String TAG_OPTION = "option";
    protected static final String ATTR_NAME = "name";
    protected static final String ATTR_VALUE = "value";
    protected static final String ATTR_TYPE = "type";
    protected static final String ATTR_SELECTED = "selected";
    protected static final String ATTR_CHECKED = "checked";
    protected static final String TYPE_CHECKBOX = "checkbox";
    protected static final String MERIDIAN = "MERIDIAN";
    protected static final String NAME_PREFIX_PGCH = "pgch";
    protected static final String NAME_PREFIX_GCH = "gch";
    protected static final String NAME_PREFIX_CCH = "cch";
    protected static final int MODES_NONE = 0;
    protected static final int MODES_MERIDIAN = 1;
    int meridianValue;
    boolean needUpdateCheckboxes;
    protected final List<String> channelIDs = new ArrayList<String>(  );
    protected int mode;

/**
     * Creates a new HandlerSettings object.
     */
    public HandlerSettings(  )
    {
        meridianValue = -100;

        needUpdateCheckboxes = false;

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
        if( TAG_SELECT.equals( qName ) )
        {
            if( MERIDIAN.equalsIgnoreCase( atts.getValue( ATTR_NAME ) ) )
            {
                mode = MODES_MERIDIAN;

                meridianValue = -100;

            }
        }

        else if( ( mode == MODES_MERIDIAN ) && TAG_OPTION.equals( qName ) )
        {
            if( atts.getValue( ATTR_SELECTED ) != null )
            {
                meridianValue = Integer.parseInt( atts.getValue( ATTR_VALUE ) );

            }
        }

        else if( 
            TAG_INPUT.equals( qName )
                && TYPE_CHECKBOX.equalsIgnoreCase( atts.getValue( ATTR_TYPE ) ) )
        {
            String name = atts.getValue( ATTR_NAME );

            boolean checked = atts.getValue( ATTR_CHECKED ) != null;

            if( name != null )
            {
                if( name.startsWith( NAME_PREFIX_PGCH ) && checked )
                {
                    needUpdateCheckboxes = true;

                }

                else if( name.startsWith( NAME_PREFIX_GCH ) && checked )
                {
                    needUpdateCheckboxes = true;

                }

                else
                {
                    if( name.startsWith( NAME_PREFIX_CCH ) && !checked )
                    {
                        needUpdateCheckboxes = true;

                    }
                }

                if( name.startsWith( NAME_PREFIX_CCH ) )
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
        if( TAG_SELECT.equals( qName ) )
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
        return channelIDs.toArray( new String[channelIDs.size(  )] );

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

            tzName.append( TIMEZONE_PREFIX );

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

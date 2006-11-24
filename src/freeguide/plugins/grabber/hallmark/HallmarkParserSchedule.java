package freeguide.plugins.grabber.hallmark;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.StringHelper;
import freeguide.common.lib.general.Time;
import freeguide.common.lib.grabber.HtmlHelper;
import freeguide.common.lib.grabber.LineProgrammeHelper;
import freeguide.common.lib.grabber.TimeHelper;

import freeguide.common.plugininterfaces.ILogger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;

import java.text.MessageFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for Hallmark channel week schedule
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class HallmarkParserSchedule extends HtmlHelper.DefaultContentHandler
{
    protected static final String TAG_A = "a";
    protected static final String TAG_TABLE = "table";
    protected static final String TAG_TR = "tr";
    protected static final String TAG_TD = "td";
    protected static final String ATTR_HREF = "href";
    protected static final String ATTR_ONMOUSEOVER = "onmouseover";
    protected static final String LINK_TO_ADOBE_COM = "http://www.adobe.com/";
    protected static final String TIMEZONES_FILE =
        "resources/plugins/grabber/hallmark/timezones.properties";
    protected static final Pattern RE_DATE =
        Pattern.compile( "\\s+(\\d{2})/(\\d{2})" );
    protected static final Pattern RE_DESCKEY =
        Pattern.compile( "CONTENT=([A-Z0-9_]+)" );
    protected static final Pattern RE_MOUSE =
        Pattern.compile( "writetxt\\('(.+)'\\)" );
    protected static final Properties TIMEZONES = new Properties(  );
    protected boolean foundAcrobat = false;
    protected boolean parse = false;
    protected int row;
    protected int col;
    protected StringBuffer text = new StringBuffer(  );
    protected String descriptionKey;
    protected String descriptionText;
    protected TimeZone timeZone;
    protected String timeZoneName;
    protected long[] baseDates = new long[7];
    protected long[] prevTimes = new long[7];
    protected Time currentTime;
    protected final TVChannel channel;
    protected final Map<String, List<TVProgramme>> descriptionsMap;
    protected final boolean isUS;
    protected final ResourceBundle i18n;
    protected final ILogger logger;

/**
     * Creates a new HallmarkScheduleParser object.
     *
     * @param channel DOCUMENT ME!
     * @param descriptionsMap map of programmes lists by description keys
     * @param isUS DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public HallmarkParserSchedule( 
        final TVChannel channel,
        final Map<String, List<TVProgramme>> descriptionsMap,
        final boolean isUS, final ResourceBundle i18n, final ILogger logger )
        throws SAXException
    {
        this.channel = channel;
        this.logger = logger;
        this.i18n = i18n;

        this.descriptionsMap = descriptionsMap;
        this.isUS = isUS;
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
        text.append( ch, start, length );
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
        text.setLength( 0 );

        if( parse )
        {
            parseStart( qName, atts );
        }
        else if( 
            TAG_A.equals( qName ) && ( atts.getValue( ATTR_HREF ) != null )
                && atts.getValue( ATTR_HREF ).startsWith( LINK_TO_ADOBE_COM ) )
        {
            foundAcrobat = true;
        }
        else if( TAG_TABLE.equals( qName ) && foundAcrobat )
        {
            parse = true;
            foundAcrobat = false;
            row = -1;
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
        if( parse )
        {
            parseEnd( qName );
            text.setLength( 0 );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param qName DOCUMENT_ME!
     * @param atts DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void parseStart( String qName, Attributes atts )
        throws SAXException
    {
        if( parse && TAG_TR.equals( qName ) )
        {
            row++;
            col = -1;
        }
        else if( parse && TAG_TD.equals( qName ) )
        {
            col++;
            descriptionKey = null;
            descriptionText = null;

            String onMouseOver = atts.getValue( ATTR_ONMOUSEOVER );

            if( onMouseOver != null )
            {
                Matcher m = RE_MOUSE.matcher( onMouseOver );

                if( m.matches(  ) )
                {
                    descriptionText = m.group( 1 );
                }
            }
        }
        else if( TAG_A.equals( qName ) && ( row > 0 ) && ( col > 0 ) )
        {
            String ref = atts.getValue( ATTR_HREF );

            if( ref != null )
            {
                Matcher m = RE_DESCKEY.matcher( ref );

                if( m.find(  ) )
                {
                    descriptionKey = m.group( 1 );
                }
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param qName DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void parseEnd( String qName ) throws SAXException
    {
        if( TAG_TR.equals( qName ) )
        {
            return;
        }

        if( TAG_TABLE.equals( qName ) )
        {
            parse = false;
        }
        else
        {
            if( col > 7 )
            {
                throw new SAXException( "Invalid column: " + col );
            }

            if( row == 0 )
            { // header

                if( col == 0 )
                { // timezone
                    timeZoneName = text.toString(  ).trim(  );
                    timeZone = getTimeZone( timeZoneName, i18n, logger );

                    if( timeZone == null )
                    {
                        Application.getInstance(  ).getLogger(  )
                                   .warning( 
                            "Invalid timezone: " + text.toString(  ).trim(  ) );
                        timeZone = TimeZone.getDefault(  );
                    }
                }
                else
                { // dates

                    Matcher m = RE_DATE.matcher( text.toString(  ) );

                    if( !m.find(  ) )
                    {
                        throw new SAXException( 
                            "Invalid date: " + text.toString(  ) );
                    }

                    try
                    {
                        String day;
                        String month;

                        if( isUS )
                        {
                            day = m.group( 2 );
                            month = m.group( 1 );
                        }
                        else
                        {
                            day = m.group( 1 );
                            month = m.group( 2 );
                        }

                        baseDates[col - 1] = TimeHelper.getBaseDate( 
                                timeZone, day, month, null, null );
                    }
                    catch( ParseException ex )
                    {
                        throw new SAXException( 
                            "Invalid date: " + text.toString(  ), ex );
                    }
                }
            }
            else
            {
                if( col == 0 )
                { // time

                    try
                    {
                        currentTime = LineProgrammeHelper.parseTime( 
                                text.toString(  ).trim(  ) );
                    }
                    catch( ParseException ex )
                    {
                        throw new SAXException( 
                            "Invalid date: " + text.toString(  ), ex );
                    }
                }
                else
                {
                    String title = HtmlHelper.strongTrim( text.toString(  ) );

                    if( 
                        !StringHelper.EMPTY_STRING.equals( title )
                            && ( channel != null ) )
                    {
                        TVProgramme prog = new TVProgramme(  );
                        prog.setStart( 
                            TimeHelper.correctTime( 
                                currentTime, baseDates[col - 1],
                                prevTimes[col - 1] ) );
                        prog.setTitle( title );
                        prog.setDescription( descriptionText );

                        try
                        {
                            channel.put( prog );
                        }
                        catch( Exception ex )
                        {
                            throw new SAXException( ex.getMessage(  ), ex );
                        }

                        addToDescriptionsMap( descriptionKey, prog );
                    }
                }
            }
        }
    }

    protected void addToDescriptionsMap( 
        final String key, final TVProgramme prog )
    {
        if( descriptionsMap == null )
        {
            return;
        }

        if( descriptionKey != null )
        {
            List<TVProgramme> list = descriptionsMap.get( key );

            if( list == null )
            {
                list = new ArrayList<TVProgramme>(  );
                descriptionsMap.put( key, list );
            }

            list.add( prog );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param hallmarkTimezone DOCUMENT_ME!
     * @param i18n DOCUMENT ME!
     * @param logger DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public synchronized static TimeZone getTimeZone( 
        final String hallmarkTimezone, final ResourceBundle i18n,
        final ILogger logger )
    {
        if( TIMEZONES.size(  ) == 0 )
        {
            try
            {
                TIMEZONES.load( 
                    HallmarkParserSchedule.class.getClassLoader(  )
                                                .getResourceAsStream( 
                        TIMEZONES_FILE ) );
            }
            catch( IOException ex )
            {
                Application.getInstance(  ).getLogger(  )
                           .severe( 
                    "Error loading timezones info: " + ex.getMessage(  ) );
            }
        }

        final String tzName = TIMEZONES.getProperty( hallmarkTimezone );

        if( tzName == null )
        {
            if( logger != null )
            {
                logger.warning( 
                    MessageFormat.format( 
                        i18n.getString( "Logging.UnknownTimeZone" ), tzName ) );
            }

            return TimeZone.getDefault(  );
        }
        else
        {
            return TimeZone.getTimeZone( tzName );
        }
    }
}

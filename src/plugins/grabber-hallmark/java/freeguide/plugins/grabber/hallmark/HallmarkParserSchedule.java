package freeguide.plugins.grabber.hallmark;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.Time;

import freeguide.lib.grabber.HtmlHelper;
import freeguide.lib.grabber.LineProgrammeHelper;
import freeguide.lib.grabber.TimeHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
    protected final Map descriptionsMap;
    protected final boolean isUS;

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
        final TVChannel channel, final Map descriptionsMap, final boolean isUS )
        throws SAXException
    {
        this.channel = channel;

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
            "a".equals( qName ) && ( atts.getValue( "href" ) != null )
                && atts.getValue( "href" ).startsWith( 
                    "http://www.adobe.com/" ) )
        {
            foundAcrobat = true;
        }
        else if( "table".equals( qName ) && foundAcrobat )
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

        if( parse && "tr".equals( qName ) )
        {
            row++;
            col = -1;
        }
        else if( parse && "td".equals( qName ) )
        {
            col++;
            descriptionKey = null;
            descriptionText = null;

            String onMouseOver = atts.getValue( "onmouseover" );

            if( onMouseOver != null )
            {

                Matcher m = RE_MOUSE.matcher( onMouseOver );

                if( m.matches(  ) )
                {
                    descriptionText = m.group( 1 );
                }
            }
        }
        else if( "a".equals( qName ) && ( row > 0 ) && ( col > 0 ) )
        {

            String ref = atts.getValue( "href" );

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

        if( "tr".equals( qName ) )
        {

            return;
        }

        if( "table".equals( qName ) )
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
                    timeZone = getTimeZone( timeZoneName );

                    if( timeZone == null )
                    {
                        System.out.println( 
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

                        baseDates[col - 1] =
                            TimeHelper.getBaseDate( 
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
                        currentTime =
                            LineProgrammeHelper.parseTime( 
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

                    if( !"".equals( title ) && ( channel != null ) )
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
                            throw new SAXException( ex );
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

            List list = (List)descriptionsMap.get( key );

            if( list == null )
            {
                list = new ArrayList(  );
                descriptionsMap.put( key, list );
            }

            list.add( prog );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param hallmarkTimezone DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public synchronized static TimeZone getTimeZone( 
        final String hallmarkTimezone )
    {

        if( TIMEZONES.size(  ) == 0 )
        {

            try
            {
                TIMEZONES.load( 
                    HallmarkParserSchedule.class.getClassLoader(  )
                                                .getResourceAsStream( 
                        HallmarkParserSchedule.class.getPackage(  ).getName(  )
                                                    .replace( '.', '/' )
                        + "/timezones.properties" ) );
            }
            catch( IOException ex )
            {
                Application.getInstance(  ).getLogger(  ).severe( 
                    "Error loading timezones info: " + ex.getMessage(  ) );
            }
        }

        final String tzName = TIMEZONES.getProperty( hallmarkTimezone );

        if( tzName == null )
        {
            Application.getInstance(  ).getLogger(  ).warning( 
                "Unknown timezone: " + tzName );

            return TimeZone.getDefault(  );
        }
        else
        {

            return TimeZone.getTimeZone( tzName );
        }
    }
}

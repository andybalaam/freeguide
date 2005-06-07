package freeguide.plugins.grabber.vsetv;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.Time;

import freeguide.lib.grabber.HtmlHelper;
import freeguide.lib.grabber.LineProgrammeHelper;
import freeguide.lib.grabber.TimeHelper;

import freeguide.plugins.ILogger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.text.ParseException;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerParseProg extends HtmlHelper.DefaultContentHandler
{

    protected static final int MODES_NONE = 0;
    protected static final int MODES_TITLE = 1;
    protected static final int MODES_CHANNEL_NAME = 2;
    protected static final int MODES_END = 3;
    protected static final Pattern DATE_PATTERN =
        Pattern.compile( "(\\S+)\\s*,\\s*(\\d{1,2})\\s+(\\S+)\\s+(\\d{4})" );
    protected static final Pattern FILM_ID_PATTERN =
        Pattern.compile( ".*fid=(\\d+).*" );
    protected int mode;
    protected long currentDate;
    protected TVChannel currentChannel;
    protected long prevTime;
    protected TVProgramme currentProg;
    protected final TVData siteData;
    protected final TimeZone tz;
    protected boolean isAnnounces;
    protected ILogger logger;

    /**
     * Creates a new HandlerParseProg object.
     *
     * @param logger DOCUMENT ME!
     * @param siteData DOCUMENT ME!
     * @param tz DOCUMENT ME!
     */
    public HandlerParseProg( ILogger logger, TVData siteData, TimeZone tz )
    {
        mode = MODES_NONE;
        isAnnounces = false;
        this.logger = logger;
        this.siteData = siteData;
        this.tz = tz;
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void startDocument(  ) throws SAXException
    {
        currentChannel = null;
        currentProg = null;
        currentDate = 0L;
        prevTime = 0;
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

        if( mode == MODES_NONE )
        {

            if( "font".equals( qName ) )
            {

                if( "WHITE".equalsIgnoreCase( atts.getValue( "class" ) ) )
                {
                    mode = MODES_CHANNEL_NAME;
                    prevTime = 0;
                }
            }
            else if( "b".equals( qName ) )
            {

                if( "TITLE".equalsIgnoreCase( atts.getValue( "class" ) ) )
                {
                    mode = MODES_TITLE;
                    prevTime = 0;
                }
            }
            else if( "a".equals( qName ) )
            {

                String href = atts.getValue( "href" );

                if( ( href != null ) && ( href.indexOf( "print.php" ) != -1 ) )
                {
                    currentChannel = null;
                    mode = MODES_END;
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

        if( isAnnounces && "td".equals( qName ) )
        {
            currentProg = null;
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

        switch( mode )
        {

        case MODES_TITLE:

            Matcher titleMatcher =
                DATE_PATTERN.matcher( new String( ch, start, length ) );

            if( !titleMatcher.matches(  ) )
            {
                throw new SAXException( 
                    "Error in page format: invalid title format" );
            }

            try
            {
                currentDate =
                    TimeHelper.getBaseDate( 
                        tz, titleMatcher.group( 2 ), titleMatcher.group( 3 ),
                        titleMatcher.group( 4 ), titleMatcher.group( 1 ) );
                prevTime = 0;
            }
            catch( ParseException ex )
            {
                logger.warning( 
                    "Error parse : " + new String( ch, start, length ) );
            }

            mode = MODES_NONE;

            break;

        case MODES_CHANNEL_NAME:

            if( currentDate == 0 )
            {
                throw new SAXException( 
                    "Error in page format: title not found" );
            }

            String channelName = new String( ch, start, length );
            currentChannel =
                siteData.get( 
                    GrabberVsetv.ID + "/" + channelName.replace( '/', '_' ) );
            currentChannel.setDisplayName( channelName );
            mode = MODES_NONE;
            prevTime = 0;

            break;

        case MODES_NONE:

            if( currentChannel != null )
            {

                String text =
                    HtmlHelper.strongTrim( new String( ch, start, length ) );

                if( !"".equals( text ) )
                {

                    if( !isAnnounces )
                    {
                        parseTextForProg( text );
                    }
                    else
                    {
                        parseTextForAnnonce( text );
                    }
                }
            }

            break;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param announces DOCUMENT_ME!
     */
    public void setAnnounces( boolean announces )
    {
        isAnnounces = announces;
    }

    protected void parseTextForProg( String text )
    {

        Matcher timeMatcher = TimeHelper.getTimePattern(  ).matcher( text );

        if( timeMatcher.matches(  ) )
        {

            try
            {

                Time tm = LineProgrammeHelper.parseTime( text );
                long time =
                    TimeHelper.correctTime( tm, currentDate, prevTime );

                currentProg = new TVProgramme(  );
                currentProg.setStart( time );
                prevTime = time;
            }
            catch( ParseException ex )
            {
                currentProg = null;
                logger.warning( "Invalid time format: " + text );
            }
        }
        else if( currentProg != null )
        {
            currentProg.setTitle( text );
            currentChannel.put( currentProg );
            currentProg = null;
        }
    }

    protected void parseTextForAnnonce( String text )
    {

        if( currentProg == null )
        {

            Matcher timeMatcher =
                TimeHelper.getTimePattern(  ).matcher( text );

            if( timeMatcher.matches(  ) )
            {

                try
                {

                    Time tm = LineProgrammeHelper.parseTime( text );
                    long time =
                        TimeHelper.correctTime( tm, currentDate, prevTime );

                    currentProg = currentChannel.getProgrammeByTime( time );

                    if( currentProg == null )
                    {
                        currentProg =
                            currentChannel.getProgrammeByTime( 
                                time + TimeHelper.MILLISECONDS_IN_DAY );
                    }

                    if( currentProg != null )
                    {
                        prevTime = currentProg.getStart(  );
                    }
                }
                catch( ParseException ex )
                {
                    currentProg = null;
                    logger.warning( "Invalid time format: " + text );
                }
            }
        }
        else
        {
            currentProg.addDesc( text );
        }
    }
}

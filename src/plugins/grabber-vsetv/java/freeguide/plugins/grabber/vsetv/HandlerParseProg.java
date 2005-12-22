package freeguide.plugins.grabber.vsetv;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.Time;

import freeguide.lib.grabber.HtmlHelper;
import freeguide.lib.grabber.LineProgrammeHelper;
import freeguide.lib.grabber.TimeHelper;

import freeguide.plugins.ILogger;
import freeguide.plugins.IStoragePipe;

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
    protected static final int MODES_PROG_TIME = 3;
    protected static final int MODES_PROG_TITLE = 4;
    protected static final int MODES_ANON_TIME = 5;
    protected static final int MODES_ANON_TEXT = 6;
    protected static final Pattern DATE_PATTERN =
        Pattern.compile( "(\\S+)\\s*,\\s*(\\d{1,2})\\s+(\\S+)\\s+(\\d{4})" );
    protected static final Pattern FILM_ID_PATTERN =
        Pattern.compile( ".*fid=(\\d+).*" );
    protected int mode;
    protected long currentDate;
    protected TVChannel currentChannel;
    protected long prevTime;
    protected TVProgramme currentProg;
    protected final TVData data;
    protected final TimeZone tz;
    protected boolean isAnnounces;
    protected ILogger logger;
    protected final StringBuffer currentText = new StringBuffer(  );

    /**
     * Creates a new HandlerParseProg object.
     *
     * @param logger DOCUMENT ME!
     * @param tz DOCUMENT ME!
     */
    public HandlerParseProg( ILogger logger, TimeZone tz )
    {
        mode = MODES_NONE;
        isAnnounces = false;
        this.logger = logger;
        this.data = new TVData(  );
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
        currentText.setLength( 0 );

        if( mode == MODES_NONE )
        {

            if( 
                "td".equals( qName )
                    && "channeltitle".equals( atts.getValue( "class" ) ) )
            {
                mode = MODES_CHANNEL_NAME;
                prevTime = 0;
                currentProg = null;
            }
            else if( 
                "span".equals( qName )
                    && "pagedate".equals( atts.getValue( "class" ) ) )
            {
                mode = MODES_TITLE;
                prevTime = 0;
            }
            else if( 
                "td".equals( qName )
                    && "progtime".equals( atts.getValue( "class" ) ) )
            {
                mode = MODES_PROG_TIME;
            }
            else if( 
                "td".equals( qName )
                    && "anonstime".equals( atts.getValue( "class" ) ) )
            {
                mode = MODES_ANON_TIME;
            }
            else if( 
                "span".equals( qName )
                    && "descr1".equals( atts.getValue( "class" ) ) )
            {
                mode = MODES_ANON_TEXT;
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

        String text = HtmlHelper.strongTrim( currentText.toString(  ) );

        switch( mode )
        {

        case MODES_TITLE:

            Matcher titleMatcher = DATE_PATTERN.matcher( text );

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
                logger.warning( "Error parse : " + text );
            }

            mode = MODES_NONE;

            break;

        case MODES_CHANNEL_NAME:

            if( currentDate == 0 )
            {
                throw new SAXException( 
                    "Error in page format: title not found" );
            }

            String channelName = text;
            currentChannel =
                data.get( "vsetv/" + channelName.replace( '/', '_' ) );
            currentChannel.setDisplayName( channelName );

            mode = MODES_NONE;
            prevTime = 0;

            break;

        case MODES_PROG_TIME:

            if( currentChannel == null )
            {
                throw new SAXException( 
                    "Error in page format: channel not found" );
            }

            currentProg = null;

            try
            {

                Time tm = LineProgrammeHelper.parseTime( text );
                long time =
                    TimeHelper.correctTime( tm, currentDate, prevTime );
                currentProg = new TVProgramme(  );
                currentProg.setStart( time );
                prevTime = time;
                mode = MODES_PROG_TITLE;
            }
            catch( ParseException ex )
            {
            }

            if( currentProg != null )
            {
                currentChannel.put( currentProg );
            }

            break;

        case MODES_PROG_TITLE:

            if( currentProg != null )
            {

                if( "td".equals( qName ) )
                {
                    currentProg.setTitle( text );
                    mode = MODES_NONE;
                }
            }
            else
            {
                mode = MODES_NONE;
            }

            break;

        case MODES_ANON_TIME:

            if( currentDate == 0 )
            {
                throw new SAXException( 
                    "Error in page format: title not found" );
            }

            try
            {

                Time tm = LineProgrammeHelper.parseTime( text );
                long time = TimeHelper.correctTime( tm, currentDate, 0 );
                long timeCor =
                    TimeHelper.correctTime( tm, currentDate, prevTime );
                currentProg = currentChannel.getProgrammeByTime( timeCor );

                if( currentProg == null )
                {
                    currentProg =
                        currentChannel.getProgrammeByTime( 
                            time + TimeHelper.MILLISECONDS_IN_DAY );
                }

                if( currentProg == null )
                {
                    currentProg = currentChannel.getProgrammeByTime( time );
                }

                if( currentProg != null )
                {
                    prevTime = currentProg.getStart(  );
                }

                mode = MODES_ANON_TEXT;
            }
            catch( ParseException ex )
            {
            }

            break;

        case MODES_ANON_TEXT:

            if( currentDate == 0 )
            {
                throw new SAXException( 
                    "Error in page format: title not found" );
            }

            if( currentProg != null )
            {
                currentProg.addDesc( text );
            }

            break;
        }

        if( "table".equals( qName ) )
        {
            mode = MODES_NONE;
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
        currentText.append( ch, start, length );
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

    /**
     * DOCUMENT_ME!
     *
     * @param storage DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void store( final IStoragePipe storage ) throws Exception
    {
        GrabberVsetv.patch( data );
        storage.addData( data );
        storage.finishBlock(  );
    }
}

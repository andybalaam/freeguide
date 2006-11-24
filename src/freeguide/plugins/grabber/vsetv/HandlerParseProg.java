package freeguide.plugins.grabber.vsetv;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.Time;
import freeguide.common.lib.grabber.HtmlHelper;
import freeguide.common.lib.grabber.LineProgrammeHelper;
import freeguide.common.lib.grabber.TimeHelper;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IStoragePipe;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;

import java.text.MessageFormat;
import java.text.ParseException;

import java.util.ResourceBundle;
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
    protected static final String TAG_A = "a";
    protected static final String TAG_BR = "br";
    protected static final String TAG_TD = "td";
    protected static final String TAG_SPAN = "span";
    protected static final String TAG_TABLE = "table";
    protected static final String ATTR_CLASS = "class";
    protected static final String LINE_BREAK_IN_STORAGE = "<br>";
    protected static final int MODES_NONE = 0;
    protected static final int MODES_TITLE = 1;
    protected static final int MODES_CHANNEL_NAME = 2;
    protected static final int MODES_PROG_TIME = 3;
    protected static final int MODES_PROG_TITLE = 4;
    protected static final int MODES_ANON_TIME = 5;
    protected static final int MODES_ANON_TEXT = 6;
    protected static final String CLASS_CHANNEL = "channeltitle";
    protected static final String CLASS_DATE = "pagedate";
    protected static final String CLASS_TIME = "progtime";
    protected static final String CLASS_ANTIME = "descr1";
    protected static final String CLASS_DESCR = "descr1";
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
    protected final ResourceBundle i18n;
    protected boolean isAnnounces;
    protected ILogger logger;
    protected final StringBuffer currentText = new StringBuffer(  );

/**
     * Creates a new HandlerParseProg object.
     *
     * @param logger DOCUMENT ME!
     * @param tz DOCUMENT ME!
     */
    public HandlerParseProg( 
        ILogger logger, TimeZone tz, final ResourceBundle i18n )
    {
        mode = MODES_NONE;
        isAnnounces = false;
        this.logger = logger;
        this.data = new TVData(  );
        this.tz = tz;
        this.i18n = i18n;
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
        String text = HtmlHelper.strongTrim( currentText.toString(  ) );
        currentText.setLength( 0 );

        switch( mode )
        {
        case MODES_NONE:

            if( 
                TAG_TD.equals( qName )
                    && CLASS_CHANNEL.equals( atts.getValue( ATTR_CLASS ) ) )
            {
                mode = MODES_CHANNEL_NAME;
                prevTime = 0;
                currentProg = null;
            }
            else if( 
                TAG_SPAN.equals( qName )
                    && CLASS_DATE.equals( atts.getValue( ATTR_CLASS ) ) )
            {
                mode = MODES_TITLE;
                prevTime = 0;
            }
            else if( 
                TAG_TD.equals( qName )
                    && CLASS_TIME.equals( atts.getValue( ATTR_CLASS ) ) )
            {
                mode = MODES_PROG_TIME;
            }
            else if( 
                TAG_TD.equals( qName )
                    && CLASS_ANTIME.equals( atts.getValue( ATTR_CLASS ) ) )
            {
                mode = MODES_ANON_TIME;
            }
            else if( 
                TAG_SPAN.equals( qName )
                    && CLASS_DESCR.equals( atts.getValue( ATTR_CLASS ) ) )
            {
                mode = MODES_ANON_TEXT;
            }

            break;

        case MODES_ANON_TEXT:

            if( currentProg != null )
            {
                currentProg.addDesc( text );

                if( TAG_BR.equals( qName ) )
                {
                    currentProg.addDesc( LINE_BREAK_IN_STORAGE );
                }
            }

            break;
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
        currentText.setLength( 0 );

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
                currentDate = TimeHelper.getBaseDate( 
                        tz, titleMatcher.group( 2 ), titleMatcher.group( 3 ),
                        titleMatcher.group( 4 ), titleMatcher.group( 1 ) );
                prevTime = 0;
            }
            catch( ParseException ex )
            {
                logger.warning( 
                    MessageFormat.format( 
                        i18n.getString( "Logging.ErrorParse" ), text ) );
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
            currentChannel = data.get( 
                    GrabberVsetv.CHANNEL_PREFIX
                    + channelName.replace( '/', '_' ) );
            currentChannel.setDisplayName( channelName );

            mode = MODES_NONE;
            prevTime = 0;

            break;

        case MODES_PROG_TIME:

            if( currentChannel == null )
            {
                currentProg = null;
                mode = MODES_NONE;

                break;
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
                if( TAG_TD.equals( qName ) || TAG_A.equals( qName ) )
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
                    currentProg = currentChannel.getProgrammeByTime( 
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

        if( TAG_TABLE.equals( qName ) )
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
     */
    public void store( final IStoragePipe storage )
    {
        try
        {
            GrabberVsetv.patch( data );
        }
        catch( IOException e )
        {
            e.printStackTrace(  );
        }

        storage.addData( data );
        storage.finishBlock(  );
    }
}

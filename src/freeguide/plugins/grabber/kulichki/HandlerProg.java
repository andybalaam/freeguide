package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.grabber.HtmlHelper;
import freeguide.common.lib.grabber.LineProgrammeHelper;
import freeguide.common.lib.grabber.TimeHelper;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IStoragePipe;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

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
public class HandlerProg extends HtmlHelper.DefaultContentHandler
{
    protected static final String TAG_FONT = "font";
    protected static final String TAG_PRE = "pre";
    protected static final int MODES_NONE = 0;
    protected static final int MODES_CHANNEL_NAME = 1;
    protected static final int MODES_DATA = 2;
    protected static final Pattern RE_CHANNEL =
        Pattern.compile( 
            "\\s*(\\S+)\\s*\\.\\s*(\\d{1,2})\\s+(\\S+)\\s*\\.\\s+(.+)" );
    protected final IStoragePipe storage;
    protected ILogger logger;
    protected TimeZone tz;
    protected long currentDate;
    protected String currentChannelID;
    protected TVProgramme[] currentProgs;
    protected int mode;
    protected String channelIDprefix;

/**
     * Creates a new HandlerProg object.
     *
     * @param storage DOCUMENT ME!
     * @param logger DOCUMENT ME!
     */
    public HandlerProg( final IStoragePipe storage, ILogger logger )
    {
        this.storage = storage;

        this.logger = logger;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param tz DOCUMENT_ME!
     */
    public void setTimeZone( TimeZone tz )
    {
        this.tz = tz;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefix DOCUMENT_ME!
     */
    public void setChannelIDprefix( final String prefix )
    {
        channelIDprefix = prefix;

    }

    /**
     * DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void startDocument(  ) throws SAXException
    {
        mode = MODES_NONE;

        currentChannelID = null;
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
        if( TAG_FONT.equals( qName ) )
        {
            mode = MODES_CHANNEL_NAME;

        }

        else if( TAG_PRE.equals( qName ) )
        {
            mode = MODES_DATA;

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
        if( TAG_FONT.equals( qName ) )
        {
            mode = MODES_NONE;

        }

        else if( TAG_PRE.equals( qName ) )
        {
            mode = MODES_NONE;

            currentChannelID = null;

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
        case MODES_CHANNEL_NAME:
            storage.finishBlock(  );

            String data =
                HtmlHelper.strongTrim( new String( ch, start, length ) );
            Matcher m = RE_CHANNEL.matcher( data );

            if( m.matches(  ) )
            {
                try
                {
                    currentDate = TimeHelper.getBaseDate( 
                            tz, m.group( 2 ), m.group( 3 ), null, m.group( 1 ) );

                    currentChannelID = channelIDprefix
                        + m.group( 4 ).replace( '/', '_' );

                    try
                    {
                        storage.addChannel( 
                            new TVChannel( currentChannelID, m.group( 4 ) ) );
                    }
                    catch( Exception ex )
                    {
                        throw new SAXException( ex.getMessage(  ), ex );
                    }
                }

                catch( ParseException ex )
                {
                    currentChannelID = null;

                    logger.warning( "Error on channel name: " + data );

                }
            }

            else
            {
                currentChannelID = null;

            }

            break;

        case MODES_DATA:

            if( currentChannelID != null )
            {
                BufferedReader rd =
                    new BufferedReader( 
                        new StringReader( new String( ch, start, length ) ) );

                try
                {
                    currentProgs = null;

                    String line;

                    while( ( line = rd.readLine(  ) ) != null )
                    {
                        line = line.trim(  );

                        if( "".equals( line ) )
                        {
                            continue;

                        }

                        if( LineProgrammeHelper.isProgram( line ) )
                        {
                            try
                            {
                                currentProgs = LineProgrammeHelper.parse( 
                                        logger, line, currentDate,
                                        ( currentProgs != null )
                                        ? currentProgs[0].getStart(  ) : 0 );

                                try
                                {
                                    storage.addProgrammes( 
                                        currentChannelID, currentProgs );
                                }
                                catch( Exception ex )
                                {
                                    throw new SAXException( 
                                        ex.getMessage(  ), ex );
                                }
                            }

                            catch( ParseException ex )
                            {
                                logger.warning( "Error parse: " + line );

                            }
                        }

                        else
                        {
                            if( currentProgs != null )
                            {
                                for( int i = 0; i < currentProgs.length;
                                        i++ )
                                {
                                    currentProgs[i].addDesc( line + '\n' );

                                }
                            }
                        }
                    }
                }

                catch( IOException ex )
                {
                    throw new SAXException( 
                        "Error parse text block: " + ex.getMessage(  ), ex );

                }
            }

            break;
        }
    }
}

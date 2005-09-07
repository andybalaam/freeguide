package freeguide.plugins.grabber.kulichki;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.grabber.HtmlHelper;
import freeguide.lib.grabber.LineProgrammeHelper;
import freeguide.lib.grabber.TimeHelper;

import freeguide.plugins.ILogger;

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

    protected static final int MODES_NONE = 0;
    protected static final int MODES_CHANNEL_NAME = 1;
    protected static final int MODES_DATA = 2;
    protected static Pattern RE_CHANNEL =
        Pattern.compile( 
            "\\s*(\\S+)\\s*\\.\\s*(\\d{1,2})\\s+(\\S+)\\s*\\.\\s+(.+)" );
    protected TVData result;
    protected ILogger logger;
    protected TimeZone tz;
    protected long currentDate;
    protected TVChannel currentChannel;
    protected TVProgramme[] currentProgs;
    protected int mode;
    protected String channelIDprefix;

    /**
     * Creates a new HandlerProg object.
     *
     * @param result DOCUMENT ME!
     * @param logger DOCUMENT ME!
     */
    public HandlerProg( TVData result, ILogger logger )
    {
        this.result = result;

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

        currentChannel = null;

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

        if( "font".equals( qName ) )
        {
            mode = MODES_CHANNEL_NAME;

        }

        else if( "pre".equals( qName ) )
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

        if( "font".equals( qName ) )
        {
            mode = MODES_NONE;

        }

        else if( "pre".equals( qName ) )
        {
            mode = MODES_NONE;

            currentChannel = null;

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

            String data =
                HtmlHelper.strongTrim( new String( ch, start, length ) );
            Matcher m = RE_CHANNEL.matcher( data );

            if( m.matches(  ) )
            {

                try
                {
                    currentDate =
                        TimeHelper.getBaseDate( 
                            tz, m.group( 2 ), m.group( 3 ), null, m.group( 1 ) );

                    currentChannel =
                        result.get( 
                            channelIDprefix + m.group( 4 ).replace( '/', '_' ) );
                    currentChannel.setDisplayName( m.group( 4 ) );

                }

                catch( ParseException ex )
                {
                    currentChannel = null;

                    logger.warning( "Error on channel name: " + data );

                }
            }

            else
            {
                currentChannel = null;

            }

            break;

        case MODES_DATA:

            if( currentChannel != null )
            {

                BufferedReader rd =
                    new BufferedReader( 
                        new StringReader( new String( ch, start, length ) ) );

                try
                {

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
                                currentProgs =
                                    LineProgrammeHelper.parse( 
                                        logger, line, currentDate,
                                        ( currentProgs != null )
                                        ? currentProgs[0].getStart(  ) : 0 );

                                currentChannel.put( currentProgs );

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
                        "Error parse text block: " + ex.getMessage(  ) );

                }
            }

            break;
        }
    }
}

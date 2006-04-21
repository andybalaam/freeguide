package freeguide.plugins.grabber.newsvm;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.lib.general.LanguageHelper;

import freeguide.common.lib.grabber.HtmlHelper;
import freeguide.common.lib.grabber.HttpBrowser;
import freeguide.common.lib.grabber.LineProgrammeHelper;
import freeguide.common.lib.grabber.TimeHelper;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
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
public class GrabberNewsvm extends BaseModule implements IModuleGrabber
{

    protected static Pattern reDate =
        Pattern.compile( 
            "(\\p{L}+)\\s*,\\s*(\\d{1,2})\\s+(\\p{L}+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected static TimeZone tz = TimeZone.getTimeZone( "Europe/Minsk" );
    protected static final String[] DAYS =
    { "mo", "tu", "we", "th", "fr", "sa", "su" };
    boolean isStopped;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {

        return null;
    }

    /**
     * DOCUMENT_ME!
     */
    public void start(  )
    {
    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     * @param storage DOCUMENT ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void grabData( 
        IProgress progress, ILogger logger, final IStoragePipe storage )
        throws Exception
    {
        isStopped = false;
        progress.setProgressValue( 0 );

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( HttpBrowser.HEADER_ACCEPT_LANGUAGE, "ru" );

        browser.setHeader( HttpBrowser.HEADER_ACCEPT_CHARSET, "windows-1251" );

        isStopped = false;

        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        PageParser parser = new PageParser( storage, logger );

        for( int i = 0; ( i < DAYS.length ) && !isStopped; i++ )
        {
            progress.setProgressValue( ( i * 100 ) / 7 );

            if( isStopped )
            {

                return;
            }

            //            progress.setProgressMessage(  "Load page [" + ( i + 1 ) + "/" + DAYS.length + "]" );
            browser.loadURL( "http://newsvm.com/tv/" + DAYS[i] + ".shtml" );

            browser.parse( parser ); //logger, browser.getData(  ), result );
            storage.finishBlock(  );
        }

        progress.setProgressValue( 100 );
    }

    /**
     * DOCUMENT_ME!
     */
    public void stopGrabbing(  )
    {
        isStopped = true;

    }

    protected static class PageParser extends HtmlHelper.DefaultContentHandler
    {

        protected StringBuffer out;
        protected final IStoragePipe storage;
        protected final ILogger logger;
        protected final String[] nen;

        /**
         * Creates a new PageParser object.
         *
         * @param storage DOCUMENT ME!
         * @param logger DOCUMENT ME!
         *
         * @throws IOException DOCUMENT ME!
         */
        public PageParser( final IStoragePipe storage, final ILogger logger )
            throws IOException
        {
            this.storage = storage;
            this.logger = logger;
            nen = LanguageHelper.loadStrings( 
                    getClass(  ).getPackage(  ).getName(  ).replace( '.', '/' )
                    + "/nen.utf8.list" );
        }

        void patchProgrammes( 
            final String channelID, final TVProgramme[] programmes )
        {

            if( !nen[0].equals( channelID ) )
            {

                return;
            }

            for( int i = 0; i < programmes.length; i++ )
            {

                if( programmes[i].getTitle(  ) != null )
                {

                    for( int j = 2; j < nen.length; j++ )
                    {

                        if( programmes[i].getTitle(  ).indexOf( nen[j] ) != -1 )
                        {
                            programmes[i].setTitle( nen[1] );

                            break;
                        }
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
         * @param atts DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void startElement( 
            String uri, String localName, String qName, Attributes atts )
            throws SAXException
        {

            if( "table".equals( qName ) )
            {
                out = new StringBuffer(  );
            }
            else if( ( out != null ) && "br".equals( qName ) )
            {
                out.append( '\n' );
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

            if( "table".equals( qName ) && ( out != null ) )
            {

                if( out.length(  ) > 4096 )
                {
                    parseText( out.toString(  ) );
                }

                out = null;
            }
            else if( ( out != null ) && "td".equals( qName ) )
            {
                out.append( '\n' );
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

            if( out != null )
            {
                out.append( ch, start, length );
            }
        }

        protected void parseText( final String text ) throws SAXException
        {

            BufferedReader rd = new BufferedReader( new StringReader( text ) );
            long basedate = 0;
            long prevTime = 0;

            String currentChannelID = null;

            String line;

            try
            {

                while( ( line = rd.readLine(  ) ) != null )
                {
                    line = line.trim(  );

                    if( "".equals( line ) )
                    {

                        continue;
                    }

                    if( basedate == 0 )
                    {

                        Matcher mDate = reDate.matcher( line );

                        if( mDate.matches(  ) )
                        {

                            try
                            {
                                basedate =
                                    TimeHelper.getBaseDate( 
                                        tz, mDate.group( 2 ), mDate.group( 3 ),
                                        null, mDate.group( 1 ) );
                            }
                            catch( ParseException ex )
                            {
                            }
                        }
                    }

                    else
                    {

                        if( LineProgrammeHelper.isProgram( line ) )
                        {

                            try
                            {

                                TVProgramme[] programmes =
                                    LineProgrammeHelper.parse( 
                                        logger, line, basedate, prevTime );
                                prevTime = programmes[0].getStart(  );

                                try
                                {
                                    patchProgrammes( 
                                        currentChannelID, programmes );
                                    storage.addProgrammes( 
                                        currentChannelID, programmes );
                                }
                                catch( Exception ex )
                                {
                                    throw new SAXException( ex );
                                }
                            }
                            catch( ParseException ex )
                            {
                            }
                        }
                        else
                        {

                            String channelName = line;

                            if( 
                                ( channelName.toLowerCase(  ).indexOf( 
                                        "перепечатка" ) == -1 )
                                    && ( channelName.toLowerCase(  ).indexOf( 
                                        "профилактика" ) == -1 ) )
                            {
                                currentChannelID =
                                    "newsvm/"
                                    + channelName.replace( '/', '_' );

                                try
                                {
                                    storage.addChannel( 
                                        new TVChannel( 
                                            currentChannelID, channelName ) );
                                }
                                catch( Exception ex )
                                {
                                    throw new SAXException( ex );
                                }

                                prevTime = 0;
                            }
                        }
                    }
                }
            }
            catch( IOException ex )
            {
                throw new SAXException( "IOError", ex );
            }
        }
    }
}

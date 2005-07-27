package freeguide.plugins.grabber.newsvm;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.LanguageHelper;

import freeguide.lib.grabber.HtmlHelper;
import freeguide.lib.grabber.HttpBrowser;
import freeguide.lib.grabber.LineProgrammeHelper;
import freeguide.lib.grabber.TimeHelper;

import freeguide.plugins.BaseModule;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

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
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData grabData( IProgress progress, ILogger logger )
        throws Exception
    {
        isStopped = false;
        progress.setProgressValue( 0 );

        TVData result = new TVData(  );

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( HttpBrowser.HEADER_ACCEPT_LANGUAGE, "ru" );

        browser.setHeader( HttpBrowser.HEADER_ACCEPT_CHARSET, "windows-1251" );

        isStopped = false;

        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        PageParser parser = new PageParser( result, logger );

        for( int i = 0; ( i < DAYS.length ) && !isStopped; i++ )
        {
            progress.setProgressValue( ( i * 100 ) / 7 );

            if( isStopped )
            {

                return null;
            }

            //            progress.setProgressMessage(  "Load page [" + ( i + 1 ) + "/" + DAYS.length + "]" );
            browser.loadURL( "http://newsvm.com/tv/" + DAYS[i] + ".shtml" );

            browser.parse( parser ); //logger, browser.getData(  ), result );

        }

        progress.setProgressValue( 100 );
        patch( result );

        return result;

    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
        isStopped = true;

    }

    protected void patch( final TVData data ) throws IOException
    {

        final String[] nen =
            LanguageHelper.loadStrings( 
                getClass(  ).getPackage(  ).getName(  ).replace( '.', '/' )
                + "/nen.utf8.list" );
        data.iterateProgrammes( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {

                    if( !nen[0].equals( channel.getDisplayName(  ) ) )
                    {
                        stopIterateChanel(  );
                    }
                }

                protected void onProgramme( TVProgramme programme )
                {

                    if( programme.getTitle(  ) == null )
                    {

                        return;
                    }

                    for( int i = 2; i < nen.length; i++ )
                    {

                        if( programme.getTitle(  ).indexOf( nen[i] ) != -1 )
                        {
                            programme.setTitle( nen[1] );

                            break;
                        }
                    }
                }
            } );
    }

    protected static class PageParser extends HtmlHelper.DefaultContentHandler
    {

        protected StringBuffer out;
        protected final TVData result;
        protected final ILogger logger;

        /**
         * Creates a new PageParser object.
         *
         * @param result DOCUMENT ME!
         * @param logger DOCUMENT ME!
         */
        public PageParser( final TVData result, final ILogger logger )
        {
            this.result = result;
            this.logger = logger;
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

            TVChannel currentChannel = null;

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
                                currentChannel.put( programmes );
                            }
                            catch( ParseException ex )
                            {
                            }
                        }
                        else
                        {

                            String channelName = line;

                            if( 
                                channelName.toLowerCase(  ).indexOf( 
                                        "перепечатка" ) != -1 )
                            {
                                basedate = 0;

                            }

                            else
                            {
                                currentChannel =
                                    result.get( 
                                        "newsvm/"
                                        + channelName.replace( '/', '_' ) );

                                currentChannel.setDisplayName( channelName );

                            }

                            prevTime = 0;
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

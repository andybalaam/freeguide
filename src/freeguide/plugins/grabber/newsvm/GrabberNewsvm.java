package freeguide.plugins.grabber.newsvm;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.StringHelper;
import freeguide.common.lib.grabber.HtmlHelper;
import freeguide.common.lib.grabber.HttpBrowser;
import freeguide.common.lib.grabber.LineProgrammeHelper;
import freeguide.common.lib.grabber.ListTVParser;
import freeguide.common.lib.grabber.TimeHelper;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.common.plugininterfaces.IStoragePipe;
import freeguide.plugins.grabber.xmltv.XMLTVConfig.ModuleInfo;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.text.ParseException;

import java.util.Properties;
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
    protected static final String VALUE_ACCEPT_LANGUAGE = "ru";
    protected static final String VALUE_ACCEPT_CHARSET = "windows-1251";
    protected static final String URL_PREFIX = "http://newsvm.com/tv/";
    protected static final String URL_SUFFIX = ".shtml";
    protected static final String TAG_TABLE = "table";
    protected static final String TAG_TD = "td";
    protected static final String TAG_BR = "br";
    protected static final String CHANNEL_PREFIX = "newsvm/";
    protected static final String FILE_NEN =
        "resources/plugins/grabber/newsvm/nen.properties";
    protected static final String EOC1 = "перепечатка";
    protected static final String EOC2 = "профилактика";
    protected static final Pattern reDate =
        Pattern.compile( 
            "(\\p{L}+)\\s*,\\s*(\\d{1,2})\\s+(\\p{L}+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected static final TimeZone tz =
        TimeZone.getTimeZone( "Europe/Minsk" );
    protected static final String[] DAYS = "mo,tu,we,th,fr,sa,su".split( "," );

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
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public boolean grabData( 
        IProgress progress, ILogger logger, final IStoragePipe storage )
        throws Exception
    {
        progress.setProgressValue( 0 );

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_LANGUAGE, VALUE_ACCEPT_LANGUAGE );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_CHARSET, VALUE_ACCEPT_CHARSET );

        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        PageParser parser = new PageParser( storage, logger );

        for( int i = 0; i < DAYS.length; i++ )
        {
            progress.setProgressValue( ( i * 100 ) / 7 );

            if( Thread.interrupted(  ) )
            {
                return true;
            }

            //            progress.setProgressMessage(  "Load page [" + ( i + 1 ) + "/" + DAYS.length + "]" );
            browser.loadURL( URL_PREFIX + DAYS[i] + URL_SUFFIX );

            browser.parse( parser ); //logger, browser.getData(  ), result );
            storage.finishBlock(  );
        }

        progress.setProgressValue( 100 );

        return true;
    }

    protected static class PageParser extends HtmlHelper.DefaultContentHandler
    {
        protected StringBuffer out;
        protected final IStoragePipe storage;
        protected final ILogger logger;
        protected final Properties nen;

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
            nen = new Properties(  );
            nen.load( 
                PageParser.class.getClassLoader(  )
                                .getResourceAsStream( FILE_NEN ) );
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
            if( TAG_TABLE.equals( qName ) )
            {
                out = new StringBuffer(  );
            }
            else if( ( out != null ) && TAG_BR.equals( qName ) )
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
            if( TAG_TABLE.equals( qName ) && ( out != null ) )
            {
                if( out.length(  ) > 4096 )
                {
                    parseText( out.toString(  ) );
                }

                out = null;
            }
            else if( ( out != null ) && TAG_TD.equals( qName ) )
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

                    if( StringHelper.EMPTY_STRING.equals( line ) )
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
                                basedate = TimeHelper.getBaseDate( 
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
                                    ListTVParser.patch( 
                                        currentChannelID, programmes, nen );
                                    storage.addProgrammes( 
                                        currentChannelID, programmes );
                                }
                                catch( Exception ex )
                                {
                                    throw new SAXException( 
                                        ex.getMessage(  ), ex );
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
                                ( channelName.toLowerCase(  ).indexOf( EOC1 ) == -1 )
                                    && ( channelName.toLowerCase(  )
                                                        .indexOf( EOC2 ) == -1 ) )
                            {
                                currentChannelID = CHANNEL_PREFIX
                                    + channelName.replace( '/', '_' );

                                try
                                {
                                    storage.addChannel( 
                                        new TVChannel( 
                                            currentChannelID, channelName ) );
                                }
                                catch( Exception ex )
                                {
                                    throw new SAXException( 
                                        ex.getMessage(  ), ex );
                                }

                                prevTime = 0;
                            }
                        }
                    }
                }
            }
            catch( IOException ex )
            {
                throw new SAXException( "IOError: " + ex.getMessage(  ), ex );
            }
        }
    }

    public boolean chooseChannels( IProgress progress, ILogger logger )
    {
        // No need to choose channels for this grabber
        return false;
    }

    public int chooseChannelsOne( ModuleInfo moduleInfo, IProgress progress, ILogger logger )
    {
        // No need to choose channels for this grabber
        return -1;
    }
}

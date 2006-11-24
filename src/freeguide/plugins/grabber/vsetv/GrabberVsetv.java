package freeguide.plugins.grabber.vsetv;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorChannels;
import freeguide.common.lib.general.ResourceHelper;
import freeguide.common.lib.grabber.HttpBrowser;
import freeguide.common.lib.grabber.ListTVParser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.common.plugininterfaces.IStoragePipe;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.JDialog;

/**
 * Grabber for www.vsetv.com.
 *
 * @author Alex Buloichik
 */
public class GrabberVsetv extends BaseModule implements IModuleGrabber
{
    protected static final String CHANNEL_PREFIX = "vsetv/";
    protected static final String URL = "http://www.vsetv.com";
    protected static final String URL_LOGIN = URL + "/login.php";
    protected static final String URL_SETTINGS = URL + "/settings.php";
    protected static final String URL_SETTINGS_REFERER =
        URL_SETTINGS + "?fromscript=/";
    protected static final String URL_DATA = URL + "/vsetv.php";
    protected static final String FILE_TIMEZONES =
        "resources/plugins/grabber/vsetv/timezones.properties";
    protected static final String FILE_NEN =
        "resources/plugins/grabber/vsetv/nen.properties";
    protected static final String VALUE_ACCEPT_LANGUAGE = "ru";
    protected static final String VALUE_ACCEPT_CHARSET = "windows-1251";
    protected static final String PARAM_HOURS_BEG = "hours1";
    protected static final String PARAM_HOURS_END = "hours2";
    protected static final String PARAM_HOURS_BEG_VALUE = "5";
    protected static final String PARAM_HOURS_END_VALUE = "5";
    protected static final String PARAM_DATE = "selectdate";
    protected static final String PARAM_CATEGORY = "category";
    protected static final String VALUE_CATEGORY_PROGRAMMES = "prog";
    protected static final String VALUE_CATEGORY_ANNOUNCES = "anfi";
    protected static final String PARAM_CHANNELS = "selectchannels";
    protected static final String PARAM_CHANNELS_PERSONAL = "personal";
    protected static final String PARAM_LOGIN_USER = "inlogin";
    protected static final String PARAM_LOGIN_PASS = "inpassword";
    protected static final String PARAM_MERIDIAN = "meridian";
    protected static final String PARAM_NOWPERIOD = "nowperiod";
    protected static final String PARAM_NOWPERIOD_VALUE = "60";
    protected static final String PARAM_DOSAVE = "meridian";
    protected static final String PARAM_DOSAVE_VALUE = "1";
    protected static final String PARAM_VALUE_ON = "on";
    protected static final String PARAM_SORT = "selectsort";
    protected static final String PARAM_SORT_VALUE = "chan";
    protected static final TimeZone DEFAULT_TIMEZONE =
        TimeZone.getTimeZone( "Europe/Kiev" );
    protected VsetvConfig config = new VsetvConfig(  );
    protected Properties TIMEZONES;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {
        return config;
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
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     * @throws SAXException DOCUMENT_ME!
     */
    protected TVChannelsSet getChannelList(  )
        throws IOException, SAXException
    {
        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_LANGUAGE, VALUE_ACCEPT_LANGUAGE );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_CHARSET, VALUE_ACCEPT_CHARSET );

        browser.setHeader( HttpBrowser.HEADER_REFERER, URL );

        browser.loadURL( URL );

        HandlerChannelsList handler = new HandlerChannelsList(  );

        browser.parse( handler );

        //return handler.getResult(  );
        return null;
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
        if( TIMEZONES == null )
        {
            loadTimeZones(  );

        }

        final TimeZone tz;
        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_LANGUAGE, VALUE_ACCEPT_LANGUAGE );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_CHARSET, VALUE_ACCEPT_CHARSET );

        browser.setHeader( HttpBrowser.HEADER_REFERER, URL );

        logger.info( i18n.getString( "Logging.LoadInitial" ) );

        browser.loadURL( URL );

        if( Thread.interrupted(  ) )
        {
            return;
        }

        HandlerDates handlerDates = new HandlerDates(  );

        browser.parse( handlerDates );

        String[] dates = handlerDates.getResult(  );

        progress.setStepCount( 2 + ( dates.length * 2 ) );
        progress.setStepNumber( 1 );

        if( config.isAuth )
        {
            login( logger, browser );
            tz = checkSettings( logger, browser );
        }
        else
        {
            tz = DEFAULT_TIMEZONE;
        }

        if( Thread.interrupted(  ) )
        {
            return;
        }

        progress.setStepNumber( 2 );

        HandlerParseProg handler = new HandlerParseProg( logger, tz, i18n );

        final Map<String, String> request = new TreeMap<String, String>(  );

        request.put( PARAM_SORT, PARAM_SORT_VALUE );

        if( config.isAuth )
        {
            request.put( PARAM_CHANNELS, PARAM_CHANNELS_PERSONAL );

        }

        else
        {
            request.put( PARAM_CHANNELS, config.channelGroup );

        }

        request.put( PARAM_HOURS_BEG, PARAM_HOURS_BEG_VALUE );

        request.put( PARAM_HOURS_END, PARAM_HOURS_END_VALUE );

        for( int i = 0; i < dates.length; i++ )
        {
            if( Thread.interrupted(  ) )
            {
                return;
            }

            request.put( PARAM_DATE, dates[i] );

            logger.info( 
                MessageFormat.format( 
                    i18n.getString( "Logging.LoadProgs" ), i + 1, dates.length ) );

            request.put( PARAM_CATEGORY, VALUE_CATEGORY_PROGRAMMES );

            browser.loadURL( URL_DATA, request, false );
            progress.setStepNumber( 3 + ( i * 2 ) );

            handler.setAnnounces( false );

            browser.parse( handler );

            if( Thread.interrupted(  ) )
            {
                return;
            }

            logger.info( 
                MessageFormat.format( 
                    i18n.getString( "Logging.LoadAnons" ), i + 1, dates.length ) );

            request.put( PARAM_CATEGORY, VALUE_CATEGORY_ANNOUNCES );

            browser.loadURL( URL_DATA, request, false );
            progress.setStepNumber( 4 + ( i * 2 ) );

            handler.setAnnounces( true );

            browser.parse( handler );

            handler.store( storage );
        }

        logger.info( i18n.getString( "Logging.Done" ) );
    }

    protected void loadTimeZones(  )
    {
        TIMEZONES = new Properties(  );

        try
        {
            TIMEZONES.load( 
                ResourceHelper.getUncachedStream( FILE_TIMEZONES ) );

        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( 
                Level.SEVERE,
                "Error loading timezone settings for www.vsetv.com", ex );
        }
    }

    protected TimeZone checkSettings( ILogger logger, HttpBrowser browser )
        throws IOException, SAXException
    {
        logger.info( i18n.getString( "Logging.CheckSettings" ) );

        browser.loadURL( URL_SETTINGS );

        HandlerSettings handler = new HandlerSettings(  );

        browser.parse( handler );

        if( config.isGetAll && handler.isNeedUpdate(  ) )
        {
            logger.info( i18n.getString( "Logging.ModifySettings" ) );

            final Map<String, String> values = new TreeMap<String, String>(  );

            values.put( 
                PARAM_MERIDIAN, Integer.toString( handler.meridianValue ) );

            values.put( PARAM_NOWPERIOD, PARAM_NOWPERIOD_VALUE );

            values.put( PARAM_DOSAVE, PARAM_DOSAVE_VALUE );

            String[] chs = handler.getChannelIDs(  );

            for( final String ch : chs )
            {
                values.put( ch, PARAM_VALUE_ON );
            }

            browser.setHeader( 
                HttpBrowser.HEADER_REFERER, URL_SETTINGS_REFERER );
            browser.loadURL( URL_SETTINGS, values, false );

        }

        return handler.getTimeZone( TIMEZONES );

    }

    protected void login( ILogger logger, HttpBrowser browser )
        throws IOException
    {
        logger.info( i18n.getString( "Logging.Login" ) );

        final Map<String, String> loginInfo = new TreeMap<String, String>(  );

        loginInfo.put( PARAM_LOGIN_USER, config.user );

        loginInfo.put( PARAM_LOGIN_PASS, config.pass );

        browser.loadURL( URL_LOGIN, loginInfo, true );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return new VsetvConfigurationUIController( this );

    }

    protected static void patch( final TVData data ) throws IOException
    {
        final Properties nen = new Properties(  );
        nen.load( 
            GrabberVsetv.class.getClassLoader(  ).getResourceAsStream( 
                FILE_NEN ) );

        data.iterateChannels( 
            new TVIteratorChannels(  )
            {
                protected void onChannel( TVChannel channel )
                {
                    ListTVParser.patch( 
                        channel.getID(  ), channel.getProgrammes(  ), nen );
                }
            } );
    }
}

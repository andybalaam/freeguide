package freeguide.plugins.grabber.www_vsetv_com;

import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;

import freeguide.lib.grabber.HttpBrowser;

import freeguide.plugins.BaseModule;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleConfigurationUI;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javax.swing.JDialog;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabberVsetv extends BaseModule implements IModuleGrabber
{

    /** DOCUMENT ME! */
    public static final String ID = "vsetv";
    protected Config config = new Config(  );
    protected Properties TIMEZONES;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return ID;

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

        browser.setHeader( "Accept-Language", "ru" );

        browser.setHeader( "Accept-Charset", "windows-1251" );

        browser.setHeader( "Referer", "http://www.vsetv.com" );

        browser.loadURL( "http://www.vsetv.com" );

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
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData grabData( IProgress progress, ILogger logger )
        throws Exception
    {

        if( TIMEZONES == null )
        {
            loadTimeZones(  );

        }

        final TimeZone tz;

        /*        HttpBrowser b = new HttpBrowser();


        Map<String, List<Prog>> siteData = new TreeMap<String, List<Prog>>();


        HandlerParseProg h = new HandlerParseProg(logger, siteData, tz);


        h.setAnnounces(false);


        b.loadURL("file:///tmp/prog.html");


        b.parse(h);


        h.setAnnounces(true);


        b.loadURL("file:///tmp/anfi.html");


        b.parse(h);


        return siteData;*/
        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( "Accept-Language", "ru" );

        browser.setHeader( "Accept-Charset", "windows-1251" );

        browser.setHeader( "Referer", "http://www.vsetv.com" );

        logger.info( "Load initial page" );

        browser.loadURL( "http://www.vsetv.com" );

        HandlerDates handlerDates = new HandlerDates(  );

        browser.parse( handlerDates );

        if( config.isAuth )
        {
            login( logger, browser );

            tz = checkSettings( logger, browser );

        }

        else
        {
            tz = TimeZone.getTimeZone( "Europe/Kiev" );

        }

        TVData result = new TVData(  );

        HandlerParseProg handler = new HandlerParseProg( logger, result, tz );

        Map request = new TreeMap(  );

        request.put( "selectsort", "chan" );

        if( config.isAuth )
        {
            request.put( "selectchannels", "personal" );

        }

        else
        {
            request.put( "selectchannels", config.channelGroup );

        }

        request.put( "hours1", "5" );

        request.put( "hours2", "5" );

        String[] dates = handlerDates.getResult(  );

        for( int i = 0; i < dates.length; i++ )
        {
            request.put( "selectdate", dates[i] );

            logger.info( 
                "Load list page [" + ( i + 1 ) + "/" + dates.length + "]" );

            request.put( "category", "prog" );

            browser.loadURL( "http://www.vsetv.com/vsetv.php", request, true );

            handler.setAnnounces( false );

            browser.parse( handler );

            logger.info( 
                "Load announce page [" + ( i + 1 ) + "/" + dates.length + "]" );

            request.put( "category", "anfi" );

            browser.loadURL( "http://www.vsetv.com/vsetv.php", request, true );

            handler.setAnnounces( true );

            browser.parse( handler );

        }

        logger.info( "Done" );

        return result;

    }

    protected void loadTimeZones(  )
    {
        TIMEZONES = new Properties(  );

        try
        {
            TIMEZONES.load( 
                this.getClass(  ).getClassLoader(  ).getResourceAsStream( 
                    this.getClass(  ).getPackage(  ).getName(  ).replace( 
                        '.', '/' ) + "/timezones.properties" ) );

        }

        catch( Exception ex )
        {
            ex.printStackTrace(  );

        }
    }

    protected TimeZone checkSettings( ILogger logger, HttpBrowser browser )
        throws IOException, SAXException
    {
        logger.info( "Check settings" );

        browser.loadURL( "http://www.vsetv.com/settings.php" );

        HandlerSettings handler = new HandlerSettings(  );

        browser.parse( handler );

        if( config.isGetAll && handler.isNeedUpdate(  ) )
        {
            logger.info( "Modify settings" );

            Map values = new TreeMap(  );

            values.put( "meridian", Integer.toString( handler.meridianValue ) );

            values.put( "nowperiod", "60" );

            String[] chs = handler.getChannelIDs(  );

            for( int i = 0; i < chs.length; i++ )
            {
                values.put( chs[i], "on" );

            }

            browser.loadURL( 
                "http://www.vsetv.com/savesettings.php", values, true );

        }

        return handler.getTimeZone( TIMEZONES );

    }

    protected void login( ILogger logger, HttpBrowser browser )
        throws IOException
    {
        logger.info( "Login" );

        Map loginInfo = new TreeMap(  );

        loginInfo.put( "inlogin", config.user );

        loginInfo.put( "inpassword", config.pass );

        browser.loadURL( "http://www.vsetv.com/login.php", loginInfo, true );
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

        return new ConfigurationUIController( this );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefs DOCUMENT_ME!
     */
    public void setConfigStorage( Preferences prefs )
    {
        super.setConfigStorage( prefs );

        loadObjectFromPreferences( config );

    }

    /**
     * DOCUMENT_ME!
     */
    public void saveConfig(  )
    {
        saveObjectToPreferences( config );

    }
}

package freeguide.plugins.grabber.vsetv;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.LanguageHelper;

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
import java.util.logging.Level;
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

    protected VsetvConfig config = new VsetvConfig(  );
    protected Properties TIMEZONES;

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
        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( "Accept-Language", "ru" );

        browser.setHeader( "Accept-Charset", "windows-1251" );

        browser.setHeader( "Referer", "http://www.vsetv.com" );

        logger.info( "Load initial page" );

        browser.loadURL( "http://www.vsetv.com" );

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
            tz = TimeZone.getTimeZone( "Europe/Kiev" );

        }

        progress.setStepNumber( 2 );

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

        for( int i = 0; i < dates.length; i++ )
        {
            request.put( "selectdate", dates[i] );

            logger.info( 
                "Load list page [" + ( i + 1 ) + "/" + dates.length + "]" );

            request.put( "category", "prog" );

            browser.loadURL( "http://www.vsetv.com/vsetv.php", request, true );
            progress.setStepNumber( 3 + ( i * 2 ) );

            handler.setAnnounces( false );

            browser.parse( handler );

            logger.info( 
                "Load announce page [" + ( i + 1 ) + "/" + dates.length + "]" );

            request.put( "category", "anfi" );

            browser.loadURL( "http://www.vsetv.com/vsetv.php", request, true );
            progress.setStepNumber( 4 + ( i * 2 ) );

            handler.setAnnounces( true );

            browser.parse( handler );

        }

        logger.info( "Done" );

        patch( result );

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
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE,
                "Error loading timezone settings for www.vsetv.com", ex );
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

        return new VsetvConfigurationUIController( this );

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

    protected void patch( final TVData data ) throws IOException
    {

        final String[] nen =
            LanguageHelper.loadStrings( 
                getClass(  ).getClassLoader(  ).getResourceAsStream( 
                    getClass(  ).getPackage(  ).getName(  ).replace( '.', '/' )
                    + "/nen.utf8.list" ) );
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
}

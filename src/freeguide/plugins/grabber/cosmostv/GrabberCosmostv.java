package freeguide.plugins.grabber.cosmostv;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.grabber.HttpBrowser;
import freeguide.common.lib.grabber.ListTVParser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.common.plugininterfaces.IStoragePipe;

import java.util.TimeZone;

/**
 * Grabber for http://cosmostv.com.
 *
 * @author Alex Buloichik
 */
public class GrabberCosmostv extends BaseModule implements IModuleGrabber
{
    protected static final TimeZone TIMEZONE =
        TimeZone.getTimeZone( "Europe/Minsk" );
    protected static final String VALUE_ACCEPT_LANGUAGE = "ru";
    protected static final String VALUE_ACCEPT_CHARSET = "windows-1251";
    protected static final String URL = "http://www.cosmostv.com/schedule.asp";
    protected static final String CHANNELS_PREFIX = "cosmostv/";

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
        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_LANGUAGE, VALUE_ACCEPT_LANGUAGE );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_CHARSET, VALUE_ACCEPT_CHARSET );

        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        logger.info( i18n.getString( "Logging.LoadList" ) );

        HandlerZips handlerZips = new HandlerZips(  );

        browser.loadURL( URL );

        browser.parse( handlerZips );

        String[] zips = handlerZips.getZips(  );

        logger.info( i18n.getString( "Logging.LoadData" ) );

        new ListTVParser( CHANNELS_PREFIX, storage ).parseZips( 
            zips, TIMEZONE, progress, logger );

        return true;
    }
}

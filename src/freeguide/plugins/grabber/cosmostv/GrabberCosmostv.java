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
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabberCosmostv extends BaseModule implements IModuleGrabber
{

    protected static final TimeZone TIMEZONE =
        TimeZone.getTimeZone( "Europe/Minsk" );
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

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( "Accept-Language", "ru" );

        browser.setHeader( "Accept-Charset", "windows-1251" );

        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        logger.info( "Load initial page..." );

        HandlerZips handlerZips = new HandlerZips(  );

        browser.loadURL( "http://www.cosmostv.com/schedule.asp" );

        browser.parse( handlerZips );

        String[] zips = handlerZips.getZips(  );

        logger.info( "Load data files..." );

        new ListTVParser( "cosmostv/", storage ).parseZips( 
            zips, TIMEZONE, progress, logger );
    }

    /**
     * DOCUMENT_ME!
     */
    public void stopGrabbing(  )
    {
        isStopped = true;

    }
}

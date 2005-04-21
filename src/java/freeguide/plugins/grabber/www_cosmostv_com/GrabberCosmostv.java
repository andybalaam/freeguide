package freeguide.plugins.grabber.www_cosmostv_com;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVData;

import freeguide.lib.grabber.HttpBrowser;
import freeguide.lib.grabber.ListTVParser;

import freeguide.plugins.BaseModule;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import java.util.TimeZone;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabberCosmostv extends BaseModule implements IModuleGrabber
{

    /** DOCUMENT ME! */
    public static final String ID = "cosmostv";
    protected static final TimeZone TIMEZONE =
        TimeZone.getTimeZone( "Europe/Minsk" );
    boolean isStopped;

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

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( "Accept-Language", "ru" );

        browser.setHeader( "Accept-Charset", "windows-1251" );

        progress.setProgressMessage( FreeGuide.msg.getString( "downloading" ) );

        logger.info( "Load initial page..." );

        HandlerZips handlerZips = new HandlerZips(  );

        browser.loadURL( "http://www.cosmostv.com/schedule.asp" );

        browser.parse( handlerZips );

        String[] zips = handlerZips.getZips(  );

        logger.info( "Load data files..." );

        return new ListTVParser( ID + "/" ).parseZips( 
            "www.cosmostv.com", zips, TIMEZONE, progress, logger );

    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
        isStopped = true;

    }
}

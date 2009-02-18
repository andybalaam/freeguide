package freeguide.plugins.grabber.ntvplus;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.grabber.ListTVParser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.common.plugininterfaces.IStoragePipe;
import freeguide.plugins.grabber.xmltv.XMLTVConfig.ModuleInfo;

import java.util.TimeZone;

/**
 * Grabber for www.ntvplus.ru.
 *
 * @author Alex Buloichik
 */
public class GrabberNtvplus extends BaseModule implements IModuleGrabber
{
    protected static final String CHANNEL_PREFIX = "ntvplus/";
    protected static final String URL =
        "http://www.ntvplus.ru/static/schedule/schedule.zip";
    protected static final TimeZone TIMEZONE =
        TimeZone.getTimeZone( "Europe/Moscow" );

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
        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        new ListTVParser( CHANNEL_PREFIX, storage ).parseZips( 
            new String[] { URL }, TIMEZONE, progress, logger );

        return true;
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

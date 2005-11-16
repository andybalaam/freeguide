package freeguide.plugins.grabber.ntvplus;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVData;

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
public class GrabberNtvplus extends BaseModule implements IModuleGrabber
{

    protected static final TimeZone TIMEZONE =
        TimeZone.getTimeZone( "Europe/Moscow" );
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
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData grabData( IProgress progress, ILogger logger )
        throws Exception
    {
        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        return new ListTVParser( "ntvplus/" ).parseZips( 
            new String[] { "http://www.ntvplus.ru/static/schedule/schedule.zip" },
            TIMEZONE, progress, logger );

    }

    /**
     * DOCUMENT_ME!
     */
    public void stopGrabbing(  )
    {
        isStopped = true;

    }
}

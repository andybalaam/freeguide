package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVData;

/**
 * Interface for grabber modules. Each grabber module should implement this
 * interface.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleGrabber extends IModule
{

    /**
     * Get data from site.
     *
     * @param progress IProgress
     * @param logger ILogger
     *
     * @return data
     *
     * @throws Exception
     */
    TVData grabData( IProgress progress, ILogger logger )
        throws Exception;

    /**
     * Stop grabbing.
     */
    void stop(  );
}

package freeguide.common.plugininterfaces;

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
     * @throws Exception
     */
    void grabData( IProgress progress, ILogger logger, IStoragePipe storage )
        throws Exception;

    /**
     * Stop grabbing.
     */
    void stopGrabbing(  );

    /**
     * Start module. Called when application starts.
     */
    void start(  );

    /**
     * Stop module. Called when application stops.
     */
    void stop(  );
}

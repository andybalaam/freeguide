package freeguide.common.plugininterfaces;

/**
 * Interface for grabber modules. Each grabber module should implement this
 * interface. Each grabber should be ready to be interrupted by
 * Thread.interrupt().
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
     * @param storage DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception
     */
    boolean grabData( 
        IProgress progress, ILogger logger, IStoragePipe storage )
        throws Exception;

    /**
     * Start module. Called when application starts.
     */
    void start(  );

    /**
     * Stop module. Called when application stops.
     */
    void stop(  );
}

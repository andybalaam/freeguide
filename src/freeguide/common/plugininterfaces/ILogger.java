package freeguide.common.plugininterfaces;

/**
 * Interface for display or store logs about module work.  Implemented
 * progress bar can implement logger also, and can display additional
 * information.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface ILogger
{

    /**
     * Info level message.
     *
     * @param message
     */
    void info( String message );

    /**
     * Warning level message.
     *
     * @param message
     */
    void warning( String message );

    /**
     * Error message.
     *
     * @param message
     */
    void error( String message );

    /**
     * Error message with exception information.
     *
     * @param message
     * @param ex
     */
    void error( String message, Exception ex );
}

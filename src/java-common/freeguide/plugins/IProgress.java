package freeguide.plugins;

/**
 * Interface for display progress about module work. Implemented progress bar
 * should display progress bar and current step's message.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IProgress
{

    /**
     * Set percent of work ready. You can use setStepCount/setStepNumber
     * instead this method.
     *
     * @param percent percent value
     */
    void setProgressValue( int percent );

    /**
     * Set step count for progress.  It is more friendly method for calculate
     * percents.  Module can setup step count on startup, and set step number
     * after each step. In this case, progress bar should calculate
     * percentage himself.
     *
     * @param stepCount
     */
    void setStepCount( int stepCount );

    /**
     * Set step number for progress.  It is more friendly method for calculate
     * percents.  Module can setup step count on startup, and set step number
     * after each step. In this case, progress bar should calculate
     * percentage himself.
     *
     * @param stepNumber
     */
    void setStepNumber( int stepNumber );

    /**
     * Set message about current work. Module should call attention to
     * preferred languages.
     *
     * @param message message string
     */
    void setProgressMessage( String message );

    /**
     * Set label for button. Module should call attention to preferred
     * languages.
     *
     * @param label label string
     */
    void setButtonLabel( String label );
}

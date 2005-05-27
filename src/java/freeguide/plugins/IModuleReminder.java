package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVProgramme;

/**
 * Reminder support module for alarm or VCR recording.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public interface IModuleReminder extends IModule
{

    /**
     * DOCUMENT_ME!
     */
    public void reschedule(  );

    /**
     * DOCUMENT_ME!
     */
    public void start(  );

    /**
     * DOCUMENT_ME!
     */
    public void stop(  );

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isFavourite( final TVProgramme programme );

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isSelected( final TVProgramme programme );

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public void selectProgramme( final TVProgramme programme );

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public void deselectProgramme( final TVProgramme programme );
}

package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVProgramme;

import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

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
    public boolean isSelected( TVProgramme programme );

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

    /**
     * DOCUMENT_ME!
     *
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToMenu( final JMenu menu );

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToPopupMenu( 
        final TVProgramme programme, final JPopupMenu menu );

    /**
     * DOCUMENT_ME!
     *
     * @param prog DOCUMENT_ME!
     * @param label DOCUMENT_ME!
     */
    public void onPaintProgrammeLabel( 
        final TVProgramme prog, final JLabel label );

    /**
     * DOCUMENT_ME!
     *
     * @param prog DOCUMENT_ME!
     * @param label DOCUMENT_ME!
     * @param graphics DOCUMENT_ME!
     */
    public void onPaintProgrammeLabel( 
        final TVProgramme prog, final JLabel label, final Graphics2D graphics );
}

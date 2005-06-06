package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVProgramme;

import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * Reminder support module for alarm or VCR recording.  There are favourites
 * and selections. Favourite describes programme parameters, like channel and
 * title. Selection describes only one TV programme for scheduled date and
 * time. New grabbed programmes can have favourites, but can't have
 * selections. Selections can change favourites settings only for own
 * programme. For example, programme can equals to favourite, but there is
 * selection where flag  'selected'==false. In this case programme should be
 * deselected.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public interface IModuleReminder extends IModule
{

    /**
     * Read programmes and schedule again.
     */
    public void reschedule(  );

    /**
     * Start scheduler for wait new schedule.
     */
    public void start(  );

    /**
     * Stop scheduler.
     */
    public void stop(  );

    /**
     * Check if programme is selected.
     *
     * @param programme programme
     *
     * @return true if selected
     */
    public boolean isSelected( TVProgramme programme );

    /**
     * Select programme.
     *
     * @param programme programme
     */
    public void selectProgramme( final TVProgramme programme );

    /**
     * Deselect programme.
     *
     * @param programme programme
     */
    public void deselectProgramme( final TVProgramme programme );

    /**
     * Add items to main frame menu.
     *
     * @param menu main frame menu
     */
    public void addItemsToMenu( final JMenu menu );

    /**
     * Add items to popup menu for programme.
     *
     * @param programme programme
     * @param menu popup menu
     */
    public void addItemsToPopupMenu( 
        final TVProgramme programme, final JPopupMenu menu );

    /**
     * Calls on paint programme label.  You can change border, background
     * color, etc. Label has default presets.
     *
     * @param programme programme
     * @param label label
     */
    public void onPaintProgrammeLabel( 
        final TVProgramme programme, final JLabel label );

    /**
     * Calls on paint programme label. You can draw icon, etc.
     *
     * @param programme programme
     * @param label label
     * @param graphics graphics object
     */
    public void onPaintProgrammeLabel( 
        final TVProgramme programme, final JLabel label,
        final Graphics2D graphics );
}

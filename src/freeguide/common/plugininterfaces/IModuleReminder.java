package freeguide.common.plugininterfaces;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;

import java.awt.Component;
import java.awt.Graphics;

import java.util.Set;

import javax.swing.JComponent;
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
     * Check if programme is in the even one white list or in
     * favourites.
     *
     * @param programme programme
     *
     * @return true if in guide
     */
    public boolean isInGuide( TVProgramme programme );

    /**
     * Switch programme selection for all reminders.
     *
     * @param programme DOCUMENT ME!
     */
    void switchProgrammeSelection( TVProgramme programme );

    void showProgramme( 
        TVProgramme programme, Component component, Graphics g );

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

    Favourite getFavourite( TVProgramme programme );

    void addFavourite( final Favourite favourite );

    void removeFavourite( final Favourite favourite );

    ManualSelection getManualSelection( TVProgramme programme );

    Set<String> getReminderNames(  );
}

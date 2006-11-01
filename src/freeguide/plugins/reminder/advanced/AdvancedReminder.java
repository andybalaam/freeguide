package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleReminder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * Alarm reminder module. It allow to create many user's reminders.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class AdvancedReminder extends BaseModule implements IModuleReminder
{
    /** Config object. */
    protected final Config config = new Config(  );

    /**
     * Start plugin.
     */
    public void start(  )
    {
        config.reminders.clear(  );
        config.reminders.add( new OneReminderConfig(  ) );
        config.reminders.add( new OneReminderConfig(  ) );
        config.reminders.add( new OneReminderConfig(  ) );
    }

    /**
     * Stop plugin.
     */
    public void stop(  )
    {
    }

    protected void onTime(  )
    {
    }

    protected void onMenuItem(  )
    {
    }

    protected long getNextTime(  )
    {
        return 0;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToPopupMenu( TVProgramme programme, JPopupMenu menu )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Favourite getFavourite( TVProgramme programme )
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourite DOCUMENT_ME!
     */
    public void addFavourite( Favourite favourite )
    {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourite DOCUMENT_ME!
     */
    public void removeFavourite( Favourite favourite )
    {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT_ME!
     *
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToMenu( JMenu menu )
    {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT_ME!
     */
    public void reschedule(  )
    {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     * @param newSelection DOCUMENT_ME!
     * @param newHighlight DOCUMENT_ME!
     */
    public void setProgrammeSelection( 
        TVProgramme programme, boolean newSelection, boolean newHighlight )
    {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isHighlighted( TVProgramme programme )
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isSelected( TVProgramme programme )
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return new AdvancedReminderUIController( config );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {
        return config;
    }

    /**
     * Config for reminder.
     *
     * @author Alex Buloichik
     */
    public static class Config
    {
        /** DOCUMENT ME! */
        public static Class channels_KEY_TYPE = String.class;

        /** DOCUMENT ME! */
        public static Class channels_VALUE_TYPE = String.class;

        /** DOCUMENT ME! */
        public static Class reminders_TYPE = OneReminderConfig.class;

        /** Path for cron file export. */
        public String cronOutputPath;

        /** Map for chennels id into hardware channels. */
        public Map<String, String> channels = new TreeMap<String, String>(  );

        /** All user's created reminders. */
        public List<OneReminderConfig> reminders =
            new ArrayList<OneReminderConfig>(  );
    }

    /**
     * This is config for one reminder.
     *
     * @author Alex Buloichik
     */
    public static class OneReminderConfig
    {
        /** Reminder name. */
        public String name;

        /** Show popup. */
        public boolean isPopup;

        /** Time when popup should be open before programme start. */
        public long popupOpenTimeBefore = 300000L;

        /** Time when popup should be closed after programme start. */
        public long popupCloseTimeAfter = 600000L;

        /** Play sound. */
        public boolean isSound;

        /** Time when sound should be played before programme start. */
        public long soundPlayTimeBefore = 300000L;

        /** File which should be played. */
        public String soundFile;

        /** Execute command. */
        public boolean isExecute;

        /** Time when execute start command programme start. */
        public long executeStartTimeBefore = 300000L;

        /** Time when execute stop command after programme end. */
        public long executeStopTimeBefore = 300000L;

        /** Start command. */
        public String executeStartCommand;

        /** Stop command. */
        public String executeStopCommand;
    }
}

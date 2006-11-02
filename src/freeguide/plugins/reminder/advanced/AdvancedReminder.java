package freeguide.plugins.reminder.advanced;

import freeguide.common.gui.FavouritesController;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;
import freeguide.common.lib.general.Utils;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleReminder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
    protected SchedulerThread thread;

    /**
     * Start plugin.
     */
    public void start(  )
    {
        thread = new SchedulerThread(  );
        thread.start(  );
    }

    /**
     * Stop plugin.
     */
    public void stop(  )
    {
        if( thread != null )
        {
            thread.finish(  );
            thread = null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Set<String> getReminderNames(  )
    {
        final Set<String> result = new TreeSet<String>(  );

        for( final OneReminderConfig cfg : config.reminders )
        {
            if( cfg.name != null )
            {
                result.add( cfg.name );
            }
        }

        return result;
    }

    protected void onMenuItem(  )
    {
        FavouritesController favController =
            new FavouritesController( 
                Application.getInstance(  ).getApplicationFrame(  ),
                config.favouritesList,
                Application.getInstance(  ).getDataStorage(  ).getInfo(  ).channelsList );

        Utils.centreDialog( 
            Application.getInstance(  ).getApplicationFrame(  ),
            favController.getListDialog(  ) );
        favController.getListDialog(  ).setVisible( true );

        if( favController.isChanged(  ) )
        {
            config.favouritesList = favController.getFavourites(  );

            saveConfigNow(  );

            Application.getInstance(  ).getViewer(  ).redraw(  );

            reschedule(  );
        }
    }

    /**
     * Construct popup menu.
     *
     * @param programme programme
     * @param menu menu
     */
    public void addItemsToPopupMenu( TVProgramme programme, JPopupMenu menu )
    {
        PopupMenuHandler.fillMenu( menu, programme, this );
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
        JMenuItem item = new JMenuItem(  );
        item.setText( i18n.getString( "menu.label" ) );
        menu.insert( item, 0 );
        item.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    onMenuItem(  );
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     */
    public void reschedule(  )
    {
        if( thread != null )
        {
            thread.reschedule(  );
        }
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
     * Get config object.
     *
     * @return config object
     */
    public Object getConfig(  )
    {
        return config;
    }

    /**
     * Config for plugin.
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

        /** Favourite element type. */
        public static final Class favouritesList_TYPE = Favourite.class;

        /** Selection element type. */
        public static final Class manualSelectionList_TYPE =
            ManualSelection.class;

        /** Path for cron file export. */
        public String cronOutputPath;

        /** Map for chennels id into hardware channels. */
        public Map<String, String> channels = new TreeMap<String, String>(  );

        /** All user's created reminders. */
        public List<OneReminderConfig> reminders =
            new ArrayList<OneReminderConfig>(  );

        /** Favourites list. */
        public List<Favourite> favouritesList = new ArrayList<Favourite>(  );

        /** Selections list. */
        public List<ManualSelection> manualSelectionList =
            new ArrayList<ManualSelection>(  );
    }

    /**
     * This is config for one user's reminder. All times is offset
     * between event and start(or finish) programme in milliseconds. I.e. if
     * we need something to do on 30 seconds before start programme, then
     * time value will be "-30000".
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
        public long popupOpenTime = -300000L;

        /** Time when popup should be closed after programme start. */
        public long popupCloseTime = +300000L;

        /** Play sound. */
        public boolean isSound;

        /** Time when sound should be played before programme start. */
        public long soundPlayTime = -300000L;

        /** File which should be played. */
        public String soundFile;

        /** Execute command. */
        public boolean isExecute;

        /** Time when execute start command programme start. */
        public long executeStartTime = -300000L;

        /** Time when execute stop command after programme end. */
        public long executeStopTimeOnFinishProgramme = +300000L;

        /** Start command. */
        public String executeStartCommand;

        /** Stop command. */
        public String executeStopCommand;
    }
}

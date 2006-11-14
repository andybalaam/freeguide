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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
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
    protected static Logger LOG =
        Logger.getLogger( "org.freeguide-tv.reminder" );
    protected static final String RESOURCES_PREFIX =
        "resources/plugins/reminder/advanced/";
    protected static Map<String, ImageIcon> imagesCache =
        new TreeMap<String, ImageIcon>(  );
    protected static final long MAX_STORED_SELECTION_TIME =
        3L * 24L * 60L * 60L * 1000L;

    /** Config object. */
    protected final Config config = new Config(  );
    protected SchedulerThread thread;

    /**
     * Start plugin.
     */
    public void start(  )
    {
        removeOldSelections(  );
        thread = new SchedulerThread( this );
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
     * Remove selections for too old programmes.
     */
    protected void removeOldSelections(  )
    {
        final long now = System.currentTimeMillis(  );

        for( 
            final Iterator<ManualSelection> it =
                config.manualSelectionList.iterator(  ); it.hasNext(  ); )
        {
            final ManualSelection sel = it.next(  );

            if( sel.programmeTime < ( now - MAX_STORED_SELECTION_TIME ) )
            {
                it.remove(  );
            }
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
        new PopupMenuHandler( this ).fillMenu( menu, programme );
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
     * Reschedule all events.
     */
    public void reschedule(  )
    {
        if( thread != null )
        {
            thread.reschedule(  );
        }
    }

    /**
     * Check if programme is in the even one white list or in
     * favourites.
     *
     * @param programme programme
     *
     * @return true if in guide
     */
    public boolean isInGuide( final TVProgramme programme )
    {
        Set<String> deSelectedReminders = null;

        for( final ManualSelection sel : config.manualSelectionList )
        {
            if( sel.matches( programme ) )
            {
                for( final Boolean value : sel.reminders.values(  ) )
                {
                    if( value.booleanValue(  ) )
                    {
                        return true;
                    }
                }

                deSelectedReminders = sel.reminders.keySet(  );
            }
        }

        for( final Favourite fav : config.favouritesList )
        {
            if( fav.matches( programme ) )
            {
                for( final String reminder : fav.reminders )
                {
                    if( 
                        ( deSelectedReminders == null )
                            || !deSelectedReminders.contains( reminder ) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Switch programme selection for all reminders.
     *
     * @param programme DOCUMENT ME!
     */
    public void switchProgrammeSelection( TVProgramme programme )
    {
        for( 
            final Iterator<ManualSelection> it =
                config.manualSelectionList.iterator(  ); it.hasNext(  ); )
        {
            final ManualSelection sel = it.next(  );

            if( sel.matches( programme ) )
            {
                it.remove(  );

                return;
            }
        }

        final ManualSelection sel = new ManualSelection( programme );

        for( final OneReminderConfig rem : config.reminders )
        {
            if( rem.name != null )
            {
                sel.reminders.put( rem.name, Boolean.TRUE );
            }
        }
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
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Favourite getFavourite( TVProgramme programme )
    {
        for( final Favourite fav : config.favouritesList )
        {
            if( fav.matches( programme ) )
            {
                return fav;
            }
        }

        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public ManualSelection getManualSelection( TVProgramme programme )
    {
        for( final ManualSelection sel : config.manualSelectionList )
        {
            if( sel.matches( programme ) )
            {
                return sel;
            }
        }

        return null;
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
     * Show programme on setup component.
     *
     * @param programme programme
     * @param icons icons list for icons for this programme
     *
     * @return DOCUMENT_ME!
     */
    public Color getProgrammeSettings( 
        TVProgramme programme, final List<ImageIcon> icons )
    {
        Color color = null;

        final Map<String, Boolean> whereSelected =
            new TreeMap<String, Boolean>(  );

        for( final ManualSelection sel : config.manualSelectionList )
        {
            if( sel.matches( programme ) )
            {
                whereSelected.putAll( sel.reminders );
                color = config.selectedColor;

                break;
            }
        }

        for( final Favourite fav : config.favouritesList )
        {
            if( fav.matches( programme ) )
            {
                for( final String reminder : fav.reminders )
                {
                    if( !whereSelected.containsKey( reminder ) )
                    {
                        whereSelected.put( reminder, Boolean.TRUE );
                    }
                }

                if( color == null )
                {
                    color = fav.getSelectedColor(  );
                }
            }
        }

        for( final OneReminderConfig cfg : config.reminders )
        {
            if( 
                whereSelected.containsKey( cfg.name )
                    && whereSelected.get( cfg.name ).booleanValue(  )
                    && ( cfg.iconName != null ) )
            {
                final ImageIcon i = getImage( cfg.iconName );

                if( i != null )
                {
                    icons.add( i );
                }
            }
        }

        return color;
    }

    protected static ImageIcon getImage( final String name )
    {
        synchronized( imagesCache )
        {
            loadImages(  );

            return imagesCache.get( name );
        }
    }

    protected static Set<String> getImagesNames(  )
    {
        synchronized( imagesCache )
        {
            loadImages(  );

            return imagesCache.keySet(  );
        }
    }

    protected static void loadImages(  )
    {
        if( imagesCache.size(  ) == 0 )
        {
            try
            {
                Properties iconsList = new Properties(  );

                iconsList.load( 
                    AdvancedReminder.class.getClassLoader(  )
                                          .getResourceAsStream( 
                        AdvancedReminder.RESOURCES_PREFIX
                        + "iconsList.properties" ) );

                for( String fn : (Set<String>)(Set)iconsList.keySet(  ) )
                {
                    final URL u =
                        AdvancedReminder.class.getClassLoader(  )
                                              .getResource( 
                            RESOURCES_PREFIX + fn );
                    imagesCache.put( fn, new ImageIcon( u ) );
                }
            }
            catch( IOException ex )
            {
                LOG.log( Level.WARNING, "Error read icons", ex );
            }
        }
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

        /** DOCUMENT ME! */
        public Color selectedColor = Color.YELLOW;
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

        /** Name of icon file. */
        public String iconName;
    }
}

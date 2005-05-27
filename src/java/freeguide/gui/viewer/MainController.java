package freeguide.gui.viewer;

import freeguide.FreeGuide;

import freeguide.gui.dialogs.ChannelSetListDialog;
import freeguide.gui.dialogs.FGDialog;
import freeguide.gui.dialogs.FavouritesController;

import freeguide.lib.fgspecific.GrabberController;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.selection.SelectionManager;

import freeguide.lib.general.LookAndFeelManager;
import freeguide.lib.general.PreferencesHelper;
import freeguide.lib.general.Utils;

import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleViewer;
import freeguide.plugins.IStorage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main window of application.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class MainController implements IModuleViewer.Parent
{

    /** DOCUMENT ME! */
    public static final Config config = new Config(  );
    protected static Reminder reminder = new Reminder(  );

    /** DOCUMENT ME! */
    public MainFrame mainFrame;
    protected IModuleViewer viewer;
    protected final Preferences configStore;
    protected GrabberController grab = new GrabberController(  );

    /**
     * Creates a new MainController object.
     *
     * @param configStore DOCUMENT ME!
     */
    public MainController( final Preferences configStore )
    {
        this.configStore = configStore;

        reminder.start(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param viewer DOCUMENT_ME!
     * @param grabberFromWizard DOCUMENT ME!
     */
    public void start( 
        final IModuleViewer viewer, final String grabberFromWizard )
    {
        this.viewer = viewer;

        mainFrame = new MainFrame( FreeGuide.msg );

        mainFrame.setTitle( "FreeGuide " + FreeGuide.version.getDotFormat(  ) );

        new MenuHandler( this );

        mainFrame.getContentPane(  ).add( 
            viewer.getPanel(  ), BorderLayout.CENTER );

        mainFrame.addWindowListener( 
            new java.awt.event.WindowAdapter(  )
            {
                public void windowClosing( java.awt.event.WindowEvent evt )
                {
                    viewer.close(  );

                    saveConfig(  );

                    reminder.isStopped = true;

                    reminder.reSchedule(  );

                }
            } );
        mainFrame.getProgressBar(  ).addMouseListener( 
            new MouseListener(  )
            {
                public void mouseClicked( MouseEvent e )
                {
                    doShowGrabbers(  );
                }

                public void mouseEntered( MouseEvent e )
                {
                }

                public void mouseExited( MouseEvent e )
                {
                }

                public void mousePressed( MouseEvent e )
                {
                }

                public void mouseReleased( MouseEvent e )
                {
                }
            } );

        loadConfig(  );

        if( grabberFromWizard != null )
        {
            config.activeGrabberIDs.add( grabberFromWizard );
        }

        setLookAndFeel(  );

        mainFrame.pack(  );

        mainFrame.setBounds( config.ui.mainWindowPosition );

        viewer.open( this );

        mainFrame.getRootPane(  ).setDefaultButton( 
            viewer.getDefaultButton(  ) );
        mainFrame.setVisible( true );

        reminderReschedule(  );

        FreeGuide.hidePleaseWait(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public static void reminderReschedule(  )
    {
        reminder.reSchedule(  );

    }

    protected void loadConfig(  )
    {

        try
        {
            PreferencesHelper.load( configStore, config );

            SelectionManager.load( configStore.node( "selection" ) );

        }

        catch( Exception ex )
        {
            FreeGuide.log.log( Level.WARNING, "Error loading config", ex );
        }
    }

    protected void saveConfig(  )
    {
        config.ui.mainWindowPosition = mainFrame.getBounds(  );

        try
        {
            PreferencesHelper.save( configStore, config );

            SelectionManager.save( configStore.node( "selection" ) );
        }

        catch( Exception ex )
        {
            FreeGuide.log.log( Level.WARNING, "Error save config", ex );
        }
    }

    private boolean centreDialogAndRun( FGDialog dialog )
    {

        Dimension thisSize = mainFrame.getSize(  );

        Dimension dialogSize = dialog.getSize(  );

        Point thisLocation = mainFrame.getLocation(  );

        dialog.setLocation( 
            thisLocation.x + ( ( thisSize.width - dialogSize.width ) / 2 ),
            thisLocation.y + ( ( thisSize.height - dialogSize.height ) / 2 ) );

        return dialog.showDialog(  );

    }

    /**
     * Set L&F as described in config.
     */
    public void setLookAndFeel(  )
    {

        final String inspectedLFClassName;
        final String currentLAFClassName;

        if( config.ui.LFname == null )
        {
            inspectedLFClassName = UIManager.getSystemLookAndFeelClassName(  );
        }
        else
        {
            inspectedLFClassName =
                LookAndFeelManager.getLookAndFeelClassName( config.ui.LFname );
        }

        final LookAndFeel currentLAF = UIManager.getLookAndFeel(  );

        if( currentLAF != null )
        {
            currentLAFClassName = currentLAF.getClass(  ).getName(  );
        }
        else
        {
            currentLAFClassName = null;
        }

        if( !inspectedLFClassName.equals( currentLAFClassName ) )
        {

            try
            {
                UIManager.setLookAndFeel( inspectedLFClassName );

                SwingUtilities.updateComponentTreeUI( mainFrame );
            }
            catch( Exception ex )
            {
                FreeGuide.log.log( 
                    Level.WARNING, "Error setup L&F to "
                    + inspectedLFClassName, ex );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void doEditChannelsSets(  )
    {

        ChannelSetListDialog dialog =
            new ChannelSetListDialog( 
                mainFrame, getDataStorage(  ).getInfo(  ).allChannels,
                config.channelsSetsList );

        boolean updated = Utils.centreDialogAndRun( mainFrame, dialog );

        if( updated )
        {
            config.channelsSetsList = dialog.getChannelsSets(  );
            viewer.onChannelsSetsChanged(  );
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void doEditFavourites(  )
    {

        FavouritesController favController =
            new FavouritesController( 
                mainFrame, SelectionManager.getFavouritesList(  ),
                getDataStorage(  ).getInfo(  ).allChannels );

        Utils.centreDialog( mainFrame, favController.getListDialog(  ) );
        favController.getListDialog(  ).setVisible( true );

        if( favController.isChanged(  ) )
        {
            SelectionManager.setFavouritesList( 
                favController.getFavourites(  ) );

            saveConfig(  );

            viewer.onFavouritesChanged(  );

            reminderReschedule(  );

        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void doStartGrabbers(  )
    {

        synchronized( grab )
        {

            if( grab.isStarted(  ) )
            {
                grab.showDialog(  );
            }
            else
            {
                new Thread(  )
                    {
                        public void run(  )
                        {
                            FreeGuide.log.finest( "start grabbing" );
                            grab.grab( 
                                getApplicationFrame(  ),
                                mainFrame.getProgressBar(  ) );
                            viewer.onDataChanged(  );
                            reminderReschedule(  );
                            FreeGuide.log.finest( "stop grabbing" );
                        }
                    }.start(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void doShowGrabbers(  )
    {

        synchronized( grab )
        {

            if( grab.isStarted(  ) )
            {
                grab.showDialog(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param exp DOCUMENT_ME!
     */
    public void exportTo( final IModuleExport exp )
    {
        new Thread(  )
            {
                public void run(  )
                {

                    IStorage.Info info = getDataStorage(  ).getInfo(  );

                    try
                    {

                        TVData data =
                            getDataStorage(  ).get( 
                                null, info.minDate, info.maxDate );
                        exp.exportData( data, mainFrame );
                    }
                    catch( Exception ex )
                    {
                        FreeGuide.log.log( 
                            Level.WARNING, "Error export data", ex );
                    }
                }
            }.start(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void doPrint(  )
    {
        viewer.printHTML(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JFrame getApplicationFrame(  )
    {

        return mainFrame;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public List getChannelsSetsList(  )
    {

        return config.channelsSetsList;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IStorage getDataStorage(  )
    {

        return FreeGuide.storage;

    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Config
    {

        /** The list of available channel sets (all are ChannelSet objects) */
        public static final Class channelsSetsList_TYPE = TVChannelsSet.class;

        /** DOCUMENT ME! */
        public static final Class activeGrabberIDs_TYPE = String.class;

        /** DOCUMENT ME! */
        public UI ui = new UI(  );

        /** DOCUMENT ME! */
        public List channelsSetsList = new ArrayList(  );

        /** Is reminder on. */
        public boolean reminderOn = true;

        /** Time in milliseconds. */
        public long reminderGiveUp = 600000L;

        /** Time in milliseconds. */
        public long reminderWarning = 300000L;

        /** DOCUMENT ME! */
        public Set activeGrabberIDs = new TreeSet(  );

        /**
         * DOCUMENT ME!
         *
         * @author $author$
         * @version $Revision$
         */
        public static class UI
        {

            /** DOCUMENT ME! */
            public Rectangle mainWindowPosition;

            /** DOCUMENT ME! */
            public String LFname;

            /**
             * Creates a new UI object.
             */
            public UI(  )
            {

                Dimension screenSize =
                    Toolkit.getDefaultToolkit(  ).getScreenSize(  );

                mainWindowPosition = new Rectangle( 640, 400 );

                mainWindowPosition.setLocation( 
                    ( screenSize.width - mainWindowPosition.width ) / 2,
                    ( screenSize.height - mainWindowPosition.height ) / 2 );
            }
        }
    }
}

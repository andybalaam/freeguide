package freeguide.gui.viewer;

import freeguide.FreeGuide;

import freeguide.gui.dialogs.ChannelSetListDialog;
import freeguide.gui.dialogs.FGDialog;

import freeguide.lib.fgspecific.GrabberController;
import freeguide.lib.fgspecific.PluginsManager;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;

import freeguide.lib.general.LookAndFeelManager;
import freeguide.lib.general.PreferencesHelper;
import freeguide.lib.general.Utils;

import freeguide.plugins.IApplication;
import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleImport;
import freeguide.plugins.IModuleReminder;
import freeguide.plugins.IModuleStorage;
import freeguide.plugins.IModuleViewer;

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
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class MainController implements IApplication
{

    /** DOCUMENT ME! */
    public static final Config config = new Config(  );

    /** DOCUMENT ME! */
    public MainFrame mainFrame;
    protected IModuleViewer viewer;
    protected Preferences configStore;
    protected GrabberController grab = new GrabberController(  );

    /**
     * DOCUMENT_ME!
     *
     * @param configStore DOCUMENT_ME!
     */
    public MainController( final Preferences configStore )
    {
        this.configStore = configStore;
    }

    /**
     * DOCUMENT_ME!
     */
    public void redraw(  )
    {
        viewer.redraw(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void redrawPersonalizedGuide(  )
    {
        viewer.redrawPersonalizedGuide(  );
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

        mainFrame.setTitle( "FreeGuide " + FreeGuide.VERSION.getDotFormat(  ) );

        new MenuHandler( this );

        final IModuleReminder[] reminders = PluginsManager.getReminders(  );

        for( int i = 0; i < reminders.length; i++ )
        {
            reminders[i].addItemsToMenu( mainFrame.getMenuTools(  ) );
            reminders[i].start(  );
        }

        mainFrame.getContentPane(  ).add( 
            viewer.getPanel(  ), BorderLayout.CENTER );

        mainFrame.addWindowListener( 
            new java.awt.event.WindowAdapter(  )
            {
                public void windowClosing( java.awt.event.WindowEvent evt )
                {
                    viewer.close(  );

                    saveConfig(  );

                    final IModuleReminder[] reminders =
                        PluginsManager.getReminders(  );

                    for( int i = 0; i < reminders.length; i++ )
                    {
                        reminders[i].stop(  );
                    }
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

        viewer.open(  );

        mainFrame.getRootPane(  ).setDefaultButton( 
            viewer.getDefaultButton(  ) );
        mainFrame.setVisible( true );

        remindersReschedule(  );

        FreeGuide.hidePleaseWait(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public static void remindersReschedule(  )
    {

        final IModuleReminder[] reminders = PluginsManager.getReminders(  );

        for( int i = 0; i < reminders.length; i++ )
        {
            reminders[i].reschedule(  );
        }
    }

    protected void loadConfig(  )
    {

        try
        {
            PreferencesHelper.load( configStore, config );

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

        Utils.centreDialog( mainFrame, dialog );

        boolean updated = dialog.showDialog(  );

        if( updated )
        {
            config.channelsSetsList = dialog.getChannelsSets(  );
            viewer.onChannelsSetsChanged(  );
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
                            remindersReschedule(  );
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

                    IModuleStorage.Info info = getDataStorage(  ).getInfo(  );

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
     *
     * @param imp DOCUMENT_ME!
     */
    public void importFrom( final IModuleImport imp )
    {
        new Thread(  )
            {
                public void run(  )
                {

                    try
                    {

                        TVData data = imp.importData( mainFrame );
                        getDataStorage(  ).add( data );
                        viewer.onDataChanged(  );
                    }
                    catch( Exception ex )
                    {
                        FreeGuide.log.log( 
                            Level.WARNING, "Error import data", ex );
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
    public IModuleStorage getDataStorage(  )
    {

        return FreeGuide.storage;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isUnix(  )
    {

        // TODO Auto-generated method stub
        return FreeGuide.runtimeInfo.isUnix;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public TimeZone getTimeZone(  )
    {

        return FreeGuide.getTimeZone(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getBrowserCommand(  )
    {

        return FreeGuide.config.browserCommand;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getInstallDirectory(  )
    {

        return FreeGuide.runtimeInfo.installDirectory;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Logger getLogger(  )
    {

        // TODO Auto-generated method stub
        return FreeGuide.log;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleReminder[] getReminders(  )
    {

        // TODO Auto-generated method stub
        return PluginsManager.getReminders(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getLocalizedMessage( String key )
    {

        // TODO Auto-generated method stub
        return FreeGuide.msg.getLocalizedMessage( key );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param params DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getLocalizedMessage( String key, Object[] params )
    {

        // TODO Auto-generated method stub
        return FreeGuide.msg.getLocalizedMessage( key, params );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getWorkingDirectory(  )
    {

        // TODO Auto-generated method stub
        return FreeGuide.config.workingDirectory;
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

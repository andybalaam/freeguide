package freeguide.plugins.program.freeguide.viewer;

import freeguide.common.gui.JWaitFrame;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.LanguageHelper;

import freeguide.common.plugininterfaces.IApplication;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.util.Locale;

import javax.swing.*;

/**
 * Main application frame.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class MainFrame extends JWaitFrame
{
    private javax.swing.JPanel jContentPane;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemAbout;
    private JMenuItem menuItemUserGuide;
    private JMenuItem menuItemOptions;
    private JMenu menuView;
    private JMenu menuTools;
    private JMenuItem menuItemDownload;
    private JMenuItem menuItemPrint;
    private JMenu menuItemExport;
    private JMenuItem menuItemWizard;
    private JMenuItem menuItemChannelsSets;
    private JPanel jPanel;
    private JProgressBar progressBar;
    private JMenuItem menuItemUpdater;
    private JMenuItem menuItemImport = null;
    private final IApplication.IMainMenu menuForExport =
        new IApplication.IMainMenu(  )
        {
            public JMenu getTools(  )
            {
                return getMenuTools(  );
            }
        };

/**
     * This is the default constructor
     */
    public MainFrame(  )
    {
        super(  );

        try
        {
            byte[] data =
                LanguageHelper.loadResourceAsByteArray( 
                    "resources/plugins/program/freeguide/images/logo-16x16.png" );

            if( data != null )
            {
                Image icon = ( new ImageIcon( data, "icon" ) ).getImage(  );
                setIconImage( icon );
            }
        }
        catch( IOException ex )
        {
        }

        initialize(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IApplication.IMainMenu getMainMenuForExport(  )
    {
        return menuForExport;
    }

    protected String getLocalizedString( final String key )
    {
        return Application.getInstance(  ).getLocalizedMessage( key );

    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {
        this.setDefaultCloseOperation( javax.swing.JFrame.EXIT_ON_CLOSE );
        this.setJMenuBar( getMainMenu(  ) );

        this.setSize( 300, 200 );

        this.setContentPane( getJContentPane(  ) );

    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane(  )
    {
        if( jContentPane == null )
        {
            jContentPane = new javax.swing.JPanel(  );

            jContentPane.setLayout( new java.awt.BorderLayout(  ) );

            jContentPane.add( getJPanel(  ), java.awt.BorderLayout.SOUTH );
        }

        return jContentPane;

    }

    /**
     * This method initializes jJMenuBar
     *
     * @return javax.swing.JMenuBar
     */
    public javax.swing.JMenuBar getMainMenu(  )
    {
        if( mainMenu == null )
        {
            mainMenu = new javax.swing.JMenuBar(  );

            mainMenu.add( getMenuFile(  ) );

            addMenuView(  );

            mainMenu.add( getMenuTools(  ) );

            mainMenu.add( getMenuHelp(  ) );

        }

        return mainMenu;

    }

    /**
     * This method initializes jMenu
     *
     * @return javax.swing.JMenu
     */
    public javax.swing.JMenu getMenuFile(  )
    {
        if( menuFile == null )
        {
            menuFile = new javax.swing.JMenu(  );

            menuFile.setText( getLocalizedString( "MainFrame.Menu.File" ) );

            menuFile.setMnemonic( KeyEvent.VK_F );

            menuFile.add( getMenuItemDownload(  ) );

            menuFile.add( getMenuItemPrint(  ) );

            menuFile.add( getMenuItemImport(  ) );
            menuFile.add( getMenuItemExport(  ) );

            menuFile.add( new JSeparator(  ) );

            menuFile.add( getMenuItemExit(  ) );

        }

        return menuFile;

    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemOptions(  )
    {
        if( menuItemOptions == null )
        {
            menuItemOptions = new JMenuItem(  );

            menuItemOptions.setText( 
                getLocalizedString( "MainFrame.Menu.Options" ) );

            menuItemOptions.setMnemonic( KeyEvent.VK_O );

            menuItemOptions.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK ) );

        }

        return menuItemOptions;

    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public javax.swing.JMenuItem getMenuItemExit(  )
    {
        if( menuItemExit == null )
        {
            menuItemExit = new javax.swing.JMenuItem(  );

            menuItemExit.setText( getLocalizedString( "MainFrame.Menu.Exit" ) );

            menuItemExit.setMnemonic( KeyEvent.VK_Q );

            menuItemExit.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_Q, InputEvent.CTRL_MASK ) );

        }

        return menuItemExit;

    }

    /**
     * This method initializes jMenu
     *
     * @return javax.swing.JMenu
     */
    public javax.swing.JMenu getMenuHelp(  )
    {
        if( menuHelp == null )
        {
            menuHelp = new javax.swing.JMenu(  );

            menuHelp.setText( getLocalizedString( "MainFrame.Menu.Help" ) );

            menuHelp.setMnemonic( KeyEvent.VK_H );

            menuHelp.add( getMenuItemUserGuide(  ) );

            menuHelp.add( new JSeparator(  ) );

            menuHelp.add( getMenuItemAbout(  ) );

        }

        return menuHelp;

    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemAbout(  )
    {
        if( menuItemAbout == null )
        {
            menuItemAbout = new JMenuItem(  );

            menuItemAbout.setText( 
                getLocalizedString( "MainFrame.Menu.About" ) );

            menuItemAbout.setMnemonic( KeyEvent.VK_A );

            menuItemAbout.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_A, InputEvent.CTRL_MASK ) );

        }

        return menuItemAbout;

    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemUserGuide(  )
    {
        if( menuItemUserGuide == null )
        {
            menuItemUserGuide = new JMenuItem(  );

            menuItemUserGuide.setText( 
                getLocalizedString( "MainFrame.Menu.UserGuide" ) );

            menuItemUserGuide.setMnemonic( KeyEvent.VK_U );

        }

        return menuItemUserGuide;

    }

    /**
     * This method initializes the View menu. It automatically creates
     * the submenu with all available viewers. I had to make an
     * "addMenuView", because it's determined here if we need this menu. If
     * we don't need the menu, we can't return it, and adding a NULL to a
     * component isn't good
     *
     * @return javax.swing.JMenu
     */
    public JMenu addMenuView(  )
    {
        if( menuView == null )
        {
            menuView = new JMenu(  );
            menuView.setText( getLocalizedString( "MainFrame.Menu.View" ) );
            menuView.setMnemonic( KeyEvent.VK_V );
            class JDataRadioButtonMenuItem extends JRadioButtonMenuItem
            {
                /** DOCUMENT ME! */
                public String data = null;

/**
                 * Creates a new JDataRadioButtonMenuItem object.
                 *
                 * @param string DOCUMENT ME!
                 */
                public JDataRadioButtonMenuItem( String string )
                {
                    super( string );
                }
            }

            PluginInfo[] viewers = PluginsManager.getViewers(  );

            if( viewers.length <= 1 )
            {
                //Don't show viewers menu if there is only one
                return null;
            }

            ButtonGroup group = new ButtonGroup(  );

            for( int i = 0; i < viewers.length; i++ )
            {
                PluginInfo viewer = viewers[i];
                JDataRadioButtonMenuItem item =
                    new JDataRadioButtonMenuItem( viewer.getName(  ) );
                item.data = viewer.getID(  );
                item.addActionListener( 
                    new ActionListener(  )
                    {
                        public void actionPerformed( ActionEvent actionEvent )
                        {
                            ( (MainController)Application.getInstance(  ) )
                            .setViewer( 
                                ( (JDataRadioButtonMenuItem)actionEvent
                                .getSource(  ) ).data );
                        }
                    } );

                if( 
                    viewer.getID(  )
                              .equals( 
                            ( (MainController.Config)( (MainController)Application
                            .getInstance(  ) ).getConfig(  ) ).viewerId ) )
                {
                    item.setSelected( true );
                }

                group.add( item );
                menuView.add( item );
            }

            mainMenu.add( menuView );
        }

        return menuView;
    }

    /**
     * This method initializes jMenu
     *
     * @return javax.swing.JMenu
     */
    public JMenu getMenuTools(  )
    {
        if( menuTools == null )
        {
            menuTools = new JMenu(  );
            menuTools.setText( getLocalizedString( "MainFrame.Menu.Tools" ) );
            menuTools.setMnemonic( KeyEvent.VK_T );

            // Will be added here:  // Favourites
            menuTools.add( getMenuItemChannelsSets(  ) ); // Channel Sets
            menuTools.add( new JSeparator(  ) );
            menuTools.add( getMenuItemWizard(  ) ); // First time wiz

            // menuTools.add( getMenuItemUpdater(  ) ); // Plugins man
            menuTools.add( new JSeparator(  ) );
            menuTools.add( getMenuItemOptions(  ) ); // Options
            menuTools.add( new JSeparator(  ) );
        }

        return menuTools;

    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemDownload(  )
    {
        if( menuItemDownload == null )
        {
            menuItemDownload = new JMenuItem(  );

            menuItemDownload.setText( 
                getLocalizedString( "MainFrame.Menu.Download" ) );

            menuItemDownload.setMnemonic( KeyEvent.VK_D );

            menuItemDownload.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_D, InputEvent.CTRL_MASK ) );

            //	                    mbtDownloadActionPerformed( evt );
        }

        return menuItemDownload;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JMenuItem getMenuItemExport(  )
    {
        if( menuItemExport == null )
        {
            menuItemExport = new JMenu(  );
            menuItemExport.setText( 
                getLocalizedString( "MainFrame.Menu.Export" ) );

        }

        return menuItemExport;
    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemPrint(  )
    {
        if( menuItemPrint == null )
        {
            menuItemPrint = new JMenuItem(  );

            menuItemPrint.setText( 
                getLocalizedString( "MainFrame.Menu.Print" ) );

            menuItemPrint.setMnemonic( KeyEvent.VK_P );

            menuItemPrint.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_MASK ) );

            //	                    mbtPrintActionPerformed( evt );
        }

        return menuItemPrint;

    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemWizard(  )
    {
        if( menuItemWizard == null )
        {
            menuItemWizard = new JMenuItem(  );

            menuItemWizard.setText( 
                getLocalizedString( "MainFrame.Menu.Wizard" ) );
        }

        return menuItemWizard;

    }

    /**
     * This method initializes jMenuItem2
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemChannelsSets(  )
    {
        if( menuItemChannelsSets == null )
        {
            menuItemChannelsSets = new JMenuItem(  );

            menuItemChannelsSets.setText( 
                getLocalizedString( "MainFrame.Menu.ChannelsSets" ) );

            menuItemChannelsSets.setMnemonic( KeyEvent.VK_H );

            menuItemChannelsSets.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_H, InputEvent.CTRL_MASK ) );

        }

        return menuItemChannelsSets;

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel(  )
    {
        if( jPanel == null )
        {
            jPanel = new JPanel(  );
            jPanel.setLayout( new BorderLayout(  ) );
            jPanel.add( getProgressBar(  ), java.awt.BorderLayout.EAST );
        }

        return jPanel;
    }

    /**
     * This method initializes jProgressBar
     *
     * @return javax.swing.JProgressBar
     */
    public JProgressBar getProgressBar(  )
    {
        if( progressBar == null )
        {
            progressBar = new JProgressBar(  );
            progressBar.setVisible( false );
        }

        return progressBar;
    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemUpdater(  )
    {
        if( menuItemUpdater == null )
        {
            menuItemUpdater = new JMenuItem(  );
            menuItemUpdater.setText( 
                getLocalizedString( "MainFrame.Menu.Updater" ) );

            menuItemUpdater.setMnemonic( KeyEvent.VK_U );

            menuItemUpdater.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_U, InputEvent.CTRL_MASK ) );
        }

        return menuItemUpdater;
    }

    /**
     * This method initializes jMenuItem
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemImport(  )
    {
        if( menuItemImport == null )
        {
            menuItemImport = new JMenu(  );
            menuItemImport.setText( 
                getLocalizedString( "MainFrame.Menu.Import" ) );
        }

        return menuItemImport;
    }
}

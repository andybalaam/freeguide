package freeguide.gui.viewer;

import freeguide.lib.general.LanguageHelper;

import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

/**
 * Main application frame.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class MainFrame extends JFrame
{

    private javax.swing.JPanel jContentPane = null;
    private javax.swing.JMenuBar mainMenu = null;
    private javax.swing.JMenu menuFile = null;
    private javax.swing.JMenu menuHelp = null;
    private javax.swing.JMenuItem menuItemExit = null;
    private javax.swing.JMenuItem menuItemAbout = null;
    final protected LanguageHelper i18n;
    private JMenuItem menuItemUserGuide = null;
    private JMenuItem menuItemOptions = null;
    private JMenu menuTools = null;
    private JMenuItem menuItemDownload = null;
    private JMenuItem menuItemPrint = null;
    private JMenu menuItemExport = null;
    private JMenuItem menuItemWizard = null;
    private JMenuItem menuItemFavourites = null;
    private JMenuItem menuItemChannelsSets = null;

    /**
     * This is the default constructor
     *
     * @param i18n DOCUMENT ME!
     */
    public MainFrame( final LanguageHelper i18n )
    {
        super(  );

        URL imgURL = getClass(  ).getResource( "/logo-16x16.png" );
        Image icon =
            ( new javax.swing.ImageIcon( imgURL, "icon" ) ).getImage(  );
        setIconImage( icon );

        this.i18n = i18n;

        initialize(  );

    }

    protected String getLocalizedString( final String key )
    {

        return i18n.getString( key );

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

            menuTools.add( getMenuItemFavourites(  ) );

            menuTools.add( getMenuItemChannelsSets(  ) );

            menuTools.add( new JSeparator(  ) );

            menuTools.add( getMenuItemWizard(  ) );

            menuTools.add( getMenuItemOptions(  ) );

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

            menuItemWizard.setMnemonic( KeyEvent.VK_F );

            //	                    mbtFirstTimeActionPerformed( evt );
        }

        return menuItemWizard;

    }

    /**
     * This method initializes jMenuItem1
     *
     * @return javax.swing.JMenuItem
     */
    public JMenuItem getMenuItemFavourites(  )
    {

        if( menuItemFavourites == null )
        {
            menuItemFavourites = new JMenuItem(  );

            menuItemFavourites.setText( 
                getLocalizedString( "MainFrame.Menu.Favourites" ) );

            menuItemFavourites.setMnemonic( KeyEvent.VK_F );

            menuItemFavourites.setAccelerator( 
                KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK ) );

        }

        return menuItemFavourites;

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
}

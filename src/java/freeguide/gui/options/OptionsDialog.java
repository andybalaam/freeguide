/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.gui.options;

import freeguide.FreeGuide;

import freeguide.gui.dialogs.FGDialog;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.PluginInfo;
import freeguide.lib.fgspecific.PluginsManager;

import freeguide.lib.general.GridBagEasy;
import freeguide.lib.general.Utils;

import freeguide.plugins.IModule;
import freeguide.plugins.IModuleConfigurationUI;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * FreeGuide's options screen.
 *
 * @author Andy Balaam
 * @version 1
 */
public class OptionsDialog extends FGDialog implements TreeSelectionListener,
    ActionListener
{

    // -----------------------------------------
    private JTree menuTree;
    private OptionPanel currentOptionPanel;
    private JPanel optionsPane;
    private CardLayout optionsPaneLayout;
    private JButton okButton;
    private JButton cancelButton;
    private JButton defaultButton;
    protected List modulesConf = new ArrayList(  );
    protected List modulesParents = new ArrayList(  );
    JSplitPane splitPane;

    /**
     * Launch this screen as a normal options screen with everything on it.
     *
     * @param owner DOCUMENT ME!
     */
    public OptionsDialog( JFrame owner )
    {
        super( 
            owner, Application.getInstance(  ).getLocalizedMessage( "options" ) );

        // Draw the screen
        buildGUI(  );
        Utils.centreDialog( owner, this );
    }

    /**
     * Build the basic GUI of the options screen
     */
    private void buildGUI(  )
    {
        optionsPaneLayout = new CardLayout(  );
        optionsPane = new JPanel( optionsPaneLayout );
        optionsPane.add( new JPanel(  ), "" );

        // Make the standard objects
        DefaultMutableTreeNode defaultLeaf = buildMenuTree(  );

        menuTree.getAccessibleContext(  ).setAccessibleName( 
            Application.getInstance(  ).getLocalizedMessage( 
                "options_menu_tree" ) );

        JScrollPane menuScrollPane = new JScrollPane( menuTree );

        splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );

        defaultButton =
            newStandardJButton( 
                Application.getInstance(  ).getLocalizedMessage( "default" ) );

        defaultButton.setMnemonic( KeyEvent.VK_D );

        cancelButton =
            newStandardJButton( 
                Application.getInstance(  ).getLocalizedMessage( "cancel" ) );

        cancelButton.setMnemonic( KeyEvent.VK_C );

        okButton =
            newStandardJButton( 
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );

        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( getContentPane(  ) );

        gbe.addFWXWYGW( splitPane, 0, 0, gbe.FILL_BOTH, 1, 1, 3 );

        gbe.addAWXWY( defaultButton, 0, 1, gbe.ANCH_WEST, 1, 0 );

        gbe.addWXWY( okButton, 1, 1, 0, 0 );

        gbe.addWXWY( cancelButton, 2, 1, 0, 0 );

        splitPane.setLeftComponent( menuScrollPane );

        splitPane.setRightComponent( optionsPane );

        // Make the menu tree
        setTreeNode( defaultLeaf );

        splitPane.setDividerLocation( 150 );

        // Events
        defaultButton.addActionListener( this );

        cancelButton.addActionListener( this );

        okButton.addActionListener( this );

        // Set dialog-wide stuff
        getRootPane(  ).setDefaultButton( okButton );

        optionsPaneLayout.first( optionsPane );

        pack(  );

        //        setSize( new Dimension( 500, 350 ) );
    }

    /**
     * This is the section which must be customised to change the panels
     * available on the options screen.
     *
     * @return DOCUMENT_ME!
     */
    private DefaultMutableTreeNode buildMenuTree(  )
    {

        IModuleConfigurationUI ui;

        DefaultMutableTreeNode trunk =
            new DefaultMutableTreeNode( 
                Application.getInstance(  ).getLocalizedMessage( "options" ) );

        ui = new PanelGeneralController(  );

        DefaultMutableTreeNode generalLeaf =
            new DefaultMutableTreeNode( 
                new ModuleNode( 
                    ui,
                    Application.getInstance(  ).getLocalizedMessage( 
                        "general" ) ) );
        optionsPane.add( 
            ui.getPanel(  ),
            Application.getInstance(  ).getLocalizedMessage( "general" ) );
        modulesConf.add( ui );

        trunk.add( generalLeaf );

        IModule hv = PluginsManager.getModuleByID( FreeGuide.VIEWER_ID );
        ui = hv.getConfigurationUI( this );

        DefaultMutableTreeNode horzViewer =
            new DefaultMutableTreeNode( 
                new ModuleNode( 
                    ui,
                    Application.getInstance(  ).getLocalizedMessage( 
                        "Options.Tree.HorizontalViewer" ) ) );

        optionsPane.add( 
            ui.getPanel(  ),
            Application.getInstance(  ).getLocalizedMessage( 
                "Options.Tree.HorizontalViewer" ) );
        modulesConf.add( ui );
        trunk.add( horzViewer );

        OptionPanel panel = new BrowserOptionPanel( this );
        DefaultMutableTreeNode browserLeaf =
            new DefaultMutableTreeNode( panel );
        panel.construct(  );
        optionsPane.add( panel, panel.toString(  ) );

        trunk.add( browserLeaf );

        panel = new PrivacyOptionPanel( this );

        DefaultMutableTreeNode privateLeaf =
            new DefaultMutableTreeNode( panel );
        panel.construct(  );
        optionsPane.add( panel, panel.toString(  ) );

        trunk.add( privateLeaf );

        DefaultMutableTreeNode advancedBranch =
            new DefaultMutableTreeNode( 
                Application.getInstance(  ).getLocalizedMessage( "advanced" ) );

        //optionsPane.add(panel, panel.toString());
        trunk.add( advancedBranch );

        /*TreePath pathViewers =
        addBranchWithModules(
        trunk, FreeGuide.msg.getString( "OptionsDialog.Tree.Viewers" ),
        PluginsManager.getViewers(  ) );*/
        panel = new GrabbersOptionPanel( this );
        panel.construct(  );
        optionsPane.add( panel, panel.toString(  ) );

        addBranchWithModules( 
            advancedBranch, panel, PluginsManager.getGrabbers(  ) );

        addBranchWithModules( 
            advancedBranch,
            Application.getInstance(  ).getLocalizedMessage( 
                "OptionsDialog.Tree.Importers" ),
            PluginsManager.getImporters(  ) );
        addBranchWithModules( 
            advancedBranch,
            Application.getInstance(  ).getLocalizedMessage( 
                "OptionsDialog.Tree.Exporters" ),
            PluginsManager.getExporters(  ) );

        addBranchWithModules( 
            advancedBranch,
            Application.getInstance(  ).getLocalizedMessage( 
                "OptionsDialog.Tree.Reminders" ),
            PluginsManager.getReminders(  ) );

        menuTree = new JTree( trunk );

        menuTree.getSelectionModel(  ).setSelectionMode( 
            TreeSelectionModel.SINGLE_TREE_SELECTION );

        menuTree.addTreeSelectionListener( this );

        //        menuTree.expandPath( pathGrabbers );
        //        menuTree.expandPath( pathExporters );
        //menuTree.setRootVisible( false );

        /*for( int i=0; i<menuTree.getRowCount(); i++ ) {


        menuTree.expandRow(i);


        }*/
        return generalLeaf;

    }

    protected TreePath addBranchWithModules( 
        DefaultMutableTreeNode parent, Object obj, PluginInfo[] plugins )
    {

        DefaultMutableTreeNode branch = new DefaultMutableTreeNode( obj );

        for( int i = 0; i < plugins.length; i++ )
        {

            IModuleConfigurationUI confUI =
                plugins[i].getInstance(  ).getConfigurationUI( this );

            if( confUI != null )
            {
                modulesConf.add( confUI );

                DefaultMutableTreeNode modBranch =
                    new DefaultMutableTreeNode( 
                        new ModuleNode( 
                            confUI, plugins[i].getName( Locale.getDefault(  ) ) ) );

                optionsPane.add( 
                    confUI.getPanel(  ),
                    plugins[i].getName( Locale.getDefault(  ) ) );
                branch.add( modBranch );

            }
        }

        if( branch.getChildCount(  ) > 0 )
        {
            parent.add( branch );
        }

        return new TreePath( branch.getPath(  ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param e DOCUMENT_ME!
     */
    public void valueChanged( TreeSelectionEvent e )
    {

        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)menuTree.getLastSelectedPathComponent(  );

        setTreeNode( node );

    }

    private void setTreeNode( DefaultMutableTreeNode node )
    {

        if( node == null )
        {

            return;

        }

        Object userObject = node.getUserObject(  );

        if( userObject instanceof OptionPanel )
        {

            OptionPanel newOptionPanel = (OptionPanel)userObject;

            replaceOptionPanel( newOptionPanel );

        }

        else if( userObject instanceof ModuleNode )
        {
            replaceOptionPanel( (ModuleNode)userObject );

        }

        else
        {
            clearOptionPanel(  );

        }
    }

    private void replaceOptionPanel( OptionPanel newOptionPanel )
    {
        optionsPaneLayout.show( optionsPane, newOptionPanel.toString(  ) );
    }

    private void replaceOptionPanel( ModuleNode moduleNode )
    {
        optionsPaneLayout.show( optionsPane, moduleNode.toString(  ) );
    }

    private void clearOptionPanel(  )
    {
        optionsPaneLayout.first( optionsPane );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param e DOCUMENT_ME!
     */
    public void actionPerformed( ActionEvent e )
    {

        Object source = e.getSource(  );

        if( source == defaultButton )
        {
            currentOptionPanel.resetToDefaults(  );

        }

        else if( source == cancelButton )
        {
            quit(  );

        }

        else if( source == okButton )
        {
            saveAll(  );

            quit(  );

        }
    }

    private void saveAll(  )
    {

        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)( menuTree.getModel(  ).getRoot(  ) );

        for( 
            Enumeration e = node.depthFirstEnumeration(  );
                e.hasMoreElements(  );
                node = (DefaultMutableTreeNode)e.nextElement(  ) )
        {

            Object userObject = node.getUserObject(  );

            if( userObject instanceof OptionPanel )
            {

                if( ( (OptionPanel)userObject ).save(  ) )
                {
                    setChanged(  );

                    setSave(  );

                }
            }
        }

        for( int i = 0; i < modulesConf.size(  ); i++ )
        {

            IModuleConfigurationUI m =
                (IModuleConfigurationUI)modulesConf.get( i );

            m.save(  );

        }

        PluginsManager.saveAllConfigs(  );

        setChanged(  );
        setSave(  );
    }

    protected JButton newStandardJButton( String text )
    {

        JButton ans = new JButton( text );

        Dimension size = new Dimension( 100, 24 );

        ans.setMaximumSize( size );

        ans.setMinimumSize( size );

        ans.setPreferredSize( size );

        return ans;

    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class ModuleNode
    {

        IModuleConfigurationUI confUI;
        String title;

        /**
         * Creates a new ModuleNode object.
         *
         * @param confUI DOCUMENT ME!
         * @param title DOCUMENT ME!
         */
        public ModuleNode( IModuleConfigurationUI confUI, final String title )
        {
            this.confUI = confUI;
            this.title = title;
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String toString(  )
        {

            return title;

        }
    }
}

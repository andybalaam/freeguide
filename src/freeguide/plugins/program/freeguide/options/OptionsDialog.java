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
package freeguide.plugins.program.freeguide.options;

import freeguide.common.gui.FGDialog;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.GridBagEasy;
import freeguide.common.lib.general.Utils;

import freeguide.common.plugininterfaces.IModule;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
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
    private JButton okButton;
    private JButton cancelButton;
    private JButton defaultButton;
    protected JSplitPane splitPane;

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
        // Make the standard objects
        DefaultMutableTreeNode defaultLeaf = buildMenuTree(  );

        menuTree.getAccessibleContext(  )
                .setAccessibleName( 
            Application.getInstance(  ).getLocalizedMessage( 
                "options_menu_tree" ) );

        JScrollPane menuScrollPane = new JScrollPane( menuTree );

        splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );

        defaultButton = newStandardJButton( 
                Application.getInstance(  ).getLocalizedMessage( "default" ) );

        defaultButton.setMnemonic( KeyEvent.VK_D );

        cancelButton = newStandardJButton( 
                Application.getInstance(  ).getLocalizedMessage( "cancel" ) );

        cancelButton.setMnemonic( KeyEvent.VK_C );

        okButton = newStandardJButton( 
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );

        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( getContentPane(  ) );

        gbe.addFWXWYGW( splitPane, 0, 0, GridBagEasy.FILL_BOTH, 1, 1, 3 );

        gbe.addAWXWY( defaultButton, 0, 1, GridBagEasy.ANCH_WEST, 1, 0 );

        gbe.addWXWY( okButton, 1, 1, 0, 0 );

        gbe.addWXWY( cancelButton, 2, 1, 0, 0 );

        splitPane.setLeftComponent( menuScrollPane );

        // Make the menu tree
        setTreeNode( defaultLeaf );

        splitPane.setDividerLocation( 150 );

        // Events
        defaultButton.addActionListener( this );

        cancelButton.addActionListener( this );

        okButton.addActionListener( this );

        // Set dialog-wide stuff
        getRootPane(  ).setDefaultButton( okButton );

        pack(  );

        setSize( new Dimension( 600, 440 ) );
    }

    /**
     * This is the section which must be customised to change the
     * panels available on the options screen.
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

        trunk.add( generalLeaf );

        IModule hv = PluginsManager.getModuleByID( FreeGuide.VIEWER_ID );
        ui = hv.getConfigurationUI( this );

        DefaultMutableTreeNode horzViewer =
            new DefaultMutableTreeNode( 
                new ModuleNode( 
                    ui,
                    Application.getInstance(  )
                               .getLocalizedMessage( 
                        "Options.Tree.HorizontalViewer" ) ) );

        trunk.add( horzViewer );

        OptionPanel panel = new BrowserOptionPanel( this );
        DefaultMutableTreeNode browserLeaf =
            new DefaultMutableTreeNode( panel );
        panel.construct(  );

        trunk.add( browserLeaf );

        panel = new PrivacyOptionPanel( this );

        DefaultMutableTreeNode privateLeaf =
            new DefaultMutableTreeNode( panel );
        panel.construct(  );

        trunk.add( privateLeaf );

        DefaultMutableTreeNode advancedBranch =
            new DefaultMutableTreeNode( 
                Application.getInstance(  ).getLocalizedMessage( "advanced" ) );

        //optionsPane.add(panel, panel.toString());
        trunk.add( advancedBranch );

        panel = new GrabbersOptionPanel( this );
        panel.construct(  );

        addBranchWithModules( 
            advancedBranch, panel, PluginsManager.getGrabbers(  ) );

        addBranchWithModules( 
            advancedBranch,
            Application.getInstance(  )
                       .getLocalizedMessage( "OptionsDialog.Tree.Importers" ),
            PluginsManager.getImporters(  ) );
        addBranchWithModules( 
            advancedBranch,
            Application.getInstance(  )
                       .getLocalizedMessage( "OptionsDialog.Tree.Exporters" ),
            PluginsManager.getExporters(  ) );

        final PluginInfo reminderPluginInfo = PluginsManager.getReminder(  );

        if( reminderPluginInfo != null )
        {
            addBranchWithModules( 
                advancedBranch,
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "OptionsDialog.Tree.Reminders" ),
                new PluginInfo[] { reminderPluginInfo } );
        }

        menuTree = new JTree( trunk );

        menuTree.getSelectionModel(  )
                .setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

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
                final String[] nodes = confUI.getTreeNodes(  );

                if( nodes != null )
                {
                    for( String node : nodes )
                    {
                        final DefaultMutableTreeNode modBranch =
                            new DefaultMutableTreeNode( 
                                new ModuleNode( confUI, node ) );
                        branch.add( modBranch );
                    }
                }
                else
                {
                    final DefaultMutableTreeNode modBranch =
                        new DefaultMutableTreeNode( 
                            new ModuleNode( confUI, plugins[i].getName(  ) ) );
                    branch.add( modBranch );
                }
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
            replaceOptionPanel( (ModuleNode)userObject, node );
        }
        else
        {
            clearOptionPanel(  );
        }
    }

    private void replaceOptionPanel( OptionPanel newOptionPanel )
    {
        showPanel( newOptionPanel );
    }

    private void replaceOptionPanel( 
        ModuleNode moduleNode, DefaultMutableTreeNode node )
    {
        showPanel( 
            moduleNode.confUI.getPanel( moduleNode.title, node, menuTree ) );
    }

    private void clearOptionPanel(  )
    {
        showPanel( new JPanel(  ) );
    }

    protected void showPanel( final Component comp )
    {
        int pos = splitPane.getDividerLocation(  );
        splitPane.setRightComponent( comp );
        splitPane.setDividerLocation( pos );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param e DOCUMENT_ME!
     */
    public void actionPerformed( ActionEvent e )
    {
        Object source = e.getSource(  );

        if( ( source == defaultButton ) && ( currentOptionPanel != null ) )
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
            else if( userObject instanceof ModuleNode )
            {
                IModuleConfigurationUI m = ( (ModuleNode)userObject ).confUI;
                m.save(  );
            }
        }

        //PluginsManager.saveAllConfigs(  );
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
     * Node which has title and IModuleConfigurationUI link.
     */
    public static class ModuleNode
    {
        protected final IModuleConfigurationUI confUI;
        protected final String title;

/**
         * Creates a new ModuleNode object.
         *
         * @param confUI configuration interface
         * @param title node title
         */
        public ModuleNode( IModuleConfigurationUI confUI, final String title )
        {
            this.confUI = confUI;
            this.title = title;
        }

        /**
         * Show title.
         *
         * @return title
         */
        public String toString(  )
        {
            return title;
        }
    }
}

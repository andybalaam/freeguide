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

import freeguide.lib.fgspecific.PluginsManager;

import freeguide.lib.general.GridBagEasy;

import freeguide.plugins.IModule;
import freeguide.plugins.IModuleConfigurationUI;

import freeguide.plugins.ui.horizontal.HorizontalViewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
    private Container contentPane;
    private OptionPanel optionPanel;
    private JPanel optionsPane;
    private JButton okButton;
    private JButton cancelButton;
    private JButton defaultButton;
    List modulesConf = new ArrayList(  );
    JSplitPane splitPane;

    /**
     * Launch this screen as a normal options screen with everything on it.
     *
     * @param owner DOCUMENT ME!
     */
    public OptionsDialog( JFrame owner )
    {
        super( owner, FreeGuide.msg.getString( "options" ) );

        // Draw the screen
        buildGUI(  );

    }

    /**
     * Build the basic GUI of the options screen
     */
    private void buildGUI(  )
    {

        // Make the standard objects
        DefaultMutableTreeNode defaultLeaf = buildMenuTree(  );

        menuTree.getAccessibleContext(  ).setAccessibleName( 
            FreeGuide.msg.getString( "options_menu_tree" ) );

        JScrollPane menuScrollPane = new JScrollPane( menuTree );

        optionsPane = new JPanel( new BorderLayout(  ) );

        splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );

        defaultButton =
            newStandardJButton( FreeGuide.msg.getString( "default" ) );

        defaultButton.setMnemonic( KeyEvent.VK_D );

        cancelButton =
            newStandardJButton( FreeGuide.msg.getString( "cancel" ) );

        cancelButton.setMnemonic( KeyEvent.VK_C );

        okButton = newStandardJButton( FreeGuide.msg.getString( "ok" ) );

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

        pack(  );

        Dimension screenSize = Toolkit.getDefaultToolkit(  ).getScreenSize(  );

        Dimension dialogSize = new Dimension( 500, 400 );

        setSize( dialogSize );

        setLocation( 
            ( screenSize.width - dialogSize.width ) / 2,
            ( screenSize.height - dialogSize.height ) / 2 );

        // To Be Added Shortly (Rob)
        //        GuiUtils.centerDialog( this, 500, 400 );
    }

    /**
     * This is the section which must be customised to change the panels
     * available on the options screen.
     *
     * @return DOCUMENT_ME!
     */
    private DefaultMutableTreeNode buildMenuTree(  )
    {

        DefaultMutableTreeNode trunk =
            new DefaultMutableTreeNode( FreeGuide.msg.getString( "options" ) );

        DefaultMutableTreeNode generalLeaf =
            new DefaultMutableTreeNode( new GeneralOptionPanel( this ) );

        trunk.add( generalLeaf );

        IModule hv = PluginsManager.getViewerByID( HorizontalViewer.ID );
        IModuleConfigurationUI ui = hv.getConfigurationUI( this );
        DefaultMutableTreeNode horzViewer =
            new DefaultMutableTreeNode( 
                new ModuleNode( hv, ui, "Horizontal Viewer" ) );

        trunk.add( horzViewer );
        modulesConf.add( ui );

        DefaultMutableTreeNode browserLeaf =
            new DefaultMutableTreeNode( new BrowserOptionPanel( this ) );

        trunk.add( browserLeaf );

        DefaultMutableTreeNode remindLeaf =
            new DefaultMutableTreeNode( new RemindersOptionPanel( this ) );

        trunk.add( remindLeaf );

        DefaultMutableTreeNode privateLeaf =
            new DefaultMutableTreeNode( new PrivacyOptionPanel( this ) );

        trunk.add( privateLeaf );

        DefaultMutableTreeNode advancedBranch =
            new DefaultMutableTreeNode( FreeGuide.msg.getString( "advanced" ) );

        trunk.add( advancedBranch );

        /*TreePath pathViewers =
            addBranchWithModules(
                trunk, FreeGuide.msg.getString( "OptionsDialog.Tree.Viewers" ),
                PluginsManager.getViewers(  ) );*/
        TreePath pathGrabbers =
            addBranchWithModules( 
                advancedBranch, new GrabbersOptionPanel( this ),
                PluginsManager.getGrabbers(  ) );

        TreePath pathExporters =
            addBranchWithModules( 
                advancedBranch,
                FreeGuide.msg.getString( "OptionsDialog.Tree.Exporters" ),
                PluginsManager.getExporters(  ) );

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
        DefaultMutableTreeNode parent, Object obj, IModule[] modules )
    {

        DefaultMutableTreeNode branch = new DefaultMutableTreeNode( obj );

        parent.add( branch );

        for( int i = 0; i < modules.length; i++ )
        {

            IModuleConfigurationUI confUI =
                modules[i].getConfigurationUI( this );

            if( confUI != null )
            {
                modulesConf.add( confUI );

                DefaultMutableTreeNode modBranch =
                    new DefaultMutableTreeNode( 
                        new ModuleNode(
                            modules[i], confUI, modules[i].getName(  ) ) );

                branch.add( modBranch );

            }
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

        optionsPane.revalidate(  );

        optionsPane.repaint(  );

    }

    private void replaceOptionPanel( OptionPanel newOptionPanel )
    {
        optionsPane.removeAll(  );

        newOptionPanel.construct(  );

        optionsPane.add( newOptionPanel );

        optionPanel = newOptionPanel;

    }

    private void replaceOptionPanel( ModuleNode moduleNode )
    {
        optionsPane.removeAll(  );

        optionsPane.add( moduleNode.confUI.getPanel(  ) );

    }

    private void clearOptionPanel(  )
    {
        optionsPane.removeAll(  );

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
            optionPanel.resetToDefaults(  );

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

        IModule module;
        IModuleConfigurationUI confUI;
        String title;

        /**
         * Creates a new ModuleNode object.
         *
         * @param module DOCUMENT ME!
         * @param confUI DOCUMENT ME!
         * @param title DOCUMENT ME!
         */
        public ModuleNode( 
            IModule module, IModuleConfigurationUI confUI, final String title )
        {
            this.module = module;
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

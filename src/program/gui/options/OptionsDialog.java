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

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 *  FreeGuide's options screen.
 *
 *@author     Andy Balaam
 *@created    9 Dec 2003
 *@version    1
 */
public class OptionsDialog extends FGDialog implements TreeSelectionListener,
		ActionListener {

    /**
     *  Launch this screen as a normal options screen with everything on it.
     */
    public OptionsDialog( JFrame owner ) {
		super( owner, "Options" );
		
        // Draw the screen
        buildGUI();

    }

    /**
     *  Build the basic GUI of the options screen
     */
    private void buildGUI() {
        
		// Make the standard objects

		DefaultMutableTreeNode defaultLeaf = buildMenuTree();
		menuTree.getAccessibleContext().setAccessibleName(
			"Options Menu Tree" );
		JScrollPane menuScrollPane = new JScrollPane( menuTree );
		optionsScrollPane = new JScrollPane();
		
		JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
		
		defaultButton = newStandardJButton( "Default" );
		defaultButton.setMnemonic( KeyEvent.VK_D );
		cancelButton  = newStandardJButton( "Cancel" );
		cancelButton.setMnemonic( KeyEvent.VK_C );
		okButton      = newStandardJButton( "OK" );
		
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( getContentPane() );
		
		gbe.addFWXWYGW( splitPane    , 0, 0, gbe.FILL_BOTH, 1, 1, 3 );
		gbe.addAWXWY  ( defaultButton, 0, 1, gbe.ANCH_WEST, 1, 0 );
		gbe.addWXWY   ( cancelButton , 1, 1,                0, 0 );
		gbe.addWXWY   ( okButton     , 2, 1,                0, 0 );
		
		splitPane.setLeftComponent( menuScrollPane );
		splitPane.setRightComponent( optionsScrollPane );
		
		// Make the menu tree
		setTreeNode( defaultLeaf );
		
		splitPane.setDividerLocation( 150 );
		
		// Events
		defaultButton.addActionListener(this);
		cancelButton.addActionListener(this);
		okButton.addActionListener(this);
		
		// Set dialog-wide stuff
		
		getRootPane().setDefaultButton( okButton );
		
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		Dimension dialogSize = new Dimension( 500, 400 );
		setSize( dialogSize );
		
        setLocation( 	(screenSize.width  - dialogSize.width ) / 2,
						(screenSize.height - dialogSize.height) / 2 );

    }


	/**
	 * This is the section which must be customised to change the panels
	 * available on the options screen.
	 */
	private DefaultMutableTreeNode buildMenuTree() {
		
		DefaultMutableTreeNode trunk = new DefaultMutableTreeNode(
			"Options" );
		
		DefaultMutableTreeNode layoutLeaf = new DefaultMutableTreeNode(
			new LayoutOptionPanel( this ) );
		trunk.add( layoutLeaf );
		
		DefaultMutableTreeNode colourLeaf = new DefaultMutableTreeNode(
			new ColoursOptionPanel( this ) );
		trunk.add( colourLeaf );
		
		DefaultMutableTreeNode timeLeaf = new DefaultMutableTreeNode(
			new TimeOptionPanel( this ) );
		trunk.add( timeLeaf );
		
		DefaultMutableTreeNode browserLeaf = new DefaultMutableTreeNode(
			new BrowserOptionPanel( this ) );
		trunk.add( browserLeaf );

		DefaultMutableTreeNode remindLeaf = new DefaultMutableTreeNode(
			new RemindersOptionPanel( this ) );
		trunk.add( remindLeaf );
		
		DefaultMutableTreeNode privateLeaf = new DefaultMutableTreeNode(
			new PrivacyOptionPanel( this ) );
		trunk.add( privateLeaf );
		
		
		DefaultMutableTreeNode advancedBranch = new DefaultMutableTreeNode(
			"Advanced" );
		trunk.add( advancedBranch );
		
		DefaultMutableTreeNode grabberLeaf = new DefaultMutableTreeNode(
			new DownloadingOptionPanel( this ) );
		advancedBranch.add( grabberLeaf );
		
		DefaultMutableTreeNode dirLeaf = new DefaultMutableTreeNode(
			new DirectoriesOptionPanel( this ) );
		advancedBranch.add( dirLeaf );
		
		menuTree = new JTree( trunk );
		
		menuTree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION );
		
		menuTree.addTreeSelectionListener(this);
		//menuTree.setRootVisible( false );
		
		/*for( int i=0; i<menuTree.getRowCount(); i++ ) {
			menuTree.expandRow(i);
		}*/
		
		return layoutLeaf;
		
	}
	
	public void valueChanged(TreeSelectionEvent e) {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
        	menuTree.getLastSelectedPathComponent();

		setTreeNode( node );
		
	}
	
	private void setTreeNode( DefaultMutableTreeNode node ) {
		
		if( node == null) {
				return;
		}
		
		Object userObject = node.getUserObject();
		
    	if( userObject instanceof OptionPanel ) {
			
			OptionPanel newOptionPanel = (OptionPanel)userObject;

			replaceOptionPanel( newOptionPanel );
			
    	}
	}
	
	private void replaceOptionPanel( OptionPanel newOptionPanel ) {
		
		newOptionPanel.construct();
		
		optionsScrollPane.setViewportView( newOptionPanel );
		
		optionPanel = newOptionPanel;
		
		optionPanel.setPreferredSize( new Dimension( 10, 10 ) );
		
		repaint();
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		Object source = e.getSource();
		
		if( source == defaultButton ) {
			
			optionPanel.resetToDefaults();
			
		} else if( source == cancelButton ) {
			
			quit();
			
		} else if( source == okButton ) {
			
			saveAll();
			quit();
			
		}
		
	}
	
	private void saveAll() {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)(
			menuTree.getModel().getRoot() );
		
		for( 	Enumeration e = node.depthFirstEnumeration();
			 	e.hasMoreElements();
				node = (DefaultMutableTreeNode)e.nextElement() ) {
			
			Object userObject = node.getUserObject();
			
			if( userObject instanceof OptionPanel ) {
				
				if( ((OptionPanel)userObject).save() ) {
					updatedFlag = true;
				}
				
			}
			
		}
		
	}

	protected JButton newStandardJButton( String text ) {
		
		JButton ans = new JButton( text );
		Dimension size = new Dimension( 100, 24 );
		ans.setMaximumSize( size );
		ans.setMinimumSize( size );
		ans.setPreferredSize( size );
		
		return ans;
		
	}
	
	// -----------------------------------------
	
	private JTree menuTree;
	private Container contentPane;
	private OptionPanel optionPanel;
	private JScrollPane optionsScrollPane;
	
	private JButton okButton;
	private JButton cancelButton;
	private JButton defaultButton;
	
}

/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
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
import javax.swing.*;
import javax.swing.event.*;

/*
 *  A panel full of options about time
 *
 * @author     Andy Balaam
 * @created    12 Dec 2003
 * @version    1
 */

public class BrowserOptionPanel extends OptionPanel {

	public BrowserOptionPanel( FGDialog parent ) {
		super( parent );
	}
			
	public void doConstruct() {
		
		browsers = FreeGuide.prefs.getBrowsers();
		
		// Make the objects
		
		JLabel browserLabel = newLeftJLabel( "Web browser:" );
		browserComboBox = newRightJComboBox( browsers );
		browserLabel.setLabelFor(browserComboBox);
		browserLabel.setDisplayedMnemonic(KeyEvent.VK_W);
		
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( this );
		
		gbe.default_insets = new Insets( 1, 1, 1, 1 );
		gbe.default_ipadx = 5;
		gbe.default_ipady = 5;
		
		gbe.addFWX  ( browserLabel    , 0, 0, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( browserComboBox , 1, 0, gbe.FILL_HOR   , 0.8 );
		
		// Load in the values from config
		load();
		
	}
	
	protected void doLoad( String prefix ) {

		browserComboBox.setSelectedItem( misc.get( prefix + "browser",
			browsers[0] ) );

	}
	
	
	
	/**
	 * Saves the values in this option pane.
	 *
	 * @return false since nothing changes the view
	 */
	public boolean doSave() {
	
		int i = browserComboBox.getSelectedIndex();
	
		misc.put( "browser", browsers[i] );
		
		commandline.putStrings( "browser_command",
			FreeGuide.prefs.getCommands( "browser_command." + (i+1) ) );

		return false;
		
	}
	
	/**
	 * Used to find the name of this panel when displayed in a JTree.
	 */
	public String toString() {
		
		return "Browser";
		
	}

	// ----------------------------------
	
	private JComboBox browserComboBox;
	private String[] browsers;
	
}

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

package freeguidetv.gui.options;

import freeguidetv.gui.dialogs.*;
import freeguidetv.lib.general.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*
 *  A panel of options about directory paths
 *
 * @author     Andy Balaam
 * @created    12 Dec 2003
 * @version    1
 */

public class DirectoriesOptionPanel extends OptionPanel {

	public DirectoriesOptionPanel( FGDialog parent ) {
		super( parent );
	}
			
	public void doConstruct() {
		
		// Make the objects
		
		JLabel workingLabel = newLeftJLabel( "Working dir:" );
		workingTextField = newRightJTextField();
		workingLabel.setLabelFor(workingTextField);
		workingLabel.setDisplayedMnemonic(KeyEvent.VK_W);
		
		JLabel xmltvLabel = newLeftJLabel( "XMLTV dir:" );
		xmltvTextField = newRightJTextField();
		xmltvLabel.setLabelFor(xmltvTextField);
		xmltvLabel.setDisplayedMnemonic(KeyEvent.VK_X);
		
        JLabel configLabel = newLeftJLabel( "Grabber config:" );
        configTextField = newRightJTextField();
        configLabel.setLabelFor(configTextField);
		configLabel.setDisplayedMnemonic(KeyEvent.VK_O);
        
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( this );
		
		gbe.default_insets = new Insets( 1, 1, 1, 1 );
		gbe.default_ipadx = 5;
		gbe.default_ipady = 5;
		
		gbe.addFWX  ( workingLabel    , 0, 0, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( workingTextField, 1, 0, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( xmltvLabel      , 0, 1, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( xmltvTextField  , 1, 1, gbe.FILL_HOR   , 0.8 );
		
        gbe.addFWX  ( configLabel      , 0, 2, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( configTextField  , 1, 2, gbe.FILL_HOR   , 0.8 );
        
		// Load in the values from config
		load();
		
	}
	
	protected void doLoad( String prefix ) {
		
		xmltvTextField.setText( misc.get( prefix + "xmltv_directory" ) );
		
		workingTextField.setText( misc.get( prefix + "working_directory" ) );
        
        configTextField.setText( misc.get( prefix + "grabber_config" ) );
		
	}
	
	
	
	/**
	 * Saves the values in this option pane.
	 *
	 * @return false since the options don't affect the immediate display.
	 */
	public boolean doSave() {

		misc.put( "xmltv_directory", xmltvTextField.getText() );
		
		misc.put( "working_directory", workingTextField.getText() );
        
        misc.put( "grabber_config", configTextField.getText() );
		
		return false;
		
	}
	
	
	/**
	 * Used to find the name of this panel when displayed in a JTree.
	 */
	public String toString() {
		
		return "Directories";
		
	}

	// ----------------------------------
	
	private JTextField workingTextField;
	private JTextField xmltvTextField;
    private JTextField configTextField;
	
}

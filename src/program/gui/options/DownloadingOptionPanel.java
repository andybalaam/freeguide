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
 *  A panel full of options about the downloading listings
 *
 * @author     Andy Balaam
 * @created    10 Dec 2003
 * @version    1
 */

public class DownloadingOptionPanel extends OptionPanel {

	public DownloadingOptionPanel( FGDialog parent ) {
		super( parent );
	}
			
	public void doConstruct() {
		
		// Make the objects
		
		JLabel commandLabel = newLeftJLabel( "Grabber Command:" );
		commandTextArea = newRightJTextArea();
		JScrollPane commandScrollPane = new JScrollPane(commandTextArea);
		commandLabel.setLabelFor(commandTextArea);
		commandLabel.setDisplayedMnemonic( KeyEvent.VK_G );
		
		JLabel daysLabel = newLeftJLabel( "Download how much:" );
		Object[] daysOptions = new Object[8];
		daysOptions[0] = "1 day";
		daysOptions[1] = "2 days";
		daysOptions[2] = "3 days";
		daysOptions[3] = "4 days";
		daysOptions[4] = "5 days";
		daysOptions[5] = "6 days";
		daysOptions[6] = "1 week";
		daysOptions[7] = "2 weeks";
		daysComboBox = newRightJComboBox( daysOptions );
		
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( this );
		
		gbe.default_insets = new Insets( 1, 1, 1, 1 );
		gbe.default_ipadx = 5;
		gbe.default_ipady = 5;
		
		gbe.addFWX    ( commandLabel     , 0, 0, gbe.FILL_HOR   , 0.2 );
		gbe.addFWXWY  ( commandScrollPane, 1, 0, gbe.FILL_BOTH  , 0.8, 0.5 );
		
		gbe.addFWX    ( daysLabel        , 0, 1, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX    ( daysComboBox     , 1, 1, gbe.FILL_HOR   , 0.8 );
		
		// Load in the values from config
		load();

		//repaint();
		
	}
	
	protected void doLoad( String prefix ) {

		String[] commands = commandline.getStrings( prefix + "tv_grab" );
		commandTextArea.setText( lineBreakise( commands ) );
		
		int daysToDownload = misc.getInt( prefix + "days_to_grab", 7 );
		if( daysToDownload < 7 ) {
			daysComboBox.setSelectedIndex( daysToDownload - 1 );
		} else if( daysToDownload < 14 ) {
			daysComboBox.setSelectedIndex( 6 );
		} else {
			daysComboBox.setSelectedIndex( 7 );
		}
	
	}
	
	
	
	/**
	 * Saves the values in this option pane.
	 *
	 * @return false always since these options don't affect the screen display.
	 */
	public boolean doSave() {

		commandline.putStrings( "tv_grab", unlineBreakise(
			commandTextArea.getText() ) );
		
		int daysToDownload;
		int selectedIndex = daysComboBox.getSelectedIndex();
		switch( selectedIndex ) {
			case 7:
				daysToDownload = 14;
				break;
			case 6:
				daysToDownload = 7;
				break;
			default:
				daysToDownload = selectedIndex + 1;
				break;
		}
		misc.putInt( "days_to_grab", daysToDownload );
		
		// Return value is false since none of these options alter the screen
		// appearance.
		return false;
		
	}
	
	private void setDaysCombo( int daysToDownload ) {
		
		if( daysToDownload < 7 ) {
			daysComboBox.setSelectedIndex( daysToDownload - 1 );
		} else if( daysToDownload < 14 ) {
			daysComboBox.setSelectedIndex( 6 );
		} else {
			daysComboBox.setSelectedIndex( 7 );
		}
		
	}
	
	private String lineBreakise( String[] lines ) {
		
		String withLineBreaks = new String();
        for (int i = 0; i < lines.length; i++) {
            withLineBreaks += lines[i] + lb;
        }
		
		return withLineBreaks;
	}
	
	private String[] unlineBreakise( String withLineBreaks ) {
		
		return withLineBreaks.split(lb);
		
	}
	
	/**
	 * Used to find the name of this panel when displayed in a JTree.
	 */
	public String toString() {
		
		return "Downloading";
		
	}

	// ----------------------------------
	
	private JTextArea commandTextArea;
	private JComboBox daysComboBox;
	
	private String lb = System.getProperty("line.separator");	
	
}

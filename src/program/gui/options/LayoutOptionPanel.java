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
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;

/*
 *  A panel full of options about the screen layout in FreeGuide
 *
 * @author     Andy Balaam
 * @created    9 Dec 2003
 * @version    2
 */

public class LayoutOptionPanel extends OptionPanel implements ActionListener,
		ChangeListener, FocusListener {

	public LayoutOptionPanel( FGDialog parent ) {
		super( parent );
	}
			
	public void doConstruct() {
		
		// Make the objects
		
		JLabel lookAndFeelLabel = newLeftJLabel( "Look And Feel:" );
		lookAndFeelCombo = new JComboBox();
		lookAndFeelCombo.setEditable(true);
		List lafs = LookAndFeelManager.getAvailableLooksAndFeels();
		Iterator lafsIterator = lafs.iterator();
		while (lafsIterator.hasNext()) {
			lookAndFeelCombo.addItem(lafsIterator.next());
		}

		JLabel channelHeightLabel = newLeftJLabel( "Channel Height:" );
		channelHeightText = newMiddleJTextField();
		channelHeightSlider = newRightJSlider( 10, 100 );
		channelHeightSlider.getAccessibleContext().setAccessibleName(
			"Channel Height slider" );
		channelHeightLabel.setLabelFor(channelHeightText);
		channelHeightLabel.setDisplayedMnemonic( KeyEvent.VK_H );
			
		JLabel panelWidthLabel = newLeftJLabel( "Width of 1hr:" );
		panelWidthText = newMiddleJTextField();
		panelWidthSlider = newRightJSlider( 100, 1000 );
		panelWidthSlider.getAccessibleContext().setAccessibleName(
			"Width of 1hr slider" );
		panelWidthLabel.setLabelFor(panelWidthText);
		panelWidthLabel.setDisplayedMnemonic( KeyEvent.VK_W );
		
		JLabel fontLabel = newLeftJLabel( "Font:" );
		fontDemoText = newMiddleJTextField();
		fontDemoText.setEnabled( false );
		fontButton = newRightJButton( "Modify..." );
		fontButton.setMnemonic( KeyEvent.VK_M );
		
        JLabel alignLeftLabel = newLeftJLabel( "Moving names?" );
		Object[] options = new Object[2];
		options[0] = "Yes";
		options[1] = "No";
		alignLeftComboBox = newRightJComboBox( options );
        
        JLabel printDeltaLabel = newLeftJLabel( "Print time delta" );
		options = new Object[2];
		options[0] = "Yes";
		options[1] = "No";
		printDeltaComboBox = newRightJComboBox( options );
        
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( this );
		
		gbe.default_insets = new Insets( 1, 1, 1, 1 );
		gbe.default_ipadx = 5;
		gbe.default_ipady = 5;
		
		gbe.addFWX    ( lookAndFeelLabel , 0, 0, gbe.FILL_HOR   , 0.2 );
		gbe.addFWXWYGW( lookAndFeelCombo , 1, 0, gbe.FILL_HOR   , 0.1, 0, 2 );
        
		gbe.addFWX    ( channelHeightLabel , 0, 1, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX    ( channelHeightText  , 1, 1, gbe.FILL_HOR   , 0.4 );
		gbe.addFWX    ( channelHeightSlider, 2, 1, gbe.FILL_HOR   , 0.4 );
		
		gbe.addFWX    ( panelWidthLabel    , 0, 2, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX    ( panelWidthText     , 1, 2, gbe.FILL_HOR   , 0.4 );
		gbe.addFWX    ( panelWidthSlider   , 2, 2, gbe.FILL_HOR   , 0.4 );
		
		gbe.addFWX    ( fontLabel          , 0, 3, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX    ( fontDemoText      , 1, 3, gbe.FILL_HOR   , 0.4 );
		gbe.addAWXPXPY( fontButton         , 2, 3, gbe.ANCH_WEST  , 0.4, 0, 0 );
		
        gbe.addFWX    ( alignLeftLabel  , 0, 4, gbe.FILL_HOR   , 0.2 );
		gbe.addFWXWYGW( alignLeftComboBox,1, 4, gbe.FILL_HOR   , 0.8, 0, 2 );
        
        gbe.addFWX    ( printDeltaLabel, 0, 5, gbe.FILL_HOR   , 0.2 );
		gbe.addFWXWYGW( printDeltaComboBox,1, 5, gbe.FILL_HOR   , 0.8, 0, 2 );
        
		// Set up events
		channelHeightText.addFocusListener(this);
		channelHeightText.addActionListener(this);
		channelHeightSlider.addChangeListener(this);
		
		panelWidthText.addFocusListener(this);
		panelWidthText.addActionListener(this);
		panelWidthSlider.addChangeListener(this);
		
		fontButton.addActionListener(this);
		
		// Load in the values from config
		load();
		
		fontDialog = new FontChooserDialog( parent, "Choose Font", true,
        	new Font( screen.get( "font_name", "Dialog" ),
				screen.getInt( "font_style", Font.PLAIN ),
				screen.getInt( "font_size", 12) ) );
		
	}
		
	protected void doLoad( String prefix ) {
		
		
		LookAndFeel currentLAF = UIManager.getLookAndFeel();
		String defaultLAFName = "Metal";
		if (currentLAF != null) {
			defaultLAFName = currentLAF.getName();
		}
		String lookAndFeelName = FreeGuide.prefs.screen.get(
					"look_and_feel", defaultLAFName);
		lookAndFeelCombo.setSelectedItem(lookAndFeelName);

		int channelHeight = screen.getInt( prefix + "channel_height", 28 );
		channelHeightSlider.setValue( channelHeight );
		channelHeightText.setText( String.valueOf( channelHeight ) );
		
		int panelWidth = screen.getInt( prefix + "panel_width", 8000 );
		panelWidthSlider.setValue( panelWidth / 24 );
		panelWidthText.setText( String.valueOf( panelWidth / 24 ) );
		
		String fontName = screen.get( prefix + "font_name", "Dialog" );
		int fontStyleInt = screen.getInt( prefix + "font_style", Font.PLAIN );
		int fontSize = screen.getInt( prefix + "font_size", 11 );
		currentFont = new Font( fontName, fontStyleInt, fontSize );
		fontDemoText.setText( fontName + " " + fontSize );
		
        boolean alignLeft = screen.getBoolean( "align_text_to_left", true);
		if( alignLeft ) {
			alignLeftComboBox.setSelectedIndex( 0 );
		} else {
			alignLeftComboBox.setSelectedIndex( 1 );
		}
        
        boolean printDelta = screen.getBoolean( "display_time_delta", true);
		if( printDelta) {
			printDeltaComboBox.setSelectedIndex( 0 );
		} else {
			printDeltaComboBox.setSelectedIndex( 1 );
		}
        
	}
	
	public boolean doSave() {

		boolean updated = false;
		
		updated = screen.update( "look_and_feel",
			lookAndFeelCombo.getSelectedItem().toString())
			|| updated;
		
		updated = screen.updateInt( "channel_height",
			channelHeightSlider.getValue() ) || updated;
			
		updated = screen.updateInt( "panel_width",
			panelWidthSlider.getValue() * 24 ) || updated;
		
		updated = screen.update( "font_name", currentFont.getFontName() )
			|| updated;
		
		updated = screen.updateInt( "font_style", currentFont.getStyle() )
			|| updated;
		
		updated = screen.updateInt( "font_size", currentFont.getSize() )
			|| updated;
		
        updated = screen.updateBoolean( "align_text_to_left",
                ( alignLeftComboBox.getSelectedIndex() == 0 ) )
    			|| updated;
            
        updated = screen.updateBoolean( "display_time_delta",
                ( printDeltaComboBox.getSelectedIndex() == 0 ) )
    			|| updated;
            
		return updated;
		
	}
	
	/**
	 * Used to find the name of this panel when displayed in a JTree.
	 */
	public String toString() {
		
		return "Layout";
		
	}
	
	// ------------------------------------
	// Event handlers
	
	public void stateChanged(ChangeEvent e) {
		
		Object source = e.getSource();
		
		if( source == channelHeightSlider ) {
			
			channelHeightText.setText( String.valueOf(
				channelHeightSlider.getValue() ) );
			
		} else if( source == panelWidthSlider ) {
			
			panelWidthText.setText( String.valueOf(
				panelWidthSlider.getValue() ) );
			
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		
		updateSlider( e.getSource() );
		
	}

	public void focusGained( FocusEvent e ) {}
	
	public void focusLost( FocusEvent e ) {
	
		updateSlider( e.getSource() );
	
	}
	
	private void updateSlider( Object source ) {
		
		if( source == channelHeightText ) {
			
			channelHeightSlider.setValue( Integer.parseInt(
				channelHeightText.getText() ) );
			
		} else if( source == panelWidthText ) {
			
			panelWidthSlider.setValue( Integer.parseInt(
				panelWidthText.getText() ) );
			
		} else if( source == fontButton ) {
			
			Dimension fontDialogSize = new Dimension(300, 200);
			Dimension parentSize = parent.getSize();
			Point parentLocation = parent.getLocationOnScreen();
		
			fontDialog.setLocation(
				parentLocation.x + ((parentSize.width
					- fontDialogSize.width)/2),
				parentLocation.y + ((parentSize.height
					- fontDialogSize.height)/2));
			
			fontDialog.setSize( fontDialogSize );
			fontDialog.show();
			currentFont = fontDialog.getSelectedFont();
			fontDemoText.setText( currentFont.getFontName() + " "
				+ currentFont.getSize() );
			
		}
		
	}
	
	// ----------------------------------
	
	private JComboBox lookAndFeelCombo;
	private JSlider channelHeightSlider;
	private JTextField channelHeightText;
	private JSlider panelWidthSlider;
	private JTextField panelWidthText;
	private JTextField fontDemoText;
	private JButton fontButton;
    private JComboBox alignLeftComboBox;
    private JComboBox printDeltaComboBox;
	
	private FontChooserDialog fontDialog;
	private Font currentFont;
	
	
}

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

package freeguidetv.gui.wizard;

import freeguidetv.gui.dialogs.*;
import freeguidetv.lib.fgspecific.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 *  A JPanel to go on a FreeGuideWizard to choose the user's privacy
 * preferences.
 *
 *@author     Andy Balaam
 *@created    5th December 2003
 *@version    1
 */
public class PrivacyWizardPanel extends WizardPanel implements ActionListener {

    /**
     *  Create a new PrivacyWizardPanel.
     *
     */
    PrivacyWizardPanel() {
        super();
    }


    /**
     *  Construct the GUI of this Wizard Panel.
     */
    public void construct() {

        java.awt.GridBagConstraints gridBagConstraints;

        JPanel midPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
        JLabel topLabel = new JLabel();
        JLabel bottomLabel = new JLabel();
		JLabel bottomLabel2 = new JLabel();
		JLabel bottomLabel3 = new JLabel();
		JButton butMoreInfo = new JButton();

		nickField = new JTextField();
		
        setLayout(new java.awt.GridBagLayout());

		bottomPanel.setLayout(new java.awt.GridBagLayout());
		
        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText( "Would you like to check for updates when FreeGuide starts?" );
		gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.3;
        add( topLabel, gridBagConstraints );

        midPanel.setLayout(new java.awt.GridBagLayout());

		noButton = new JRadioButton("No, I'm quite happy with this version.");
		yesIPButton 	= new JRadioButton(
			"Yes, and you can show my IP address." );
		yesNothingButton= new JRadioButton("Yes, but don't show anything .");
		yesNickButton	= new JRadioButton(
			"Yes, and use this nickname for me:" );
		
		noButton.setMnemonic(KeyEvent.VK_N);
		yesIPButton.setMnemonic(KeyEvent.VK_Y);
		yesNothingButton.setMnemonic(KeyEvent.VK_E);
		yesNickButton.setMnemonic(KeyEvent.VK_S);
		
		butMoreInfo.setMnemonic(KeyEvent.VK_M);
		
        ButtonGroup group = new ButtonGroup();
		group.add(noButton);
		group.add(yesIPButton);
		group.add(yesNothingButton);
		group.add(yesNickButton);

		noButton.addActionListener(this);
		yesIPButton.addActionListener(this);
		yesNothingButton.addActionListener(this);
		yesNickButton.addActionListener(this);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);

		gridBagConstraints.gridy = 0;
		midPanel.add(noButton, gridBagConstraints);
		gridBagConstraints.gridy = 1;
        midPanel.add(yesIPButton, gridBagConstraints);
		gridBagConstraints.gridy = 2;
		midPanel.add(yesNothingButton, gridBagConstraints);
		gridBagConstraints.gridy = 3;
		midPanel.add(yesNickButton, gridBagConstraints);
		
		gridBagConstraints.gridy = 4;
		midPanel.add(nickField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.4;
        add( midPanel, gridBagConstraints );

		bottomLabel.setLayout(new java.awt.GridBagLayout());
		
        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText( "It is useful to us to know how many people" );
			
		bottomLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel2.setText( " are using FreeGuide,but your information won't" );
        
		bottomLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel3.setText( "be used for anything except counting." );
		
		gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomPanel.add( bottomLabel, gridBagConstraints );
		
		gridBagConstraints.gridy = 1;
		bottomPanel.add( bottomLabel2, gridBagConstraints );
		
		gridBagConstraints.gridy = 2;
		bottomPanel.add( bottomLabel3, gridBagConstraints );
		
		butMoreInfo.setText( "More Info..." );
		butMoreInfo.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    moreInfo();
                }
            });
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
		gridBagConstraints.insets = new java.awt.Insets(3, 8, 3, 3);
		bottomPanel.add( butMoreInfo, gridBagConstraints );
		
		gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.3;
		add( bottomPanel, gridBagConstraints );

		doEnabling();
		
    }
	
    // --------------------------------------------

	public void actionPerformed(ActionEvent e) {
		
		doEnabling();
		
	}
	
	private void doEnabling() {
		
		if( yesNickButton.isSelected() ) {
			nickField.setEnabled( true );
			nickField.setBackground( Color.WHITE );
		} else {
			nickField.setEnabled( false );
			nickField.setBackground( Color.LIGHT_GRAY );
		}
		
	}
	
	private void moreInfo() {
		
		new PrivacyInfoDialog().show();
		
	}
	
    /**
     *  Description of the Method
     *
     *@param  pref  Description of the Parameter
     */
    protected void saveToPrefs(FGPreferences pref) {
        pref.put(configEntry, (String) getBoxValue());
    }


    /**
     *  Description of the Method
     *
     *@param  pref  Description of the Parameter
     */
    protected void loadFromPrefs(FGPreferences pref) {
        setBoxValue( pref.get( configEntry, "yes_ip" ) );
    }


    // --------------------------------------------

    /**
     *  Gets the boxValue attribute of the PrivacyWizardPanel object
     *
     *@return    The boxValue value
     */
    protected Object getBoxValue() {
		
		if( yesIPButton.isSelected() ) {
			return "yes_ip";
		} else if( yesNickButton.isSelected() ) {
			return "yes_nick:" + nickField.getText();
		} else if( yesNothingButton.isSelected() ) {
			return "yes_nothing";
		} else if( noButton.isSelected() ) {
			return "no";
		}		

		return "";
		
    }


    /**
     *  Sets the boxValue attribute of the PrivacyWizardPanel object
     *
     *@param  val  The new boxValue value
     */
    protected void setBoxValue(Object val) {
        String value = (String)val;
		
		if( value.startsWith("yes_nick:") ) {
			yesNickButton.setSelected( true );
			nickField.setText( value.substring( 9 ) );
		} else if( value.equals("yes_ip") ) {
			yesIPButton.setSelected( true );
		} else if( value.equals("yes_nothing") ) {
			yesNothingButton.setSelected( true );
		} else if( value.equals("no") ) {
			noButton.setSelected( true );
		}
	
		doEnabling();
	
    }

	JRadioButton yesIPButton;
	JRadioButton yesNickButton;
	JRadioButton yesNothingButton;
	JRadioButton noButton;
	JTextField nickField;

}


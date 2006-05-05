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
package freeguide.plugins.program.freeguide.wizard;

import freeguide.common.lib.fgspecific.Application;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.dialogs.PrivacyInfoDialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * A JPanel to go on a FreeGuideWizard to choose the user's privacy
 * preferences.
 *
 * @author Andy Balaam
 * @version 1
 */
public class PrivacyWizardPanel extends WizardPanel implements ActionListener
{
    JRadioButton yesIPButton;
    JRadioButton yesNickButton;
    JRadioButton yesNothingButton;
    JRadioButton noButton;
    JTextField nickField;

/**
     * Create a new PrivacyWizardPanel.
     */
    PrivacyWizardPanel(  )
    {
        super(  );

    }

    /**
     * Construct the GUI of this Wizard Panel.
     */
    public void construct(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;

        JPanel midPanel = new JPanel(  );

        JPanel bottomPanel = new JPanel(  );

        JLabel topLabel = new JLabel(  );

        JLabel bottomLabel = new JLabel(  );

        JLabel bottomLabel2 = new JLabel(  );

        JLabel bottomLabel3 = new JLabel(  );

        JButton butMoreInfo = new JButton(  );

        nickField = new JTextField(  );

        setLayout( new java.awt.GridBagLayout(  ) );

        bottomPanel.setLayout( new java.awt.GridBagLayout(  ) );

        topLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        topLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        topLabel.setText( 
            Application.getInstance(  )
                       .getLocalizedMessage( 
                "check_for_updates_when_freeguide_starts" ) );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints.weighty = 0.3;

        add( topLabel, gridBagConstraints );

        midPanel.setLayout( new java.awt.GridBagLayout(  ) );

        noButton = new JRadioButton( 
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "no_im_quite_happy_with_this_version" ) );

        yesIPButton = new JRadioButton( 
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "yes_and_you_can_show_my_ip_address" ) );

        yesNothingButton = new JRadioButton( 
                Application.getInstance(  )
                           .getLocalizedMessage( "yes_but_dont_show_anything" ) );

        yesNickButton = new JRadioButton( 
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "yes_and_use_this_nickname_for_me" ) + ":" );

        noButton.setMnemonic( KeyEvent.VK_N );

        yesIPButton.setMnemonic( KeyEvent.VK_Y );

        yesNothingButton.setMnemonic( KeyEvent.VK_E );

        yesNickButton.setMnemonic( KeyEvent.VK_S );

        butMoreInfo.setMnemonic( KeyEvent.VK_M );

        ButtonGroup group = new ButtonGroup(  );

        group.add( noButton );

        group.add( yesIPButton );

        group.add( yesNothingButton );

        group.add( yesNickButton );

        noButton.addActionListener( this );

        yesIPButton.addActionListener( this );

        yesNothingButton.addActionListener( this );

        yesNickButton.addActionListener( this );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints.weightx = 0.9;

        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );

        gridBagConstraints.gridy = 0;

        midPanel.add( noButton, gridBagConstraints );

        gridBagConstraints.gridy = 1;

        midPanel.add( yesIPButton, gridBagConstraints );

        gridBagConstraints.gridy = 2;

        midPanel.add( yesNothingButton, gridBagConstraints );

        gridBagConstraints.gridy = 3;

        midPanel.add( yesNickButton, gridBagConstraints );

        gridBagConstraints.gridy = 4;

        midPanel.add( nickField, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 1;

        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints.weighty = 0.4;

        add( midPanel, gridBagConstraints );

        bottomLabel.setLayout( new java.awt.GridBagLayout(  ) );

        bottomLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        bottomLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        bottomLabel.setText( 
            Application.getInstance(  )
                       .getLocalizedMessage( 
                "it_is_useful_to_know_how_many.1" ) );

        bottomLabel2.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        bottomLabel2.setHorizontalAlignment( 
            javax.swing.SwingConstants.CENTER );

        bottomLabel2.setText( 
            Application.getInstance(  )
                       .getLocalizedMessage( 
                "it_is_useful_to_know_how_many.2" ) );

        bottomLabel3.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        bottomLabel3.setHorizontalAlignment( 
            javax.swing.SwingConstants.CENTER );

        bottomLabel3.setText( 
            Application.getInstance(  )
                       .getLocalizedMessage( 
                "it_is_useful_to_know_how_many.3" ) );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

        bottomPanel.add( bottomLabel, gridBagConstraints );

        gridBagConstraints.gridy = 1;

        bottomPanel.add( bottomLabel2, gridBagConstraints );

        gridBagConstraints.gridy = 2;

        bottomPanel.add( bottomLabel3, gridBagConstraints );

        butMoreInfo.setText( 
            Application.getInstance(  ).getLocalizedMessage( "more_info_dot" ) );

        butMoreInfo.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    moreInfo(  );

                }
            } );

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.gridheight = 3;

        gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;

        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;

        gridBagConstraints.insets = new java.awt.Insets( 3, 8, 3, 3 );

        bottomPanel.add( butMoreInfo, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 2;

        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints.weighty = 0.3;

        add( bottomPanel, gridBagConstraints );

        doEnabling(  );

    }

    // --------------------------------------------
    /**
     * DOCUMENT_ME!
     *
     * @param e DOCUMENT_ME!
     */
    public void actionPerformed( ActionEvent e )
    {
        doEnabling(  );

    }

    private void doEnabling(  )
    {
        if( yesNickButton.isSelected(  ) )
        {
            nickField.setEnabled( true );

            nickField.setBackground( Color.WHITE );

        }

        else
        {
            nickField.setEnabled( false );

            nickField.setBackground( Color.LIGHT_GRAY );

        }
    }

    private void moreInfo(  )
    {
        new PrivacyInfoDialog(  ).setVisible( true );

    }

    /**
     * Gets the boxValue attribute of the PrivacyWizardPanel object
     *
     * @return The boxValue value
     */
    protected Object getBoxValue(  )
    {
        if( yesIPButton.isSelected(  ) )
        {
            return "yes_ip";

        }

        else if( yesNickButton.isSelected(  ) )
        {
            return "yes_nick:" + nickField.getText(  );

        }

        else if( yesNothingButton.isSelected(  ) )
        {
            return "yes_nothing";

        }

        else if( noButton.isSelected(  ) )
        {
            return "no";

        }

        return "";

    }

    /**
     * Sets the boxValue attribute of the PrivacyWizardPanel object
     *
     * @param val The new boxValue value
     */
    protected void setBoxValue( Object val )
    {
        String value = (String)val;

        if( value.startsWith( "yes_nick:" ) )
        {
            yesNickButton.setSelected( true );

            nickField.setText( value.substring( 9 ) );

        }

        else if( value.equals( "yes_ip" ) )
        {
            yesIPButton.setSelected( true );

        }

        else if( value.equals( "yes_nothing" ) )
        {
            yesNothingButton.setSelected( true );

        }

        else if( value.equals( "no" ) )
        {
            noButton.setSelected( true );

        }

        doEnabling(  );

    }
}

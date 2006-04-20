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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A JPanel to go on a FreeGuideWizard to choose a boolean option with a
 * checkbox.
 *
 * @author Andy Balaam
 * @version 1
 */
public class BooleanWizardPanel extends WizardPanel
{

    // -------------------------------------------
    private JCheckBox checkbox;

    /**
     * Constructor for the BooleanWizardPanel object
     */
    BooleanWizardPanel(  )
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

        JLabel topLabel = new JLabel(  );

        JLabel bottomLabel = new JLabel(  );

        JButton butBrowse = new JButton(  );

        checkbox = new JCheckBox(  );

        setLayout( new java.awt.GridLayout( 3, 0 ) );

        topLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        topLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        add( topLabel );

        midPanel.setLayout( new java.awt.GridBagLayout(  ) );

        checkbox.setText( topMessage );

        checkbox.setMnemonic( topMnemonic );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints.weightx = 0.9;

        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );

        midPanel.add( checkbox, gridBagConstraints );

        add( midPanel );

        bottomLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        bottomLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        bottomLabel.setText( bottomMessage );

        add( bottomLabel );

    }

    /**
     * Gets the boxValue attribute of the BooleanWizardPanel object
     *
     * @return The boxValue value
     */
    protected Object getBoxValue(  )
    {

        return new Boolean( checkbox.isSelected(  ) );

    }

    /**
     * Sets the boxValue attribute of the BooleanWizardPanel object
     *
     * @param val The new boxValue value
     */
    protected void setBoxValue( Object val )
    {
        checkbox.setSelected( ( (Boolean)val ).booleanValue(  ) );

    }

    // The checkbox for the choice
}

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
package freeguide.gui.wizard;

import freeguide.lib.fgspecific.*;

import javax.swing.*;

/**
 * A JPanel to go on a FreeGuideWizard that allows entry of a text value.
 *
 * @author Andy Balaam
 * @version 1
 */
public class TextWizardPanel extends WizardPanel
{

    // -------------------------------------------
    private JTextField textfield;

    /**
     * Create a new FreeGuideTextWizardPanel.
     */
    public TextWizardPanel(  )
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

        textfield = new JTextField(  );

        setLayout( new java.awt.GridLayout( 3, 0 ) );

        topLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );
        topLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
        topLabel.setText( topMessage );
        topLabel.setDisplayedMnemonic( topMnemonic );
        add( topLabel );

        midPanel.setLayout( new java.awt.GridBagLayout(  ) );

        textfield.setMinimumSize( new java.awt.Dimension( 4, 26 ) );
        textfield.setPreferredSize( new java.awt.Dimension( 69, 26 ) );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );

        midPanel.add( textfield, gridBagConstraints );

        add( midPanel );

        bottomLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );
        bottomLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
        bottomLabel.setText( bottomMessage );
        add( bottomLabel );

        topLabel.setLabelFor( textfield );

    }

    // -------------------------------------------

    /**
     * Description of the Method
     *
     * @param pref Description of the Parameter
     */
    protected void saveToPrefs( FGPreferences pref )
    {
        pref.put( configEntry, (String)getBoxValue(  ) );
    }

    /**
     * Description of the Method
     *
     * @param pref Description of the Parameter
     */
    protected void loadFromPrefs( FGPreferences pref )
    {
        setBoxValue( pref.get( configEntry ) );
    }

    // -----------------------------------

    /**
     * Gets the boxValue attribute of the FreeGuideTextWizardPanel object
     *
     * @return The boxValue value
     */
    protected Object getBoxValue(  )
    {

        return textfield.getText(  );
    }

    /**
     * Sets the boxValue attribute of the FreeGuideTextWizardPanel object
     *
     * @param val The new boxValue value
     */
    protected void setBoxValue( Object val )
    {
        textfield.setText( (String)val );
    }

    // The textfield that is the box on this panel
}

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

import freeguide.lib.fgspecific.FGPreferences;

import java.awt.GridLayout;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A JPanel to go on a WizardFrame to choose from a list of choices in a
 * combobox.
 *
 * @author Andy Balaam
 * @version 3
 */
public class ChoiceWizardPanel extends WizardPanel
{

    // -------------------------------------------
    private JComboBox combobox;

    // The combobox for the choice
    private Set choices;

    /**
     * Create a new FreeGuideChoiceWizardPanel.
     *
     * @param choices Description of the Parameter
     */
    ChoiceWizardPanel( Set choices )
    {
        super(  );

        this.choices = choices;

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

        combobox = new JComboBox(  );

        setLayout( new GridLayout( 3, 0 ) );

        topLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        topLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        topLabel.setText( topMessage );

        topLabel.setDisplayedMnemonic( topMnemonic );

        add( topLabel );

        midPanel.setLayout( new java.awt.GridBagLayout(  ) );

        combobox.setMinimumSize( new java.awt.Dimension( 4, 26 ) );

        combobox.setPreferredSize( new java.awt.Dimension( 69, 26 ) );

        combobox.setMaximumRowCount( 20 );

        updateChoices(  );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints.weightx = 0.9;

        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );

        midPanel.add( combobox, gridBagConstraints );

        add( midPanel );

        bottomLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        bottomLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        bottomLabel.setText( bottomMessage );

        add( bottomLabel );

        topLabel.setLabelFor( combobox );

    }

    // --------------------------------------------

    /**
     * Change the choices available in the combobox
     *
     * @param choices the new choices in an array of String.
     */
    public void setChoices( String[] choices )
    {
        this.choices = new TreeSet( Arrays.asList( choices ) );

        updateChoices(  );

    }

    /**
     * Make the combobox reflect the choices available.
     */
    private void updateChoices(  )
    {

        String[] data =
            (String[])choices.toArray( new String[choices.size(  )] );
        Arrays.sort( data );

        combobox.removeAllItems(  );

        for( int i = 0; i < data.length; i++ )
        {
            combobox.addItem( data[i] );
        }
    }

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

    // --------------------------------------------

    /**
     * Gets the boxValue attribute of the ChoiceWizardPanel object
     *
     * @return The boxValue value
     */
    protected Object getBoxValue(  )
    {

        return combobox.getSelectedItem(  );

    }

    /**
     * Sets the boxValue attribute of the ChoiceWizardPanel object
     *
     * @param val The new boxValue value
     */
    protected void setBoxValue( Object val )
    {

        if( val != null )
        {
            combobox.setSelectedItem( val );
        }
    }

    // The possible choices
}

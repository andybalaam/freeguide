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

import freeguide.*;

import freeguide.lib.fgspecific.*;

import java.awt.event.*;

import java.io.*;

import javax.swing.*;

/**
 * A JPanel to go on a FreeGuideWizard that alowws you to choose a file.
 *
 * @author Andy Balaam
 * @version 1
 */
public abstract class AbstractFileWizardPanel extends WizardPanel
{

    // ------------------------------
    private JTextField textfield;

    /**
     * Create a new FreeGuideFileWizardPanel.
     */
    AbstractFileWizardPanel(  )
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

        // Make the browse button
        butBrowse.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        butBrowse.setText( FreeGuide.msg.getString( "browse" ) );

        butBrowse.setMnemonic( KeyEvent.VK_B );

        butBrowse.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    browse(  );

                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 2;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );

        midPanel.add( butBrowse, gridBagConstraints );

        add( midPanel );

        bottomLabel.setFont( new java.awt.Font( "Dialog", 0, 12 ) );

        bottomLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        bottomLabel.setText( bottomMessage );

        add( bottomLabel );

        topLabel.setLabelFor( textfield );

    }

    /**
     * Description of the Method
     */
    private void browse(  )
    {

        JFileChooser chooser = new JFileChooser(  );

        chooser.setCurrentDirectory( new File( textfield.getText(  ) ) );

        int returnVal;

        chooser.setFileSelectionMode( this.getFileSelectionMode(  ) );

        returnVal = chooser.showDialog( this, this.getFileChooserMessage(  ) );

        if( returnVal == JFileChooser.APPROVE_OPTION )
        {
            textfield.setText( 
                chooser.getSelectedFile(  ).getAbsolutePath(  ) );

        }
    }

    /**
     * Gets the fileSelectionMode attribute of the AbstractFileWizard object
     *
     * @return The fileSelectionMode value
     */
    protected abstract int getFileSelectionMode(  );

    /**
     * Gets the fileChooserMessage attribute of the AbstractFileWizard object
     *
     * @return The fileChooserMessage value
     */
    protected abstract String getFileChooserMessage(  );

    // -----------------------------------

    /**
     * Description of the Method
     *
     * @param pref Description of the Parameter
     */
    protected void saveToPrefs( FGPreferences pref )
    {
        pref.put( configEntry, ( (File)getBoxValue(  ) ).getPath(  ) );

    }

    /**
     * Description of the Method
     *
     * @param pref Description of the Parameter
     */
    protected void loadFromPrefs( FGPreferences pref )
    {

        String pr = pref.get( configEntry );

        if( pr == null )
        {
            pr = "";

        }

        setBoxValue( new File( pr ) );

    }

    // -----------------------------------

    /**
     * Gets the boxValue attribute of the AbstractFileWizard object
     *
     * @return The boxValue value
     */
    protected Object getBoxValue(  )
    {

        return new File( textfield.getText(  ) );

    }

    /**
     * Sets the boxValue attribute of the AbstractFileWizard object
     *
     * @param val The new boxValue value
     */
    protected void setBoxValue( Object val )
    {
        textfield.setText( (String)val );

    }

    // The textfield for dir, file and text panels
}

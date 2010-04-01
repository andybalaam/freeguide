package freeguide.plugins.program.freeguide.options;

import freeguide.common.lib.fgspecific.Application;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PanelGeneralUI extends JPanel
{
    private JLabel labelWorkDir = null;
    private JLabel labelLF = null;
    private JTextField textWorkingDir = null;
    private JComboBox cbLF = null;

    //private JLabel labelLang = null;
    //private JComboBox cbLang = null;
    /**
     * This is the default constructor
     */
    public PanelGeneralUI(  )
    {
        super(  );
        initialize(  );
    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {
        //labelLang = new JLabel(  );
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints5 = new GridBagConstraints(  );
        labelLF = new JLabel(  );
        labelWorkDir = new JLabel(  );

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints(  );
        this.setLayout( new GridBagLayout(  ) );
        this.setSize( 300, 200 );
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets( 3, 3, 3, 3 );
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        labelWorkDir.setText( "Working dir:" );
        labelWorkDir.setText(
            Application.getInstance(  )
                       .getLocalizedMessage( "Options.General.WorkingDir" )
            + ":" );
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.insets = new java.awt.Insets( 3, 3, 3, 3 );
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        labelLF.setText( "LF:" );
        labelLF.setLabelFor( getCbLF(  ) );
        labelLF.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_F );
        labelWorkDir.setLabelFor( getTextWorkingDir(  ) );
        labelWorkDir.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_W );
        labelLF.setText(
            Application.getInstance(  )
                       .getLocalizedMessage( "Options.General.LF" ) + ":" );
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.insets = new java.awt.Insets( 3, 3, 3, 3 );
        gridBagConstraints4.gridx = 1;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.weightx = 1.0;
        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints4.insets = new java.awt.Insets( 3, 3, 3, 3 );
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.gridy = 2;
        /*labelLang.setText( "Language:" );
        labelLang.setText(
            Application.getInstance(  )
                       .getLocalizedMessage( "Options.General.Language" )
            + ":" );
        labelLang.setLabelFor( getCbLang(  ) );
        labelLang.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_L );*/
        gridBagConstraints6.gridx = 1;
        gridBagConstraints6.gridy = 2;
        gridBagConstraints6.weightx = 1.0;
        gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints6.insets = new java.awt.Insets( 3, 3, 3, 3 );
        gridBagConstraints5.insets = new java.awt.Insets( 3, 3, 3, 3 );
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
        this.add( labelLF, gridBagConstraints2 );
        //this.add( labelLang, gridBagConstraints5 );
        this.add( labelWorkDir, gridBagConstraints1 );
        this.add( getTextWorkingDir(  ), gridBagConstraints3 );
        this.add( getCbLF(  ), gridBagConstraints4 );

        //this.add( getCbLang(  ), gridBagConstraints6 );
    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    public JTextField getTextWorkingDir(  )
    {
        if( textWorkingDir == null )
        {
            textWorkingDir = new JTextField(  );
        }

        return textWorkingDir;
    }

    /**
     * This method initializes jComboBox
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getCbLF(  )
    {
        if( cbLF == null )
        {
            cbLF = new JComboBox(  );
        }

        return cbLF;
    }

    /**
     * This method initializes jComboBox
     *
     * @return javax.swing.JComboBox
     */

    //public JComboBox getCbLang(  )
    //{
    //    if( cbLang == null )
    //    {
    //        cbLang = new JComboBox(  );
    //    }

    //    return cbLang;
    //}
}

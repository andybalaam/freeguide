package freeguide.gui.options;

import freeguide.FreeGuide;

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
        labelWorkDir.setText( "Working dir:" );
        labelWorkDir.setText( FreeGuide.msg.getString( "working_dir" ) + ":" );
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        gridBagConstraints2.insets = new java.awt.Insets( 3, 3, 3, 3 );
        labelLF.setText( "LF:" );
        labelLF.setLabelFor( getCbLF(  ) );
        labelWorkDir.setLabelFor( getTextWorkingDir(  ) );
        labelWorkDir.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_W );
        labelLF.setText( FreeGuide.msg.getString( "look_and_feel" ) + ":" );
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
        this.add( labelWorkDir, gridBagConstraints1 );
        this.add( labelLF, gridBagConstraints2 );
        this.add( getTextWorkingDir(  ), gridBagConstraints3 );
        this.add( getCbLF(  ), gridBagConstraints4 );
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
}

package freeguide.plugins.grabber.xmltv;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class ConfigureUIPanel extends JPanel
{

    private JCheckBox cbGrab = null;
    private JButton btnChannels = null;
    private JTextField textCommand = null;
    private JButton btnCommandReset = null;

    /**
     * This is the default constructor
     *
     * @param title DOCUMENT ME!
     */
    public ConfigureUIPanel( final String title )
    {
        super(  );

        initialize( title );

    }

    /**
     * This method initializes this
     *
     * @param title DOCUMENT ME!
     */
    private void initialize( final String title )
    {

        //setPreferredSize(new Dimension(300,200));
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints4 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );

        this.setLayout( new GridBagLayout(  ) );

        this.setBorder( 
            javax.swing.BorderFactory.createTitledBorder( 
                null, title,
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null ) );

        gridBagConstraints1.gridx = 0;

        gridBagConstraints1.gridy = 0;

        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints2.gridx = 1;

        gridBagConstraints2.gridy = 0;

        gridBagConstraints2.insets = new java.awt.Insets( 3, 3, 3, 3 );

        gridBagConstraints4.gridx = 0;

        gridBagConstraints4.gridy = 1;

        gridBagConstraints4.weightx = 1.0;

        gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints4.insets = new java.awt.Insets( 5, 5, 5, 5 );

        gridBagConstraints6.gridx = 1;

        gridBagConstraints6.gridy = 1;

        gridBagConstraints6.insets = new java.awt.Insets( 3, 3, 3, 3 );

        this.add( getCbGrab(  ), gridBagConstraints1 );

        this.add( getBtnChannels(  ), gridBagConstraints2 );

        this.add( getTextCommand(  ), gridBagConstraints4 );

        this.add( getBtnCommandReset(  ), gridBagConstraints6 );

    }

    /**
     * This method initializes jCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getCbGrab(  )
    {

        if( cbGrab == null )
        {
            cbGrab = new JCheckBox(  );

            cbGrab.setText( "Grab data" );

        }

        return cbGrab;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnChannels(  )
    {

        if( btnChannels == null )
        {
            btnChannels = new JButton(  );

            btnChannels.setText( "..." );

            btnChannels.setToolTipText( "Select channels" );

        }

        return btnChannels;

    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    public JTextField getTextCommand(  )
    {

        if( textCommand == null )
        {
            textCommand = new JTextField(  );

            textCommand.setColumns( 10 );

        }

        return textCommand;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnCommandReset(  )
    {

        if( btnCommandReset == null )
        {
            btnCommandReset = new JButton(  );

            btnCommandReset.setText( "C" );

            btnCommandReset.setToolTipText( "Reset command to default" );

        }

        return btnCommandReset;

    }
}

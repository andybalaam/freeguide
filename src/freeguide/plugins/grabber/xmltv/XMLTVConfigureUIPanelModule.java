package freeguide.plugins.grabber.xmltv;

import freeguide.common.plugininterfaces.ILocalizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel for edit one XMLTV grabber.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class XMLTVConfigureUIPanelModule extends JPanel
{

    protected final XMLTVConfig.ModuleInfo moduleInfo;
    protected final XMLTVConfigureUIController.TextChanged textChangedEvent;
    private JButton btnChannels = null;
    private JTextField textCommand = null;
    private JButton btnCommandReset = null;
    private JButton btnDelete = null;
    private JPanel jPanel = null;
    private JPanel jPanel1 = null;
    private JComboBox comboModules = null;
    protected final ILocalizer localizer;

    /**
     * This is the default constructor
     *
     * @param localizer DOCUMENT ME!
     * @param moduleInfo DOCUMENT ME!
     * @param textChangedEvent DOCUMENT ME!
     */
    public XMLTVConfigureUIPanelModule( 
        final ILocalizer localizer, final XMLTVConfig.ModuleInfo moduleInfo,
        final XMLTVConfigureUIController.TextChanged textChangedEvent )
    {
        super(  );
        this.localizer = localizer;
        this.moduleInfo = moduleInfo;
        this.textChangedEvent = textChangedEvent;
        this.textChangedEvent.panel = this;

        initialize(  );

    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {

        GridBagConstraints gridBagConstraints5 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );
        this.setLayout( new GridBagLayout(  ) );

        this.setBorder( 
            javax.swing.BorderFactory.createEtchedBorder( 
                javax.swing.border.EtchedBorder.RAISED ) );

        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.gridy = 1;
        gridBagConstraints21.weightx = 1.0D;
        gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridx = 0;
        gridBagConstraints5.gridy = 0;
        gridBagConstraints5.anchor = java.awt.GridBagConstraints.EAST;
        this.add( getJPanel1(  ), gridBagConstraints5 );
        this.add( getJPanel(  ), gridBagConstraints21 );
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

            btnChannels.setText( "Configure" );
            btnChannels.setMnemonic( java.awt.event.KeyEvent.VK_C );
            btnChannels.setText( 
                localizer.getLocalizedMessage( "Options.ChooseChannels" ) );
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

            btnCommandReset.setText( "Default" );
            btnCommandReset.setMnemonic( java.awt.event.KeyEvent.VK_D );
            btnCommandReset.setText( 
                localizer.getLocalizedMessage( "Options.Reset" ) );

        }

        return btnCommandReset;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnDelete(  )
    {

        if( btnDelete == null )
        {
            btnDelete = new JButton(  );
            btnDelete.setText( "Delete" );
            btnDelete.setMnemonic( java.awt.event.KeyEvent.VK_E );
            btnDelete.setText( 
                localizer.getLocalizedMessage( "Options.Remove" ) );
        }

        return btnDelete;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel(  )
    {

        if( jPanel == null )
        {

            GridBagConstraints gridBagConstraints6 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints4 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints3 =
                new GridBagConstraints(  );
            jPanel = new JPanel(  );
            jPanel.setLayout( new GridBagLayout(  ) );
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 0;
            gridBagConstraints3.insets = new Insets( 3, 3, 3, 3 );
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.gridwidth = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints4.insets = new Insets( 5, 5, 5, 5 );
            jPanel.setBorder( 
                javax.swing.BorderFactory.createEtchedBorder( 
                    javax.swing.border.EtchedBorder.RAISED ) );
            gridBagConstraints6.gridx = 0;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints6.insets = new java.awt.Insets( 3, 3, 3, 3 );
            jPanel.add( getComboModules(  ), gridBagConstraints6 );
            jPanel.add( getBtnCommandReset(  ), gridBagConstraints3 );
            jPanel.add( getTextCommand(  ), gridBagConstraints4 );
        }

        return jPanel;
    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1(  )
    {

        if( jPanel1 == null )
        {
            jPanel1 = new JPanel(  );
            jPanel1.add( getBtnChannels(  ), null );
            jPanel1.add( getBtnDelete(  ), null );
        }

        return jPanel1;
    }

    /**
     * This method initializes jComboBox
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getComboModules(  )
    {

        if( comboModules == null )
        {
            comboModules = new JComboBox(  );
        }

        return comboModules;
    }
}

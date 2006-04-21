package freeguide.plugins.grabber.vsetv;

import freeguide.common.plugininterfaces.ILocalizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * Panel for select channels.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class VsetvConfigurationUIPanel extends JPanel
{

    private JTextField textUser = null;
    private JTextField textPass = null;
    private JTextPane message = null;
    protected final ILocalizer localizer;
    private JPanel panelAuthorized = null;
    private JPanel panelNonAuthorized = null;
    private JLabel labelUser = null;
    private JLabel labelPass = null;
    private JList listChannels = null;
    private JRadioButton rbNoAuth = null;
    private JRadioButton rbAuth = null;
    private JScrollPane jScrollPane = null;
    private JCheckBox cbGetAll = null;

    /**
     * This is the default constructor
     *
     * @param localizer DOCUMENT ME!
     */
    public VsetvConfigurationUIPanel( final ILocalizer localizer )
    {
        super(  );

        this.localizer = localizer;

        initialize(  );

    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {

        ButtonGroup gr = new ButtonGroup(  );

        gr.add( getRbAuth(  ) );

        gr.add( getRbNoAuth(  ) );

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints18 = new GridBagConstraints(  );

        this.setLayout( new GridBagLayout(  ) );

        this.setSize( 300, 300 );

        gridBagConstraints18.gridx = 0;

        gridBagConstraints18.gridy = 1;

        gridBagConstraints18.fill = GridBagConstraints.BOTH;

        gridBagConstraints18.insets = new java.awt.Insets( 5, 5, 5, 5 );

        gridBagConstraints18.weightx = 1.0D;

        gridBagConstraints1.gridx = 0;

        gridBagConstraints1.gridy = 2;

        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints1.weightx = 1.0D;

        gridBagConstraints2.gridx = 0;

        gridBagConstraints2.gridy = 3;

        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints2.weightx = 1.0D;

        gridBagConstraints2.weighty = 1.0D;

        this.add( getMessage(  ), gridBagConstraints18 );

        this.add( getPanelAuthorized(  ), gridBagConstraints1 );

        this.add( getPanelNonAuthorized(  ), gridBagConstraints2 );

    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    public JTextField getTextUser(  )
    {

        if( textUser == null )
        {
            textUser = new JTextField(  );

            textUser.setColumns( 8 );

        }

        return textUser;

    }

    /**
     * This method initializes jTextField1
     *
     * @return javax.swing.JTextField
     */
    public JTextField getTextPass(  )
    {

        if( textPass == null )
        {
            textPass = new JTextField(  );

            textPass.setColumns( 8 );

        }

        return textPass;

    }

    /**
     * This method initializes jTextPane
     *
     * @return javax.swing.JTextPane
     */
    public JTextPane getMessage(  )
    {

        if( message == null )
        {
            message = new JTextPane(  );

            message.setEditable( false );

            message.setOpaque( false );

            message.setText( 
                "message sdagkn ashdkgjh asdgjhasdklgh laskhg kdfhgklsdfhjlghsdfh" );

            message.setText( 
                localizer.getLocalizedMessage( "Options.UI.Message" ) );

        }

        return message;

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelAuthorized(  )
    {

        if( panelAuthorized == null )
        {

            GridBagConstraints gridBagConstraints16 =
                new GridBagConstraints(  );

            GridBagConstraints gridBagConstraints14 =
                new GridBagConstraints(  );

            labelPass = new JLabel(  );

            labelUser = new JLabel(  );

            GridBagConstraints gridBagConstraints4 =
                new GridBagConstraints(  );

            GridBagConstraints gridBagConstraints3 =
                new GridBagConstraints(  );

            GridBagConstraints gridBagConstraints6 =
                new GridBagConstraints(  );

            GridBagConstraints gridBagConstraints7 =
                new GridBagConstraints(  );

            panelAuthorized = new JPanel(  );

            panelAuthorized.setLayout( new GridBagLayout(  ) );

            gridBagConstraints3.gridx = 1;

            gridBagConstraints3.gridy = 1;

            gridBagConstraints3.weightx = 1.0D;

            gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;

            gridBagConstraints3.insets = new java.awt.Insets( 5, 5, 5, 5 );

            gridBagConstraints4.gridx = 1;

            gridBagConstraints4.gridy = 2;

            gridBagConstraints4.weightx = 1.0D;

            gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;

            gridBagConstraints4.insets = new java.awt.Insets( 5, 5, 5, 5 );

            gridBagConstraints6.gridx = 0;

            gridBagConstraints6.gridy = 1;

            labelUser.setText( "Username" );

            labelUser.setLabelFor( getTextUser(  ) );
            labelUser.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_U );
            labelUser.setText( 
                localizer.getLocalizedMessage( "Options.UI.Username" ) );

            gridBagConstraints7.gridx = 0;

            gridBagConstraints7.gridy = 2;

            labelPass.setText( "Password" );

            labelPass.setLabelFor( getTextPass(  ) );
            labelPass.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_P );
            labelPass.setText( 
                localizer.getLocalizedMessage( "Options.UI.Password" ) );

            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;

            gridBagConstraints7.insets = new java.awt.Insets( 0, 5, 0, 0 );

            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;

            gridBagConstraints6.insets = new java.awt.Insets( 0, 5, 0, 0 );

            gridBagConstraints14.gridx = 0;

            gridBagConstraints14.gridy = 0;

            gridBagConstraints14.fill = java.awt.GridBagConstraints.HORIZONTAL;

            gridBagConstraints14.gridwidth = 2;

            panelAuthorized.setBorder( 
                javax.swing.BorderFactory.createEtchedBorder( 
                    javax.swing.border.EtchedBorder.LOWERED ) );

            gridBagConstraints16.gridx = 0;

            gridBagConstraints16.gridy = 3;

            gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;

            gridBagConstraints16.anchor = java.awt.GridBagConstraints.WEST;

            gridBagConstraints16.gridwidth = 2;

            panelAuthorized.add( labelUser, gridBagConstraints6 );

            panelAuthorized.add( labelPass, gridBagConstraints7 );

            panelAuthorized.add( getTextUser(  ), gridBagConstraints3 );

            panelAuthorized.add( getTextPass(  ), gridBagConstraints4 );

            panelAuthorized.add( getRbAuth(  ), gridBagConstraints14 );

            panelAuthorized.add( getCbGetAll(  ), gridBagConstraints16 );

        }

        return panelAuthorized;

    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelNonAuthorized(  )
    {

        if( panelNonAuthorized == null )
        {

            GridBagConstraints gridBagConstraints15 =
                new GridBagConstraints(  );

            GridBagConstraints gridBagConstraints13 =
                new GridBagConstraints(  );

            panelNonAuthorized = new JPanel(  );

            panelNonAuthorized.setLayout( new GridBagLayout(  ) );

            panelNonAuthorized.setBorder( 
                javax.swing.BorderFactory.createEtchedBorder( 
                    javax.swing.border.EtchedBorder.LOWERED ) );

            gridBagConstraints13.gridx = 0;

            gridBagConstraints13.gridy = 0;

            gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;

            gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;

            gridBagConstraints13.gridwidth = 2;

            gridBagConstraints15.gridx = 0;

            gridBagConstraints15.gridy = 1;

            gridBagConstraints15.weightx = 1.0;

            gridBagConstraints15.weighty = 1.0;

            gridBagConstraints15.fill = java.awt.GridBagConstraints.BOTH;

            panelNonAuthorized.add( getRbNoAuth(  ), gridBagConstraints13 );

            panelNonAuthorized.add( getJScrollPane(  ), gridBagConstraints15 );

        }

        return panelNonAuthorized;

    }

    /**
     * This method initializes jList
     *
     * @return javax.swing.JList
     */
    public JList getListChannels(  )
    {

        if( listChannels == null )
        {
            listChannels = new JList(  );

            listChannels.setSelectionMode( 
                javax.swing.ListSelectionModel.SINGLE_SELECTION );

        }

        return listChannels;

    }

    /**
     * This method initializes rbNoAuth
     *
     * @return javax.swing.JRadioButton
     */
    public JRadioButton getRbNoAuth(  )
    {

        if( rbNoAuth == null )
        {
            rbNoAuth = new JRadioButton(  );

            rbNoAuth.setText( "Non-Authorized" );

            rbNoAuth.setMnemonic( java.awt.event.KeyEvent.VK_N );
            rbNoAuth.setText( 
                localizer.getLocalizedMessage( "Options.UI.CB.NoAuth" ) );

        }

        return rbNoAuth;

    }

    /**
     * This method initializes rbAuth
     *
     * @return javax.swing.JRadioButton
     */
    public JRadioButton getRbAuth(  )
    {

        if( rbAuth == null )
        {
            rbAuth = new JRadioButton(  );

            rbAuth.setText( "Authorized" );

            rbAuth.setMnemonic( java.awt.event.KeyEvent.VK_A );
            rbAuth.setText( 
                localizer.getLocalizedMessage( "Options.UI.CB.Auth" ) );

        }

        return rbAuth;

    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane(  )
    {

        if( jScrollPane == null )
        {
            jScrollPane = new JScrollPane(  );

            jScrollPane.setViewportView( getListChannels(  ) );

        }

        return jScrollPane;

    }

    /**
     * This method initializes jCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getCbGetAll(  )
    {

        if( cbGetAll == null )
        {
            cbGetAll = new JCheckBox(  );

            cbGetAll.setText( "Get all site channels" );

            cbGetAll.setMnemonic( java.awt.event.KeyEvent.VK_G );
            cbGetAll.setText( 
                localizer.getLocalizedMessage( "Options.UI.Auth.GetAll" ) );

        }

        return cbGetAll;

    }
}

package freeguide.plugins.program.freeguide.updater;

import freeguide.common.lib.fgspecific.Application;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * Update UI frame.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class UpdaterUI extends JDialog
{
    private JPanel jContentPane = null;
    private JLabel labelTitle = null;
    private JPanel panelButtons = null;
    private JTable tablePackages = null;
    private JScrollPane jScrollPane = null;
    private JButton btnCheck = null;
    private JButton btnGo = null;
    private JButton btnClose = null;
    private JPanel jPanel = null;
    private JLabel labelMirror = null;
    private JComboBox cbMirror = null;

/**
     * This is the default constructor
     *
     * @param parent DOCUMENT ME!
     */
    public UpdaterUI( final JFrame parent )
    {
        super( parent, true );
        initialize(  );
    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {
        this.setModal( true );
        this.setTitle( "Plugins update manager" );
        this.setTitle( 
            Application.getInstance(  )
                       .getLocalizedMessage( "UpdateManager.Header" ) );
        this.setDefaultCloseOperation( 
            javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
        this.setSize( new java.awt.Dimension( 400, 200 ) );
        this.setContentPane( getJContentPane(  ) );
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane(  )
    {
        if( jContentPane == null )
        {
            GridBagConstraints gridBagConstraints2 =
                new GridBagConstraints(  );
            gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints2.gridx = 1;
            gridBagConstraints2.gridy = 1;
            gridBagConstraints2.weightx = 0.5D;
            gridBagConstraints2.insets = new java.awt.Insets( 5, 5, 0, 5 );

            GridBagConstraints gridBagConstraints11 =
                new GridBagConstraints(  );
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.gridy = 1;
            gridBagConstraints11.weightx = 0.5D;
            gridBagConstraints11.insets = new java.awt.Insets( 5, 5, 0, 0 );
            gridBagConstraints11.anchor = java.awt.GridBagConstraints.EAST;
            labelMirror = new JLabel(  );
            labelMirror.setText( 
                Application.getInstance(  )
                           .getLocalizedMessage( "UpdateManager.MirrorPrompt" ) );

            GridBagConstraints gridBagConstraints4 =
                new GridBagConstraints(  );
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.gridy = 2;
            gridBagConstraints4.weightx = 1.0;
            gridBagConstraints4.weighty = 1.0D;
            gridBagConstraints4.insets = new java.awt.Insets( 5, 5, 5, 5 );

            gridBagConstraints4.gridwidth = 2;

            GridBagConstraints gridBagConstraints3 =
                new GridBagConstraints(  );
            gridBagConstraints3.gridx = 0;
            gridBagConstraints3.gridy = 4;
            gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints3.weightx = 1.0D;

            gridBagConstraints3.gridwidth = 2;

            GridBagConstraints gridBagConstraints1 =
                new GridBagConstraints(  );
            gridBagConstraints1.gridx = 0;
            gridBagConstraints1.gridy = 0;
            gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints1.weightx = 1.0D;
            gridBagConstraints1.insets = new java.awt.Insets( 5, 5, 0, 5 );
            gridBagConstraints1.gridwidth = 2;
            labelTitle = new JLabel(  );
            labelTitle.setText( 
                Application.getInstance(  )
                           .getLocalizedMessage( "UpdateManager.Prompt" ) );
            jContentPane = new JPanel(  );
            jContentPane.setLayout( new GridBagLayout(  ) );
            jContentPane.add( getCbMirror(  ), gridBagConstraints2 );
            jContentPane.add( labelMirror, gridBagConstraints11 );
            jContentPane.add( getJScrollPane(  ), gridBagConstraints4 );
            jContentPane.add( labelTitle, gridBagConstraints1 );
            jContentPane.add( getPanelButtons(  ), gridBagConstraints3 );
        }

        return jContentPane;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelButtons(  )
    {
        if( panelButtons == null )
        {
            GridBagConstraints gridBagConstraints8 =
                new GridBagConstraints(  );
            gridBagConstraints8.gridx = 1;
            gridBagConstraints8.gridy = 0;
            gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints8.weightx = 1.0D;

            GridBagConstraints gridBagConstraints7 =
                new GridBagConstraints(  );
            gridBagConstraints7.gridx = 4;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.insets = new java.awt.Insets( 5, 5, 5, 5 );

            GridBagConstraints gridBagConstraints6 =
                new GridBagConstraints(  );
            gridBagConstraints6.gridx = 3;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.insets = new java.awt.Insets( 5, 5, 5, 0 );

            GridBagConstraints gridBagConstraints5 =
                new GridBagConstraints(  );
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.gridy = 0;
            gridBagConstraints5.insets = new java.awt.Insets( 5, 5, 5, 5 );
            panelButtons = new JPanel(  );
            panelButtons.setLayout( new GridBagLayout(  ) );
            panelButtons.add( getBtnClose(  ), gridBagConstraints7 );
            panelButtons.add( getJPanel(  ), gridBagConstraints8 );
            panelButtons.add( getBtnCheck(  ), gridBagConstraints5 );
            panelButtons.add( getBtnGo(  ), gridBagConstraints6 );
        }

        return panelButtons;
    }

    /**
     * This method initializes jTable
     *
     * @return javax.swing.JTable
     */
    public JTable getTablePackages(  )
    {
        if( tablePackages == null )
        {
            tablePackages = new JTable(  );
        }

        return tablePackages;
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
            jScrollPane.setViewportView( getTablePackages(  ) );
        }

        return jScrollPane;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnCheck(  )
    {
        if( btnCheck == null )
        {
            btnCheck = new JButton(  );
            btnCheck.setText( "Check repository" );
            btnCheck.setText( 
                Application.getInstance(  )
                           .getLocalizedMessage( "UpdateManager.Button.Check" ) );
        }

        return btnCheck;
    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnGo(  )
    {
        if( btnGo == null )
        {
            btnGo = new JButton(  );
            btnGo.setText( "Update" );
            btnGo.setText( 
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "UpdateManager.Button.Update" ) );
        }

        return btnGo;
    }

    /**
     * This method initializes jButton2
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnClose(  )
    {
        if( btnClose == null )
        {
            btnClose = new JButton(  );
            btnClose.setText( "Close" );
            btnClose.setText( 
                Application.getInstance(  )
                           .getLocalizedMessage( "UpdateManager.Button.Close" ) );
        }

        return btnClose;
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
            jPanel = new JPanel(  );
        }

        return jPanel;
    }

    /**
     * This method initializes jComboBox
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getCbMirror(  )
    {
        if( cbMirror == null )
        {
            cbMirror = new JComboBox(  );
        }

        return cbMirror;
    }
}

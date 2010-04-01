package freeguide.common.gui;

import freeguide.common.lib.fgspecific.Application;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Provides a list of the user's favourites and allows them to add or edit
 * them by launching a FreeGuideFavouriteEditor.
 *
 * @author Alex Buloichik (alex73 at zaval.org) (based on FavouritesListDialog
 *         by Brendan Corrigan)
 */
public class FavouritesListDialog extends JDialog
{
    private javax.swing.JPanel jContentPane = null;
    private JPanel jPanel = null;
    private JButton btnOK = null;
    private JButton btnCancel = null;
    private JPanel jPanel1 = null;
    private JButton btnRemove = null;
    private JButton btnAdd = null;
    private JButton btnEdit = null;
    private JPanel jPanel2 = null;
    private JPanel jPanel3 = null;
    private JScrollPane jScrollPane = null;
    private JList list = null;
    private JPanel jPanel4 = null;

    /**
     * This is the default constructor
     *
     * @param owner DOCUMENT ME!
     */
    public FavouritesListDialog( JFrame owner )
    {
        super(
            owner,
            Application.getInstance(  ).getLocalizedMessage( "favourites" ),
            true );
        initialize(  );
        getRootPane(  ).setDefaultButton( btnOK );
    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {
        this.setSize( 400, 220 );
        this.setContentPane( getJContentPane(  ) );
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane(  )
    {
        if( jContentPane == null )
        {
            jContentPane = new javax.swing.JPanel(  );
            jContentPane.setLayout( new java.awt.BorderLayout(  ) );
            jContentPane.add( getJPanel(  ), java.awt.BorderLayout.SOUTH );
            jContentPane.add( getJPanel1(  ), java.awt.BorderLayout.CENTER );
        }

        return jContentPane;
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
            GridBagConstraints gridBagConstraints11 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints10 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints9 =
                new GridBagConstraints(  );
            jPanel = new JPanel(  );
            jPanel.setLayout( new GridBagLayout(  ) );
            gridBagConstraints9.gridx = 1;
            gridBagConstraints9.gridy = 0;
            gridBagConstraints9.insets = new java.awt.Insets( 5, 5, 5, 5 );
            gridBagConstraints10.gridx = 2;
            gridBagConstraints10.gridy = 0;
            gridBagConstraints10.insets = new java.awt.Insets( 5, 5, 5, 5 );
            gridBagConstraints11.gridx = 0;
            gridBagConstraints11.gridy = 0;
            gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints11.weightx = 1.0D;
            jPanel.add( getBtnOK(  ), gridBagConstraints9 );
            jPanel.add( getBtnCancel(  ), gridBagConstraints10 );
            jPanel.add( getJPanel4(  ), gridBagConstraints11 );
        }

        return jPanel;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnOK(  )
    {
        if( btnOK == null )
        {
            btnOK = new JButton(  );
            btnOK.setMinimumSize( new java.awt.Dimension( 87, 26 ) );
            btnOK.setPreferredSize( new java.awt.Dimension( 87, 26 ) );
            btnOK.setText(
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );
        }

        return btnOK;
    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnCancel(  )
    {
        if( btnCancel == null )
        {
            btnCancel = new JButton(  );
            btnCancel.setMinimumSize( new java.awt.Dimension( 87, 26 ) );
            btnCancel.setPreferredSize( new java.awt.Dimension( 87, 26 ) );
            btnCancel.setText(
                Application.getInstance(  ).getLocalizedMessage( "cancel" ) );
        }

        return btnCancel;
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
            GridBagConstraints gridBagConstraints8 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints7 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints6 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints5 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints4 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints3 =
                new GridBagConstraints(  );
            jPanel1 = new JPanel(  );
            jPanel1.setLayout( new GridBagLayout(  ) );
            gridBagConstraints3.gridx = 1;
            gridBagConstraints3.gridy = 3;
            gridBagConstraints3.insets = new java.awt.Insets( 5, 5, 5, 5 );
            gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints4.gridx = 1;
            gridBagConstraints4.gridy = 1;
            gridBagConstraints4.insets = new java.awt.Insets( 5, 5, 5, 5 );
            gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints5.gridx = 1;
            gridBagConstraints5.gridy = 2;
            gridBagConstraints5.insets = new java.awt.Insets( 5, 5, 5, 5 );
            gridBagConstraints5.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints6.gridx = 1;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weighty = 0.5D;
            gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints7.gridx = 1;
            gridBagConstraints7.gridy = 4;
            gridBagConstraints7.weighty = 0.5D;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.gridy = 0;
            gridBagConstraints8.weightx = 1.0;
            gridBagConstraints8.weighty = 1.0;
            gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.gridheight = 5;
            gridBagConstraints8.insets = new java.awt.Insets( 5, 5, 5, 5 );
            jPanel1.add( getBtnRemove(  ), gridBagConstraints3 );
            jPanel1.add( getBtnAdd(  ), gridBagConstraints4 );
            jPanel1.add( getBtnEdit(  ), gridBagConstraints5 );
            jPanel1.add( getJPanel2(  ), gridBagConstraints6 );
            jPanel1.add( getJPanel3(  ), gridBagConstraints7 );
            jPanel1.add( getJScrollPane(  ), gridBagConstraints8 );
        }

        return jPanel1;
    }

    /**
     * This method initializes jButton2
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnRemove(  )
    {
        if( btnRemove == null )
        {
            btnRemove = new JButton(  );
            btnRemove.setText(
                Application.getInstance(  ).getLocalizedMessage( "remove" ) );
        }

        return btnRemove;
    }

    /**
     * This method initializes jButton3
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnAdd(  )
    {
        if( btnAdd == null )
        {
            btnAdd = new JButton(  );
            btnAdd.setText(
                Application.getInstance(  ).getLocalizedMessage( "add" ) );
        }

        return btnAdd;
    }

    /**
     * This method initializes jButton4
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnEdit(  )
    {
        if( btnEdit == null )
        {
            btnEdit = new JButton(  );
            btnEdit.setText(
                Application.getInstance(  ).getLocalizedMessage( "edit" ) );
        }

        return btnEdit;
    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel2(  )
    {
        if( jPanel2 == null )
        {
            jPanel2 = new JPanel(  );
        }

        return jPanel2;
    }

    /**
     * This method initializes jPanel3
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel3(  )
    {
        if( jPanel3 == null )
        {
            jPanel3 = new JPanel(  );
        }

        return jPanel3;
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
            jScrollPane.setViewportView( getList(  ) );
        }

        return jScrollPane;
    }

    /**
     * This method initializes jList
     *
     * @return javax.swing.JList
     */
    protected JList getList(  )
    {
        if( list == null )
        {
            list = new JList(  );
        }

        return list;
    }

    /**
     * This method initializes jPanel4
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel4(  )
    {
        if( jPanel4 == null )
        {
            jPanel4 = new JPanel(  );
        }

        return jPanel4;
    }
}

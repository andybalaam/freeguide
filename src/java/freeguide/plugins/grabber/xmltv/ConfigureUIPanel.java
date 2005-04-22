/*
 * Created on 22.04.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package freeguide.plugins.grabber.xmltv;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * DOCUMENT ME!
 *
 * @author alex TODO To change the template for this generated type comment go
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class ConfigureUIPanel extends JScrollPane
{

    private JPanel jPanel = null;
    private JButton btnAdd = null;
    private JPanel panelModules = null;

    /**
     * This is the default constructor
     */
    public ConfigureUIPanel(  )
    {
        super(  );
        initialize(  );
    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {
        this.setViewportView( getJPanel(  ) );
        this.setHorizontalScrollBarPolicy( 
            javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        this.setSize( 300, 200 );
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

            GridBagConstraints gridBagConstraints9 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints8 =
                new GridBagConstraints(  );
            GridBagConstraints gridBagConstraints7 =
                new GridBagConstraints(  );
            jPanel = new JPanel(  );
            jPanel.setLayout( new GridBagLayout(  ) );
            gridBagConstraints7.gridx = 0;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.NORTHEAST;
            gridBagConstraints7.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints7.insets = new java.awt.Insets( 5, 5, 5, 5 );
            gridBagConstraints8.gridx = 0;
            gridBagConstraints8.gridy = 1;
            gridBagConstraints8.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints8.weightx = 1.0D;
            gridBagConstraints9.gridx = 0;
            gridBagConstraints9.gridy = 1;
            gridBagConstraints9.weightx = 1.0D;
            gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints9.weighty = 1.0D;
            jPanel.add( getBtnAdd(  ), gridBagConstraints7 );
            jPanel.add( getPanelModules(  ), gridBagConstraints9 );
        }

        return jPanel;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnAdd(  )
    {

        if( btnAdd == null )
        {
            btnAdd = new JButton(  );
            btnAdd.setText( "Add" );
        }

        return btnAdd;
    }

    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    public JPanel getPanelModules(  )
    {

        if( panelModules == null )
        {
            panelModules = new JPanel(  );
            panelModules.setLayout( new GridBagLayout(  ) );
        }

        return panelModules;
    }
}

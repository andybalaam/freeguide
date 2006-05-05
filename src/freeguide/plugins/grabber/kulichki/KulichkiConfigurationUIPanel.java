package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.fgspecific.TVChannelsTree;

import freeguide.common.plugininterfaces.ILocalizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class KulichkiConfigurationUIPanel extends JPanel
{
    private final ILocalizer localizer;
    private JTextPane textDescription = null;
    private JButton btnRefresh = null;
    private JScrollPane jScrollPane = null;
    private TVChannelsTree treeChannels = null;

/**
     * This is the default constructor
     *
     * @param localizer DOCUMENT ME!
     */
    public KulichkiConfigurationUIPanel( final ILocalizer localizer )
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
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );

        this.setLayout( new GridBagLayout(  ) );

        this.setSize( 300, 200 );

        gridBagConstraints1.gridx = 0;

        gridBagConstraints1.gridy = 0;

        gridBagConstraints1.weightx = 1.0;

        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints2.gridx = 1;

        gridBagConstraints2.gridy = 0;

        gridBagConstraints2.insets = new java.awt.Insets( 5, 5, 5, 5 );

        gridBagConstraints4.gridx = 0;

        gridBagConstraints4.gridy = 1;

        gridBagConstraints4.weightx = 1.0;

        gridBagConstraints4.weighty = 1.0;

        gridBagConstraints4.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints4.gridwidth = 2;

        this.add( getTextDescription(  ), gridBagConstraints1 );

        this.add( getBtnRefresh(  ), gridBagConstraints2 );

        this.add( getJScrollPane(  ), gridBagConstraints4 );

    }

    /**
     * This method initializes jTextPane
     *
     * @return javax.swing.JTextPane
     */
    private JTextPane getTextDescription(  )
    {
        if( textDescription == null )
        {
            textDescription = new JTextPane(  );

            textDescription.setOpaque( false );

            textDescription.setText( "description" );

        }

        return textDescription;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnRefresh(  )
    {
        if( btnRefresh == null )
        {
            btnRefresh = new JButton(  );

            btnRefresh.setText( "Refresh" );

        }

        return btnRefresh;

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

            jScrollPane.setViewportView( getTreeChannels(  ) );

        }

        return jScrollPane;

    }

    /**
     * This method initializes jTree
     *
     * @return javax.swing.JTree
     */
    public TVChannelsTree getTreeChannels(  )
    {
        if( treeChannels == null )
        {
            treeChannels = new TVChannelsTree(  );

        }

        return treeChannels;

    }
}
//  @jve:decl-index=0:

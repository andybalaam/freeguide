package freeguide.plugins.importexport.palmatv;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class PalmUIPanel extends JPanel
{
    private JComboBox cbCharset = null;
    private JLabel jLabel = null;

/**
     * This is the default constructor
     *
     * @param parent DOCUMENT ME!
     */
    public PalmUIPanel( final ResourceBundle i18n )
    {
        super(  );
        initialize( i18n );
    }

    /**
     * This method initializes this
     *
     * @param i18n DOCUMENT ME!
     */
    private void initialize( final ResourceBundle i18n )
    {
        GridBagConstraints gridBagConstraints = new GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 0 );
        gridBagConstraints.gridy = 0;
        jLabel = new JLabel(  );
        jLabel.setText( i18n.getString( "label.Text" ) + ":" );
        jLabel.setLabelFor( getCbCharset(  ) );

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints1.gridx = 1;
        this.setLayout( new GridBagLayout(  ) );
        this.setSize( 300, 200 );
        this.add( getCbCharset(  ), gridBagConstraints1 );
        this.add( jLabel, gridBagConstraints );
    }

    /**
     * This method initializes cbCharset
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getCbCharset(  )
    {
        if( cbCharset == null )
        {
            cbCharset = new JComboBox(  );
        }

        return cbCharset;
    }
}

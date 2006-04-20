package freeguide.plugins.importexport.palmatv;

import freeguide.common.plugins.ILocalizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

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
    protected ILocalizer localizer;

    /**
     * This is the default constructor
     *
     * @param parent DOCUMENT ME!
     */
    public PalmUIPanel( final ExportPalmAtv parent )
    {
        super(  );
        localizer = parent.getLocalizer(  );
        initialize(  );
    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {

        GridBagConstraints gridBagConstraints = new GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 0 );
        gridBagConstraints.gridy = 0;
        jLabel = new JLabel(  );
        jLabel.setText( localizer.getLocalizedMessage( "label.Text" ) + ":" );
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

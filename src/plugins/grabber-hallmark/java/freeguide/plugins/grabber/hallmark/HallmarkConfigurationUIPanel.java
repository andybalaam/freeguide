package freeguide.plugins.grabber.hallmark;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HallmarkConfigurationUIPanel extends JPanel
{

    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JComboBox cbCountry = null;
    private JComboBox cbLanguage = null;
    private JLabel jLabel2 = null;
    private JTextField textWeeks = null;

    /**
     * This is the default constructor
     */
    public HallmarkConfigurationUIPanel(  )
    {
        super(  );
        initialize(  );
    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {

        GridBagConstraints gridBagConstraints5 = new GridBagConstraints(  );
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridy = 2;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints5.gridx = 1;

        GridBagConstraints gridBagConstraints4 = new GridBagConstraints(  );
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.insets = new java.awt.Insets( 5, 5, 5, 0 );
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.gridy = 2;
        jLabel2 = new JLabel(  );
        jLabel2.setText( "Weeks for load" );
        jLabel2.setLabelFor( getTextWeeks(  ) );

        GridBagConstraints gridBagConstraints3 = new GridBagConstraints(  );
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridy = 1;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints3.gridx = 1;

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints(  );
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints2.gridx = 1;

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.gridy = 1;
        jLabel1 = new JLabel(  );
        jLabel1.setText( "Language:" );
        jLabel1.setLabelFor( getCbLanguage(  ) );

        GridBagConstraints gridBagConstraints = new GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.gridy = 0;
        jLabel = new JLabel(  );
        jLabel.setText( "Country:" );
        jLabel.setLabelFor( getCbCountry(  ) );
        this.setLayout( new GridBagLayout(  ) );
        this.setSize( 300, 200 );
        this.add( jLabel, gridBagConstraints );
        this.add( jLabel1, gridBagConstraints1 );
        this.add( getCbCountry(  ), gridBagConstraints2 );
        this.add( getCbLanguage(  ), gridBagConstraints3 );
        this.add( jLabel2, gridBagConstraints4 );
        this.add( getTextWeeks(  ), gridBagConstraints5 );
    }

    /**
     * This method initializes cbCountry
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getCbCountry(  )
    {

        if( cbCountry == null )
        {
            cbCountry = new JComboBox(  );
        }

        return cbCountry;
    }

    /**
     * This method initializes cbLanguage
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getCbLanguage(  )
    {

        if( cbLanguage == null )
        {
            cbLanguage = new JComboBox(  );
        }

        return cbLanguage;
    }

    /**
     * This method initializes textWeeks
     *
     * @return javax.swing.JTextField
     */
    public JTextField getTextWeeks(  )
    {

        if( textWeeks == null )
        {
            textWeeks = new JTextField(  );
        }

        return textWeeks;
    }
}

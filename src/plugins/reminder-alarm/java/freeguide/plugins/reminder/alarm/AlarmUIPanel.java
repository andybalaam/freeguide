package freeguide.plugins.reminder.alarm;

import freeguide.plugins.ILocalizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AlarmUIPanel extends JPanel
{

    private JCheckBox cbRemind = null;
    private JLabel jLabel = null;
    private JTextField textWarning = null;
    private JLabel jLabel1 = null;
    private JTextField textGiveup = null;
    private ILocalizer localizer;
    private JLabel jLabel2 = null;
    private JButton btnColorInGuide = null;
    private JPanel panelColorInGuide = null;

    /**
     * This is the default constructor
     *
     * @param localizer DOCUMENT ME!
     */
    public AlarmUIPanel( final ILocalizer localizer )
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

        GridBagConstraints gridBagConstraints51 = new GridBagConstraints(  );
        gridBagConstraints51.gridx = 1;
        gridBagConstraints51.gridy = 0;
        gridBagConstraints51.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints51.insets = new java.awt.Insets( 5, 5, 0, 0 );

        GridBagConstraints gridBagConstraints41 = new GridBagConstraints(  );
        gridBagConstraints41.gridx = 2;
        gridBagConstraints41.gridy = 1;
        gridBagConstraints41.insets = new java.awt.Insets( 5, 5, 0, 5 );

        GridBagConstraints gridBagConstraints31 = new GridBagConstraints(  );
        gridBagConstraints31.gridx = 2;
        gridBagConstraints31.gridy = 0;
        gridBagConstraints31.insets = new java.awt.Insets( 5, 5, 0, 5 );

        GridBagConstraints gridBagConstraints21 = new GridBagConstraints(  );
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.gridy = 1;
        gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints21.insets = new java.awt.Insets( 5, 5, 0, 0 );

        GridBagConstraints gridBagConstraints11 = new GridBagConstraints(  );
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.gridy = 0;
        gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints11.insets = new java.awt.Insets( 5, 5, 0, 0 );
        jLabel2 = new JLabel(  );
        jLabel2.setText( "inguide_colour" );
        jLabel2.setText( 
            localizer.getLocalizedMessage( "options.inguide_colour" ) );

        GridBagConstraints gridBagConstraints5 = new GridBagConstraints(  );
        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints5.gridx = 1;
        gridBagConstraints5.gridy = 4;
        gridBagConstraints5.weightx = 1.0;
        gridBagConstraints5.insets = new java.awt.Insets( 5, 5, 5, 5 );

        GridBagConstraints gridBagConstraints4 = new GridBagConstraints(  );
        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 4;
        gridBagConstraints4.insets = new java.awt.Insets( 5, 5, 5, 0 );
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        jLabel1 = new JLabel(  );
        jLabel1.setText( "Give up after" );
        jLabel1.setText( 
            localizer.getLocalizedMessage( "options.give_up_after_secs" ) );
        jLabel1.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_G );
        jLabel1.setLabelFor( getTextGiveup(  ) );

        GridBagConstraints gridBagConstraints3 = new GridBagConstraints(  );
        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridy = 3;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.insets = new java.awt.Insets( 5, 5, 0, 5 );

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints(  );
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 3;
        gridBagConstraints2.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        jLabel = new JLabel(  );
        jLabel.setText( "Seconds warning" );
        jLabel.setText( 
            localizer.getLocalizedMessage( "options.seconds_warning" ) );
        jLabel.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_W );
        jLabel.setLabelFor( getTextWarning(  ) );

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.insets = new java.awt.Insets( 5, 5, 0, 5 );
        this.setLayout( new GridBagLayout(  ) );
        this.setSize( 300, 200 );
        this.add( getPanelColorInGuide(  ), gridBagConstraints51 );
        this.add( getBtnColorInGuide(  ), gridBagConstraints31 );
        this.add( jLabel2, gridBagConstraints11 );
        this.add( getCbRemind(  ), gridBagConstraints1 );
        this.add( jLabel, gridBagConstraints2 );
        this.add( getTextWarning(  ), gridBagConstraints3 );
        this.add( jLabel1, gridBagConstraints4 );
        this.add( getTextGiveup(  ), gridBagConstraints5 );
    }

    /**
     * This method initializes jCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getCbRemind(  )
    {

        if( cbRemind == null )
        {
            cbRemind = new JCheckBox(  );
            cbRemind.setText( "Remind me of progs" );
            cbRemind.setText( 
                localizer.getLocalizedMessage( "options.remind_me_of_progs" ) );
            cbRemind.setMnemonic( java.awt.event.KeyEvent.VK_R );
        }

        return cbRemind;
    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    public JTextField getTextWarning(  )
    {

        if( textWarning == null )
        {
            textWarning = new JTextField(  );
        }

        return textWarning;
    }

    /**
     * This method initializes jTextField1
     *
     * @return javax.swing.JTextField
     */
    public JTextField getTextGiveup(  )
    {

        if( textGiveup == null )
        {
            textGiveup = new JTextField(  );
        }

        return textGiveup;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnColorInGuide(  )
    {

        if( btnColorInGuide == null )
        {
            btnColorInGuide = new JButton(  );
            btnColorInGuide.setText( "..." );
        }

        return btnColorInGuide;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    public JPanel getPanelColorInGuide(  )
    {

        if( panelColorInGuide == null )
        {
            panelColorInGuide = new JPanel(  );
        }

        return panelColorInGuide;
    }
}

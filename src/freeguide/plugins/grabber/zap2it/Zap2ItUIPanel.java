package freeguide.plugins.grabber.zap2it;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class Zap2ItUIPanel extends JPanel
{
    protected JTextField textUser;
    protected JTextField textPass;
    protected SpinnerNumberModel daysLoad;

    /**
     * Creates a new Zap2ItUIPanel object.
     *
     * @param i18n DOCUMENT ME!
     */
    public Zap2ItUIPanel( final ResourceBundle i18n )
    {
        super(  );

        initialize( i18n );
    }

    protected void initialize( final ResourceBundle i18n )
    {
        textUser = new JTextField(  );
        textUser.setColumns( 20 );
        textPass = new JTextField(  );
        textPass.setColumns( 20 );

        setLayout( new GridBagLayout(  ) );

        GridBagConstraints gbcLabel = new GridBagConstraints(  );
        gbcLabel.gridx = 0;
        gbcLabel.insets = new Insets( 0, 0, 5, 5 );
        gbcLabel.anchor = GridBagConstraints.WEST;

        GridBagConstraints gbcInput = new GridBagConstraints(  );
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.gridx = 1;
        gbcInput.insets = new Insets( 0, 0, 5, 5 );

        gbcLabel.gridy = 0;
        add( 
            new JLabel( i18n.getString( "Configuration.Panel.Username" ) ),
            gbcLabel );

        gbcLabel.gridy = 1;
        add( 
            new JLabel( i18n.getString( "Configuration.Panel.Password" ) ),
            gbcLabel );

        gbcLabel.gridy = 2;
        add( 
            new JLabel( i18n.getString( "Configuration.Panel.Days" ) ),
            gbcLabel );

        gbcInput.gridy = 0;
        add( textUser, gbcInput );
        gbcInput.gridy = 1;
        add( textPass, gbcInput );

        daysLoad = new SpinnerNumberModel( 1, 1, 30, 1 );

        JSpinner sp = new JSpinner(  );
        sp.setModel( daysLoad );
        sp.setFont( getFont(  ) );

        gbcInput.gridy = 2;
        add( sp, gbcInput );
    }
}

package freeguide.plugins.reminder.advanced;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class OneReminderPanel extends JPanel
{
    protected JButton btnDelete;
    protected JTextField txtName;
    protected JTextField tmPopupShow;
    protected JTextField tmPopupHide;
    protected JTextField tmSound;
    protected JTextField txtSoundFile;
    protected JTextField tmExecuteStart;
    protected JTextField tmExecuteStop;
    protected JTextField txtExecuteStart;
    protected JTextField txtExecuteStop;
    protected JCheckBox cbPopup;
    protected JCheckBox cbSound;
    protected JCheckBox cbExecute;
    protected OneReminderConfig config;

/**
     * Creates a new OneReminderPanel object.
     */
    public OneReminderPanel( final OneReminderConfig config )
    {
        btnDelete = new JButton( "Delete" );
        txtName = new JTextField(  );
        tmPopupShow = new JTextField(  );
        tmPopupHide = new JTextField(  );
        tmSound = new JTextField(  );
        txtSoundFile = new JTextField(  );
        tmExecuteStart = new JTextField(  );
        txtExecuteStart = new JTextField(  );
        tmExecuteStop = new JTextField(  );
        txtExecuteStop = new JTextField(  );

        tmPopupShow.setColumns( 5 );

        JLabel label;

        setLayout( new GridBagLayout(  ) );

        GridBagConstraints gbcLabel = new GridBagConstraints(  );
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.insets = new Insets( 0, 5, 0, 5 );

        GridBagConstraints gbcLabelWide = new GridBagConstraints(  );
        gbcLabelWide.anchor = GridBagConstraints.WEST;
        gbcLabelWide.insets = new Insets( 0, 5, 0, 5 );
        gbcLabelWide.gridwidth = 4;

        GridBagConstraints gbcInput = new GridBagConstraints(  );
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.insets = new Insets( 0, 5, 0, 5 );
        gbcInput.weightx = 1;

        GridBagConstraints gbcInputTime = new GridBagConstraints(  );
        gbcInputTime.fill = GridBagConstraints.HORIZONTAL;
        gbcInputTime.insets = new Insets( 0, 5, 0, 5 );

        GridBagConstraints gbcCheckBox = new GridBagConstraints(  );
        gbcCheckBox.fill = GridBagConstraints.HORIZONTAL;
        gbcCheckBox.anchor = GridBagConstraints.WEST;
        gbcCheckBox.insets = new Insets( 0, 5, 0, 5 );
        gbcCheckBox.gridx = 0;
        gbcCheckBox.gridwidth = 4;

        GridBagConstraints gbcBtnDelete = new GridBagConstraints(  );
        gbcBtnDelete.anchor = GridBagConstraints.EAST;
        gbcBtnDelete.insets = new Insets( 5, 5, 5, 5 );
        gbcBtnDelete.gridx = 0;
        gbcBtnDelete.gridwidth = 4;

        int line = 0;

        gbcBtnDelete.gridy = line;
        add( btnDelete, gbcBtnDelete );

        line++;

        label = new JLabel( "Name:" );
        gbcLabel.gridx = 0;
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInput.gridx = 1;
        gbcInput.gridy = line;
        gbcInput.gridwidth = 3;
        add( txtName, gbcInput );
        gbcInput.gridwidth = 1;

        line++;

        cbPopup = new JCheckBox( "Show popup" );
        gbcCheckBox.gridy = line;
        add( cbPopup, gbcCheckBox );

        line++;

        label = new JLabel( "Show before start programme" );
        gbcLabelWide.gridy = line;
        add( label, gbcLabelWide );

        line++;

        label = new JLabel( "Time" );
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInputTime.gridx = 1;
        gbcInputTime.gridy = line;
        add( tmPopupShow, gbcInputTime );

        line++;

        label = new JLabel( "Hide after showing" );
        gbcLabelWide.gridy = line;
        add( label, gbcLabelWide );

        line++;

        label = new JLabel( "Time" );
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInputTime.gridx = 1;
        gbcInputTime.gridy = line;
        add( tmPopupHide, gbcInputTime );

        line++;

        cbSound = new JCheckBox( "Play sound" );
        gbcCheckBox.gridy = line;
        add( cbSound, gbcCheckBox );

        line++;

        label = new JLabel( "Play before start programme" );
        gbcLabelWide.gridy = line;
        add( label, gbcLabelWide );

        line++;

        label = new JLabel( "Time" );
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInputTime.gridx = 1;
        gbcInputTime.gridy = line;
        add( tmSound, gbcInputTime );

        label = new JLabel( "File" );
        gbcLabel.gridx = 2;
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInput.gridx = 3;
        gbcInput.gridy = line;
        add( txtSoundFile, gbcInput );

        line++;

        cbExecute = new JCheckBox( "Execute command" );
        gbcCheckBox.gridy = line;
        add( cbExecute, gbcCheckBox );

        line++;

        label = new JLabel( "Execute before start programme" );
        gbcLabelWide.gridy = line;
        add( label, gbcLabelWide );

        line++;

        label = new JLabel( "Time" );
        gbcLabel.gridx = 0;
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInputTime.gridx = 1;
        gbcInputTime.gridy = line;
        add( tmExecuteStart, gbcInputTime );

        label = new JLabel( "Command" );
        gbcLabel.gridx = 2;
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInput.gridx = 3;
        gbcInput.gridy = line;
        add( txtExecuteStart, gbcInput );

        line++;

        label = new JLabel( "Execute after finish programme" );
        gbcLabelWide.gridy = line;
        add( label, gbcLabelWide );

        line++;

        label = new JLabel( "Time" );
        gbcLabel.gridx = 0;
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInputTime.gridx = 1;
        gbcInputTime.gridy = line;
        add( tmExecuteStop, gbcInputTime );

        label = new JLabel( "Command" );
        gbcLabel.gridx = 2;
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInput.gridx = 3;
        gbcInput.gridy = line;
        gbcInput.insets = new Insets( 0, 5, 5, 5 );
        add( txtExecuteStop, gbcInput );

        setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
    }
}

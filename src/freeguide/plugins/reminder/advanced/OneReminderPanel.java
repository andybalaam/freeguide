package freeguide.plugins.reminder.advanced;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
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
    protected JComboBox cbIcons;
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
        Properties iconsList = new Properties(  );

        try
        {
            iconsList.load( 
                getClass(  ).getClassLoader(  )
                    .getResourceAsStream( 
                    AdvancedReminder.RESOURCES_PREFIX + "iconsList.properties" ) );
        }
        catch( IOException ex )
        {
        }

        final List<CBItem> items = new ArrayList<CBItem>( iconsList.size(  ) );

        for( final String fn : AdvancedReminder.getImagesNames(  ) )
        {
            items.add( new CBItem( fn, AdvancedReminder.getImage( fn ) ) );
        }

        cbIcons = new JComboBox( items.toArray( new CBItem[items.size(  )] ) );

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

        line++;

        label = new JLabel( "Icon" );
        gbcLabel.gridx = 0;
        gbcLabel.gridy = line;
        add( label, gbcLabel );

        gbcInput.gridx = 1;
        gbcInput.gridy = line;
        gbcInput.insets = new Insets( 0, 5, 5, 5 );
        add( cbIcons, gbcInput );

        cbIcons.setRenderer( new IconsRenderer(  ) );

        setBorder( BorderFactory.createEtchedBorder( EtchedBorder.RAISED ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param name DOCUMENT_ME!
     */
    public void setIcon( final String name )
    {
        for( int i = 0; i < cbIcons.getItemCount(  ); i++ )
        {
            final CBItem item = (CBItem)cbIcons.getItemAt( i );

            if( item.key.equals( name ) )
            {
                cbIcons.setSelectedIndex( i );

                break;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class CBItem
    {
        protected String key;
        protected ImageIcon value;

/**
         * Creates a new CBItem object.
         *
         * @param key DOCUMENT ME!
         * @param value DOCUMENT ME!
         */
        public CBItem( final String key, final ImageIcon value )
        {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class IconsRenderer extends JLabel
        implements ListCellRenderer
    {
        /**
         * DOCUMENT_ME!
         *
         * @param list DOCUMENT_ME!
         * @param value DOCUMENT_ME!
         * @param index DOCUMENT_ME!
         * @param isSelected DOCUMENT_ME!
         * @param cellHasFocus DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public Component getListCellRendererComponent( 
            JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus )
        {
            if( isSelected )
            {
                setBackground( list.getSelectionBackground(  ) );
                setForeground( list.getSelectionForeground(  ) );
            }
            else
            {
                setBackground( list.getBackground(  ) );
                setForeground( list.getForeground(  ) );
            }

            final CBItem item = (CBItem)value;
            setIcon( item.value );

            return this;
        }
    }
}

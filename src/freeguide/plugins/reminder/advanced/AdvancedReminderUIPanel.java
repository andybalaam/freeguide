package freeguide.plugins.reminder.advanced;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AdvancedReminderUIPanel extends JPanel
{
    protected JPanel mainPanel;
    protected JButton btnAddReminder;
    protected final List<OneReminderPanel> reminderPanels =
        new ArrayList<OneReminderPanel>(  );
    protected int latestLine;

    /**
     * Creates a new AdvancedReminderUIPanel object.
     */
    public AdvancedReminderUIPanel(  )
    {
        setLayout( new BorderLayout(  ) );
        mainPanel = new JPanel(  );

        final JScrollPane scrollPane = new JScrollPane( mainPanel );
        add( scrollPane, BorderLayout.CENTER );
        btnAddReminder = new JButton( "Add reminder" );

        mainPanel.setLayout( new GridBagLayout(  ) );

        final GridBagConstraints gbcButtonAdd = new GridBagConstraints(  );
        gbcButtonAdd.gridy = 0;
        gbcButtonAdd.anchor = GridBagConstraints.EAST;
        mainPanel.add( btnAddReminder, gbcButtonAdd );
        latestLine = 5;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param panel DOCUMENT_ME!
     */
    public void addReminderPanel( final OneReminderPanel panel )
    {
        reminderPanels.add( panel );

        final GridBagConstraints gbc = new GridBagConstraints(  );
        gbc.gridy = latestLine;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add( panel, gbc );
        latestLine++;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param panel DOCUMENT_ME!
     */
    public void removeReminderPanel( final OneReminderPanel panel )
    {
        reminderPanels.remove( panel );
        mainPanel.remove( panel );
    }
}

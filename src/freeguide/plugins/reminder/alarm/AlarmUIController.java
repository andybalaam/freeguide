/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.plugins.reminder.alarm;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

/*
 *  A panel full of options about time
 *
 * @author     Andy Balaam
 * @created    12 Dec 2003
 * @version    1
 */
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AlarmUIController implements IModuleConfigurationUI
{
    // ----------------------------------
    private JComboBox remindComboBox;
    private JTextField warningTextField;
    private JTextField giveupTextField;
    protected final AlarmUIPanel panel;
    protected final AlarmReminder parent;

/**
     * Creates a new AlarmUIController object.
     *
     * @param parent DOCUMENT ME!
     * @param parentDialog DOCUMENT ME!
     */
    public AlarmUIController( 
        final AlarmReminder parent, final JDialog parentDialog )
    {
        this.parent = parent;
        panel = new AlarmUIPanel( parent.getLocalizer(  ) );
        panel.getCbRemind(  ).setSelected( parent.config.reminderOn );
        panel.getTextWarning(  )
             .setText( String.valueOf( parent.config.reminderWarning / 1000 ) );

        panel.getTextGiveup(  )
             .setText( String.valueOf( parent.config.reminderGiveUp / 1000 ) );
    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {
        return panel;
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
        // TODO Auto-generated method stub
    }

    /**
     * Saves the values in this option pane.
     */
    public void save(  )
    {
        parent.config.reminderOn = panel.getCbRemind(  ).isSelected(  );
        parent.config.reminderWarning = Long.parseLong( 
                panel.getTextWarning(  ).getText(  ) ) * 1000L;
        parent.config.reminderGiveUp = Long.parseLong( 
                panel.getTextGiveup(  ).getText(  ) ) * 1000L;
    }
}

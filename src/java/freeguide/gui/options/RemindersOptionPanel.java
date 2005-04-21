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
package freeguide.gui.options;

import freeguide.*;

import freeguide.gui.dialogs.*;

import freeguide.gui.viewer.MainController;

import freeguide.lib.general.*;

import freeguide.plugins.ui.horizontal.HorizontalViewer;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/*

 *  A panel full of options about time

 *

 * @author     Andy Balaam

 * @created    12 Dec 2003

 * @version    1

 */
public class RemindersOptionPanel extends OptionPanel
{

    // ----------------------------------
    private JComboBox remindComboBox;
    private JTextField warningTextField;
    private JTextField giveupTextField;

    /**
     * Creates a new RemindersOptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public RemindersOptionPanel( FGDialog parent )
    {
        super( parent );

    }

    /**
     * DOCUMENT_ME!
     */
    public void doConstruct(  )
    {

        // Make the objects
        JLabel remindLabel =
            newLeftJLabel( 
                FreeGuide.msg.getString( "remind_me_of_progs" ) + ":" );

        Object[] options = new Object[2];

        options[0] = "Yes";

        options[1] = "No";

        remindComboBox = newRightJComboBox( options );

        remindLabel.setLabelFor( remindComboBox );

        remindLabel.setDisplayedMnemonic( KeyEvent.VK_R );

        JLabel warningLabel =
            newLeftJLabel( FreeGuide.msg.getString( "seconds_warning" ) + ":" );

        warningTextField = newRightJTextField(  );

        warningLabel.setLabelFor( warningTextField );

        warningLabel.setDisplayedMnemonic( KeyEvent.VK_W );

        JLabel giveupLabel =
            newLeftJLabel( 
                FreeGuide.msg.getString( "give_up_after_secs" ) + ":" );

        giveupTextField = newRightJTextField(  );

        giveupLabel.setLabelFor( giveupTextField );

        giveupLabel.setDisplayedMnemonic( KeyEvent.VK_G );

        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( this );

        gbe.default_insets = new Insets( 1, 1, 1, 1 );

        gbe.default_ipadx = 5;

        gbe.default_ipady = 5;

        gbe.addFWX( remindLabel, 0, 0, gbe.FILL_HOR, 0.2 );

        gbe.addFWX( remindComboBox, 1, 0, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( warningLabel, 0, 1, gbe.FILL_HOR, 0.2 );

        gbe.addFWX( warningTextField, 1, 1, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( giveupLabel, 0, 2, gbe.FILL_HOR, 0.2 );

        gbe.addFWX( giveupTextField, 1, 2, gbe.FILL_HOR, 0.8 );

        // Load in the values from config
        load(  );

    }

    protected void doLoad( String prefix )
    {

        if( MainController.config.reminderOn )
        {
            remindComboBox.setSelectedIndex( 0 );

        }

        else
        {
            remindComboBox.setSelectedIndex( 1 );

        }

        warningTextField.setText( 
            String.valueOf( MainController.config.reminderWarning / 1000 ) );

        giveupTextField.setText( 
            String.valueOf( MainController.config.reminderGiveUp / 1000 ) );

    }

    /**
     * Saves the values in this option pane.
     *
     * @return false since nothing changes the view
     */
    public boolean doSave(  )
    {

        if( remindComboBox.getSelectedIndex(  ) == 0 )
        {
            MainController.config.reminderOn = true;

        }

        else
        {
            MainController.config.reminderOn = false;

        }

        MainController.config.reminderWarning =
            Long.parseLong( warningTextField.getText(  ) ) * 1000L;

        MainController.config.reminderGiveUp =
            Long.parseLong( giveupTextField.getText(  ) ) * 1000L;

        return false;

    }

    /**
     * Used to find the name of this panel when displayed in a JTree.
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {

        return FreeGuide.msg.getString( "reminders" );

    }
}

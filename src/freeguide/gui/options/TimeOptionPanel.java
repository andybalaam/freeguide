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

import freeguide.lib.general.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/*
 *  A panel full of options about time
 *
 * @author     Andy Balaam
 * @created    12 Dec 2003
 * @version    2
 */
public class TimeOptionPanel extends OptionPanel
{

    // ----------------------------------
    private JTextField dayStartTextField;
    private JComboBox twelveHourComboBox;
    private JComboBox showTimesComboBox;

    /**
     * Creates a new TimeOptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public TimeOptionPanel( FGDialog parent )
    {
        super( parent );
    }

    /**
     * DOCUMENT_ME!
     */
    public void doConstruct(  )
    {

        // Make the objects
        JLabel dayStartLabel =
            newLeftJLabel( FreeGuide.msg.getString( "day_starts_at" ) + ":" );
        dayStartTextField = newRightJTextField(  );

        JLabel twelveHourLabel =
            newLeftJLabel( FreeGuide.msg.getString( "time_format" ) + ":" );
        Object[] options = new Object[2];
        options[0] = FreeGuide.msg.getString( "12_hour" );
        options[1] = FreeGuide.msg.getString( "24_hour" );
        twelveHourComboBox = newRightJComboBox( options );

        JLabel showTimesLabel =
            newLeftJLabel( FreeGuide.msg.getString( "show_programme_times" ) );
        options = new Object[2];
        options[0] = FreeGuide.msg.getString( "yes" );
        options[1] = FreeGuide.msg.getString( "no" );
        showTimesComboBox = newRightJComboBox( options );

        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( this );

        gbe.default_insets = new Insets( 1, 1, 1, 1 );
        gbe.default_ipadx = 5;
        gbe.default_ipady = 5;

        gbe.addFWX( dayStartLabel, 0, 0, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( dayStartTextField, 1, 0, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( twelveHourLabel, 0, 1, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( twelveHourComboBox, 1, 1, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( showTimesLabel, 0, 2, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( showTimesComboBox, 1, 2, gbe.FILL_HOR, 0.8 );

        // Load in the values from config
        load(  );

    }

    protected void doLoad( String prefix )
    {
        dayStartTextField.setText( 
            misc.get( prefix + "day_start_time", "06:00" ) );

        boolean disp24 = screen.getBoolean( "display_24hour_time", true );

        if( disp24 )
        {
            twelveHourComboBox.setSelectedIndex( 1 );
        }
        else
        {
            twelveHourComboBox.setSelectedIndex( 0 );
        }

        boolean dispTime = screen.getBoolean( "display_programme_time", true );

        if( dispTime )
        {
            showTimesComboBox.setSelectedIndex( 0 );
        }
        else
        {
            showTimesComboBox.setSelectedIndex( 1 );
        }
    }

    /**
     * Saves the values in this option pane.
     *
     * @return true if anything has been changed
     */
    public boolean doSave(  )
    {

        boolean updated = false;

        updated =
            misc.updateTime( 
                "day_start_time", new Time( dayStartTextField.getText(  ) ) )
            || updated;

        updated =
            screen.updateBoolean( 
                "display_24hour_time",
                ( twelveHourComboBox.getSelectedIndex(  ) != 0 ) ) || updated;

        updated =
            screen.updateBoolean( 
                "display_programme_time",
                ( showTimesComboBox.getSelectedIndex(  ) == 0 ) ) || updated;

        return updated;

    }

    /**
     * Used to find the name of this panel when displayed in a JTree.
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {

        return FreeGuide.msg.getString( "time" );

    }
}
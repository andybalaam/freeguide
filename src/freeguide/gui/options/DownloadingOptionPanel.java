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
 *  A panel full of options about the downloading listings
 *
 * @author     Andy Balaam
 * @created    10 Dec 2003
 * @version    2
 */
public class DownloadingOptionPanel extends OptionPanel
{

    // ----------------------------------
    private JTextArea commandTextArea;
    private JTextArea configTextArea;
    private JComboBox daysComboBox;
    private JComboBox startTodayComboBox;
    private JTextField dayStartTextField;
    private JTextField todayOffsetTextField;
    private JComboBox modalComboBox;
    private JComboBox redownloadComboBox;

    /**
     * Creates a new DownloadingOptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public DownloadingOptionPanel( FGDialog parent )
    {
        super( parent );
    }

    /**
     * DOCUMENT_ME!
     */
    public void doConstruct(  )
    {

        // Make the objects
        JLabel startTodayLabel =
            newLeftJLabel( FreeGuide.msg.getString( "start_grabbing" ) + ":" );
        Object[] options = new Object[2];
        options[0] = FreeGuide.msg.getString( "today" );
        options[1] = FreeGuide.msg.getString( "day_viewed" );
        startTodayComboBox = newRightJComboBox( options );
        startTodayLabel.setLabelFor( startTodayComboBox );
        startTodayLabel.setDisplayedMnemonic( KeyEvent.VK_S );

        JLabel dayStartLabel =
            newLeftJLabel( 
                FreeGuide.msg.getString( "day_start_time_hhmm" ) + ":" );
        dayStartTextField = newRightJTextField(  );
        dayStartLabel.setLabelFor( dayStartTextField );
        dayStartLabel.setDisplayedMnemonic( KeyEvent.VK_A );

        JLabel todayOffsetLabel =
            newLeftJLabel( FreeGuide.msg.getString( "today_offset" ) + ":" );
        todayOffsetTextField = newRightJTextField(  );
        todayOffsetLabel.setLabelFor( todayOffsetTextField );
        todayOffsetLabel.setDisplayedMnemonic( KeyEvent.VK_T );

        JLabel daysLabel =
            newLeftJLabel( 
                FreeGuide.msg.getString( "download_how_much" ) + ":" );
        options = new Object[8];
        options[0] = FreeGuide.msg.getString( "1_day" );
        options[1] = FreeGuide.msg.getString( "2_days" );
        options[2] = FreeGuide.msg.getString( "3_days" );
        options[3] = FreeGuide.msg.getString( "4_days" );
        options[4] = FreeGuide.msg.getString( "5_days" );
        options[5] = FreeGuide.msg.getString( "6_days" );
        options[6] = FreeGuide.msg.getString( "1_week" );
        options[7] = FreeGuide.msg.getString( "2_weeks" );
        daysComboBox = newRightJComboBox( options );
        daysLabel.setLabelFor( daysComboBox );
        daysLabel.setDisplayedMnemonic( KeyEvent.VK_D );

        options = new Object[3];
        options[ExecutorDialog.REDOWNLOAD_ALWAYS] =
            FreeGuide.msg.getString( "always" );
        options[ExecutorDialog.REDOWNLOAD_NEVER] =
            FreeGuide.msg.getString( "never" );
        options[ExecutorDialog.REDOWNLOAD_ASK] =
            FreeGuide.msg.getString( "ask" );

        JLabel redownloadLabel =
            newLeftJLabel( FreeGuide.msg.getString( "re_download_q" ) );
        redownloadComboBox = newRightJComboBox( options );
        redownloadLabel.setLabelFor( redownloadComboBox );
        redownloadLabel.setDisplayedMnemonic( KeyEvent.VK_B );

        options = new Object[2];
        options[0] = FreeGuide.msg.getString( "yes" );
        options[1] = FreeGuide.msg.getString( "no" );

        JLabel modalLabel =
            newLeftJLabel( 
                FreeGuide.msg.getString( "download_in_background" ) );
        modalComboBox = newRightJComboBox( options );
        modalLabel.setLabelFor( modalComboBox );
        modalLabel.setDisplayedMnemonic( KeyEvent.VK_B );

        JLabel commandLabel =
            newLeftJLabel( FreeGuide.msg.getString( "grabber_command" ) + ":" );
        commandTextArea = newRightJTextArea(  );

        JScrollPane commandScrollPane = new JScrollPane( commandTextArea );
        commandLabel.setLabelFor( commandTextArea );
        commandLabel.setDisplayedMnemonic( KeyEvent.VK_G );

        JLabel configLabel =
            newLeftJLabel( FreeGuide.msg.getString( "config_command" ) + ":" );
        configTextArea = newRightJTextArea(  );

        JScrollPane configScrollPane = new JScrollPane( configTextArea );
        configLabel.setLabelFor( configTextArea );
        configLabel.setDisplayedMnemonic( KeyEvent.VK_G );

        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( this );

        gbe.default_insets = new Insets( 1, 1, 1, 1 );
        gbe.default_ipadx = 5;
        gbe.default_ipady = 5;

        gbe.addFWX( startTodayLabel, 0, 1, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( startTodayComboBox, 1, 1, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( dayStartLabel, 0, 2, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( dayStartTextField, 1, 2, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( todayOffsetLabel, 0, 3, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( todayOffsetTextField, 1, 3, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( daysLabel, 0, 0, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( daysComboBox, 1, 0, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( redownloadLabel, 0, 5, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( redownloadComboBox, 1, 5, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( modalLabel, 0, 4, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( modalComboBox, 1, 4, gbe.FILL_HOR, 0.8 );

        gbe.addAFWX( commandLabel, 0, 6, gbe.ANCH_NORTH, gbe.FILL_HOR, 0.2 );
        gbe.addFWXWYGWGH( 
            commandScrollPane, 0, 7, gbe.FILL_BOTH, 1.0, 0.5, 2, 1 );

        gbe.addAFWX( configLabel, 0, 8, gbe.ANCH_NORTH, gbe.FILL_HOR, 0.2 );
        gbe.addFWXWYGWGH( 
            configScrollPane, 0, 9, gbe.FILL_BOTH, 1.0, 0.5, 2, 1 );

        // Load in the values from config
        load(  );

    }

    protected void doLoad( String prefix )
    {

        boolean startToday =
            misc.getBoolean( prefix + "grabber_start_today", true );

        if( startToday )
        {
            startTodayComboBox.setSelectedIndex( 0 );
        }
        else
        {
            startTodayComboBox.setSelectedIndex( 1 );
        }

        dayStartTextField.setText( 
            misc.get( prefix + "grabber_start_time", "06:00" ) );

        todayOffsetTextField.setText( 
            misc.get( prefix + "grabber_today_offset" ) );

        int daysToDownload = misc.getInt( prefix + "days_to_grab", 7 );

        if( daysToDownload < 7 )
        {
            daysComboBox.setSelectedIndex( daysToDownload - 1 );
        }
        else if( daysToDownload < 14 )
        {
            daysComboBox.setSelectedIndex( 6 );
        }
        else
        {
            daysComboBox.setSelectedIndex( 7 );
        }

        int redownload = misc.getInt( prefix + "re_download", 2 );
        redownloadComboBox.setSelectedIndex( redownload );

        boolean modalExecutor =
            screen.getBoolean( prefix + "executor_modal", true );

        if( modalExecutor )
        {
            modalComboBox.setSelectedIndex( 1 );
        }
        else
        {
            modalComboBox.setSelectedIndex( 0 );
        }

        String[] commands = commandline.getStrings( prefix + "tv_grab" );
        commandTextArea.setText( lineBreakise( commands ) );

        String[] configs = commandline.getStrings( prefix + "tv_config" );
        configTextArea.setText( lineBreakise( configs ) );

    }

    /**
     * Saves the values in this option pane.
     *
     * @return false always since these options don't affect the screen
     *         display.
     */
    public boolean doSave(  )
    {

        if( startTodayComboBox.getSelectedIndex(  ) == 0 )
        {
            misc.putBoolean( "grabber_start_today", true );
        }
        else
        {
            misc.putBoolean( "grabber_start_today", false );
        }

        misc.putTime( 
            "grabber_start_time", new Time( dayStartTextField.getText(  ) ) );

        misc.putInt( 
            "grabber_today_offset",
            Integer.parseInt( todayOffsetTextField.getText(  ) ) );

        int daysToDownload;
        int selectedIndex = daysComboBox.getSelectedIndex(  );

        switch( selectedIndex )
        {

        case 7:
            daysToDownload = 14;

            break;

        case 6:
            daysToDownload = 7;

            break;

        default:
            daysToDownload = selectedIndex + 1;

            break;
        }

        misc.putInt( "days_to_grab", daysToDownload );

        int redownload = redownloadComboBox.getSelectedIndex(  );
        misc.putInt( "re_download", redownload );

        if( modalComboBox.getSelectedIndex(  ) == 0 )
        {
            screen.putBoolean( "executor_modal", false );
        }
        else
        {
            screen.putBoolean( "executor_modal", true );
        }

        commandline.putStrings( 
            "tv_grab", unlineBreakise( commandTextArea.getText(  ) ) );

        commandline.putStrings( 
            "tv_config", unlineBreakise( configTextArea.getText(  ) ) );

        // Return value is false since none of these options alter the screen
        // appearance.
        return false;

    }

    /**
     * Used to find the name of this panel when displayed in a JTree.
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {

        return FreeGuide.msg.getString( "downloading" );

    }
}
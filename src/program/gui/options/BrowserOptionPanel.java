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
 * @version    1
 */

public class BrowserOptionPanel extends OptionPanel {

    public BrowserOptionPanel( FGDialog parent ) {
        super( parent );
    }
            
    public void doConstruct() {
        
        browsers = FreeGuide.prefs.getBrowsers();
        
        // Make the objects
        
        JLabel browserLabel = newLeftJLabel(
            FreeGuide.msg.getString( "web_browser" ) + ":" );
        browserComboBox = newRightJComboBox( browsers );
        browserLabel.setLabelFor(browserComboBox);
        browserLabel.setDisplayedMnemonic(KeyEvent.VK_W);
        
        JLabel commandLabel = newLeftJLabel( FreeGuide.msg.getString( "full_command" ) + ":" );
        commandTextArea = newRightJTextArea();
        JScrollPane commandScrollPane = new JScrollPane(commandTextArea);
        commandLabel.setLabelFor( commandTextArea );
        commandLabel.setDisplayedMnemonic( KeyEvent.VK_F );
        
        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( this );
        
        gbe.default_insets = new Insets( 1, 1, 1, 1 );
        gbe.default_ipadx = 5;
        gbe.default_ipady = 5;
        
        gbe.addFWX  ( browserLabel    , 0, 0, gbe.FILL_HOR   , 0.2 );
        gbe.addFWX  ( browserComboBox , 1, 0, gbe.FILL_HOR   , 0.8 );
        
        gbe.addAFWX ( commandLabel     , 0, 1, gbe.ANCH_NORTH, gbe.FILL_HOR,
            0.2 );
        gbe.addFWXWY( commandScrollPane, 1, 1, gbe.FILL_BOTH  , 0.8, 0.5 );
        
        // Load in the values from config
        load();
        
        browserComboBoxItemListener = (
            new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    browserComboBoxItemStateChanged( evt );
                }
            });
        
        browserComboBox.addItemListener( browserComboBoxItemListener );
        
    }
    
    protected void doLoad( String prefix ) {

        browserComboBox.setSelectedItem( misc.get( prefix + "browser",
            browsers[0] ) );

        commandTextArea.setText( lineBreakise(
            commandline.getStrings( prefix + "browser_command" ) ) );
            
    }
    
    
    
    /**
     * Saves the values in this option pane.
     *
     * @return false since nothing changes the view
     */
    public boolean doSave() {
    
        int i = browserComboBox.getSelectedIndex();
    
        misc.put( "browser", browsers[i] );
        
        commandline.putStrings( "browser_command",
            FreeGuide.prefs.getCommands( "browser_command." + (i+1) ) );

        commandline.putStrings( "browser_command", unlineBreakise(
            commandTextArea.getText() ) );
        
        // Return value is false since none of these options alter the screen
        // appearance.
        return false;
        
    }
    
    /**
     * Used to find the name of this panel when displayed in a JTree.
     */
    public String toString() {
        
        return FreeGuide.msg.getString( "browser" );
        
    }

    protected void browserComboBoxItemStateChanged(
        java.awt.event.ItemEvent evt )
    {
        
        int i = browserComboBox.getSelectedIndex();
        
        commandTextArea.setText( lineBreakise(
            FreeGuide.prefs.getCommands( "browser_command." + (i+1) ) ) );
        
    }
    
    // ----------------------------------
    
    private JComboBox browserComboBox;
    private JTextArea commandTextArea;
    private String[] browsers;
    
    private ItemListener browserComboBoxItemListener;
    
}

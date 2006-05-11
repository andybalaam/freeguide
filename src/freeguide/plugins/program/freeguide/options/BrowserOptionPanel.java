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
package freeguide.plugins.program.freeguide.options;

import freeguide.common.gui.FGDialog;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.*;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.dialogs.*;
import freeguide.plugins.program.freeguide.wizard.FirstTimeWizard;

import java.awt.*;
import java.awt.event.*;

import java.util.Map;

import javax.swing.*;
import javax.swing.event.*;

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
public class BrowserOptionPanel extends OptionPanel
{
    // ----------------------------------
    private JComboBox browserComboBox;
    private JTextArea commandTextArea;
    private Map browsers;
    private ItemListener browserComboBoxItemListener;

/**
     * Creates a new BrowserOptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public BrowserOptionPanel( FGDialog parent )
    {
        super( parent );
    }

    /**
     * DOCUMENT_ME!
     */
    public void doConstruct(  )
    {
        browsers = FirstTimeWizard.getAllBrowsers(  );

        // Make the objects
        JLabel browserLabel =
            newLeftJLabel( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "web_browser" ) + ":" );
        browserComboBox = newRightJComboBox( 
                browsers.keySet(  ).toArray( new String[0] ) );
        browserLabel.setLabelFor( browserComboBox );
        browserLabel.setDisplayedMnemonic( KeyEvent.VK_W );

        JLabel commandLabel =
            newLeftJLabel( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "full_command" ) + ":" );
        commandTextArea = newRightJTextArea(  );

        JScrollPane commandScrollPane = new JScrollPane( commandTextArea );
        commandLabel.setLabelFor( commandTextArea );
        commandLabel.setDisplayedMnemonic( KeyEvent.VK_F );

        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( this );
        gbe.default_insets = new Insets( 1, 1, 1, 1 );
        gbe.default_ipadx = 5;
        gbe.default_ipady = 5;
        gbe.addFWX( browserLabel, 0, 0, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( browserComboBox, 1, 0, gbe.FILL_HOR, 0.8 );
        gbe.addAFWX( commandLabel, 0, 1, gbe.ANCH_NORTH, gbe.FILL_HOR, 0.2 );
        gbe.addFWXWY( commandScrollPane, 1, 1, gbe.FILL_BOTH, 0.8, 0.5 );
        // Load in the values from config
        load(  );
        browserComboBoxItemListener = ( new java.awt.event.ItemListener(  )
                {
                    public void itemStateChanged( 
                        java.awt.event.ItemEvent evt )
                    {
                        browserComboBoxItemStateChanged( evt );
                    }
                } );
        browserComboBox.addItemListener( browserComboBoxItemListener );
    }

    protected void doLoad( String prefix )
    {
        browserComboBox.setSelectedItem( FreeGuide.config.browserName );
        commandTextArea.setText( FreeGuide.config.browserCommand );
    }

    /**
     * Saves the values in this option pane.
     *
     * @return false since nothing changes the view
     */
    public boolean doSave(  )
    {
        FreeGuide.config.browserName = (String)browserComboBox.getSelectedItem(  );
        FreeGuide.config.browserCommand = commandTextArea.getText(  );

        return true;
    }

    /**
     * Used to find the name of this panel when displayed in a JTree.
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {
        return Application.getInstance(  ).getLocalizedMessage( "browser" );
    }

    protected void browserComboBoxItemStateChanged( 
        java.awt.event.ItemEvent evt )
    {
        commandTextArea.setText( 
            (String)browsers.get( browserComboBox.getSelectedItem(  ) ) );
    }
}

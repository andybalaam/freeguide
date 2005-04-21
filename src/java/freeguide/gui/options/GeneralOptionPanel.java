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

import freeguide.FreeGuide;

import freeguide.gui.dialogs.FGDialog;

import freeguide.gui.viewer.MainController;

import freeguide.lib.general.GridBagEasy;
import freeguide.lib.general.LookAndFeelManager;

import java.awt.Insets;
import java.awt.event.KeyEvent;

import java.util.Iterator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/*
 *  A panel full of options about the screen layout in FreeGuide
 *
 * @author     Andy Balaam
 * @created    9 Dec 2003
 * @version    3
 */
public class GeneralOptionPanel extends OptionPanel
{

    // ----------------------------------
    private JTextField workingTextField;
    private JComboBox lookAndFeelCombo;

    /**
     * Creates a new LayoutOptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public GeneralOptionPanel( FGDialog parent )
    {
        super( parent );

    }

    /**
     * DOCUMENT_ME!
     */
    public void doConstruct(  )
    {

        JLabel workingLabel =
            newLeftJLabel( FreeGuide.msg.getString( "working_dir" ) + ":" );

        workingTextField = newRightJTextField(  );

        workingLabel.setLabelFor( workingTextField );

        workingLabel.setDisplayedMnemonic( KeyEvent.VK_W );

        // Make the objects
        JLabel lookAndFeelLabel =
            newLeftJLabel( FreeGuide.msg.getString( "look_and_feel" ) + ":" );

        lookAndFeelCombo = new JComboBox(  );

        lookAndFeelCombo.setEditable( true );

        List lafs = LookAndFeelManager.getAvailableLooksAndFeels(  );

        Iterator lafsIterator = lafs.iterator(  );

        while( lafsIterator.hasNext(  ) )
        {
            lookAndFeelCombo.addItem( lafsIterator.next(  ) );

        }

        // Lay them out in a GridBag layout
        GridBagEasy gbe = new GridBagEasy( this );

        gbe.default_insets = new Insets( 1, 1, 1, 1 );

        gbe.default_ipadx = 5;

        gbe.default_ipady = 5;

        gbe.addFWX( workingLabel, 0, 0, gbe.FILL_HOR, 0.2 );
        gbe.addFWX( workingTextField, 1, 0, gbe.FILL_HOR, 0.8 );

        gbe.addFWX( lookAndFeelLabel, 0, 1, gbe.FILL_HOR, 0.2 );

        gbe.addFWXWYGW( lookAndFeelCombo, 1, 1, gbe.FILL_HOR, 0.1, 0, 2 );

        // Load in the values from config
        load(  );

    }

    protected void doLoad( String prefix )
    {
        workingTextField.setText( FreeGuide.config.workingDirectory );

        LookAndFeel currentLAF = UIManager.getLookAndFeel(  );

        String defaultLAFName = "Metal";

        if( currentLAF != null )
        {
            defaultLAFName = currentLAF.getName(  );

        }

        lookAndFeelCombo.setSelectedItem( MainController.config.ui.LFname );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean doSave(  )
    {
        FreeGuide.config.workingDirectory = workingTextField.getText(  );

        MainController.config.ui.LFname =
            lookAndFeelCombo.getSelectedItem(  ).toString(  );

        FreeGuide.mainController.setLookAndFeel(  );

        return true;

    }

    /**
     * Used to find the name of this panel when displayed in a JTree.
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {

        return FreeGuide.msg.getString( "general" );

    }
}

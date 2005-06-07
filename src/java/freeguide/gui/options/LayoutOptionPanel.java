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

import freeguide.lib.fgspecific.Application;

import freeguide.lib.general.*;

import java.awt.Insets;

import java.util.*;

import javax.swing.*;

/*

 *  A panel full of options about the screen layout in FreeGuide

 *

 * @author     Andy Balaam

 * @created    9 Dec 2003

 * @version    3

 */
public class LayoutOptionPanel extends OptionPanel
{

    // ----------------------------------
    private JComboBox lookAndFeelCombo;

    /**
     * Creates a new LayoutOptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public LayoutOptionPanel( FGDialog parent )
    {
        super( parent );

    }

    /**
     * DOCUMENT_ME!
     */
    public void doConstruct(  )
    {

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

        gbe.addFWX( lookAndFeelLabel, 0, 0, gbe.FILL_HOR, 0.2 );

        gbe.addFWXWYGW( lookAndFeelCombo, 1, 0, gbe.FILL_HOR, 0.1, 0, 2 );

        // Load in the values from config
        load(  );

    }

    protected void doLoad( String prefix )
    {

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
        MainController.config.ui.LFname =
            lookAndFeelCombo.getSelectedItem(  ).toString(  );

        ( (MainController)Application.getInstance(  ) ).setLookAndFeel(  );

        return true;

    }

    /**
     * Used to find the name of this panel when displayed in a JTree.
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {

        return FreeGuide.msg.getString( "layout" );

    }
}

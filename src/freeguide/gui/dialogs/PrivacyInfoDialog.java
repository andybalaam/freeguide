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
package freeguide.gui.dialogs;

import freeguide.*;

import freeguide.lib.general.*;

import java.awt.*;

import java.util.*;

import javax.swing.*;

/**
 * Tell the user about their privacy in FreeGuide
 *
 * @author Andy Balaam
 * @version 1
 */
public class PrivacyInfoDialog extends JDialog
{

    /**
     * Constructor which sets the customiser up as a JDialog...
     */
    public PrivacyInfoDialog(  )
    {
        super(  );
        setTitle( FreeGuide.msg.getString( "privacy_information" ) );
        setModal( true );

        initComponents(  );
    }

    private void initComponents(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;

        ResourceBundle privBundle = ResourceBundle.getBundle( 
                "resources/PrivacyBundle", FreeGuide.locale );

        String privacyInfo = privBundle.getString( "privacy_html" );

        JEditorPane infoPane = new JEditorPane( "text/html", privacyInfo );
        infoPane.setEditable( false );
        infoPane.setCaretPosition( 0 );

        GridBagEasy gbe = new GridBagEasy( getContentPane(  ) );
        gbe.default_insets = new java.awt.Insets( 5, 5, 5, 5 );

        JScrollPane scrollPane = new JScrollPane( infoPane );
        gbe.addFWXWY( scrollPane, 0, 0, gbe.FILL_BOTH, 1, 1 );

        JButton OKButton = new JButton( FreeGuide.msg.getString( "ok" ) );
        gbe.addAWXWY( OKButton, 0, 1, gbe.ANCH_EAST, 1, 0 );

        OKButton.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    OKButtonActionPerformed( evt );
                }
            } );

        getRootPane(  ).setDefaultButton( OKButton );

        pack(  ); // pack comes before the size instructions or they get ignored.

        Dimension screenSize = Toolkit.getDefaultToolkit(  ).getScreenSize(  );
        Dimension dialogSize = new Dimension( 600, 400 );

        setSize( dialogSize );

        setLocation( 
            ( screenSize.width - dialogSize.width ) / 2,
            ( screenSize.height - dialogSize.height ) / 2 );

// To Be Added Shortly (Rob)
//        GuiUtils.centerDialog( this, 600, 400 );
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt
     */
    private void OKButtonActionPerformed( java.awt.event.ActionEvent evt )
    {
        quit(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void exitForm( java.awt.event.WindowEvent evt )
    {
        quit(  );
    }

    /**
     * Closes the form and goes back to the viewer.
     */
    private void quit(  )
    {
        dispose(  );

    }
}

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

import javax.swing.*;

/**
 * Tell the user there's a new version of FreeGuide available
 *
 * @author Andy Balaam
 * @version 1
 */
public class NewVersionDialog extends JDialog
{
    private JButton butOK;
    private JButton butURL;
    private JCheckBox chkTellMeAgain;

    /**
     * Constructor which sets the customiser up as a JDialog...
     *
     * @param parent DOCUMENT ME!
     */
    public NewVersionDialog( JFrame parent )
    {
        super( 
            parent, FreeGuide.msg.getString( "new_version_available" ), true );

        initComponents(  );
    }

    private void initComponents(  )
    {

        java.awt.GridBagConstraints gridBagConstraints;

        Object[] messageArguments = { "http://freeguide-tv.sourceforge.net" };

        Container pane = getContentPane(  );
        pane.setLayout( new java.awt.GridBagLayout(  ) );

        JLabel labTopMessage = new javax.swing.JLabel( 
            FreeGuide.getCompoundMessage(
                "new_version_available_at_template", messageArguments ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pane.add( labTopMessage, gridBagConstraints );

        butURL = new javax.swing.JButton( 
            FreeGuide.msg.getString( "go_to_the_web_site" ) );
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pane.add( butURL, gridBagConstraints );
        butURL.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butURLActionPerformed( evt );
                }
            } );

        chkTellMeAgain = new javax.swing.JCheckBox(
            FreeGuide.msg.getString( "check_new_version_every_time" ), true );
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        pane.add( chkTellMeAgain, gridBagConstraints );

        butOK = new javax.swing.JButton( FreeGuide.msg.getString( "ok" ) );
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        pane.add( butOK, gridBagConstraints );

        butOK.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butOKActionPerformed( evt );
                }
            } );

        getRootPane(  ).setDefaultButton( butOK );

        pack(  ); // pack comes before the size instructions or they get ignored.

        java.awt.Dimension screenSize =
            java.awt.Toolkit.getDefaultToolkit(  ).getScreenSize(  );
        setLocation( 
            ( screenSize.width - getWidth(  ) ) / 2,
            ( screenSize.height - getHeight(  ) ) / 2 );

// To Be Added Shortly (Rob)
//        GuiUtils.centerDialog( this );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butOKActionPerformed( java.awt.event.ActionEvent evt )
    {

        if( !chkTellMeAgain.isSelected(  ) )
        {
            FreeGuide.prefs.misc.put( "privacy", "no" );

        }

        quit(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butURLActionPerformed( java.awt.event.ActionEvent evt )
    {

        String[] cmds =
            Utils.substitute( 
                FreeGuide.prefs.commandline.getStrings( "browser_command" ),
                "%filename%", "http://freeguide-tv-sourceforge.net" );
        Utils.execNoWait( cmds );

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

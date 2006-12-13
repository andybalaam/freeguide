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
package freeguide.plugins.program.freeguide.dialogs;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.*;

import freeguide.plugins.program.freeguide.FreeGuide;

import java.awt.*;

import java.util.logging.Level;

import javax.swing.*;

/**
 * Tell the user there's a new version of FreeGuide available
 *
 * @author Andy Balaam
 * @version 1
 */
public class NewVersionDialog extends JDialog
{
    protected static final String HOME_URL =
        "http://freeguide-tv.sourceforge.net";
    protected static final String URL_PATTERN = "%url%";
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
            parent,
            Application.getInstance(  )
                       .getLocalizedMessage( "new_version_available" ), true );
        initComponents(  );
    }

    private void initComponents(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;
        Object[] messageArguments = { HOME_URL };
        Container pane = getContentPane(  );
        pane.setLayout( new java.awt.GridBagLayout(  ) );

        JLabel labTopMessage =
            new javax.swing.JLabel( 
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "new_version_available_at_template", messageArguments ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pane.add( labTopMessage, gridBagConstraints );
        butURL = new javax.swing.JButton( 
                Application.getInstance(  )
                           .getLocalizedMessage( "go_to_the_web_site" ) );
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
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "check_new_version_every_time" ), true );
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        pane.add( chkTellMeAgain, gridBagConstraints );
        butOK = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );
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
            FreeGuide.config.privacyInfo = "no";
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
        try
        {
            String cmd =
                StringHelper.replaceAll( 
                    Application.getInstance(  ).getBrowserCommand(  ),
                    URL_PATTERN, HOME_URL );
            Utils.execNoWait( cmd );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( Level.WARNING, "Error open home url", ex );
        }
    }

    /**
     * Closes the form and goes back to the viewer.
     */
    private void quit(  )
    {
        dispose(  );
    }
}

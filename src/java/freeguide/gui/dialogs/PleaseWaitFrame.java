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

import freeguide.FreeGuide;

import java.awt.Image;

import java.net.URL;

import javax.swing.JFrame;

/**
 * A window saying "Please Wait", displayed while FreeGuide loads.
 *
 * @author Andy Balaam
 * @version 4
 */
public class PleaseWaitFrame extends JFrame
{

    private javax.swing.JLabel imageLabel;
    private javax.swing.ImageIcon image;

    /**
     * Creates this form, makes it visible, and starts the StartupChecker -
     * called on launching the program
     */
    public PleaseWaitFrame(  )
    {
        super( FreeGuide.msg.getString( "please_wait" ) );
        initComponents(  );
    }

    private void initComponents(  )
    {

        URL imgURL = getClass(  ).getResource( "/images/logo-16x16.png" );
        Image icon =
            ( new javax.swing.ImageIcon( imgURL, "icon" ) ).getImage(  );
        setIconImage( icon );
        imgURL = getClass(  ).getResource( "/images/logo-256x256.png" );
        image =
            new javax.swing.ImageIcon( 
                imgURL, FreeGuide.msg.getString( "please_wait" ) );
        imageLabel =
            new javax.swing.JLabel( image, javax.swing.SwingConstants.CENTER );
        imageLabel.setBorder( 
            javax.swing.BorderFactory.createLineBorder( java.awt.Color.BLACK ) );
        getContentPane(  ).add( imageLabel, java.awt.BorderLayout.CENTER );
        setResizable( false );
        addWindowListener( 
            new java.awt.event.WindowAdapter(  )
            {
                public void windowClosing( java.awt.event.WindowEvent evt )
                {
                    exitForm( evt );
                }
            } );
        pack(  );

        java.awt.Dimension screenSize =
            java.awt.Toolkit.getDefaultToolkit(  ).getScreenSize(  );
        java.awt.Dimension windowSize = getSize(  );
        setLocation( 
            ( screenSize.width - windowSize.width ) / 2,
            ( screenSize.height - windowSize.height ) / 2 );

        // To Be Added Shortly (Rob)
        //        GuiUtils.centerDialog( this );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void exitForm( java.awt.event.WindowEvent evt )
    {
        FreeGuide.log.info( 
            FreeGuide.msg.getString( "halting_due_to_please_wait_closed" ) );
        System.exit( 0 );
    }
}

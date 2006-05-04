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

import freeguide.plugins.program.freeguide.FreeGuide;

import freeguide.common.lib.general.LanguageHelper;
import freeguide.common.lib.general.Utils;

import java.awt.Image;

import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
        super( "Please Wait" );
        initComponents(  );
        Utils.centreDialog( this );
    }

    private void initComponents(  )
    {

        try
        {

            byte[] data =
                LanguageHelper.loadResourceAsByteArray( 
                    "resources/plugins/program/freeguide/images/logo-16x16.png" );

            if( data != null )
            {

                Image icon = ( new ImageIcon( data, "icon" ) ).getImage(  );
                setIconImage( icon );
            }

            byte[] data2 =
                LanguageHelper.loadResourceAsByteArray( 
                    "resources/plugins/program/freeguide/images/logo-256x256.png" );

            if( data2 != null )
            {
                image = new ImageIcon( data2, "Please Wait" );
                imageLabel =
                    new JLabel( image, javax.swing.SwingConstants.CENTER );
                imageLabel.setBorder( 
                    BorderFactory.createLineBorder( java.awt.Color.BLACK ) );
                getContentPane(  ).add( 
                    imageLabel, java.awt.BorderLayout.CENTER );
                setResizable( false );
            }
        }
        catch( IOException ex )
        {
        }

        addWindowListener( 
            new java.awt.event.WindowAdapter(  )
            {
                public void windowClosing( java.awt.event.WindowEvent evt )
                {
                    exitForm( evt );
                }
            } );
        pack(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void exitForm( java.awt.event.WindowEvent evt )
    {
        FreeGuide.log.info( "Halting due to user closing Please Wait dialog." );
        System.exit( 0 );
    }
}

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

package freeguidetv.gui.dialogs;

import freeguidetv.*;
import freeguidetv.gui.*;

/**
 *  A window saying "Please Wait", displayed while FreeGuide loads.
 * 
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    4
 */
public class PleaseWaitFrame extends javax.swing.JFrame implements Progressor {

    /**
     * Creates this form, makes it visible, and starts the StartupChecker
	 * - called on launching the program
     */
    public PleaseWaitFrame() {
		
		super( "Please Wait" );
		
        initComponents();
		
    }
	
    private void initComponents() {
        
		imageLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar( 0, 100 );
		
		java.net.URL imgURL = getClass().getResource( "/pleasewait.png" );
		
		image = new javax.swing.ImageIcon(imgURL, "Please Wait");

        setResizable( false );
		
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });

		imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setIcon( image );
        imageLabel.setBorder( javax.swing.BorderFactory.createLineBorder( 
			java.awt.Color.BLACK ) );
        getContentPane().add( imageLabel, java.awt.BorderLayout.CENTER );
		
        getContentPane().add( progressBar, java.awt.BorderLayout.SOUTH );
		
        pack();
        java.awt.Dimension screenSize =
			java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		java.awt.Dimension windowSize = getSize();
		
        setLocation((screenSize.width - windowSize.width) / 2,
			(screenSize.height - windowSize.height) / 2);
    }

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void exitForm(java.awt.event.WindowEvent evt) {
		FreeGuide.log.info( "Halting due to user closing Please Wait dialog." );
		System.exit( 0 );
    }

	public void setProgress( int percent ) {
		
		progressBar.setValue( percent );
		
	}
	
	private javax.swing.JLabel imageLabel;
	private javax.swing.ImageIcon image;
	private javax.swing.JProgressBar progressBar;

}

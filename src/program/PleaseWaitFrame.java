/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
 
import java.util.Vector;
 
/**
 *  A window saying "Please Wait", displayed while FreeGuide loads.
 * 
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    3
 */
public class PleaseWaitFrame extends javax.swing.JFrame implements Progressor {

    /**
     * Creates this form, makes it visible, and starts the StartupChecker
	 * - called on launching the program
     */
    public PleaseWaitFrame(Launcher launcher, String[] args) {
		
		super( "Please Wait" );
		
        initComponents();
		setVisible(true);
		
		Vector failedWhat = StartupChecker.runChecks(launcher, args);
		
		if (failedWhat.size() > 0) {
            
            // Something's wrong, so begin with configuration
			
            FreeGuide.log.info("Checks failed, going into configuration ...");
			
            new OptionsFrame(launcher, failedWhat).setVisible(true);
			
			dispose();
			
        } else {
			
            // All is ok, so begin with viewer
            
            /*if (FreeGuide.prefs.screen.getBoolean("use_metal_landf", false)) {
                ViewerFrame.setDefaultLookAndFeelDecorated(true);
            }*/

            new ViewerFrame(launcher, this);
        }
		
    }

	/**
     * Creates this form and makes it visible - used later in the use of the
	 * program.
     */
    /*public PleaseWaitFrame( Launcher launcher ) {
		
		super( "Please Wait" );
		
        initComponents();
		setVisible(true);
		
    }*/
	
    private void initComponents() {
        
        //textLabel = new javax.swing.JLabel();
		imageLabel = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar( 0, 100 );
		
		java.net.URL imgURL = getClass().getResource( "/pleasewait.png" );
		
		image = new javax.swing.ImageIcon(imgURL, "Please Wait");

        setResizable( false );
        //setUndecorated(true);
		
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });

        /*textLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textLabel.setText("FreeGuide is loading, please wait...");
        textLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(textLabel, java.awt.BorderLayout.NORTH);*/

		imageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imageLabel.setIcon( image );
        imageLabel.setBorder( new javax.swing.border.LineBorder( 
			new java.awt.Color(0, 0, 0) ) );
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
	
    //private javax.swing.JLabel textLabel;
	private javax.swing.JLabel imageLabel;
	private javax.swing.ImageIcon image;
	private javax.swing.JProgressBar progressBar;

}

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

package freeguide;

import freeguide.gui.dialogs.*;
import freeguide.gui.viewer.*;
import freeguide.gui.wizard.*;
import freeguide.lib.fgspecific.*;
import freeguide.lib.general.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.prefs.*;
import javax.swing.*;

/**
 *  The main class called to start FreeGuide. Calls other objects
 *  to do the real work. Also contains some global objects.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    10
 */
public class FreeGuide {
    
    public FreeGuide(String[] args) {
        
        // Check Java version.  If wrong, exit with error
        // Also set up a log and the preferences classes.
        StartupChecker.basicSetup( args );

        if( arguments.isSet( "language" ) ) {

            String country = arguments.getValue( "country" );
            if( country == null ) {
                country = "";
            }
            
            locale = new Locale( arguments.getValue( "language" ), country );
            
        } else {
            
            locale = Locale.getDefault();
            
        }
        
        msg = ResourceBundle.getBundle( "resources/MessagesBundle", locale );
        
        // Find out what the documents directory is from the command line
        if( arguments.isSet( "doc_directory" ) ) {
            
            prefs.misc.put( "doc_directory",
                arguments.getValue( "doc_directory" ) );
            
        } else {
            log.warning( FreeGuide.msg.getString( "no_docs_dir_supplied" ) );
        }
        
        if( arguments.isSet( "install_directory" ) ) {
            
            prefs.misc.put( "install_directory",
                arguments.getValue( "install_directory" ) );
                
        } else if( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
            log.warning( FreeGuide.msg.getString( "no_install_dir_supplied" ) );
        }
        
        String install_version = FreeGuide.prefs.misc.get( "install_version" );
        if( install_version == null ) {
            
            launchFirstTime();
            
        } else {
            
            // If the installed version number is lower than the version we are
            // running, we need to upgrade
            if( new Version( install_version ).lessThan( version ) ) {
                
                launchUpgrade();
                
            } else {
                
                normalStartup();
                
            }
            
        }

        // [Note: upgrade question just notifies you that your custom settings
        // will be over-written and lets you cancel.]
        
    }
    
    
    private void launchFirstTime() {
        
        new FirstTimeWizard( this, false );
        
    }
    
    private void launchUpgrade() {
        
        new FirstTimeWizard( this, true );
        
    }
    
    
    public void normalStartup() {
        
        // Show the Please Wait frame
        PleaseWaitFrame pleaseWait = new PleaseWaitFrame();
        pleaseWait.setVisible(true);
        
        Vector failedWhat = StartupChecker.runChecks();
        
        if( failedWhat.size() > 0 ) {
            // Something's wrong, so begin with configuration

            String message;
            
            message = FreeGuide.msg.getString( "config_messed_up" );
                
            for( int i=0; i<failedWhat.size(); i++ ) {
                message += failedWhat.get(i) + "\n";
            }
            
            message += 
            
            message += FreeGuide.msg.getString( "go_to_options_screen" );
                
            JOptionPane.showMessageDialog( null, message,
                FreeGuide.msg.getString( "configuration_problems" ),
                JOptionPane.WARNING_MESSAGE );

        }
        
        ViewerFrame viewerFrame = new ViewerFrame( pleaseWait );
        
    }
        
    /**
     *  Deletes a whole directory recursively (also deletes a single file).
     *
     *@param  dir  The directory to delete
     */
    private void deleteDir(File dir) {

        if (!dir.exists()) {
            return;
        }

        if (dir.isDirectory()) {
            String[] list = dir.list();
            for (int i = 0; i < list.length; i++) {
                deleteDir(new File(dir.getPath() + File.separator + list[i]));
            }
        }

        dir.delete();

    }
    
    /**
     *  The method called when FreeGuide is run.
     *
     *@param  args  the command line arguments
     */
    public static void main(String args[]) {

        new FreeGuide(args);

    }


    /**
     *  Stop the program and display the supplied error message
     *
     *@param  msg  The error message string to display
     */
    public static void die( String msg ) {

        System.err.println(msg);
        log.severe(msg);
        System.exit(1);

    }

    /**
     * Construct a compound internationalized message using the supplied
     * template and arguments.
     */
    public static String getCompoundMessage( String template,
        Object[] messageArguments )
    {

        // Note, could be quicker using static MessageFormat object
        MessageFormat formatter = new MessageFormat( "" );
        formatter.setLocale( FreeGuide.locale );
        formatter.applyPattern( FreeGuide.msg.getString( template ) );
        return formatter.format( messageArguments );
        
    }
    
    //------------------------------------------------------------------------

    /**
     *  Holds all commandline arguments
     */
    public static CmdArgs arguments;
    
    /**
     *  Holds all preferences info
     */
    public static PreferencesGroup prefs;

    /**
     *  The log file
     */
    public static Logger log;

    /**
     * The locale set from the command line.
     */
    public static Locale locale;
    
    /**
     * The bundle of internationalized messages.
     */
    public static ResourceBundle msg;
     
    /**
     *  The current version of the programme
     */
    public final static Version version = new Version( 0, 9, 0 );
    
    /**
     *  Default colour of a normal programme
     */
    public final static Color PROGRAMME_NOTINGUIDE_COLOUR = Color.white;
    /**
     *  Default colour of a clicked programme
     */
    public final static Color PROGRAMME_INGUIDE_COLOUR
        = new Color(204, 255, 204);
    /**
        *  Default colour of a heart that indicates a favourite
        */
    public final static Color PROGRAMME_HEART_COLOUR = Color.red;
    /**
     *  Default colour of a movie
     */
    public final static Color PROGRAMME_MOVIE_COLOUR = new Color(255, 230, 230);
    /**
     *  Default colour of the channel labels
     */
    public final static Color CHANNEL_COLOUR = new Color(245, 245, 255);
    /**
     *  Default height of each channel row
     */
    public final static int CHANNEL_HEIGHT = 28;
    /**
     *  Default gap between channel rows
     */
    public final static int VERTICAL_GAP = 1;
    /**
     *  Default horizontal gap between programmes
     */
    public final static int HORIZONTAL_GAP = 1;
    /**
     *  Default width of the scrolling panel containign programmes
     */
    public final static int PANEL_WIDTH = 8000;
    /**
     *  Default width of the channels scrolling panel
     */
    public final static int CHANNEL_PANEL_WIDTH = 400;

}


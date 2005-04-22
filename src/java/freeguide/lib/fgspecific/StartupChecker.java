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
package freeguide.lib.fgspecific;

import freeguide.*;

import freeguide.gui.viewer.*;

import freeguide.lib.fgspecific.*;

import freeguide.lib.general.*;

import java.io.File;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * FreeGuideStartupChecker Provides a single static method that checks
 * everything is ok, and exits if not.
 *
 * @author Andy Balaam
 * @version 4
 */
public class StartupChecker
{

    /**
     * Description of the Method
     *
     * @param args Description of the Parameter
     */
    public static void basicSetup( String[] args )
    {

        // ------------------------------------------------------
        // Check the version of Java that we are running in
        doJavaVersionCheck(  );

        // ------------------------------------------------------
        // Check we can make a log file
        if( !setupLog(  ) )
        {
            die( FreeGuide.msg.getString( "failed_to_create_log_file" ) );

        }

        // Check any arguments that were passed in are ok
        if( !processArgs( args ) )
        {
            FreeGuide.die( 
                FreeGuide.msg.getString( "argument_processing_failed" ) );

        }

        // Set up the Preferences clases

        /*if( !setupPrefs(  ) )


        {


        FreeGuide.die(


        FreeGuide.msg.getString( "failed_to_set_up_configuration" ) );


        }*/
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static Vector runChecks(  )
    {

        // Make an icon cache dir if it doesn't exist
        File iconcache =
            new File( FreeGuide.config.workingDirectory + "/iconcache" );

        if( !iconcache.isDirectory(  ) )
        {
            iconcache.mkdirs(  );

        }

        // --------------------------------------------------------------------
        // Checks that need correction
        // Variables that will be true if something needs correcting
        Vector failedWhat = new Vector(  );

        //PreferencesGroup prefs = FreeGuide.prefs;

        /* checkTextFailure( prefs.misc, "day_start_time", failedWhat );


        checkTextFailure( prefs.misc, "grabber_start_time", failedWhat );


        checkTextFailure( prefs.misc, "days_to_grab", failedWhat );


        checkFileFailure( prefs.misc, "working_directory", failedWhat );*/
        return failedWhat;

    }

    /**
     * Stop the program with an error
     *
     * @param message The error message to display
     */
    private static void die( String message )
    {
        FreeGuide.log.severe( message );
        System.exit( 1 );
    }

    /**
     * DOCUMENT_ME!
     */
    public static void doJavaVersionCheck(  )
    {

        if( !checkJavaVersion(  ) )
        {

            // FIXME should be a Java 1 compatible dialog box
            die( 
                FreeGuide.msg.getString( "halted_wrong_java_version" )
                + System.getProperty( "java.version" ) + "." );

        }
    }

    /**
     * Checks we have at least Java 1.4.
     *
     * @return true if the Java virtual machine is version 1.4.0 or above
     */
    private static boolean checkJavaVersion(  )
    {

        // Check for Java 1.4 +
        return isJavaVersionAtLeast( 1, 4, 0 );

    }

    /**
     * Checks whether the Java virtual machine is of the required version
     *
     * @param wantedMajor The major version number of Java required e.g. 1
     * @param wantedMinor The minor version number of Java required e.g. 4
     * @param wantedRevision The revision number of Java required e.g. 0
     *
     * @return true if the Java version is above that specified
     */
    private static boolean isJavaVersionAtLeast( 
        int wantedMajor, int wantedMinor, int wantedRevision )
    {

        // Find out the version from the system
        String versionString = System.getProperty( "java.version" );

        String[] splitVersion = new String[3];

        int pos = 0;

        int oldpos = 0;

        for( int i = 0; i < 3; i++ )
        {
            pos = versionString.indexOf( '.', oldpos );

            if( pos == -1 )
            {
                pos = versionString.indexOf( '_', oldpos );

            }

            if( pos == -1 )
            {
                pos = versionString.indexOf( '-', oldpos );

            }

            if( pos == -1 )
            {
                pos = versionString.length(  );

            }

            splitVersion[i] = versionString.substring( oldpos, pos );

            oldpos = pos + 1;

        }

        int actualMajor;

        int actualMinor;

        int actualRevision;

        // Parse the bits
        try
        {
            actualMajor = Integer.parseInt( splitVersion[0] );

            actualMinor = Integer.parseInt( splitVersion[1] );

            actualRevision = Integer.parseInt( splitVersion[2] );

        }

        catch( NumberFormatException ex )
        {
            FreeGuide.log.log( 
                Level.SEVERE, "Invalid java version : " + versionString, ex );

            return false;
        }

        // Check we have the required version
        if( 
            ( actualMajor > wantedMajor )
                || ( ( actualMajor == wantedMajor )
                && ( actualMinor > wantedMinor ) )
                || ( ( actualMajor == wantedMajor )
                && ( actualMinor == wantedMinor )
                && ( actualRevision >= wantedRevision ) ) )
        {

            return true;

        }

        // If not, we fail
        return false;

    }

    /**
     * Check that there is a config entry of text type
     *
     * @param pref Description of the Parameter
     * @param entry Description of the Parameter
     * @param failedWhat Description of the Parameter
     */
    private static void checkTextFailure( 
        FGPreferences pref, String entry, Vector failedWhat )
    {

        /*        String error =


            ConfigGuesser.checkValue(


                "misc", entry,


                FreeGuide.prefs.performSubstitutions( pref.get( entry ) ) );




        if( error != null )


        {


            failedWhat.add( error );


        }*/
    }

    /**
     * Check that there is a config entry of file type
     *
     * @param pref Description of the Parameter
     * @param entry Description of the Parameter
     * @param failedWhat Description of the Parameter
     */
    private static void checkFileFailure( 
        FGPreferences pref, String entry, Vector failedWhat )
    {

        /*


        String pr = FreeGuide.prefs.performSubstitutions( pref.get( entry ) );




        if( pr == null )


        {




            Object[] messageArguments = { entry };


            failedWhat.add(


                FreeGuide.getCompoundMessage(


                    "config_entry_misc_does_not_exist_template",


                    messageArguments ) );




            return;


        }




        String error =


            ConfigGuesser.checkValue( "misc", entry, new File( pr ) );




        if( error != null )


        {


            failedWhat.add( error );




        }*/
    }

    /**
     * processArgs Processes the commandline arguments and stores them.
     *
     * @param args The commandline arguments
     *
     * @return true if all is well, false otherwise.
     */
    private static boolean processArgs( String[] args )
    {

        Vector boolArgs = new Vector(  );

        boolArgs.add( "--install" );

        boolArgs.add( "--uninstall" );

        boolArgs.add( "--prefs" );

        // Process the command line arguments and store them
        FreeGuide.arguments = new CmdArgs( args, boolArgs );

        return FreeGuide.arguments.noErrors(  );

    }

    /**
     * setupLog Creates a log file.
     *
     * @return true if all is well, false otherwise
     */
    private static boolean setupLog(  )
    {

        // Set up the logger
        FreeGuide.log = Logger.getLogger( "org.freeguide-tv" );

        return true;

    }

    /**
     * setupPrefs Creates the preferences object that holds the configuration
     * info
     *
     * @return true if all is well, false otherwise
     */
    private static boolean setupPrefs(  )
    {

        // Make the object that holds all the Preferences objects
        //FreeGuide.prefs = new PreferencesGroup(  );
        //return FreeGuide.prefs.noErrors(  );
        return true;

    }
}

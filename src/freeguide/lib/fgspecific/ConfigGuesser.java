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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * A static class to check configuration options.
 *
 * @author Andy Balaam
 * @version 4
 */
public class ConfigGuesser
{

    private final static String fs = File.separator;

    /**
     * Description of the Method
     *
     * @param group Description of the Parameter
     * @param entry Description of the Parameter
     * @param val Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public static String checkValue( String group, String entry, Object val )
    {

        if( group.equals( "misc" ) )
        {

            if( entry.equals( "day_start_time" ) )
            {

                return checkValidTime( 
                    (String)val,
                    FreeGuide.msg.getString( "the_freeguide_day_start" ) );

            }
            else if( entry.equals( "grabber_start_time" ) )
            {

                return checkValidTime( 
                    (String)val,
                    FreeGuide.msg.getString( "the_grabber_start_time" ) );

            }
            else if( entry.equals( "days_to_grab" ) )
            {

                return checkNumeric( 
                    (String)val,
                    FreeGuide.msg.getString( "the_number_of_days_to_grab" ) );

            }
            else if( entry.equals( "working_directory" ) )
            {

                return checkDirWriteable( 
                    (File)val, FreeGuide.msg.getString( "a_working_directory" ) );

            }
            else if( entry.equals( "install_directory" ) )
            {

                return checkDirWriteable( 
                    (File)val,
                    FreeGuide.msg.getString( "the_install_directory" ) );

            }
            else if( entry.equals( "region" ) )
            {

                return checkNonEmpty( 
                    (String)val, FreeGuide.msg.getString( "region" ) );

            }
            else if( entry.equals( "browser" ) )
            {

                return null;

            }
            else if( entry.equals( "privacy" ) )
            {

                return null;

            }
        }

        String msg =
            FreeGuide.msg.getString( "check_asked_for_on_an_unknown_option" )
            + " \"" + group + "." + entry + "\".";

        if( FreeGuide.log != null )
        {
            FreeGuide.log.info( msg );
        }
        else
        {
            System.err.println( msg );
        }

        // If it's not one of these, don't check , just say it's good.
        return null;

    }

    /**
     * Description of the Method
     *
     * @param time Description of the Parameter
     * @param whatFor Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private static String checkValidTime( String time, String whatFor )
    {

        if( 
            ( time != null ) && ( time.length(  ) == 5 )
                && ( time.charAt( 2 ) == ':' ) )
        {

            return null;
        }

        Object[] messageArguments = { whatFor };

        return FreeGuide.getCompoundMessage( 
            FreeGuide.msg.getString( "the_time_you_gave_not_hhmm_template" ),
            messageArguments );
    }

    /**
     * Description of the Method
     *
     * @param number Description of the Parameter
     * @param whatFor Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private static String checkNumeric( String number, String whatFor )
    {

        if( number == null )
        {

            Object[] messageArguments = { whatFor };

            return FreeGuide.getCompoundMessage( 
                FreeGuide.msg.getString( "option_is_blank_template" ),
                messageArguments );
        }

        try
        {
            Integer.parseInt( number );
        }
        catch( NumberFormatException e )
        {

            Object[] messageArguments = { whatFor };

            return FreeGuide.getCompoundMessage( 
                FreeGuide.msg.getString( "option_not_numeric" ),
                messageArguments );

        }

        return null;
    }

    /**
     * Description of the Method
     *
     * @param number Description of the Parameter
     * @param whatFor Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private static String checkNonEmpty( String number, String whatFor )
    {

        if( ( number == null ) || number.equals( "" ) )
        {

            Object[] messageArguments = { whatFor };

            return FreeGuide.getCompoundMessage( 
                FreeGuide.msg.getString( "option_blank" ), messageArguments );
        }

        return null;
    }

    /**
     * Description of the Method
     *
     * @param dir Description of the Parameter
     * @param whatFor Description of the Parameter
     *
     * @return Description of the Return Value
     */
    private static String checkDirWriteable( File dir, String whatFor )
    {

        PreferencesGroup prefs = new PreferencesGroup(  );

        dir = new File( prefs.performSubstitutions( dir.toString(  ) ) );

        try
        {

            if( !dir.exists(  ) )
            {
                dir.mkdirs(  );
            }

            // Check it exists and you can write a file to it
            File testFile = new File( dir.getPath(  ) + "test.tmp" );
            testFile.createNewFile(  );

            if( dir.exists(  ) && testFile.canWrite(  ) )
            {
                testFile.delete(  );

                return null;
            }

            testFile.delete(  );

            Object[] messageArguments = { whatFor };

            return FreeGuide.getCompoundMessage( 
                FreeGuide.msg.getString( "dir_isnt_writeable" ),
                messageArguments );

        }
        catch( java.io.IOException e )
        {

            Object[] messageArguments = { whatFor };

            return FreeGuide.getCompoundMessage( 
                FreeGuide.msg.getString( "error_creating_dir" ),
                messageArguments );
        }
    }
}

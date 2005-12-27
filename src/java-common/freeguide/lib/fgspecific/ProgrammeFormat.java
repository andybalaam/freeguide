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

import freeguide.lib.fgspecific.data.TVProgramme;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Formats Programme information.
 */
public class ProgrammeFormat
{

    /** Plain text format */
    public final static int TEXT_FORMAT = 0;

    /**
     * HTML format including the "<code>&lt;html&gt;&lt;body&gt; ...
     * &lt;/body&gt;&lt;/html&gt;</code>" tags
     */
    public final static int HTML_FORMAT = 1;

    /**
     * HTML format without the <code>&lt;html&gt;&lt;body&gt; ...
     * &lt;/body&gt;&lt;/html&gt;</code> tags (for use in building a page
     * from many fragments)
     */
    public final static int HTML_FRAGMENT_FORMAT = 2;

    /** DOCUMENT ME! */
    public final static String LINE_FEED =
        System.getProperty( "line.separator", "\r\n" );
    private final static int MARGIN = 78;

    /** Format used for dates in the HTML links. */
    public static SimpleDateFormat LINK_DATE_FORMAT =
        new SimpleDateFormat( "yyyyMMddHHmmss" );
    private String newline = LINE_FEED;
    private final Pattern defaultWrapPattern =
        Pattern.compile( "(.{1," + MARGIN + "})(?:\\s|$)" );
    private int outputFormat = TEXT_FORMAT;
    private boolean printTimeDelta = false;
    private DateFormat dateFormat = null;
    private boolean wrap = false;
    private boolean onScreen = true;

    /*private static StringBuffer wrap( CharSequence input, int preferredMargin )
    {

        int newlineLength = newline.length(  );

        Pattern wrapPattern = defaultWrapPattern;

        if( preferredMargin != MARGIN )
        {
            wrapPattern = Pattern.compile( "(.{1," + MARGIN + "})(?:\\s|$)" );

        }

        Matcher m = wrapPattern.matcher( input );

        StringBuffer value = new StringBuffer( 300 );

        while( m.find(  ) )
        {
            m.appendReplacement( value, "$0" + newline );

        }

        // Shouldn't ever happen, but...
        m.appendTail( value );

        // Strip the trailing newline
        int length = value.length(  );

        int possibleStart = length - newlineLength;

        if( value.lastIndexOf( newline ) == possibleStart )
        {
            value.delete( possibleStart, length );

        }

        return value;

    }*/

    /**
     * Function that returns the time difference from now in a format like "2
     * hours and 1 minute"
     *
     * @param startTime starting time of the program
     * @param toAppend StringBuffer the resulting string gets added to
     */
    public static void calcTimeDelta( long startTime, StringBuffer toAppend )
    {

        // Get the current time and calculates the difference in minutes
        // from the starting time.  >0 means in the future
        long delta = startTime - System.currentTimeMillis(  );

        delta /= 60000;

        // If delta = 0 then it starts now and we leave as there's
        // nothing else to do
        if( delta == 0 )
        {
            toAppend.append( 
                Application.getInstance(  ).getLocalizedMessage( "starts_now" ) );

            return;

        }

        // Split delta in meaningful fields
        int days = (int)( delta / ( 24 * 60 ) );

        int hours = (int)( ( delta / 60 ) % 60 );

        int minutes = (int)( delta % 60 );

        if( delta > 0 )
        {

            if( days == 1 )
            {
                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "starts_in_1_day" ) );

            }

            else if( days > 1 )
            {

                Object[] messageArguments = { new Integer( days ) };

                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "starts_in_days_template", messageArguments ) );

            }

            else if( hours == 1 )
            {
                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "starts_in_1_hour" ) );

            }

            else if( hours > 1 )
            {

                Object[] messageArguments = { new Integer( hours ) };

                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "starts_in_hours_template", messageArguments ) );

            }

            else if( minutes == 1 )
            {
                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "starts_in_1_minute" ) );

            }

            else
            {

                Object[] messageArguments = { new Integer( minutes ) };

                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "starts_in_minutes_template", messageArguments ) );

            }
        }

        else
        {

            if( days == -1 )
            {
                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "started_1_day_ago" ) );

            }

            else if( days < -1 )
            {

                Object[] messageArguments = { new Integer( -days ) };

                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "started_days_ago_template", messageArguments ) );

            }

            else if( hours == -1 )
            {
                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "started_1_hour_ago" ) );

            }

            else if( hours < -1 )
            {

                Object[] messageArguments = { new Integer( -hours ) };

                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "started_hours_ago_template", messageArguments ) );

            }

            else if( minutes == -1 )
            {
                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "started_1_minute_ago" ) );

            }

            else
            {

                Object[] messageArguments = { new Integer( -minutes ) };

                toAppend.append( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "started_minutes_ago_template", messageArguments ) );

            }
        }
    }

    /**
     * Utility method to create a unique ASCII-only name (reference) to
     * identify each program in the HTML program guide.
     *
     * @param programme programme for create reference
     *
     * @return reference string
     */
    public static String createLinkReference( final TVProgramme programme )
    {

        String reference = null;

        // According to HTML spec, name must be unique and use only ASCII chars
        StringBuffer ref =
            new StringBuffer( 
                LINK_DATE_FORMAT.format( new Date( programme.getStart(  ) ) ) );

        ref.append( ';' );
        ref.append( programme.getChannel(  ).getID(  ) );

        try
        {
            reference = URLEncoder.encode( ref.toString(  ), "UTF-8" );
        }
        catch( UnsupportedEncodingException e )
        {

            // Won't happen.  All JVM's must support "UTF-8"
            // (and it's the character set recommended by the W3C).
        }

        return reference;
    }
}

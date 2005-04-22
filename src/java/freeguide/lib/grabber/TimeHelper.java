package freeguide.lib.grabber;

import freeguide.FreeGuide;

import freeguide.lib.general.LanguageHelper;

import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for parse dates and times.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class TimeHelper
{

    /** DOCUMENT ME! */
    public static TimeZone localTZ;
    protected static Map monthes;
    protected static Pattern reTime =
        Pattern.compile( "(\\d{1,2})[\\.|:|\\s](\\d{2})" );

    static
    {

        try
        {
            monthes = new HashMap(  );

            final String resourceName =
                TimeHelper.class.getPackage(  ).getName(  ).replace( '.', '/' )
                + "/monthes.utf8.properties";
            LanguageHelper.loadProperties( 
                TimeHelper.class.getClassLoader(  ).getResourceAsStream( 
                    resourceName ), monthes );
        }
        catch( IOException ex )
        {
            FreeGuide.log.log( Level.SEVERE, "Error read monthes names", ex );
        }
    }

    private static DateFormat xmltvFormat =
        new SimpleDateFormat( "yyyyMMddHHmmss Z" );
    private static DateFormat xmlatvFormat =
        new SimpleDateFormat( "yyyy-MM-dd HH:mm z" );

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static Pattern getTimePattern(  )
    {

        return reTime;
    }

    protected static int getMonth( String monthName ) throws ParseException
    {

        int i;

        try
        {
            i = Integer.parseInt( monthName );

            if( ( i >= 1 ) && ( i <= 12 ) )
            {

                return i - 1;
            }
        }
        catch( NumberFormatException e )
        {
        }

        String monthInd = (String)monthes.get( monthName.toLowerCase(  ) );

        if( monthInd != null )
        {

            return Integer.parseInt( monthInd );
        }

        throw new ParseException( 
            "Error parsing month name : " + monthName, 0 );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param day DOCUMENT_ME!
     * @param month DOCUMENT_ME!
     * @param year DOCUMENT_ME!
     * @param dow DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static long parseDate( 
        String day, String month, String year, String dow )
        throws ParseException
    {

        Calendar cal =
            Calendar.getInstance( 
                TimeZone.getTimeZone( "GMT" ), Locale.getDefault(  ) );

        int iyear = 0;
        int cm = getMonth( month );

        if( year != null )
        {

            try
            {
                iyear = Integer.parseInt( year );
            }
            catch( NumberFormatException e )
            {
                year = null;
            }
        }

        if( year == null )
        {
            iyear = cal.get( Calendar.YEAR );

            if( ( cal.get( Calendar.MONTH ) == 11 ) && ( cm == 0 ) )
            {
                iyear++;
            }
        }

        try
        {
            cal.set( iyear, cm, Integer.parseInt( day ), 0, 0, 0 );
        }
        catch( Exception e )
        {

            return -1;
        }

        cal.set( Calendar.MILLISECOND, 0 );

        return cal.getTime(  ).getTime(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param tm DOCUMENT_ME!
     * @param tz DOCUMENT_ME!
     * @param onDate DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static long parseTime( String tm, TimeZone tz, long onDate )
        throws ParseException
    {

        Matcher ma = reTime.matcher( tm );

        if( ma.matches(  ) )
        {

            try
            {

                int h = Integer.parseInt( ma.group( 1 ) );
                int m = Integer.parseInt( ma.group( 2 ) );

                if( ( h < 0 ) || ( h > 24 ) || ( m < 0 ) || ( m > 59 ) )
                {

                    return -1;
                }

                if( ( h == 24 ) && ( m > 0 ) )
                {

                    return -1;
                }

                return ( ( ( h * 60 ) + m ) * 60L * 1000 )
                - getOffset( onDate, tz );
            }
            catch( NumberFormatException ex )
            {
                throw new ParseException( "Error parsing time : " + tm, 0 );
            }
        }
        else
        {
            throw new ParseException( "Error parsing time : " + tm, 0 );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param onDate DOCUMENT_ME!
     * @param tz DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static long getOffset( long onDate, TimeZone tz )
    {

        Calendar c = Calendar.getInstance(  );
        c.setTimeZone( tz );
        c.setTime( new Date( onDate ) );

        return c.get( Calendar.ZONE_OFFSET ) + c.get( Calendar.DST_OFFSET );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param time DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static long parseXMLTVtime( String time ) throws ParseException
    {

        return xmltvFormat.parse( time ).getTime(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param time DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static long parseXMLATVtime( String time ) throws ParseException
    {

        return xmlatvFormat.parse( time ).getTime(  );
    }
}

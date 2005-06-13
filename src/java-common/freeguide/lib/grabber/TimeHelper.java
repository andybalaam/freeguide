package freeguide.lib.grabber;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannel;

import freeguide.lib.general.LanguageHelper;
import freeguide.lib.general.Time;

import java.io.InputStream;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Helper for parse dates and times.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class TimeHelper
{

    protected static Map monthes;

    /** Milliseconds per day. */
    public static final long MILLISECONDS_IN_DAY = 24L * 60L * 60L * 1000L;

    static
    {

        try
        {
            monthes = new HashMap(  );

            final String resourceName =
                TimeHelper.class.getPackage(  ).getName(  ).replace( '.', '/' )
                + "/monthes.utf8.properties";
            InputStream ins =
                TimeHelper.class.getClassLoader(  ).getResourceAsStream( 
                    resourceName );

            if( ins == null )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.SEVERE, "Monthes file not found" );
            }
            else
            {
                LanguageHelper.loadProperties( ins, monthes );
            }
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE, "Error read monthes names", ex );
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

        return LineProgrammeHelper.reTime;
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
     * @param tz DOCUMENT ME!
     * @param day DOCUMENT_ME!
     * @param month DOCUMENT_ME!
     * @param year DOCUMENT_ME!
     * @param dow DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static long getBaseDate( 
        TimeZone tz, String day, String month, String year, String dow )
        throws ParseException
    {

        Calendar cal = Calendar.getInstance( tz );

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
     * @param baseDate milliseconds of 00:00 of day, using timezone
     * @param prevTime DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public static long correctTime( 
        Time tm, final long baseDate, final long prevTime )
    {

        long newTime =
            baseDate
            + ( ( ( tm.getHours(  ) * 60 ) + tm.getMinutes(  ) ) * 60L * 1000 );

        // check for new time is after midnight
        if( ( prevTime != 0 ) && ( newTime < prevTime ) )
        {
            newTime += MILLISECONDS_IN_DAY;

            if( ( newTime - prevTime ) > TVChannel.PROG_LENGTH_MAX )
            {
                newTime -= MILLISECONDS_IN_DAY;
            }
        }

        return newTime;
    }
}

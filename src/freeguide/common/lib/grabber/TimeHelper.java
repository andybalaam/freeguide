package freeguide.common.lib.grabber;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.general.Time;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.text.ParseException;

import java.util.Calendar;
import java.util.Properties;
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
    protected static Properties months;

    /** Milliseconds per day. */
    public static final long MILLISECONDS_IN_DAY = 24L * 60L * 60L * 1000L;

    static
    {
        months = new Properties(  );

        final InputStream in =
            TimeHelper.class.getClassLoader(  )
                            .getResourceAsStream( 
                "resources.main.months.properties2" );

        try
        {
            if( in == null )
            {
                throw new FileNotFoundException(  );
            }

            months.load( in );
            in.close(  );
        }
        catch( IOException ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( Level.SEVERE, "Error reading month names", ex );
            throw new RuntimeException( "Error reading months.properties", ex );
        }
    }

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

        String monthInd = (String)months.get( monthName.toLowerCase(  ) );

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

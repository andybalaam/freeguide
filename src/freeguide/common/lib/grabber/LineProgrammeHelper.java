package freeguide.common.lib.grabber;

import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.lib.general.Time;

import freeguide.common.plugins.ILogger;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for parse programme's line.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class LineProgrammeHelper
{

    protected static Pattern reProgram =
        Pattern.compile( 
            "^(\\d{1,2}[\\.|:]\\d{2})([ |,]*)(.+)$", Pattern.CASE_INSENSITIVE );
    protected static Pattern reTime =
        Pattern.compile( "(\\d{1,2})[\\.|:|\\s](\\d{2})" );
    protected static Pattern reTimeUS =
        Pattern.compile( "(\\d{1,2}):(\\d{2})\\s+([A|P]M)" );

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isProgram( String str )
    {

        Matcher m = reProgram.matcher( str );

        return m.matches(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param logger DOCUMENT_ME!
     * @param str DOCUMENT_ME!
     * @param baseDate DOCUMENT_ME!
     * @param prevTime DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static TVProgramme[] parse( 
        ILogger logger, String str, long baseDate, long prevTime )
        throws ParseException
    {

        List timelist = new ArrayList(  );

        String p = str;

        while( true )
        {

            Matcher m = reProgram.matcher( p );

            if( !m.matches(  ) )
            {

                break;

            }

            try
            {
                timelist.add( parseTime( m.group( 1 ) ) );
            }

            catch( ParseException ex )
            {
                logger.warning( 
                    "Error parse time '" + m.group( 1 ) + "' on line: " + str );

            }

            p = m.group( 3 );

        }

        if( timelist.size(  ) == 0 )
        {
            throw new ParseException( "Bad line format on line :" + str, 0 );

        }

        TVProgramme[] result = new TVProgramme[timelist.size(  )];

        for( int i = 0; i < timelist.size(  ); i++ )
        {

            Time tm = ( (Time)timelist.get( i ) );

            TVProgramme prog = new TVProgramme(  );

            prog.setStart( TimeHelper.correctTime( tm, baseDate, prevTime ) );

            prog.setTitle( p );

            result[i] = prog;
            prevTime = prog.getStart(  );

        }

        return result;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param tm DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static Time parseTime( String tm ) throws ParseException
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
                    new ParseException( "Error parsing time : " + tm, 0 );
                }

                if( ( h == 24 ) && ( m > 0 ) )
                {
                    new ParseException( "Error parsing time : " + tm, 0 );
                }

                return new Time( h, m );
            }
            catch( NumberFormatException ex )
            {
                throw new ParseException( "Error parsing time : " + tm, 0 );
            }
        }
        else
        {

            Matcher maUS = reTimeUS.matcher( tm );

            if( maUS.matches(  ) )
            {

                try
                {

                    int h = Integer.parseInt( maUS.group( 1 ) );
                    int m = Integer.parseInt( maUS.group( 2 ) );
                    boolean isPM = "PM".equals( maUS.group( 3 ) );

                    if( ( h < 1 ) || ( h > 12 ) || ( m < 0 ) || ( m > 59 ) )
                    {
                        new ParseException( "Error parsing time : " + tm, 0 );
                    }

                    if( isPM && ( h < 12 ) )
                    {
                        h += 12;
                    }
                    else if( !isPM && ( h == 12 ) )
                    {
                        h -= 12;
                    }

                    return new Time( h, m );
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
    }
}

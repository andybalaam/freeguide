package freeguide.lib.grabber;

import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ILogger;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
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
     * @param basedate DOCUMENT_ME!
     * @param htz DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws ParseException DOCUMENT_ME!
     */
    public static TVProgramme[] parse( 
        ILogger logger, String str, long basedate, TimeZone htz )
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
                timelist.add( 
                    new Long( 
                        TimeHelper.parseTime( m.group( 1 ), htz, basedate ) ) );

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

            long tm = ( (Long)timelist.get( i ) ).longValue(  );

            TVProgramme prog = new TVProgramme(  );

            prog.setStart( tm + basedate );

            prog.setTitle( p );

            result[i] = prog;

        }

        return result;

    }
}

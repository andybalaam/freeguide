package freeguide.plugins.grabber.hallmark;

import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Timezones mapping for hallmarkchannel.com sites.
 * http://www.nationsonline.org/oneworld/capitals_europe.htm
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class HallmarkTimeZones
{

    protected static String[] LIST = new String[] { "MSK", "Europe/Moscow", };
    protected static Map TIMEZONES;

    /**
     * DOCUMENT_ME!
     *
     * @param name DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static synchronized TimeZone getTimeZone( final String name )
    {

        if( TIMEZONES == null )
        {
            createTimeZones(  );
        }

        return (TimeZone)TIMEZONES.get( name );
    }

    protected static void createTimeZones(  )
    {
        TIMEZONES = new TreeMap(  );

        for( int i = 0; i < LIST.length; i += 2 )
        {
            TIMEZONES.put( LIST[i], TimeZone.getTimeZone( LIST[i + 1] ) );
        }
    }
}

package freeguide.common.lib.fgspecific.data.test;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.data.TVProgrammeStartTimeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Iterator;

public class TestTVProgrammeStartTimeComparator
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        new TestTVProgrammeStartTimeComparator().runTests();
    }

    private void runTests()
    {
        testSortingProgrammes();
        System.out.println( "All tests passed" );
    }

    private void testSortingProgrammes()
    {
        List result = new ArrayList(  );

        TVProgramme prog08to10 = new TVProgramme();
        prog08to10.setStart( getLongTime(  8,  0 ) );
        prog08to10.setEnd(   getLongTime( 10,  0 ) );
        result.add( prog08to10 );

        TVProgramme prog07to09 = new TVProgramme();
        prog07to09.setStart( getLongTime(  7,  0 ) );
        prog07to09.setEnd(   getLongTime(  9,  0 ) );
        result.add( prog07to09 );

        TVProgramme prog06to08 = new TVProgramme();
        prog06to08.setStart( getLongTime(  6,  0 ) );
        prog06to08.setEnd(   getLongTime(  8,  0 ) );
        result.add( prog06to08 );

        Collections.sort( result, new TVProgrammeStartTimeComparator() );

        // They are sorted even though they came in in the wrong order,
        // and they overlap
        Iterator it = result.iterator();
        assert it.next() == prog06to08;
        assert it.next() == prog07to09;
    }

    private long getLongTime(int hour, int minute)
    {
        return new GregorianCalendar( 2008, 06, 06, hour, minute ).getTimeInMillis();
    }

}

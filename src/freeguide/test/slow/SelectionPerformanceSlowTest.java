package freeguide.test.slow;

import java.util.Calendar;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.ManualSelection;
import freeguide.test.FreeGuideTest;

public class SelectionPerformanceSlowTest
{
    /** A number suitable for doing a lot of in-memory things */
    private static final int LARGE_NUMBER = 100000000;

    public void run() throws Exception
    {
        test_matches();
    }

    private void test_matches() throws Exception
    {
        TVChannel chan = new TVChannel( "chan1" );

        TVProgramme prog = new TVProgramme();
        prog.setTitle( "My Programme" );
        prog.setChannel( chan );

        ManualSelection sel = new ManualSelection( prog, true );

        FreeGuideTest.my_assert( sel.matches( prog ) );

        Calendar start = Calendar.getInstance();
        for( int i = 0; i < LARGE_NUMBER; i++ )
        {
            sel.matches( prog );
        }
        Calendar end = Calendar.getInstance();

        String timePerMatch = FreeGuideTest.Cals2SecsInterval( start, end );

        System.out.println( "SelectionPerformanceSlowTest.test_matches: "
            + timePerMatch + " secs for " + LARGE_NUMBER / 1000000
            + " million calls to match" );
    }
}

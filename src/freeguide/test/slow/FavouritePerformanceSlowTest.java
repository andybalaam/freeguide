package freeguide.test.slow;

import java.util.Calendar;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.test.FreeGuideTest;

public class FavouritePerformanceSlowTest
{
    /** A number suitable for doing a lot of in-memory things */
    private static final int LARGE_NUMBER = 100000000;

    public void run() throws Exception
    {
        test_matches_title();
    }

    private void test_matches_title() throws Exception
    {
        TVProgramme prog = new TVProgramme();
        prog.setTitle( "My Programme" );

        Favourite fav = new Favourite();
        fav.setTitleContains( "no match" );

        FreeGuideTest.my_assert( !fav.matches( prog ) );

        Calendar start = Calendar.getInstance();
        for( int i = 0; i < LARGE_NUMBER; i++ )
        {
            fav.matches( prog );
        }
        Calendar end = Calendar.getInstance();

        String timePerMatch = FreeGuideTest.Cals2SecsInterval( start, end );

        System.out.println( "FavouritePerformanceSlowTest.test_matches_title: "
            + timePerMatch + " secs for " + LARGE_NUMBER
            + " calls to match" );
    }
}

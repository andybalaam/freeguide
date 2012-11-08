package freeguide.test.slow;

import java.util.Calendar;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.test.FreeGuideTest;

public class FavouriteDescriptionPerformanceSlowTest
{
    /** A number suitable for doing a lot of in-memory things */
    private static final int LARGE_NUMBER = 10000;

    public void run() throws Exception
    {
        testMatchesDescription();
    }

    private void testMatchesDescription() throws Exception
    {
        TVProgramme prog1 = new TVProgramme();
        prog1.setTitle( "prog1" );
        prog1.setStart( 1000 );
        prog1.setDescription( "description1" );
        prog1.setExtraTag( "tagname1", "episode-num", "EP14" );
        prog1.setExtraTag( "tagname2", "Actor", "actor1" );
        prog1.setExtraTag( "tagname2", "Actor", "actor2" );
        prog1.setExtraTag( "tagname2", "Actor", "actor3" );
        prog1.setExtraTag( "tagname2", "Actor", "actor4" );
        prog1.setExtraTag( "tagname2", "Actor", "actor5" );
        prog1.setExtraTag( "tagname2", "Actor", "actor6" );

        Favourite fav = new Favourite();
        fav.setDescriptionContains( "no match" );

        FreeGuideTest.my_assert( !fav.matches( prog1 ) );

        Calendar start = Calendar.getInstance();
        for( int i = 0; i < LARGE_NUMBER; i++ )
        {
            fav.matches( prog1 );
        }
        Calendar end = Calendar.getInstance();

        String timePerMatch = FreeGuideTest.Cals2SecsInterval( start, end );

        System.out.println( "FavouriteDescriptionPerformanceSlowTest.testMatchesDescription: "
            + timePerMatch + " secs for " + LARGE_NUMBER
            + " calls to match" );
    }
}

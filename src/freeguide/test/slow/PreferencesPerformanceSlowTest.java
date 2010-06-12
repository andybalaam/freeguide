package freeguide.test.slow;

import java.util.Calendar;
import java.util.prefs.Preferences;

import freeguide.test.FreeGuideTest;

public class PreferencesPerformanceSlowTest
{
    /** A number suitable for doing a lot of in-memory things */
    private static final int LARGE_NUMBER = 100000;

    public void run() throws Exception
    {
        test_save_and_load();
    }

    private void test_save_and_load() throws Exception
    {
        Preferences testPutNode = Preferences.userRoot().node(
            "freeguide-preferences-performance-test" );

        Calendar start = Calendar.getInstance();
        for( int i = 0; i < LARGE_NUMBER; i++ )
        {
            testPutNode.put( "k" + i, "v" + i );
        }
        Calendar preFlushEnd = Calendar.getInstance();
        testPutNode.sync();
        Calendar postFlushEnd = Calendar.getInstance();

        String timeForPut = FreeGuideTest.Cals2SecsInterval( start,
            preFlushEnd );

        System.out.println( "PreferencesPerformanceSlowTest."
            + "test_save_and_load save: "
            + timeForPut + " secs for " + LARGE_NUMBER
            + " calls to put" );

        String timeForFlush = FreeGuideTest.Cals2SecsInterval( start,
            postFlushEnd );

        System.out.println( "PreferencesPerformanceSlowTest."
            + "test_save_and_load save: "
            + timeForFlush + " secs for flush" );

        // -----------------------

        Preferences testGetNode = Preferences.userRoot().node(
            "freeguide-preferences-performance-test" );

        start = Calendar.getInstance();

        for( int i = 0; i < LARGE_NUMBER; i++ )
        {
            String s = testGetNode.get( "k" + i, null );
            FreeGuideTest.my_assert( s.equals( "v" + i ) );
        }

        Calendar end = Calendar.getInstance();

        String timeForGet = FreeGuideTest.Cals2SecsInterval( start,
            end );

        System.out.println( "PreferencesPerformanceSlowTest."
            + "test_save_and_load load: "
            + timeForGet + " secs for " + LARGE_NUMBER + " gets" );

        testGetNode.removeNode();
    }
}

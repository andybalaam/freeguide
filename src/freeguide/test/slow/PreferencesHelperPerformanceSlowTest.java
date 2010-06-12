package freeguide.test.slow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.Preferences;

import freeguide.test.FreeGuideTest;
import freeguide.common.lib.general.PreferencesHelper;

import freeguide.common.lib.fgspecific.selection.Favourite;

public class PreferencesHelperPerformanceSlowTest
{
    /** A number suitable for doing a lot of in-memory things */
    private static final int LARGE_NUMBER = 10000;

    public void run() throws Exception
    {
        //test_strings();
        test_favourites();
    }

    public static class StringConfig
    {
        public static final Class stringList_TYPE = String.class;
        public List stringList = new ArrayList();
    }

    public static class FavConfig
    {
        public static final Class favList_TYPE = Favourite.class;
        public List favList = new ArrayList();
    }

    private void test_strings() throws Exception
    {
        StringConfig config = new StringConfig();

        for( int i = 0; i < LARGE_NUMBER; i++ )
        {
            config.stringList.add( "sel" + i );
        }

        Preferences testNode = Preferences.userRoot().node(
            "freeguide-preferenceshelper-performance-test" );

        Calendar start = Calendar.getInstance();

        PreferencesHelper helper = new PreferencesHelper();
        helper.save( testNode, config );

        Calendar end = Calendar.getInstance();

        String timeForSave = FreeGuideTest.Cals2SecsInterval( start,
            end );

        System.out.println( "PreferencesHelperPerformanceSlowTest."
            + "test_save_and_load save: "
            + timeForSave + " secs to save " + LARGE_NUMBER
            + " strings" );

        StringConfig config2 = new StringConfig();

        start = Calendar.getInstance();
        helper.load( testNode, config2 );
        end = Calendar.getInstance();

        FreeGuideTest.my_assert( config2.stringList.size() == LARGE_NUMBER );

        String timeForLoad = FreeGuideTest.Cals2SecsInterval( start,
            end );

        System.out.println( "PreferencesHelperPerformanceSlowTest."
            + "test_save_and_load load: "
            + timeForLoad + " secs to load " + LARGE_NUMBER
            + " strings" );

        testNode.removeNode();
    }

    private void test_favourites() throws Exception
    {
        FavConfig config = new FavConfig();

        for( int i = 0; i < LARGE_NUMBER; i++ )
        {
            Favourite fav = new Favourite();
            fav.name = "favname" + i;
            fav.titleString = "titlestring" + i;

            config.favList.add( fav );
        }

        Preferences testNode = Preferences.userRoot().node(
            "freeguide-preferenceshelper-performance-test" );

        Calendar start = Calendar.getInstance();

        PreferencesHelper helper = new PreferencesHelper();
        helper.save( testNode, config );

        Calendar end = Calendar.getInstance();

        String timeForSave = FreeGuideTest.Cals2SecsInterval( start,
            end );

        System.out.println( "PreferencesHelperPerformanceSlowTest."
            + "test_save_and_load save: "
            + timeForSave + " secs to save " + LARGE_NUMBER
            + " favourites" );

        FavConfig config2 = new FavConfig();

        start = Calendar.getInstance();
        helper.load( testNode, config2 );
        end = Calendar.getInstance();

        FreeGuideTest.my_assert( config2.favList.size() == LARGE_NUMBER );

        String timeForLoad = FreeGuideTest.Cals2SecsInterval( start,
            end );

        System.out.println( "PreferencesHelperPerformanceSlowTest."
            + "test_save_and_load load: "
            + timeForLoad + " secs to load " + LARGE_NUMBER
            + " favourites" );

        testNode.removeNode();
    }
}

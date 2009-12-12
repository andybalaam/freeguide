
package freeguide.test.fast;

import java.util.ArrayList;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.search.SearchProgrammesThread;
import freeguide.common.lib.fgspecific.search.SearchResultsHolder;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.test.FreeGuideTest;

public class SearchProgrammesThreadFastTest
{
    private class FakeTVData extends TVData
    {
        FakeTVData()
        {
            TVProgramme prog1 = new TVProgramme();
            prog1.setTitle( "prog1" );
            prog1.setStart( 1000 );
            TVProgramme prog2 = new TVProgramme();
            prog2.setTitle( "prog2" );
            prog2.setStart( 1001 );
            TVChannel chan1 = new TVChannel( "channel1" );
            chan1.setDisplayName( "Channel One" );
            chan1.put( prog1 );
            chan1.put( prog2 );
            channels.put( chan1.getID(), chan1 );

            TVProgramme prog3 = new TVProgramme();
            prog3.setTitle( "prog3" );
            prog3.setStart( 1002 );
            TVChannel chan2 = new TVChannel( "channel2" );
            chan2.setDisplayName( "Channel Two" );
            chan2.put( prog3 );

            channels.put( chan2.getID(), chan2 );
        }
    }

    private void testFindAll() throws Exception
    {
        SearchResultsHolder holder = new SearchResultsHolder();
        FakeTVData dt = new FakeTVData();

        Favourite allFav = new Favourite();

        SearchProgrammesThread spt = new SearchProgrammesThread(
            holder, dt, allFav );

        spt.start();

        spt.join();

        ArrayList<TVProgramme> resultList = new ArrayList<TVProgramme>();
        FreeGuideTest.my_assert( holder.getNewResults( resultList ) );
        FreeGuideTest.my_assert( resultList.size() == 3 );

        // Note: really the order could be different, so this is over-specific
        FreeGuideTest.my_assert( resultList.get( 0 ).getTitle() == "prog1" );
        FreeGuideTest.my_assert( resultList.get( 1 ).getTitle() == "prog2" );
        FreeGuideTest.my_assert( resultList.get( 2 ).getTitle() == "prog3" );
    }

    private void testFindByTitle() throws Exception
    {
        SearchResultsHolder holder = new SearchResultsHolder();
        FakeTVData dt = new FakeTVData();

        Favourite titleProg1Fav = new Favourite();
        titleProg1Fav.setTitleString( "prog3" );

        SearchProgrammesThread spt = new SearchProgrammesThread(
            holder, dt, titleProg1Fav );

        spt.start();

        spt.join();

        ArrayList<TVProgramme> resultList = new ArrayList<TVProgramme>();
        FreeGuideTest.my_assert( holder.getNewResults( resultList ) );
        FreeGuideTest.my_assert( resultList.size() == 1 );
        FreeGuideTest.my_assert( resultList.get( 0 ).getTitle() == "prog3" );
    }

    private void testFindByChannel() throws Exception
    {
        SearchResultsHolder holder = new SearchResultsHolder();
        FakeTVData dt = new FakeTVData();

        Favourite titleProg1Fav = new Favourite();
        titleProg1Fav.setChannelID( "channel1" );

        SearchProgrammesThread spt = new SearchProgrammesThread(
            holder, dt, titleProg1Fav );

        spt.start();

        spt.join();

        ArrayList<TVProgramme> resultList = new ArrayList<TVProgramme>();
        FreeGuideTest.my_assert( holder.getNewResults( resultList ) );
        FreeGuideTest.my_assert( resultList.size() == 2 );

        // Note: really the order could be different, so this is over-specific
        FreeGuideTest.my_assert( resultList.get( 0 ).getTitle() == "prog1" );
        FreeGuideTest.my_assert( resultList.get( 1 ).getTitle() == "prog2" );
    }

    public void run() throws Exception
    {
        Application.setInstance( new FakeApplication() ); // Needed for getTimeZone

        testFindAll();
        testFindByTitle();
        testFindByChannel();
    }
}


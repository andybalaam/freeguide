
package freeguide.test.fast;

import java.util.ArrayList;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.search.SearchResultsHolder;
import freeguide.test.FreeGuideTest;

public class SearchResultsHolderFastTest
{

    private void testBasicAdd() throws Exception
    {
        SearchResultsHolder holder = new SearchResultsHolder();

        ArrayList<TVProgramme> resultList = new ArrayList<TVProgramme>();
        FreeGuideTest.my_assert( !holder.getNewResults( resultList ) );
        FreeGuideTest.my_assert( resultList.isEmpty() );

        TVProgramme prog = new TVProgramme();
        holder.addResult( prog );

        FreeGuideTest.my_assert( holder.getNewResults( resultList ) );
        FreeGuideTest.my_assert( resultList.size() == 1 );
    }

    private void test2Threads() throws Exception
    {
        class GenerateResultsThread extends Thread
        {
            private SearchResultsHolder resultsHolder;
            private boolean cont = true;

            GenerateResultsThread( SearchResultsHolder resultsHolder )
            {
                this.resultsHolder = resultsHolder;
            }

            public synchronized void sendStop()
            {
                cont = false;
            }

            public void run()
            {
                try
                {
                    int i = 1;
                    while( cont )
                    {
                        Thread.sleep( 10 );
                        TVProgramme prog = new TVProgramme();
                        prog.setTitle( "prog" + i );
                        this.resultsHolder.addResult( prog );
                        ++i;
                    }
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }

        class CollectResultsThread extends Thread
        {
            private SearchResultsHolder resultsHolder =
                new SearchResultsHolder();
            private boolean cont = true;
            public ArrayList<TVProgramme> resultsList = new ArrayList<TVProgramme>();
            public boolean newResults = false;

            CollectResultsThread( SearchResultsHolder resultsHolder )
            {
                this.resultsHolder = resultsHolder;
            }

            public synchronized void sendStop()
            {
                cont = false;
            }

            public void run()
            {
                try
                {
                    while( cont )
                    {
                        Thread.sleep( 7 );
                        newResults = resultsHolder.getNewResults( resultsList );
                    }
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
            }
        }

        SearchResultsHolder resultsHolder = new SearchResultsHolder();
        GenerateResultsThread gen = new GenerateResultsThread( resultsHolder );
        CollectResultsThread col = new CollectResultsThread( resultsHolder );
        gen.start();
        col.start();
        Thread.sleep( 30 );
        gen.sendStop();
        col.sendStop();
        Thread.sleep( 11 );

        FreeGuideTest.my_assert( col.resultsList.size() > 0 );
        FreeGuideTest.my_assert( col.newResults );
    }

    public void run() throws Exception
    {
        testBasicAdd();
        test2Threads();
    }
}


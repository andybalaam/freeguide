
package freeguide.common.lib.fgspecific.search;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;

public class SearchProgrammesThread extends Thread
{
    class ProgIt extends TVIteratorProgrammes
    {
        protected void onChannel( TVChannel channel )
        {
        }

        protected void onProgramme( TVProgramme programme )
        {
            if( searchFavourite.matches( programme ) )
            {
                resultsHolder.addResult( programme );
            }
        }
    }

    private SearchResultsHolder resultsHolder;
    private TVData data;
    private Favourite searchFavourite;

    private ProgIt progIt = new ProgIt();

    public SearchProgrammesThread( SearchResultsHolder resultsHolder,
        TVData data, Favourite searchFavourite )
    {
        this.resultsHolder = resultsHolder;
        this.data = data;
        this.searchFavourite = searchFavourite;
    }

    public synchronized void sendStop()
    {
        progIt.stopIterating();
    }

    public void run()
    {
        data.iterate( progIt );
    }
}



package freeguide.common.lib.fgspecific.search;

import java.util.ArrayList;
import java.util.List;

import freeguide.common.lib.fgspecific.data.TVProgramme;

public class SearchResultsHolder
{
    private boolean changed = false;
    private ArrayList<TVProgramme> results = new ArrayList<TVProgramme>();

    public synchronized boolean getNewResults( List<TVProgramme> resultsCopy )
    {
        if( changed )
        {
            resultsCopy.clear();
            resultsCopy.addAll( results );
            changed = false;
            return true;
        }
        return false;
    }

    public synchronized void clearResults()
    {
        changed = true;
        results.clear();
    }

    public synchronized void addResult( TVProgramme prog )
    {
        changed = true;
        results.add( prog );
    }
}




package freeguide.common.lib.fgspecific.search;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JOptionPane;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.plugininterfaces.IModuleStorage;

public class ProgrammeSearcher
{
    private final static SimpleDateFormat dayTimeFormat =
        new SimpleDateFormat( "EE HH:mm" );

    public int run( String searchString, PrintStream out, PrintStream err )
    {
        // Eventually we will parse searchString for e.g. "channel=X;title=Y"
        // but for now we just assume the user is searching for "title contains"

        Favourite titleSearchFav = new Favourite();
        titleSearchFav.setTitleContains( searchString );

        SearchResultsHolder resultsHolder = new SearchResultsHolder();

        IModuleStorage.Info info = Application.getInstance()
            .getDataStorage()
            .getInfo()
            .cloneInfo();
        info.channelsList = null;

        try
        {
            TVData data = Application.getInstance(  ).getDataStorage(  ).get( info );

            SearchProgrammesThread searcher = new SearchProgrammesThread(
                resultsHolder, data, titleSearchFav );

            searcher.start();

            ArrayList<TVProgramme> results = new ArrayList<TVProgramme>();
            int numAlreadyPrinted = fetchResults( resultsHolder, out, results, 0 );
            while( searcher.isAlive() )
            {
                Thread.sleep( 100 );

                numAlreadyPrinted = fetchResults( resultsHolder, out, results, numAlreadyPrinted );
            }

            if( numAlreadyPrinted == 0 )
            {
                // TODO: translate!
                err.println( "---No search results found for search string '"
                    + searchString + "'---" );

                return 3;
            }
        }
        catch( Exception e )
        {
            e.printStackTrace( err );
            return 2;
        }

        return 0;
    }

    /**
     * @param out
     * @param resultsHolder
     * @param results
     * @param numAlreadyPrinted
     * @return
     */
    private int fetchResults( SearchResultsHolder resultsHolder, PrintStream out, ArrayList<TVProgramme> results, int numAlreadyPrinted )
    {
        if( resultsHolder.getNewResults( results ) )
        {
            for( int i = numAlreadyPrinted; i < results.size(); ++i )
            {
                printProgramme( out, results.get( i ) );
                ++numAlreadyPrinted;
            }
        }
        return numAlreadyPrinted;
    }

    private void printProgramme( PrintStream out, TVProgramme prog )
    {
        out.print( dayTimeFormat.format( new Date( prog.getStart() ) ) );
        out.print( " " );
        out.print( prog.getTitle() );
        if( prog.getSubTitle() != null )
        {
            out.print( ": " );
            out.print( prog.getSubTitle() );
        }
        out.print( " (" );
        out.print( prog.getChannel().getDisplayName() );
        out.println( ")" );

        out.flush();
    }
}

package freeguide.test;

import freeguide.test.slow.*;
import freeguide.test.fast.*;

public class FreeGuideTest
{
    public static void main( String[] args )
    {
        try
        {
            new PathSearcherFastTest().run();
            new BrowserFastTest().run();
            new BadUTF8FastTest().run();
            new TVProgrammeHashCodeFastTest().run();
            new ImportFastTest().run();
            new XMLTVExportFilterFastTest().run();
            new DeleteSelectionsFastTest().run();
            new SearchResultsHolderFastTest().run();
            new SearchProgrammesThreadFastTest().run();

            System.out.println( "All fast tests passed." );

            if( args.length > 0 )
            {
                if( args[0].equals( "--slow" ) )
                {
                    new ImportPerformanceSlowTest().run();
                    new ImportTwiceSlowTest().run();
                    new DisplayDocsSlowTest().run();

                    System.out.println( "All slow tests passed." );
                }
                else
                {
                    System.err.println( "Unrecognised argument '" + args[0]
                        + "'" );
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            System.exit( 1 );
        }
    }

    public static void my_assert( boolean condition )
        throws MyAssertFailureException
    {
        if( !condition )
        {
            throw new MyAssertFailureException( "Assertion failed" );
        }
    }
}

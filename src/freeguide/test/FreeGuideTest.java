package freeguide.test;

import java.text.DecimalFormat;
import java.util.Calendar;

import freeguide.test.slow.*;
import freeguide.test.fast.*;

public class FreeGuideTest
{
    private static final DecimalFormat number_format =
        new DecimalFormat( "####0.0000000000" );

    public static void main( String[] args )
    {
        try
        {
            new GrabberXMLTVFastTest().run();
            new DateParsingFastTest().run();
            new PathSearcherFastTest().run();
            new BrowserFastTest().run();
            new MigrationFastTest().run();
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
                    new PreferencesHelperPerformanceSlowTest().run();
                    new PreferencesPerformanceSlowTest().run();
                    new FavouritePerformanceSlowTest().run();
                    new SelectionPerformanceSlowTest().run();
                    new PathSearcherSlowTest().run();
                    new ImportPerformanceSlowTest().run();
                    new ImportTwiceSlowTest().run();
                    new DisplayDocsSlowTest().run();

                    System.out.println( "All slow tests passed." );
                }
                else
                {
                    System.err.println( "Unrecognised argument '" + args[0]
                        + "'" );
                    System.exit( 2 );
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

    public static String Cals2SecsInterval( Calendar start, Calendar end )
    {
        return number_format.format(
            0.001*(double)( end.getTimeInMillis() - start.getTimeInMillis() ) );
    }
}

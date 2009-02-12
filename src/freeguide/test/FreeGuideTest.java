package freeguide.test;

import freeguide.test.slow.*;
import freeguide.test.fast.*;

public class FreeGuideTest
{
    public static void main( String[] args )
    {
        try
        {
            new BadUTF8FastTest().run();
            new TVProgrammeHashCodeFastTest().run();
            new ImportFastTest().run();
            new ImportTwiceSlowTest().run();
            // Disabled since fails from cmd line new DisplayDocsSlowTest().run();

            System.out.println( "All tests passed." );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

    public static void my_assert( boolean condition ) throws MyAssertFailureException
    {
        if( !condition )
        {
            throw new MyAssertFailureException( "Assertion failed" );
        }
    }
}

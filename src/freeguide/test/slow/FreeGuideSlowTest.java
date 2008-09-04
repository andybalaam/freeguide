package freeguide.test.slow;


public class FreeGuideSlowTest
{
    public static void main( String[] args )
    {
        try
        {
            new ImportSlowTest().run();
            new DisplayDocsSlowTest().run();

            System.out.println( "Slow tests passed." );
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

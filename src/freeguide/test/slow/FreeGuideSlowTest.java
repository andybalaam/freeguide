package freeguide.test.slow;

public class FreeGuideSlowTest
{
    public static void main( String[] args )
    {
        try
        {
            new ImportSlowTest().run();

            System.out.println( "Slow tests passed." );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
}

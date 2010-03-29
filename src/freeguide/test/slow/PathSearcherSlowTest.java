package freeguide.test.slow;

import java.io.File;

import freeguide.common.lib.general.PathSearcher;
import freeguide.test.FreeGuideTest;

public class PathSearcherSlowTest
{
    public void run() throws Exception
    {
        test_getPathDirs();
        test_existsAndIsExecutable();
    }

    public void test_getPathDirs() throws Exception
    {
        String pathSep = System.getProperty( "path.separator" );

        PathSearcher pathSearcher = new PathSearcher();

        String[] pathDirs = pathSearcher.getPathDirs();

        String result = new String();
        for( int i = 0; i < pathDirs.length; i++ )
        {
            if( i != 0 )
            {
                result += pathSep;
            }
            result += pathDirs[i];
        }

        FreeGuideTest.my_assert( result.equals( System.getenv( "PATH" ) ) );
    }

    public void test_existsAndIsExecutable() throws Exception
    {
        PathSearcher pathSearcher = new PathSearcher();

        // Create a (hopefully executable) file
        File existandexe = File.createTempFile( "freeguide", "existandexe" );
        existandexe.setExecutable( true );

        // Create a (hopefully not executable) file
        File exist = File.createTempFile( "freeguide", "exist" );
        exist.setExecutable( false );

        // Create and delete a file
        File noexist = File.createTempFile( "freeguide", "noexist" );
        noexist.delete();

        // File exists and should be executable
        FreeGuideTest.my_assert( pathSearcher.existsAndIsExecutable(
            existandexe ) == existandexe.canExecute() );

        // File exists and might not be executable
        FreeGuideTest.my_assert( pathSearcher.existsAndIsExecutable(
            exist ) == exist.canExecute() );

        // File does not exist
        FreeGuideTest.my_assert( ! pathSearcher.existsAndIsExecutable(
            noexist ) );

        existandexe.delete();
        exist.delete();
    }

}

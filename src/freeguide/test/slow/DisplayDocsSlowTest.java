package freeguide.test.slow;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import freeguide.common.lib.fgspecific.DisplayDocs;
import freeguide.common.lib.general.IFileOpener;


public class DisplayDocsSlowTest
{
    class FakeFileOpener implements IFileOpener
    {
        public ArrayList<String> filesOpened = new ArrayList<String>();

        public void openFile( String filename )
        {
            filesOpened.add( filename );
        }

    }

    public void run() throws MyAssertFailureException, ClassNotFoundException, MalformedURLException
    {
        FakeFileOpener opener = new FakeFileOpener();
        DisplayDocs.displayDocs( "tmp", "../build/package/lib", opener );

        FreeGuideSlowTest.my_assert( new File( "tmp/docs/UserGuide/UserGuide.html" ).isFile() );
        FreeGuideSlowTest.my_assert( new File( "tmp/docs/index.html" ).isFile() );
        FreeGuideSlowTest.my_assert( new File( "tmp/docs/pub/skins/plain-freeguide/plain1.css" ).isFile() );

        FreeGuideSlowTest.my_assert( opener.filesOpened.size() == 1 );
        FreeGuideSlowTest.my_assert( opener.filesOpened.get( 0 ).equals(
            new File( "tmp/docs/UserGuide/UserGuide.html" ).getAbsolutePath() ) );

        deleteDirectory( new File( "tmp" ) );
    }

    private static void deleteDirectory( File dir )
    {
        for( File f : dir.listFiles() )
        {
            if( f.isDirectory() )
            {
                deleteDirectory( f );
            }
            else
            {
                f.delete();
            }
        }
        dir.delete();
    }
}

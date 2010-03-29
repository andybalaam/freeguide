package freeguide.test.slow;

import java.io.File;
import java.util.ArrayList;

import freeguide.common.lib.fgspecific.DisplayDocs;
import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.IBrowserLauncher;
import freeguide.test.FreeGuideTest;

public class DisplayDocsSlowTest
{
    class FakeBrowserLauncher implements IBrowserLauncher
    {
        public ArrayList<String> filesOpened = new ArrayList<String>();

        public void browseLocalFile( final File file ) throws Exception
        {
            filesOpened.add( file.toString() );
        }
    }

    public void run() throws Exception
    {
        test_CurrentDir();
        test_SuppliedDir();
        test_DocBinDir();
    }

    /**
     * When run in the source code directory, we correctly find the ../doc-bin
     * documentation.
     */
    public void test_CurrentDir() throws Exception
    {
        FakeBrowserLauncher launcher = new FakeBrowserLauncher();
        DisplayDocs.displayDocs( null, launcher );

        FreeGuideTest.my_assert( launcher.filesOpened.size() == 1 );
        FreeGuideTest
            .my_assert( launcher.filesOpened
                .get( 0 )
                .equals(
                    "../doc-bin/UserGuide/UserGuide.html" ) );
    }

    /**
     * When we supply a docs directory, we throw if docs are not found, and work
     * if they are.
     */
    public void test_SuppliedDir() throws Exception
    {
        deleteDirectory( new File( "tmp" ) );

        new File( "tmp" ).mkdir();

        try
        {
            FakeBrowserLauncher launcher = new FakeBrowserLauncher();
            DisplayDocs.displayDocs( "mydocdir", launcher, "tmp" );

            // Should not get here since an exception should be thrown
            throw new Exception(
                "Should have thrown an UnableToFindFileToBrowseException." );
        }
        catch( FileHelper.UnableToFindFileToBrowseException e )
        {
            // We expected this exception
        }

        // Make a fake documentation setup
        new File( "tmp/mydocdir/UserGuide" ).mkdirs();
        new File( "tmp/mydocdir/UserGuide/UserGuide.html" ).createNewFile();

        // Check the file got created correctly.
        FreeGuideTest.my_assert( new File(
            "tmp/mydocdir/UserGuide/UserGuide.html" ).isFile() );

        FakeBrowserLauncher launcher = new FakeBrowserLauncher();
        DisplayDocs.displayDocs( "mydocdir", launcher, "tmp" );

        FreeGuideTest.my_assert( launcher.filesOpened.size() == 1 );
        FreeGuideTest
            .my_assert( launcher.filesOpened
                .get( 0 )
                .equals(
                    "tmp/mydocdir/UserGuide/UserGuide.html" ) );

        deleteDirectory( new File( "tmp" ) );
    }

    public void test_DocBinDir() throws Exception
    {
        deleteDirectory( new File( "tmp" ) );

        new File( "tmp/doc-bin" ).mkdirs();

        new File( "tmp/doc-bin/UserGuide" ).mkdirs();
        new File( "tmp/doc-bin/UserGuide/UserGuide.html" ).createNewFile();

        // Check the file got created correctly.
        FreeGuideTest.my_assert( new File(
            "tmp/doc-bin/UserGuide/UserGuide.html" ).isFile() );

        FakeBrowserLauncher launcher = new FakeBrowserLauncher();
        DisplayDocs.displayDocs( null, launcher, "tmp" );

        FreeGuideTest.my_assert( launcher.filesOpened.size() == 1 );
        FreeGuideTest.my_assert( launcher.filesOpened.get( 0 ).equals(
            "tmp/./doc-bin/UserGuide/UserGuide.html" ) );

        deleteDirectory( new File( "tmp" ) );
    }

    private static void deleteDirectory( File dir )
    {
        if( dir.isDirectory() )
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
}

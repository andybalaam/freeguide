package freeguide.test.fast;

import java.io.File;
import java.net.URI;

import freeguide.common.lib.general.PathSearcher;

import freeguide.test.FreeGuideTest;

public class PathSearcherFastTest
{
    class FakePathSearcher extends PathSearcher
    {
        private String[] dirsToSearch;
        private String[] exesThatExist;

        FakePathSearcher( String[] dirsToSearch, String[] exesThatExist )
        {
            this.dirsToSearch = dirsToSearch;
            this.exesThatExist = exesThatExist;
        }

        protected String[] getPathDirs()
        {
            return dirsToSearch;
        }

        protected boolean existsAndIsExecutable( final File fullPath )
        {
            String stringPath = fullPath.toString();

            for( int i = 0; i < exesThatExist.length; i++ )
            {
                if( exesThatExist[i].equals( stringPath ) )
                {
                    return true;
                }
            }
            return false;
        }
    }

    public void run()
    throws Exception
    {
        test_FoundFirst();
        //test_FoundLast();
        //test_NotFound();
    }

    private void test_FoundFirst()
    throws Exception
    {
        String[] dirsToSearch = { "/dir/dir1", "/dir2" };
        String[] exesThatExist = { "/dir2/exe1" };
        FakePathSearcher ps = new FakePathSearcher( dirsToSearch,
            exesThatExist );

        String[] exeNames = { "exe1", "exe2" };
        String ans = ps.findInPath( exeNames, "fallback" );

        FreeGuideTest.my_assert( ans.equals( "exe1" ) );
    }

}

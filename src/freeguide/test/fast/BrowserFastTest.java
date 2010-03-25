package freeguide.test.fast;

import java.net.URI;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.IPathSearcher;

import freeguide.test.FreeGuideTest;

public class BrowserFastTest
{
    class FakeFileHelper extends FileHelper
    {
        public String cmdThatWasRun;

        protected void runBrowserCommand( String cmd )
        {
            cmdThatWasRun = cmd;
        }
    }

    class FakePathSearcher implements IPathSearcher
    {
        public String exeToReturn = null;

        public String findInPath( String[] listOfExes, String fallbackExe )
        {
            if( exeToReturn != null )
            {
                return exeToReturn;
            }
            else
            {
                return fallbackExe;
            }
        }
    }

    class BrowserCmdApplication extends FakeApplication
    {
        private String browserCmd;

        BrowserCmdApplication( String browserCmd )
        {
            this.browserCmd = browserCmd;
        }

        public String getBrowserCommand()
        {
            return browserCmd;
        }
    }

    public void run()
    throws Exception
    {
        test_NothingInPath();
        test_AppSuppliesBrowserCommand();
        test_FoundInPath();
    }

    private void test_NothingInPath()
    throws Exception
    {
        FakeApplication app = new FakeApplication();
        FakeFileHelper fh = new FakeFileHelper();

        fh.useDesktopAPI = false;
        fh.pathSearcher = new FakePathSearcher();

        fh.browseURIImpl( new URI( "http://t" ), app );

        // We default to firefox if we have no information
        FreeGuideTest.my_assert( fh.cmdThatWasRun.equals(
            "firefox \"http://t\"" ) );
    }

    private void test_AppSuppliesBrowserCommand()
    throws Exception
    {
        BrowserCmdApplication app = new BrowserCmdApplication(
            "mybrowser %url%" );
        FakeFileHelper fh = new FakeFileHelper();

        fh.useDesktopAPI = false;

        fh.browseURIImpl( new URI( "http://t" ), app );

        // We default to firefox if we have no information
        FreeGuideTest.my_assert( fh.cmdThatWasRun.equals(
            "mybrowser http://t" ) );
    }

    private void test_FoundInPath()
    throws Exception
    {
        FakeApplication app = new FakeApplication();
        FakeFileHelper fh = new FakeFileHelper();

        fh.useDesktopAPI = false;
        FakePathSearcher ps = new FakePathSearcher();
        ps.exeToReturn = "fredbrowser";
        fh.pathSearcher = ps;

        fh.browseURIImpl( new URI( "http://t" ), app );

        // We default to firefox if we have no information
        FreeGuideTest.my_assert( fh.cmdThatWasRun.equals(
            "fredbrowser \"http://t\"" ) );
    }
}

package freeguide.test.fast;

import java.io.File;

import freeguide.plugins.grabber.xmltv.GrabberXMLTV;
import freeguide.plugins.grabber.xmltv.XMLTVConfig;
import freeguide.test.FreeGuideTest;


public class GrabberXMLTVFastTest
{
    class DirsApplication extends FakeApplication
    {
        private String workingDir;
        private String installDir;

        DirsApplication( String workingDir, String installDir )
        {
            this.workingDir = workingDir;
            this.installDir = installDir;
        }

        public String getWorkingDirectory()
        {
            return workingDir;
        }

        public String getInstallDirectory()
        {
            return installDir;
        }
    }

    public void run() throws Exception
    {
        test_substituteDirs();
    }

    private void test_substituteDirs() throws Exception
    {
        String workingDir = "fake\\working\\dir";
        String installDir = "fake/install/dir";
        String absWorkingDir = new File( workingDir ).getAbsolutePath();
        String absInstallDir = new File( installDir ).getAbsolutePath();

        DirsApplication app = new DirsApplication( workingDir, installDir );

        XMLTVConfig.ModuleInfo moduleInfo = new XMLTVConfig.ModuleInfo();
        moduleInfo.configFileName = "fake_config_filename";

        FreeGuideTest.my_assert(
        	GrabberXMLTV.substituteXmltvDirs(
    			app, "Doesn't contain anything", moduleInfo ).equals(
    				"Doesn't contain anything" ) );

        String subs = GrabberXMLTV.substituteXmltvDirs(
			app, "%xmltv_path%/xmltv.exe",
			moduleInfo );
        String expected = new File( absInstallDir, "xmltv/xmltv.exe" ).toString();
        FreeGuideTest.my_assert( subs.equals( expected ) );

        subs = GrabberXMLTV.substituteXmltvDirs(
			app, "exe \"%config_file%\" -p -i -e", moduleInfo );
        expected = "exe \""
        	+ new File( absWorkingDir,
        		"xmltv-configs/fake_config_filename"
        		).toString()
        	+ "\" -p -i -e";
        FreeGuideTest.my_assert( subs.equals( expected ) );

        subs = GrabberXMLTV.substituteXmltvDirs(
			app,
			"\"%xmltv_path%/xmltv.exe\" " +
				"tv_grab_uk_rt " +
				"--quiet " +
				"--config-file \"%config_file%\"",
			moduleInfo );
        expected = "\""
        	+ new File( absInstallDir, "xmltv/xmltv.exe"
        		).toString()
        	+ "\" " +
        	"tv_grab_uk_rt " +
        	"--quiet " +
        	"--config-file \""
        	+ new File( absWorkingDir,
        		"xmltv-configs/fake_config_filename"
    			).toString()
        	+ "\"";
        FreeGuideTest.my_assert( subs.equals( expected ) );
    }
}

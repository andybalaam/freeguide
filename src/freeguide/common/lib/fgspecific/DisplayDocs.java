package freeguide.common.lib.fgspecific;

import java.io.File;
import java.io.IOException;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.IBrowserLauncher;
import freeguide.common.plugininterfaces.IApplication;

public class DisplayDocs
{
    public static class UnableToDisplayDocsException extends Exception
    {
        private static final long serialVersionUID = 1L;

        public String docsDirectory;

        public UnableToDisplayDocsException( String docsDirectory )
        {
            this.docsDirectory = docsDirectory;
        }
    }

    private static final String START_FILE = "UserGuide/UserGuide.html";

    private static final String CURRENT_DIR_DOCS = "./doc-bin";

    private static final String UP_ONE_DIR_DOCS = "../doc-bin";

    /**
     * Unpack and show docs.
     * 
     * @throws IOException
     */
    public static void displayDocs() throws Exception
    {
        IApplication app = Application.getInstance();
        displayDocs( app.getDocsDirectory(), new FileHelper() );
    }

    public static void displayDocs( String docsDirectory,
        IBrowserLauncher fileOpener ) throws Exception
    {
        displayDocs( docsDirectory, fileOpener, null );
    }

    private static File newThreePartFile( String part1, String part2,
        String part3 )
    {
        File ans;
        if( part1 != null )
        {
            if( part2 != null )
            {
                ans = new File( part1, part2 );
                ans = new File( ans, part3 );
            }
            else
            {
                ans = new File( part1, part3 );
            }
        }
        else
        {
            ans = new File( part2, part3 );
        }
        return ans;
    }

    public static void displayDocs( String docsDirectory,
        IBrowserLauncher fileOpener, String presentWorkingDir )
        throws Exception
    {
        String[] dirsToTry = { docsDirectory, CURRENT_DIR_DOCS, UP_ONE_DIR_DOCS };

        File userGuideFile = new File( "." );
        boolean fileExists = false;
        for( int dirNo = 0; dirNo < dirsToTry.length; dirNo++ )
        {
            // If the dir we are trying is null, this file
            // will be relative to the current directory.
            userGuideFile = newThreePartFile( presentWorkingDir,
                dirsToTry[dirNo], START_FILE );
            if( userGuideFile.exists() )
            {
                fileExists = true;
                break;
            }
        }

        if( !fileExists )
        {
            throw new UnableToDisplayDocsException( docsDirectory );
        }

        fileOpener.browseLocalFile( userGuideFile );
    }

}

package freeguide.common.lib.fgspecific;

import java.io.File;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.IBrowserLauncher;
import freeguide.common.plugininterfaces.IApplication;

public class DisplayDocs
{
    private static final String START_FILE = "UserGuide/UserGuide.html";

    private static final String CURRENT_DIR_DOCS = "./doc-bin";

    private static final String UP_ONE_DIR_DOCS = "../doc-bin";

    /**
     * Show the FreeGuide user guide in a browser.
     *
     * @throws Exception
     */
    public static void displayDocs() throws Exception
    {
        IApplication app = Application.getInstance();
        displayDocs( app.getDocsDirectory(), new FileHelper(), null );
    }

    /**
     * Show the FreeGuide user guide in a browser.
     *
     * @param docsDirectory the directory where the documentation is installed,
     *            or null if it is not (e.g. we are running in a development
     *            environment.
     * @param browserLauncher something that provides a browseLocalFile function
     *            we can use to launch the browser. In test code, a fake will be
     *            passed here. Normally, it will be a FileHelper.
     * @throws Exception
     */
    public static void displayDocs( String docsDirectory,
        IBrowserLauncher browserLauncher ) throws Exception
    {
        displayDocs( docsDirectory, browserLauncher, null );
    }

    /**
     * Show the FreeGuide user guide in a browser.
     *
     * @param docsDirectory the directory where the documentation is installed,
     *            or null if it is not (e.g. we are running in a development
     *            environment.
     * @param browserLauncher something that provides a browseLocalFile function
     *            we can use to launch the browser. In test code, a fake will be
     *            passed here. Normally, it will be a FileHelper.
     * @param presentWorkingDir assume this is the working directory - used in
     *            tests to simulate running from inside a different directory.
     * @throws Exception
     */
    public static void displayDocs( String docsDirectory,
        IBrowserLauncher browserLauncher, String presentWorkingDir )
        throws Exception
    {
        String[] dirsToTry = { docsDirectory, CURRENT_DIR_DOCS, UP_ONE_DIR_DOCS };

        File fullPathFile = new File( "." );
        boolean fileExists = false;
        for( int dirNo = 0; dirNo < dirsToTry.length; dirNo++ )
        {
            // If the dir we are trying is null, this file
            // will be relative to the current directory.
            fullPathFile = newThreePartFile( presentWorkingDir,
                dirsToTry[dirNo], START_FILE );
            if( fullPathFile.exists() )
            {
                fileExists = true;
                break;
            }
        }

        if( !fileExists )
        {
            String firstDir = null;
            if( dirsToTry.length > 0 )
            {
                firstDir = dirsToTry[0];
            }
            throw new FileHelper.UnableToFindFileToBrowseException(
                newThreePartFile( presentWorkingDir, firstDir, START_FILE )
                    .toString() );
        }

        browserLauncher.browseLocalFile( fullPathFile );
    }

    /**
     * Construct a File object made from the three parts supplied.
     *
     * @param part1 the first part of the directory path, or null
     * @param part2 the second part of the directory path, or null
     * @param part3 the last part, including the file name if this is a file.
     * @return a new File object made from whichever of the supplied parts was
     *         non-null.
     */
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
}

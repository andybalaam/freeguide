package freeguide.common.lib.fgspecific;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.IFileOpener;
import freeguide.common.plugininterfaces.IApplication;

public class DisplayDocs
{
    private static final String DOCS_SUBDIR = "docs";
    private static final String DOCS_ZIPFILE = "docs.zip";
    private static final String START_FILE = "UserGuide/UserGuide.html";

    /**
     * Unpack and show docs.
     * @throws IOException
     */
    public static void displayDocs()
    {
        IApplication app = Application.getInstance();
        displayDocs( app.getWorkingDirectory(),
            app.getLibDirectory(), app.getDocsDirectory(), new FileHelper() );
    }

    public static void displayDocs( String workingDirectory,
        String libDirectory, String docsDirectory, IFileOpener fileOpener )
    {
        // If the user didn't provide a documents dir on
        // the command line, we unzip the docs into a
        // temporary location.
        if( docsDirectory == null )
        {
            final File outDir = new File( workingDirectory,
                DOCS_SUBDIR );
            final File inZipFile = new File( libDirectory, DOCS_ZIPFILE );

            try
            {
                FileHelper.unzip( inZipFile, outDir );
                docsDirectory = outDir.toString();
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }
        }

        fileOpener.openFile(
            new File( docsDirectory, DisplayDocs.START_FILE
                ).getAbsolutePath() );
    }

}

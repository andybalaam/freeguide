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
            app.getLibDirectory(), new FileHelper() );
    }

    public static File displayDocs( String workingDirectory,
        String libDirectory, IFileOpener fileOpener )
    {
        final File outDir = new File( workingDirectory,
            DOCS_SUBDIR );
        final File inZipFile = new File( libDirectory, DOCS_ZIPFILE );

        try
        {
            FileHelper.unzip( inZipFile, outDir );
            fileOpener.openFile(
                new File( outDir, DisplayDocs.START_FILE ).getAbsolutePath() );
        }
        catch( FileNotFoundException e )
        {
            e.printStackTrace();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }

        return outDir;
    }

}

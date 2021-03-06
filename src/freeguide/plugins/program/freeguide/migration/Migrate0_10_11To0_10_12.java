package freeguide.plugins.program.freeguide.migration;

import freeguide.common.lib.general.FileHelper;

import freeguide.plugins.program.freeguide.FreeGuide;

import java.io.File;

import java.util.prefs.BackingStoreException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Migrate0_10_11To0_10_12 extends MigrationProcessBase
{
    /**
     * Creates a new Migrate0_10_11To0_10_12 object.
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public Migrate0_10_11To0_10_12( final String nodeName )
        throws BackingStoreException
    {
        super( nodeName );
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void migrate(  ) throws Exception
    {
        FreeGuide.log.info( "Upgrading preferences 0.10.11 -> 0.10.12" );

        boolean isWindows =
            System.getProperty( "os.name" ).startsWith( "Windows" );

        // Delete the installed XMLTV version so we can unzip a newer one
        if( isWindows )
        {
            final File xmltvDir =
                new File( FreeGuide.config.workingDirectory, "xmltv" );

            FileHelper.deleteDir( xmltvDir );
        }

        moveNode( "" );

        getAndRemoveKey( "version" );
        putKey( "version", "0.10.12" );
    }
}


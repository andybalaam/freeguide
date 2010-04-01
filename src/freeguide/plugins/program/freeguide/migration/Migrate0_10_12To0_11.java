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
public class Migrate0_10_12To0_11 extends MigrationProcessBase
{
    /**
     * Creates a new Migrate0_10_12To0_11 object.
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public Migrate0_10_12To0_11( final String nodeName )
        throws BackingStoreException
    {
        super( nodeName );
    }

    /**
     * TEST ONLY: creates a new Migrate0_10_12To0_11 object
     * without initialising it.
     */
    public Migrate0_10_12To0_11()
    {
        super();
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void migrate(  ) throws Exception
    {
        FreeGuide.log.info( "Upgrading preferences 0.10.12 -> 0.11" );

        boolean isWindows =
            System.getProperty( "os.name" ).startsWith( "Windows" );

        // Delete the installed XMLTV version so we can unzip a newer one
        if( isWindows )
        {
            final File xmltvDir =
                new File( FreeGuide.config.workingDirectory, "xmltv" );

            FileHelper.deleteDir( xmltvDir );
        }

        // Convert from panel width to programme width
        String sizeProgrammePanelWidth = getAndRemoveKey(
            "modules/ui-horizontal/sizeProgrammePanelWidth" );

        if( sizeProgrammePanelWidth != null )
        {
            int panelWidth = Integer.parseInt( sizeProgrammePanelWidth );
            int programmeWidth = panelWidth / 24;

            putKey( "modules/ui-horizontal/sizeProgrammeHour",
                Integer.toString( programmeWidth ) );
        }

        moveNode( "" );

        getAndRemoveKey( "version" );
        putKey( "version", "0.11" );
    }
}


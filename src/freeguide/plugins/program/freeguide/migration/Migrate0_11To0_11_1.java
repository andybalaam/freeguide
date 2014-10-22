package freeguide.plugins.program.freeguide.migration;

import freeguide.common.lib.general.FileHelper;

import freeguide.plugins.program.freeguide.FreeGuide;

import java.io.File;

import java.util.prefs.BackingStoreException;

public class Migrate0_11To0_11_1 extends MigrationProcessBase
{
    public Migrate0_11To0_11_1( final String nodeName )
        throws BackingStoreException
    {
        super( nodeName );
    }

    public Migrate0_11To0_11_1()
    {
        super();
    }

    public void migrate(  ) throws Exception
    {
        FreeGuide.log.info( "Upgrading preferences 0.11 -> 0.11.1" );

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
        putKey( "version", "0.11.1" );
    }
}


package freeguide.plugins.program.freeguide.migration;

import freeguide.common.base.Version;
import freeguide.common.lib.fgspecific.Application;

import freeguide.plugins.program.freeguide.FreeGuide;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Main class for migration. It checks version and run migrations.
 *
 * @author Alex Bulouichik (alex73 at zaval.org)
 */
public class Migrate
{
    protected static boolean firstTime = true;
    protected static boolean needToRunWizard = false;
    protected static boolean needToRunAfter = false;
    protected static boolean dumpPrefs = false;

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void migrateBeforeWizard(  ) throws Exception
    {
        firstTime = !Preferences.userRoot(  )
                                .nodeExists( FreeGuide.pref_root_name );
        FreeGuide.log.finer( "Migration: firstTime=" + firstTime );

        if( firstTime )
        {
            needToRunWizard = true;

            return;
        }

        Version installedVersion = getInstalledVersion(  );

        if(
            installedVersion.equals( new Version( null ) )
                || ( installedVersion.greaterThan( Application.VERSION ) ) )
        {
            // If we have a very old version installed, or the
            // installed version is later than the code, we
            // must re-run the wizard.
            needToRunWizard = true;
        }
        else
        {
            // Otherwise, we can just run the automatic migration
            // code to update the user's preferences to the
            // pattern expected by this version of the code.

            MigrationProcessBase migr = null;

            if( installedVersion.lessThan( new Version( 0, 10, 1 ) ) )
            {
                needToRunWizard = true;
                needToRunAfter = true;

                migr = new MigrateOldTo0_10_1( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 2 ) ) )
            {
                migr = new Migrate0_10_1To0_10_2( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 3 ) ) )
            {
                migr = new Migrate0_10_2To0_10_3( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 4 ) ) )
            {
                migr = new Migrate0_10_3To0_10_4( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 5 ) ) )
            {
                migr = new Migrate0_10_4To0_10_5( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 6 ) ) )
            {
                migr = new Migrate0_10_5To0_10_6( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 7 ) ) )
            {
                migr = new Migrate0_10_6To0_10_7( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 8 ) ) )
            {
                migr = new Migrate0_10_7To0_10_8( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 9 ) ) )
            {
                migr = new Migrate0_10_8To0_10_9( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 10 ) ) )
            {
                migr = new Migrate0_10_9To0_10_10( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( installedVersion.lessThan( new Version( 0, 10, 11 ) ) )
            {
                migr = new Migrate0_10_10To0_10_11( FreeGuide.pref_root_name );
                migr.migrate(  );
            }

            if( migr != null )
            {
                migr.saveTo( FreeGuide.pref_root_name );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isNeedToRunWizard(  )
    {
        return needToRunWizard;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isFirstTime(  )
    {
        return firstTime;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isDumpPrefs(  )
    {
        return dumpPrefs;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param iDumpPrefs DOCUMENT_ME!
     */
    public static void setDumpPrefs( boolean iDumpPrefs )
    {
        dumpPrefs = iDumpPrefs;
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void migrateAfterWizard(  ) throws Exception
    {
        if( needToRunAfter )
        {
            MigrateOldTo0_10_1.migrateAfterWizard(  );
        }
    }

    protected static Version getInstalledVersion(  )
    {
        String storedVersionName =
            Preferences.userRoot(  ).node( FreeGuide.pref_root_name + "/misc" )
                       .get( "install_version", null );

        if( storedVersionName == null )
        {
            storedVersionName = Preferences.userRoot(  )
                                           .node( FreeGuide.pref_root_name )
                                           .get( "version", null );
        }

        FreeGuide.log.finer(
            "Migration: storedVersionName=" + storedVersionName );

        return new Version( storedVersionName );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param node_name DOCUMENT_ME!
     */
    public static void dumpPrefs( String node_name )
    {
        try
        {
            Map prefs = new TreeMap(  );
            loadMap( "", Preferences.userRoot(  ).node( node_name ), prefs );
            dumpPrefs( prefs, "prefs_cur" );
        }
        catch( BackingStoreException e )
        {
            e.printStackTrace(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefs DOCUMENT_ME!
     * @param prefix DOCUMENT_ME!
     */
    public static void dumpPrefs( final Map prefs, String prefix )
    {
        if( Migrate.isDumpPrefs(  ) )
        {
            String versionName =
                (String)( prefs.get( "misc.install_version" ) );

            if( versionName == null )
            {
                versionName = (String)( prefs.get( "version" ) );
            }

            try
            {
                File fl = new File( prefix + "-" + versionName + ".txt" );

                FileWriter writer = new FileWriter( fl );

                for(
                    Iterator it = prefs.entrySet(  ).iterator(  );
                        it.hasNext(  ); )
                {
                    Map.Entry entry = (Map.Entry)it.next(  );
                    writer.write(
                        entry.getKey(  ) + "=" + entry.getValue(  ) + "\n" );
                }

                writer.close(  );
            }
            catch( IOException e )
            {
                e.printStackTrace(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param path DOCUMENT_ME!
     * @param node DOCUMENT_ME!
     * @param prefs DOCUMENT_ME!
     *
     * @throws BackingStoreException DOCUMENT_ME!
     */
    public static void loadMap(
        final String path, final Preferences node, Map prefs )
        throws BackingStoreException
    {
        final String[] keys = node.keys(  );

        for( int i = 0; i < keys.length; i++ )
        {
            prefs.put( path + keys[i], node.get( keys[i], null ) );
        }

        final String[] childrenNames = node.childrenNames(  );

        for( int i = 0; i < childrenNames.length; i++ )
        {
            loadMap(
                path + childrenNames[i] + "/", node.node( childrenNames[i] ),
                prefs );
        }
    }
}

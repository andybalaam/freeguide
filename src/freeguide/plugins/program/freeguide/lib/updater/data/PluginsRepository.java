package freeguide.plugins.program.freeguide.lib.updater.data;

import freeguide.plugins.program.freeguide.FreeGuide;

import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Plugins Repository Info and update implementation.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class PluginsRepository
{

    /** Program type. */
    public static final String PACKAGE_TYPE_APPLICATION = "application";

    /** Plugins grabber type. */
    public static final String PACKAGE_TYPE_GRABBER = "plugin-grabber";

    /** Plugins import/export type. */
    public static final String PACKAGE_TYPE_IMPEXP = "plugin-impexp";

    /** Plugins storage type. */
    public static final String PACKAGE_TYPE_STORAGE = "plugin-storage";

    /** Plugins UI(viewer) type. */
    public static final String PACKAGE_TYPE_UI = "plugin-ui";

    /** Plugins reminder type. */
    public static final String PACKAGE_TYPE_REMINDER = "plugin-reminder";

    /** Other type. */
    public static final String PACKAGE_TYPE_OTHER = "other";

    /** List of packages. */
    protected List packages = new ArrayList(  );

    /** Map of mirrors, where key=location, value=path. */
    protected Map mirrors = new TreeMap(  );
    protected File baseDirectory;

    /**
     * Creates a new PluginsRepository object.
     *
     * @param baseDirectory DOCUMENT ME!
     */
    public PluginsRepository( final String baseDirectory )
    {
        this.baseDirectory = new File( baseDirectory );
    }

    /**
     * Add package to repository.
     *
     * @param pkg package
     */
    public void addPackage( final PluginPackage pkg )
    {
        packages.add( pkg );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param location DOCUMENT_ME!
     * @param path DOCUMENT ME!
     */
    public void addMirror( final String location, final String path )
    {
        mirrors.put( location, path );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public List getAllPackages(  )
    {

        return packages;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getMirrorLocations(  )
    {

        return (String[])mirrors.keySet(  ).toArray( 
            new String[mirrors.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param location DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getPathByMirrorsLocation( final String location )
    {

        return (String)mirrors.get( location );
    }

    /**
     * Get packages for specified type.
     *
     * @param typeName type
     *
     * @return list of packages
     */
    public List getPackagesByType( final String typeName )
    {

        List result = new ArrayList(  );

        for( int i = 0; i < packages.size(  ); i++ )
        {

            PluginPackage pkg = (PluginPackage)packages.get( i );

            if( typeName.equals( pkg.getType(  ) ) )
            {
                result.add( pkg );
            }
        }

        return result;
    }

    /**
     * Check for need to update any packages, like upgrade, install or remove.
     *
     * @return true if packages need to update
     */
    public boolean needToUpdate(  )
    {

        for( int i = 0; i < packages.size(  ); i++ )
        {

            PluginPackage pkg = (PluginPackage)packages.get( i );

            try
            {

                if( pkg.needToUpdate(  ) )
                {

                    return true;
                }
            }
            catch( Exception ex )
            {
                FreeGuide.log.warning( 
                    "Error check package for update : " + pkg.getID(  ) );
            }
        }

        return false;
    }

    /**
     * Get list of files for download from repository.
     *
     * @return list of files (PluginPackage.PackageFile)
     */
    public String[] getFilesForDownload(  )
    {

        final List result = new ArrayList(  );

        for( int i = 0; i < packages.size(  ); i++ )
        {

            PluginPackage pkg = (PluginPackage)packages.get( i );

            if( 
                ( pkg.isChanged(  ) && !pkg.isMarkedForRemove(  ) )
                    || pkg.isMarkedForInstall(  ) )
            {
                result.add( pkg.getRepositoryPath(  ) );
            }
        }

        return (String[])result.toArray( new String[result.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getFilesForDelete(  )
    {

        List result = new ArrayList(  );

        for( int i = 0; i < packages.size(  ); i++ )
        {

            PluginPackage pkg = (PluginPackage)packages.get( i );

            if( pkg.isChanged(  ) || pkg.isMarkedForRemove(  ) )
            {

                PluginInfo info =
                    PluginsManager.getPluginInfoByID( pkg.getID(  ) );

                if( info != null )
                {
                    result.addAll( info.getFiles(  ) );
                }
            }
        }

        return (String[])result.toArray( new String[result.size(  )] );
    }
}

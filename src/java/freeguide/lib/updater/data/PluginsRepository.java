package freeguide.lib.updater.data;

import freeguide.FreeGuide;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Plugins Repository Info and update implementation.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class PluginsRepository
{

    /** Program type. */
    public static final String PACKAGE_TYPE_PROGRAM = "program";

    /** Plugins grabber type. */
    public static final String PACKAGE_TYPE_GRABBER = "plugin-grabber";

    /** Plugins import/export type. */
    public static final String PACKAGE_TYPE_IMPEXP = "plugin-impexp";

    /** Other type. */
    public static final String PACKAGE_TYPE_OTHER = "other";

    /** List of packages. */
    protected List packages = new ArrayList(  );
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
     * @return DOCUMENT_ME!
     */
    public List getAllPackages(  )
    {

        return packages;
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
    public List getFilesForDownload(  )
    {

        final List result = new ArrayList(  );

        for( int i = 0; i < packages.size(  ); i++ )
        {

            PluginPackage pkg = (PluginPackage)packages.get( i );

            try
            {
                pkg.listFilesForDownload( result );
            }
            catch( Exception ex )
            {
                FreeGuide.log.warning( 
                    "Error check package for update : " + pkg.getID(  ) );
            }
        }

        return result;
    }

    /**
     * Copy files from src to dst dir, which we need to update.
     *
     * @param srcDirectory source directory (where we downloaded files)
     * @param files list fo files for update(PluginPackage.PackageFile)
     *
     * @throws IOException exception
     */
    public void update( final String srcDirectory, final List files )
        throws IOException
    {

        for( int i = 0; i < files.size(  ); i++ )
        {

            PluginPackage.PackageFile file =
                (PluginPackage.PackageFile)files.get( i );
            final File destFile = new File( baseDirectory, file.getPath(  ) );

            if( !destFile.canWrite(  ) )
            {
                throw new IOException( 
                    "Error update: file " + destFile.getPath(  )
                    + " is not writable" );
            }
        }

        for( int i = 0; i < files.size(  ); i++ )
        {

            PluginPackage.PackageFile file =
                (PluginPackage.PackageFile)files.get( i );
            final File srcFile = new File( srcDirectory, file.getPath(  ) );
            final File destFile = new File( baseDirectory, file.getPath(  ) );
            srcFile.renameTo( destFile );
        }
    }
}
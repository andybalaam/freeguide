package freeguide.lib.updater.data;

import freeguide.lib.fgspecific.PluginsManager;

import java.io.File;
import java.io.FileInputStream;

import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Repository package info.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class PluginPackage
{

    protected final static char[] HEX_DIGITS =
        new char[]
        {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F'
        };

    /** Not marked. */
    public static final int STATE_NOT_MARKED = 0;

    /** Marked for remove. */
    public static final int STATE_MARKED_FOR_REMOVE = 1;

    /** Marked for install. */
    public static final int STATE_MARKED_FOR_INSTALL = 2;

    /** Package ID. It equals to ID of module. */
    protected String id;

    /** Package version. Not used yet. */
    protected String version;

    /** Package type. It equals to module type or 'program'. */
    protected String type;

    /** Names for all languages. */
    protected Map names = new TreeMap(  );

    /** Descriptions for all languages. */
    protected Map descriptions = new TreeMap(  );

    /** Files in package. */
    protected List files = new ArrayList(  );
    protected int mark = STATE_NOT_MARKED;
    protected Boolean changed;
    protected PluginsRepository parent;

    /**
     * Creates a new PluginPackage object.
     *
     * @param id DOCUMENT ME!
     * @param repository DOCUMENT ME!
     */
    public PluginPackage( final String id, final PluginsRepository repository )
    {
        this.id = id;
        parent = repository;
    }

    /**
     * Get package ID. It equals to IModule.getID.
     *
     * @return ID
     */
    public String getID(  )
    {

        return id;
    }

    /**
     * Set package type.
     *
     * @param type package type
     */
    public void setType( String type )
    {
        this.type = type;
    }

    /**
     * Get package type.
     *
     * @return package type
     */
    public String getType(  )
    {

        return type;
    }

    /**
     * Set package version.
     *
     * @param version package version
     */
    public void setVersion( String version )
    {
        this.version = version;
    }

    /**
     * Get package version.
     *
     * @return package version
     */
    public String getVersion(  )
    {

        return version;
    }

    /**
     * Set name of package for language.
     *
     * @param lang language
     * @param text name
     */
    public void setName( final String lang, final String text )
    {
        names.put( lang, text );
    }

    /**
     * Set description of package for language.
     *
     * @param lang language
     * @param text description
     */
    public void setDescription( final String lang, final String text )
    {
        descriptions.put( lang, text );
    }

    /**
     * Get package name for language.
     *
     * @param lang language
     *
     * @return package name
     */
    public String getName( final String lang )
    {

        return (String)names.get( lang );
    }

    /**
     * Get package description for language.
     *
     * @param lang language
     *
     * @return package description
     */
    public String getDescription( final String lang )
    {

        return (String)descriptions.get( lang );
    }

    /**
     * Add file to package.
     *
     * @param localPath relative path
     * @param repositoryPath DOCUMENT ME!
     * @param size size
     * @param md5sum MD5 checksum
     */
    public void addFile( 
        final String localPath, final String repositoryPath, final String size,
        final String md5sum )
    {
        files.add( new PackageFile( localPath, repositoryPath, size, md5sum ) );
    }

    /**
     * Mark package for install.
     */
    public void markForInstall(  )
    {
        mark = STATE_MARKED_FOR_INSTALL;
    }

    /**
     * Mark package for remove.
     */
    public void markForRemove(  )
    {
        mark = STATE_MARKED_FOR_REMOVE;
    }

    /**
     * Unmark package.
     */
    public void markOff(  )
    {
        mark = STATE_NOT_MARKED;
    }

    /**
     * Check is package marked for install.
     *
     * @return true if marked for install
     */
    public boolean isMarkedForInstall(  )
    {

        return mark == STATE_MARKED_FOR_INSTALL;
    }

    /**
     * Check is package marked for remove.
     *
     * @return true if marked for remove
     */
    public boolean isMarkedForRemove(  )
    {

        return mark == STATE_MARKED_FOR_REMOVE;
    }

    /**
     * Check is package installed.
     *
     * @return true if package installed
     */
    public boolean isInstalled(  )
    {

        return PluginsManager.isInstalled( id );
    }

    /**
     * List files which modified on filesystem.
     *
     * @param ls list of PackageFile's
     *
     * @throws Exception DOCUMENT ME!
     */
    protected void listFilesForDownload( final List ls )
        throws Exception
    {

        switch( mark )
        {

        case STATE_NOT_MARKED:

            if( isInstalled(  ) )
            {

                for( int i = 0; i < files.size(  ); i++ )
                {

                    final PackageFile file = (PackageFile)files.get( i );

                    if( file.isChanged(  ) )
                    {
                        ls.add( file );
                    }
                }
            }

            break;

        case STATE_MARKED_FOR_INSTALL:
            ls.addAll( files );

            break;
        }
    }

    /**
     * Check for package need to upgrade, install or remove.
     *
     * @return true if need to update any file
     *
     * @throws Exception DOCUMENT ME!
     */
    public boolean needToUpdate(  ) throws Exception
    {

        if( mark != STATE_NOT_MARKED )
        {

            return true;
        }

        if( isInstalled(  ) )
        {

            return isChanged(  );
        }
        else
        {

            return false;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public List getFiles(  )
    {

        return files;
    }

    protected synchronized boolean isChanged(  ) throws Exception
    {

        if( changed == null )
        {
            changed = Boolean.FALSE;

            for( int i = 0; i < files.size(  ); i++ )
            {

                final PackageFile file = (PackageFile)files.get( i );

                if( file.isChanged(  ) )
                {
                    changed = Boolean.TRUE;

                    break;
                }
            }
        }

        return changed.booleanValue(  );
    }

    /**
     * Class for store info about file for package.
     */
    public class PackageFile
    {

        /** Path of file from install root(relative). */
        protected String localPath;

        /** Path of file from mirror root(relative). */
        protected String repositoryPath;

        /** Size of file. */
        protected long size;

        /** MD5 sum of file for check. Not used yet. */
        protected String md5sum;

        /**
         * Creates a new PackageFile object.
         *
         * @param localPath relative path
         * @param repositoryPath DOCUMENT ME!
         * @param size size
         * @param md5sum MD5 checksum
         */
        public PackageFile( 
            final String localPath, final String repositoryPath,
            final String size, final String md5sum )
        {
            this.localPath = localPath;
            this.repositoryPath = repositoryPath;

            if( size != null )
            {
                this.size = Long.decode( size ).longValue(  );
            }
            else
            {
                this.size = -1;
            }

            this.md5sum = md5sum;
        }

        /**
         * Get relative path.
         *
         * @return relative path
         */
        public String getLocalPath(  )
        {

            return localPath;
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String getRepositoryPath(  )
        {

            return repositoryPath;
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public long getSize(  )
        {

            return size;
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String getMd5sum(  )
        {

            return md5sum;
        }

        /**
         * Check for file if changed in filesystem.
         *
         * @return true if file changed
         *
         * @throws Exception DOCUMENT ME!
         */
        public boolean isChanged(  ) throws Exception
        {

            final File file = new File( parent.baseDirectory, localPath );

            if( file.exists(  ) )
            {

                return ( file.length(  ) != size ) || isMD5changed( file );
            }
            else
            {

                return size != 0;
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @throws Exception DOCUMENT_ME!
         */
        public void loadData(  ) throws Exception
        {

            final File file = new File( parent.baseDirectory, localPath );
            size = file.length(  );
            md5sum = calculateFileDigest(  );
        }

        /**
         * Check for file is valid by MD5 checksum.
         *
         * @param file file for check
         *
         * @return true if file not valid
         *
         * @throws Exception DOCUMENT ME!
         */
        protected boolean isMD5changed( final File file )
            throws Exception
        {

            return !calculateFileDigest(  ).equalsIgnoreCase( md5sum );
        }

        protected String calculateFileDigest(  ) throws Exception
        {

            MessageDigest md = MessageDigest.getInstance( "MD5" );
            FileInputStream fin =
                new FileInputStream( 
                    new File( parent.baseDirectory, localPath ) );
            byte[] buffer = new byte[64 * 1024];

            while( true )
            {

                int len = fin.read( buffer );

                if( len == -1 )
                {

                    break;
                }

                md.update( buffer );
            }

            fin.close(  );

            byte[] digest = md.digest(  );
            final StringBuffer result = new StringBuffer(  );

            for( int i = 0; i < digest.length; i++ )
            {
                result.append( HEX_DIGITS[( digest[i] >> 4 ) & 0x0F] );
                result.append( HEX_DIGITS[digest[i] & 0x0F] );
            }

            return result.toString(  );
        }
    }
}

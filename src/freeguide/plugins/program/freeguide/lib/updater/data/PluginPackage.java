package freeguide.plugins.program.freeguide.lib.updater.data;

import freeguide.common.lib.general.Version;

import freeguide.common.plugininterfaces.IModule;

import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;

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
    protected Version version;

    /** Path from repository root. */
    protected String repositoryPath;

    /** Package type. It equals to module type or 'program'. */
    protected String type;

    /** Names for all languages. */
    protected Map names = new TreeMap(  );

    /** Descriptions for all languages. */
    protected Map descriptions = new TreeMap(  );

    /** Files in package. */
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
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Version getVersion(  )
    {
        return version;
    }

    /**
     * Set package version.
     *
     * @param version package version
     */
    public void setVersion( String version )
    {
        this.version = new Version( version );
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
     * @param repositoryPath DOCUMENT_ME!
     */
    public void setRepositoryPath( String repositoryPath )
    {
        this.repositoryPath = repositoryPath;
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
        if( !PluginsRepository.PACKAGE_TYPE_APPLICATION.equals( type ) )
        {
            mark = STATE_MARKED_FOR_REMOVE;
        }
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
     * Check for package need to upgrade, install or remove.
     *
     * @return true if need to update any file
     */
    public boolean needToUpdate(  )
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

    protected synchronized boolean isChanged(  )
    {
        PluginInfo info = PluginsManager.getPluginInfoByID( id );

        if( info == null )
        {
            return true;
        }
        else
        {
            return info.getVersion(  ).lessThan( version );
        }
    }
}

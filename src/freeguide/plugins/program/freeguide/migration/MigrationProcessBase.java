package freeguide.plugins.program.freeguide.migration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

/**
 * Base class for migration processes.
 *
 * @author Alex Bulouichik (alex73 at zaval.org)
 */
abstract public class MigrationProcessBase
{
    protected final Map<String, String> prefFrom;
    protected final Map<String, String> prefTo;

/**
     * Creates a new MigrationProcessBase object.
     *
     * @param source DOCUMENT ME!
     */
    public MigrationProcessBase( final Map source )
    {
        prefFrom = source;
        Migrate.dumpPrefs( prefFrom, "prefs_old" );
        prefTo = new TreeMap(  );
    }

/**
     * Creates a new MigrationProcessBase object.
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public MigrationProcessBase( final String nodeName )
        throws BackingStoreException
    {
        prefFrom = new TreeMap(  );
        loadFrom( "", Preferences.userRoot(  ).node( nodeName ) );
        Migrate.dumpPrefs( prefFrom, "prefs_old" );
        prefTo = new TreeMap(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    abstract public void migrate(  ) throws Exception;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Map getResult(  )
    {
        return prefTo;
    }

    protected void moveKey( final String keyFrom, final String keyTo )
    {
        final String value = (String)prefFrom.remove( keyFrom );

        if( value != null )
        {
            prefTo.put( keyTo, value );
        }
    }

    protected void moveKey( final String key )
    {
        moveKey( key, key );
    }

    protected void moveNode( final String name )
    {
        moveNode( name, name );
    }

    protected void moveNode( final String fromName, final String toName )
    {
        final String[] keys =
            (String[])prefFrom.keySet(  ).toArray( 
                new String[prefFrom.size(  )] );

        for( int i = 0; i < keys.length; i++ )
        {
            if( keys[i].startsWith( fromName ) )
            {
                final String newKey =
                    toName + keys[i].substring( fromName.length(  ) );
                final String value = (String)prefFrom.remove( keys[i] );
                prefTo.put( newKey, value );
            }
        }
    }

    protected String getAndRemoveKey( final String keyFrom )
    {
        return (String)prefFrom.remove( keyFrom );
    }

    protected String getKey( final String keyFrom )
    {
        return (String)prefFrom.get( keyFrom );
    }

    /**
     * Get list of keys which stars from prefix.
     *
     * @param prefix prefix for check
     *
     * @return list of keys
     */
    protected String[] listKeys( final String prefix )
    {
        final List result = new ArrayList(  );

        for( Iterator it = prefFrom.keySet(  ).iterator(  ); it.hasNext(  ); )
        {
            final String key = (String)it.next(  );

            if( key.startsWith( prefix ) )
            {
                result.add( key );
            }
        }

        return (String[])result.toArray( new String[result.size(  )] );
    }

    protected String[] listKeysTo( final Pattern regexp )
    {
        final List<String> result = new ArrayList<String>(  );

        for( Iterator it = prefTo.keySet(  ).iterator(  ); it.hasNext(  ); )
        {
            final String key = (String)it.next(  );

            if( regexp.matcher( key ).matches(  ) )
            {
                result.add( key );
            }
        }

        return (String[])result.toArray( new String[result.size(  )] );
    }

    protected void putKey( final String key, final String value )
    {
        prefTo.put( key, value );
    }

    protected void loadFrom( final String path, final Preferences node )
        throws BackingStoreException
    {
        Migrate.loadMap( path, node, prefFrom );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param rootNodeName DOCUMENT_ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public void saveTo( final String rootNodeName )
        throws BackingStoreException
    {
        Migrate.dumpPrefs( prefTo, "prefs_new" );

        Preferences.userRoot(  ).node( rootNodeName ).removeNode(  );

        Preferences root = Preferences.userRoot(  ).node( rootNodeName );

        for( Iterator it = prefTo.entrySet(  ).iterator(  ); it.hasNext(  ); )
        {
            Map.Entry entry = (Map.Entry)it.next(  );
            final String key = (String)entry.getKey(  );
            final String value = (String)entry.getValue(  );
            int pos = key.lastIndexOf( '/' );
            Preferences node;

            if( pos != -1 )
            {
                node = root.node( key.substring( 0, pos ) );
            }
            else
            {
                node = root;
            }

            //System.out.println( "key=" + key + " value=" + value );
            node.put( key.substring( pos + 1 ), value );
        }
    }
}

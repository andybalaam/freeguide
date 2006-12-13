package freeguide.common.lib.general;

import freeguide.common.lib.fgspecific.Application;

import java.awt.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.prefs.Preferences;

/**
 * Helper for store non-transient non-final non-static public fields of
 * object in Preferences, using reflection. It supports types: List, String,
 * int, long. When List variable defined, then class should have static
 * variable fieldname_TYPE, which store class for create list.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class PreferencesHelper
{
    protected static final String LOCALE_SUFFIX_LANGUAGE = ".language";
    protected static final String LOCALE_SUFFIX_COUNTRY = ".country";
    protected static final String LOCALE_SUFFIX_VARIANT = ".variant";
    protected static final String MAP_SUFFIX_KEY_TYPE = "_KEY_TYPE";
    protected static final String MAP_SUFFIX_VALUE_TYPE = "_VALUE_TYPE";
    protected static final String COLLECTION_SUFFIX_TYPE = "_TYPE";
    protected static final String SUFFIX_SIZE = "size";
    protected static final String MAP_SUFFIX_KEY = ".key";
    protected static final String MAP_SUFFIX_VALUE = ".value";
    protected static final String DOT = ".";

    /**
     * DOCUMENT_ME!
     *
     * @param prefNode DOCUMENT_ME!
     * @param obj DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void load( final Preferences prefNode, final Object obj )
        throws Exception
    {
        loadObject( prefNode, StringHelper.EMPTY_STRING, obj );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefNode DOCUMENT_ME!
     * @param namePrefix DOCUMENT ME!
     * @param obj DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    protected static void loadObject( 
        final Preferences prefNode, final String namePrefix, final Object obj )
        throws Exception
    {
        final Set existKeys =
            new TreeSet( Arrays.asList( prefNode.keys(  ) ) );

        final Field[] fields = obj.getClass(  ).getFields(  );

        for( int i = 0; i < fields.length; i++ )
        {
            final Field field = fields[i];

            if( !isFieldSerialized( field ) )
            {
                continue;
            }

            final String keyName = namePrefix + field.getName(  );

            if( field.getType(  ).isPrimitive(  ) )
            { // primitive type

                if( existKeys.contains( keyName ) )
                {
                    if( boolean.class == field.getType(  ) )
                    {
                        field.setBoolean( 
                            obj, prefNode.getBoolean( keyName, false ) );
                    }
                    else if( int.class == field.getType(  ) )
                    {
                        field.setInt( obj, prefNode.getInt( keyName, 0 ) );
                    }
                    else if( long.class == field.getType(  ) )
                    {
                        field.setLong( obj, prefNode.getLong( keyName, 0 ) );
                    }
                }
            }
            else
            { // non-primitive type

                if( String.class == field.getType(  ) )
                {
                    if( existKeys.contains( keyName ) )
                    {
                        Object value = prefNode.get( keyName, null );

                        field.set( obj, value );

                    }
                }

                else if( Color.class.isAssignableFrom( field.getType(  ) ) )
                {
                    if( existKeys.contains( keyName ) )
                    {
                        int value = prefNode.getInt( keyName, 0 );

                        field.set( obj, new Color( value ) );

                    }
                }
                else if( Locale.class.isAssignableFrom( field.getType(  ) ) )
                {
                    if( existKeys.contains( keyName + LOCALE_SUFFIX_LANGUAGE ) )
                    {
                        String language =
                            prefNode.get( 
                                keyName + LOCALE_SUFFIX_LANGUAGE,
                                StringHelper.EMPTY_STRING );
                        String country =
                            prefNode.get( 
                                keyName + LOCALE_SUFFIX_COUNTRY,
                                StringHelper.EMPTY_STRING );
                        String variant =
                            prefNode.get( 
                                keyName + LOCALE_SUFFIX_VARIANT,
                                StringHelper.EMPTY_STRING );

                        field.set( 
                            obj, new Locale( language, country, variant ) );
                    }
                }
                else if( Map.class.isAssignableFrom( field.getType(  ) ) )
                {
                    Class keyType =
                        checkTypeDefined( 
                            obj, field.getName(  ) + MAP_SUFFIX_KEY_TYPE );

                    Class valueType =
                        checkTypeDefined( 
                            obj, field.getName(  ) + MAP_SUFFIX_VALUE_TYPE );

                    Map map = (Map)field.get( obj );

                    loadMap( prefNode, keyName + '.', map, keyType, valueType );

                    //field.set( obj, list );
                }

                else if( 
                    Collection.class.isAssignableFrom( field.getType(  ) ) )
                {
                    Field typeField =
                        obj.getClass(  )
                           .getField( 
                            field.getName(  ) + COLLECTION_SUFFIX_TYPE );

                    int modsT = typeField.getModifiers(  );

                    if( 
                        !Modifier.isPublic( modsT )
                            || !Modifier.isStatic( modsT )
                            || ( Class.class != typeField.getType(  ) ) )
                    {
                        throw new Exception( 
                            "Type not defined for Collection "
                            + field.getName(  ) + " of class "
                            + obj.getClass(  ) );

                    }

                    Collection list = (Collection)field.get( obj );

                    Class typeClass = (Class)typeField.get( obj.getClass(  ) );

                    loadList( prefNode, keyName + '.', list, typeClass );
                }
                else
                {
                    // Object fieldObj = field.getType().newInstance();
                    loadObject( prefNode, keyName + '.', field.get( obj ) );

                    // field.set(obj, fieldObj);
                }
            }
        }
    }

    protected static Class checkTypeDefined( 
        final Object obj, final String fieldName ) throws Exception
    {
        Field typeField = obj.getClass(  ).getField( fieldName );

        int modsT = typeField.getModifiers(  );

        if( 
            !Modifier.isPublic( modsT ) || !Modifier.isStatic( modsT )
                || ( Class.class != typeField.getType(  ) ) )
        {
            throw new Exception( 
                "Type not defined for Collection " + fieldName + " of class "
                + obj.getClass(  ) );

        }

        return (Class)typeField.get( obj.getClass(  ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefNode DOCUMENT_ME!
     * @param namePrefix DOCUMENT ME!
     * @param list DOCUMENT ME!
     * @param elementClass DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void loadList( 
        final Preferences prefNode, final String namePrefix,
        final Collection list, final Class elementClass )
        throws Exception
    {
        if( prefNode.get( namePrefix + SUFFIX_SIZE, null ) == null )
        {
            return;

        }

        int size = prefNode.getInt( namePrefix + SUFFIX_SIZE, 0 );

        list.clear(  );

        for( int i = 0; i < size; i++ )
        {
            list.add( 
                loadAndCreateObject( 
                    prefNode, elementClass, namePrefix + i + '.' ) );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefNode DOCUMENT_ME!
     * @param namePrefix DOCUMENT_ME!
     * @param map DOCUMENT_ME!
     * @param keyClass DOCUMENT_ME!
     * @param valueClass DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void loadMap( 
        final Preferences prefNode, final String namePrefix, final Map map,
        final Class keyClass, final Class valueClass )
        throws Exception
    {
        if( prefNode.get( namePrefix + SUFFIX_SIZE, null ) == null )
        {
            return;

        }

        int size = prefNode.getInt( namePrefix + SUFFIX_SIZE, 0 );

        map.clear(  );

        for( int i = 0; i < size; i++ )
        {
            Object key =
                loadAndCreateObject( 
                    prefNode, keyClass, namePrefix + i + MAP_SUFFIX_KEY + '.' );

            Object value =
                loadAndCreateObject( 
                    prefNode, keyClass, namePrefix + i + MAP_SUFFIX_KEY + '.' );

            map.put( key, value );

        }
    }

    protected static Object loadAndCreateObject( 
        final Preferences prefNode, final Class objClass, String namePrefix )
        throws Exception
    {
        final Object obj;

        if( objClass == String.class )
        {
            if( namePrefix.endsWith( DOT ) )
            {
                namePrefix = namePrefix.substring( 
                        0, namePrefix.length(  ) - 1 );

            }

            obj = prefNode.get( namePrefix, null );

        }

        else
        {
            obj = objClass.newInstance(  );

            loadObject( prefNode, namePrefix, obj );

        }

        return obj;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefNode DOCUMENT_ME!
     * @param obj DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void save( final Preferences prefNode, final Object obj )
        throws Exception
    {
        final String[] keys = prefNode.keys(  );

        for( int i = 0; i < keys.length; i++ )
        {
            prefNode.remove( keys[i] );
        }

        saveObject( prefNode, obj, StringHelper.EMPTY_STRING );
    }

    protected static void saveObject( 
        final Preferences prefNode, final Object obj, final String namePrefix )
        throws Exception
    {
        if( obj == null )
        {
            return;
        }

        if( obj.getClass(  ).isPrimitive(  ) )
        {
            // primitive type
        }
        else
        {
            // not primitive type
        }

        if( obj instanceof String )
        {
            prefNode.put( namePrefix, (String)obj );
        }
        else if( Color.class.isAssignableFrom( obj.getClass(  ) ) )
        {
            int value = ( (Color)obj ).getRGB(  );

            prefNode.putInt( namePrefix, value );
        }
        else if( Locale.class.isAssignableFrom( obj.getClass(  ) ) )
        {
            final Locale value = (Locale)obj;

            prefNode.put( 
                namePrefix + LOCALE_SUFFIX_LANGUAGE, value.getLanguage(  ) );
            prefNode.put( 
                namePrefix + LOCALE_SUFFIX_COUNTRY, value.getCountry(  ) );
            prefNode.put( 
                namePrefix + LOCALE_SUFFIX_VARIANT, value.getVariant(  ) );
        }
        else if( Map.class.isAssignableFrom( obj.getClass(  ) ) )
        {
            saveMap( prefNode, namePrefix, (Map)obj );
        }
        else if( Collection.class.isAssignableFrom( obj.getClass(  ) ) )
        {
            saveList( prefNode, namePrefix, (Collection)obj );
        }
        else
        {
            final Field[] fields = obj.getClass(  ).getFields(  );

            for( int i = 0; i < fields.length; i++ )
            {
                final Field field = fields[i];

                if( !isFieldSerialized( field ) )
                {
                    continue;
                }

                final String newPrefix =
                    StringHelper.EMPTY_STRING.equals( namePrefix )
                    ? field.getName(  ) : ( namePrefix + '.'
                    + field.getName(  ) );

                if( field.getType(  ).isPrimitive(  ) )
                {
                    if( boolean.class == field.getType(  ) )
                    {
                        prefNode.putBoolean( 
                            newPrefix, field.getBoolean( obj ) );
                    }
                    else if( int.class == field.getType(  ) )
                    {
                        prefNode.putInt( newPrefix, field.getInt( obj ) );
                    }
                    else if( long.class == field.getType(  ) )
                    {
                        prefNode.putLong( newPrefix, field.getLong( obj ) );
                    }
                }
                else
                {
                    saveObject( prefNode, field.get( obj ), newPrefix );
                }
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefNode DOCUMENT_ME!
     * @param namePrefix DOCUMENT ME!
     * @param elements DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    protected static void saveList( 
        final Preferences prefNode, final String namePrefix,
        final Collection elements ) throws Exception
    {
        Iterator it = elements.iterator(  );

        int i;

        for( i = 0; it.hasNext(  ); i++ )
        {
            saveObject( prefNode, it.next(  ), namePrefix + '.' + i );
        }

        prefNode.putInt( namePrefix + '.' + SUFFIX_SIZE, i );

    }

    protected static void saveMap( 
        final Preferences prefNode, final String namePrefix, final Map map )
        throws Exception
    {
        Iterator it = map.keySet(  ).iterator(  );

        int i;

        for( i = 0; it.hasNext(  ); i++ )
        {
            Object key = it.next(  );

            Object value = map.get( key );

            saveObject( prefNode, key, namePrefix + '.' + i + MAP_SUFFIX_KEY );

            saveObject( 
                prefNode, value, namePrefix + '.' + i + MAP_SUFFIX_VALUE );
        }

        prefNode.putInt( namePrefix + SUFFIX_SIZE, i );

    }

    protected static boolean isFieldSerialized( final Field field )
    {
        int mods = field.getModifiers(  );

        if( 
            !Modifier.isPublic( mods )
                || ( Modifier.isTransient( mods ) && Modifier.isFinal( mods ) )
                || Modifier.isStatic( mods ) )
        {
            return false;

        }

        return true;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param from DOCUMENT_ME!
     * @param to DOCUMENT_ME!
     */
    public static void cloneObject( final Object from, final Object to )
    {
        try
        {
            Field[] fields = from.getClass(  ).getFields(  );

            for( int i = 0; i < fields.length; i++ )
            {
                Field field = fields[i];

                if( !isFieldSerialized( field ) )
                {
                    continue;

                }

                Field toField = to.getClass(  ).getField( field.getName(  ) );

                if( field.getType(  ) == int.class )
                {
                    toField.setInt( to, field.getInt( from ) );

                }

                else if( field.getType(  ) == long.class )
                {
                    toField.setLong( to, field.getLong( from ) );

                }

                else if( field.getType(  ) == boolean.class )
                {
                    toField.setBoolean( to, field.getBoolean( from ) );

                }

                else
                {
                    toField.set( to, field.get( from ) );

                }
            }
        }

        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( Level.WARNING, "Error clone simple object", ex );
        }
    }
}

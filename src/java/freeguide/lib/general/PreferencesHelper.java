package freeguide.lib.general;

import freeguide.FreeGuide;

import java.awt.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.prefs.Preferences;

/**
 * Helper for store non-transient non-final non-static public fields of object
 * in Preferences, using reflection. It supports types: List, String, int,
 * long. When List variable defined, then class should have static variable
 * fieldname_TYPE, which store class for create list.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class PreferencesHelper
{

    /**
     * DOCUMENT_ME!
     *
     * @param prefNode DOCUMENT_ME!
     * @param namePrefix DOCUMENT ME!
     * @param obj DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void loadObject( 
        final Preferences prefNode, final String namePrefix, final Object obj )
        throws Exception
    {

        final Set existKeys =
            new TreeSet( Arrays.asList( prefNode.keys(  ) ) );

        Field[] fields = obj.getClass(  ).getFields(  );

        for( int i = 0; i < fields.length; i++ )
        {

            Field field = fields[i];

            if( !isFieldSerialized( field ) )
            {

                continue;

            }

            final String keyName = namePrefix + field.getName(  );

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

            else if( boolean.class == field.getType(  ) )
            {

                if( existKeys.contains( keyName ) )
                {

                    boolean value = prefNode.getBoolean( keyName, false );

                    field.setBoolean( obj, value );

                }
            }

            else if( int.class == field.getType(  ) )
            {

                if( existKeys.contains( keyName ) )
                {

                    int value = prefNode.getInt( keyName, 0 );

                    field.setInt( obj, value );

                }
            }

            else if( long.class == field.getType(  ) )
            {

                if( existKeys.contains( keyName ) )
                {

                    long value = prefNode.getLong( keyName, 0 );

                    field.setLong( obj, value );

                }
            }

            else if( Map.class.isAssignableFrom( field.getType(  ) ) )
            {

                Class keyType =
                    checkTypeDefined( obj, field.getName(  ) + "_KEY_TYPE" );

                Class valueType =
                    checkTypeDefined( obj, field.getName(  ) + "_VALUE_TYPE" );

                Map map = (Map)field.get( obj );

                loadMap( prefNode, keyName + ".", map, keyType, valueType );

                //field.set( obj, list );
            }

            else if( Collection.class.isAssignableFrom( field.getType(  ) ) )
            {

                Field typeField =
                    obj.getClass(  ).getField( field.getName(  ) + "_TYPE" );

                int modsT = typeField.getModifiers(  );

                if( 
                    !Modifier.isPublic( modsT ) || !Modifier.isStatic( modsT )
                        || ( Class.class != typeField.getType(  ) ) )
                {
                    throw new Exception( 
                        "Type not defined for Collection " + field.getName(  )
                        + " of class " + obj.getClass(  ) );

                }

                Collection list = (Collection)field.get( obj );

                /*if( List.class.isAssignableFrom( field.getType(  ) ) )


                {


                list = new ArrayList(  );


                }


                else if( Set.class.isAssignableFrom( field.getType(  ) ) )


                {


                list = new TreeSet(  );


                }


                else


                {


                throw new Exception(


                "Unknown collection type: " + field.getType(  ) );


                }*/
                Class typeClass = (Class)typeField.get( obj.getClass(  ) );

                loadList( prefNode, keyName + ".", list, typeClass );

                //field.set( obj, list );
            }

            else
            {

                // Object fieldObj = field.getType().newInstance();
                loadObject( prefNode, keyName + ".", field.get( obj ) );

                // field.set(obj, fieldObj);
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

        if( prefNode.get( namePrefix + "size", null ) == null )
        {

            return;

        }

        int size = prefNode.getInt( namePrefix + "size", 0 );

        list.clear(  );

        for( int i = 0; i < size; i++ )
        {
            list.add( 
                loadAndCreateObject( 
                    prefNode, elementClass, namePrefix + i + "." ) );

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

        if( prefNode.get( namePrefix + "size", null ) == null )
        {

            return;

        }

        int size = prefNode.getInt( namePrefix + "size", 0 );

        map.clear(  );

        for( int i = 0; i < size; i++ )
        {

            Object key =
                loadAndCreateObject( 
                    prefNode, keyClass, namePrefix + i + ".key." );

            Object value =
                loadAndCreateObject( 
                    prefNode, keyClass, namePrefix + i + ".value." );

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

            if( namePrefix.endsWith( "." ) )
            {
                namePrefix =
                    namePrefix.substring( 0, namePrefix.length(  ) - 1 );

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
     * @param namePrefix DOCUMENT ME!
     * @param obj DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void saveObject( 
        final Preferences prefNode, String namePrefix, final Object obj )
        throws Exception
    {

        if( obj == null )
        { // remove data if object is null

            if( namePrefix.endsWith( "." ) )
            {
                namePrefix =
                    namePrefix.substring( 0, namePrefix.length(  ) - 1 );

            }

            final String[] keys = prefNode.keys(  );

            for( int i = 0; i < keys.length; i++ )
            {

                if( keys[i].startsWith( namePrefix ) )
                {
                    prefNode.remove( keys[i] );

                }
            }

            return;

        }

        if( obj instanceof String )
        {

            if( namePrefix.endsWith( "." ) )
            {
                namePrefix =
                    namePrefix.substring( 0, namePrefix.length(  ) - 1 );

            }

            prefNode.put( namePrefix, (String)obj );

            return;

        }

        Field[] fields = obj.getClass(  ).getFields(  );

        for( int i = 0; i < fields.length; i++ )
        {

            Field field = fields[i];

            if( !isFieldSerialized( field ) )
            {

                continue;

            }

            final String keyName = namePrefix + field.getName(  );

            if( Color.class.isAssignableFrom( field.getType(  ) ) )
            {

                int value = ( (Color)field.get( obj ) ).getRGB(  );

                prefNode.putInt( keyName, value );

            }

            else if( boolean.class == field.getType(  ) )
            {
                prefNode.putBoolean( keyName, field.getBoolean( obj ) );

            }

            else if( int.class == field.getType(  ) )
            {
                prefNode.putInt( keyName, field.getInt( obj ) );

            }

            else if( long.class == field.getType(  ) )
            {
                prefNode.putLong( keyName, field.getLong( obj ) );

            }

            else if( Map.class.isAssignableFrom( field.getType(  ) ) )
            {
                saveMap( prefNode, keyName + ".", (Map)field.get( obj ) );

            }

            else if( Collection.class.isAssignableFrom( field.getType(  ) ) )
            {
                saveList( 
                    prefNode, keyName + ".", (Collection)field.get( obj ) );

            }

            else
            {
                saveObject( prefNode, keyName + ".", field.get( obj ) );

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
    public static void saveList( 
        final Preferences prefNode, final String namePrefix,
        final Collection elements ) throws Exception
    {

        Iterator it = elements.iterator(  );

        int i = 0;

        while( it.hasNext(  ) )
        {

            Object obj = it.next(  );

            if( obj.getClass(  ) == String.class )
            {
                prefNode.put( namePrefix + i, (String)obj );

            }

            else
            {
                saveObject( prefNode, namePrefix + i + ".", obj );

            }

            i++;

        }

        prefNode.putInt( namePrefix + "size", i );

    }

    protected static void saveMap( 
        final Preferences prefNode, final String namePrefix, final Map map )
        throws Exception
    {

        Iterator it = map.keySet(  ).iterator(  );

        int i = 0;

        while( it.hasNext(  ) )
        {

            Object key = it.next(  );

            Object value = map.get( key );

            saveObject( prefNode, namePrefix + i + ".key.", key );

            saveObject( prefNode, namePrefix + i + ".value.", value );

            i++;

        }

        prefNode.putInt( namePrefix + "size", i );

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
            FreeGuide.log.log( Level.WARNING, "Error clone simple object", ex );
        }
    }
}

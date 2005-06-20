package freeguide.lib.general;

import freeguide.plugins.ILocalizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Helper for some language releated functions.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class LanguageHelper implements ILocalizer
{

    /**
     * Translations for selected locale. Map(String(key), String(localized
     * string)).
     */
    protected Map translation;
    final protected Locale locale;

    /**
     * Create language support object. We use it instead ResourceBundle,
     * because it works with UTF-8 property files.
     *
     * @param classLoader ClassLoader for resource
     * @param resourcePrefix package prefix in form
     *        "freeguide/plugins/module/messages"
     * @param locale
     *
     * @throws IOException
     */
    public LanguageHelper( 
        final ClassLoader classLoader, final String resourcePrefix,
        final Locale locale ) throws IOException
    {
        this.locale = locale;

        String fileName =
            resourcePrefix + "." + locale.getLanguage(  ) + ".properties";

        InputStream in = classLoader.getResourceAsStream( fileName );

        if( in != null )
        {
            translation = new TreeMap(  );

            loadProperties( in, translation );

            fileName =
                resourcePrefix + "." + locale.toString(  ) + ".properties";

            in = classLoader.getResourceAsStream( fileName );

            if( in != null )
            {
                loadProperties( in, translation );

            }
        }

        else
        {
            throw new IOException( 
                "There is no resource file '" + fileName + "' for locale '"
                + locale + "'" );

        }
    }

    /**
     * Load locales list by parse properties.list file.
     *
     * @param classLoader ClassLoader for resource
     * @param resourcePrefix package prefix in form
     *        "freeguide/plugins/module/messages"
     *
     * @return locales list
     *
     * @throws IOException
     */
    public static Locale[] getLocaleList( 
        final ClassLoader classLoader, final String resourcePrefix )
        throws IOException
    {

        final String packageName;

        final String fPrefix;

        final int beg = resourcePrefix.lastIndexOf( '/' );

        if( beg != -1 )
        {
            packageName = resourcePrefix.substring( 0, beg );

            fPrefix = resourcePrefix.substring( beg + 1 ) + ".";

        }

        else
        {
            packageName = "";

            fPrefix = resourcePrefix + ".";

        }

        final InputStream in =
            classLoader.getResourceAsStream( packageName + "/ls" );

        if( in != null )
        {

            final String[] properitesFiles = loadStrings( in );

            final List result = new ArrayList(  );

            for( int i = 0; i < properitesFiles.length; i++ )
            {

                if( 
                    properitesFiles[i].startsWith( fPrefix )
                        && properitesFiles[i].endsWith( ".properties" ) )
                {

                    final String localeName =
                        properitesFiles[i].substring( 
                            fPrefix.length(  ),
                            properitesFiles[i].length(  )
                            - ".properties".length(  ) );

                    int up = localeName.indexOf( '_' );

                    if( up == -1 )
                    { // add language only locale
                        result.add( new Locale( localeName ) );

                    }

                    else
                    { // add language and country locale
                        result.add( 
                            new Locale( 
                                localeName.substring( 0, up ),
                                localeName.substring( up + 1 ) ) );

                    }
                }
            }

            return (Locale[])result.toArray( new Locale[result.size(  )] );

        }

        else
        {

            return new Locale[0];

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param classLoader DOCUMENT_ME!
     * @param resourcePrefix DOCUMENT_ME!
     * @param resourceSuffix DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static String[] listResources( 
        final ClassLoader classLoader, final String resourcePrefix,
        final String resourceSuffix ) throws IOException
    {

        final String packageName;

        final String fPrefix;

        final int beg = resourcePrefix.lastIndexOf( '/' );

        if( beg != -1 )
        {
            packageName = resourcePrefix.substring( 0, beg );

            fPrefix = resourcePrefix.substring( beg + 1 );

        }

        else
        {
            packageName = "";

            fPrefix = resourcePrefix;

        }

        final InputStream in =
            classLoader.getResourceAsStream( packageName + "/ls" );

        if( in != null )
        {

            final String[] properitesFiles = loadStrings( in );

            final List result = new ArrayList(  );

            for( int i = 0; i < properitesFiles.length; i++ )
            {

                if( 
                    properitesFiles[i].startsWith( fPrefix )
                        && properitesFiles[i].endsWith( resourceSuffix ) )
                {
                    result.add( packageName + "/" + properitesFiles[i] );

                }
            }

            return (String[])result.toArray( new String[result.size(  )] );

        }

        else
        {

            return new String[0];

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Set getKeys(  )
    {

        return translation.keySet(  );

    }

    /**
     * Get localized message by key.
     *
     * @param key key for message
     *
     * @return localized messages
     */
    public String getLocalizedMessage( final String key )
    {

        return getString( key );

    }

    /**
     * Get localized message by key with parameters.
     *
     * @param key key for message
     * @param messageArguments message's parameters
     *
     * @return localized messages
     */
    public String getLocalizedMessage( 
        final String key, final Object[] messageArguments )
    {

        MessageFormat formatter =
            new MessageFormat( getString( key ), getLocale(  ) );

        return formatter.format( messageArguments );

    }

    /**
     * Get localized string from loaded translation.
     *
     * @param key key
     *
     * @return string
     */
    public String getString( final String key )
    {

        if( translation != null )
        {

            final String result = (String)translation.get( key );

            if( result != null )
            {

                return result;

            }

            else
            {

                return "!" + key + "!";

            }
        }

        else
        {

            return "!" + key + "!(locale not selected)";

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Locale getLocale(  )
    {

        return locale;

    }

    /**
     * Load UTF-8 properties file.
     *
     * @param in input stream
     * @param result DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static void loadProperties( final InputStream in, final Map result )
        throws IOException
    {

        final BufferedReader rd =
            new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

        String line;

        while( ( line = rd.readLine(  ) ) != null )
        {

            if( line.startsWith( "#" ) || line.startsWith( ";" ) )
            {

                continue;

            }

            int i = line.indexOf( '=' );

            if( i == -1 )
            {

                continue;

            }

            final String key = line.substring( 0, i );
            String value = line.substring( i + 1 );
            value = StringHelper.replaceAll( value, "\\n", "\n" );
            result.put( key, value );

        }
    }

    /**
     * Load UTF-8 strings file.
     *
     * @param in input stream
     *
     * @return Map with properties
     *
     * @throws IOException DOCUMENT ME!
     */
    public static String[] loadStrings( final InputStream in )
        throws IOException
    {

        final BufferedReader rd =
            new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

        final List result = new ArrayList(  );

        String line;

        while( ( line = rd.readLine(  ) ) != null )
        {

            if( 
                line.startsWith( "#" ) || line.startsWith( ";" )
                    || "".equals( line.trim(  ) ) )
            {

                continue;

            }

            result.add( line );

        }

        return (String[])result.toArray( new String[result.size(  )] );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param in DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static String loadFileAsString( final InputStream in )
        throws IOException
    {

        if( in == null )
        {

            return null;

        }

        final BufferedReader rd =
            new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

        final StringBuffer result = new StringBuffer(  );

        String line;

        while( ( line = rd.readLine(  ) ) != null )
        {
            result.append( line );

        }

        return result.toString(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param want DOCUMENT_ME!
     * @param supported DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static Locale getPreferredLocale( 
        final Locale[] want, final Locale[] supported )
    {

        for( int i = 0; i < want.length; i++ )
        {

            for( int j = 0; j < supported.length; j++ )
            {

                if( want[i].equals( supported[j] ) )
                {

                    return want[i];
                }
            }
        }

        return Locale.ENGLISH;
    }
}

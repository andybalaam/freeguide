package freeguide.lib.general;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Helper for some language releated functions.
 *
 * @author Alex Buloichik alex73 at zaval.org
 */
public class LanguageHelper
{

    /**
     * Translations for selected locale. Map String(key), String(localized
     * string).
     */
    protected Map translation;

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

        final String fileName =
            resourcePrefix + "." + locale.getLanguage(  ) + ".properties";
        final InputStream in = classLoader.getResourceAsStream( fileName );

        if( in != null )
        {
            translation = loadProperties( in );
        }
        else
        {
            throw new IOException( 
                "There is no resource file '" + fileName + "' for locale '"
                + locale + "'" );
        }
    }

    /**
     * Load locales list from "i18n.list" resource file.
     *
     * @param classLoader ClassLoader for resource
     * @param resourcePrefix package prefix in form "freeguide/plugins/module"
     *
     * @return locales list
     *
     * @throws IOException
     */
    public static Locale[] getLocaleList( 
        final ClassLoader classLoader, final String resourcePrefix )
        throws IOException
    {

        final InputStream in =
            classLoader.getResourceAsStream( resourcePrefix + "/i18n.list" );

        if( in != null )
        {

            final String[] languages = loadStrings( in );
            final Locale[] result = new Locale[languages.length];

            for( int i = 0; i < languages.length; i++ )
            {
                result[i] = new Locale( languages[i] );
            }

            return result;
        }
        else
        {

            return new Locale[0];
        }
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
     * Load UTF-8 properties file.
     *
     * @param in input stream
     *
     * @return Map with properties
     *
     * @throws IOException DOCUMENT ME!
     */
    protected static Map loadProperties( final InputStream in )
        throws IOException
    {

        final BufferedReader rd =
            new BufferedReader( new InputStreamReader( in, "UTF-8" ) );
        final Map result = new TreeMap(  );
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

            result.put( line.substring( 0, i ), line.substring( i + 1 ) );
        }

        return result;
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
    protected static String[] loadStrings( final InputStream in )
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
}

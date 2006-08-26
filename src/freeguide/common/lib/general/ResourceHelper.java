package freeguide.common.lib.general;

import java.io.*;
import java.net.URLConnection;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper method to load resources
 */
public class ResourceHelper
{

    /**
     * Load UTF-8 properties file.
     *
     * @param resourceName input stream
     * @param result DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @deprecated
     */
    public static void loadProperties(
        final String resourceName, final Map result ) throws IOException
    {
        final InputStream in = getUncachedStream( resourceName );

        if( in == null )
        {
            return;
        }

        try
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
        finally
        {
            in.close(  );
        }
    }

    /**
     * Load UTF-8 strings file.
     *
     * @param resourceName input stream
     *
     * @return Map with properties
     *
     * @throws IOException DOCUMENT ME!
     */
    public static String[] loadStrings( final String resourceName )
        throws IOException
    {
        final InputStream in = getUncachedStream( resourceName );

        if( in == null )
        {
            return new String[0];
        }

        try
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
        finally
        {
            in.close(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param resourceName DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static String loadResourceAsString( final String resourceName )
        throws IOException
    {
        final String lineSeparator = System.getProperty( "line.separator" );

        final InputStream in = getUncachedStream( resourceName );

        if( in == null )
        {
            return null;
        }

        try
        {
            final BufferedReader rd =
                new BufferedReader( new InputStreamReader( in, "UTF-8" ) );

            final StringBuffer result = new StringBuffer(  );

            String line;

            while( ( line = rd.readLine(  ) ) != null )
            {
                result.append( line );
                result.append( lineSeparator );
            }

            return result.toString(  );
        }
        finally
        {
            in.close(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param resourceName DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static byte[] loadResourceAsByteArray( final String resourceName )
        throws IOException
    {
        final InputStream in = getUncachedStream( resourceName );

        if( in == null )
        {
            return null;
        }

        try
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream(  );
            int len;
            byte[] buffer = new byte[65536];

            while( ( len = in.read( buffer ) ) >= 0 )
            {
                out.write( buffer, 0, len );
            }

            return out.toByteArray(  );
        }
        finally
        {
            in.close(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param url DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static InputStream getUncachedStream( final URL url )
        throws IOException
    {
        if( url != null )
        {
            URLConnection conn = url.openConnection(  );
            conn.setUseCaches( false );

            return conn.getInputStream(  );
        }
        else
        {
            return null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param resourceName DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static InputStream getUncachedStream( final String resourceName )
        throws IOException
    {
        return getUncachedStream(
            ResourceHelper.class.getClassLoader(  ).getResource( resourceName ) );
    }

}

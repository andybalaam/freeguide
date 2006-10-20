package freeguide.common.lib.general;

import java.io.*;

import java.net.URL;
import java.net.URLConnection;

/**
 * Helper method to load resources
 */
public class ResourceHelper
{
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

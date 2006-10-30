package freeguide.common.lib.general;

import freeguide.common.lib.fgspecific.Application;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.URL;

import java.util.logging.Level;

/**
 * Helper for support some file operations.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class FileHelper
{
    /**
     * ToDo: DOCUMENT ME!
     *
     * @param in ToDo: DOCUMENT ME!
     * @param out ToDo: DOCUMENT ME!
     *
     * @throws IOException ToDo: DOCUMENT ME!
     */
    public static void copy( final File in, final File out )
        throws IOException
    {
        FileInputStream rd = new FileInputStream( in );

        try
        {
            FileOutputStream wr = new FileOutputStream( out );

            try
            {
                byte[] buffer = new byte[65536];
                int len;

                while( true )
                {
                    len = rd.read( buffer );

                    if( len < 0 )
                    {
                        break;
                    }

                    wr.write( buffer, 0, len );
                }

                wr.flush(  );
            }
            finally
            {
                wr.close(  );
            }
        }
        finally
        {
            rd.close(  );
        }
    }

    /**
     * Open file in the current browser.
     *
     * @param filename file name
     */
    public static void openFile( final String filename )
    {
        try
        {
            String cmd =
                StringHelper.replaceAll( 
                    Application.getInstance(  ).getBrowserCommand(  ), "%url%",
                    new File( filename ).toURL(  ).toExternalForm(  ) );
            Utils.execNoWait( cmd );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( Level.WARNING, "Error open file " + filename, ex );
        }
    }

    /**
     * Open url in the current browser.
     *
     * @param url url
     */
    public static void openURL( final URL url )
    {
        try
        {
            String cmd =
                StringHelper.replaceAll( 
                    Application.getInstance(  ).getBrowserCommand(  ), "%url%",
                    url.toExternalForm(  ) );
            Utils.execNoWait( cmd );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( 
                Level.WARNING, "Error open url " + url.toExternalForm(  ), ex );
        }
    }

    /**
     * Deletes a whole directory recursively (also deletes a single
     * file).
     *
     * @param dir The directory to delete
     */
    public static void deleteDir( File dir )
    {
        if( !dir.exists(  ) )
        {
            return;
        }

        if( dir.isDirectory(  ) )
        {
            String[] list = dir.list(  );

            for( int i = 0; i < list.length; i++ )
            {
                deleteDir( 
                    new File( dir.getPath(  ) + File.separator + list[i] ) );
            }
        }

        dir.delete(  );
    }

    /**
     * Write data to file.
     *
     * @param fileName file name
     * @param data data
     *
     * @throws IOException
     */
    public static void write( final String fileName, final String data )
        throws IOException
    {
        OutputStreamWriter out =
            new OutputStreamWriter( new FileOutputStream( fileName ), "UTF-8" );

        try
        {
            out.write( data );
            out.flush(  );
        }
        finally
        {
            out.close(  );
        }
    }

    /**
     * Method unpacks files from classpath. It used for unpack docs
     * into temp directory and xmltv.
     *
     * @param lsPath resource path with file with list of files
     * @param packagePrefix DOCUMENT ME!
     * @param outDir DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public static void unpackFiles( 
        final String lsPath, final String packagePrefix, final File outDir )
        throws IOException
    {
        final InputStream inLs =
            FileHelper.class.getClassLoader(  ).getResourceAsStream( lsPath );

        if( inLs == null )
        {
            throw new FileNotFoundException( "There is no " + lsPath );
        }

        final BufferedReader rd =
            new BufferedReader( new InputStreamReader( inLs ) );

        while( true )
        {
            final String line = rd.readLine(  );

            if( line == null )
            {
                break;
            }

            new File( outDir, line ).getParentFile(  ).mkdirs(  );
            writeFile( packagePrefix + line, new File( outDir, line ) );
        }

        try
        {
            rd.close(  );
        }
        finally
        {
            rd.close(  );
        }
    }

    /**
     * Write data from classpath resource into file.
     *
     * @param classpath
     * @param outFile
     *
     * @throws IOException
     * @throws FileNotFoundException DOCUMENT ME!
     */
    protected static void writeFile( 
        final String classpath, final File outFile ) throws IOException
    {
        final InputStream in =
            FileHelper.class.getClassLoader(  ).getResourceAsStream( 
                classpath );

        if( in == null )
        {
            throw new FileNotFoundException( "There is no " + classpath );
        }

        try
        {
            final OutputStream out =
                new BufferedOutputStream( new FileOutputStream( outFile ) );

            try
            {
                final byte[] buffer = new byte[65536];

                while( true )
                {
                    int len = in.read( buffer );

                    if( len < 0 )
                    {
                        break;
                    }

                    out.write( buffer, 0, len );
                }
            }
            finally
            {
                out.close(  );
            }
        }
        finally
        {
            in.close(  );
        }
    }

    /**
     * Unpack and show docs.
     *
     * @throws IOException
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public static void showDocs(  ) throws IOException
    {
        final String tempDir = System.getProperty( "java.io.tmpdir" );

        if( tempDir == null )
        {
            throw new FileNotFoundException( "Temp directory doesn't defined" );
        }

        final File outDir = new File( tempDir, "freeguide-docs" );
        FileHelper.unpackFiles( "other/docs/ls-docs", "other/docs/", outDir );
        FileHelper.openFile( 
            new File( outDir, "userguide.html" ).getAbsolutePath(  ) );
    }
}

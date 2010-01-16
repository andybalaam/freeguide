package freeguide.common.lib.general;

import java.awt.Desktop;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import freeguide.common.lib.fgspecific.Application;

/**
 * Helper for support some file operations.
 * 
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class FileHelper implements IBrowserLauncher
{
    protected static final String URL_PATTERN = "%url%";

    public static class UnableToFindFileToBrowseException extends Exception
    {
        private static final long serialVersionUID = 1L;

        public String filePath;

        public UnableToFindFileToBrowseException( String filePath )
        {
            this.filePath = filePath;
        }
    }

    protected static final String DEFAULT_CHARSET = "UTF-8";

    protected static final String TEMP_PROPERTY = "java.io.tmpdir";

    /**
     * ToDo: DOCUMENT ME!
     * 
     * @param in ToDo: DOCUMENT ME!
     * @param out ToDo: DOCUMENT ME!
     * 
     * @throws IOException ToDo: DOCUMENT ME!
     */
    public static void copy( final File in, final File out ) throws IOException
    {
        FileInputStream rd = new FileInputStream( in );

        try
        {
            FileOutputStream wr = new FileOutputStream( out );

            try
            {
                copy( rd, wr );
                wr.flush();
            }
            finally
            {
                wr.close();
            }
        }
        finally
        {
            rd.close();
        }
    }

    /**
     * DOCUMENT_ME!
     * 
     * @param in DOCUMENT_ME!
     * @param out DOCUMENT_ME!
     * 
     * @throws IOException DOCUMENT_ME!
     */
    public static void copy( final InputStream in, final OutputStream out )
        throws IOException
    {
        byte[] buffer = new byte[65536];
        int len;

        while( true )
        {
            len = in.read( buffer );

            if( len < 0 )
            {
                break;
            }

            out.write( buffer, 0, len );
        }
    }

    /**
     * Open file in the current browser.
     * 
     * @param filename file name
     * @throws MalformedURLException
     */
    public void browseLocalFile( final File localFile ) throws Exception
    {
        if( !localFile.exists() )
        {
            throw new FileHelper.UnableToFindFileToBrowseException( localFile
                .toString() );
        }

        browseURI( localFile.toURI() );
    }

    /**
     * Open url in the current browser.
     * 
     * @param url url
     */
    public void browseURL( final URL url ) throws Exception
    {
        browseURI( url.toURI() );
    }


    public void browseURI( final URI uri ) throws Exception
    {
        boolean doneWithDesktopAPI = false;
        if( Desktop.isDesktopSupported() )
        {
            Desktop desktop = Desktop.getDesktop();
            if( desktop.isSupported( Desktop.Action.BROWSE ) )
            {
                try
                {
                    desktop.browse( uri );
                    doneWithDesktopAPI = true;
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                    // Browsing failed. We didn't set the
                    // doneWithDesktopAPI flag, so we will
                    // try the old-fashioned way.
                }
            }
        }

        if( !doneWithDesktopAPI )
        {
            // Try to open it the old-fashioned way
            String cmd = StringHelper
                .replaceAll( Application.getInstance().getBrowserCommand(),
                    URL_PATTERN, uri.toURL().toExternalForm() );
            Utils.execNoWait( cmd );
        }
    }

    /**
     * Deletes a whole directory recursively (also deletes a single file).
     * 
     * @param dir The directory to delete
     */
    public static void deleteDir( File dir )
    {
        if( !dir.exists() )
        {
            return;
        }

        if( dir.isDirectory() )
        {
            String[] list = dir.list();

            for( int i = 0; i < list.length; i++ )
            {
                deleteDir( new File( dir.getPath() + File.separator + list[i] ) );
            }
        }

        dir.delete();
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
        OutputStreamWriter out = new OutputStreamWriter( new FileOutputStream(
            fileName ), DEFAULT_CHARSET );

        try
        {
            out.write( data );
            out.flush();
        }
        finally
        {
            out.close();
        }
    }

    /**
     * Method unpacks files from classpath. Used to unpack docs into temp
     * directory and xmltv from its jar.
     * 
     * @param lsPath resource path with file with list of files
     * @param packagePrefix DOCUMENT ME!
     * @param outDir DOCUMENT ME!
     * 
     * @return boolean indicates success
     */
    public static boolean unpackFiles( final String lsPath,
        final String packagePrefix, final File outDir )
    {
        final InputStream inLs = FileHelper.class.getClassLoader()
            .getResourceAsStream( lsPath );

        if( inLs == null )
        {
            String msg = "Unable to unpack file '" + lsPath
                + "'.  It does not exist in the resource.";
            System.err.println( msg );
            Application.getInstance().getLogger().severe( msg );

            return false;
        }

        final BufferedReader rd = new BufferedReader( new InputStreamReader(
            inLs ) );

        try
        {
            while( true )
            {
                final String line = rd.readLine();

                if( line == null )
                {
                    break;
                }

                new File( outDir, line ).getParentFile().mkdirs();
                writeResourceToFile( packagePrefix + line, new File( outDir,
                    line ) );
            }
        }
        catch( IOException ioe )
        {
            String msg = "Error unpacking files: '" + ioe.getMessage() + "'.";
            System.err.println( msg );
            Application.getInstance().getLogger().severe( msg );

            return false;
        }
        finally
        {
            try
            {
                rd.close();
            }
            catch( IOException e )
            {
                // nothing the consumer can do about this, so ignore -RSH
            }
        }

        return true;
    }

    public static void unzip( File zipFilePath, File outDir )
        throws FileNotFoundException, IOException
    {
        ZipFile zipFile = new ZipFile( zipFilePath );

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while( entries.hasMoreElements() )
        {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            File entryFile = new File( outDir, entry.getName() );

            if( entry.isDirectory() )
            {
                entryFile.mkdirs();
            }
            else
            {
                entryFile.getParentFile().mkdirs();

                InputStream is = zipFile.getInputStream( entry );
                BufferedOutputStream os = new BufferedOutputStream(
                    new FileOutputStream( entryFile ) );

                copy( is, os );

                os.close();
            }
        }

        zipFile.close();
    }

    /**
     * Write data from classpath resource into file.
     * 
     * @param resourcePath
     * @param outFile
     * @throws IOException
     * 
     * @throws IOException
     * @throws FileNotFoundException DOCUMENT ME!
     */

    public static void writeResourceToFile( final String resourcePath,
        final File outFile ) throws IOException
    {
        writeResourceToFile( resourcePath, outFile, FileHelper.class
            .getClassLoader() );
    }

    public static void writeResourceToFile( final String resourcePath,
        final File outFile, ClassLoader classLoader ) throws IOException
    {
        final InputStream in = classLoader.getResourceAsStream( resourcePath );

        if( in == null )
        {
            throw new FileNotFoundException( "There is no " + resourcePath );
        }

        try
        {
            final OutputStream out = new BufferedOutputStream(
                new FileOutputStream( outFile ) );

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
                out.close();
            }
        }
        finally
        {
            in.close();
        }
    }
}

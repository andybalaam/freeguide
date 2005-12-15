package freeguide.lib.general;

import freeguide.lib.fgspecific.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING, "Error open file " + filename, ex );
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
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING, "Error open url " + url.toExternalForm(  ), ex );
        }
    }

    /**
     * Deletes a whole directory recursively (also deletes a single file).
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
}

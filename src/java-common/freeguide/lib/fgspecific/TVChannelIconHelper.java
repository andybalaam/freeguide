package freeguide.lib.fgspecific;

import freeguide.lib.fgspecific.data.TVChannel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.logging.Level;

import javax.swing.ImageIcon;

/**
 * Helper for work with channel's icons.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public class TVChannelIconHelper
{

    /**
     * Get channel's icon. Get icon from web if required.
     *
     * @param channel channel
     *
     * @return icon file, or null if icon not exists
     */
    public static File getIconFile( final TVChannel channel )
    {

        File result = new File( getIconFileName( channel ) );

        if( !result.canRead(  ) )
        {

            if( !loadIcon( channel.getIconURL(  ), result ) )
            {
                result = null;
            }
        }

        return result;
    }

    /**
     * Get channel's icon. Get icon from web if required.
     *
     * @param channel channel
     *
     * @return icon object, or null if icon not exists
     */
    public static ImageIcon getIcon( final TVChannel channel )
    {

        final File iconFile = getIconFile( channel );

        if( iconFile == null )
        {

            return null;
        }

        return new ImageIcon( iconFile.getPath(  ) );
    }

    /**
     * Get channel's icon filename. It doesn't download icon.
     *
     * @param channel channel
     *
     * @return icon filename
     */
    public static String getIconFileName( final TVChannel channel )
    {

        StringBuffer sb = new StringBuffer(  );

        sb.append( Application.getInstance(  ).getWorkingDirectory(  ) );

        sb.append( '/' );

        sb.append( TVChannel.ICONCACHE_SUBDIR );

        sb.append( '/' );

        File dir = new File( sb.toString(  ) );

        if( !dir.exists(  ) )
        {
            dir.mkdirs(  );
        }

        sb.append( 
            channel.getID(  ).replace( '.', '_' ).replaceAll( 
                "[^a-zA-Z0-9_]", "-" ) );

        return sb.toString(  );
    }

    /**
     * Load icon from URL.
     *
     * @param url icon url
     * @param outFile file
     *
     * @return true if icon downloaded, else - false
     */
    protected static boolean loadIcon( final String url, final File outFile )
    {

        if( url == null )
        {

            // there is no icon for channel
            return false;
        }

        try
        {

            InputStream i = new URL( url ).openStream(  );

            try
            {

                ByteArrayOutputStream o = new ByteArrayOutputStream(  );

                byte[] buffer = new byte[4096];

                int bCount;

                while( ( bCount = i.read( buffer ) ) != -1 )
                {
                    o.write( buffer, 0, bCount );
                }

                o.close(  );

                FileOutputStream of = new FileOutputStream( outFile );

                try
                {
                    of.write( o.toByteArray(  ) );
                    of.flush(  );
                }
                finally
                {
                    of.close(  );
                }
            }
            finally
            {
                i.close(  );
            }
        }
        catch( MalformedURLException ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.FINE, "Error cache channel icon", ex );

            return false;
        }
        catch( IOException ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.FINE, "Error cache channel icon", ex );

            return false;
        }

        return true;
    }
}
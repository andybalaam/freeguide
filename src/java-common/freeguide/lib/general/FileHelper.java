package freeguide.lib.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
}

package freeguide.startup;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Class for update application modules.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class StartupUpdates
{
    /**
     * DOCUMENT_ME!
     *
     * @param installDir DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void update( final File installDir ) throws IOException
    {
        deleteFiles( installDir );
        unzipFiles( installDir );
        new File( installDir, "updates" ).delete(  );
    }

    protected void unzipFiles( final File installDir )
        throws IOException
    {
        File[] files =
            new File( installDir, "updates" ).listFiles( 
                new FileFilter(  )
                {
                    public boolean accept( File pathname )
                    {
                        return !pathname.isDirectory(  )
                        && pathname.getName(  ).toLowerCase(  ).endsWith( 
                            ".zip" );
                    }
                } );

        if( files != null )
        {
            for( int i = 0; i < files.length; i++ )
            {
                System.out.println( 
                    "Unzip file '" + files[i] + "' to "
                    + installDir.getPath(  ) );

                ZipFile zip = new ZipFile( files[i] );

                try
                {
                    Enumeration zipEntries = zip.entries(  );

                    while( zipEntries.hasMoreElements(  ) )
                    {
                        ZipEntry entry = (ZipEntry)zipEntries.nextElement(  );
                        File outFile =
                            new File( installDir, entry.getName(  ) );

                        if( entry.isDirectory(  ) )
                        {
                            outFile.mkdirs(  );
                        }
                        else
                        {
                            InputStream in = zip.getInputStream( entry );

                            try
                            {
                                unzipFile( in, outFile );
                            }
                            finally
                            {
                                in.close(  );
                            }
                        }
                    }
                }
                finally
                {
                    zip.close(  );
                }

                if( !files[i].delete(  ) )
                {
                    System.err.println( 
                        "Error delete " + files[i].getPath(  ) );
                }
            }
        }
    }

    protected void unzipFile( final InputStream from, final File to )
        throws IOException
    {
        byte[] buffer = new byte[65536];
        int len;
        BufferedOutputStream out =
            new BufferedOutputStream( new FileOutputStream( to ) );

        try
        {
            while( ( len = from.read( buffer ) ) > 0 )
            {
                out.write( buffer, 0, len );
            }

            out.flush(  );
        }
        finally
        {
            out.close(  );
        }
    }

    protected void deleteFiles( final File installDir )
        throws IOException
    {
        File deleteList = new File( installDir, "updates/delete.list" );

        if( deleteList.exists(  ) )
        {
            BufferedReader in =
                new BufferedReader( 
                    new InputStreamReader( 
                        new FileInputStream( deleteList ), "UTF-8" ) );

            try
            {
                while( true )
                {
                    String line = in.readLine(  );

                    if( line == null )
                    {
                        break;
                    }

                    File toDelete = new File( installDir, line );

                    if( !toDelete.delete(  ) )
                    {
                        if( 
                            !toDelete.isDirectory(  )
                                && !"startup.jar".equals( 
                                    toDelete.getName(  ) ) )
                        {
                            System.err.println( 
                                "Error delete " + toDelete.getPath(  ) );
                        }
                    }
                }
            }
            finally
            {
                in.close(  );
            }

            if( !deleteList.delete(  ) )
            {
                System.err.println( "Error delete " + deleteList.getPath(  ) );
            }
        }
    }
}

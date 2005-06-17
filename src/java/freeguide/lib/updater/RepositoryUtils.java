package freeguide.lib.updater;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.Application;

import freeguide.lib.grabber.HttpBrowser;

import freeguide.lib.updater.data.PluginsRepository;

import org.xml.sax.InputSource;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Handler for parse and downloading repository.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class RepositoryUtils
{

    /** Repository URL. */
    public static String REPOSITORY_URL =
        "http://freeguide-tv.sourceforge.net/repositoryInfo.xml";

    static
    {

        // for debugging
        if( System.getProperty( "repositoryUrl" ) != null )
        {
            REPOSITORY_URL = System.getProperty( "repositoryUrl" );
        }
    }

    /**
     * Download repository description.
     *
     * @return repository description object
     *
     * @throws Exception
     */
    public static PluginsRepository downloadRepositoryInfo(  )
        throws Exception
    {

        HttpBrowser browser = new HttpBrowser(  );
        InputSource src;

        try
        {
            Application.getInstance(  ).getLogger(  ).fine( 
                "Loading repository info from " + REPOSITORY_URL + ".gz" );
            browser.loadURL( REPOSITORY_URL + ".gz" );
            src = new InputSource( 
                    new GZIPInputStream( 
                        new ByteArrayInputStream( browser.getBinaryData(  ) ) ) );
        }
        catch( IOException ex )
        {
            Application.getInstance(  ).getLogger(  ).fine( 
                "Loading repository info from " + REPOSITORY_URL );
            browser.loadURL( REPOSITORY_URL );
            src = new InputSource( 
                    new ByteArrayInputStream( browser.getBinaryData(  ) ) );
        }

        return parse( src, FreeGuide.runtimeInfo.installDirectory );
    }

    /**
     * Download all files from list from repository.
     *
     * @param baseUrl DOCUMENT ME!
     * @param files files list
     * @param toDirectory destination directory for save files
     *
     * @throws IOException
     */
    public static void downloadFiles( 
        final String baseUrl, final String[] files, final File toDirectory )
        throws IOException
    {

        HttpBrowser browser = new HttpBrowser(  );

        for( int i = 0; i < files.length; i++ )
        {
            browser.loadURL( baseUrl + files[i] );

            File dstFile = new File( toDirectory, files[i] );
            dstFile.getParentFile(  ).mkdirs(  );

            FileOutputStream fout = new FileOutputStream( dstFile );
            fout.write( browser.getBinaryData(  ) );
            fout.flush(  );
            fout.close(  );

            Application.getInstance(  ).getLogger(  ).fine( 
                "Load package '" + files[i] + "' to "
                + toDirectory.getPath(  ) );
        }
    }

    /**
     * Parse repository description XML file.
     *
     * @param input
     * @param baseDirectory DOCUMENT ME!
     *
     * @return repository description object
     *
     * @throws Exception
     */
    public static PluginsRepository parse( 
        final InputSource input, final String baseDirectory )
        throws Exception
    {

        SAXParserFactory factory = SAXParserFactory.newInstance(  );
        SAXParser saxParser = factory.newSAXParser(  );

        DescriptionParser handler =
            new DescriptionParser( new PluginsRepository( baseDirectory ) );
        saxParser.parse( input, handler );

        return handler.repository;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param fromDirectory DOCUMENT_ME!
     * @param baseDirectory DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static void unzipPackages( 
        final File fromDirectory, final File baseDirectory )
        throws IOException
    {

        File[] files =
            fromDirectory.listFiles( 
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

                ZipFile zip = new ZipFile( files[i] );
                Enumeration zipEntries = zip.entries(  );

                while( zipEntries.hasMoreElements(  ) )
                {

                    ZipEntry entry = (ZipEntry)zipEntries.nextElement(  );
                    File outFile =
                        new File( baseDirectory, entry.getName(  ) );

                    if( entry.isDirectory(  ) )
                    {
                        outFile.mkdirs(  );
                    }
                    else
                    {
                        unzipFile( zip.getInputStream( entry ), outFile );
                    }
                }

                files[i].delete(  );
            }
        }
    }

    protected static void unzipFile( final InputStream from, final File to )
        throws IOException
    {

        byte[] buffer = new byte[65536];
        int len;
        BufferedOutputStream out =
            new BufferedOutputStream( new FileOutputStream( to ) );

        while( ( len = from.read( buffer ) ) > 0 )
        {
            out.write( buffer, 0, len );
        }

        from.close(  );
        out.flush(  );
        out.close(  );
    }
}

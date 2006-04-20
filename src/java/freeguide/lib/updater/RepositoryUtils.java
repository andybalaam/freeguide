package freeguide.plugins.program.freeguide.lib.updater;

import freeguide.FreeGuide;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.lib.grabber.HttpBrowser;

import freeguide.common.lib.updater.data.PluginsRepository;

import org.xml.sax.InputSource;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.zip.GZIPInputStream;

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

            try
            {
                fout.write( browser.getBinaryData(  ) );
                fout.flush(  );
            }
            finally
            {
                fout.close(  );
            }

            Application.getInstance(  ).getLogger(  ).finer( 
                "Download package '" + files[i] + "' to "
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
     * @param baseDirectory DOCUMENT_ME!
     * @param files DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static void checkForDelete( 
        final File baseDirectory, final String[] files )
        throws IOException
    {

        for( int i = 0; i < files.length; i++ )
        {

            final File file = new File( baseDirectory, files[i] );

            if( file.exists(  ) && !file.canWrite(  ) )
            {
                throw new IOException( file.getPath(  ) );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param dest DOCUMENT_ME!
     * @param names DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static void prepareForDelete( 
        final File dest, final String[] names ) throws IOException
    {

        BufferedWriter out =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( dest, true ), "UTF-8" ) );

        try
        {

            for( int i = 0; i < names.length; i++ )
            {
                out.write( names[i] );
                out.write( '\n' );
            }

            out.flush(  );
        }
        finally
        {
            out.close(  );
        }
    }
}

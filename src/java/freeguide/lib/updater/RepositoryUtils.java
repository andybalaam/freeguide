package freeguide.lib.updater;

import freeguide.FreeGuide;

import freeguide.lib.grabber.HttpBrowser;

import freeguide.lib.updater.data.PluginPackage;
import freeguide.lib.updater.data.PluginsRepository;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;
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
    public static final String REPOSITORY_URL =
        "file:////E:/Workspace/freeguide-tv/src/repositoryInfo.xml";

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
            browser.loadURL( REPOSITORY_URL + ".gz" );
            src = new InputSource( 
                    new GZIPInputStream( 
                        new ByteArrayInputStream( browser.getBinaryData(  ) ) ) );
        }
        catch( IOException ex )
        {
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
        final String baseUrl, final List files, final File toDirectory )
        throws IOException
    {

        HttpBrowser browser = new HttpBrowser(  );

        for( int i = 0; i < files.size(  ); i++ )
        {

            PluginPackage.PackageFile file =
                (PluginPackage.PackageFile)files.get( i );
            browser.loadURL( baseUrl + file.getRepositoryPath(  ) );

            File dstFile = new File( toDirectory, file.getLocalPath(  ) );
            dstFile.getParentFile(  ).mkdirs(  );

            FileOutputStream fout = new FileOutputStream( dstFile );
            fout.write( browser.getBinaryData(  ) );
            fout.flush(  );
            fout.close(  );
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
}

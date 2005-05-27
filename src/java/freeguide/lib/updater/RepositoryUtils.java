package freeguide.lib.updater;

import freeguide.FreeGuide;

import freeguide.lib.grabber.HttpBrowser;

import freeguide.lib.updater.data.PluginPackage;
import freeguide.lib.updater.data.PluginsRepository;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.List;

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
        "file:////E:/freeguide-plugins.xml";

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
        browser.loadURL( REPOSITORY_URL );

        return parse( 
            new InputSource( 
                new ByteArrayInputStream( browser.getBinaryData(  ) ) ),
            FreeGuide.runtimeInfo.installDirectory );
    }

    /**
     * Download all files from list from repository.
     *
     * @param files files list
     * @param toDirectory destination directory for save files
     *
     * @throws IOException
     */
    public static void downloadFiles( 
        final List files, final File toDirectory ) throws IOException
    {

        HttpBrowser browser = new HttpBrowser(  );
        int pos = REPOSITORY_URL.lastIndexOf( '/' );

        if( pos == -1 )
        {
            throw new IOException( "Invalid repository URL" );
        }

        final String baseUrl = REPOSITORY_URL.substring( 0, pos + 1 );

        for( int i = 0; i < files.size(  ); i++ )
        {

            PluginPackage.PackageFile file =
                (PluginPackage.PackageFile)files.get( i );
            browser.loadURL( baseUrl + file.getPath(  ) );

            File dstFile = new File( toDirectory, file.getPath(  ) );
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

        Handler handler =
            new Handler( new PluginsRepository( baseDirectory ) );
        saxParser.parse( input, handler );

        return handler.repository;
    }

    /**
     * Class for parse repository description XML.
     */
    public static class Handler extends DefaultHandler
    {

        protected final PluginsRepository repository;
        protected PluginPackage currentPackage;
        protected String lang;
        protected String text;

        /**
         * Creates a new Handler object.
         *
         * @param repository
         */
        public Handler( final PluginsRepository repository )
        {
            this.repository = repository;
        }

        /**
         * startElement handler
         */
        public void startElement( 
            String uri, String localName, String qName, Attributes attributes )
            throws SAXException
        {

            if( "package".equals( qName ) )
            {
                currentPackage =
                    new PluginPackage( 
                        attributes.getValue( "id" ), repository );
                currentPackage.setType( attributes.getValue( "type" ) );
                currentPackage.setVersion( attributes.getValue( "version" ) );
            }
            else if( "name".equals( qName ) && ( currentPackage != null ) )
            {
                lang = attributes.getValue( "lang" );

                if( lang == null )
                {
                    lang = "en";
                }
            }
            else if( 
                "description".equals( qName ) && ( currentPackage != null ) )
            {
                lang = attributes.getValue( "lang" );

                if( lang == null )
                {
                    lang = "en";
                }
            }
            else if( "file".equals( qName ) && ( currentPackage != null ) )
            {
                currentPackage.addFile( 
                    attributes.getValue( "path" ),
                    Long.decode( attributes.getValue( "size" ) ).longValue(  ),
                    attributes.getValue( "md5sum" ) );
            }
        }

        /**
         * endElement handler
         */
        public void endElement( String uri, String localName, String qName )
            throws SAXException
        {

            if( "package".equals( qName ) )
            {

                if( currentPackage != null )
                {
                    repository.addPackage( currentPackage );
                    currentPackage = null;
                }
            }
            else if( "name".equals( qName ) && ( currentPackage != null ) )
            {
                currentPackage.setName( lang, text );
                lang = null;
            }
            else if( 
                "description".equals( qName ) && ( currentPackage != null ) )
            {
                currentPackage.setDescription( lang, text );
                lang = null;
            }
        }

        /**
         * characters handler
         */
        public void characters( char[] ch, int start, int length )
            throws SAXException
        {

            if( lang != null )
            {
                text = new String( ch, start, length );
            }
        }
    }
}

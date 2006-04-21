package freeguide.plugins.importexport.html;

import freeguide.common.gui.FileChooserExtension;

import freeguide.common.lib.fgspecific.data.TVData;

import freeguide.common.lib.general.TemplateParser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleExport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ExportHTML extends BaseModule implements IModuleExport
{

    protected final Config config = new Config(  );

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     * @param parent DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void exportData( TVData data, JFrame parent )
        throws Exception
    {

        JFileChooser chooser = new JFileChooser(  );

        if( config.path != null )
        {
            chooser.setSelectedFile( new File( config.path ) );
        }

        final FileFilter zipFilter =
            new FileFilter(  )
            {
                public String getDescription(  )
                {

                    return "Zipped HTML file (*.zip)";
                }

                public boolean accept( File f )
                {

                    return !f.isDirectory(  )
                    && f.getName(  ).endsWith( ".zip" );
                }
            };

        final FileFilter gzipFilter =
            new FileFilter(  )
            {
                public String getDescription(  )
                {

                    return "Gzipped HTML file (*.gz)";
                }

                public boolean accept( File f )
                {

                    return !f.isDirectory(  )
                    && f.getName(  ).endsWith( ".gz" );
                }
            };

        final FileFilter htmlFilter =
            new FileFilter(  )
            {
                public String getDescription(  )
                {

                    return "HTML file (*.html, *.htm)";
                }

                public boolean accept( File f )
                {

                    return !f.isDirectory(  )
                    && ( f.getName(  ).endsWith( ".htm" )
                    || f.getName(  ).endsWith( ".html" ) );
                }
            };

        chooser.addChoosableFileFilter( gzipFilter );
        chooser.addChoosableFileFilter( zipFilter );
        chooser.addChoosableFileFilter( htmlFilter );

        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( false );

        final FileChooserExtension ext = new FileChooserExtension(  );
        chooser.setAccessory( ext );

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {

            String path = chooser.getSelectedFile(  ).getPath(  );
            String lowerPath = path.toLowerCase(  );

            if( 
                ( chooser.getFileFilter(  ) == zipFilter )
                    && !lowerPath.endsWith( ".zip" ) )
            {
                path += ".zip";
            }

            if( 
                ( chooser.getFileFilter(  ) == gzipFilter )
                    && !lowerPath.endsWith( ".gz" ) )
            {
                path += ".gz";
            }

            if( 
                ( chooser.getFileFilter(  ) == htmlFilter )
                    && !( lowerPath.endsWith( ".html" )
                    || ( lowerPath.endsWith( ".htm" ) ) ) )
            {
                path += ".html";
            }

            config.path = path;
            lowerPath = path.toLowerCase(  );

            TemplateParser parser =
                new TemplateParser( 
                    ExportHTML.class.getPackage(  ).getName(  ).replace( 
                        '.', '/' ) + "/template.html" );
            final OutputStream outStream;

            if( lowerPath.endsWith( ".zip" ) )
            {

                ZipOutputStream zipOut =
                    new ZipOutputStream( new FileOutputStream( path ) );
                ZipEntry zipEntry = new ZipEntry( "tv.html" );
                zipOut.putNextEntry( zipEntry );
                outStream = zipOut;
            }
            else if( lowerPath.endsWith( ".gz" ) )
            {
                outStream =
                    new GZIPOutputStream( new FileOutputStream( path ) );
            }
            else
            {
                outStream = new FileOutputStream( path );
            }

            final Writer out =
                new BufferedWriter( 
                    new OutputStreamWriter( outStream, "UTF-8" ) );

            try
            {
                parser.process( new TemplateHandler( ext ), out );
            }
            finally
            {
                out.flush(  );
                out.close(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {

        return config;
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Config
    {

        /** Path of last saved file. */
        public String path;
    }
}

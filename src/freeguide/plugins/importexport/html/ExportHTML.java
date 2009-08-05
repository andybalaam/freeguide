package freeguide.plugins.importexport.html;

import freeguide.common.gui.FileChooserExtension;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.general.TemplateParser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IApplication;
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
    protected static final String EXT_GZ = ".gz";
    protected static final String EXT_ZIP = ".zip";
    protected static final String EXT_HTML = ".html";
    protected static final String EXT_HTM = ".htm";
    protected static final String TEMPLATE =
        "resources/plugins/importexport/html/template.html";
    protected static final String CHARSET = "UTF-8";
    protected static final String ZIP_ENTRY = "tv.html";
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
                    return i18n.getString( "Format.zip" );
                }

                public boolean accept( File f )
                {
                    return !f.isDirectory(  )
                    && f.getName(  ).endsWith( EXT_ZIP );
                }
            };

        final FileFilter gzipFilter =
            new FileFilter(  )
            {
                public String getDescription(  )
                {
                    return i18n.getString( "Format.gz" );
                }

                public boolean accept( File f )
                {
                    return !f.isDirectory(  )
                    && f.getName(  ).endsWith( EXT_GZ );
                }
            };

        final FileFilter htmlFilter =
            new FileFilter(  )
            {
                public String getDescription(  )
                {
                    return i18n.getString( "Format.html" );
                }

                public boolean accept( File f )
                {
                    return !f.isDirectory(  )
                    && ( f.getName(  ).endsWith( EXT_HTML )
                    || f.getName(  ).endsWith( EXT_HTM ) );
                }
            };

        chooser.addChoosableFileFilter( gzipFilter );
        chooser.addChoosableFileFilter( zipFilter );
        chooser.addChoosableFileFilter( htmlFilter );

        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( false );

        IApplication app = Application.getInstance(  );
        final FileChooserExtension ext = new FileChooserExtension(
            app.getDataStorage(  ), app.getViewer(  ), app );
        chooser.setAccessory( ext );

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {
            String path = chooser.getSelectedFile(  ).getPath(  );
            String lowerPath = path.toLowerCase(  );

            if( 
                ( chooser.getFileFilter(  ) == zipFilter )
                    && !lowerPath.endsWith( EXT_ZIP ) )
            {
                path += EXT_ZIP;
            }

            if( 
                ( chooser.getFileFilter(  ) == gzipFilter )
                    && !lowerPath.endsWith( EXT_GZ ) )
            {
                path += EXT_GZ;
            }

            if( 
                ( chooser.getFileFilter(  ) == htmlFilter )
                    && !( lowerPath.endsWith( EXT_HTML )
                    || ( lowerPath.endsWith( EXT_HTM ) ) ) )
            {
                path += EXT_HTML;
            }

            config.path = path;
            lowerPath = path.toLowerCase(  );

            TemplateParser parser = new TemplateParser( TEMPLATE );
            final OutputStream outStream;

            if( lowerPath.endsWith( EXT_ZIP ) )
            {
                ZipOutputStream zipOut =
                    new ZipOutputStream( new FileOutputStream( path ) );
                ZipEntry zipEntry = new ZipEntry( ZIP_ENTRY );
                zipOut.putNextEntry( zipEntry );
                outStream = zipOut;
            }
            else if( lowerPath.endsWith( EXT_GZ ) )
            {
                outStream = new GZIPOutputStream( 
                        new FileOutputStream( path ) );
            }
            else
            {
                outStream = new FileOutputStream( path );
            }

            final Writer out =
                new BufferedWriter( 
                    new OutputStreamWriter( outStream, CHARSET ) );

            try
            {
                parser.process( new TemplateHandler( ext, i18n ), out );
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

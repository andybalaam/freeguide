package freeguide.plugins.importexport.xmltv;

import freeguide.common.gui.FileChooserExtension;

import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.general.StringHelper;
import freeguide.common.lib.importexport.XMLTVExport;
import freeguide.common.lib.importexport.XMLTVImport;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleExport;
import freeguide.common.plugininterfaces.IModuleImport;
import freeguide.common.plugininterfaces.IStoragePipe;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import javax.xml.parsers.ParserConfigurationException;

/**
 * XMLTV import/export plugin.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ImpExpXmltv extends BaseModule implements IModuleImport,
    IModuleExport
{
    protected static final String EXT_XMLTV = ".xmltv";

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {
        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parent DOCUMENT_ME!
     * @param storage DOCUMENT ME!
     */
    public void importDataUI( final JFrame parent, final IStoragePipe storage )
    {
        JFileChooser chooser = new JFileChooser(  );
        chooser.setFileFilter( 
            new FileFilter(  )
            {
                public String getDescription(  )
                {
                    return i18n.getString( "Format.xmltv" );
                }

                public boolean accept( File pathname )
                {
                    return pathname.isDirectory(  )
                    || pathname.getName(  ).endsWith( EXT_XMLTV );
                }
            } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( true );

        if( chooser.showOpenDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {
            File[] files = chooser.getSelectedFiles(  );

            try
            {
                XMLTVImport imp = new XMLTVImport(  );

                if( files != null )
                {
                    for( int i = 0; i < files.length; i++ )
                    {
                        try
                        {
                            imp.process( 
                                files[i], storage, null,
                                new XMLTVImport.Filter(  ),
                                StringHelper.EMPTY_STRING );
                        }
                        catch( IOException e )
                        {
                            e.printStackTrace(  );
                        }
                    }
                }
            }
            catch( SAXException e )
            {
                e.printStackTrace(  );
            }
            catch( ParserConfigurationException e )
            {
                e.printStackTrace(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param file DOCUMENT_ME!
     * @param storage DOCUMENT ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void importData( final File file, final IStoragePipe storage )
        throws Exception
    {
        new XMLTVImport(  ).process( 
            file, storage, null, new XMLTVImport.Filter(  ),
            StringHelper.EMPTY_STRING );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     * @param parent DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void exportData( TVData data, JFrame parent )
        throws IOException
    {
        JFileChooser chooser = new JFileChooser(  );
        chooser.setFileFilter( 
            new FileFilter(  )
            {
                public String getDescription(  )
                {
                    return i18n.getString( "Format.xmltv" );
                }

                public boolean accept( File pathname )
                {
                    return pathname.isDirectory(  )
                    || pathname.getName(  ).endsWith( EXT_XMLTV );
                }
            } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( false );

        chooser.setAccessory( new FileChooserExtension(  ) );

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {
            File destination = chooser.getSelectedFile(  );

            if( !destination.getPath(  ).endsWith( EXT_XMLTV ) )
            {
                destination = new File( destination.getPath(  ) + EXT_XMLTV );
            }

            destination.delete(  );

            new XMLTVExport(  ).export( destination, data );
        }
    }
}

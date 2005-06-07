package freeguide.plugins.impexp.xmltv;

import freeguide.lib.fgspecific.data.TVData;

import freeguide.lib.impexp.XMLTVExport;
import freeguide.lib.impexp.XMLTVImport;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleImport;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/**
 * XMLTV import/export plugin.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ImpExpXmltv extends BaseModule implements IModuleImport,
    IModuleExport
{

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return "IO-XMLTV";
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parent DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData importData( final JFrame parent ) throws Exception
    {

        JFileChooser chooser = new JFileChooser(  );
        chooser.setFileFilter( 
            new FileFilter(  )
            {
                public String getDescription(  )
                {

                    return "XMLTV .xml file";
                }

                public boolean accept( File pathname )
                {

                    return pathname.isDirectory(  )
                    || pathname.getName(  ).endsWith( ".xmltv" );
                }
            } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( true );

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {

            final TVData result = new TVData(  );
            File[] files = chooser.getSelectedFiles(  );
            XMLTVImport imp = new XMLTVImport(  );

            if( files != null )
            {

                for( int i = 0; i < files.length; i++ )
                {
                    imp.process( files[i], result, new XMLTVImport.Filter(  ) );
                }
            }

            return result;
        }

        return null;
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

                    return "XMLTV .xml file";
                }

                public boolean accept( File pathname )
                {

                    return pathname.isDirectory(  )
                    || pathname.getName(  ).endsWith( ".xmltv" );
                }
            } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( false );

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {

            File destination = chooser.getSelectedFile(  );

            if( !destination.getPath(  ).endsWith( ".xmltv" ) )
            {
                destination = new File( destination.getPath(  ) + ".xmltv" );
            }

            destination.delete(  );

            new XMLTVExport(  ).export( destination, data );
        }
    }
}

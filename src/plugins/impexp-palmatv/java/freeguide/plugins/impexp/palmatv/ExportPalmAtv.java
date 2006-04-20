package freeguide.plugins.importexport.palmatv;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.lib.general.EndianInputStream;
import freeguide.common.lib.general.EndianOutputByteArray;

import freeguide.common.plugins.BaseModule;
import freeguide.common.plugins.IModuleConfigurationUI;
import freeguide.common.plugins.IModuleExport;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * Export to Palm's ATV module.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ExportPalmAtv extends BaseModule implements IModuleExport
{

    /** Offset to Palm's time. */
    public static final long PALM_TIME_DELTA = 24107L * 24 * 60 * 60;
    protected static final String hPDBName = "all";

    /** Cretor tag for palm database. */
    protected static final String hCreatorID = "abTV";

    /** Type tag for palm database. */
    protected static final String hDatabaseType = "Data";
    protected EndianInputStream rd;
    protected Config config = new Config(  );

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
     * Export data method.
     *
     * @param data TV data
     * @param parent parent frame
     *
     * @throws IOException
     */
    public void exportData( final TVData data, final JFrame parent )
        throws IOException
    {

        JFileChooser chooser = new JFileChooser(  );

        if( config.path != null )
        {
            chooser.setSelectedFile( new File( config.path ) );
        }

        chooser.setFileFilter( 
            new FileFilter(  )
            {
                public String getDescription(  )
                {

                    return "Palm .pdb databases";
                }

                public boolean accept( File pathname )
                {

                    return pathname.isDirectory(  )
                    || pathname.getName(  ).endsWith( ".pdb" );
                }
            } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( false );

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {

            File destination = chooser.getSelectedFile(  );

            if( !destination.getPath(  ).endsWith( ".pdb" ) )
            {
                destination = new File( destination.getPath(  ) + ".pdb" );
            }

            config.path = destination.getPath(  );

            saveConfigNow(  );

            destination.delete(  );

            StoreIterator iterator = new StoreIterator( hPDBName );
            data.iterate( iterator );
            iterator.sync(  );

            if( iterator.ex != null )
            {
                throw iterator.ex;
            }

            iterator.pdb.writeFile( destination );
        }
    }

    /**
     * Export data from command line.
     *
     * @param data TV data
     * @param pdbName site name
     * @param outFile DOCUMENT ME!
     *
     * @throws IOException
     */
    public void exportBatch( 
        final TVData data, final String pdbName, final File outFile )
        throws IOException
    {

        StoreIterator iterator = new StoreIterator( pdbName );
        data.iterate( iterator );
        iterator.sync(  );

        if( iterator.ex != null )
        {
            throw iterator.ex;
        }

        iterator.pdb.writeFile( outFile );
    }

    protected long readTime(  ) throws IOException
    {

        long tm = ( rd.readInt(  ) - PALM_TIME_DELTA ) * 1000;

        return tm /*-TimeEngine.getOffset(tm,TimeEngine.localTZ)*/;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {

        return new PalmUIController( this );
    }

    protected class StoreIterator extends TVIteratorProgrammes
    {

        final List programmes = new ArrayList(  );
        protected IOException ex;
        final PDBFile pdb;
        protected EndianOutputByteArray wr;

        /**
         * Creates a new StoreIterator object.
         *
         * @param pdbName DOCUMENT ME!
         *
         * @throws IOException DOCUMENT ME!
         */
        public StoreIterator( final String pdbName ) throws IOException
        {
            pdb = new PDBFile( pdbName, hCreatorID, hDatabaseType );
            wr = new EndianOutputByteArray( false, config.charset );
        }

        protected void onChannel( TVChannel channel )
        {

            if( getCurrentChannel(  ) != null )
            {
                sync(  );
            }
        }

        protected void onProgramme( TVProgramme programme )
        {
            programmes.add( programme );
        }

        /**
         * DOCUMENT_ME!
         */
        public void sync(  )
        {

            if( ex != null )
            {

                return;
            }

            int mprev = 0;

            while( mprev < programmes.size(  ) )
            {

                try
                {

                    String channelID = getCurrentChannel(  ).getID(  );
                    int pos = channelID.lastIndexOf( '/' );

                    if( pos != -1 )
                    {
                        channelID = channelID.substring( 0, pos );
                    }

                    int m =
                        checkMaxRecSize( 
                            getCurrentChannel(  ).getDisplayName(  ),
                            programmes, channelID, mprev );
                    wr.reset(  );
                    saveChannelProgTo( 
                        getCurrentChannel(  ).getDisplayName(  ), programmes,
                        channelID, mprev, m );
                    pdb.addRecord( wr.getBytes(  ) );
                    mprev = m;
                }
                catch( IOException ex )
                {
                    this.ex = ex;

                    return;
                }
            }

            programmes.clear(  );
        }

        protected int checkMaxRecSize( 
            String channelName, List programmes, String sitename, int beginfrom )
            throws IOException
        {

            int sz = 0;

            sz += ( wr.calcSPasString0( 
                channelName + "(" + sitename + ")", config.charset ) + 1 );

            sz += 4;

            for( int i = beginfrom; i < programmes.size(  ); i++ )
            {

                TVProgramme pr = (TVProgramme)programmes.get( i );
                sz += 4;
                sz += 2;
                sz += ( wr.calcSPasString( pr.getTitle(  ), config.charset )
                + 1 );
                sz += ( wr.calcSPasString( 
                    pr.getDescription(  ), config.charset ) + 1 );

                if( sz > PDBFile.MAX_RECORD_SIZE )
                {

                    if( i == beginfrom )
                    {

                        return i + 1;
                    }
                    else
                    {

                        return i;
                    }
                }
            }

            return programmes.size(  );
        }

        protected void saveChannelProgTo( 
            String channelName, List programmes, String sitename, int from,
            int to ) throws IOException
        {
            wr.writeSPasString0( 
                channelName + "(" + sitename + ")", config.charset );
            wr.alignToShort(  );

            wr.writeInt( to - from );

            for( int i = from; i < to; i++ )
            {

                TVProgramme pr = (TVProgramme)programmes.get( i );
                writeTime( pr.getStart(  ) );
                wr.writeShort( 
                    (short)( ( pr.getEnd(  ) - pr.getStart(  ) ) / 1000 ) );

                wr.writeSPasString( pr.getTitle(  ) );
                wr.alignToShort(  );

                wr.writeSPasString( pr.getDescription(  ) );
                wr.alignToShort(  );
            }
        }

        protected void writeTime( long dt ) throws IOException
        {
            wr.writeInt( 
                (int)( ( ( dt ) /*+TimeEngine.getOffset(dt,TimeEngine.localTZ)*/ / 1000 )
                + PALM_TIME_DELTA ) );

        }
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

        /** Charset for output. */
        public String charset = "cp1251";
    }
}

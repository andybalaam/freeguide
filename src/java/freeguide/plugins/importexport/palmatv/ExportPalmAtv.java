package freeguide.plugins.importexport.palmatv;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleExport;

import java.io.ByteArrayOutputStream;
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

    /** DOCUMENT ME! */
    public static final String hCreatorID = "abTV";

    /** DOCUMENT ME! */
    public static final String hDatabaseType = "Data";
    protected ADataInputStream rd;
    protected String charset;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return "palm-atv";

    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     * @param parent DOCUMENT ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void export( final TVData data, final JFrame parent )
        throws IOException
    {

        JFileChooser chooser = new JFileChooser(  );
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

            destination.delete(  );

            StoreIterator iterator = new StoreIterator( "sitename", hPDBName );
            data.iterate( iterator );
            iterator.sync(  );

            if( iterator.ex != null )
            {
                throw iterator.ex;
            }

            iterator.pdb.writeFile( destination );
        }
    }

    protected long readTime(  ) throws IOException
    {

        long tm = ( rd.readInt(  ) - PALM_TIME_DELTA ) * 1000;

        return tm /*-TimeEngine.getOffset(tm,TimeEngine.localTZ)*/;

    }

    protected class StoreIterator extends TVIteratorProgrammes
    {

        final List programmes = new ArrayList(  );
        final String siteName;
        protected IOException ex;
        final ByteArrayOutputStream ba;
        final PDBFile pdb;
        protected ADataOutputStream wr;

        /**
         * Creates a new StoreIterator object.
         *
         * @param siteName DOCUMENT ME!
         * @param pdbName DOCUMENT ME!
         *
         * @throws IOException DOCUMENT ME!
         */
        public StoreIterator( final String siteName, final String pdbName )
            throws IOException
        {
            this.siteName = siteName;
            pdb = new PDBFile( pdbName, hCreatorID, hDatabaseType );
            ba = new ByteArrayOutputStream(  );
            wr = new ADataOutputStream( ba, charset );
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

                    int m =
                        checkMaxRecSize( 
                            getCurrentChannel(  ).getDisplayName(  ),
                            programmes, siteName, mprev );
                    ba.reset(  );
                    saveChannelProgTo( 
                        getCurrentChannel(  ).getDisplayName(  ), programmes,
                        siteName, mprev, m );
                    pdb.addRecord( ba.toByteArray(  ) );
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
                channelName + "(" + sitename + ")", charset ) + 1 );

            sz += 4;

            for( int i = beginfrom; i < programmes.size(  ); i++ )
            {

                TVProgramme pr = (TVProgramme)programmes.get( i );
                sz += 4;
                sz += 2;
                sz += ( wr.calcSPasString( pr.getTitle(  ), charset ) + 1 );
                sz += ( wr.calcSPasString( pr.getDescription(  ), charset )
                + 1 );

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

            if( 
                ( wr.writeSPasString0( 
                        channelName + "(" + sitename + ")", charset ) % 2 ) == 1 )
            {
                wr.write( 0 );
            }

            wr.writeInt( to - from );

            for( int i = from; i < to; i++ )
            {

                TVProgramme pr = (TVProgramme)programmes.get( i );
                writeTime( pr.getStart(  ) );
                wr.writeShort( 
                    (short)( ( pr.getEnd(  ) - pr.getStart(  ) ) / 1000 / 60 ) );

                if( 
                    ( wr.writeSPasString( pr.getTitle(  ), charset ) % 2 ) == 1 )
                {
                    wr.write( 0 );
                }

                if( 
                    ( wr.writeSPasString( pr.getDescription(  ), charset ) % 2 ) == 1 )
                {
                    wr.write( 0 );
                }
            }
        }

        protected void writeTime( long dt ) throws IOException
        {
            wr.writeInt( 
                (int)( ( ( dt ) /*+TimeEngine.getOffset(dt,TimeEngine.localTZ)*/ / 1000 )
                + PALM_TIME_DELTA ) );

        }
    }
}

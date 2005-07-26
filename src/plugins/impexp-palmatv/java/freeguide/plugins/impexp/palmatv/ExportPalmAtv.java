package freeguide.plugins.impexp.palmatv;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleExport;

import org.alex73.utils.io.EndianInputStream;
import org.alex73.utils.io.EndianOutputByteArray;
import org.alex73.utils.palm.PDBFile;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

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
    protected EndianInputStream rd;
    protected String charset = "Cp1251";
    protected Config config = new Config(  );

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     * @param parent DOCUMENT ME!
     *
     * @throws IOException DOCUMENT_ME!
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
            saveConfig(  );

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
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     * @param site DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void exportBatch( final TVData data, final String site )
        throws IOException
    {

        StoreIterator iterator = new StoreIterator( site );
        data.iterate( iterator );
        iterator.sync(  );

        if( iterator.ex != null )
        {
            throw iterator.ex;
        }

        iterator.pdb.writeFile( new File( site + ".pdb" ) );
    }

    protected long readTime(  ) throws IOException
    {

        long tm = ( rd.readInt(  ) - PALM_TIME_DELTA ) * 1000;

        return tm /*-TimeEngine.getOffset(tm,TimeEngine.localTZ)*/;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefs DOCUMENT_ME!
     */
    public void setConfigStorage( Preferences prefs )
    {
        super.setConfigStorage( prefs );
        loadObjectFromPreferences( config );
    }

    /**
     * DOCUMENT_ME!
     */
    public void saveConfig(  )
    {
        saveObjectToPreferences( config );
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
            wr = new EndianOutputByteArray( false, charset );
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
                    int pos = channelID.indexOf( '/' );

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
            wr.writeSPasString0( channelName + "(" + sitename + ")", charset );
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
        public String charset = "Cp1251";
    }
}

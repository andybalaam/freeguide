package freeguide.plugins.importexport.jtv;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.lib.general.EndianInputStream;
import freeguide.common.lib.general.EndianOutputByteArray;

import freeguide.common.plugins.BaseModule;
import freeguide.common.plugins.IModuleExport;
import freeguide.common.plugins.IModuleImport;
import freeguide.common.plugins.IStoragePipe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class JTV extends BaseModule implements IModuleImport, IModuleExport
{

    protected static final byte[] SIGNATURE =
        new String( "JTV 3.x TV Program Data\n\n\n" ).getBytes(  );
    protected static final long DATE_DELTA = 134774L * 24L * 60L * 60L * 1000L; // milliseconds from Jan 1 1601 to Jan 1 1970
    protected static final String CHARSET = "Cp1251";

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
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void importDataUI( JFrame parent, final IStoragePipe storage )
        throws Exception
    {

        JFileChooser chooser = new JFileChooser(  );
        chooser.setFileFilter( 
            new FileFilter(  )
            {
                public String getDescription(  )
                {

                    return "JTV files";
                }

                public boolean accept( File pathname )
                {

                    return pathname.isDirectory(  )
                    || pathname.getName(  ).endsWith( ".ndx" )
                    || pathname.getName(  ).endsWith( ".pdt" );
                }
            } );
        chooser.setFileSelectionMode( JFileChooser.FILES_ONLY );
        chooser.setMultiSelectionEnabled( true );

        if( chooser.showOpenDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {

            File[] files = chooser.getSelectedFiles(  );
            Set fUniq = new TreeSet(  );

            for( int i = 0; i < files.length; i++ )
            {

                String path = files[i].getPath(  );

                if( path.endsWith( ".ndx" ) || path.endsWith( ".pdt" ) )
                {
                    path = path.substring( 0, path.length(  ) - 4 );
                    fUniq.add( path );
                }
            }

            Iterator it = fUniq.iterator(  );

            while( it.hasNext(  ) )
            {

                String fileName = (String)it.next(  );
                loadFromFile( fileName, storage );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param path DOCUMENT_ME!
     * @param storage DOCUMENT ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void importData( File path, final IStoragePipe storage )
        throws Exception
    {
        loadFromFile( path.getPath(  ), storage );
    }

    protected void loadFromFile( 
        final String fileName, final IStoragePipe storage )
        throws Exception
    {

        final EndianInputStream inndx =
            new EndianInputStream( new File( fileName + ".ndx" ), CHARSET );
        final EndianInputStream inpdt =
            new EndianInputStream( new File( fileName + ".pdt" ), CHARSET );
        final byte[] sig = new byte[SIGNATURE.length];
        inpdt.read( sig );

        if( !Arrays.equals( SIGNATURE, sig ) )
        {
            throw new IOException( 
                "Error JTV file format in '" + fileName + ".pdt'" );
        }

        final int posf = fileName.lastIndexOf( File.separatorChar );
        final String channelName =
            ( ( posf == -1 ) ? fileName : fileName.substring( posf + 1 ) );
        final String channelID = "jtv/" + channelName;
        storage.addChannel( new TVChannel( channelID, channelName ) );

        short progCount = inndx.readShort(  );

        for( int i = 0; i < progCount; i++ )
        {

            final TVProgramme prog = new TVProgramme(  );
            inndx.readShort(  );
            prog.setStart( readTime( inndx ) );

            final int pos = inndx.readUnsignedShort(  );
            inpdt.setCurrentPos( pos );
            prog.setTitle( inpdt.readSPasString(  ) );
            storage.addProgramme( channelID, prog );
        }

        storage.finishBlock(  );
    }

    protected long readTime( final EndianInputStream in )
    {

        Calendar c = Calendar.getInstance(  );
        c.setTimeZone( Application.getInstance(  ).getTimeZone(  ) );

        // Time stored in a 64-bit value representing the number of 100-nanosecond intervals since January 1, 1601 (UTC).
        long v = in.readLong(  );

        long dt = ( v / 10000 ) - DATE_DELTA;

        return dt - c.get( Calendar.ZONE_OFFSET )
        - c.get( Calendar.DST_OFFSET );
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

        ExportIterator ex = new ExportIterator(  );
        data.iterate( ex );

        if( ex.ex != null )
        {
            throw ex.ex;
        }
    }

    protected static class ExportIterator extends TVIteratorProgrammes
    {

        EndianOutputByteArray wrndx;
        EndianOutputByteArray wrpdt;
        IOException ex;
        Calendar c = Calendar.getInstance(  );

        /**
         * Creates a new ExportIterator object.
         */
        public ExportIterator(  )
        {
            c.setTimeZone( Application.getInstance(  ).getTimeZone(  ) );
        }

        protected void onChannel( TVChannel channel )
        {
            wrndx = new EndianOutputByteArray(  );
            wrpdt = new EndianOutputByteArray(  );

            wrpdt.write( SIGNATURE );
            wrndx.writeShort( channel.getProgrammesCount(  ) );
        }

        protected void onProgramme( TVProgramme programme )
        {
            c.setTime( new Date( programme.getStart(  ) ) );

            long saveTime =
                programme.getStart(  ) + c.get( Calendar.ZONE_OFFSET )
                + c.get( Calendar.DST_OFFSET );

            wrndx.writeShort( 0 );
            writeTime( saveTime );
            wrndx.writeShort( wrpdt.getCurrentPos(  ) );

            try
            {
                wrpdt.writeSPasString( programme.getTitle(  ) );
            }
            catch( IOException ex )
            {
                this.ex = ex;
            }
        }

        protected void onChannelFinish(  )
        {
            save( 
                wrndx.getBytes(  ),
                getCurrentChannel(  ).getID(  ).replace( '/', '_' ) + ".ndx" );
            save( 
                wrpdt.getBytes(  ),
                getCurrentChannel(  ).getID(  ).replace( '/', '_' ) + ".pdt" );
        }

        protected void save( byte[] data, String fileName )
        {

            try
            {

                FileOutputStream fndx = new FileOutputStream( fileName );
                fndx.write( data );
                fndx.flush(  );
                fndx.close(  );
            }
            catch( IOException ex )
            {
                this.ex = ex;
            }
        }

        protected void writeTime( long dt )
        {
            wrndx.writeLong( ( dt - DATE_DELTA ) * 10000 );
        }
    }
}

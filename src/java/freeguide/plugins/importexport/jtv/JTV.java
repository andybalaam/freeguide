package freeguide.plugins.importexport.jtv;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleImport;

import org.alex73.utils.io.EndianInputStream;
import org.alex73.utils.io.EndianOutputByteArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
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

    /** Plugin ID. */
    public static final String ID = "impexp-jtv";
    protected static final byte[] SIGNATURE =
        new String( "JTV 3.x TV Program Data\n\n\n" ).getBytes(  );
    protected static final long DATE_DELTA = 134774;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return ID;
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
    public TVData importData( JFrame parent ) throws Exception
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

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
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

            final TVData result = new TVData(  );
            Iterator it = fUniq.iterator(  );

            while( it.hasNext(  ) )
            {

                String fileName = (String)it.next(  );
                loadFromFile( fileName, result );
            }

            return result;
        }

        return null;
    }

    protected void loadFromFile( final String fileName, final TVData data )
        throws IOException
    {

        final EndianInputStream inndx =
            new EndianInputStream( new File( fileName + ".ndx" ) );
        final EndianInputStream inpdt =
            new EndianInputStream( new File( fileName + ".pdt" ) );
        final byte[] sig = new byte[SIGNATURE.length];
        inpdt.read( sig );

        if( !Arrays.equals( SIGNATURE, sig ) )
        {
            throw new IOException( 
                "Error JTV file format in '" + fileName + ".pdt'" );
        }

        final int posf = fileName.lastIndexOf( '/' );
        final TVChannel channel =
            data.get( 
                "jtv/"
                + ( ( posf == -1 ) ? fileName : fileName.substring( posf + 1 ) ) );
        short progCount = inndx.readShort(  );

        for( int i = 0; i < progCount; i++ )
        {

            final TVProgramme prog = new TVProgramme(  );
            inndx.readShort(  );
            prog.setStart( readTime( inndx ) );

            final long dt = inndx.readLong(  );
            final int pos = inndx.readUnsignedShort(  );
            inpdt.setCurrentPos( pos );
            prog.setTitle( inpdt.readSPasString(  ) );
            channel.put( prog );
        }
    }

    protected long readTime( final EndianInputStream in )
    {

        Calendar c = Calendar.getInstance(  );
        c.setTimeZone( TimeZone.getTimeZone( FreeGuide.config.timeZoneName ) );

        long v = in.readLong(  );

        long dt = ( v / 10000 ) - ( DATE_DELTA * 24L * 60 * 60 * 1000 );

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
    public void export( TVData data, JFrame parent ) throws IOException
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
            c.setTimeZone( 
                TimeZone.getTimeZone( FreeGuide.config.timeZoneName ) );
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
            wrndx.writeLong( 
                ( dt + ( DATE_DELTA * 24L * 60 * 60 * 1000 ) ) * 10000 );
        }
    }
}

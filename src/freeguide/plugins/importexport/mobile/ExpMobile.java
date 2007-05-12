package freeguide.plugins.importexport.mobile;

import freeguide.common.gui.FileChooserExtension;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorChannels;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.Time;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleExport;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 * Export to TVGuide Mobile format.
 *
 * @author Alex Buloichik
 */
public class ExpMobile extends BaseModule implements IModuleExport
{
    protected static Time DAY_BEGIN = new Time( 5 );
    protected static final long MSEC_PER_DAY = 24L * 60L * 60L * 1000L;
    protected static final String FILE_LIST = "list";

    /** Pattern for data files. */
    protected static final Pattern DATA_FILE_RE =
        Pattern.compile( "\\d{4}-\\d{2}-\\d{2}" );
    protected static final String DATEFORMAT_MASK = "yyyy-MM-dd";
    protected final int version;

/**
     * Creates a new ExpMobile object.
     */
    public ExpMobile(  )
    {
        version = 2;
    }

/**
     * Creates a new ExpMobile object.
     *
     * @param version DOCUMENT ME!
     */
    public ExpMobile( final int version )
    {
        this.version = version;
    }

    /**
     * Returns export config if need.
     *
     * @return config object
     */
    public Object getConfig(  )
    {
        return null;
    }

    /**
     * Ask directory and export data to it.
     *
     * @param data data for export
     * @param parent parent frame
     *
     * @throws IOException
     */
    public void exportData( final TVData data, final JFrame parent )
        throws IOException
    {
        JFileChooser chooser = new JFileChooser(  );

        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        chooser.setMultiSelectionEnabled( false );

        chooser.setAccessory( new FileChooserExtension(  ) );

        if( chooser.showSaveDialog( parent ) == JFileChooser.APPROVE_OPTION )
        {
            final File destination = chooser.getSelectedFile(  );

            clearDir( destination );
            exportToDir( 
                data, destination, Application.getInstance(  ).getTimeZone(  ) );
        }
    }

    /**
     * Clear dir before export for clear old data.
     *
     * @param dir output directory for clean
     */
    protected void clearDir( final File dir )
    {
        final File[] files = dir.listFiles(  );

        if( files != null )
        {
            for( int i = 0; i < files.length; i++ )
            {
                if( !files[i].isDirectory(  ) )
                {
                    if( 
                        FILE_LIST.equals( files[i].getName(  ) )
                            || DATA_FILE_RE.matcher( files[i].getName(  ) )
                                               .matches(  ) )
                    {
                        files[i].delete(  );
                    }
                }
            }
        }
    }

    /**
     * Export data to files.
     *
     * @param data data for export
     * @param dir output directory
     * @param tz DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void exportToDir( 
        final TVData data, final File dir, final TimeZone tz )
        throws IOException
    {
        final DivideIterator itdivide =
            new DivideIterator( 
                tz, System.currentTimeMillis(  ) - ( MSEC_PER_DAY * 2 ) );
        data.iterate( itdivide );

        for( final Map.Entry<String, TVData> entry : itdivide.filesData
            .entrySet(  ) )
        {
            final String fileName = entry.getKey(  );
            final TVData dayData = (TVData)entry.getValue(  );
            exportOneDay( new File( dir, fileName ), dayData, tz );
        }

        final DataOutputStream dout =
            new DataOutputStream( 
                new BufferedOutputStream( 
                    new FileOutputStream( new File( dir, FILE_LIST ) ) ) );

        try
        {
            dout.writeShort( version );

            final String[] channelIDs = data.getChannelIDs(  );
            dout.writeShort( channelIDs.length );

            for( int i = 0; i < channelIDs.length; i++ )
            {
                dout.writeUTF( channelIDs[i] );

                final TVChannel ch = data.get( channelIDs[i] );

                if( ( ch != null ) && ( ch.getDisplayName(  ) != null ) )
                {
                    dout.writeUTF( ch.getDisplayName(  ) );
                }
                else
                {
                    dout.writeUTF( channelIDs[i] );
                }
            }

            dout.flush(  );
            dout.close(  );
        }
        finally
        {
            dout.close(  );
        }
    }

    /**
     * Export one day data.
     *
     * @param file output file
     * @param dayData day data
     * @param tz DOCUMENT ME!
     *
     * @throws IOException
     */
    protected void exportOneDay( 
        final File file, final TVData dayData, final TimeZone tz )
        throws IOException
    {
        final OutIterator itDay = new OutIterator( tz, version );
        // pack data
        dayData.iterate( itDay );

        final Map<String, Integer> dataOffsets =
            new TreeMap<String, Integer>(  );
        final int headerLength =
            exportHeader( itDay.channelsData, dataOffsets, 0 ).length;

        byte[] header =
            exportHeader( itDay.channelsData, dataOffsets, headerLength );

        // save to file
        final FileOutputStream out = new FileOutputStream( file );

        try
        {
            out.write( header );

            for( final byte[] channelData : itDay.channelsData.values(  ) )
            {
                out.write( channelData );
            }

            out.flush(  );
        }
        finally
        {
            out.close(  );
        }
    }

    /**
     * Export header for one day file.
     *
     * @param channelsData
     * @param offsets
     * @param headerOffset
     *
     * @return
     *
     * @throws IOException
     */
    protected byte[] exportHeader( 
        final Map<String, byte[]> channelsData,
        final Map<String, Integer> offsets, final int headerOffset )
        throws IOException
    {
        final ByteArrayOutputStream array = new ByteArrayOutputStream(  );
        final DataOutputStream dout = new DataOutputStream( array );
        dout.writeShort( channelsData.size(  ) );

        int currentOffset = 0;

        for( final Map.Entry<String, byte[]> entry : channelsData.entrySet(  ) )
        {
            final String channelID = entry.getKey(  );
            final byte[] channelData = entry.getValue(  );
            dout.writeUTF( channelID );

            final Integer offset = offsets.get( channelID );

            if( offset != null )
            {
                dout.writeInt( headerOffset + offset.intValue(  ) );
            }
            else
            {
                dout.writeInt( 0 );
                offsets.put( channelID, new Integer( currentOffset ) );
            }

            dout.writeInt( channelData.length );
            currentOffset += channelData.length;
        }

        dout.flush(  );
        dout.close(  );

        return array.toByteArray(  );
    }

    /**
     * Iterator for divide data by days.
     */
    protected static class DivideIterator extends TVIteratorProgrammes
    {
        protected final SimpleDateFormat dateFormat;
        protected final Calendar calendar;
        protected final long minimumDate;

        /**
         * Map for store TVData by day. Key is day, value is
         * TVData for this day.
         */
        protected Map<String, TVData> filesData =
            new TreeMap<String, TVData>(  );

/**
         * Creates a new DivideIterator object.
         */
        public DivideIterator( final TimeZone tz, final long minimumDate )
        {
            this.calendar = Calendar.getInstance( tz );
            this.dateFormat = new SimpleDateFormat( DATEFORMAT_MASK );
            this.dateFormat.setTimeZone( tz );
            this.minimumDate = minimumDate;
        }

        protected void onChannel( TVChannel channel )
        {
        }

        protected void onProgramme( TVProgramme programme )
        {
            final String fileName = getFileName( programme.getStart(  ) );

            if( fileName == null )
            {
                // too old programme for export
                return;
            }

            TVData data = (TVData)filesData.get( fileName );

            if( data == null )
            {
                data = new TVData(  );
                filesData.put( fileName, data );
            }

            TVChannel ch = data.get( programme.getChannel(  ).getID(  ) );

            ch.put( programme );
        }

        protected String getFileName( long date )
        {
            calendar.setTimeInMillis( date );

            final Time pTime = new Time( calendar );

            if( pTime.compareTo( DAY_BEGIN ) < 0 )
            {
                calendar.add( Calendar.DAY_OF_YEAR, -1 );
            }

            final String result = dateFormat.format( calendar.getTime(  ) );

            calendar.set( Calendar.HOUR_OF_DAY, 0 );
            calendar.set( Calendar.MINUTE, 0 );
            calendar.set( Calendar.SECOND, 0 );
            calendar.set( Calendar.MILLISECOND, 0 );

            return ( calendar.getTimeInMillis(  ) >= minimumDate ) ? result
                                                                   : null;
        }
    }

    /**
     * Iterator for save each channel data in own block.
     */
    protected static class OutIterator extends TVIteratorChannels
    {
        protected static final int BUFFER_SIZE = 65536;

        /** Key is channel ID, value is gzipped data. */
        final Map<String, byte[]> channelsData =
            new TreeMap<String, byte[]>(  );
        protected final TimeZone tz;
        protected final int version;
        protected IOException ex;

/**
         * Creates a new OutIterator object.
         *
         * @param tz DOCUMENT ME!
         * @param version DOCUMENT ME!
         */
        public OutIterator( final TimeZone tz, final int version )
        {
            this.tz = tz;
            this.version = version;
        }

        protected void onChannel( final TVChannel channel )
        {
            final Set<TVProgramme> progs = channel.getProgrammes(  );
            final byte[] data =
                saveChannel( progs.toArray( new TVProgramme[progs.size(  )] ) );

            if( data != null )
            {
                channelsData.put( channel.getID(  ), data );
            }
        }

        protected byte[] saveChannel( final TVProgramme[] programmes )
        {
            if( programmes.length == 0 )
            {
                return null;
            }

            final List<String> strings = new ArrayList<String>(  );
            final int[] progNames = new int[programmes.length];
            final int[] progDescs = new int[programmes.length];

            for( int i = 0; i < programmes.length; i++ )
            {
                progNames[i] = putToList( strings, programmes[i].getTitle(  ) );
                progDescs[i] = putToList( 
                        strings, programmes[i].getDescription(  ) );
            }

            final ByteArrayOutputStream array = new ByteArrayOutputStream(  );

            try
            {
                // final DataOutputStream dout = new DataOutputStream(new
                // GZIPOutputStream(array, BUFFER_SIZE));
                final DataOutputStream dout = new DataOutputStream( array );
                dout.writeShort( programmes.length );
                dout.writeShort( strings.size(  ) );

                for( int i = 0; i < programmes.length; i++ )
                {
                    dout.writeInt( 
                        (int)( programmes[i].getStart(  ) / 1000 / 60 ) );

                    switch( version )
                    {
                    case 1:
                        break;

                    case 2:
                        dout.writeShort( 
                            tz.getOffset( programmes[i].getStart(  ) ) / 1000 / 60 );

                        break;

                    default:
                        throw new IOException( "Unknown version" );
                    }

                    dout.writeShort( 
                        (int)( ( programmes[i].getEnd(  )
                        - programmes[i].getStart(  ) ) / 1000 / 60 ) );
                    dout.writeShort( progNames[i] );
                    dout.writeShort( progDescs[i] );
                }

                for( int i = 0; i < strings.size(  ); i++ )
                {
                    final String str = strings.get( i );
                    dout.writeUTF( str );
                }

                dout.flush(  );
                dout.close(  );
            }
            catch( IOException ex )
            {
                this.ex = ex;
            }

            return ( ex == null ) ? gzipArray( array.toByteArray(  ) ) : null;
        }

        protected int putToList( final List<String> list, final String str )
        {
            if( ( str == null ) || ( str.length(  ) == 0 ) )
            {
                return -1;
            }

            int result = list.indexOf( str );

            if( result < 0 )
            {
                result = list.size(  );
                list.add( str );
            }

            return result;
        }

        protected byte[] gzipArray( final byte[] data )
        {
            final ByteArrayOutputStream array = new ByteArrayOutputStream(  );

            try
            {
                final DataOutputStream dout =
                    new DataOutputStream( 
                        new GZIPOutputStream( array, BUFFER_SIZE ) );
                dout.write( data );
                dout.flush(  );
                dout.close(  );
            }
            catch( IOException ex )
            {
            }

            return array.toByteArray(  );
        }
    }
}

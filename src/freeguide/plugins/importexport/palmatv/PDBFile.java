/*
 *  PDBFile - the abstract class to interface with Palm PDB files
 *  Copyright (C) 2000  James Thrasher (jjt@gjt.org)
 *  Copyright (C) 2005  Alex Buloichik (alex73@zaval.org)
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package freeguide.plugins.importexport.palmatv;

import freeguide.common.lib.general.EndianInputStream;
import freeguide.common.lib.general.EndianOutputByteArray;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PDBFile
{
    protected static final int HEADER_SIZE = 78;
    protected static final int MAX_RECORD_SIZE = 65000;
    protected static short dmHdrAttrStream = 0x0080;
    protected static final int STREAM_BLOCK_SIZE = 4096;
    protected static final byte[] STREAM_BLOCK_MARK =
        new byte[] { 'D', 'B', 'L', 'K' };
    protected String PDBName;
    protected byte[] filler =
        {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0
        };
    protected short fileAttributes = 8; //24;
    protected short version = 1;
    protected int creationDate; // to be set by constructor
    protected int modificationDate; // to be set by constructor
    protected int lastBackupDate; // to be set by constructor
    protected int modificationNumber = 0;
    protected int appInfoArea = 0;
    protected int sortInfoArea = 0;
    protected byte[] databaseType;
    protected byte[] creatorID;
    protected int uniqueIDSeed = 0; // = 0 until they find out something to do with it.
    protected int nextRecord = 0;
    protected short numRecs = 0;

    // internal data
    protected Calendar cl;
    protected List records;

    //protected Addable AppInfoBlock = null;
    //protected Addable SortInfoBlock = null;
    protected int[][] recordList;

/**
     * Creates a new PDBFile object.
     *
     * @param pdbName DOCUMENT ME!
     * @param creatorId DOCUMENT ME!
     * @param databaseType DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public PDBFile( String pdbName, String creatorId, String databaseType )
        throws IOException
    {
        // find the current time, and convert into number of seconds since Jan 1, 1904
        cl = Calendar.getInstance(  );

        Date d = new Date(  ); // Get current time.
        cl.set( 2036, 0, 1 ); // Jan 1, 2036  1970-2036 = 1904 - 1970
        creationDate = (int)( ( d.getTime(  ) + cl.getTime(  ).getTime(  ) ) / 1000 ); // secs since 1970 + secs between 1904 to 1970 (1970 to 2036)
        modificationDate = creationDate;
        lastBackupDate = modificationDate;

        creationDate = 1;
        modificationDate = 1;
        lastBackupDate = 1;

        // initialize stuff
        records = new ArrayList(  );

        this.PDBName = pdbName;
        this.creatorID = ( creatorId + "    " ).substring( 0, 4 ).getBytes(  );
        this.databaseType = ( databaseType + "    " ).substring( 0, 4 )
                              .getBytes(  );
    }

/**
     * Creates a new PDBFile object.
     *
     * @param fileName DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public PDBFile( String fileName ) throws IOException
    {
        readFile( fileName );
    }

    //protected Addable recordType = null;
    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int getRecordCount(  )
    {
        return records.size(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param arr DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void addRecord( byte[] arr ) throws IOException
    {
        if( arr.length > MAX_RECORD_SIZE )
        {
            throw new IOException( 
                "Too long record for pdb file : " + arr.length + " bytes" );
        }

        records.add( arr );
    }

    /**
     * writes everything to the file.
     *
     * @param file DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void writeFile( File file ) throws IOException
    {
        EndianOutputByteArray out = new EndianOutputByteArray( false );
        writeHeader( out );
        writeRecordList( out );

        //writeAppInfoBlock(out);
        writeFiller( out );

        FileOutputStream fout = new FileOutputStream( file );
        fout.write( out.getBytes(  ) );
        writeRecords( fout );
        fout.flush(  );
        fout.close(  );
    }

    /**
     * writes a PDB header.
     *
     * @param out DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writeHeader( EndianOutputByteArray out )
        throws IOException
    {
        // write all the data straight to the file
        out.writeString0( PDBName, 32 );
        out.writeShort( fileAttributes );
        out.writeShort( version );
        out.writeInt( creationDate );
        out.writeInt( modificationDate );
        out.writeInt( lastBackupDate );
        out.writeInt( modificationNumber );
        out.writeInt( appInfoArea );
        out.writeInt( sortInfoArea );
        out.write( databaseType );
        out.write( creatorID );
        out.writeInt( uniqueIDSeed );
        out.writeInt( nextRecord );
        out.writeShort( records.size(  ) );
    }

    /**
     * writes a record index list, based on the records.
     *
     * @param out DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writeRecordList( EndianOutputByteArray out )
        throws IOException
    {
        int runningTotal = HEADER_SIZE; // size of the header
        runningTotal += ( ( 8 * records.size(  ) ) + 2 ); // size of record list and filler

        /*    if (AppInfoBlock != null)
              runningTotal += AppInfoBlock.size();                // size of AppInfoBlock
            if (SortInfoBlock != null)
              runningTotal += SortInfoBlock.size();               // size of SortInfoBlock
              */
        for( int i = 0; i < records.size(  ); i++ )
        {
            out.writeInt( runningTotal ); // record offset (in bytes)

            byte[] obj = (byte[])records.get( i );
            out.writeByte( 0 );
            out.write( filler, 0, 3 );
            runningTotal += obj.length;
        }
    }

    /**
     * writes AppInfoBlock.
     *
     * @param out DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */

    /*  protected void writeAppInfoBlock(ADataOutputStream out) throws IOException {
        if (AppInfoBlock != null)
          out.write(AppInfoBlock.getBytes(), 0, AppInfoBlock.size());
      }*/
    /**
     * writes 2 blank bytes.  see <a
     * href="http://www.roadcoders.com/pdb.html">pdb reference</a>.
     *
     * @param out DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writeFiller( EndianOutputByteArray out )
        throws IOException
    {
        out.write( filler, 0, 2 );
    }

    /**
     * writes records.
     *
     * @param out DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writeRecords( FileOutputStream out )
        throws IOException
    {
        for( int i = 0; i < records.size(  ); i++ )
        {
            byte[] record = (byte[])records.get( i );

            if( record.length > 65505 )
            {
                throw new IOException( 
                    "Too long record(" + record.length + ") bytes" );
            }

            out.write( record );
        }
    }

    /**
     * reads the file into memory.
     *
     * @param fileName DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    private void readFile( String fileName ) throws IOException
    {
        EndianInputStream in = new EndianInputStream( new File( fileName ) );
        in.setBigEndian(  );
        readHeader( in );
        readRecordList( in );
        readAppInfoBlock( in );
        readRecords( in );
    }

    /**
     * reads the header from the current file.
     *
     * @param in DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void readHeader( EndianInputStream in ) throws IOException
    {
        byte[] buffer = new byte[32];
        in.read( buffer );

        int i;

        for( i = 0; ( i < 32 ) && ( buffer[i] != (byte)0 ); i++ )
        {
            ;
        }

        PDBName = new String( buffer, 0, i );
        fileAttributes = in.readShort(  );
        version = in.readShort(  );
        creationDate = in.readInt(  );
        modificationDate = in.readInt(  );
        lastBackupDate = in.readInt(  );
        modificationNumber = in.readInt(  );
        appInfoArea = in.readInt(  );
        sortInfoArea = in.readInt(  );
        databaseType = new byte[4];
        creatorID = new byte[4];
        in.read( databaseType );
        in.read( creatorID );
        uniqueIDSeed = in.readInt(  );
        nextRecord = in.readInt(  );
        numRecs = in.readShort(  );
    }

    /**
     * reads the list of records into memory.
     *
     * @param in DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void readRecordList( EndianInputStream in )
        throws IOException
    {
        recordList = new int[numRecs][];

        int itemp;
        int btemp;

        for( int i = 0; i < numRecs; i++ )
        {
            itemp = in.readInt(  );
            btemp = in.readByte(  );
            in.skip( 3 );
            recordList[i] = new int[] { itemp, btemp };
        }
    }

    /**
     * reads the AppInfoBlock into memory.
     *
     * @param in DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void readAppInfoBlock( EndianInputStream in )
        throws IOException
    {
        if( appInfoArea != 0 )
        {
            in.skip( sortInfoArea - appInfoArea );
        }
    }

    protected void readRecords( EndianInputStream in )
        throws IOException
    {
        byte[] buffer;

        if( numRecs == 0 )
        {
            return;
        }

        int i;

        for( i = 0; i < ( numRecs - 1 ); i++ )
        {
            buffer = new byte[recordList[i + 1][0] - recordList[i][0]];
            in.read( buffer );
            records.add( buffer );
        }

        /*      buffer = new byte[(int) length() - recordList[i][0]];
              seek(recordList[i][0]);
              read(buffer);
              rec = (Addable) recordType.getClass().newInstance();
              rec.setBytes(buffer);
              rec.setAttributes((byte) recordList[i][1]);
              records.addElement(rec);*/
    }

    /**
     * DOCUMENT_ME!
     *
     * @param fileName DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void dumpToH( String fileName ) throws IOException
    {
        PrintStream out =
            new PrintStream( 
                new BufferedOutputStream( new FileOutputStream( fileName ) ) );

        for( int i = 0; i < records.size(  ); i++ )
        {
            out.println( 
                "static signed char record" + i
                + "[] __attribute__((aligned(2))) = {" );

            byte[] rec = (byte[])records.get( i );

            for( int j = 0; j < rec.length; j++ )
            {
                if( j > 0 )
                {
                    out.print( ", " );
                }

                out.print( rec[j] );
            }

            out.println( "};" );
        }

        out.println( "unsigned char * DBRECORDS[]={" );

        for( int i = 0; i < records.size(  ); i++ )
        {
            if( i > 0 )
            {
                out.print( ", " );
            }

            out.print( "(unsigned char *)record" + i );
        }

        out.println( "};" );
        out.println( "#define DBRECORDCOUNT " + records.size(  ) );
        out.flush(  );
        out.close(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     * @param pdbName DOCUMENT_ME!
     * @param creatorId DOCUMENT_ME!
     * @param databaseType DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static PDBFile createStreamPDB( 
        final byte[] data, String pdbName, String creatorId,
        String databaseType ) throws IOException
    {
        PDBFile pdb = new PDBFile( pdbName, creatorId, databaseType );
        pdb.fileAttributes |= PDBFile.dmHdrAttrStream;

        EndianOutputByteArray buffer = new EndianOutputByteArray( false );

        for( int i = 0; i < data.length; i += STREAM_BLOCK_SIZE )
        {
            int len = STREAM_BLOCK_SIZE;

            if( ( i + len ) > data.length )
            {
                len = data.length - i;
            }

            buffer.write( STREAM_BLOCK_MARK );
            buffer.writeInt( len );
            buffer.write( data, i, len );
            pdb.addRecord( buffer.getBytes(  ) );
            buffer.reset(  );
        }

        return pdb;
    }
}

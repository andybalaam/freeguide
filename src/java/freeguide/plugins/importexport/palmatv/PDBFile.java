/*

 *  PDBFile - the abstract class to interface with Palm PDB files

 *  Copyright (C) 2000  James Thrasher

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

 *

 *  email: jjt@gjt.org

 */
package freeguide.plugins.importexport.palmatv;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

    // static variables

    /** DOCUMENT ME! */
    public static final int HEADER_SIZE = 78;

    /** DOCUMENT ME! */
    public static final int MAX_RECORD_SIZE = 65000;

    // fields of header
    protected String PDBName;
    protected byte[] filler =
    {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        
        0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    protected short fileAttributes = 8; //24;

    /** DOCUMENT ME! */
    public short version = 1;
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

        creationDate =
            (int)( ( d.getTime(  ) + cl.getTime(  ).getTime(  ) ) / 1000 ); // secs since 1970 + secs between 1904 to 1970 (1970 to 2036)

        modificationDate = creationDate;

        lastBackupDate = modificationDate;

        creationDate = 1;

        modificationDate = 1;

        lastBackupDate = 1;

        // initialize stuff
        records = new ArrayList(  );

        this.PDBName = pdbName;

        this.creatorID = ( creatorId + "    " ).substring( 0, 4 ).getBytes(  );

        this.databaseType =
            ( databaseType + "    " ).substring( 0, 4 ).getBytes(  );

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

        //    if (arr.length > MAX_RECORD_SIZE)
        //      throw new IOException("Too long record for pdb file : "+arr.length+" bytes");
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

        ADataOutputStream out = new ADataOutputStream( file );

        out.setBigEndian(  );

        writeHeader( out );

        writeRecordList( out );

        //writeAppInfoBlock(out);
        writeFiller( out );

        writeRecords( out );

        out.trunc(  );

        out.close(  );

    }

    /**
     * writes a PDB header.
     *
     * @param out DOCUMENT ME!
     */
    protected void writeHeader( ADataOutputStream out )
    {

        // write all the data straight to the file
        try
        {
            out.write( PDBName.getBytes(  ) );

            out.write( filler, 0, 32 - PDBName.getBytes(  ).length );

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

        catch( IOException e )
        {
            System.err.println( "ERROR - In writeHeader:" );

            e.printStackTrace(  );

        }
    }

    /**
     * writes a record index list, based on the records.
     *
     * @param out DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected void writeRecordList( ADataOutputStream out )
        throws IOException
    {

        //  Addable obj;
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
    protected void writeFiller( ADataOutputStream out )
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
    protected void writeRecords( ADataOutputStream out )
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
     * sets the record format up so that we can read things in.
     *
     * @param fileName The Addable that tells us how to handle data.
     *
     * @throws IOException DOCUMENT ME!
     */

    /*  public void attachRec(Addable rec) {


    recordType = rec;


    }*/

    /**
     * reads the file into memory.
     *
     * @param fileName DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    private void readFile( String fileName ) throws IOException
    {

        ADataInputStream in =
            new ADataInputStream( 
                new BufferedInputStream( new FileInputStream( fileName ) ) );

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
    protected void readHeader( ADataInputStream in ) throws IOException
    {

        byte[] buffer = new byte[256];

        in.readFully( buffer, 0, 32 );

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

        in.readFully( databaseType, 0, 4 );

        in.readFully( creatorID, 0, 4 );

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
    protected void readRecordList( ADataInputStream in )
        throws IOException
    {

        byte[] buffer;

        recordList = new int[numRecs][];

        int itemp;

        int btemp;

        for( int i = 0; i < numRecs; i++ )
        {
            buffer = new byte[8];

            itemp = in.readInt(  );

            btemp = in.readByte(  );

            in.readFully( buffer, 0, 3 );

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
    protected void readAppInfoBlock( ADataInputStream in )
        throws IOException
    {

        if( appInfoArea != 0 )
        {

            byte[] buffer = new byte[256];

            in.readFully( buffer, 0, sortInfoArea - appInfoArea );

        }
    }

    protected void readRecords( ADataInputStream in ) throws IOException
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

            in.readFully( buffer );

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
}

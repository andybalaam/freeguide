package freeguide.plugins.importexport.palmatv;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ADataOutputStream implements DataOutput
{

    DataOutput out;
    boolean littleEndian;
    byte[] buf;
    String charsetName;

    /**
     * Creates a new ADataOutputStream object.
     *
     * @param fileName DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public ADataOutputStream( String fileName ) throws FileNotFoundException
    {
        this( fileName, null );

    }

    /**
     * Creates a new ADataOutputStream object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public ADataOutputStream( File file ) throws FileNotFoundException
    {
        this( file, null );

    }

    /**
     * Creates a new ADataOutputStream object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public ADataOutputStream( OutputStream file ) throws FileNotFoundException
    {
        this( file, null );

    }

    /**
     * Creates a new ADataOutputStream object.
     *
     * @param fileName DOCUMENT ME!
     * @param charsetName DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public ADataOutputStream( String fileName, String charsetName )
        throws FileNotFoundException
    {
        out = new RandomAccessFile( fileName, "rw" );

        littleEndian = true;

        buf = new byte[8];

        this.charsetName = charsetName;

    }

    /**
     * Creates a new ADataOutputStream object.
     *
     * @param file DOCUMENT ME!
     * @param charsetName DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public ADataOutputStream( File file, String charsetName )
        throws FileNotFoundException
    {
        out = new RandomAccessFile( file, "rw" );

        littleEndian = true;

        buf = new byte[8];

        this.charsetName = charsetName;

    }

    /**
     * Creates a new ADataOutputStream object.
     *
     * @param file DOCUMENT ME!
     * @param charsetName DOCUMENT ME!
     *
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public ADataOutputStream( OutputStream file, String charsetName )
        throws FileNotFoundException
    {
        out = new DataOutputStream( file );

        littleEndian = true;

        buf = new byte[8];

        this.charsetName = charsetName;

    }

    /**
     * DOCUMENT_ME!
     */
    public void setLittleEndian(  )
    {
        littleEndian = true;

    }

    /**
     * DOCUMENT_ME!
     */
    public void setBigEndian(  )
    {
        littleEndian = false;

    }

    /*  public void write(int v) throws IOException {


    out.write(v);


    }




    public void writeByte(int v) throws IOException {


    write(v);


    }*/
    public void writeShort( int v ) throws IOException
    {

        if( littleEndian )
        {
            buf[0] = (byte)( v );

            buf[1] = (byte)( v >> 8 );

        }

        else
        {
            buf[1] = (byte)( v );

            buf[0] = (byte)( v >> 8 );

        }

        out.write( buf, 0, 2 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeInt( int v ) throws IOException
    {

        if( littleEndian )
        {
            buf[0] = (byte)( v );

            buf[1] = (byte)( v >> 8 );

            buf[2] = (byte)( v >> 16 );

            buf[3] = (byte)( v >> 24 );

        }

        else
        {
            buf[3] = (byte)( v );

            buf[2] = (byte)( v >> 8 );

            buf[1] = (byte)( v >> 16 );

            buf[0] = (byte)( v >> 24 );

        }

        out.write( buf, 0, 4 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeLong( long v ) throws IOException
    {

        if( littleEndian )
        {
            buf[0] = (byte)( v );

            buf[1] = (byte)( v >> 8 );

            buf[2] = (byte)( v >> 16 );

            buf[3] = (byte)( v >> 24 );

            buf[4] = (byte)( v >> 32 );

            buf[5] = (byte)( v >> 40 );

            buf[6] = (byte)( v >> 48 );

            buf[7] = (byte)( v >> 56 );

        }

        else
        {
            buf[7] = (byte)( v );

            buf[6] = (byte)( v >> 8 );

            buf[5] = (byte)( v >> 16 );

            buf[4] = (byte)( v >> 24 );

            buf[3] = (byte)( v >> 32 );

            buf[2] = (byte)( v >> 40 );

            buf[1] = (byte)( v >> 48 );

            buf[0] = (byte)( v >> 56 );

        }

        out.write( buf, 0, 8 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeFloat( float v ) throws IOException
    {
        writeInt( Float.floatToIntBits( v ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeDouble( double v ) throws IOException
    {
        writeLong( Double.doubleToLongBits( v ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param s DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeChars( String s ) throws IOException
    {

        for( int i = 0; i < s.length(  ); i++ )
        {
            writeChar( s.charAt( i ) );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeChar( int v ) throws IOException
    {
        writeShort( v );

    }

    /*  public void writePasString(String str, String charset) throws IOException {


    if (str == null) {


      writeInt(0);


    } else {


      byte[] sb;


      if (charset == null)


        sb = str.getBytes();


      else


        sb = str.getBytes(charset);


      int len = sb.length;


      writeInt(len);


      write(sb);


    }


    }*/
    public int writeSPasString( String str, String charset )
        throws IOException
    {

        if( str == null )
        {
            writeShort( 0 );

            return 0;

        }

        else
        {

            byte[] sb;

            if( charset == null )
            {
                sb = str.getBytes(  );

            }

            else
            {
                sb = str.getBytes( charset );

            }

            int len = sb.length;

            if( len > 65535 )
            {
                len = 65535;

            }

            writeShort( len );

            write( sb, 0, len );

            return len;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     * @param charset DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int calcSPasString( String str, String charset )
        throws IOException
    {

        if( str == null )
        {

            return 2;

        }

        else
        {

            byte[] sb;

            if( charset == null )
            {
                sb = str.getBytes(  );

            }

            else
            {
                sb = str.getBytes( charset );

            }

            int len = sb.length;

            if( len > 65535 )
            {
                len = 65535;

            }

            return len + 2;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeString0( String str ) throws IOException
    {

        byte[] data;

        if( getCharsetName(  ) == null )
        {
            data = str.getBytes(  );

        }

        else
        {
            data = str.getBytes( getCharsetName(  ) );

        }

        write( data );

        writeByte( 0 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     * @param length DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeStringXor0( String str, int length )
        throws IOException
    {

        byte[] data;

        if( getCharsetName(  ) == null )
        {
            data = str.getBytes(  );

        }

        else
        {
            data = str.getBytes( getCharsetName(  ) );

        }

        if( ( data.length + 1 ) > length )
        {
            throw new IOException( "Length of string too long" );

        }

        write( data );

        writeByte( 0 );

        skipBytes( length - data.length - 1 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     * @param length DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeString0( String str, int length )
        throws IOException
    {

        byte[] data;

        if( getCharsetName(  ) == null )
        {
            data = str.getBytes(  );

        }

        else
        {
            data = str.getBytes( getCharsetName(  ) );

        }

        if( ( data.length + 1 ) > length )
        {
            throw new IOException( "Length of string too long" );

        }

        write( data );

        writeByte( 0 );

        skipBytes( length - data.length - 1 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     * @param charset DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int writeSPasString0( String str, String charset )
        throws IOException
    {

        if( str == null )
        {
            writeShort( 0 );

            write( 0 );

            return 1;

        }

        else
        {

            byte[] sb;

            if( charset == null )
            {
                sb = str.getBytes(  );

            }

            else
            {
                sb = str.getBytes( charset );

            }

            int len = sb.length;

            if( len > 65535 )
            {
                len = 65535;

            }

            writeShort( len );

            write( sb, 0, len );

            write( 0 );

            return len + 1;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     * @param charset DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int calcSPasString0( String str, String charset )
        throws IOException
    {

        if( str == null )
        {

            return 3;

        }

        else
        {

            byte[] sb;

            if( charset == null )
            {
                sb = str.getBytes(  );

            }

            else
            {
                sb = str.getBytes( charset );

            }

            int len = sb.length;

            if( len > 65535 )
            {
                len = 65535;

            }

            return len + 3;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void close(  ) throws IOException
    {

        if( out instanceof RandomAccessFile )
        {
            ( (RandomAccessFile)out ).close(  );

        }

        else
        {
            ( (DataOutputStream)out ).flush(  );

            ( (DataOutputStream)out ).close(  );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param pos DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void seek( long pos ) throws IOException
    {
        ( (RandomAccessFile)out ).seek( pos );

    }

    /**
     * DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void trunc(  ) throws IOException
    {
        ( (RandomAccessFile)out ).setLength( 
            ( (RandomAccessFile)out ).getFilePointer(  ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public long length(  ) throws IOException
    {

        return ( (RandomAccessFile)out ).length(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeUTF( String str ) throws IOException
    {
        out.writeUTF( str );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeBytes( String str ) throws IOException
    {
        out.writeBytes( str );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param b DOCUMENT_ME!
     * @param off DOCUMENT_ME!
     * @param len DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void write( byte[] b, int off, int len ) throws IOException
    {
        out.write( b, off, len );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param b DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void write( byte[] b ) throws IOException
    {
        out.write( b );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeByte( int v ) throws IOException
    {
        out.writeByte( v );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void writeBoolean( boolean v ) throws IOException
    {
        out.writeBoolean( v );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param b DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void write( int b ) throws IOException
    {
        out.write( b );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param len DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void skipBytes( int len ) throws IOException
    {

        for( int i = 0; i < len; i++ )
        {
            out.write( 0 );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getCharsetName(  )
    {

        return charsetName;

    }
}

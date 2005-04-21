package freeguide.plugins.importexport.palmatv;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ADataInputStream implements DataInput
{

    DataInputStream in;
    boolean littleEndian;
    byte[] buf;

    /**
     * Creates a new ADataInputStream object.
     *
     * @param s DOCUMENT ME!
     */
    public ADataInputStream( InputStream s )
    {
        in = new DataInputStream( s );

        littleEndian = true;

        buf = new byte[8];

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

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public short readShort(  ) throws IOException
    {
        readFully( buf, 0, 2 );

        if( littleEndian )
        {

            return (short)( ( ( buf[1] & 0xff ) << 8 ) | ( buf[0] & 0xff ) );

        }

        else
        {

            return (short)( ( ( buf[0] & 0xff ) << 8 ) | ( buf[1] & 0xff ) );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public char readChar(  ) throws IOException
    {

        return (char)in.readUnsignedShort(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int readUnsignedShort(  ) throws IOException
    {
        readFully( buf, 0, 2 );

        if( littleEndian )
        {

            return ( ( ( buf[1] & 0xff ) << 8 ) | ( buf[0] & 0xff ) );

        }

        else
        {

            return ( ( ( buf[0] & 0xff ) << 8 ) | ( buf[1] & 0xff ) );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int readInt(  ) throws IOException
    {
        readFully( buf, 0, 4 );

        if( littleEndian )
        {

            return ( buf[3] << 24 ) | ( ( buf[2] & 0xff ) << 16 )
            | ( ( buf[1] & 0xff ) << 8 ) | ( buf[0] & 0xff );

        }

        else
        {

            return ( buf[0] << 24 ) | ( ( buf[1] & 0xff ) << 16 )
            | ( ( buf[2] & 0xff ) << 8 ) | ( buf[3] & 0xff );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public long readUnsignedInt(  ) throws IOException
    {
        readFully( buf, 0, 4 );

        if( littleEndian )
        {

            return ( ( (long)buf[3] ) << 24 )
            | ( ( (long)buf[2] & 0xff ) << 16 )
            | ( ( (long)buf[1] & 0xff ) << 8 ) | ( (long)buf[0] & 0xff );

        }

        else
        {

            return ( ( (long)buf[0] ) << 24 )
            | ( ( (long)buf[1] & 0xff ) << 16 )
            | ( ( (long)buf[2] & 0xff ) << 8 ) | ( (long)buf[3] & 0xff );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public long readLong(  ) throws IOException
    {
        readFully( buf, 0, 8 );

        if( littleEndian )
        {

            return ( ( (long)buf[7] ) << 56 )
            | ( ( (long)buf[6] & 0xff ) << 48 )
            | ( ( (long)buf[5] & 0xff ) << 40 )
            | ( ( (long)buf[4] & 0xff ) << 32 )
            | ( ( (long)buf[3] & 0xff ) << 24 )
            | ( ( (long)buf[2] & 0xff ) << 16 )
            | ( ( (long)buf[1] & 0xff ) << 8 ) | ( (long)buf[0] & 0xff );

        }

        else
        {

            return ( ( (long)buf[0] ) << 56 )
            | ( ( (long)buf[1] & 0xff ) << 48 )
            | ( ( (long)buf[2] & 0xff ) << 40 )
            | ( ( (long)buf[3] & 0xff ) << 32 )
            | ( ( (long)buf[4] & 0xff ) << 24 )
            | ( ( (long)buf[5] & 0xff ) << 16 )
            | ( ( (long)buf[6] & 0xff ) << 8 ) | ( (long)buf[7] & 0xff );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public double readDouble(  ) throws IOException
    {

        return Double.longBitsToDouble( readLong(  ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public float readFloat(  ) throws IOException
    {

        return Float.intBitsToFloat( readInt(  ) );

    }

    /*public String readPasString(String charset) throws IOException {


    int len = readInt();


    if (len > 0) {


    byte[] d = new byte[len];


    read(d);


    if (charset == null)


    return new String(d);


    else


    return new String(d, charset);


    }


    return null;


    }*/
    /*public String readSPasString(String charset) throws IOException {


    int len = readUShort();


    if (len > 0) {


    byte[] d = new byte[len];


    read(d);


    if (charset == null)


    return new String(d);


    else


    return new String(d, charset);


    }


    return null;


    }*/
    /*public String readSPasString0(String charset) throws IOException {


    int len = readUShort();


    if (len > 0) {


    byte[] d = new byte[len];


    read(d);


    read();


    if (charset == null)


    return new String(d);


    else


    return new String(d, charset);


    }


    return null;


    }*/
    public void readFully( byte[] b ) throws IOException
    {
        in.readFully( b );

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
    public void readFully( byte[] b, int off, int len )
        throws IOException
    {
        in.readFully( b, off, len );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param n DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int skipBytes( int n ) throws IOException
    {

        return in.skipBytes( n );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public String readUTF(  ) throws IOException
    {

        return in.readUTF(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public byte readByte(  ) throws IOException
    {

        return in.readByte(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int readUnsignedByte(  ) throws IOException
    {

        return in.readUnsignedByte(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public boolean readBoolean(  ) throws IOException
    {

        return in.readBoolean(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public String readLine(  ) throws IOException
    {

        return null;

    }

    /**
     * DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void close(  ) throws IOException
    {
        in.close(  );

    }
}

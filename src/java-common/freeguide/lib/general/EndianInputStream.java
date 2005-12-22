package freeguide.lib.general;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EndianInputStream
{

    String charsetName;
    boolean littleEndian = true;
    byte[] data;
    int pos;

    /**
     * Creates a new EndianInputStream object.
     *
     * @param file DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public EndianInputStream( final File file ) throws IOException
    {
        this( file, null );
    }

    /**
     * Creates a new EndianInputStream object.
     *
     * @param file DOCUMENT ME!
     * @param charsetName DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public EndianInputStream( final File file, final String charsetName )
        throws IOException
    {
        this.charsetName = charsetName;

        FileInputStream fin = new FileInputStream( file );
        ByteArrayOutputStream out =
            new ByteArrayOutputStream( (int)file.length(  ) );
        byte[] buf = new byte[64 * 1024];

        while( true )
        {

            int len = fin.read( buf );

            if( len < 0 )
            {

                break;
            }

            out.write( buf, 0, len );
        }

        fin.close(  );
        data = out.toByteArray(  );
        pos = 0;
    }

    /**
     * Creates a new EndianInputStream object.
     *
     * @param data DOCUMENT ME!
     */
    public EndianInputStream( final byte[] data )
    {
        this.data = data;
        pos = 0;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public String readSPasString(  ) throws IOException
    {

        short len = readShort(  );

        if( len == 0 )
        {

            return new String( "" );
        }

        byte[] buf = new byte[len];
        read( buf );

        return ( charsetName != null ) ? new String( buf, charsetName )
                                       : new String( buf );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param num DOCUMENT_ME!
     */
    public void skip( int num )
    {
        pos += num;
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
     * @param pos DOCUMENT_ME!
     */
    public void setCurrentPos( int pos )
    {
        this.pos = pos;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param buffer DOCUMENT_ME!
     */
    public void read( byte[] buffer )
    {
        System.arraycopy( data, pos, buffer, 0, buffer.length );
        pos += data.length;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public short readShort(  )
    {

        short result;

        if( littleEndian )
        {
            result =
                (short)( ( ( data[pos + 1] & 0xff ) << 8 )
                | ( data[pos + 0] & 0xff ) );
        }
        else
        {
            result =
                (short)( ( ( data[pos + 0] & 0xff ) << 8 )
                | ( data[pos + 1] & 0xff ) );
        }

        pos += 2;

        return result;
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

        return (char)readUnsignedShort(  );
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

        int result;

        if( littleEndian )
        {
            result = ( ( ( data[pos + 1] & 0xff ) << 8 )
                | ( data[pos + 0] & 0xff ) );
        }
        else
        {
            result = ( ( ( data[pos + 0] & 0xff ) << 8 )
                | ( data[pos + 1] & 0xff ) );
        }

        pos += 2;

        return result;
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

        int result;

        if( littleEndian )
        {
            result =
                ( data[pos + 3] << 24 ) | ( ( data[pos + 2] & 0xff ) << 16 )
                | ( ( data[pos + 1] & 0xff ) << 8 ) | ( data[pos + 0] & 0xff );
        }
        else
        {
            result =
                ( data[pos + 0] << 24 ) | ( ( data[pos + 1] & 0xff ) << 16 )
                | ( ( data[pos + 2] & 0xff ) << 8 ) | ( data[pos + 3] & 0xff );
        }

        pos += 4;

        return result;
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

        long result;

        if( littleEndian )
        {
            result =
                ( ( (long)data[pos + 3] ) << 24 )
                | ( ( (long)data[pos + 2] & 0xff ) << 16 )
                | ( ( (long)data[pos + 1] & 0xff ) << 8 )
                | ( (long)data[pos + 0] & 0xff );
        }
        else
        {
            result =
                ( ( (long)data[pos + 0] ) << 24 )
                | ( ( (long)data[pos + 1] & 0xff ) << 16 )
                | ( ( (long)data[pos + 2] & 0xff ) << 8 )
                | ( (long)data[pos + 3] & 0xff );
        }

        pos += 4;

        return result;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public long readLong(  )
    {

        long result;

        if( littleEndian )
        {
            result =
                ( ( (long)data[pos + 7] ) << 56 )
                | ( ( (long)data[pos + 6] & 0xff ) << 48 )
                | ( ( (long)data[pos + 5] & 0xff ) << 40 )
                | ( ( (long)data[pos + 4] & 0xff ) << 32 )
                | ( ( (long)data[pos + 3] & 0xff ) << 24 )
                | ( ( (long)data[pos + 2] & 0xff ) << 16 )
                | ( ( (long)data[pos + 1] & 0xff ) << 8 )
                | ( (long)data[pos + 0] & 0xff );
        }
        else
        {
            result =
                ( ( (long)data[pos + 0] ) << 56 )
                | ( ( (long)data[pos + 1] & 0xff ) << 48 )
                | ( ( (long)data[pos + 2] & 0xff ) << 40 )
                | ( ( (long)data[pos + 3] & 0xff ) << 32 )
                | ( ( (long)data[pos + 4] & 0xff ) << 24 )
                | ( ( (long)data[pos + 5] & 0xff ) << 16 )
                | ( ( (long)data[pos + 6] & 0xff ) << 8 )
                | ( (long)data[pos + 7] & 0xff );
        }

        pos += 8;

        return result;
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

    /*
     * public String readPasString(String charset) throws IOException { int len =
     * readInt(); if (len > 0) { byte[] d = new byte[len]; read(d); if (charset ==
     * null) return new String(d); else return new String(d, charset); } return
     * null; }
     */
    /*
     * public String readSPasString(String charset) throws IOException { int len =
     * readUShort(); if (len > 0) { byte[] d = new byte[len]; read(d); if
     * (charset == null) return new String(d); else return new String(d,
     * charset); } return null; }
     */
    /*
     * public String readSPasString0(String charset) throws IOException { int
     * len = readUShort(); if (len > 0) { byte[] d = new byte[len]; read(d);
     * read(); if (charset == null) return new String(d); else return new
     * String(d, charset); } return null; }
     */
    /*
     * public int skipBytes(int n) throws IOException { return in.skipBytes(n); }
     *
     * public String readUTF() throws IOException { return in.readUTF(); }
     */
    public byte readByte(  ) throws IOException
    {

        return data[pos++];
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

        return data[pos++];
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

        return data[pos++] != 0;
    }
}

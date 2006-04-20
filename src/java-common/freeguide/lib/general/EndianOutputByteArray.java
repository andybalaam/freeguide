package freeguide.common.lib.general;

import java.io.IOException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class EndianOutputByteArray
{

    String charsetName;
    boolean littleEndian = true;
    byte[] data = new byte[256];
    int size;
    int pos;

    /**
     * Creates a new EndianOutputByteArray object.
     */
    public EndianOutputByteArray(  )
    {
    }

    /**
     * Creates a new EndianOutputByteArray object.
     *
     * @param littleEndian DOCUMENT ME!
     */
    public EndianOutputByteArray( final boolean littleEndian )
    {
        this.littleEndian = littleEndian;
    }

    /**
     * Creates a new EndianOutputByteArray object.
     *
     * @param littleEndian DOCUMENT ME!
     * @param charsetName DOCUMENT ME!
     */
    public EndianOutputByteArray( 
        final boolean littleEndian, String charsetName )
    {
        this.littleEndian = littleEndian;
        this.charsetName = charsetName;
    }

    protected void checkMemory( final int blockSize )
    {

        if( ( pos + blockSize ) > data.length )
        {

            byte[] newdata =
                new byte[Math.max( ( data.length * 12 ) / 10, pos + blockSize )];
            System.arraycopy( data, 0, newdata, 0, size );
            data = newdata;
        }
    }

    protected void fixSize(  )
    {

        if( pos > size )
        {
            size = pos;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public byte[] getBytes(  )
    {

        byte[] result = new byte[size];

        System.arraycopy( data, 0, result, 0, size );

        return result;
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

    /*
     * public void write(int v) throws IOException { out.write(v); }
     *
     * public void writeByte(int v) throws IOException { write(v); }
     */
    public void writeShort( int v )
    {
        checkMemory( 2 );

        if( littleEndian )
        {
            data[pos + 0] = (byte)( v );
            data[pos + 1] = (byte)( v >> 8 );
        }
        else
        {
            data[pos + 0] = (byte)( v >> 8 );
            data[pos + 1] = (byte)( v );
        }

        pos += 2;
        fixSize(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     */
    public void writeInt( int v )
    {
        checkMemory( 4 );

        if( littleEndian )
        {
            data[pos + 0] = (byte)( v );
            data[pos + 1] = (byte)( v >> 8 );
            data[pos + 2] = (byte)( v >> 16 );
            data[pos + 3] = (byte)( v >> 24 );
        }
        else
        {
            data[pos + 3] = (byte)( v );
            data[pos + 2] = (byte)( v >> 8 );
            data[pos + 1] = (byte)( v >> 16 );
            data[pos + 0] = (byte)( v >> 24 );
        }

        pos += 4;
        fixSize(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     */
    public void writeLong( long v )
    {
        checkMemory( 8 );

        if( littleEndian )
        {
            data[pos + 0] = (byte)( v );
            data[pos + 1] = (byte)( v >> 8 );
            data[pos + 2] = (byte)( v >> 16 );
            data[pos + 3] = (byte)( v >> 24 );
            data[pos + 4] = (byte)( v >> 32 );
            data[pos + 5] = (byte)( v >> 40 );
            data[pos + 6] = (byte)( v >> 48 );
            data[pos + 7] = (byte)( v >> 56 );
        }
        else
        {
            data[pos + 7] = (byte)( v );
            data[pos + 6] = (byte)( v >> 8 );
            data[pos + 5] = (byte)( v >> 16 );
            data[pos + 4] = (byte)( v >> 24 );
            data[pos + 3] = (byte)( v >> 32 );
            data[pos + 2] = (byte)( v >> 40 );
            data[pos + 1] = (byte)( v >> 48 );
            data[pos + 0] = (byte)( v >> 56 );
        }

        pos += 8;
        fixSize(  );
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

    /*
     * public void writePasString(String str, String charset) throws IOException {
     * if (str == null) { writeInt(0); } else { byte[] sb; if (charset == null)
     * sb = str.getBytes(); else sb = str.getBytes(charset); int len =
     * sb.length; writeInt(len); write(sb); } }
     */
    public void writeBPasString( String str ) throws IOException
    {

        if( str == null )
        {
            writeByte( 0 );
        }
        else
        {

            byte[] sb;

            if( charsetName == null )
            {
                sb = str.getBytes(  );
            }
            else
            {
                sb = str.getBytes( charsetName );
            }

            int len = sb.length;

            if( len > 255 )
            {
                len = 255;
            }

            writeByte( len );
            write( sb, 0, len );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param str DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int writeSPasString( String str ) throws IOException
    {

        if( str == null )
        {
            writeShort( 0 );

            return 0;
        }
        else
        {

            byte[] sb;

            if( charsetName == null )
            {
                sb = str.getBytes(  );
            }
            else
            {
                sb = str.getBytes( charsetName );
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
            writeByte( 0 );

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
            writeByte( 0 );

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
     * @param pos DOCUMENT_ME!
     */
    public void setCurrentPos( int pos )
    {
        this.pos = pos;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int getCurrentPos(  )
    {

        return pos;
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void trunc(  ) throws IOException
    {
        size = pos;
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void reset(  ) throws IOException
    {
        size = 0;
        pos = 0;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public int length(  ) throws IOException
    {

        return size;
    }

    /*
     * public void writeUTF(String str) throws IOException { out.writeUTF(str); }
     */
    /*
     * public void writeBytes(String str) throws IOException {
     * out.writeBytes(str); }
     */
    public void write( byte[] b, int off, int len )
    {
        checkMemory( len );
        System.arraycopy( b, off, data, pos, len );
        pos += len;
        fixSize(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param b DOCUMENT_ME!
     */
    public void write( byte[] b )
    {
        write( b, 0, b.length );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param v DOCUMENT_ME!
     */
    public void writeByte( int v )
    {
        checkMemory( 1 );
        data[pos] = (byte)v;
        pos++;
        fixSize(  );
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
        checkMemory( 1 );
        writeByte( v ? 1 : 0 );
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
            writeByte( 0 );
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

    /**
     * DOCUMENT_ME!
     */
    public void alignToShort(  )
    {

        if( ( pos % 2 ) == 1 )
        {
            writeByte( 0 );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param obj DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void serialize( final Object obj ) throws Exception
    {

        if( obj.getClass(  ).isArray(  ) )
        {

            for( int i = 0; i < Array.getLength( obj ); i++ )
            {
                serialize( Array.get( obj, i ) );
            }
        }
        else if( obj.getClass(  ) == Short.class )
        {
            writeShort( ( (Short)obj ).shortValue(  ) );
        }
        else if( obj.getClass(  ) == Integer.class )
        {
            writeInt( ( (Integer)obj ).intValue(  ) );
        }
        else if( obj.getClass(  ) == Long.class )
        {
            writeLong( ( (Long)obj ).longValue(  ) );
        }
        else
        {

            Field[] fields = obj.getClass(  ).getFields(  );

            for( int i = 0; i < fields.length; i++ )
            {

                if( 
                    !Modifier.isPublic( fields[i].getModifiers(  ) )
                        || Modifier.isTransient( fields[i].getModifiers(  ) )
                        || Modifier.isStatic( fields[i].getModifiers(  ) ) )
                {

                    continue;
                }

                if( fields[i].getType(  ) == String.class )
                {

                    int len =
                        obj.getClass(  )
                           .getField( fields[i].getName(  ) + "_LENGTH" )
                           .getInt( obj.getClass(  ) );

                    if( len > 0 )
                    {
                        writeString0( (String)fields[i].get( obj ), len );
                    }
                    else
                    {
                        writeBPasString( (String)fields[i].get( obj ) );
                    }
                }
                else
                {
                    serialize( fields[i].get( obj ) );
                }
            }
        }
    }
}

package freeguide.common.lib.general;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BadUTF8FilterInputStream extends FilterInputStream
{
    byte[] previousBytes = new byte[0];

    public BadUTF8FilterInputStream( InputStream arg0 )
    {
        super( arg0 );
    }

    public int read( byte[] bytes, int off, int len ) throws IOException
    {
        int ret = this.in.read( bytes, off, len );

        int bytes_left = 0;
        int bytes_read = 0;
        for( int i = off; i < ret; ++i )
        {
            byte bt = bytes[i];

            if( bytes_left == 0 )
            {
                if( ( (byte)bt & (byte)0xfc ) == (byte)0xfc )
                    // start of 6 byte sequence 1111110.
                {
                    bytes_left = 5;
                    bytes_read = 1;
                }
                else if( (byte)( bt & (byte)0xf8 ) == (byte)0xf8 )
                    // start of 5 byte sequence 111110..
                {
                    bytes_left = 4;
                    bytes_read = 1;
                }
                else if( (byte)( bt & (byte)0xf0 ) == (byte)0xf0 )
                    // start of 4 byte sequence 11110...
                {
                    bytes_left = 3;
                    bytes_read = 1;
                }
                else if( (byte)( bt & (byte)0xe0 ) == (byte)0xe0 )
                    // start of 3 byte sequence 1110....
                {
                    bytes_left = 2;
                    bytes_read = 1;
                }
                else if( bt == (byte)0xc0 ||
                         bt == (byte)0xc1 )
                        // invalid byte since c0 and c1 are not allowed
                {
                    bytes[i] = 63;
                }
                else if( (byte)( bt & (byte)0xc0 ) == (byte)0xc0 )
                    // start of 2 byte sequence 110.....
                {
                    bytes_left = 1;
                    bytes_read = 1;
                }
                else if( (byte)( bt & (byte)0x80 ) == (byte)0x80 )
                    // invalid byte (starts with 10)
                {
                    bytes[i] = 63;
                }
            }
            else
            {
                if( (byte)( bt & (byte)0x80 ) != (byte)0x80 ||
                    (byte)( bt & (byte)0x40 ) == (byte)0x40 )
                        // invalid byte (does not start with 10)
                {
                    int starti = i - bytes_read;
                    if( starti < off )
                    {
                        bytes[i] = 0;
                    }
                    else
                    {
                        for( int j = starti;
                             j < Math.min( i + bytes_left, off + len );
                             ++j )
                        {
                            bytes[j] = 63;
                        }
                        i += bytes_left - 1;
                        bytes_left = 1;
                    }
                }
                --bytes_left;
                ++bytes_read;
            }
        }

        return ret;
    }

}

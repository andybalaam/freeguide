package freeguide.common.lib.general;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import freeguide.common.plugininterfaces.ILogger;

public class BadUTF8FilterInputStream extends FilterInputStream
{
    private int bytes_left = 0;
    private int bytes_read = 0;
    private ILogger logger;

    private IsUTF8StreamChecker checker = new IsUTF8StreamChecker();

    public BadUTF8FilterInputStream( InputStream arg0, ILogger logger )
    {
        super( arg0 );
        this.logger = logger;
    }

    public int read() throws IOException
    {
        byte[] bytes = new byte[1];
        int ret = read( bytes, 0, 1 );
        if( ret == 0 )
        {
            return -1;
        }
        else
        {
            return bytes[0];
        }
    }

    public int read( byte[] bytes, int off, int len ) throws IOException
    {
        int ret = this.in.read( bytes, off, len );

        boolean isUTF8 = checker.checkUTF8( bytes, off, ret );

        if( isUTF8 )
        {
            doRead( bytes, off, ret );
        }

        return ret;
    }

    private void warnRemovingCharacter( byte byteRemoved )
    {
        logger.warning( "Removing bad utf-8 character: " +
            Integer.toHexString( byteRemoved & 0xff ) );
    }

    private void doRead( byte[] bytes, int off, int ret )
    {
        // If ret == -1 we won't enter the loop, as required
        for( int i = off; i < off + ret; ++i )
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
                    warnRemovingCharacter( bt );
                    bytes[i] = 63; // ? character
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
                    warnRemovingCharacter( bt );
                    bytes[i] = 63; // ? character
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
                        // The first byte of this bad sequence
                        // was before we entered this read call,
                        // so all we can do is set this byte to 0x80
                        // so the whole sequence will be valid UTF-8,
                        // but we may have wiped out characters we
                        // needed.  Nothing we can do about it.
                        warnRemovingCharacter( bt );
                        bytes[i] = (byte)0x80;  // A valid 2nd, 3rd etc. byte
                    }
                    else
                    {
                        i = starti;
                        warnRemovingCharacter( bytes[i] );
                        bytes[i] = 63; // ? character
                        bytes_left = 1;
                    }
                }
                --bytes_left;
                ++bytes_read;
            }
        }
    }

    public boolean TESTING_ONLY_isUTF8Stream()
    {
        return checker.isUTF8Stream();
    }
}

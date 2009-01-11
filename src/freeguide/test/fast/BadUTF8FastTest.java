package freeguide.test.fast;

import java.io.ByteArrayInputStream;

import freeguide.common.lib.general.BadUTF8FilterInputStream;

import freeguide.test.FreeGuideTest;

public class BadUTF8FastTest
{

    public BadUTF8FastTest()
    {
    }

    public void run()
    throws Exception
    {
        test_OneGoodChar();
        test_OneBadChar();
        test_BadStartOf2ByteSequence();
        test_Bad2Bytes();
        test_BadFiancC3C2A9e();
        test_Bad3BytesInStringLastBad();
        test_Bad3BytesInStringMiddleBad();
        test_BadStartOf2ByteBeforeLT();
    }

    private void test_OneGoodChar()
    throws Exception
    {
        byte[] inbytes = { 116 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == 116 );
            // The byte is left unchanged
    }

    private void test_OneBadChar()
    throws Exception
    {
        byte[] inbytes = { (byte)0x81 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == (byte)63 );
            // The byte is changed to '?'
    }

    private void test_BadStartOf2ByteSequence()
    throws Exception
    {
        byte[] inbytes = { (byte)0xc0, (byte)0xc1 };
        // c0 and c1 are invalid since they represent the start of a
        // 2-byte sequence where the code point is < 127

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == 63 );
        FreeGuideTest.my_assert( readbytes[1] == 63 );
            // Both bytes changed to '?'
    }

    private void test_Bad2Bytes()
    throws Exception
    {
        byte[] inbytes = { (byte)0xc2, (byte)0x24 };

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == 63 );
            // The bad first byte has become ?
        FreeGuideTest.my_assert( readbytes[1] == 0x24 );
            // The second byte is unchanged since once the bad
            // first byte is gone, this looks like good UTF-8
    }

    private void test_BadFiancC3C2A9e()
    throws Exception
    {
        byte[] inbytes = {
            (byte)0x66, // f
            (byte)0x69, // i
            (byte)0x61, // a
            (byte)0x6e, // n
            (byte)0x63, // c
            (byte)0xc3, (byte)0xc2, (byte)0xa9,
                // wrong e-acute - one bad char followed by what looks
                // like a good 2-byte sequence.
            (byte)0x65, // e
        };

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream );

        // Read in 2 parts just to check that works too
        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 2 );
        filterstream.read( readbytes, 2, inbytes.length - 2 );

        //for( int i = 0; i < readbytes.length; ++i )
        //{
        //    System.err.println( Integer.toHexString( readbytes[i] ) );
        //}

        FreeGuideTest.my_assert( readbytes[0] == 102 ); // f
        FreeGuideTest.my_assert( readbytes[1] == 105 ); // i
        FreeGuideTest.my_assert( readbytes[2] ==  97 ); // a
        FreeGuideTest.my_assert( readbytes[3] == 110 ); // n
        FreeGuideTest.my_assert( readbytes[4] ==  99 ); // c
        FreeGuideTest.my_assert( readbytes[5] ==  63 ); // ?
        FreeGuideTest.my_assert( ( readbytes[6] & 0xff ) == 0xc2 );
        FreeGuideTest.my_assert( ( readbytes[7] & 0xff ) == 0xa9 );
            // c2a9 actually looks like a valid 2-byte character
        FreeGuideTest.my_assert( readbytes[8] == 101 ); // e
    }

    private void test_Bad3BytesInStringLastBad()
    throws Exception
    {
        byte[] inbytes = { (byte)0x3e, (byte)0xe0, (byte)0xf4,
            (byte)0x41, (byte)0x3c };
            // ">???<"

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == '>' ); // First byte unchanged
        FreeGuideTest.my_assert( readbytes[1] == 63 );
        FreeGuideTest.my_assert( readbytes[2] == 63 );
            // Bad UTF replaced by '?'s

        FreeGuideTest.my_assert( readbytes[3] == 0x41 ); // Last char is ok
        FreeGuideTest.my_assert( readbytes[4] == '<' ); // Last unchanged
    }

    private void test_Bad3BytesInStringMiddleBad()
    throws Exception
    {
        byte[] inbytes = { (byte)0x3e, (byte)0xe0, (byte)0x41,
            (byte)0xf4, (byte)0x3c };
            // ">???<"

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == '>' ); // First byte unchanged
        FreeGuideTest.my_assert( readbytes[1] == 63 );
        FreeGuideTest.my_assert( readbytes[2] == 0x41 ); // This one is ok
            // Bad UTF replaced by '?'s

        FreeGuideTest.my_assert( readbytes[3] == 63 );
        FreeGuideTest.my_assert( readbytes[4] == '<' ); // Last unchanged
    }

    private void test_BadStartOf2ByteBeforeLT()
    throws Exception
    {
        byte[] inbytes = { (byte)0x50, (byte)0x61, (byte)0x72,
                           (byte)0x61, (byte)0x64, (byte)0x69,
                           (byte)0x73, (byte)0x65, (byte)0x20,
                           (byte)0x43, (byte)0x61, (byte)0x66,
                           (byte)0xE9, (byte)0x3C, (byte)0x2F,
                           (byte)0x74
        };

        //  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15
        // 50 61 72 61 64 69 73 65 20 43 61 66 E9 3C 2f 74
        // P  a  r  a  d  i  s  e     C  a  f  ?  <  /  t

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        //for( int i = 0; i < readbytes.length; ++i )
        //{
        //    System.err.println( Integer.toHexString( readbytes[i] ) );
        //}

        FreeGuideTest.my_assert( readbytes[ 0] == 'P' );
        FreeGuideTest.my_assert( readbytes[ 1] == 'a' );
        FreeGuideTest.my_assert( readbytes[ 2] == 'r' );
        FreeGuideTest.my_assert( readbytes[ 3] == 'a' );
        FreeGuideTest.my_assert( readbytes[ 4] == 'd' );
        FreeGuideTest.my_assert( readbytes[ 5] == 'i' );
        FreeGuideTest.my_assert( readbytes[ 6] == 's' );
        FreeGuideTest.my_assert( readbytes[ 7] == 'e' );
        FreeGuideTest.my_assert( readbytes[ 8] == ' ' );
        FreeGuideTest.my_assert( readbytes[ 9] == 'C' );
        FreeGuideTest.my_assert( readbytes[10] == 'a' );
        FreeGuideTest.my_assert( readbytes[11] == 'f' );
        FreeGuideTest.my_assert( readbytes[12] == '?' );
            // Even though this indicates a 3 byte sequence, we only
            // delete it and not the other bytes that would have been part
            // of it.
        FreeGuideTest.my_assert( readbytes[13] == '<' );
        FreeGuideTest.my_assert( readbytes[14] == '/' );
        FreeGuideTest.my_assert( readbytes[15] == 't' );
    }

}

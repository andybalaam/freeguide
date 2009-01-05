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
        // c0 is invalid since it represents the start of a 2-byte sequence
        // where the code point is < 127

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream );

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
            // Both bytes have changed into '?'
        FreeGuideTest.my_assert( readbytes[1] == 63 );
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
            (byte)0xc3, (byte)0xc2, (byte)0xa9, // wrong e-acute
            (byte)0x65, // e
        };

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == 102 ); // f
        FreeGuideTest.my_assert( readbytes[1] == 105 ); // i
        FreeGuideTest.my_assert( readbytes[2] ==  97 ); // a
        FreeGuideTest.my_assert( readbytes[3] == 110 ); // n
        FreeGuideTest.my_assert( readbytes[4] ==  99 ); // c
        FreeGuideTest.my_assert( readbytes[5] ==  63 ); // ?
        FreeGuideTest.my_assert( readbytes[6] ==  63 ); // ?
        FreeGuideTest.my_assert( readbytes[7] ==  63 ); // ?
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

        FreeGuideTest.my_assert( readbytes[3] == 63 );
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
        FreeGuideTest.my_assert( readbytes[2] == 63 );
            // Bad UTF replaced by '?'s

        FreeGuideTest.my_assert( readbytes[3] == 63 );
        FreeGuideTest.my_assert( readbytes[4] == '<' ); // Last unchanged
    }
}

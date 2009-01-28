package freeguide.test.fast;

import java.io.ByteArrayInputStream;

import freeguide.common.lib.general.BadUTF8FilterInputStream;
import freeguide.common.lib.general.IsUTF8StreamChecker;

import freeguide.test.FakeLogger;
import freeguide.test.FreeGuideTest;

public class BadUTF8FastTest
{
    private FakeLogger logger = new FakeLogger();

    public BadUTF8FastTest()
    {
    }

    public void run()
    throws Exception
    {
        test_OneGoodChar();
        test_OneGoodMultibyteChar();
        test_OneGoodMultibyteChar_split1();
        test_OneGoodMultibyteChar_split2();
        test_OneGoodMultibyteChar_split3();
        test_OneBadChar();
        test_BadStartOf2ByteSequence();
        test_Bad2Bytes();
        test_BadFiancC3C2A9e();
        test_BadFiancC3C2A9e_split1();
        test_BadFiancC3C2A9e_split2();
        test_BadFiancC3C2A9e_split3();
        test_Bad3BytesInStringLastBad();
        test_Bad3BytesInStringMiddleBad();
        test_BadStartOf2ByteBeforeLT();
        test_UTFChecker_ExtractBracket();
        test_UTFChecker_ExtractBracket_BOMsplit();
        test_UTFChecker_ProcessEncoding_UTF8();
        test_UTFChecker_ProcessEncoding_NotUTF8();
        test_UTFChecker_ProcessBracket_UTF8();
        test_UTFChecker_ProcessBracket_NotUTF8();
        test_UTFChecker_UTF8();
        test_UTFChecker_UTF8BOM();
        test_UTFChecker_NotUTF8();
        test_UTFChecker_NoHeader();
        test_NotUTF8Stream();
    }

    private void test_OneGoodChar()
    throws Exception
    {
        byte[] inbytes = { 116 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == 116 );
            // The byte is left unchanged
    }

    private void test_OneGoodMultibyteChar()
    throws Exception
    {
        // A good 3-byte character
        byte[] inbytes = { (byte)0xe4, (byte)0x80, (byte)0x81 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( ( readbytes[0] & 0xff ) == 0xe4 );
        FreeGuideTest.my_assert( ( readbytes[1] & 0xff ) == 0x80 );
        FreeGuideTest.my_assert( ( readbytes[2] & 0xff ) == 0x81 );
            // The bytes are left unchanged
    }

    private void test_OneGoodMultibyteChar_split1()
    throws Exception
    {
        // A good 3-byte character
        byte[] inbytes = { (byte)0xe4, (byte)0x80, (byte)0x81 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 1 );
        filterstream.read( readbytes, 1, inbytes.length - 1 );

        FreeGuideTest.my_assert( ( readbytes[0] & 0xff ) == 0xe4 );
        FreeGuideTest.my_assert( ( readbytes[1] & 0xff ) == 0x80 );
        FreeGuideTest.my_assert( ( readbytes[2] & 0xff ) == 0x81 );
            // The bytes are left unchanged
    }

    private void test_OneGoodMultibyteChar_split2()
    throws Exception
    {
        // A good 3-byte character
        byte[] inbytes = { (byte)0xe4, (byte)0x80, (byte)0x81 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 2 );
        filterstream.read( readbytes, 2, inbytes.length - 2 );

        FreeGuideTest.my_assert( ( readbytes[0] & 0xff ) == 0xe4 );
        FreeGuideTest.my_assert( ( readbytes[1] & 0xff ) == 0x80 );
        FreeGuideTest.my_assert( ( readbytes[2] & 0xff ) == 0x81 );
            // The bytes are left unchanged
    }

    private void test_OneGoodMultibyteChar_split3()
    throws Exception
    {
        // A good 3-byte character
        byte[] inbytes = { (byte)0xe4, (byte)0x80, (byte)0x81 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 1 );
        filterstream.read( readbytes, 1, 1 );
        filterstream.read( readbytes, 2, inbytes.length - 2 );

        FreeGuideTest.my_assert( ( readbytes[0] & 0xff ) == 0xe4 );
        FreeGuideTest.my_assert( ( readbytes[1] & 0xff ) == 0x80 );
        FreeGuideTest.my_assert( ( readbytes[2] & 0xff ) == 0x81 );
            // The bytes are left unchanged
    }

    private void test_OneBadChar()
    throws Exception
    {
        byte[] inbytes = { (byte)0x81 };
        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

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
            stream, logger );

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
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( readbytes[0] == 63 );
            // The bad first byte has become ?
        FreeGuideTest.my_assert( readbytes[1] == 0x24 );
            // The second byte is unchanged since once the bad
            // first byte is gone, this looks like good UTF-8
    }

    private byte[] getFiancC3C2A9eBytes()
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

        return inbytes;
    }

    private void test_BadFiancC3C2A9e()
    throws Exception
    {
        byte[] inbytes = getFiancC3C2A9eBytes();

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream, logger );

        // Read in 2 parts just to check that works too
        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 2 );
        filterstream.read( readbytes, 2, inbytes.length - 2 );

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

    private void test_BadFiancC3C2A9e_split1()
    throws Exception
    {
        byte[] inbytes = getFiancC3C2A9eBytes();

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream, logger );

        // Read in 2 parts, with the split in the middle of the
        // invalid multi-byte character
        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 6 );
        filterstream.read( readbytes, 6, inbytes.length - 6 );

        //for( int i = 0; i < readbytes.length; ++i )
        //{
        //    System.err.println( Integer.toHexString( readbytes[i] & 0xff ) );
        //}

        FreeGuideTest.my_assert( readbytes[0] == 102 ); // f
        FreeGuideTest.my_assert( readbytes[1] == 105 ); // i
        FreeGuideTest.my_assert( readbytes[2] ==  97 ); // a
        FreeGuideTest.my_assert( readbytes[3] == 110 ); // n
        FreeGuideTest.my_assert( readbytes[4] ==  99 ); // c
        FreeGuideTest.my_assert( ( readbytes[5] & 0xff ) == 0xc3 );
        FreeGuideTest.my_assert( ( readbytes[6] & 0xff ) == 0x80 );
            // We received byte 5 in a previous read so we can't go back and
            // fix it - we must just make the next byte a valid byte of
            // a 2-byte sequence.
        FreeGuideTest.my_assert( readbytes[7] == 63 ); // ?
            // The a9 byte is not valid as the start of a sequence
        FreeGuideTest.my_assert( readbytes[8] == 101 ); // e
    }

    private void test_BadFiancC3C2A9e_split2()
    throws Exception
    {
        byte[] inbytes = getFiancC3C2A9eBytes();

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream, logger );

        // Read in 2 parts with the split immediately after the character
        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 7 );
        filterstream.read( readbytes, 7, inbytes.length - 7 );

        //for( int i = 0; i < readbytes.length; ++i )
        //{
        //    System.err.println( Integer.toHexString( readbytes[i] & 0xff ) );
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

    private void test_BadFiancC3C2A9e_split3()
    throws Exception
    {
        byte[] inbytes = getFiancC3C2A9eBytes();

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream, logger );

        // Read in 3 parts just to check that works too
        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, 6 );
        filterstream.read( readbytes, 6, 1 );
        filterstream.read( readbytes, 7, inbytes.length - 7 );

        //for( int i = 0; i < readbytes.length; ++i )
        //{
        //    System.err.println( Integer.toHexString( readbytes[i] & 0xff ) );
        //}

        FreeGuideTest.my_assert( readbytes[0] == 102 ); // f
        FreeGuideTest.my_assert( readbytes[1] == 105 ); // i
        FreeGuideTest.my_assert( readbytes[2] ==  97 ); // a
        FreeGuideTest.my_assert( readbytes[3] == 110 ); // n
        FreeGuideTest.my_assert( readbytes[4] ==  99 ); // c
        FreeGuideTest.my_assert( ( readbytes[5] & 0xff ) == 0xc3 );
        FreeGuideTest.my_assert( ( readbytes[6] & 0xff ) == 0x80 );
            // We received byte 5 in a previous read so we can't go back and
            // fix it - we must just make the next byte a valid byte of
            // a 2-byte sequence.
        FreeGuideTest.my_assert( readbytes[7] == 63 ); // ?
            // The a9 byte is not valid as the start of a sequence
        FreeGuideTest.my_assert( readbytes[8] == 101 ); // e
    }

    private void test_Bad3BytesInStringLastBad()
    throws Exception
    {
        byte[] inbytes = { (byte)0x3e, (byte)0xe0, (byte)0xf4,
            (byte)0x41, (byte)0x3c };
            // ">???<"

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

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
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream( stream, logger );

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
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream, logger );

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

    private void test_UTFChecker_ExtractBracket()
    throws Exception
    {
        byte[] inbytes = { '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', '"', '1', '.', '0', '"', ' ', 'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g', '=', '"', 'U', 'T', 'F', '-', '8', '"', '?', '>', '\n', 'h', 'e', 'l', 'l', 'o' };

        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        checker.checkUTF8( inbytes, 0, inbytes.length );
        String bracket = checker.TESTING_ONLY_getCollectedBracket();

        FreeGuideTest.my_assert( bracket.equals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ) );
    }

    private void test_UTFChecker_ExtractBracket_BOMsplit()
    throws Exception
    {
        byte[] inbytes = { (byte)0xef, (byte)0xbb, (byte)0xbf, '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', '"', '1', '.', '0', '"', ' ', 'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g', '=', '"', 'U', 'T', 'F', '-', '8', '"', '?', '>', '\n', 'h', 'e', 'l', 'l', 'o' };

        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        checker.checkUTF8( inbytes, 0,  2 ); // Half-way through BOM
        checker.checkUTF8( inbytes, 2,  2 ); // Half-way through <?
        checker.checkUTF8( inbytes, 4, 36 ); // Half-way through ?>
        checker.checkUTF8( inbytes, 40, inbytes.length - 40 );

        String bracket = checker.TESTING_ONLY_getCollectedBracket();

        FreeGuideTest.my_assert( bracket.equals(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" ) );
    }

    private void test_UTFChecker_ProcessEncoding_UTF8()
    throws Exception
    {
        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        IsUTF8StreamChecker.IsUTF8Status status =
            checker.TESTING_ONLY_processEncoding( "utF-8" );

        FreeGuideTest.my_assert(
            status == IsUTF8StreamChecker.IsUTF8Status.UTF8 );
    }

    private void test_UTFChecker_ProcessEncoding_NotUTF8()
    throws Exception
    {
        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        IsUTF8StreamChecker.IsUTF8Status status =
            checker.TESTING_ONLY_processEncoding( "iso-8859-1" );

        FreeGuideTest.my_assert(
            status == IsUTF8StreamChecker.IsUTF8Status.OTHER );
    }

    private void test_UTFChecker_ProcessBracket_UTF8()
    throws Exception
    {
        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        IsUTF8StreamChecker.IsUTF8Status status =
            checker.TESTING_ONLY_processBracket(
                "<?xml version = '1.0' encoding = 'utf-8'?>" );

        FreeGuideTest.my_assert(
            status == IsUTF8StreamChecker.IsUTF8Status.UTF8 );
    }

    private void test_UTFChecker_ProcessBracket_NotUTF8()
    throws Exception
    {
        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        IsUTF8StreamChecker.IsUTF8Status status =
            checker.TESTING_ONLY_processBracket(
                "<?xml version='1.0' encoding='ISO-8859-1'?>" );

        FreeGuideTest.my_assert(
            status == IsUTF8StreamChecker.IsUTF8Status.OTHER );
    }

    private void test_UTFChecker_UTF8()
    throws Exception
    {
        // Normal UTF-8 sequence

        byte[] inbytes = { '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', '"', '1', '.', '0', '"', ' ', 'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g', '=', '"', 'U', 'T', 'F', '-', '8', '"', '?', '>', '\n', 'h', 'e', 'l', 'l', 'o' };

        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        boolean isUTF8 = checker.checkUTF8( inbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( isUTF8 );

    }

    private void test_UTFChecker_UTF8BOM()
    throws Exception
    {
        // Windows evil byte-order mark and then the normal UTF-8 sequence

        byte[] inbytes = { (byte)0xef, (byte)0xbb, (byte)0xbf, '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', '"', '1', '.', '0', '"', ' ', 'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g', '=', '\'', 'u', 't', 'f', '-', '8', '\'', '?', '>', '\n', 'h', 'e', 'l', 'l', 'o' };

        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        boolean isUTF8 = checker.checkUTF8( inbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( isUTF8 );
    }

    private void test_UTFChecker_NotUTF8()
    throws Exception
    {
        byte[] inbytes = { '\n', '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', '"', '1', '.', '0', '"', ' ', 'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g', '=', '"', 'I', 'S', 'O', '-', '8', '8', '5', '9', '-', '1', '"', '?', '>', '\n', 'h', 'e', 'l', 'l', 'o' };

        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        boolean isUTF8 = checker.checkUTF8( inbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( !isUTF8 );
    }

    private void test_UTFChecker_NoHeader()
    throws Exception
    {
        byte[] inbytes = { 'h', 'e', 'l', 'l', 'o', ' ', 'e', 'v', 'e', 'r', 'y', 'o', 'n', 'e', ' ', 'h', 'o', 'w', ' ', 'a', 'r', 'e', ' ', 'y', 'o', 'u', '?' };

        IsUTF8StreamChecker checker = new IsUTF8StreamChecker();
        boolean isUTF8 = checker.checkUTF8( inbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( isUTF8 );
    }

    private void test_NotUTF8Stream()
    throws Exception
    {
        byte[] inbytes = { '<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', '"', '1', '.', '0', '"', ' ', 'e', 'n', 'c', 'o', 'd', 'i', 'n', 'g', '=', '"', 'I', 'S', 'O', '-', '8', '8', '5', '9', '-', '1', '"', '?', '>', '\n', 'h', 'e', 'l', 'l', 'o' };

        ByteArrayInputStream stream = new ByteArrayInputStream( inbytes );
        BadUTF8FilterInputStream filterstream = new BadUTF8FilterInputStream(
            stream, logger );

        FreeGuideTest.my_assert( filterstream.TESTING_ONLY_isUTF8Stream() );

        byte[] readbytes = new byte[inbytes.length];
        filterstream.read( readbytes, 0, inbytes.length );

        FreeGuideTest.my_assert( !filterstream.TESTING_ONLY_isUTF8Stream() );
    }

}

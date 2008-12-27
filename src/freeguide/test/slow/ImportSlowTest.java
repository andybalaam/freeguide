package freeguide.test.slow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.BadUTF8FilterInputStream;
import freeguide.common.lib.importexport.XMLTVImport;
import freeguide.common.lib.importexport.XMLTVImportHandler;
import freeguide.common.lib.importexport.XMLTVImport.Filter;
import freeguide.common.lib.importexport.XMLTVImport.ProgrammesCountCallback;
import freeguide.common.plugininterfaces.IStoragePipe;

import freeguide.test.FreeGuideTest;

public class ImportSlowTest
{
    public class FakeProgCountCallBack extends ProgrammesCountCallback
    {
        public int count;

        @Override
        public void onProgramme( int count )
        {
            this.count = count;
        }
    }

    public class FakeStoragePipe implements IStoragePipe
    {
        public ArrayList<TVChannel> channels = new ArrayList<TVChannel>();

        public void addChannel( TVChannel channel )
        {
            channels.add( channel );
        }

        public void addData( TVData data )
        {
        }

        public void addProgramme( String channelID, TVProgramme programme )
        {
        }

        public void addProgrammes( String channelID, TVProgramme[] programmes )
        {
        }

        public void finishBlock()
        {
        }
    }

    private SAXParserFactory factory;
    private SAXParser saxParser;

    public ImportSlowTest() throws ParserConfigurationException, SAXException
    {
        factory = SAXParserFactory.newInstance(  );
        saxParser = factory.newSAXParser(  );
    }

    public void run() throws SAXException, IOException, MyAssertFailureException, ParserConfigurationException
    {
        test_EmptyTVTags();
        test_SingleChannelNoGenerator();
        test_TwoChannelsNormal();
        test_ProgrammeDate14Num();
        test_ProgrammeDate14NumPlusZ();
        test_InvalidUTF8();
    }

    private void test_EmptyTVTags()
    throws SAXException, IOException, MyAssertFailureException
    {
        ArrayList<TVChannel> expectedChannels = new ArrayList<TVChannel>();
        parseString( "<tv></tv>", expectedChannels );
    }

    private void test_SingleChannelNoGenerator()
    throws SAXException, IOException, MyAssertFailureException
    {
        ArrayList<TVChannel> expectedChannels = new ArrayList<TVChannel>();
        expectedChannels.add( new TVChannel( "c.p.ct1_sk", "CT1" ) );
        parseString(
            "<tv>\n" +
            "   <channel id=\"ct1_sk\">\n" +
            "       <display-name>CT1</display-name>\n" +
            "       <display-name>1</display-name>\n" +
            "       <url>http://frantisheq.net84.net</url>\n" +
            "   </channel>\n" +
            "</tv>\n",
            expectedChannels );
    }

    private void test_TwoChannelsNormal()
    throws SAXException, IOException, MyAssertFailureException
    {
        ArrayList<TVChannel> expectedChannels = new ArrayList<TVChannel>();
        expectedChannels.add( new TVChannel( "c.p.abc1.disney.com", "ABC1" ) );
        expectedChannels.add( new TVChannel( "c.p.arydigital.tv", "ARY Digital" ) );
        parseString(
            "<tv source-info-name=\"Radio Times\" " +
            "generator-info-name=\"XMLTV\" "+
            "generator-info-url=\"http://membled.com/work/apps/xmltv/\">\n" +
            "   <channel id=\"abc1.disney.com\">\n" +
            "       <display-name>ABC1</display-name>\n" +
            "       <display-name>abc1</display-name>\n" +
            "       <icon src=\"http://www.lyngsat-logo.com/logo/tv/aa/abc1.jpg\" />\n" +
            "   </channel>\n" +
            "   <channel id=\"arydigital.tv\">\n" +
            "       <display-name>ARY Digital</display-name>\n" +
            "       <display-name>ARY Digital</display-name>\n" +
            "       <icon src=\"http://www.lyngsat-logo.com/logo/tv/aa/ary_digital.jpg\" />\n" +
            "   </channel>\n" +
            "</tv>\n",
            expectedChannels );
    }

    private void test_ProgrammeDate14Num()
    throws SAXException, IOException, MyAssertFailureException
    {
        ArrayList<TVChannel> expectedChannels = new ArrayList<TVChannel>();
        expectedChannels.add( new TVChannel( "c.p.ct1_sk", "ABC1" ) );
        parseString(
            "<tv>\n" +
            "   <channel id=\"ct1_sk\">\n" +
            "       <display-name>CT1</display-name>\n" +
            "       <display-name>1</display-name>\n" +
            "       <url>http://frantisheq.net84.net</url>\n" +
            "   </channel>\n" +
            "   <programme start=\"20080818000000\" stop=\"20080818050000\" " +
            "channel=\"ct1_sk\">\n" +
            "       <title>no schedule</title>\n" +
            "       <desc>TVxb TERMS AND CONDITIONS: This guide " +
            "software may be used by private users for personal " +
            "use only. Commercial use, sale, or distribution, or " +
            "bundling of this software with commercial products, " +
            "is prohibited. The use of software or this guide to " +
            "provide a free or paid service is prohibited. The " +
            "program information in this guide might be protected " +
            "by copyright and distribution to other people or " +
            "organizations without the permission of the copyright " +
            "holders might not be permitted. [ www.tvxb.com ]</desc>\n" +
            "   </programme>\n" +
            "</tv>\n",
            expectedChannels );
    }

    private void test_ProgrammeDate14NumPlusZ()
    throws SAXException, IOException, MyAssertFailureException
    {
        ArrayList<TVChannel> expectedChannels = new ArrayList<TVChannel>();
        expectedChannels.add( new TVChannel( "c.p.ct1_sk", "ABC1" ) );
        parseString(
            "<tv>\n" +
            "   <channel id=\"ct1_sk\">\n" +
            "       <display-name>CT1</display-name>\n" +
            "       <display-name>1</display-name>\n" +
            "       <url>http://frantisheq.net84.net</url>\n" +
            "   </channel>\n" +
            "   <programme start=\"20080818000000 +0500\" stop=\"20080818050000 +0500\" " +
            "channel=\"ct1_sk\">\n" +
            "       <title>My Prog Title</title>\n" +
            "       <desc>my prog desc</desc>\n" +
            "   </programme>\n" +
            "</tv>\n",
            expectedChannels );
    }

    byte[] ConcatenateByteArrays( byte[][] byteArrays )
    {
        ArrayList<Byte> allBytesList = new ArrayList<Byte>();

        for( int i = 0; i < byteArrays.length; ++i )
        {
            for( int j = 0; j < byteArrays[i].length; ++j )
            {
                allBytesList.add( byteArrays[i][j] );
            }
        }

        byte[] ret = new byte[allBytesList.size()];
        for( int i = 0; i < allBytesList.size(); ++i )
        {
            ret[i] = allBytesList.get( i );
        }

        return ret;
    }

    private void test_InvalidUTF8()
    throws SAXException, IOException, MyAssertFailureException, ParserConfigurationException
    {
        // Create a byte sequence that contains 11000010, 00100100,
        // which is incorrect UTF-8 because the first byte indicates
        // that this is a 2-byte sequence, but the second byte
        // does not begin with 1 (in binary), which is required
        // for the second byte of a 2-byte UTF-8 sequence.
        byte[] badUTF8Bytes = { (byte)0xC2, (byte)0x24 };

        // Create the rest of the string we want
        String beforeBad =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE tv SYSTEM \"xmltv.dtd\">\n" +
            "<tv>\n" +
            "   <channel id=\"ct1_sk\">\n" +
            "       <display-name>";

        String afterBad =
            "</display-name>\n" +
            "       <display-name>1</display-name>\n" +
            "       <url>http://frantisheq.net84.net</url>\n" +
            "   </channel>\n" +
            "</tv>\n";

        // Create a byte array that contains the full string including
        // the bad
        /*ByteArrayOutputStream allBytesOStream = new ByteArrayOutputStream();
        DataOutputStream allBytesDataStream = new DataOutputStream( allBytesOStream );
        allBytesDataStream.writeUTF( beforeBad );
        allBytesDataStream.write( badUTF8Bytes );
        allBytesDataStream.writeUTF( afterBad );*/

        byte[][] byteArrays = {
            beforeBad.getBytes( "UTF-8" ),
            badUTF8Bytes,
            afterBad.getBytes( "UTF-8" )
        };

        byte[] allBytes = ConcatenateByteArrays( byteArrays );

        ByteArrayInputStream allBytesIStream = new ByteArrayInputStream( allBytes );

        FakeStoragePipe storage = new FakeStoragePipe();
        FakeProgCountCallBack countCallback = new FakeProgCountCallBack();
        Filter filter = new Filter();

        XMLTVImport imp = new XMLTVImport();
        imp.process( allBytesIStream, storage, countCallback, filter, "c.p." );
    }

    // ------------------------------
    private void parseString( String xmlToParse, ArrayList<TVChannel> expectedChannels )
    throws SAXException, IOException, MyAssertFailureException
    {
        FakeStoragePipe storage = new FakeStoragePipe();
        FakeProgCountCallBack countCallback = new FakeProgCountCallBack();
        Filter filter = new Filter();

        XMLTVImportHandler handler =
            new XMLTVImportHandler(
                storage, countCallback, filter, "c.p." );
        InputSource inputSource = new InputSource( new StringReader( xmlToParse ) );

        saxParser.parse( inputSource, handler );

        FreeGuideTest.my_assert( storage.channels.equals( expectedChannels ) );
    }
}

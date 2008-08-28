package freeguide.test.slow;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.importexport.XMLTVImportHandler;
import freeguide.common.lib.importexport.XMLTVImport.Filter;
import freeguide.common.lib.importexport.XMLTVImport.ProgrammesCountCallback;
import freeguide.common.plugininterfaces.IStoragePipe;

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

    public class MyAssertFailureException extends Exception
    {
        static final long serialVersionUID = 1;

        public MyAssertFailureException( String message )
        {
            super( message );
        }
    }

    private SAXParserFactory factory;
    private SAXParser saxParser;

    public ImportSlowTest() throws ParserConfigurationException, SAXException
    {
        factory = SAXParserFactory.newInstance(  );
        saxParser = factory.newSAXParser(  );
    }

    public void run() throws SAXException, IOException, MyAssertFailureException
    {
        test_EmptyTVTags();
        test_SingleChannelNoGenerator();
        test_TwoChannelsNormal();
        test_ProgrammeDate14Num();
        test_ProgrammeDate14NumPlusZ();
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

    // ------------------------------

    private void my_assert( boolean condition ) throws MyAssertFailureException
    {
        if( !condition )
        {
            throw new MyAssertFailureException( "Assertion failed" );
        }
    }

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

        my_assert( storage.channels.equals( expectedChannels ) );
    }
}

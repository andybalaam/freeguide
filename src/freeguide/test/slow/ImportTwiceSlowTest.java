package freeguide.test.slow;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.StringHelper;
import freeguide.common.lib.importexport.XMLTVImport;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.plugins.program.freeguide.lib.fgspecific.StoragePipe;
import freeguide.plugins.storage.serfiles.StorageSerFilesByDay;

import freeguide.test.FakeLogger;
import freeguide.test.FreeGuideTest;

public class ImportTwiceSlowTest
{
    private FakeLogger logger = new FakeLogger();

    public void run() throws Exception
    {
        test_bug270348();
    }

    /**
     * See http://bugs.launchpad.net/freeguide-tv/+bug/270348
     *
     * If programmes which span a file boundary are wiped out
     * by a long incoming programme, the programmes in the
     * later file do not get wiped out (because they are in
     * a later file).  Then, when we read in the files again
     * in date order, the earlier programme is deleted because
     * the later one (which should have been wiped out)
     * overlaps it.
     *
     * The fix (in StorageSerFilesByDay) is to read the files
     * back in in reverse date order, so that any programmes
     * overhanging the file boundary like this are read later
     * than the programmes they wipe out, so they are deleted.
     *
     * This actually still leaves a weird corner case: if we
     * originally got a long programme and that is replaced
     * by a shorter one (with an empty gap before it) that
     * goes into the next file, the newer, shorter programme
     * will be lost, wiped out by the older, longer one.
     * This is a weird case because normally if you were
     * getting new programmes to replace the old long one,
     * you'd get one starting at the same time as the old
     * one, so it would get wiped out as normal.  So I don't
     * think this case is worth considering.
     *
     * If I change my mind in the future, probably the best
     * thing to do would be to store a "received date" on
     * each programme and explicitly keep the latest.
     *
     * Note that we probably ought to have a slightly simpler
     * test here as well.  This one uses XML copied and pasted
     * (and edited) from the bug itself.
     *
     * @throws Exception
     */
    private void test_bug270348() throws Exception
    {
        /*
         * We start with 3 programmes:
         * 19:00 to 21:15
         * 21:15 to 21:30
         * 21:30 to 22:00
         */

        String firstXML = "<tv><programme start=\"20080914190000 -0400\"" +
            " stop=\"20080914211500 -0400\"" +
            " channel=\"I16318.labs.zap2it.com\">" +
            "<title lang=\"en\">The Terminal</title>" +
            "<desc lang=\"en\">blah</desc>" +
            "</programme>" +
            "<programme start=\"20080914211500 -0400\"" +
            " stop=\"20080914213000 -0400\"" +
            " channel=\"I16318.labs.zap2it.com\">" +
            "<title lang=\"en\">To Be Announced</title>" +
            "</programme>" +
            "<programme start=\"20080914213000 -0400\"" +
            " stop=\"20080914220000 -0400\"" +
            " channel=\"I16318.labs.zap2it.com\">" +
            "<title lang=\"en\">Billable Hours</title>" +
            "</programme></tv>";

        /*
         * Then we will add two programmes:
         * 19:00 to 21:45
         * 21:45 to 22:00
         */

        String secondXML = "<tv><programme start=\"20080914190000 -0400\"" +
                " stop=\"20080914214500 -0400\"" +
                " channel=\"I16318.labs.zap2it.com\">" +
                "<title lang=\"en\">The Terminal 2</title>" +
                "</programme>" +
                "<programme start=\"20080914214500 -0400\"" +
                " stop=\"20080914220000 -0400\"" +
                " channel=\"I16318.labs.zap2it.com\">" +
                "<title lang=\"en\">To Be Announced 2</title>" +
                "</programme></tv>";

        // Read in the first 3 programmes
        InputStream is = new ByteArrayInputStream(
            firstXML.getBytes( "utf-8" ) );

        File storagedir = new File( "tmp_slow" );
        storagedir.mkdir();
        StorageSerFilesByDay storage = new StorageSerFilesByDay( storagedir );
        storage.debugIncludeOldProgrammes = true;

        StoragePipe pipe = new StoragePipe( storage );
        XMLTVImport imp = new XMLTVImport(  );
        imp.process( is, pipe, null, new XMLTVImport.Filter(  ),
            StringHelper.EMPTY_STRING, logger );

        // Tell the pipe we have finished - flush to file.  (This is needed
        // trigger the bug - if it's all within the same download you will
        // not see it.
        pipe.finish();

        // Now read in the second 2 programmes, which overlap the first 3.
        is = new ByteArrayInputStream(
            secondXML.getBytes( "utf-8" ) );
        imp.process( is, pipe, null, new XMLTVImport.Filter(  ),
            StringHelper.EMPTY_STRING, logger );

        // Again, tell the pipe we have finished so we flush to file.
        pipe.finish();

        // Now read in for the files - first we need to make an "Info"
        // object to say what time and channel we are interested in.
        IModuleStorage.Info infoAllDates = new IModuleStorage.Info();
        infoAllDates.channelsList.add( new TVChannelsSet.Channel(
            "I16318.labs.zap2it.com", "My Channel" ) );

        SimpleDateFormat fmt = new SimpleDateFormat( "yyyyMMddHHmmss Z" );

        infoAllDates.minDate = fmt.parse( "20080914180000 -0400" ).getTime();
        infoAllDates.maxDate = fmt.parse( "20080914235900 -0400" ).getTime();

        // Then we load the data from the files.
        TVData data = storage.get( infoAllDates );

        // And now we get the channel we need out of the loaded data.
        TVChannel channel = data.get( "I16318.labs.zap2it.com" );

        // There should be 2 programmes - the 2 new ones.  The old ones
        // should be gone.
        FreeGuideTest.my_assert( channel.getProgrammesCount() == 2 );

        Iterator<TVProgramme> it = channel.getProgrammes().iterator();

        FreeGuideTest.my_assert( it.next().getTitle().equals(
            "The Terminal 2" ) );

        FreeGuideTest.my_assert( it.next().getTitle().equals(
            "To Be Announced 2" ) );

        // Finally we clean up our temporary directory.
        for( File f : storagedir.listFiles() )
        {
            f.delete();
        }
        storagedir.delete();
    }
}

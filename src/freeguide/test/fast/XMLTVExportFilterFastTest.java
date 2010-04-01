package freeguide.test.fast;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import freeguide.common.gui.FileChooserExtension;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.importexport.XMLTVExport;
import freeguide.plugins.importexport.xmltv.FilteredTVData;
import freeguide.test.FreeGuideTest;

public class XMLTVExportFilterFastTest
{
    private ModifiableTVData data;

    private FakeStorage storage;

    private FakeViewer viewer;

    private FakeApplication application;

    private FileChooserExtension ext;

    private StringWriter writer;

    private Calendar cal;

    private TVChannel chan1;

    private TVChannel chan2;

    private TVProgramme prog11;

    private TVProgramme prog12;

    private TVProgramme prog13;

    private TVProgramme prog21;

    private TVProgramme prog22;

    private TVChannel addChannel( String id, String name )
    {
        TVChannel chan = new TVChannel( id, name );
        data.addChannel( chan );
        storage.info.channelsList.add( new TVChannelsSet.Channel( id, name ) );
        return chan;
    }

    private TVProgramme addProgramme( TVChannel chan, String title, int sY,
        int sMon, int sD, int sH, int sMin, int eY, int eMon, int eD, int eH,
        int eMin )
    {
        TVProgramme prog = new TVProgramme();
        prog.setTitle( title );
        cal.set( sY, sMon, sD, sH, sMin );
        prog.setStart( cal.getTimeInMillis() );
        cal.set( eY, eMon, eD, eH, eMin );
        prog.setEnd( cal.getTimeInMillis() );
        chan.put( prog );
        return prog;
    }

    private void setUp()
    {
        data = new ModifiableTVData();
        storage = new FakeStorage( data );
        viewer = new FakeViewer();
        application = new FakeApplication();
        ext = new FileChooserExtension( storage, viewer, application );
        writer = new StringWriter();
        cal = GregorianCalendar.getInstance();

        chan1 = addChannel( "chan1", "Channel 1" );

        prog11 = addProgramme( chan1, "Programme 1.1", 2009, 07, 28, 05, 20,
            2009, 7, 28, 10, 30 );
        prog12 = addProgramme( chan1, "Programme 1.2", 2009, 07, 24, 10, 30,
            2009, 7, 24, 10, 40 );
        prog13 = addProgramme( chan1, "Programme 1.3", 2009, 07, 29, 04, 30,
            2009, 7, 29, 10, 40 );

        chan2 = addChannel( "chan2", "Channel 2" );

        prog21 = addProgramme( chan2, "Programme 2.1", 2009, 7, 28, 15, 20,
            2009, 7, 28, 15, 30 );
        prog22 = addProgramme( chan2, "Programme 2.2", 2009, 7, 30, 15, 30,
            2009, 7, 30, 15, 40 );

        cal.set( 2009, 1, 1, 0, 0 );
        storage.info.minDate = cal.getTimeInMillis();
        cal.set( 2010, 1, 1, 0, 0 );
        storage.info.maxDate = cal.getTimeInMillis();
    }

    boolean contains_channel( Collection channelsInfo, TVChannel chan )
    {
        Iterator itCh = channelsInfo.iterator();
        while( itCh.hasNext() )
        {
            TVChannelsSet.Channel chinfo = (TVChannelsSet.Channel)itCh.next();
            if( chinfo.getChannelID().equals( chan.getID() ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Test exporting everything for today.
     *
     * @throws IOException
     * @throws Exception
     */
    public void test_All() throws IOException, Exception
    {
        setUp();

        FilteredTVData filtData = new FilteredTVData( data, ext, storage,
            application );
        new XMLTVExport().exportToWriter( writer, filtData );
        String xml = writer.toString();

        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan1 ) );
        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan2 ) );
        FreeGuideTest.my_assert( xml.contains( prog11.getTitle() ) );
        FreeGuideTest.my_assert( xml.contains( prog12.getTitle() ) );
        FreeGuideTest.my_assert( xml.contains( prog13.getTitle() ) );
        FreeGuideTest.my_assert( xml.contains( prog21.getTitle() ) );
        FreeGuideTest.my_assert( xml.contains( prog22.getTitle() ) );
    }

    /**
     * Test exporting only selected programmes
     *
     * @throws IOException
     * @throws Exception
     */
    public void test_Selected() throws IOException, Exception
    {
        setUp();

        // The user checked the "Selected only" checkbox
        ext.getCbSelected().setSelected( true );

        // Add programmes 11 and 22 to the selections
        FakeReminder reminder = new FakeReminder();
        reminder.selectedProgs.add( prog11 );
        reminder.selectedProgs.add( prog22 );
        application.reminders.add( reminder );

        FilteredTVData filtData = new FilteredTVData( data, ext, storage,
            application );
        new XMLTVExport().exportToWriter( writer, filtData );
        String xml = writer.toString();

        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan1 ) );
        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan2 ) );
        FreeGuideTest.my_assert( xml.contains( prog11.getTitle() ) ); // Selected
        FreeGuideTest.my_assert( !xml.contains( prog12.getTitle() ) );// Not
        FreeGuideTest.my_assert( !xml.contains( prog13.getTitle() ) );// Not
        FreeGuideTest.my_assert( !xml.contains( prog21.getTitle() ) );// Not
        FreeGuideTest.my_assert( xml.contains( prog22.getTitle() ) ); // Selected
    }

    /**
     * Test exporting only programmes on today
     *
     * @throws IOException
     * @throws Exception
     */
    public void test_Today() throws IOException, Exception
    {
        setUp();

        // The user chose to export only today's programmes
        ext.getCbToday().setSelected( true );

        // Set the viewer's day to 28th July (6am - 6am next morning)
        cal.set( 2009, 07, 28, 6, 0 );
        viewer.info.minDate = cal.getTimeInMillis();
        cal.set( 2009, 07, 29, 6, 0 );
        viewer.info.maxDate = cal.getTimeInMillis();

        FilteredTVData filtData = new FilteredTVData( data, ext, storage,
            application );
        new XMLTVExport().exportToWriter( writer, filtData );
        String xml = writer.toString();

        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan1 ) );
        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan2 ) );
        // Overlaps beginning
        FreeGuideTest.my_assert( xml.contains( prog11.getTitle() ) );
        // Before beginning
        FreeGuideTest.my_assert( !xml.contains( prog12.getTitle() ) );
        // Overlaps end
        FreeGuideTest.my_assert( xml.contains( prog13.getTitle() ) );
        // Completely within
        FreeGuideTest.my_assert( xml.contains( prog21.getTitle() ) );
        // After end
        FreeGuideTest.my_assert( !xml.contains( prog22.getTitle() ) );
    }

    public void test_ChannelSet() throws IOException, Exception
    {
        setUp();

        // The user chose to export only the current channel set
        ext.getCbChannelsList().setSelected( true );

        // Add Channel 1 to the current channel set
        ext.getSaveInfo().channelsList.add( new TVChannelsSet.Channel( chan1
            .getID(), chan1.getDisplayName() ) );

        FilteredTVData filtData = new FilteredTVData( data, ext, storage,
            application );
        new XMLTVExport().exportToWriter( writer, filtData );
        String xml = writer.toString();

        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan1 ) );
        FreeGuideTest.my_assert( !contains_channel( filtData.getChannels(),
            chan2 ) );
        FreeGuideTest.my_assert( xml.contains( prog11.getTitle() ) ); // Ch1
        FreeGuideTest.my_assert( xml.contains( prog12.getTitle() ) ); // Ch1
        FreeGuideTest.my_assert( xml.contains( prog13.getTitle() ) ); // Ch1
        FreeGuideTest.my_assert( !xml.contains( prog21.getTitle() ) );// Ch2
        FreeGuideTest.my_assert( !xml.contains( prog22.getTitle() ) );// Ch2
    }

    public void test_SelectedToday() throws IOException, Exception
    {
        setUp();

        // The user checked the "Selected only" checkbox
        ext.getCbSelected().setSelected( true );

        // Add programmes 11 and 22 to the selections
        FakeReminder reminder = new FakeReminder();
        reminder.selectedProgs.add( prog11 );
        reminder.selectedProgs.add( prog22 );
        application.reminders.add( reminder );

        // The user chose to export only today's programmes
        ext.getCbToday().setSelected( true );

        // Set the viewer's day to 28th July (6am - 6am next morning)
        cal.set( 2009, 07, 28, 6, 0 );
        viewer.info.minDate = cal.getTimeInMillis();
        cal.set( 2009, 07, 29, 6, 0 );
        viewer.info.maxDate = cal.getTimeInMillis();

        FilteredTVData filtData = new FilteredTVData( data, ext, storage,
            application );
        new XMLTVExport().exportToWriter( writer, filtData );
        String xml = writer.toString();

        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan1 ) );
        // Don't care whether it contains Channel 2 for now

        // Selected and today
        FreeGuideTest.my_assert( xml.contains( prog11.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog12.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog13.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog21.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog22.getTitle() ) );
    }

    public void test_SelectedChannelSet() throws IOException, Exception
    {
        setUp();

        // The user checked the "Selected only" checkbox
        ext.getCbSelected().setSelected( true );

        // Add programmes 11 and 22 to the selections
        FakeReminder reminder = new FakeReminder();
        reminder.selectedProgs.add( prog11 );
        reminder.selectedProgs.add( prog22 );
        application.reminders.add( reminder );

        // The user chose to export only the current channel set
        ext.getCbChannelsList().setSelected( true );

        // Add Channel 2 to the current channel set
        ext.getSaveInfo().channelsList.add( new TVChannelsSet.Channel( chan2
            .getID(), chan2.getDisplayName() ) );

        FilteredTVData filtData = new FilteredTVData( data, ext, storage,
            application );
        new XMLTVExport().exportToWriter( writer, filtData );
        String xml = writer.toString();

        FreeGuideTest.my_assert( !contains_channel( filtData.getChannels(),
            chan1 ) );
        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan2 ) );
        FreeGuideTest.my_assert( !xml.contains( prog11.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog12.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog13.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog21.getTitle() ) );
        // Selected and Channel 2
        FreeGuideTest.my_assert( xml.contains( prog22.getTitle() ) );
    }

    public void test_SelectedTodayChannelSet() throws IOException, Exception
    {
        setUp();

        // The user checked the "Selected only" checkbox
        ext.getCbSelected().setSelected( true );

        // Add programmes 11 and 22 to the selections
        FakeReminder reminder = new FakeReminder();
        reminder.selectedProgs.add( prog11 );
        reminder.selectedProgs.add( prog22 );
        application.reminders.add( reminder );

        // The user chose to export only today's programmes
        ext.getCbToday().setSelected( true );

        // Set the viewer's day to 28th July (6am - 6am next morning)
        cal.set( 2009, 07, 30, 6, 0 );
        viewer.info.minDate = cal.getTimeInMillis();
        cal.set( 2009, 07, 31, 6, 0 );
        viewer.info.maxDate = cal.getTimeInMillis();

        // The user chose to export only the current channel set
        ext.getCbChannelsList().setSelected( true );

        // Add Channel 2 to the current channel set
        ext.getSaveInfo().channelsList.add( new TVChannelsSet.Channel( chan2
            .getID(), chan2.getDisplayName() ) );

        FilteredTVData filtData = new FilteredTVData( data, ext, storage,
            application );
        new XMLTVExport().exportToWriter( writer, filtData );
        String xml = writer.toString();

        FreeGuideTest.my_assert( !contains_channel( filtData.getChannels(),
            chan1 ) );
        FreeGuideTest.my_assert( contains_channel( filtData.getChannels(),
            chan2 ) );
        FreeGuideTest.my_assert( !xml.contains( prog11.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog12.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog13.getTitle() ) );
        FreeGuideTest.my_assert( !xml.contains( prog21.getTitle() ) );
        // Selected and Channel 2 and today
        FreeGuideTest.my_assert( xml.contains( prog22.getTitle() ) );
    }

    public void run() throws Exception
    {
        test_All();
        test_Selected();
        test_Today();
        test_ChannelSet();
        test_SelectedToday();
        test_SelectedChannelSet();
        test_SelectedTodayChannelSet();
    }
}

package freeguide.test.fast;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.ManualSelection;
import freeguide.common.lib.general.BadUTF8FilterInputStream;
import freeguide.plugins.reminder.alarm.AlarmReminder;
import freeguide.test.FreeGuideTest;

public class DeleteSelectionsFastTest
{

    public void run()
    throws Exception
    {
        test_DeleteSelections();
    }

    private void test_DeleteSelections()
    throws Exception
    {
        AlarmReminder rem = new AlarmReminder();

        TVChannel chan = new TVChannel( "chan" );

        Calendar cal = GregorianCalendar.getInstance();

        // 2 programmes from yesterday
        TVProgramme prog_new1 = new TVProgramme();
        prog_new1.setChannel( chan );
        cal.set( 2008, 11, 26, 12, 15 );
        prog_new1.setStart( cal.getTimeInMillis() );

        TVProgramme prog_new2 = new TVProgramme();
        cal.set( 2008, 11, 25, 12, 15 );
        prog_new2.setStart( cal.getTimeInMillis() );
        prog_new2.setChannel( chan );

        // 2 programmes from over 4 weeks ago
        TVProgramme prog_old1 = new TVProgramme();
        prog_old1.setChannel( chan );
        cal.set( 2008, 10, 26, 12, 15 );
        prog_old1.setStart( cal.getTimeInMillis() );

        TVProgramme prog_old2 = new TVProgramme();
        prog_old2.setChannel( chan );
        cal.set( 2008, 10, 25, 12, 15 );
        prog_old2.setStart( cal.getTimeInMillis() );
        
        rem.setProgrammeSelection( prog_new1, true );
        rem.setProgrammeSelection( prog_new2, false );
        rem.setProgrammeSelection( prog_old1, true );
        rem.setProgrammeSelection( prog_old2, false );

        List selectionList = rem.getReminderConfig().manualSelectionList;

        // So we start off with 4 selections
        FreeGuideTest.my_assert( selectionList.size() == 4 );

        cal.set( 2008, 11, 27, 12, 15 );
        rem.cleanup( cal.getTimeInMillis() );

        // And now we have deleted 2, leaving 2 behind
        FreeGuideTest.my_assert( selectionList.size() == 2 );

        FreeGuideTest.my_assert(
            ( (ManualSelection)selectionList.get( 0 )
                ).programmeTime == prog_new1.getStart() );

        FreeGuideTest.my_assert(
            ( (ManualSelection)selectionList.get( 1 )
                ).programmeTime == prog_new2.getStart() );
    }
}

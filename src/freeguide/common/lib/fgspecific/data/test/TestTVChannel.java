package freeguide.common.lib.fgspecific.data.test;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import java.util.GregorianCalendar;
import java.util.Iterator;

public class TestTVChannel
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        new TestTVChannel().runTests();
    }

    private void runTests()
    {
        testOverlappingProgrammes();
        testNoEndTimes();
        System.out.println( "All tests passed" );
    }

    /**
     * Tests that if possibly-overlapping programmes
     * are added to this channel, the correct behaviour
     * happens (i.e. non-overlapping programmes are
     * added with no side effects, but programmes that
     * overlap remove programmes they clash with.
     *
     */
    private void testOverlappingProgrammes()
    {
        TVChannel channel = new TVChannel( "channel_id" );

        TVProgramme prog08to10 = new TVProgramme();
        prog08to10.setStart( getLongTime(  8, 00 ) );
        prog08to10.setEnd(   getLongTime( 10, 00 ) );

        // When we've put one programme in, we only have one programme
        channel.put( prog08to10 );
        assert channel.getProgrammes().size() == 1;
        assert channel.getProgrammes().first() == prog08to10;

        TVProgramme prog09to11 = new TVProgramme();
        prog09to11.setStart( getLongTime(  9, 00 ) );
        prog09to11.setEnd(   getLongTime( 11, 00 ) );

        // When we put another in, but it overlaps, we still only have one
        channel.put( prog09to11 );
        assert channel.getProgrammes().size() == 1;
        assert channel.getProgrammes().first() == prog09to11;

        // When we put another in, but it overlaps (the other side), we still only have one
        channel.put( prog08to10 );
        assert channel.getProgrammes().size() == 1;
        assert channel.getProgrammes().first() == prog08to10;

        TVProgramme prog0815to0830 = new TVProgramme();
        prog0815to0830.setStart( getLongTime(  8, 15 ) );
        prog0815to0830.setEnd(   getLongTime(  8, 30 ) );

        // When we put another in, but it overlaps (inside the first), we still only have one
        channel.put( prog0815to0830 );
        assert channel.getProgrammes().size() == 1;
        assert channel.getProgrammes().first() == prog0815to0830;

        // Switch back to the 8 to 10 one
        channel.put( prog08to10 );
        assert channel.getProgrammes().size() == 1;
        assert channel.getProgrammes().first() == prog08to10;

        TVProgramme prog08to10again = new TVProgramme();
        prog08to10again.setStart( getLongTime(  8, 00 ) );
        prog08to10again.setEnd(   getLongTime( 10, 00 ) );

        // When we put another in, but it's identical, we still only have one
        channel.put( prog08to10again );
        assert channel.getProgrammes().size() == 1;
        assert channel.getProgrammes().first() == prog08to10again;

        TVProgramme prog06to07 = new TVProgramme();
        prog06to07.setStart( getLongTime(  6, 00 ) );
        prog06to07.setEnd(   getLongTime(  7, 00 ) );

        // When we put another in before, not overlapping, we end up with 2
        channel.put( prog06to07 );
        assert channel.getProgrammes().size() == 2;
        Iterator it = channel.getProgrammes().iterator();
        assert it.next() == prog06to07;
        assert it.next() == prog08to10again;

        TVProgramme prog07to08 = new TVProgramme();
        prog07to08.setStart( getLongTime(  7, 00 ) );
        prog07to08.setEnd(   getLongTime(  8, 00 ) );

        // When we put another in, not overlapping but squeezed in between, we end up with 3
        channel.put( prog07to08 );
        assert channel.getProgrammes().size() == 3;
        it = channel.getProgrammes().iterator();
        assert it.next() == prog06to07;
        assert it.next() == prog07to08;
        assert it.next() == prog08to10again;

        TVProgramme prog0630to0730 = new TVProgramme();
        prog0630to0730.setStart( getLongTime(  6, 30 ) );
        prog0630to0730.setEnd(   getLongTime(  7, 30 ) );

        // Adding another that overlaps 2 programmes replaces them both
        channel.put( prog0630to0730 );
        assert channel.getProgrammes().size() == 2;
        it = channel.getProgrammes().iterator();

        assert it.next() == prog0630to0730;
        assert it.next() == prog08to10again;
    }

    /**
     * Tests that the normalizeTime method fills in correct end
     * times when they are missing.
     *
     */
    private void testNoEndTimes()
    {
        TVChannel channel = new TVChannel( "channel_id" );

        TVProgramme prog06to07 = new TVProgramme();
        prog06to07.setStart( getLongTime(  6,  0 ) );
        prog06to07.setEnd( getLongTime(  7,  0 ) );
        channel.put( prog06to07 );

        TVProgramme prog08toXX = new TVProgramme();
        prog08toXX.setStart( getLongTime(  8,  0 ) );
        channel.put( prog08toXX );

        TVProgramme prog09toXX = new TVProgramme();
        prog09toXX.setStart( getLongTime(  9, 00 ) );
        channel.put( prog09toXX );

        TVProgramme prog0830toXX = new TVProgramme();
        prog0830toXX.setStart( getLongTime(  8, 30 ) );
        channel.put( prog0830toXX );

        channel.normalizeTime();

        // Check the programme with an end time wasn't touched
        assert prog06to07.getEnd()   == getLongTime(  7, 30 );

        // But the two without were given best guesses
        assert prog08toXX.getEnd()   == getLongTime(  8, 30 );
        assert prog0830toXX.getEnd() == getLongTime(  9, 00 );
    }

    private long getLongTime(int hour, int minute)
    {
        return new GregorianCalendar( 2008, 06, 06, hour, minute ).getTimeInMillis();
    }

}

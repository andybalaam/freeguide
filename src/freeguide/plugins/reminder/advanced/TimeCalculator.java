package freeguide.plugins.reminder.advanced;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class TimeCalculator
{
    // max length is 6h
    protected static long MAX_PROGRAMME_LENGTH = 6L * 60L * 60L * 1000L;
    long maxTimeBeforeStart = 0;
    long maxTimeAfterStart = 0;
    long maxTimeBeforeStop = 0;
    long maxTimeAfterStop = 0;

    /**
     * DOCUMENT ME!
     */
    public Map<Long, List<Event>> events = new TreeMap<Long, List<Event>>(  );

    /**
     * DOCUMENT_ME!
     *
     * @param config DOCUMENT_ME!
     * @param fromTime DOCUMENT_ME!
     * @param toTime DOCUMENT_ME!
     */
    public void schedule( 
        final AdvancedReminder.Config config, final long fromTime,
        final long toTime )
    {
        // detect max and min storage time
        maxTimeBeforeStart = 0;
        maxTimeAfterStart = 0;
        maxTimeBeforeStop = 0;
        maxTimeAfterStop = 0;

        // calculate minimum and maximum time
        for( final AdvancedReminder.OneReminderConfig oneConfig : config.reminders )
        {
            if( oneConfig.isPopup )
            {
                eventStartProgramme( oneConfig.popupOpenTime );
                eventStartProgramme( oneConfig.popupCloseTime );
            }

            if( oneConfig.isSound )
            {
                eventStartProgramme( oneConfig.soundPlayTime );
            }

            if( oneConfig.isExecute )
            {
                if( 
                    ( oneConfig.executeStartCommand != null )
                        && ( oneConfig.executeStartCommand.length(  ) > 0 ) )
                {
                    eventStartProgramme( oneConfig.executeStartTime );
                }

                if( 
                    ( oneConfig.executeStopCommand != null )
                        && ( oneConfig.executeStopCommand.length(  ) > 0 ) )
                {
                    eventStopProgramme( 
                        oneConfig.executeStopTimeOnFinishProgramme );
                }
            }
        }

        long minStorageTime =
            fromTime
            + Math.min( 
                maxTimeBeforeStart,
                Math.min( 
                    maxTimeAfterStart,
                    Math.min( 
                        -MAX_PROGRAMME_LENGTH + maxTimeBeforeStop,
                        -MAX_PROGRAMME_LENGTH + maxTimeAfterStop ) ) );
        long maxStorageTime =
            fromTime
            + Math.max( 
                maxTimeBeforeStart,
                Math.max( 
                    maxTimeAfterStart,
                    Math.max( 
                        -MAX_PROGRAMME_LENGTH + maxTimeBeforeStop,
                        -MAX_PROGRAMME_LENGTH + maxTimeAfterStop ) ) );

    }

    protected void eventStartProgramme( final long offset )
    {
        maxTimeBeforeStart = Math.min( offset, maxTimeBeforeStart );
        maxTimeAfterStart = Math.max( offset, maxTimeAfterStart );
    }

    protected void eventStopProgramme( final long offset )
    {
        maxTimeBeforeStop = Math.min( offset, maxTimeBeforeStop );
        maxTimeAfterStop = Math.max( offset, maxTimeAfterStop );
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class Event
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class EventPopupShow
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class EventPopupHide
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class EventSoundPlay
    {
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class EventExecute
    {
    }
}

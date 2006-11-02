package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;

import freeguide.common.plugininterfaces.IModuleStorage;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Processor for create event queue by schedule, modify queue "on flight".
 *
 * @author Alex Buloichik
 */
public class ScheduleProcessor
{
    // max length of programme is 6 hours
    protected static long MAX_PROGRAMME_LENGTH = 6L * 60L * 60L * 1000L;
    protected long maxTimeBeforeStart = 0;
    protected long maxTimeAfterStart = 0;
    protected long maxTimeBeforeStop = 0;
    protected long maxTimeAfterStop = 0;
    protected final AdvancedReminder.Config config;
    protected final long fromTime;
    protected final long toTime;
    protected final Map<String, AdvancedReminder.OneReminderConfig> reminderConfigs =
        new TreeMap<String, AdvancedReminder.OneReminderConfig>(  );

/**
     * Creates a new ScheduleProcessor object.
     * 
     * @param config
     *            DOCUMENT ME!
     * @param fromTime
     *            DOCUMENT ME!
     * @param toTime
     *            DOCUMENT ME!
     */
    public ScheduleProcessor( 
        final AdvancedReminder.Config config, final long fromTime,
        final long toTime )
    {
        this.config = config;
        this.fromTime = fromTime;
        this.toTime = toTime;

        for( final AdvancedReminder.OneReminderConfig rc : config.reminders )
        {
            reminderConfigs.put( rc.name, rc );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public List<Event> createQueue(  ) throws Exception
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

        final IModuleStorage.Info info = new IModuleStorage.Info(  );
        info.minDate = minStorageTime;
        info.maxDate = maxStorageTime;

        return createQueueByStorage( info );
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

    protected List<Event> createQueueByStorage( 
        final IModuleStorage.Info info ) throws Exception
    {
        final List<Event> result = new ArrayList<Event>(  );

        final TVData data =
            Application.getInstance(  ).getDataStorage(  ).get( info );

        for( final OneReminderConfig rem : config.reminders )
        {
            data.iterateProgrammes( 
                new TVIteratorProgrammes(  )
                {
                    protected void onChannel( TVChannel channel )
                    {
                    }

                    protected void onProgramme( TVProgramme programme )
                    {
                        boolean selected = false;

                        for( final Favourite fav : config.favouritesList )
                        {
                            if( 
                                fav.reminders.contains( rem.name )
                                    && fav.matches( programme ) )
                            {
                                selected = true;
                            }
                        }

                        for( final ManualSelection sel : config.manualSelectionList )
                        {
                            if( 
                                sel.matches( programme )
                                    && sel.reminders.containsKey( rem.name ) )
                            {
                                selected = sel.reminders.get( rem.name )
                                                        .booleanValue(  );
                            }
                        }

                        if( selected )
                        {
                            addToQueue( result, programme, rem.name );
                        }
                    }
                } );
        }

        Collections.sort( 
            result,
            new Comparator<Event>(  )
            {
                public int compare( Event e1, Event e2 )
                {
                    return (int)( e1.time - e2.time );
                }
            } );

        return result;
    }

    protected void addToQueue( 
        final List<Event> queue, final TVProgramme programme,
        final String reminderName )
    {
        if( reminderName == null )
        {
            // there is no such reminder
            return;
        }

        final AdvancedReminder.OneReminderConfig remConfig =
            reminderConfigs.get( reminderName );

        if( remConfig == null )
        {
            // there is no such reminder
            return;
        }

        if( remConfig.isPopup )
        {
            final long popupStart =
                programme.getStart(  ) + remConfig.popupOpenTime;
            final long popupStop =
                programme.getStart(  ) + remConfig.popupCloseTime;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Event
    {
        /** DOCUMENT ME! */
        public long time;
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

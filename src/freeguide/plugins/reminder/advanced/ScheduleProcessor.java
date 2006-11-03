package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;
import freeguide.common.lib.general.Utils;

import freeguide.common.plugininterfaces.IModuleStorage;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import sun.audio.AudioPlayer;

import java.io.FileInputStream;
import java.io.IOException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Processor for create event queue by schedule, modify queue "on flight".
 *
 * @author Alex Buloichik
 */
public class ScheduleProcessor
{
    // max length of programme is 6 hours
    protected static long MAX_PROGRAMME_LENGTH = 6L * 60L * 60L * 1000L;
    protected final AdvancedReminder parent;

    /** Time which need to discover. */
    protected final long fromTime;

    /** Time which need to discover. */
    protected final long toTime;

    /** Time interval in storage where programme can be saved. */
    protected long storageFromTime;

    /** Time interval in storage where programme can be saved. */
    protected long storageToTime;

    /** Config. */
    protected final Map<String, AdvancedReminder.OneReminderConfig> reminderConfigs =
        new TreeMap<String, AdvancedReminder.OneReminderConfig>(  );

/**
     * Creates a new ScheduleProcessor object.
     * 
     * @param config
     *            config
     * @param fromTime
     *            start time for discover
     * @param toTime
     *            end time for discover
     */
    public ScheduleProcessor( 
        final AdvancedReminder parent, final long fromTime, final long toTime )
    {
        this.parent = parent;
        this.fromTime = fromTime;
        this.toTime = toTime;

        for( final AdvancedReminder.OneReminderConfig rc : parent.config.reminders )
        {
            reminderConfigs.put( rc.name, rc );
        }
    }

    /**
     * Create queue of tasks.
     *
     * @return queue
     *
     * @throws Exception exception
     */
    public List<Task> createQueue(  ) throws Exception
    {
        if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
        {
            AdvancedReminder.LOG.log( 
                Level.INFO,
                "Creating queue from " + new Date( fromTime ) + " to "
                + new Date( toTime ) );
        }

        storageFromTime = Long.MAX_VALUE;
        storageToTime = Long.MIN_VALUE;

        // calculate time interval in storage where programme can be saved
        for( final AdvancedReminder.OneReminderConfig oneConfig : parent.config.reminders )
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

        if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
        {
            AdvancedReminder.LOG.log( 
                Level.INFO,
                "  process storage from " + new Date( storageFromTime )
                + " to " + new Date( storageToTime ) );
        }

        final IModuleStorage.Info info = new IModuleStorage.Info(  );
        info.minDate = storageFromTime;
        info.maxDate = storageToTime;
        info.channelsList = null;

        return createQueueByStorage( info );
    }

    /**
     * Calculate start,stop using start programme time offset.
     *
     * @param offset offset
     */
    protected void eventStartProgramme( final long offset )
    {
        if( ( fromTime - offset ) < storageFromTime )
        {
            storageFromTime = fromTime - offset;
        }

        if( ( toTime - offset ) > storageToTime )
        {
            storageToTime = toTime - offset;
        }
    }

    /**
     * Calculate start,stop using end programme time offset.
     *
     * @param offset offset
     */
    protected void eventStopProgramme( final long offset )
    {
        if( ( fromTime - MAX_PROGRAMME_LENGTH - offset ) < storageFromTime )
        {
            storageFromTime = fromTime - MAX_PROGRAMME_LENGTH - offset;
        }

        if( ( toTime - MAX_PROGRAMME_LENGTH - offset ) > storageToTime )
        {
            storageToTime = toTime - MAX_PROGRAMME_LENGTH - offset;
        }
    }

    /**
     * Create queue by calculated part of storage.
     *
     * @param info part description
     *
     * @return queue
     *
     * @throws Exception exception
     */
    protected List<Task> createQueueByStorage( final IModuleStorage.Info info )
        throws Exception
    {
        final List<Task> result = new ArrayList<Task>(  );

        final TVData data =
            Application.getInstance(  ).getDataStorage(  ).get( info );

        for( final OneReminderConfig rem : parent.config.reminders )
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

                        for( final Favourite fav : parent.config.favouritesList )
                        {
                            if( 
                                fav.reminders.contains( rem.name )
                                    && fav.matches( programme ) )
                            {
                                selected = true;
                            }
                        }

                        for( final ManualSelection sel : parent.config.manualSelectionList )
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

        return result;
    }

    /**
     * Add selected or favourite programme's tasks to queue.
     *
     * @param queue queue
     * @param programme programme
     * @param reminderName reminder which need to discover for tasks
     */
    protected void addToQueue( 
        final List<Task> queue, final TVProgramme programme,
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

        final TaskInfo taskInfo =
            new TaskInfo( parent, reminderName, programme );

        if( remConfig.isPopup )
        {
            final long showTime =
                programme.getStart(  ) + remConfig.popupOpenTime;
            final long hideTime =
                programme.getStart(  ) + remConfig.popupCloseTime;

            if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
            {
                AdvancedReminder.LOG.log( 
                    Level.INFO,
                    "Task: popup from " + new Date( showTime ) + " to "
                    + new Date( hideTime ) + " for programme: "
                    + programme.getTitle(  ) );
            }

            queue.add( new TaskPopup( taskInfo, showTime, hideTime ) );
        }

        if( remConfig.isSound )
        {
            final long playTime =
                programme.getStart(  ) + remConfig.soundPlayTime;

            if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
            {
                AdvancedReminder.LOG.log( 
                    Level.INFO,
                    "Task: sound on " + new Date( playTime )
                    + " for programme: " + programme.getTitle(  ) );
            }

            queue.add( 
                new TaskSound( taskInfo, playTime, remConfig.soundFile ) );
        }

        if( remConfig.isExecute )
        {
            final long execTime =
                programme.getStart(  ) + remConfig.executeStartTime;
            final long stopTime =
                programme.getEnd(  )
                + remConfig.executeStopTimeOnFinishProgramme;

            if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
            {
                AdvancedReminder.LOG.log( 
                    Level.INFO,
                    "Task: execute from " + new Date( execTime ) + " to "
                    + new Date( stopTime ) + " for programme: "
                    + programme.getTitle(  ) );
            }

            queue.add( 
                new TaskExecute( 
                    taskInfo, execTime, stopTime, remConfig.executeStartCommand,
                    remConfig.executeStopCommand ) );
        }
    }

    /**
     * Information about task scheduled programme. If Task class type
     * and this info equals, then tasks are equals.
     */
    public static class TaskInfo
    {
        protected final AdvancedReminder parent;
        protected final String reminderName;
        protected final String channelID;
        protected final long progStartTime;
        protected final String progTitle;

        /**
         * Creates a new TaskInfo object.
         *
         * @param parent DOCUMENT ME!
         * @param reminderName DOCUMENT ME!
         * @param prog DOCUMENT ME!
         */
        public TaskInfo( 
            final AdvancedReminder parent, final String reminderName,
            final TVProgramme prog )
        {
            this.parent = parent;
            this.reminderName = reminderName;
            channelID = prog.getChannel(  ).getID(  );
            progStartTime = prog.getStart(  );
            progTitle = prog.getTitle(  );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param obj DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean equals( Object obj )
        {
            if( obj instanceof TaskInfo )
            {
                final TaskInfo other = (TaskInfo)obj;

                return ( progStartTime == other.progStartTime )
                && channelID.equals( other.channelID )
                && reminderName.equals( other.reminderName );
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Base task.
     */
    public static abstract class Task
    {
        protected final TaskInfo taskInfo;
        protected final long startTime;
        protected final long stopTime;
        protected boolean isStarted = false;

        /**
         * Creates a new Task object.
         *
         * @param taskInfo DOCUMENT ME!
         * @param startTime DOCUMENT ME!
         * @param stopTime DOCUMENT ME!
         */
        public Task( 
            final TaskInfo taskInfo, final long startTime, final long stopTime )
        {
            this.taskInfo = taskInfo;
            this.startTime = startTime;
            // stop time can be only after start time
            this.stopTime = Math.max( stopTime, startTime );
        }

        /**
         * DOCUMENT_ME!
         */
        abstract public void start(  );

        /**
         * DOCUMENT_ME!
         */
        abstract public void stop(  );

        /**
         * DOCUMENT_ME!
         *
         * @param oldTask DOCUMENT_ME!
         */
        abstract public void loadFrom( final Task oldTask );

        /**
         * DOCUMENT_ME!
         *
         * @param obj DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean equals( Object obj )
        {
            if( obj instanceof Task )
            {
                final Task other = (Task)obj;

                return taskInfo.equals( other.taskInfo )
                && getClass(  ).equals( other.getClass(  ) );
            }
            else
            {
                return false;
            }
        }
    }

    /**
     * Task for show and hide popup.
     */
    public static class TaskPopup extends Task
    {
        protected JDialog scheduledDialog;

        /**
         * Creates a new TaskPopup object.
         *
         * @param taskInfo DOCUMENT ME!
         * @param showTime DOCUMENT ME!
         * @param hideTime DOCUMENT ME!
         */
        public TaskPopup( 
            final TaskInfo taskInfo, final long showTime, final long hideTime )
        {
            super( taskInfo, showTime, hideTime );
        }

        /**
         * DOCUMENT_ME!
         */
        public void start(  )
        {
            isStarted = true;

            if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
            {
                AdvancedReminder.LOG.log( 
                    Level.INFO, "Task execution: show dialog" );
            }

            String message =
                MessageFormat.format( 
                    taskInfo.parent.getLocalizer(  ).getString( "alarm.text" ),
                    new Object[] { taskInfo.progTitle } );

            JOptionPane optionPane =
                new JOptionPane( message, JOptionPane.INFORMATION_MESSAGE );

            scheduledDialog = optionPane.createDialog( 
                    Application.getInstance(  ).getApplicationFrame(  ),
                    taskInfo.parent.getLocalizer(  ).getString( "alarm.title" ) );

            scheduledDialog.setModal( false );

            SwingUtilities.invokeLater( 
                new Runnable(  )
                {
                    public void run(  )
                    {
                        scheduledDialog.setVisible( true );
                    }
                } );
        }

        /**
         * DOCUMENT_ME!
         */
        public void stop(  )
        {
            if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
            {
                AdvancedReminder.LOG.log( 
                    Level.INFO, "Task execution: hide dialog" );
            }

            SwingUtilities.invokeLater( 
                new Runnable(  )
                {
                    public void run(  )
                    {
                        scheduledDialog.dispose(  );
                    }
                } );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param oldTask DOCUMENT_ME!
         */
        public void loadFrom( final Task oldTask )
        {
            isStarted = oldTask.isStarted;
            scheduledDialog = ( (TaskPopup)oldTask ).scheduledDialog;
        }
    }

    /**
     * Task for play sound.
     */
    public static class TaskSound extends Task
    {
        protected final String file;

        /**
         * Creates a new TaskSound object.
         *
         * @param taskInfo DOCUMENT ME!
         * @param playTime DOCUMENT ME!
         * @param file DOCUMENT ME!
         */
        public TaskSound( 
            final TaskInfo taskInfo, final long playTime, final String file )
        {
            super( taskInfo, playTime, playTime );
            this.file = file;
        }

        /**
         * DOCUMENT_ME!
         */
        public void start(  )
        {
            isStarted = true;

            if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
            {
                AdvancedReminder.LOG.log( 
                    Level.INFO, "Task execution: play sound: " + file );
            }

            try
            {
                AudioPlayer.player.start( new FileInputStream( file ) );
            }
            catch( IOException ex )
            {
                AdvancedReminder.LOG.warning( 
                    "Error play sound '" + file + "': " + ex.getMessage(  ) );
            }
        }

        /**
         * DOCUMENT_ME!
         */
        public void stop(  )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param oldTask DOCUMENT_ME!
         */
        public void loadFrom( final Task oldTask )
        {
            isStarted = oldTask.isStarted;
        }
    }

    /**
     * Task for execute command.
     */
    public static class TaskExecute extends Task
    {
        protected final String startCommand;
        protected final String stopCommand;

        /**
         * Creates a new TaskExecute object.
         *
         * @param taskInfo DOCUMENT ME!
         * @param execTime DOCUMENT ME!
         * @param stopTime DOCUMENT ME!
         * @param startCommand DOCUMENT ME!
         * @param stopCommand DOCUMENT ME!
         */
        public TaskExecute( 
            final TaskInfo taskInfo, final long execTime, final long stopTime,
            final String startCommand, final String stopCommand )
        {
            super( taskInfo, execTime, stopTime );
            this.startCommand = startCommand;
            this.stopCommand = stopCommand;
        }

        /**
         * DOCUMENT_ME!
         */
        public void start(  )
        {
            isStarted = true;
            execute( startCommand );
        }

        /**
         * DOCUMENT_ME!
         */
        public void stop(  )
        {
            execute( stopCommand );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param oldTask DOCUMENT_ME!
         */
        public void loadFrom( final Task oldTask )
        {
            isStarted = oldTask.isStarted;
        }

        protected void execute( final String command )
        {
            if( ( command != null ) && ( command.trim(  ).length(  ) > 0 ) )
            {
                if( AdvancedReminder.LOG.isLoggable( Level.INFO ) )
                {
                    AdvancedReminder.LOG.log( 
                        Level.INFO,
                        "Task execution: execute command: " + command );
                }

                try
                {
                    Runtime.getRuntime(  ).exec( 
                        Utils.parseCommand( command ) );
                }
                catch( IOException ex )
                {
                    AdvancedReminder.LOG.warning( 
                        "Error execute '" + command + "': "
                        + ex.getMessage(  ) );
                }
            }
        }
    }
}

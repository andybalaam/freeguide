package freeguide.plugins.reminder.advanced;

import freeguide.plugins.reminder.advanced.ScheduleProcessor.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Schedule thread implementation.
 *
 * @author Alex Buloichik
 */
public class SchedulerThread extends Thread
{
    protected static final int STATE_WORK = 0;
    protected static final int STATE_RESCHEDULE = 1;
    protected static final int STATE_STOP = 99;
    protected static final long MIN_SLEEP_TIME = 10L;
    protected static final long MAX_SLEEP_TIME = 60L * 1000L;
    protected int state = STATE_RESCHEDULE;
    protected long scheduledTime;
    protected final AdvancedReminder parent;

    /** Queue of tasks. */
    protected final List<Task> queue = new ArrayList<Task>(  );

/**
     * Creates a new SchedulerThread object.
     * 
     * @param parent
     *            DOCUMENT ME!
     */
    public SchedulerThread( final AdvancedReminder parent )
    {
        this.parent = parent;
    }

    /**
     * Thread runner.
     */
    public void run(  )
    {
        synchronized( this )
        {
            try
            {
                while( true )
                {
                    if( state == STATE_STOP )
                    {
                        return;
                    }

                    if( state == STATE_RESCHEDULE )
                    {
                        try
                        {
                            synchronized( parent.config )
                            {
                                mergeQueues( 
                                    new ScheduleProcessor( 
                                        parent, System.currentTimeMillis(  ),
                                        System.currentTimeMillis(  )
                                        + ( 60 * 60 * 1000 ) ).createQueue(  ) );
                            }
                        }
                        catch( Exception ex )
                        {
                            AdvancedReminder.LOG.log( 
                                Level.WARNING, "Error creating schedule queue",
                                ex );
                        }

                        state = STATE_WORK;
                    }

                    if( state == STATE_WORK )
                    {
                        processQueue(  );
                    }

                    scheduledTime = getNextTime(  );

                    long waitTime =
                        scheduledTime - System.currentTimeMillis(  );

                    if( waitTime < MIN_SLEEP_TIME )
                    {
                        waitTime = MIN_SLEEP_TIME;
                    }

                    if( waitTime > MAX_SLEEP_TIME )
                    {
                        waitTime = MAX_SLEEP_TIME;
                    }

                    wait( waitTime );
                }
            }
            catch( InterruptedException ex )
            {
                AdvancedReminder.LOG.log( 
                    Level.WARNING, "Reminder thread interrupted ", ex );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void reschedule(  )
    {
        synchronized( this )
        {
            state = STATE_RESCHEDULE;
            notify(  );
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void finish(  )
    {
        synchronized( this )
        {
            state = STATE_STOP;
            notifyAll(  );
        }

        try
        {
            join(  );
        }
        catch( InterruptedException ex )
        {
        }
    }

    /**
     * Process tasks in queue.
     */
    protected void processQueue(  )
    {
        AdvancedReminder.LOG.log( Level.INFO, "Process reminder queue" );

        final long nowProcessedTime = System.currentTimeMillis(  );

        for( final Iterator<Task> it = queue.iterator(  ); it.hasNext(  ); )
        {
            final Task task = it.next(  );

            if( task.startTime <= nowProcessedTime )
            {
                task.start(  );
            }

            if( task.stopTime <= nowProcessedTime )
            {
                task.stop(  );
            }

            if( task.taskState == Task.STATE.FINISHED )
            {
                it.remove(  );
            }
        }
    }

    /**
     * Find next time when task should be made.
     *
     * @return time
     */
    protected long getNextTime(  )
    {
        long nextTime = Long.MAX_VALUE;

        for( final Task task : queue )
        {
            if( 
                ( task.taskState == Task.STATE.NOT_STARTED )
                    && ( task.startTime < nextTime ) )
            {
                nextTime = task.startTime;
            }

            if( 
                ( task.taskState == Task.STATE.RUNNING )
                    && ( task.stopTime < nextTime ) )
            {
                nextTime = task.stopTime;
            }
        }

        return nextTime;
    }

    /**
     * Merge new queue with old queue.
     *
     * @param newQueue new queue
     */
    protected void mergeQueues( final List<Task> newQueue )
    {
        for( final Task newTask : newQueue )
        {
            for( final Iterator<Task> it = queue.iterator(  ); it.hasNext(  ); )
            {
                final Task oldTask = it.next(  );

                if( newTask.equals( oldTask ) )
                {
                    newTask.loadFrom( oldTask );
                    it.remove(  );
                }
            }
        }

        for( final Task oldTask : queue )
        {
            oldTask.stop(  );
        }

        queue.clear(  );
        queue.addAll( newQueue );
    }
}

package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.Application;

import java.util.logging.Level;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class SchedulerThread extends Thread
{
    protected static final int STATE_WORK = 0;
    protected static final int STATE_RESCHEDULE = 1;
    protected static final int STATE_STOP = 99;
    protected int state = STATE_RESCHEDULE;
    protected long scheduledTime;

    /**
     * DOCUMENT_ME!
     */
    public void run(  )
    {
        synchronized( this )
        {
            try
            {
                while( true )
                {
                    switch( state )
                    {
                    case STATE_WORK:

                        //  onTime();
                        //    scheduledTime = getNextTime();
                        break;

                    case STATE_RESCHEDULE:
                        //  scheduledTime = getNextTime();
                        state = STATE_WORK;

                        break;

                    case STATE_STOP:
                        return;
                    }

                    long waitTime =
                        Math.max( 
                            scheduledTime - System.currentTimeMillis(  ), 10 );

                    wait( waitTime );
                }
            }
            catch( InterruptedException ex )
            {
                Application.getInstance(  ).getLogger(  )
                           .log( 
                    Level.WARNING, "Reminder thread interrupted ", ex );
            }
        }
    }

    protected void reschedule(  )
    {
        synchronized( this )
        {
            state = STATE_RESCHEDULE;
            notify(  );
        }
    }

    protected void finish(  )
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
}

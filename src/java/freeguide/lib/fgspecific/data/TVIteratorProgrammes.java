package freeguide.lib.fgspecific.data;

/**
 * Iterator for TV programmes.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
abstract public class TVIteratorProgrammes extends TVIterator
{

    boolean needToIterateChannel = false;
    TVChannel currentChannel;

    abstract protected void onChannel( final TVChannel channel );

    abstract protected void onProgramme( final TVProgramme programme );

    protected TVChannel getCurrentChannel(  )
    {

        return currentChannel;
    }

    protected void stopIterateChanel(  )
    {
        needToIterateChannel = false;
    }
}

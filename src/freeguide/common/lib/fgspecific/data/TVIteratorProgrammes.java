package freeguide.common.lib.fgspecific.data;

import java.util.Iterator;

/**
 * Iterator for TV programmes.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
abstract public class TVIteratorProgrammes extends TVIterator
{
    boolean needToIterateChannel = false;
    TVChannel currentChannel;
    protected Iterator itChannels;
    protected Iterator itProgrammes;

    abstract protected void onChannel( final TVChannel channel );

    abstract protected void onProgramme( final TVProgramme programme );

    protected void onChannelFinish(  )
    {
    }

    protected TVChannel getCurrentChannel(  )
    {
        return currentChannel;
    }

    protected void stopIterateChanel(  )
    {
        needToIterateChannel = false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the itChannels.
     */
    public Iterator getIteratorChannels(  )
    {
        return itChannels;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the itProgrammes.
     */
    public Iterator getIteratorProgrammes(  )
    {
        return itProgrammes;
    }
}

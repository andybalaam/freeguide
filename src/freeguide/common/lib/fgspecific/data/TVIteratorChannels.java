package freeguide.common.lib.fgspecific.data;

import java.util.Iterator;

/**
 * Iterator for TV channels.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
abstract public class TVIteratorChannels extends TVIterator
{

    protected Iterator it;

    abstract protected void onChannel( final TVChannel channel );

    /**
     * DOCUMENT ME!
     *
     * @return Returns the iterator.
     */
    public Iterator getIterator(  )
    {

        return it;
    }
}

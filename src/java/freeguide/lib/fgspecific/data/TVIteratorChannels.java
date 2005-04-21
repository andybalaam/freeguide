package freeguide.lib.fgspecific.data;

/**
 * Iterator for TV channels.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
abstract public class TVIteratorChannels extends TVIterator
{
    abstract protected void onChannel( final TVChannel channel );
}

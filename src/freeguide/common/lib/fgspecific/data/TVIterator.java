package freeguide.common.lib.fgspecific.data;

/**
 * Base class for iterators.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
abstract public class TVIterator
{
    private boolean contIt = true;

    public boolean continueIterating()
    {
        return contIt;
    }

    public void stopIterating()
    {
        contIt = false;
    }
}

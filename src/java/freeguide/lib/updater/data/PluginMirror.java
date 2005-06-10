package freeguide.lib.updater.data;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PluginMirror
{

    protected String location;
    protected String path;

    /**
     * Creates a new PluginMirror object.
     *
     * @param location DOCUMENT ME!
     * @param path DOCUMENT ME!
     */
    public PluginMirror( final String location, final String path )
    {
        this.location = location;
        this.path = path;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getLocation(  )
    {

        return location;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getPath(  )
    {

        return path;
    }
}

package freeguide.common.lib.fgspecific;

import freeguide.common.base.Version;

import freeguide.common.plugininterfaces.IApplication;

/**
 * Class for retrieve information about main application.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public class Application
{
    /** Application ID. */
    public static final String ID = "freeguide";

    /** Application version. */
    public static final Version VERSION =
        new Version(  /*VER_BEG*/
            0, 11, 1 /*VER_END*/ );
    protected static IApplication instance;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IApplication getInstance(  )
    {
        return instance;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param app DOCUMENT_ME!
     */
    public static void setInstance( final IApplication app )
    {
        instance = app;
    }
}

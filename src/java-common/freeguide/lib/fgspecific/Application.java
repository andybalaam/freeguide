package freeguide.lib.fgspecific;

import freeguide.plugins.IApplication;

/**
 * Class for retrieve information about main application.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public class Application
{

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

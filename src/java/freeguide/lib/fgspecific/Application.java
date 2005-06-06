package freeguide.lib.fgspecific;

import freeguide.gui.viewer.MainController;

import freeguide.plugins.IApplication;

/**
 * Class for retrieve information about main application.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public class Application
{

    protected final static MainController instance = new MainController(  );

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
     * @return DOCUMENT_ME!
     */
    public static MainController getMainController(  )
    {

        return instance;
    }
}

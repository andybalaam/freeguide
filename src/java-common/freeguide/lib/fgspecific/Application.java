package freeguide.lib.fgspecific;

import freeguide.lib.general.Version;

import freeguide.plugins.IApplication;
import freeguide.plugins.IModule;
import freeguide.plugins.IModuleConfigurationUI;

import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JDialog;

/**
 * Class for retrieve information about main application.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public class Application
{

    /** Application ID. */
    public static final String ID = "FreeGuide";

    /** Application version. */
    public static final Version VERSION = new Version( 0, 10, 2 );
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

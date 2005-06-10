package freeguide.lib.fgspecific;

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

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModule getApplicationModule(  )
    {

        return new IModule(  )
            {
                public String getID(  )
                {

                    return ID;
                }

                public String getName(  )
                {

                    return "FreeGuide";
                }

                public String getDescription(  )
                {

                    return "FreeGuide description";
                }

                public Locale[] getSuppotedLocales(  )
                    throws Exception
                {

                    return new Locale[0];
                }

                public void setConfigStorage( Preferences prefs )
                {
                }

                public void setLocale( Locale locale )
                    throws Exception
                {
                }

                public IModuleConfigurationUI getConfigurationUI( 
                    JDialog parentDialog )
                {

                    return null;
                }
            };
    }
}

package freeguide.plugins;

import freeguide.lib.general.Version;

import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JDialog;

/**
 * Basic interface for all modules for freeguide-tv.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModule
{

    /**
     * Get module ID.  It is internal name for module, which used for find
     * module in repository. Must be english word. This method called BEFORE
     * setLocale. ID of module should be the same for different locales and
     * for different versions.
     *
     * @return module ID
     */
    String getID(  );

    /**
     * Get module version.
     *
     * @return version
     */
    Version getVersion(  );

    /**
     * Get supported locales list. This method called BEFORE setLocale. Each
     * module MUST support "en" locale.
     *
     * @return List of supported locales
     *
     * @throws Exception
     */
    Locale[] getSuppotedLocales(  ) throws Exception;

    /**
     * Set locale for module.
     *
     * @param locale locale. Can be null.
     *
     * @throws Exception
     */
    void setLocale( Locale locale ) throws Exception;

    /**
     * Get module name.  Module should return name using current locale. This
     * method called AFTER setLocale.
     *
     * @return module name
     */
    String getName(  );

    /**
     * Get module description.  Module should return description using current
     * locale. This method called AFTER setLocale.
     *
     * @return module description
     */
    String getDescription(  );

    /**
     * Set Preferences for load and store module's parameters.
     *
     * @param prefs Preferences
     */
    void setConfigStorage( Preferences prefs );

    IModuleConfigurationUI getConfigurationUI( JDialog parentDialog );
}

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
     * Set Preferences for load and store module's parameters.
     *
     * @param prefs Preferences
     */
    void setConfigStorage( Preferences prefs );

    IModuleConfigurationUI getConfigurationUI( JDialog parentDialog );
}

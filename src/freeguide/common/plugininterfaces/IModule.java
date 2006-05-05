package freeguide.common.plugininterfaces;

import java.util.Locale;

import javax.swing.JDialog;

/**
 * Basic interface for all modules for freeguide-tv.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModule
{
    /**
     * Get supported locales list. This method called BEFORE
     * setLocale. Each module MUST support "en" locale.
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
     * Get config for store and load.
     *
     * @return config object
     */
    Object getConfig(  );

    /**
     * Get configuration interface for support Option panel.
     *
     * @param parentDialog
     *
     * @return
     */
    IModuleConfigurationUI getConfigurationUI( JDialog parentDialog );
}

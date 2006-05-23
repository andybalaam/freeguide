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
     * Used to find the filename of the file in resources/i18n that
     * contains translations for this plugin.
     *
     * @return the String package of this plugin with the freeguide.plugins
     *         part removed and each '.' replaced with '_'.
     */
    public String getI18nName(  );

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
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public ILocalizer getLocalizer(  );

    /**
     * Get configuration interface for support Option panel.
     *
     * @param parentDialog
     *
     * @return
     */
    IModuleConfigurationUI getConfigurationUI( JDialog parentDialog );
}

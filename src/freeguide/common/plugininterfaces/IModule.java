package freeguide.common.plugininterfaces;

import javax.swing.JDialog;

/**
 * Basic interface for all modules for freeguide-tv.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModule
{
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
     * @throws Exception
     */
    void reloadResourceBundle(  ) throws Exception;

    /**
     * Get config for store and load.
     *
     * @return config object
     */
    Object getConfig(  );

    /**
     * Returns the language helper that has methods to translate
     * strings
     *
     * @return Localizer object
     */
    public ILocalizer getLocalizer(  );

    /**
     * Get configuration interface for support Option panel.
     *
     * @param parentDialog
     *
     * @return configuration
     */
    IModuleConfigurationUI getConfigurationUI( JDialog parentDialog );
}

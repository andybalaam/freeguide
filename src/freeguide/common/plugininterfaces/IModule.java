package freeguide.common.plugininterfaces;

import java.util.ResourceBundle;

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
     * Get resource bundle for module.
     *
     * @return resource bundle
     */
    ResourceBundle getLocalizer(  );

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
     * @return configuration
     */
    IModuleConfigurationUI getConfigurationUI( JDialog parentDialog );
}

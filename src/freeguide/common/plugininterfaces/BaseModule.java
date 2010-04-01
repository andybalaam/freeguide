package freeguide.common.plugininterfaces;

import freeguide.common.base.IModule;
import freeguide.common.base.IModuleConfigurationUI;
import freeguide.common.lib.fgspecific.Application;

import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.JDialog;

/**
 * Base class for simple modules. It implements i18n support and getName,
 * getDescription methods, based on "Name" and "Description" properties in
 * i18n files. Any module can extends this class for use basic module
 * functionality.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public abstract class BaseModule implements IModule
{
    protected static final String plugin_package_name_prefix =
        "freeguide.plugins.";
    protected static final String RESOURCES_PREFIX = "resources/i18n/";
    protected ResourceBundle i18n;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getI18nName(  )
    {
        String ret =
            getClass(  ).getName(  )
                .substring( plugin_package_name_prefix.length(  ) );

        int i = ret.lastIndexOf( '.' );

        if( i != -1 )
        {
            ret = ret.substring( 0, i );
        }

        ret = ret.replace( '.', '_' );

        return ret;
    }

    /**
     * IModule.setLocale implementation. Loads
     * i18n.(locale).properties file from current package to i18n variable.
     *
     * @throws Exception
     */
    public void reloadResourceBundle(  ) throws Exception
    {
        String package_name = getClass(  ).getPackage(  ).getName(  );

        if( package_name.startsWith( plugin_package_name_prefix ) )
        {
            final String bundleName =
                RESOURCES_PREFIX
                + package_name.substring(
                    plugin_package_name_prefix.length(  ) ).replace( '.', '_' );

            i18n = ResourceBundle.getBundle( bundleName );
        }
        else
        {
            Application.getInstance(  ).getLogger(  )
                       .log(
                Level.SEVERE,
                "Unable to set Locale for plugin '" + package_name
                + "' since the package name does not start with '"
                + plugin_package_name_prefix + "'." );
        }
    }

    protected void saveConfigNow(  )
    {
        Application.getInstance(  ).saveAllConfigs(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public ResourceBundle getLocalizer(  )
    {
        return i18n;
    }
}

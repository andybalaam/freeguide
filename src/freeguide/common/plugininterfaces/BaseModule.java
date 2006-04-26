package freeguide.common.plugininterfaces;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.lib.general.LanguageHelper;

import java.util.Locale;
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
    static final String plugin_package_name_prefix = "freeguide.plugins.";
    protected LanguageHelper i18n;
    
    /**
     * IModule.getSuppotedLocales implementation. Read list of
     * "i18n.(locale).properties" files from current package, using ls file.
     *
     * @return List of supported locales.
     *
     * @throws Exception
     */
    public Locale[] getSuppotedLocales(  ) throws Exception
    {
        return LanguageHelper.getLocaleList( "resources/i18n/" );
    }

    /**
     * IModule.setLocale implementation. Loads i18n.(locale).properties file
     * from current package to i18n variable.
     *
     * @param locale locale
     *
     * @throws Exception
     */
    public void setLocale( final Locale locale ) throws Exception
    {
        String package_name = getClass(  ).getPackage(  ).getName(  );
        
        if( package_name.startsWith( plugin_package_name_prefix ) )
        {
            System.err.println( "resources/i18n/"
                + package_name.substring( plugin_package_name_prefix.length()
                    ).replace( '.', '_' ) );
            
            i18n = new LanguageHelper( "resources/i18n/"
                + package_name.substring( plugin_package_name_prefix.length()
                    ).replace( '.', '_' ),
                locale );
        }
        else
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE, "Unable to set Locale for plugin '"
                + package_name
                + "' since the package name does not start with '"
                + plugin_package_name_prefix + "'." );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public ILocalizer getLocalizer(  )
    {
        return i18n;
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
}

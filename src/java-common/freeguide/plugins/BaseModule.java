package freeguide.plugins;

import freeguide.lib.fgspecific.Application;

import freeguide.lib.general.LanguageHelper;

import java.util.Locale;

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

        return LanguageHelper.getLocaleList( 
            getClass(  ).getPackage(  ).getName(  ).replace( '.', '/' )
            + "/i18n" );

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
        i18n =
            new LanguageHelper( 
                getClass(  ).getPackage(  ).getName(  ).replace( '.', '/' )
                + "/i18n", locale );
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

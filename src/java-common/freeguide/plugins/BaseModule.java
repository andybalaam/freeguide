package freeguide.plugins;

import freeguide.lib.fgspecific.Application;

import freeguide.lib.general.LanguageHelper;
import freeguide.lib.general.PreferencesHelper;

import java.util.Locale;
import java.util.logging.Level;
import java.util.prefs.Preferences;

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
    private Preferences prefs;

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
            getClass(  ).getClassLoader(  ),
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
                getClass(  ).getClassLoader(  ),
                getClass(  ).getPackage(  ).getName(  ).replace( '.', '/' )
                + "/i18n", locale );

    }

    /**
     * IModule.setConfigStorage implementation. It remember config place to
     * protected prefs variable.
     *
     * @param prefs DOCUMENT ME!
     */
    public void setConfigStorage( final Preferences prefs )
    {
        this.prefs = prefs;

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

    protected void loadObjectFromPreferences( final Object obj )
    {

        try
        {
            PreferencesHelper.load( prefs, obj );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING,
                "Error load config for class " + getClass(  ).getName(  ), ex );
        }
    }

    protected void saveObjectToPreferences( final Object obj )
    {

        try
        {
            PreferencesHelper.save( prefs, obj );

        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING,
                "Error save config for class " + getClass(  ).getName(  ), ex );
        }
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

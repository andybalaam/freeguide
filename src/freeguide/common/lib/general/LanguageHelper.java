package freeguide.common.lib.general;

import freeguide.common.plugininterfaces.ILocalizer;
import freeguide.common.lib.fgspecific.Application;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.*;
import java.util.logging.Level;

/**
 * Helper for some language releated functions.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class LanguageHelper implements ILocalizer
{
    final protected Locale locale;
    final protected ResourceBundle bundle;

    /**
     * Create language support object.
     *
     * @param resourcePrefix package prefix in form
     *        "freeguide/plugins/module/messages"
     */
    public LanguageHelper( final String resourcePrefix )
    {
        this.locale = Locale.getDefault();
        this.bundle = ResourceBundle.getBundle(resourcePrefix, locale);
    }

    /**
     * Create language support object.
     *
     * @param resourcePrefix package prefix in form
     *        "freeguide/plugins/module/messages"
     * @param locale
     */
    public LanguageHelper( final String resourcePrefix, final Locale locale )
    {
        if (resourcePrefix.indexOf("/") >= 0) {
            System.err.println("Still using old resource path format: " + resourcePrefix);
        }
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle(resourcePrefix, locale);
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     * @deprecated
     */
    public Set getKeys(  )
    {
        return new HashSet();
    }

    public Map getMap()
    {
        Map map = new HashMap();
        Enumeration keys = this.bundle.getKeys();
        while (keys.hasMoreElements()) {
            Object el = keys.nextElement();
            map.put(el, this.bundle.getObject(el + ""));
        }
        return map;
    }

    /**
     * Get localized message by key.
     *
     * @param key key for message
     *
     * @return localized messages
     */
    public String getLocalizedMessage( final String key )
    {
        return this.bundle.getString( key );
    }

    /**
     * Get localized message by key with parameters.
     *
     * @param key key for message
     * @param messageArguments message's parameters
     *
     * @return localized messages
     */
    public String getLocalizedMessage(
        final String key, final Object[] messageArguments )
    {
        MessageFormat formatter =
            new MessageFormat( this.bundle.getString( key ), this.locale );

        return formatter.format( messageArguments );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Locale getLocale(  )
    {
        return locale;
    }


    public static Locale[] getSupportedLocales() throws IOException
    {

        String[] codes = ResourceHelper.loadStrings("resources/main/languages.properties");
        Locale[] locales = new Locale[codes.length];
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].indexOf("_") > -1) {
                String[] split = codes[i].split("_");
                locales[i] = new Locale(split[0], split[1]);
            } else {
                locales[i] = new Locale(codes[i]);
            }
        }
        return locales;
    }
}

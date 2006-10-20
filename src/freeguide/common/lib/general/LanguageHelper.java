package freeguide.common.lib.general;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.plugininterfaces.ILocalizer;

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
    final protected ResourceBundle bundle;

/**
     * Create language support object.
     * 
     * @param resourcePrefix
     *            package prefix in form "freeguide/plugins/module/messages"
     * @param locale
     */
    public LanguageHelper( final ResourceBundle bundle )
    {
        this.bundle = bundle;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @deprecated
     */
    public Set getKeys(  )
    {
        return new HashSet(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Map getMap(  )
    {
        Map map = new HashMap(  );
        Enumeration keys = this.bundle.getKeys(  );

        while( keys.hasMoreElements(  ) )
        {
            Object el = keys.nextElement(  );
            map.put( el, this.bundle.getObject( el + "" ) );
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
            new MessageFormat( 
                this.bundle.getString( key ), bundle.getLocale(  ) );

        return formatter.format( messageArguments );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static Locale[] getSupportedLocales(  ) throws IOException
    {
        final List result = new ArrayList(  );
        Locale[] all = Locale.getAvailableLocales(  );

        for( int i = 0; i < all.length; i++ )
        {
            if( 
                ResourceBundle.getBundle( 
                        "resources/i18n/MessagesBundle", all[i] ).getLocale(  )
                                  .equals( all[i] ) )
            {
                result.add( all[i] );
            }
        }

        return (Locale[])result.toArray( new Locale[result.size(  )] );
    }
}

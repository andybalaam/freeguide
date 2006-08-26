package freeguide.common.plugininterfaces;

import java.util.Set;

/**
 * Localized messages support.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public interface ILocalizer
{
    /**
     * @deprecated
     */
    Set getKeys(  );

    String getLocalizedMessage( String key );

    String getLocalizedMessage( String key, Object[] params );
}

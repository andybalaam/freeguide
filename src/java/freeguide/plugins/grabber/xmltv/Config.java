package freeguide.plugins.grabber.xmltv;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Configuration for xmltv grabber.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class Config
{

    /** Info for loading by Preferences Helper. */
    public static final Class needToRun_TYPE = String.class;

    /** DOCUMENT ME! */
    public static final Class commandsRun_KEY_TYPE = String.class;

    /** DOCUMENT ME! */
    public static final Class commandsRun_VALUE_TYPE = String.class;

    /** List of selected grabbers. */
    public Set needToRun = new TreeSet(  );

    /** DOCUMENT ME! */
    public Map commandsRun = new TreeMap(  );

    /**
     * Clone config.
     *
     * @return new config instance.
     */
    public Object clone(  )
    {

        Config result = new Config(  );

        result.needToRun = new TreeSet( needToRun );

        result.commandsRun = new TreeMap( commandsRun );

        return result;

    }
}

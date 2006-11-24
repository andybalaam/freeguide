package freeguide.plugins.grabber.xmltv;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for xmltv grabber.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class XMLTVConfig
{
    protected static final String CONFIG_FILE_SUFFIX = ".conf";

    /** Info for loading by Preferences Helper. */
    public static final Class modules_TYPE = ModuleInfo.class;

    /** DOCUMENT ME! */
    public List modules = new ArrayList(  );

    /**
     * Clone config.
     *
     * @return new config instance.
     */
    public Object clone(  )
    {
        XMLTVConfig result = new XMLTVConfig(  );

        synchronized( modules )
        {
            for( int i = 0; i < modules.size(  ); i++ )
            {
                result.modules.add( 
                    ( (ModuleInfo)modules.get( i ) ).clone(  ) );
            }
        }

        return result;

    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class ModuleInfo
    {
        /** DOCUMENT ME! */
        public String moduleName;

        /** The grabbing command */
        public String commandToRun;

        /** The configuration command */
        public String configCommandToRun;

        /** DOCUMENT ME! */
        public String configFileName =
            Long.toString( System.currentTimeMillis(  ) ) + CONFIG_FILE_SUFFIX;

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public Object clone(  )
        {
            ModuleInfo result = new ModuleInfo(  );
            result.moduleName = moduleName;
            result.commandToRun = commandToRun;
            result.configCommandToRun = configCommandToRun;
            result.configFileName = configFileName;

            return result;
        }
    }
}

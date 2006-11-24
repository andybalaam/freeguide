package freeguide.plugins.grabber.vsetv;

import freeguide.common.lib.general.StringHelper;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class VsetvConfig
{
    protected static final String PREDEFINED_CHANNEL_GROUP = "base";

    /** DOCUMENT ME! */
    public boolean isAuth = false;

    /** DOCUMENT ME! */
    public String user = StringHelper.EMPTY_STRING;

    /** DOCUMENT ME! */
    public String pass = StringHelper.EMPTY_STRING;

    /** DOCUMENT ME! */
    public boolean isGetAll = false;

    /** DOCUMENT ME! */
    public String channelGroup = PREDEFINED_CHANNEL_GROUP;
}

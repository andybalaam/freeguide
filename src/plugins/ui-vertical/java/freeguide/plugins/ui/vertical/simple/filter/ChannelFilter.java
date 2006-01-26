package freeguide.plugins.ui.vertical.simple.filter;

import freeguide.lib.fgspecific.data.TVProgramme;
import java.util.ArrayList;


/**
 * Filter programs by channel.
 * If there are no channels set, all programs are shown.
 *
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class ChannelFilter extends ProgrammeFilter
{
    protected ArrayList arAllowedChannelIds = new ArrayList();


    public void addChannel(String strChannelId)
    {
        addChannel(strChannelId, true);
    }//public void addChannel(String strChannelId)



    public void addChannel(String strChannelId, boolean notify)
    {
        this.arAllowedChannelIds.add(strChannelId);
        if (notify) {
            this.notifyFilterChange();
        }
    }//public void addChannel(String strChannelId, boolean notify)



    public void removeChannel(String strChannelId)
    {
        removeChannel(strChannelId, true);
    }//public void removeChannel(String strChannelId)



    public void removeChannel(String strChannelId, boolean notify)
    {
        this.arAllowedChannelIds.remove(strChannelId);
        if (notify) {
            this.notifyFilterChange();
        }
    }//public void removeChannel(String strChannelId, boolean notify)



    public void removeAllChannels()
    {
        this.removeAllChannels(true);
    }//public void removeAllChannels()



    public void removeAllChannels(boolean notify)
    {
        this.arAllowedChannelIds.clear();
        if (notify) {
            this.notifyFilterChange();
        }
    }//public void removeAllChannels(boolean notify)



    public boolean showProgramme(TVProgramme programme)
    {
        return this.arAllowedChannelIds.size() == 0 || this.arAllowedChannelIds.contains(programme.getChannel().getID());
    }//public boolean showProgramme(TVProgramme programme)

}//public class ChannelFilter extends ProgrammeFilter
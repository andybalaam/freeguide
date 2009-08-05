package freeguide.plugins.importexport.xmltv;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import freeguide.common.gui.FileChooserExtension;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.data.TVChannelsSet.Channel;
import freeguide.common.plugininterfaces.IApplication;
import freeguide.common.plugininterfaces.IModuleStorage;

public class FilteredTVData implements ITVDataIterators
{
    private final TVData data;

    private final IModuleStorage.Info info;

    private final boolean selectedOnly;

    private final boolean todayOnly;

    private final boolean channelsList;

    private final IModuleStorage storage;

    private final IApplication application;

    public FilteredTVData( TVData data, FileChooserExtension ext,
        IModuleStorage storage, IApplication application )
    {
        this.data = data;
        this.info = ext.getSaveInfo();
        this.selectedOnly = ext.isSelectedOnly();
        this.todayOnly = ext.isTodayOnly();
        this.channelsList = ext.isChannelsList();
        this.storage = storage;
        this.application = application;
    }

    public Collection getChannels()
    {
        Collection channels = info.channelsList.getChannels();

        if( channelsList )
        {
            for( Iterator it = channels.iterator(); it.hasNext(); )
            {
                final Channel channel = (Channel)it.next();
                if( !info.channelsList.contains( channel.getChannelID() ) )
                {
                    it.remove();
                }
            }
        }

        return channels;
    }

    public Collection getProgrammes( Channel channel ) throws Exception
    {
        if( channelsList
            && !info.channelsList.contains( channel.getChannelID() ) )
        {
            return new TreeSet<TVProgramme>();
        }

        final IModuleStorage.Info filter = info.cloneInfo();
        filter.channelsList.getChannels().clear();
        filter.channelsList.getChannels().add( channel );

        final TVData data = storage.get( filter );
        final Set programmes = data.get( channel.getChannelID() )
            .getProgrammes();

        for( Iterator it = programmes.iterator(); it.hasNext(); )
        {
            final TVProgramme programme = (TVProgramme)it.next();

            if( ( selectedOnly && // Remove unselected or programmes from list
                !application.getReminders()[0].isSelected( programme ) )
                || ( todayOnly && // Remove programmes that are not on today
                ( programme.getEnd() < info.minDate || programme.getStart() > info.maxDate ) ) )
            {
                it.remove();
            }
        }

        return programmes;
    }

    public TVChannel getRealChannel( Channel chinfo )
    {
        Iterator it = data.getChannelsIterator();
        while( it.hasNext() )
        {
            TVChannel ch = (TVChannel)it.next();
            if( ch.getID().equals( chinfo.getChannelID() ) )
            {
                return ch;
            }
        }
        return null;
    }

}

package freeguide.test.fast;

import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.JDialog;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.plugins.storage.serfiles.StorageSerFilesByDay;

public class FakeStorage extends StorageSerFilesByDay
{
    Info info;

    TVData data;

    public FakeStorage( TVData data )
    {
        this.info = new Info();
        this.data = data;
    }

    public void cleanup()
    {
    }

    public TVProgramme findEarliest( long minDate, EarliestCheckAllow check )
        throws Exception
    {
        return null;
    }

    private ModifiableTVData cloneTVData( final TVData in )
    {
        ModifiableTVData ret = new ModifiableTVData();
        Iterator itCh = in.getChannelsIterator();
        while( itCh.hasNext() )
        {
            TVChannel oldChannel = (TVChannel)itCh.next();
            TVChannel newChannel = new TVChannel( oldChannel.getID(),
                oldChannel.getDisplayName() );
            Iterator itPr = oldChannel.getProgrammes().iterator();
            while( itPr.hasNext() )
            {
                TVProgramme oldProgramme = (TVProgramme)itPr.next();
                newChannel.put( (TVProgramme)oldProgramme.clone() );
            }
            ret.addChannel( newChannel );
        }
        return ret;
    }

    public TVData get( Info loadInfo ) throws Exception
    {
        TVData tmp = cloneTVData( data );
        TVData ret = new TVData();
        filterData( info, ret, tmp );
        return ret;
    }

    public Info getInfo()
    {
        return info;
    }

    public void store( TVData data )
    {
    }

    public Object getConfig()
    {
        return null;
    }

    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return null;
    }

    public String getI18nName()
    {
        return null;
    }

    public ResourceBundle getLocalizer()
    {
        return null;
    }

    public void reloadResourceBundle() throws Exception
    {
    }

}

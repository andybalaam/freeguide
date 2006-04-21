package freeguide.plugins.importexport.html;

import freeguide.common.gui.FileChooserExtension;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.IModuleStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TemplateHandler
{

    protected final boolean selectedOnly;
    protected final IModuleStorage.Info info;
    protected final DateFormat timeFormat;

    /**
     * Creates a new TemplateHandler object.
     *
     * @param ext DOCUMENT ME!
     */
    public TemplateHandler( final FileChooserExtension ext )
    {
        selectedOnly = ext.isSelectedOnly(  );
        info = ext.getSaveInfo(  );

        timeFormat = new SimpleDateFormat( "dd MMMM yyyy, HH:mm" );
        timeFormat.setTimeZone( Application.getInstance(  ).getTimeZone(  ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getTitle(  )
    {

        return "listing";
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getHeader(  )
    {

        return "listing";
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Collection getChannels(  )
    {

        return info.channelsList.getChannels(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channel DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public Collection getProgrammes( final TVChannelsSet.Channel channel )
        throws Exception
    {

        final IModuleStorage.Info filter = info.cloneInfo(  );
        filter.channelsList.getChannels(  ).clear(  );
        filter.channelsList.getChannels(  ).add( channel );

        final TVData data =
            Application.getInstance(  ).getDataStorage(  ).get( filter );
        final Set programmes =
            data.get( channel.getChannelID(  ) ).getProgrammes(  );

        if( selectedOnly )
        {

            // remove non selected programmes from list
            for( Iterator it = programmes.iterator(  ); it.hasNext(  ); )
            {

                final TVProgramme programme = (TVProgramme)it.next(  );

                if( 
                    !Application.getInstance(  ).getReminders(  )[0]
                        .isSelected( programme ) )
                {
                    it.remove(  );
                }
            }
        }

        return programmes;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeStartTime( final TVProgramme programme )
    {

        return timeFormat.format( new Date( programme.getStart(  ) ) );
    }
}

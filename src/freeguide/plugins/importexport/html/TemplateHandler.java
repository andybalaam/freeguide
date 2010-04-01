package freeguide.plugins.importexport.html;

import freeguide.common.gui.FileChooserExtension;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.StringHelper;

import freeguide.common.plugininterfaces.IModuleStorage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Template handler for fill template.
 *
 * @author Alex Buloichik
 */
public class TemplateHandler
{
    protected static final String DATE_FORMAT = "dd MMMM yyyy, HH:mm";
    protected static final String SUBTITLE_PREFIX = ": ";
    protected static final String HEADER = "listing";
    protected final boolean selectedOnly;
    protected final IModuleStorage.Info info;
    protected final DateFormat timeFormat;
    protected final String title;

    /**
     * Creates a new TemplateHandler object.
     *
     * @param ext DOCUMENT ME!
     */
    public TemplateHandler(
        final FileChooserExtension ext, final ResourceBundle i18n )
    {
        selectedOnly = ext.isSelectedOnly(  );
        info = ext.getSaveInfo(  );

        timeFormat = new SimpleDateFormat( DATE_FORMAT );
        timeFormat.setTimeZone( Application.getInstance(  ).getTimeZone(  ) );

        title = i18n.getString( "Template.Title" );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getTitle(  )
    {
        return title;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getSubTitle( final TVProgramme programme )
    {
        final String subTitle = programme.getSubTitle(  );

        if( subTitle != null )
        {
            return SUBTITLE_PREFIX + subTitle;
        }
        else
        {
            return StringHelper.EMPTY_STRING;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getHeader(  )
    {
        return title;
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
                    !Application.getInstance(  ).getReminders(  )[0].isSelected(
                            programme ) )
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

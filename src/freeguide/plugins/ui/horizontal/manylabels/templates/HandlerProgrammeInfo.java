package freeguide.plugins.ui.horizontal.manylabels.templates;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.ProgrammeFormat;
import freeguide.common.lib.fgspecific.TVChannelIconHelper;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.ILocalizer;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerProgrammeInfo
{

    protected final TVProgramme programme;
    protected final DateFormat timeFormat;
    protected final ILocalizer localizer;

    /**
     * Creates a new HandlerProgrammeInfo object.
     *
     * @param localizer DOCUMENT ME!
     * @param programme DOCUMENT ME!
     * @param timeFormat DOCUMENT ME!
     */
    public HandlerProgrammeInfo( 
        final ILocalizer localizer, final TVProgramme programme,
        final DateFormat timeFormat )
    {
        this.localizer = localizer;
        this.programme = programme;
        this.timeFormat = timeFormat;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getNoneMessage(  )
    {

        return localizer.getLocalizedMessage( "no_programme_selected" );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeStartTime(  )
    {

        return timeFormat.format( new Date( programme.getStart(  ) ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeEndTime(  )
    {

        return timeFormat.format( new Date( programme.getEnd(  ) ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public StringBuffer getTimeDelta(  )
    {

        final StringBuffer result = new StringBuffer(  );
        result.append( " <i>(" );
        ProgrammeFormat.calcTimeDelta( programme.getStart(  ), result );
        result.append( ")</i>" );

        return result;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean hasExtraTags(  )
    {

        return programme.getExtraTags(  ) != null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean hasProgramme(  )
    {

        return programme != null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean noProgramme(  )
    {

        return programme == null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Collection getExtraTags(  )
    {

        if( programme.getExtraTags(  ) != null )
        {

            return programme.getExtraTags(  ).entrySet(  );
        }
        else
        {

            return null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param hashOfAttrs DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getHashOfAttr( final HashMap hashOfAttrs )
    {

        return hashOfAttrs.get( "" );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param attrs DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Collection getTagAttrs( final HashMap attrs )
    {

        final Map result = new TreeMap( attrs );
        result.remove( "" );

        return result.entrySet(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeTitle(  )
    {

        return programme.getTitle(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeSubTitle(  )
    {

        if( programme.getSubTitle(  ) != null )
        {

            return ": " + programme.getTitle(  );
        }
        else
        {

            return "";
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeDescription(  )
    {

        return programme.getDescription(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeChannelName(  )
    {

        return programme.getChannel(  ).getDisplayName(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeCategory(  )
    {

        return programme.getCategory(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getPreviouslyShown(  )
    {

        if( programme.getPreviouslyShown(  ) )
        {

            return " "
            + Application.getInstance(  ).getLocalizedMessage( "repeat" );

        }
        else
        {

            return "";
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getStarRating(  )
    {

        if( 
            programme.getIsMovie(  ) && ( programme.getStarRating(  ) != null ) )
        {

            return " "
            + Application.getInstance(  ).getLocalizedMessage( "rating" )
            + ": " + programme.getStarRating(  );
        }
        else
        {

            return "";
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean hasProgrammeIcon(  )
    {

        return programme.getIconURL(  ) != null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeIconURL(  )
    {

        return programme.getIconURL(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean hasChannelIcon(  )
    {

        return TVChannelIconHelper.getIconFile( programme.getChannel(  ) ) != null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public String getChannelIconURL(  ) throws IOException
    {

        final File file =
            TVChannelIconHelper.getIconFile( programme.getChannel(  ) );

        return ( file != null ) ? file.toURL(  ).toString(  ) : null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getChannelTitle(  )
    {

        return programme.getChannel(  ).getDisplayName(  );
    }
}

package freeguide.plugins.ui.horizontal.manylabels.templates;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ILocalizer;
import freeguide.plugins.IModuleReminder;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerPersonalGuide
{

    protected final ILocalizer localizer;
    protected final TVData currentData;
    protected final Date theDate;
    protected final DateFormat dateFormat;
    protected final DateFormat timeFormat;
    protected final boolean forPrint;

    /**
     * Creates a new ParserPersonalizedGuide object.
     *
     * @param localizer DOCUMENT ME!
     * @param currentData DOCUMENT ME!
     * @param theDate DOCUMENT ME!
     * @param dateFormat DOCUMENT ME!
     * @param timeFormat DOCUMENT ME!
     * @param forPrint DOCUMENT ME!
     */
    public HandlerPersonalGuide( 
        final ILocalizer localizer, final TVData currentData,
        final Date theDate, final DateFormat dateFormat,
        final DateFormat timeFormat, final boolean forPrint )
    {
        this.localizer = localizer;
        this.currentData = currentData;
        this.theDate = theDate;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.forPrint = forPrint;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getEnds( final TVProgramme programme )
    {

        return Application.getInstance(  ).getLocalizedMessage( 
            "ends_template", new Object[] { getProgrammeEndTime( programme ) } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getTitle(  )
    {

        Object[] messageArguments = { dateFormat.format( theDate ) };

        return localizer.getLocalizedMessage( 
            "tv_guide_for_template", messageArguments );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getHeader(  )
    {

        Object[] args = { dateFormat.format( theDate ) };

        return localizer.getLocalizedMessage( 
            forPrint ? "tv_guide_for_template"
                     : "your_personalised_tv_guide_for_template", args );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getHelp(  )
    {

        return localizer.getLocalizedMessage( 
            "select_programmes_by_clicking_on_them" );
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

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeEndTime( final TVProgramme programme )
    {

        return timeFormat.format( new Date( programme.getEnd(  ) ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeURL( final TVProgramme programme )
    {

        return ProgrammeFormat.createLinkReference( programme );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeCategory( final TVProgramme programme )
    {

        return programme.getCategory(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeTitle( final TVProgramme programme )
    {

        return programme.getTitle(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeSubTitle( final TVProgramme programme )
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
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeDescription( final TVProgramme programme )
    {

        return programme.getDescription(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getProgrammeChannelName( final TVProgramme programme )
    {

        return programme.getChannel(  ).getDisplayName(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getPreviouslyShown( final TVProgramme programme )
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
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getStarRating( final TVProgramme programme )
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
    public Collection getProgrammes(  )
    {

        final IModuleReminder[] reminders =
            Application.getInstance(  ).getReminders(  );
        final List result = new ArrayList(  );
        currentData.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }

                protected void onProgramme( TVProgramme programme )
                {

                    if( 
                        ( programme.getEnd(  ) < System.currentTimeMillis(  ) ) )
                    {

                        return;
                    }

                    for( int i = 0; i < reminders.length; i++ )
                    {

                        if( reminders[i].isSelected( programme ) )
                        {
                            result.add( programme );

                        }
                    }
                }
            } );

        Collections.sort( result );

        return result;
    }
}

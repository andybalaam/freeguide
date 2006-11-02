package freeguide.plugins.ui.horizontal.manylabels.templates;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.ProgrammeFormat;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.IModuleReminder;

import java.text.DateFormat;
import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerPersonalGuide
{
    protected final ResourceBundle localizer;
    protected final TVData currentData;
    protected final Date theDate;
    protected final DateFormat dateFormat;
    protected final DateFormat timeFormat;
    protected final DateFormat weekdayFormat;
    protected final boolean forPrint;

/**
     * Creates a new ParserPersonalizedGuide object.
     * 
     * @param localizer
     *            DOCUMENT ME!
     * @param currentData
     *            DOCUMENT ME!
     * @param theDate
     *            DOCUMENT ME!
     * @param dateFormat
     *            DOCUMENT ME!
     * @param timeFormat
     *            DOCUMENT ME!
     * @param weekdayFormat
     *            DOCUMENT ME!
     * @param forPrint
     *            DOCUMENT ME!
     */
    public HandlerPersonalGuide( 
        final ResourceBundle localizer, final TVData currentData,
        final Date theDate, final DateFormat dateFormat,
        final DateFormat weekdayFormat, final DateFormat timeFormat,
        final boolean forPrint )
    {
        this.localizer = localizer;
        this.currentData = currentData;
        this.theDate = theDate;
        this.dateFormat = dateFormat;
        this.timeFormat = timeFormat;
        this.weekdayFormat = weekdayFormat;
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
        return Application.getInstance(  )
                          .getLocalizedMessage( 
            "ends_template", new Object[] { getProgrammeEndTime( programme ) } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getTitle(  )
    {
        Object[] messageArguments =
            {
                weekdayFormat.format( theDate ) + " "
                + dateFormat.format( theDate )
            };

        return MessageFormat.format( 
            localizer.getString( "tv_guide_for_template" ), messageArguments );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getHeader(  )
    {
        Object[] args =
            {
                weekdayFormat.format( theDate ) + " "
                + dateFormat.format( theDate )
            };

        String ans;

        if( forPrint )
        {
            ans = MessageFormat.format( 
                    localizer.getString( "tv_guide_for_template" ), args );
        }
        else
        {
            ans = MessageFormat.format( 
                    localizer.getString( 
                        "your_personalised_tv_guide_for_template" ), args );
        }

        return ans;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getHelp(  )
    {
        return localizer.getString( "select_programmes_by_clicking_on_them" );
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
            return ": " + programme.getSubTitle(  );
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
        final IModuleReminder reminder =
            Application.getInstance(  ).getReminder(  );
        final List result = new ArrayList(  );

        if( reminder != null )
        {
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

                        if( reminder.isSelected( programme ) )
                        {
                            result.add( programme );
                        }
                    }
                } );

            Collections.sort( result );
        }

        return result;
    }
}

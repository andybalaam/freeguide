/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.gui.viewer;

import freeguide.*;

import freeguide.gui.viewer.*;

import freeguide.lib.fgspecific.*;

import freeguide.lib.general.*;

import java.awt.*;

import java.text.*;

import java.util.*;

import javax.swing.*;

/**
 * Implements the model for ProgrammeJLabel to get and change
 * application state according to user interaction with
 * ProgrammeJLabel
 *
 * @author Risto Kankkunen (split out of ViewerFrame by Andy Balaam).
 * @version 1
 */
public class ProgrammeRenderer implements StripRenderer, ProgrammeJLabel.Model
{

    private static class RenderedProgrammeJLabel extends ProgrammeJLabel {
        
        RenderedProgrammeJLabel( SimpleDateFormat timeFormat, Font font )
        {
            super( timeFormat, font );
        }
        
        RenderedProgrammeJLabel( Model model, SimpleDateFormat timeFormat,
            Font font )
        {
            super( model, timeFormat, font );
        }
        
        /*
         * Overridden to make sure we get drawn by CellRendererPane
         *
         * @see java.awt.Component#isShowing()
         */
        public boolean isShowing(  )
        {
            return true;
        }
    }

    private ViewerFrame viewerFrame;
    private Programme programme;
    private ProgrammeJLabel label;

    ProgrammeRenderer( 
        SimpleDateFormat timeFormat, Font font, ViewerFrame viewerFrame )
    {
        this.label = new RenderedProgrammeJLabel(timeFormat, font );

        this.viewerFrame = viewerFrame;
    }


    // --- StripRenderer interface ---
    public Component getStripRendererComponent( 
        StripView view, Object value, boolean isSelected, boolean hasFocus,
        int row, long start, long end )
    {
        programme = (Programme)value;

        label.setModel( this );

        if( hasFocus )
        {

            if( view.isFocusOwner(  ) )
            {
                label.setBorder( 
                    BorderFactory.createCompoundBorder( 
                        BorderFactory.createLineBorder( Color.blue, 2 ),
                        BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) ) );
            }
            else
            {
                label.setBorder( BorderFactory.createLineBorder( Color.gray, 3 ) );
            }
        }

        return label;
    }

    // --- ProgrammeJLabel.Model interface
    public Programme getValue(  )
    {

        return programme;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isInGuide(  )
    {

        return programme.isInGuide(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param state DOCUMENT_ME!
     */
    public void setInGuide( boolean state )
    {
        programme.setInGuide( state );

        if( state )
        {
            FreeGuide.prefs.addInGuide( programme, viewerFrame.theDate );

            if( FreeGuide.prefs.misc.getBoolean( "reminders_on", true ) )
            {

                // Set up a reminder here if it's after now
                Date startTime = programme.getStart(  ).getTime(  );
                long warningSecs =
                    FreeGuide.prefs.misc.getLong( 
                        "reminders_warning_secs", 300 );
                long giveUpSecs =
                    FreeGuide.prefs.misc.getLong( 
                        "reminders_give_up_secs", 600 );

                if( startTime.after( new Date(  ) ) )
                {

                    // Find out when we will remind
                    Date reminderStartTime =
                        new Date( 
                            startTime.getTime(  ) - ( warningSecs * 1000 ) );

                    Date nowDate = new Date(  );

                    // If it's immediately, make it in 10 secs time
                    if( reminderStartTime.before( nowDate ) )
                    {
                        reminderStartTime.setTime( 
                            nowDate.getTime(  ) + 10000 );
                    }

                    // Set the ending time to be a certain time after the
                    // beginning.
                    Date reminderEndTime =
                        new Date( 
                            reminderStartTime.getTime(  )
                            + ( giveUpSecs * 1000 ) );

                    if( viewerFrame.reminderTimer != null )
                    {
                        viewerFrame.reminderTimer.cancel(  );
                    }

                    viewerFrame.reminderTimer = new MessageDialogTimer(  );

                    Object[] messageArguments = { programme.getTitle(  ) };
                    viewerFrame.reminderTimer.schedule( 
                        FreeGuide.getCompoundMessage( 
                            "is_starting_soon_template", messageArguments ),
                        reminderStartTime, reminderEndTime );

                }
            }
        }
        else
        {
            FreeGuide.prefs.removeFromGuide( programme );

            if( viewerFrame.reminderTimer != null )
            {
                viewerFrame.reminderTimer.cancel(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isFavourite(  )
    {

        FavouritesList favouritesList = FavouritesList.getInstance(  );

        return favouritesList.isFavourite( programme );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param state DOCUMENT_ME!
     */
    public void setFavourite( boolean state )
    {

        FavouritesList favouritesList = FavouritesList.getInstance(  );

        if( !state )
        {

            // Remove from favourites
            // Find out which favourite the programme matches
            Favourite theFavourite = favouritesList.getFavourite( programme );

            Object[] messageArguments = { theFavourite.getName(  ) };
            int r =
                JOptionPane.showConfirmDialog( 
                    viewerFrame,
                    FreeGuide.getCompoundMessage( 
                        "remove_favourite_template", messageArguments ),
                    FreeGuide.msg.getString( "remove_favourite" ),
                    JOptionPane.YES_NO_OPTION );

            if( r == 0 )
            {
                favouritesList.removeFavourite( theFavourite );
                setInGuide( false );

            }
        }
        else
        {

            // Add to favourites
            Favourite fav = new Favourite(  );

            String title = programme.getTitle(  );

            fav.setTitleString( title );
            fav.setName( title );

            // Remember the favourite
            favouritesList.appendFavourite( fav );
            setInGuide( true );

        }

    }


    /**
     * DOCUMENT_ME!
     */
    public void onFocus(  )
    {
        viewerFrame.scrollToReference( 
            HTMLGuideListener.createLinkReference( programme ) );
        viewerFrame.detailsPanel.updateProgramme( programme );
    }

    /**
     * DOCUMENT_ME!
     */
    public void isHovering(  )
    {

        /* Disabled since the introduction of the programme details panel
           - now you need to click a programme to see its details.
           Now replaces with the above onFocus method.
        viewerFrame.scrollToReference( HTMLGuideListener.createLinkReference(
            programme ) );
        viewerFrame.detailsPanel.updateProgramme( programme );
        */
    }
}

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
 * Draws a programme on the screen and handles some of the events happening to
 * a programme.
 *
 *@author     Risto Kankkunen (split out of ViewerFrame by Andy Balaam).
 *@created    28 June 2003
 *@version    1
 */

public class ProgrammeRenderer
    extends ProgrammeJLabel
    implements StripRenderer, ProgrammeJLabel.Model
{
    
    private StripView stripView;
    private ViewerFrame viewerFrame;
    private Programme programme;

    ProgrammeRenderer( SimpleDateFormat timeFormat, Font font,
        StripView stripView, ViewerFrame viewerFrame )
    {
        super( timeFormat, font );

        this.stripView = stripView;
        this.viewerFrame = viewerFrame;
        
    }

    /*
     * Overridden to make sure we get drawn by CellRendererPane
     * 
     * @see java.awt.Component#isShowing()
     */
    public boolean isShowing() {
        return true;
    }

    // --- StripRenderer interface ---

    public Component getStripRendererComponent(
        StripView view,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        long start,
        long end )
    {
        
        programme = (Programme) value;
        
        setModel(this);
        if (hasFocus) {
            setBorder(
                view.isFocusOwner() ?
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.blue, 2),
                    BorderFactory.createEmptyBorder(1, 1, 1, 1)
                ) :
                BorderFactory.createLineBorder(Color.gray, 3)
            );
        }

        return this;
    }

    // --- ProgrammeJLabel.Model interface

    public Programme getValue() {
        return programme;
    }

    public boolean isInGuide() {
        
        return programme.isInGuide();
        
    }

    public void setInGuide( boolean state ) {
        
        programme.setInGuide( state );
        
        if( state ) {

            FreeGuide.prefs.addInGuide( programme, viewerFrame.theDate );

            if (FreeGuide.prefs.misc.getBoolean("reminders_on", true)) {
                // Set up a reminder here if it's after now
                Date startTime = programme.getStart().getTime();
                long warningSecs = FreeGuide.prefs.misc.getLong(
                        "reminders_warning_secs", 300);
                long giveUpSecs = FreeGuide.prefs.misc.getLong(
                        "reminders_give_up_secs", 600);

                if (startTime.after(new Date())) {

                    // Find out when we will remind
                    Date reminderStartTime = new Date(startTime.getTime()
                            - warningSecs * 1000);

                    Date nowDate = new Date();

                    // If it's immediately, make it in 10 secs time
                    if (reminderStartTime.before(nowDate)) {
                        reminderStartTime
                                .setTime(nowDate.getTime() + 10000);
                    }

                    // Set the ending time to be a certain time after the
                    // beginning.
                    Date reminderEndTime = new Date(
                        reminderStartTime.getTime()    + giveUpSecs * 1000);

                    if( viewerFrame.reminderTimer != null ) {
                        viewerFrame.reminderTimer.cancel();
                    }
                    viewerFrame.reminderTimer = new MessageDialogTimer();
                    Object[] messageArguments = { programme.getTitle() };
                    viewerFrame.reminderTimer.schedule(
                        FreeGuide.getCompoundMessage(
                            "is_starting_soon_template", messageArguments ),
                        reminderStartTime,
                        reminderEndTime
                    );

                }
            }

        } else {
            
            FreeGuide.prefs.removeFromGuide(programme);
            if( viewerFrame.reminderTimer != null ) {
                viewerFrame.reminderTimer.cancel();
            }
        }
    }

    public boolean isFavourite() {
        FavouritesList favouritesList = FavouritesList.getInstance();
        return favouritesList.isFavourite(programme);
    }

    public void setFavourite( boolean state ) {
        
        FavouritesList favouritesList = FavouritesList.getInstance();

        if( !state ) {
            // Remove from favourites

            // Find out which favourite the programme matches
            Favourite theFavourite = favouritesList.getFavourite(
                programme );
            
            Object[] messageArguments = { theFavourite.getName() };
            int r = JOptionPane.showConfirmDialog( viewerFrame,
                FreeGuide.getCompoundMessage( "remove_favourite_template",
                     messageArguments ),
                FreeGuide.msg.getString( "remove_favourite" ),
                JOptionPane.YES_NO_OPTION );

            if (r == 0) {
                
                favouritesList.removeFavourite(theFavourite);
                //viewerFrame.printedGuideArea.update();
                viewerFrame.detailsPanel.updateProgramme( null );
                setInGuide( false );
                
            }
            
        } else {
            // Add to favourites

            Favourite fav = new Favourite();

            String title = programme.getTitle();

            fav.setTitleString(title);
            fav.setName(title);
            
            // Remember the favourite
            favouritesList.appendFavourite(fav);
            
            setInGuide( true );

        }
        updateIsFavourite( state );
        
    }

    public StripView getStripView() {
        return stripView;
    }
    
    public void isHovering() {
        viewerFrame.scrollToReference(HTMLGuideListener.createLinkReference(programme));
        viewerFrame.detailsPanel.updateProgramme( programme );
    }

}


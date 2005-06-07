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
package freeguide.plugins.ui.horizontal;

import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ui.horizontal.components.StripRenderer;
import freeguide.plugins.ui.horizontal.components.StripView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;

/**
 * Implements the model for ProgrammeJLabel to get and change application
 * state according to user interaction with ProgrammeJLabel
 *
 * @author Risto Kankkunen (split out of ViewerFrame by Andy Balaam).
 * @version 1
 */
public class ProgrammeRenderer implements StripRenderer, ProgrammeJLabel.Model
{

    private HorizontalViewer controller;
    private TVProgramme programme;
    private ProgrammeJLabel label;

    ProgrammeRenderer( 
        SimpleDateFormat timeFormat, Font font, HorizontalViewer controller )
    {
        this.label =
            new RenderedProgrammeJLabel( controller, timeFormat, font );

        this.controller = controller;

    }

    // --- StripRenderer interface ---
    public Component getStripRendererComponent( 
        StripView view, Object value, boolean isSelected, boolean hasFocus,
        int row, long start, long end )
    {
        programme = (TVProgramme)value;

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
                label.setBorder( 
                    BorderFactory.createLineBorder( Color.gray, 3 ) );

            }
        }

        return label;

    }

    // --- ProgrammeJLabel.Model interface
    public TVProgramme getValue(  )
    {

        return programme;

    }

    /**
     * DOCUMENT_ME!
     */

    /*public void setInGuide( boolean state )
    {

    if( state )
    {

        //TODO FreeGuide.prefs.addInGuide( programme, viewerFrame.currentDate );
        if( MainController.config.reminderOn )
        {

            // Set up a reminder here if it's after now
            Date startTime = new Date( programme.getStart(  ) );

            if( startTime.after( new Date(  ) ) )
            {

                // Find out when we will remind
                Date reminderStartTime =
                    new Date(
                        startTime.getTime(  )
                        - ( MainController.config.reminderWarning ) );

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
                        + ( MainController.config.reminderGiveUp ) );
    */
    /* TODO if( viewerFrame.reminderTimer != null )


    {


    viewerFrame.reminderTimer.cancel(  );


    }




    viewerFrame.reminderTimer = new MessageDialogTimer(  );






    Object[] messageArguments = { programme.getTitle(  ) };


    viewerFrame.reminderTimer.schedule(


    FreeGuide.getCompoundMessage(


    "is_starting_soon_template", messageArguments ),


    reminderStartTime, reminderEndTime );


    */
    /*                }
            }
        }

        else
        {
    */

    //FreeGuide.prefs.removeFromGuide( programme );

    /*TODO  if( viewerFrame.reminderTimer != null )


    {


    viewerFrame.reminderTimer.cancel(  );


    }*/
    /*}
    }*/

    /**
     * Called when this component receives the focus.  Scrolls the HTML guide
     * to this programme (if it is in the guide), shows the programme in the
     * programme details panel, and gives focus to the programmesPanel.
     */
    public void onFocus(  )
    {
        controller.panel.scrollToReference( 
            ProgrammeFormat.createLinkReference( programme ) );

        controller.panel.detailsPanel.updateProgramme( programme );

        controller.panel.programmesPanel.requestFocus(  );

    }

    private static class RenderedProgrammeJLabel extends ProgrammeJLabel
    {
        RenderedProgrammeJLabel( 
            HorizontalViewer controller, SimpleDateFormat timeFormat, Font font )
        {
            super( controller, timeFormat, font );

        }

        RenderedProgrammeJLabel( 
            HorizontalViewer controller, Model model,
            SimpleDateFormat timeFormat, Font font )
        {
            super( controller, model, timeFormat, font );

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
}

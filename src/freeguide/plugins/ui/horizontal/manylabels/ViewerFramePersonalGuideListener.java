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
package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.ProgrammeFormat;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.IModuleReminder;

import freeguide.plugins.ui.horizontal.manylabels.templates.HandlerPersonalGuide;
import freeguide.plugins.ui.horizontal.manylabels.templates.HandlerProgrammeInfo;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import java.text.ParsePosition;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Handles HyperlinkEvents created by the HTML document displayed in the HTML
 * program guide.
 *
 * @author Mike Dean
 * @version 1
 */
public class ViewerFramePersonalGuideListener implements HyperlinkListener
{

    private HorizontalViewer controller;

    /**
     * Creates a new HTMLGuideListener object.
     *
     * @param controller the HorizontalViewer controller used to access other
     *        objects.
     */
    public ViewerFramePersonalGuideListener( HorizontalViewer controller )
    {
        this.controller = controller;

    }

    /**
     * Requests the ViewerFrame scroll the program listing to the time encoded
     * in the link's hypertext reference, and shows the clicked programme in
     * the programme details panel. The method is called when a hypertext
     * link is updated ("ACTIVATED", "ENTERED", or "EXITED"). Only events of
     * type "ACTIVATED" are important.
     *
     * @param e the event triggering this method call.
     */
    public void hyperlinkUpdate( HyperlinkEvent e )
    {

        if( HyperlinkEvent.EventType.ACTIVATED == e.getEventType(  ) )
        {

            String desc;

            try
            {
                desc = URLDecoder.decode( e.getDescription(  ), "UTF-8" );
            }
            catch( UnsupportedEncodingException ex )
            {
                desc = e.getDescription(  );
            }

            int pos = desc.indexOf( ';' );

            String channelID = ( pos > 0 ) ? desc.substring( pos + 1 ) : "";

            GregorianCalendar showTime = new GregorianCalendar(  );

            // FIXME: Really, instead of scrolling to the start of the programme
            // we should select the actual ProgrammeJLabel.  We know what
            // programme it is from the stff below to find the right programme
            // to show in the programme details panel.
            showTime.setTime( 
                ProgrammeFormat.LINK_DATE_FORMAT.parse( 
                    desc.substring( 0, pos ), new ParsePosition( 1 ) ) );

            controller.panel.scrollTo( showTime, channelID );

            controller.updateProgrammeInfo( 
                getProgrammeFromReference( 
                    e.getDescription(  ).substring( 1 ) ) );
            controller.currentProgrammeLabel = null;
        }
    }

    /**
     * Utility method to find a programme given its reference.
     *
     * @param reference DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public TVProgramme getProgrammeFromReference( String reference )
    {

        // FIXME: this is a really slow way of doing it: we should cache
        // programme references somewhere - why not in programmes themselves?
        // FIXME: this is copied and pasted from ViewerFrameHTMLGuide
        final List tickedProgrammes = new ArrayList(  );
        final IModuleReminder[] reminders =
            Application.getInstance(  ).getReminders(  );

        controller.currentData.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }

                protected void onProgramme( TVProgramme programme )
                {

                    for( int i = 0; i < reminders.length; i++ )
                    {

                        if( reminders[i].isSelected( programme ) )
                        {
                            tickedProgrammes.add( programme );

                        }
                    }
                }
            } );

        // End of copy and paste from ViewerFrameHTMLGuide
        Iterator it = tickedProgrammes.iterator(  );

        while( it.hasNext(  ) )
        {

            TVProgramme prog = (TVProgramme)( it.next(  ) );
            String this_ref = ProgrammeFormat.createLinkReference( prog );

            if( this_ref.equals( reference ) )
            {

                return prog;
            }
        }

        return null;

    }

    /**
     * Utility method to create a unique ASCII-only name (reference) to
     * identify each program in the HTML program guide.
     */

    /*  public static String createLinkReference( TVProgramme programme )
    {

    String reference = null;

    // According to HTML spec, name must be unique and use only ASCII chars
    StringBuffer ref =
    new StringBuffer(
    HTMLGuideListener.LinkDateFormat.format(
      new Date( programme.getStart(  ) ) ) );

    ref.append( programme.getChannel(  ).getID(  ) );

    ref.append( programme.getTitle(  ) );

    ref.append( programme.getSubTitle(  ) );

    try
    {
    reference = URLEncoder.encode( ref.toString(  ), "UTF-8" );

    }

    catch( UnsupportedEncodingException e )
    {

    // Won't happen.  All JVM's must support "UTF-8"
    // (and it's the character set recommended by the W3C).
    }

    return reference;

    }*/
}

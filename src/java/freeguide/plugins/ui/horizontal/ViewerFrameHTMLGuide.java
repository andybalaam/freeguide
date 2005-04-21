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

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.StartTimeComparator;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.lib.fgspecific.selection.SelectionManager;

import freeguide.lib.general.StringHelper;
import freeguide.lib.general.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

/**
 * A class that deals with the HTML listings guide displayed in a scroll panel
 * below the TV grid
 *
 * @author Andy Balaam
 * @version 2
 */
public class ViewerFrameHTMLGuide extends javax.swing.JEditorPane
{

    /** This object's parent window. */
    private ViewerFrame parentViewerFrame;
    private ProgrammeStripModel model;

    /**
     * DOCUMENT ME!
     *
     * @param parentViewerFrame The screen on which this is displayed
     */
    public ViewerFrameHTMLGuide( ViewerFrame parentViewerFrame )
    {
        super(  );

        this.parentViewerFrame = parentViewerFrame;

        // Scrolls the program guide to show the program when
        // the user clicks the program name in the HTML Guide
        addHyperlinkListener( new HTMLGuideListener( parentViewerFrame ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param model DOCUMENT_ME!
     */
    public void setModel( ProgrammeStripModel model )
    {
        this.model = model;

    }

    //{{{ Printed Guide

    /**
     * Get the HTML version of the listing and show it in the printed guide
     */
    public void update(  )
    {
        setText( constructHTMLGuide( true ) );

        setCaretPosition( 0 );

    }

    /*


    *  Saves out the listings as an HTML file to be printed.


    */

    /**
     * Description of the Method
     */
    public void writeOutAsHTML(  )
    {

        String fs = System.getProperty( "file.separator" );

        // Make a file in the default location
        File f = new File( FreeGuide.config.workingDirectory + "/guide.html" );

        try
        {

            //IOException
            BufferedWriter buffy =
                new BufferedWriter( 
                    new OutputStreamWriter( 
                        new FileOutputStream( f ), "UTF-8" ) );

            buffy.write( constructHTMLGuide( false ) );

            buffy.close(  );

            String cmd =
                StringHelper.replaceAll( 
                    FreeGuide.config.browserCommand, "%filename%",
                    f.getPath(  ) );

            Utils.execNoWait( cmd );

        }

        catch( java.io.IOException e )
        {
            e.printStackTrace(  );

        }

        //try
    }

    //writeOutAsHTML

    /**
     * Makes a TV Guide in HTML format and returns it as a string.
     *
     * @param onScreen Description of the Parameter
     *
     * @return the TV guide as a string of html
     */
    private String constructHTMLGuide( boolean onScreen )
    {

        // Find out whether we're in the 24 hour clock
        boolean draw24time = parentViewerFrame.parent.config.display24time;

        SimpleDateFormat timeFormat;

        if( draw24time )
        {
            timeFormat = HorizontalViewer.timeFormat24Hour;

        }

        else
        {
            timeFormat = HorizontalViewer.timeFormat12Hour;

        }

        final Vector tickedProgrammes = new Vector(  );

        parentViewerFrame.parent.currentData.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }

                protected void onProgramme( TVProgramme programme )
                {

                    if( SelectionManager.isInGuide( programme ) )
                    {
                        tickedProgrammes.add( programme );

                    }
                }
            } );

        // The string we shall return
        StringBuffer ans = new StringBuffer(  );

        // Set up some constants
        String lineBreak = System.getProperty( "line.separator" );

        ans.append( "<html>" ).append( lineBreak );

        ans.append( "<head>" ).append( lineBreak );

        if( !onScreen )
        {
            ans.append( 
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>" )
               .append( lineBreak );
        }

        ans.append( "  <title>" );

        Object[] messageArguments =
        {
            parentViewerFrame.parent.htmlDateFormat.format( 
                new Date( parentViewerFrame.parent.theDate ) )
        };

        ans.append( 
            parentViewerFrame.parent.getLocalizer(  ).getLocalizedMessage( 
                "tv_guide_for_template", messageArguments ) );

        ans.append( "</title>" ).append( lineBreak );

        ans.append( "  <style type='text/css'>" ).append( lineBreak );

        ans.append( "    h1 {" ).append( lineBreak );

        ans.append( "        font-family: helvetica, helv, arial;" ).append( 
            lineBreak );

        ans.append( "        font-weight: bold;" ).append( lineBreak );

        ans.append( "        font-size: x-large;" ).append( lineBreak );

        ans.append( "    }" ).append( lineBreak );

        ans.append( "    h2 {" ).append( lineBreak );

        ans.append( "        font-family: helvetica, helv, arial;" ).append( 
            lineBreak );

        ans.append( "        font-weight: bold;" ).append( lineBreak );

        ans.append( "        font-size: large;" ).append( lineBreak );

        ans.append( "    }" ).append( lineBreak );

        ans.append( "    h3 {" ).append( lineBreak );

        ans.append( "        font-family: helvetica, helv, arial;" ).append( 
            lineBreak );

        ans.append( "        font-weight: bold;" ).append( lineBreak );

        ans.append( "        font-size: medium;" ).append( lineBreak );

        ans.append( "    }" ).append( lineBreak );

        ans.append( "    h4 {" ).append( lineBreak );

        ans.append( "        font-family: helvetica, helv, arial;" ).append( 
            lineBreak );

        ans.append( "        font-weight: bold;" ).append( lineBreak );

        ans.append( "        font-size: small;" ).append( lineBreak );

        ans.append( "    }" ).append( lineBreak );

        ans.append( "    body {" ).append( lineBreak );

        ans.append( "        font-family: helvetica, helv, arial;" ).append( 
            lineBreak );

        ans.append( "        font-size: small;" ).append( lineBreak );

        ans.append( "    }" ).append( lineBreak );

        ans.append( "    address {" ).append( lineBreak );

        ans.append( "        font-family: helvetica, helv, arial;" ).append( 
            lineBreak );

        ans.append( "        font-size: xx-small;" ).append( lineBreak );

        ans.append( "    }" ).append( lineBreak );

        ans.append( "  </style>" ).append( lineBreak );

        ans.append( "</head>" ).append( lineBreak );

        ans.append( "<body>" ).append( lineBreak );

        ans.append( "  <h1>" );

        if( onScreen )
        {
            ans.append( 
                "<font face='helvetica, helv, arial, sans serif' size='4'>" );

            Object[] messageArguments2 =
            {
                parentViewerFrame.parent.htmlDateFormat.format( 
                    new Date( parentViewerFrame.parent.theDate ) )
            };

            ans.append( 
                parentViewerFrame.parent.getLocalizer(  ).getLocalizedMessage( 
                    "your_personalised_tv_guide_for_template",
                    messageArguments2 ) );

            ans.append( "</font>" );

        }

        else
        {

            Object[] messageArguments2 =
            {
                parentViewerFrame.parent.htmlDateFormat.format( 
                    new Date( parentViewerFrame.parent.theDate ) )
            };

            ans.append( 
                parentViewerFrame.parent.getLocalizer(  ).getLocalizedMessage( 
                    "tv_guide_for_template", messageArguments2 ) );

        }

        ans.append( "</h1>" ).append( lineBreak );

        if( onScreen )
        {
            ans.append( 
                "<font face='helvetica, helv, arial, sans serif' size=3>" );

            ans.append( "<p>" );

            ans.append( 
                parentViewerFrame.parent.getLocalizer(  ).getLocalizedMessage( 
                    "select_programmes_by_clicking_on_them" ) );

            ans.append( "</p>" );

            ans.append( "</font>" );

        }

        // Sort the programmes
        Collections.sort( tickedProgrammes, new StartTimeComparator(  ) );

        // Add them to the HTML list
        // ----------------------------
        if( onScreen )
        {
            ans.append( 
                "<font face='helvetica, helv, arial, sans serif' size=3>" );

        }

        ProgrammeFormat pf =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FRAGMENT_FORMAT, timeFormat, false );

        pf.setOnScreen( onScreen );

        Iterator i = tickedProgrammes.iterator(  );

        while( i.hasNext(  ) )
        {

            TVProgramme prog = (TVProgramme)( i.next(  ) );

            ans.append( pf.formatLong( prog ) );

        }

        if( onScreen )
        {
            ans.append( "</font>" );

        }

        if( !onScreen )
        {
            ans.append( "<hr />" + lineBreak );

            ans.append( "<address>" );

            ans.append( "http://freeguide-tv.sourceforge.net" );

            ans.append( "</address>" ).append( lineBreak );

        }

        ans.append( "</body>" ).append( lineBreak );

        ans.append( "</html>" ).append( lineBreak );

        return ans.toString(  );

    }
}

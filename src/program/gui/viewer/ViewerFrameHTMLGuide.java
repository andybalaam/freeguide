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
import freeguide.lib.fgspecific.*;
import freeguide.lib.general.*;
import java.io.*;
import java.text.*;
import java.util.*;

/**
 * A class that deals with the HTML listings guide displayed in a scroll panel
 * below the TV grid
 *
 *@author     Andy Balaam
 *@created    17 November 2003
 *@version    2
 */
public class ViewerFrameHTMLGuide extends javax.swing.JEditorPane {

    /**
     *
     *@param  parentViewerFrame  The screen on which this is displayed
     */
    public ViewerFrameHTMLGuide( ViewerFrame parentViewerFrame ) {
        
        super();
        this.parentViewerFrame = parentViewerFrame;
        
                // Scrolls the program guide to show the program when
                // the user clicks the program name in the HTML Guide
                addHyperlinkListener(new HTMLGuideListener(parentViewerFrame));
    }
    
    public void setModel(ProgrammeStripModel model) {
        this.model = model;
    }

    //{{{ Printed Guide
    
    /**
     *  Get the HTML version of the listing and show it in the printed guide
     */
    public void update() {
        
        setText( constructHTMLGuide(true) );
        setCaretPosition(0);

    }


    /*
     *  Saves out the listings as an HTML file to be printed.
     */
    /**
     *  Description of the Method
     */
    public void writeOutAsHTML() {

        String fs = System.getProperty("file.separator");

        // Make a file in the default location
        File f = new File( FreeGuide.prefs.performSubstitutions(
            FreeGuide.prefs.misc.get("working_directory") + fs
                + "guide.html" ) );

        try {
            //IOException

            BufferedWriter buffy = new BufferedWriter(new FileWriter(f));

            buffy.write(constructHTMLGuide(false));

            buffy.close();

            String[] cmds = Utils.substitute(
                FreeGuide.prefs.commandline.getStrings( "browser_command" ),
                "%filename%",
                f.getPath() );
            
            Utils.execNoWait(cmds);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        //try

    }


    //writeOutAsHTML

    /**
     *  Makes a TV Guide in HTML format and returns it as a string.
     *
     *@param  onScreen  Description of the Parameter
     *@return           the TV guide as a string of html
     */
    private String constructHTMLGuide( boolean onScreen ) {
        
        // Find out whether we're in the 24 hour clock
        boolean draw24time = FreeGuide.prefs.screen.getBoolean(
            "display_24hour_time", true );
        
        SimpleDateFormat timeFormat = (
            draw24time ? parentViewerFrame.timeFormat24Hour :
                parentViewerFrame.timeFormat12Hour );
        
        Vector tickedProgrammes = new Vector();
        Programme programme;

        Iterator i = model.getAll().iterator();        
        while( i.hasNext() ) {
            
            programme = (Programme)( i.next() );
            
            if( programme.isInGuide() ) {
                tickedProgrammes.add( programme );
            }
            
        }
        
        // The string we shall return
        StringBuffer ans = new StringBuffer();

        // Set up some constants
        String lineBreak = System.getProperty("line.separator");

        ans.append( "<html>" ).append( lineBreak );
        ans.append( "<head>").append( lineBreak );
        ans.append( "  <title>" );
        ans.append( FreeGuide.msg.getString( "tv_guide_for" ) );
        ans.append( " " );
        ans.append( parentViewerFrame.htmlDateFormat.format(
                parentViewerFrame.theDate.getTime() ) );
        ans.append( "</title>" ).append( lineBreak );
        ans.append( "  <style type='text/css'>").append( lineBreak );
        ans.append( "    h1 {").append( lineBreak );
        ans.append( "        font-family: helvetica, helv, arial;").append(
            lineBreak );
        ans.append( "        font-weight: bold;").append( lineBreak );
        ans.append( "        font-size: x-large;").append( lineBreak );
        ans.append( "    }").append( lineBreak );
        ans.append( "    h2 {").append( lineBreak );
        ans.append( "        font-family: helvetica, helv, arial;").append(
            lineBreak );
        ans.append( "        font-weight: bold;").append( lineBreak );
        ans.append( "        font-size: large;").append( lineBreak );
        ans.append( "    }").append( lineBreak );
        ans.append( "    h3 {").append( lineBreak );
        ans.append( "        font-family: helvetica, helv, arial;").append(
            lineBreak );
        ans.append( "        font-weight: bold;").append( lineBreak );
        ans.append( "        font-size: medium;").append( lineBreak );
        ans.append( "    }").append( lineBreak );
        ans.append( "    h4 {").append( lineBreak );
        ans.append( "        font-family: helvetica, helv, arial;").append(
            lineBreak );
        ans.append( "        font-weight: bold;").append( lineBreak );
        ans.append( "        font-size: small;").append( lineBreak );
        ans.append( "    }").append( lineBreak );
        ans.append( "    body {").append( lineBreak );
        ans.append( "        font-family: helvetica, helv, arial;").append(
            lineBreak );
        ans.append( "        font-size: small;").append( lineBreak );
        ans.append( "    }").append( lineBreak );
        ans.append( "    address {").append( lineBreak );
        ans.append( "        font-family: helvetica, helv, arial;").append(
            lineBreak );
        ans.append( "        font-size: xx-small;").append( lineBreak );
        ans.append( "    }").append( lineBreak );
        ans.append( "  </style>").append( lineBreak );
        ans.append( "</head>").append( lineBreak );
        ans.append( "<body>").append( lineBreak );
        ans.append( "  <h1>" );
        
        if (onScreen) {
            
            ans.append(
                "<font face='helvetica, helv, arial, sans serif' size=4>" );
            ans.append( FreeGuide.msg.getString(
                "your_personalised_tv_guide_for" ) );
            ans.append( " " );
            ans.append( parentViewerFrame.htmlDateFormat.format(
                    parentViewerFrame.theDate.getTime() ) );
            ans.append( "</font>" );
            
        } else {
            
            ans.append( FreeGuide.msg.getString( "tv_guide_for" ) );
            ans.append( " " );
            ans.append( parentViewerFrame.htmlDateFormat.format(
                parentViewerFrame.theDate.getTime() ) );
                
        }

        ans.append( "</h1>").append( lineBreak );

        if (onScreen) {
            ans.append(
                "<font face='helvetica, helv, arial, sans serif' size=3>" );
            ans.append( "<p>" );
            ans.append( FreeGuide.msg.getString(
                "select_programmes_by_clicking_on_them" ) );
            ans.append( "</p>" );
            ans.append( "</font>" );
        }

        // Sort the programmes
        Collections.sort( tickedProgrammes, new StartTimeComparator() );

        // Add them to the HTML list
        // ----------------------------

        if (onScreen) {
            ans.append(
                "<font face='helvetica, helv, arial, sans serif' size=3>" );
        }

        ProgrammeFormat pf = new ProgrammeFormat(
        ProgrammeFormat.HTML_FRAGMENT_FORMAT, timeFormat, false);
        pf.setOnScreen(onScreen);
    
        i = tickedProgrammes.iterator();
        while( i.hasNext() ) {
    
            Programme prog = (Programme)( i.next() );
            ans.append( pf.longFormat( prog ) );
            
        }
        
        if (onScreen) {
            ans.append( "</font>" );
        }

        if (!onScreen) {

            ans.append( "<hr />" + lineBreak );
            ans.append( "<address>" );
            ans.append( "http://freeguide-tv.sourceforge.net" );
            ans.append( "</address>" )
                .append( lineBreak );

        }

        ans.append( "</body>" ).append( lineBreak );
        ans.append( "</html>" ).append( lineBreak );

        return ans.toString();
    }
    
    
    /**
     * This object's parent window.
     */
    private ViewerFrame parentViewerFrame;
    private ProgrammeStripModel model;
    
}


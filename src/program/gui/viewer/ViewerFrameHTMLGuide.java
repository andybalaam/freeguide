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
 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Vector;

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
	
	//{{{ Printed Guide
	
	/**
     *  Get the HTML version of the listing and show it in the printed guide
     */
    public void update() {

        setText( constructHTMLGuide(true) );

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
		
		// Construct a list of selected programmes
		Vector tickedProgrammes = new Vector();
		ProgrammeJLabel programmeJLabel;
		
		for( int i=0; i<parentViewerFrame.programmeJLabels.size(); i++ ) {
			
			programmeJLabel =
				(ProgrammeJLabel)parentViewerFrame.programmeJLabels.get(i);
			
			if( programmeJLabel.isSelected ) {
				
				tickedProgrammes.add( programmeJLabel.programme );
				
			}
			
		}
		
        // The string we shall return
        StringBuffer ans = new StringBuffer();

        // Set up some constants
        String lineBreak = System.getProperty("line.separator");

        ans.append( "<html>" ).append( lineBreak );
        ans.append( "<head>").append( lineBreak );
        ans.append( "  <title>TV Guide for "
			+ parentViewerFrame.htmlDateFormat.format(
				parentViewerFrame.theDate.getTime() )
			+ "</title>").append( lineBreak );
        ans.append( "  <style type='text/css'>").append( lineBreak );
        ans.append( "	h1 {").append( lineBreak );
        ans.append( "		font-family: helvetica, helv, arial;").append( lineBreak );
        ans.append( "		font-weight: bold;").append( lineBreak );
        ans.append( "		font-size: x-large;").append( lineBreak );
        ans.append( "	}").append( lineBreak );
        ans.append( "	h2 {").append( lineBreak );
        ans.append( "		font-family: helvetica, helv, arial;").append( lineBreak );
        ans.append( "		font-weight: bold;").append( lineBreak );
        ans.append( "		font-size: large;").append( lineBreak );
        ans.append( "	}").append( lineBreak );
        ans.append( "	h3 {").append( lineBreak );
        ans.append( "		font-family: helvetica, helv, arial;").append( lineBreak );
        ans.append( "		font-weight: bold;").append( lineBreak );
        ans.append( "		font-size: medium;").append( lineBreak );
        ans.append( "	}").append( lineBreak );
        ans.append( "	h4 {").append( lineBreak );
        ans.append( "		font-family: helvetica, helv, arial;").append( lineBreak );
        ans.append( "		font-weight: bold;").append( lineBreak );
        ans.append( "		font-size: small;").append( lineBreak );
        ans.append( "	}").append( lineBreak );
        ans.append( "	body {").append( lineBreak );
        ans.append( "		font-family: helvetica, helv, arial;").append( lineBreak );
        ans.append( "		font-size: small;").append( lineBreak );
        ans.append( "	}").append( lineBreak );
        ans.append( "	address {").append( lineBreak );
        ans.append( "		font-family: helvetica, helv, arial;").append( lineBreak );
        ans.append( "		font-size: xx-small;").append( lineBreak );
        ans.append( "	}").append( lineBreak );
        ans.append( "  </style>").append( lineBreak );
        ans.append( "</head>").append( lineBreak );
        ans.append( "<body>").append( lineBreak );
        ans.append( "  <h1>" );
		
        if (onScreen) {
			
            ans.append(
				"<font face='helvetica, helv, arial, sans serif' size=4>" );
            ans.append( "Your Personalised TV Guide for " )
				.append( parentViewerFrame.htmlDateFormat.format(
					parentViewerFrame.theDate.getTime() ) );
            ans.append( "</font>" );
			
        } else {
			
            ans.append( "TV Guide for ").append(
				parentViewerFrame.htmlDateFormat.format(
					parentViewerFrame.theDate.getTime() ) );
				
        }

        ans.append( "</h1>").append( lineBreak );

        if (onScreen) {
            ans.append(
				"<font face='helvetica, helv, arial, sans serif' size=3>" );
            ans.append( "<p>Select programmes above by clicking on them, " );
			ans.append( "and they will be highlighted and appear below.</p>" );
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

		String progSubTitle;
		String progLongDesc;
		
        for (int i = 0; i < tickedProgrammes.size(); i++) {
			
            Programme prog = (Programme)tickedProgrammes.get(i);
			
			progSubTitle = prog.getSubTitle();
			progLongDesc = prog.getLongDesc();
			
			ans.append( "  <p><b>" )
				.append( timeFormat.format( prog.getStart().getTime() ) )
				.append( " - " );
                        if (onScreen) {
                                String ref = HTMLGuideListener.createLinkReference(prog);
                                ans.append( "<a href=\"#" + ref +
                                            "\" name=\"" +ref + "\">" );
                        }
		        ans.append( prog.getTitle() );
			
			if( progSubTitle != null ) {
				
				ans.append( ": " + progSubTitle );
				
			}
                        if (onScreen) {
                                ans.append( "</a>" );
                        }
			
			ans.append( "</b><br>" )
				.append( prog.getChannelName() )
				.append( ", ends " )
				.append( timeFormat.format( prog.getEnd().getTime() ) );
			
            if ( progLongDesc == null) {

					ans.append( "</p>" )
						.append( lineBreak );

            } else {

				ans.append( "<br>" )
					.append( progLongDesc );
				
			}
				
            if (prog.getPreviouslyShown()) {
                ans.append( " (Repeat)" ).append( "<br>" );
            }
            if ( prog.getIsMovie() && prog.getStarRating() != null ) {
                ans.append( " Rating: ")
					.append( prog.getStarRating() ).append( "<br>" );
            }
            ans.append( "</p>" ).append( lineBreak );

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
	
}


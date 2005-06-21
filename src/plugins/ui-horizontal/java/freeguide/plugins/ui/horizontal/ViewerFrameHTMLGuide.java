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

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.PersonalizedHTMLGuide;

import freeguide.lib.general.StringHelper;
import freeguide.lib.general.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import java.util.Date;
import java.util.logging.Level;

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
    private HorizontalViewer controller;

    /**
     * DOCUMENT ME!
     *
     * @param controller The screen on which this is displayed
     */
    public ViewerFrameHTMLGuide( HorizontalViewer controller )
    {
        super(  );

        this.controller = controller;

        // Scrolls the program guide to show the program when
        // the user clicks the program name in the HTML Guide
        addHyperlinkListener( new HTMLGuideListener( controller ) );

    }

    //{{{ Printed Guide

    /**
     * Get the HTML version of the listing and show it in the printed guide
     */
    public void update(  )
    {

        StringWriter str = new StringWriter(  );

        try
        {
            new PersonalizedHTMLGuide(  ).createHTML( 
                str, controller.getLocalizer(  ),
                new Date( controller.theDate ), controller.currentData,
                controller.htmlDateFormat,
                controller.config.display24time
                ? HorizontalViewer.timeFormat24Hour
                : HorizontalViewer.timeFormat12Hour, true );
        }
        catch( IOException ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE,
                "Error construct personalized HTML guide for screen", ex );
        }

        setText( str.toString(  ) );

        setCaretPosition( 0 );
    }

    /**
     * Description of the Method
     */
    public void writeOutAsHTML(  )
    {

        // Make a file in the default location
        File f =
            new File( 
                Application.getInstance(  ).getWorkingDirectory(  )
                + "/guide.html" );

        try
        {

            //IOException
            BufferedWriter buffy =
                new BufferedWriter( 
                    new OutputStreamWriter( 
                        new FileOutputStream( f ), "UTF-8" ) );

            new PersonalizedHTMLGuide(  ).createHTML( 
                buffy, controller.getLocalizer(  ),
                new Date( controller.theDate ), controller.currentData,
                controller.htmlDateFormat,
                controller.config.display24time
                ? HorizontalViewer.timeFormat24Hour
                : HorizontalViewer.timeFormat12Hour, false );

            buffy.close(  );

            String cmd =
                StringHelper.replaceAll( 
                    Application.getInstance(  ).getBrowserCommand(  ),
                    "%filename%", f.getPath(  ) );

            Utils.execNoWait( cmd );

        }

        catch( java.io.IOException ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING, "Error write HTML guide", ex );
        }
    }
}

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

import java.awt.*;

import java.text.*;

import javax.swing.*;

/**
 * A class that displays the details of a single programme
 *
 * @author Andy Balaam
 * @version 1
 */
public class ProgrammeDetailsJPanel extends javax.swing.JPanel
{

    /** This object's parent window. */
    private ViewerFrame parentViewerFrame;

    /** The JEditorPane showing the actual programme description. */
    private JEditorPane editorPane;
    private JScrollPane scrollPane;

    ProgrammeDetailsJPanel( ViewerFrame parentViewerFrame )
    {
        super(  );

        this.parentViewerFrame = parentViewerFrame;

        createUI(  );

        updateProgramme( null );

    }

    private void createUI(  )
    {
        setLayout( new BorderLayout(  ) );

        editorPane = new JEditorPane(  );
        editorPane.setEditable( false );
        editorPane.setContentType( "text/html" );

        scrollPane = new JScrollPane(  );
        scrollPane.setViewportView( editorPane );

        add( scrollPane, BorderLayout.CENTER );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public void updateProgramme( Programme programme )
    {

        // Find out whether we're in the 24 hour clock
        boolean draw24time =
            FreeGuide.prefs.screen.getBoolean( "display_24hour_time", true );

        // And get the time format from that
        SimpleDateFormat timeFormat;

        if( draw24time )
        {
            timeFormat = parentViewerFrame.timeFormat24Hour;
        }
        else
        {
            timeFormat = parentViewerFrame.timeFormat12Hour;
        }

        ProgrammeFormat programmeFormat =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FORMAT, timeFormat, true );

        programmeFormat.setOnScreen( false );

        if( programme != null )
        {
            editorPane.setText( 
                programmeFormat.formatForProgrammeDetailsJPanel( programme ) );

        }
        else
        {

            StringBuffer buff = new StringBuffer(  );

            ProgrammeFormat.appendStyleSheet( buff );

            buff.append( "<p>" );
            buff.append( FreeGuide.msg.getString( "no_programme_selected" ) );
            buff.append( "</p></body></html>" );

            editorPane.setText( buff.toString(  ) );

        }

        editorPane.setCaretPosition( 0 );

    }
}

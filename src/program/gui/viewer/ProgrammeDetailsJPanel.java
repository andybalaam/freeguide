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
 *@author     Andy Balaam
 *@created    02 July 2004
 *@version    1
 */
public class ProgrammeDetailsJPanel extends javax.swing.JPanel {

    ProgrammeDetailsJPanel( ViewerFrame parentViewerFrame ) {
        super();
        
        this.parentViewerFrame = parentViewerFrame;
        
        createUI();
        
        updateProgramme( null );
        
    }
    
    private void createUI() {
        
        setLayout( new BorderLayout() );
        
        /*JPanel topPanel = new JPanel( new BorderLayout() );
        topPanel.add( new JButton( "Close" ), BorderLayout.WEST );
        
        add( topPanel, BorderLayout.NORTH );*/
        
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        
        scrollPane = new JScrollPane();
        scrollPane.setViewportView( editorPane );
        
        add( scrollPane, BorderLayout.CENTER );
        
    }
    
    public void updateProgramme( Programme programme ) {
        
        // Find out whether we're in the 24 hour clock
        boolean draw24time = FreeGuide.prefs.screen.getBoolean(
            "display_24hour_time", true );
        
        // And get the time format from that
        SimpleDateFormat timeFormat = (
            draw24time ? parentViewerFrame.timeFormat24Hour :
                parentViewerFrame.timeFormat12Hour );
        
        ProgrammeFormat programmeFormat = new ProgrammeFormat(
            ProgrammeFormat.HTML_FORMAT, timeFormat, true );
        
        programmeFormat.setOnScreen(false);
        
        if( programme != null ) {
        
            editorPane.setText( programmeFormat.extraLongFormat( programme ) );
            
        } else {
            
            StringBuffer buff = new StringBuffer();
            
            ProgrammeFormat.appendStyleSheet( buff );
            
            buff.append( "<p>" );
            buff.append( FreeGuide.msg.getString( "no_programme_selected" ) );
            buff.append( "</p></body></html>" );
            
            editorPane.setText( buff.toString() );
            
        }

        editorPane.setCaretPosition(0);
        
    }
    
    /**
     * This object's parent window.
     */
    private ViewerFrame parentViewerFrame;
    
    /**
     * The JEditorPane showing the actual programme description.
     */
    private JEditorPane editorPane;
    private JScrollPane scrollPane;
    
}


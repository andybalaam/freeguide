/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.gui.dialogs;

import freeguide.lib.fgspecific.Application;

import freeguide.lib.general.FileHelper;
import freeguide.lib.general.Utils;

import javax.swing.JDialog;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

// To Be Added Shortly (Rob)
//import freeguide.lib.general.*;

/**
 * A small About box.
 *
 * @author Andy Balaam
 * @version 2
 */
public class AboutFrame extends JDialog implements HyperlinkListener
{

    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;

    /**
     * Constructor for the About object
     *
     * @param parent Description of the Parameter
     * @param modal Description of the Parameter
     */
    public AboutFrame( java.awt.Frame parent, boolean modal )
    {
        super( 
            parent, Application.getInstance(  ).getLocalizedMessage( "about" ),
            modal );
        initComponents(  );
        jTextPane1.setContentType( "text/html" );

        StringBuffer str = new StringBuffer(  );
        str.append( 
            "<font face=\"verdana, arial, helvetica, helv, sans serif\" size=3>" );
        str.append( 
            "<table width=\"100%\" height=\"100%\" border=\"0\"><tr><td height=\"100%\" align=\"center\">" );
        str.append( 
            "<h1><font face=\"arial, helvetica, helv, sans serif\" size=\"5\">FreeGuide " )
           .append( Application.VERSION.getDotFormat(  ) ).append( 
            "</font></h1>" );
        str.append( "<p>" );
        str.append( 
            Application.getInstance(  ).getLocalizedMessage( 
                "free_software_by_contributors" ) );
        str.append( "</p><p>" );
        str.append( Application.getInstance(  ).getLocalizedMessage( "web" ) );
        str.append( 
            ": <a href=\"http://freeguide-tv.sourceforge.net\">freeguide-tv.sourceforge.net</a></p><p>" );
        str.append( Application.getInstance(  ).getLocalizedMessage( "mail" ) );
        str.append( 
            ": <a href=\"mailto:freeguide-tv-devel@lists.sourceforge.net\">freeguide-tv-devel@lists.sourceforge.net</a></p>" );
        str.append( "</td></tr></table>" );
        str.append( "</font>" );
        jTextPane1.setText( str.toString(  ) );
        Utils.centreDialog( parent, this );
        jTextPane1.addHyperlinkListener( this );
    }

    private void initComponents(  )
    {
        getContentPane(  ).setLayout( new java.awt.BorderLayout( 2, 2 ) );
        addWindowListener( 
            new java.awt.event.WindowAdapter(  )
            {
                public void windowClosing( java.awt.event.WindowEvent evt )
                {
                    closeDialog( evt );
                }
            } );
        jButton1 =
            new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );
        jButton1.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    jButton1ActionPerformed( evt );
                }
            } );
        getContentPane(  ).add( jButton1, java.awt.BorderLayout.SOUTH );
        jTextPane1 = new javax.swing.JTextPane(  );
        jTextPane1.setBackground( new java.awt.Color( 225, 255, 255 ) );
        jTextPane1.setEditable( false );
        jTextPane1.setContentType( "text/html\n" );
        jScrollPane1 = new javax.swing.JScrollPane( jTextPane1 );
        getContentPane(  ).add( jScrollPane1, java.awt.BorderLayout.CENTER );
        pack(  );

        setSize( new java.awt.Dimension( 416, 245 ) );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void jButton1ActionPerformed( java.awt.event.ActionEvent evt )
    {
        setVisible( false );
        dispose(  );
    }

    /**
     * Closes the dialog
     *
     * @param evt Description of the Parameter
     */
    private void closeDialog( java.awt.event.WindowEvent evt )
    {
        setVisible( false );
        dispose(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param e DOCUMENT_ME!
     */
    public void hyperlinkUpdate( HyperlinkEvent e )
    {

        if( HyperlinkEvent.EventType.ACTIVATED == e.getEventType(  ) )
        {
            FileHelper.openURL( e.getURL(  ) );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        new AboutFrame( new javax.swing.JFrame(  ), true ).setVisible( true );
    }
}

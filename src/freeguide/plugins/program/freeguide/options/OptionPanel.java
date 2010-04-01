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
package freeguide.plugins.program.freeguide.options;

import freeguide.common.gui.FGDialog;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

/*
 *  A superclass for all the screens appearing on the Options screen
 *
 * @author     Andy Balaam
 * @created    9 Dec 2003
 * @version    1
 */
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public abstract class OptionPanel extends JPanel
{
    private boolean isConstructed = false;
    protected FGDialog parent;
    private String lb = System.getProperty( "line.separator" );

    /**
     * Creates a new OptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public OptionPanel( FGDialog parent )
    {
        this.parent = parent;
    }

    /**
     * DOCUMENT_ME!
     */
    public void construct(  )
    {
        if( !isConstructed )
        {
            doConstruct(  );
            isConstructed = true;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean save(  )
    {
        if( isConstructed )
        {
            return doSave(  );
        }
        else
        {
            return false;
        }
    }

    protected void load(  )
    {
        doLoad( "" );
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
        doLoad( "default-" );
    }

    protected String lineBreakise( String[] lines )
    {
        String withLineBreaks = new String(  );

        for( int i = 0; i < lines.length; i++ )
        {
            withLineBreaks += ( lines[i] + lb );
        }

        return withLineBreaks;
    }

    protected String[] unlineBreakise( String withLineBreaks )
    {
        return withLineBreaks.split( "\n" );
    }

    protected abstract boolean doSave(  );

    protected abstract void doLoad( String prefix );

    protected abstract void doConstruct(  );

    // ----------------------------------------
    protected JLabel newLeftJLabel( String text )
    {
        JLabel ans = new JLabel( text + " " );
        ans.setBorder(
            BorderFactory.createEtchedBorder( EtchedBorder.LOWERED ) );
        ans.setHorizontalAlignment( JLabel.RIGHT );

        return ans;
    }

    protected JLabel newRightJLabel(  )
    {
        JLabel ans = new JLabel(  );

        return ans;
    }

    protected JButton newRightJButton( String text )
    {
        JButton ans = new JButton( text );
        Dimension size = new Dimension( 100, 24 );
        ans.setPreferredSize( size );

        return ans;
    }

    protected JButton newLeftJButton( String text )
    {
        JButton ans = new JButton( text );
        ans.setHorizontalTextPosition( JButton.RIGHT );

        return ans;
    }

    protected JSlider newRightJSlider( int min, int max )
    {
        JSlider ans = new JSlider( JSlider.HORIZONTAL, min, max, min );
        Dimension size = new Dimension( 10, 18 );
        ans.setPreferredSize( size );

        return ans;
    }

    protected JTextArea newRightJTextArea(  )
    {
        JTextArea ans = new JTextArea(  );
        ans.setLineWrap( true );

        return ans;
    }

    protected JTextField newRightJTextField(  )
    {
        JTextField ans = new JTextField(  );

        return ans;
    }

    protected JTextField newMiddleJTextField(  )
    {
        JTextField ans = new JTextField(  );

        return ans;
    }

    protected JComboBox newRightJComboBox( Object[] options )
    {
        JComboBox ans = new JComboBox( options );

        return ans;
    }
}

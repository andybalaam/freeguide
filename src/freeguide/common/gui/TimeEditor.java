package freeguide.common.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Keymap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class TimeEditor extends JTextField
{
    /** Mask for time string. */
    protected static final Pattern TIME_PATTERN =
        Pattern.compile( "(\\d{1,2}):(\\d{2})" );
    protected final MODE mode;
    protected final DocumentFilter filter =
        new DocumentFilter(  )
        {
            public void insertString( 
                FilterBypass fb, int offset, String string, AttributeSet attr )
                throws BadLocationException
            {
                replace( fb, offset, 0, string, null );
            }

            public void remove( FilterBypass fb, int offset, int length )
                throws BadLocationException
            {
                replace( fb, offset, length, "", null );
            }

            public void replace( 
                FilterBypass fb, int offset, int length, String text,
                AttributeSet attrs ) throws BadLocationException
            {
                final String value =
                    fb.getDocument(  )
                      .getText( 0, fb.getDocument(  ).getLength(  ) );
                final String newValue =
                    value.substring( 0, offset ) + text
                    + value.substring( offset + length );

                final Matcher ma = TIME_PATTERN.matcher( newValue );

                if( !ma.matches(  ) )
                {
                    return;
                }

                int v1 = Integer.parseInt( ma.group( 1 ) );
                int v2 = Integer.parseInt( ma.group( 2 ) );

                if( v1 > getMaxGroupValue( 0 ) )
                {
                    return;
                }

                if( v2 > getMaxGroupValue( 1 ) )
                {
                    return;
                }

                fb.replace( offset, length, text, null );
            }
        };

/**
     * Creates a new TimeEditor object.
     *
     * @param mode DOCUMENT ME!
     */
    public TimeEditor( final MODE mode )
    {
        final AbstractDocument doc = (AbstractDocument)getDocument(  );
        doc.setDocumentFilter( filter );
        setupKeymap(  );
        this.mode = mode;
        setColumns( 5 );
    }

    protected void setupKeymap(  )
    {
        final Keymap keymap =
            JTextField.addKeymap( this.getClass(  ).getName(  ), null );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ),
            new KeyPressed( KeyEvent.VK_UP ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ),
            new KeyPressed( KeyEvent.VK_DOWN ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ),
            new KeyPressed( KeyEvent.VK_LEFT ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ),
            new KeyPressed( KeyEvent.VK_RIGHT ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_HOME, 0 ),
            new KeyPressed( KeyEvent.VK_HOME ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_END, 0 ),
            new KeyPressed( KeyEvent.VK_END ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_0, 0 ),
            new KeyPressed( KeyEvent.VK_0 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_1, 0 ),
            new KeyPressed( KeyEvent.VK_1 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_2, 0 ),
            new KeyPressed( KeyEvent.VK_2 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_3, 0 ),
            new KeyPressed( KeyEvent.VK_3 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_4, 0 ),
            new KeyPressed( KeyEvent.VK_4 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_5, 0 ),
            new KeyPressed( KeyEvent.VK_5 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_6, 0 ),
            new KeyPressed( KeyEvent.VK_6 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_7, 0 ),
            new KeyPressed( KeyEvent.VK_7 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_8, 0 ),
            new KeyPressed( KeyEvent.VK_8 ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_9, 0 ),
            new KeyPressed( KeyEvent.VK_9 ) );
        /*
         * keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
         * 0), new BackwardAction( DefaultEditorKit.backwardAction));
         * keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
         * 0), new ForwardAction( DefaultEditorKit.forwardAction));
         * keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_HOME,
         * 0), new BeginAction( DefaultEditorKit.beginAction));
         * keymap.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_END,
         * 0), new EndAction( DefaultEditorKit.endAction));
         */
        setKeymap( keymap );
    }

    protected int getGroupValue( int groupNum )
    {
        final Matcher ma = TIME_PATTERN.matcher( getText(  ) );

        if( !ma.matches(  ) )
        {
            return -1;
        }

        return Integer.parseInt( ma.group( groupNum + 1 ) );
    }

    protected int getMaxGroupValue( final int groupNum )
    {
        if( groupNum == 0 )
        {
            return ( mode == MODE.MINUTES ) ? 23 : 59;
        }
        else
        {
            return 59;
        }
    }

    protected void setGroupValue( int groupNum, int value )
    {
        final Matcher ma = TIME_PATTERN.matcher( getText(  ) );

        if( !ma.matches(  ) )
        {
            if( groupNum == 0 )
            {
                setText( valueToString( value ) + ":00" );
            }
            else
            {
                setText( "00:" + valueToString( value ) );
            }
        }

        if( value > getMaxGroupValue( groupNum ) )
        {
            value = 0;
        }
        else if( value < 0 )
        {
            value = getMaxGroupValue( groupNum );
        }

        if( groupNum == 0 )
        {
            setText( valueToString( value ) + ":" + ma.group( 2 ) );
        }
        else
        {
            setText( ma.group( 1 ) + ":" + valueToString( value ) );
        }
    }

    protected void setPosValue( int pos, char value )
    {
        final AbstractDocument doc = (AbstractDocument)getDocument(  );
        int st = getSelectionStart(  );
        int len = getSelectionEnd(  ) - getSelectionStart(  );

        try
        {
            if( ( mode == MODE.MINUTES ) && ( pos == 0 ) )
            {
                if( Integer.parseInt( doc.getText( 1, 1 ) ) > 3 )
                {
                    doc.replace( 1, 1, "0", null );
                }
            }

            doc.replace( st, len, Character.toString( value ), null );
        }
        catch( Exception ex )
        {
        }
    }

    protected String valueToString( int value )
    {
        return ( ( value >= 10 ) ? "" : "0" ) + value;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public long getValue(  )
    {
        long result = 0;
        long v1 = getGroupValue( 0 );
        long v2 = getGroupValue( 1 );

        switch( mode )
        {
        case MINUTES:
            result = ( ( v1 * 60L ) + v2 ) * 60L * 1000L;

            break;

        case SECONDS:
            result = ( ( v1 * 60L ) + v2 ) * 1000L;

            break;
        }

        return result;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param value DOCUMENT_ME!
     */
    public void setValue( final long value )
    {
        int v1 = 0;
        int v2 = 0;

        switch( mode )
        {
        case MINUTES:
            v1 = (int)( ( value / 60L / 60L / 1000L ) % 24L );
            v2 = (int)( ( value / 60L / 1000L ) % 60L );

            break;

        case SECONDS:
            v1 = (int)( ( value / 60L / 1000L ) % 60L );
            v2 = (int)( ( value / 1000L ) % 60L );

            break;
        }

        setText( valueToString( v1 ) + ":" + valueToString( v2 ) );
    }

    protected class KeyPressed extends AbstractAction
    {
        protected final int key;

/**
         * Creates a new KeyPressed object.
         *
         * @param key DOCUMENT ME!
         */
        public KeyPressed( final int key )
        {
            this.key = key;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {
            int currentPos = getSelectedPos(  );

            switch( key )
            {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:

                int diff = ( key == KeyEvent.VK_UP ) ? ( +1 ) : ( -1 );
                int groupNum = ( currentPos < 2 ) ? 0 : 1;
                setGroupValue( groupNum, getGroupValue( groupNum ) + diff );
                setSelectPos( currentPos );

                break;

            case KeyEvent.VK_HOME:
                setSelectPos( 0 );

                break;

            case KeyEvent.VK_LEFT:
                setSelectPos( 
                    ( currentPos > 0 ) ? ( currentPos - 1 ) : currentPos );

                break;

            case KeyEvent.VK_END:
                setSelectPos( 3 );

                break;

            case KeyEvent.VK_RIGHT:
                setSelectPos( 
                    ( currentPos < 3 ) ? ( currentPos + 1 ) : currentPos );

                break;

            default:

                if( ( key >= KeyEvent.VK_0 ) && ( key <= KeyEvent.VK_9 ) )
                {
                    setPosValue( currentPos, (char)key );
                    setSelectPos( 
                        ( currentPos < 3 ) ? ( currentPos + 1 ) : currentPos );
                }
            }
        }

        protected int getSelectedPos(  )
        {
            int result = getSelectionStart(  );

            if( result >= 2 )
            {
                result--;
            }

            return result;
        }

        protected void setSelectPos( int pos )
        {
            if( pos >= 2 )
            {
                setSelectionStart( pos + 1 );
                setSelectionEnd( pos + 2 );
            }
            else
            {
                setSelectionStart( pos );
                setSelectionEnd( pos + 1 );
            }
        }
    }
    public static enum MODE
    {MINUTES,
        SECONDS;
    }
}

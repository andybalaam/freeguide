package freeguide.common.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.text.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;

/**
 * Text field editor for time field.
 *
 * @author Alex Buloichik
 */
public class TimeEditor extends JFormattedTextField
{
    /** Mask for time string. */
    protected static final Pattern TIME_PATTERN =
        Pattern.compile( "(\\d{1,2}):(\\d{2})" );

    /** Current editor mode. */
    protected final MODE mode;

/**
     * Creates a new TimeEditor object.
     * 
     * @param mode
     *            DOCUMENT ME!
     */
    public TimeEditor( final MODE mode )
    {
        setupKeymap(  );
        this.mode = mode;
        setColumns( 5 );
        setFormatterFactory( 
            new AbstractFormatterFactory(  )
            {
                public AbstractFormatter getFormatter( JFormattedTextField tf )
                {
                    return new AbstractFormatter(  )
                        {
                            public Object stringToValue( String text )
                                throws ParseException
                            {
                                final Matcher m = TIME_PATTERN.matcher( text );

                                if( !m.matches(  ) )
                                {
                                    throw new ParseException( 
                                        "Error parse: " + text, 0 );
                                }

                                long v1 = Integer.parseInt( m.group( 1 ) );
                                long v2 = Integer.parseInt( m.group( 2 ) );

                                long result = 0;

                                switch( mode )
                                {
                                case MINUTES:
                                    result = ( ( v1 * 60L ) + v2 ) * 60L * 1000L;

                                    break;

                                case SECONDS:
                                    result = ( ( v1 * 60L ) + v2 ) * 1000L;

                                    break;
                                }

                                return new Long( result );
                            }

                            public String valueToString( Object value )
                                throws ParseException
                            {
                                if( value == null )
                                {
                                    return null;
                                }

                                long v = ( (Long)value ).longValue(  );
                                int v1 = 0;
                                int v2 = 0;

                                switch( mode )
                                {
                                case MINUTES:
                                    v1 = (int)( ( v / 60L / 60L / 1000L ) % 24L );
                                    v2 = (int)( ( v / 60L / 1000L ) % 60L );

                                    break;

                                case SECONDS:
                                    v1 = (int)( ( v / 60L / 1000L ) % 60L );
                                    v2 = (int)( ( v / 1000L ) % 60L );

                                    break;
                                }

                                return formatTo2Digit( v1 ) + ':'
                                + formatTo2Digit( v2 );
                            }
                        };
                }
            } );
    }

    protected void setupKeymap(  )
    {
        final Keymap keymap =
            JTextField.addKeymap( 
                this.getClass(  ).getName(  ), getKeymap(  ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ),
            new KeyPressed( KeyEvent.VK_UP ) );
        keymap.addActionForKeyStroke( 
            KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ),
            new KeyPressed( KeyEvent.VK_DOWN ) );
        setKeymap( keymap );
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

    protected static String formatTo2Digit( int value )
    {
        if( value >= 10 )
        {
            return Integer.toString( value );
        }
        else
        {
            return '0' + Integer.toString( value );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public long getTimeValue(  )
    {
        return ( (Long)getValue(  ) ).longValue(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param value DOCUMENT_ME!
     */
    public void setTimeValue( final long value )
    {
        setValue( new Long( value ) );
    }

    protected class KeyPressed extends AbstractAction
    {
        protected final int key;

/**
         * Creates a new KeyPressed object.
         * 
         * @param key
         *            DOCUMENT ME!
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
            String text = getText(  );
            int div = text.indexOf( ':' );

            if( div < 0 )
            {
                return;
            }

            int diff = ( key == KeyEvent.VK_UP ) ? ( +1 ) : ( -1 );

            try
            {
                int pos = getCaretPosition(  );

                int val =
                    Integer.parseInt( 
                        ( pos <= div ) ? text.substring( 0, div )
                                       : text.substring( div + 1 ) );
                val += diff;

                if( val > getMaxGroupValue( ( pos <= div ) ? 1 : 2 ) )
                {
                    val = 0;
                }

                if( val < 0 )
                {
                    val = getMaxGroupValue( ( pos <= div ) ? 1 : 2 );
                }

                if( pos <= div )
                {
                    text = formatTo2Digit( val ) + text.substring( div );
                }
                else
                {
                    text = text.substring( 0, div + 1 )
                        + formatTo2Digit( val );
                }

                setText( text );
                setCaretPosition( pos );
            }
            catch( Exception ex )
            {
            }
        }
    }

/** Modes of editor. */
    public static enum MODE
    {MINUTES,
        SECONDS;
    }
}

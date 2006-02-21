package freeguide.plugins.ui.vertical.simple;

import java.awt.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * DOCUMENT ME!
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class VerticalViewerConfig
{

    /** Time formatter for 24 hour clock */
    public final static SimpleDateFormat listTimeFormat24Hour =
        new SimpleDateFormat( "HH:mm" );

    /** How to format dates in the list */
    public final static DateFormat listDateFormat =
        DateFormat.getDateInstance(  );

    /** Default colour of a movie */
    public final static Color colorMovie = new Color( 255, 230, 230 );

    /** Default colour of a normal programme */
    public final static Color colorNonTicked = Color.WHITE;

    /** Default colour of the channel labels */
    public final static Color colorChannel = new Color( 245, 245, 255 );

    /** Default colour of a ticked programme */
    public final static Color colorTicked = new Color( 204, 255, 204 );

    /** Default colour of a selected programme */
    public final static Color colorSelected = new Color( 204, 204, 204 );
}


//public class VerticalViewerConfig

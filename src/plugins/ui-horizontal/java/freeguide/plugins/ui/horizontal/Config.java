package freeguide.plugins.ui.horizontal;

import freeguide.lib.general.PreferencesHelper;
import freeguide.lib.general.Time;

import java.awt.Color;
import java.awt.Font;

/**
 * DOCUMENT ME!
 *
 * @author Alex Bulochik (alex at zaval.org)
 */
public class Config
{

    /** DOCUMENT ME! */
    public String currentChannelSetName;

    /** DOCUMENT ME! */
    public int positionSplitPaneVertical = 100;

    /** DOCUMENT ME! */
    public int positionSplitPaneHorizontalTop = 150;

    /** DOCUMENT ME! */
    public int positionSplitPaneHorizontalBottom = 400;

    /** DOCUMENT ME! */
    public int sizeChannelHeight = 28;

    /** DOCUMENT ME! */
    public int sizeHalfVerGap = 1;

    /** DOCUMENT ME! */
    public int sizeHalfHorGap = 1;

    /** DOCUMENT ME! */
    public int sizeProgrammePanelWidth = 8000;

    /** Default width of the channels scrolling panel */
    public int sizeChannelPanelWidth = 400;

    /** Default colour of a movie */
    public Color colorMovie = new Color( 255, 230, 230 );

    /** Default colour of a normal programme */
    public Color colorNonTicked = Color.WHITE;

    /** Default colour of the channel labels */
    public Color colorChannel = new Color( 245, 245, 255 );

    /** DOCUMENT ME! */
    public boolean displayAlignToLeft = true;

    /** DOCUMENT ME! */
    public String fontName = "Dialog";

    /** DOCUMENT ME! */
    public int fontStyle = Font.PLAIN;

    /** DOCUMENT ME! */
    public int fontSize = 12;

    /** DOCUMENT ME! */
    public boolean displayTime = true;

    /** DOCUMENT ME! */
    public boolean display24time = true;

    /** DOCUMENT ME! */
    public boolean displayDelta = true;

    /** DOCUMENT ME! */
    public boolean displayTooltips = false;

    /** DOCUMENT ME! */
    public Time dayStartTime = new Time( 6, 0 );

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object clone(  )
    {

        Config result = new Config(  );

        PreferencesHelper.cloneObject( this, result );

        return result;

    }
}

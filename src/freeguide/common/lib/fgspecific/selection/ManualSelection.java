package freeguide.common.lib.fgspecific.selection;

import freeguide.common.lib.fgspecific.data.TVProgramme;

import java.util.Map;
import java.util.TreeMap;

/**
 * Class for store manual selection or deselection of TV programme.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ManualSelection
{
    /**
     * DOCUMENT ME!
     */
    public static Class reminders_KEY_TYPE = String.class;

    /** DOCUMENT ME! */
    public static Class reminders_VALUE_TYPE = Boolean.class;

    /** DOCUMENT ME! */
    public String channelID;

    /** DOCUMENT ME! */
    public long programmeTime;

    /** DOCUMENT ME! */
    public boolean selected;

    /** PROGRAMM IS HIGHLIGHTED */
    public boolean highlighted;

    /** Reminders which should remind or not this programme. */
    public Map<String, Boolean> reminders = new TreeMap<String, Boolean>(  );

/**
     * Creates a new ManualSelection object.
     */
    public ManualSelection(  )
    {
    }

/**
     * Creates a new ManualSelection object.
     * 
     * @param programme
     *            DOCUMENT ME!
     * @param selected
     *            DOCUMENT ME!
     */
    public ManualSelection( final TVProgramme programme )
    {
        this.channelID = programme.getChannel(  ).getID(  );
        this.programmeTime = programme.getStart(  );
    }

/**
     * Creates a new ManualSelection object.
     * 
     * @param programme
     *            DOCUMENT ME!
     * @param selected
     *            DOCUMENT ME!
     */
    public ManualSelection( 
        final TVProgramme programme, final boolean selected,
        final boolean highlighted )
    {
        this.channelID = programme.getChannel(  ).getID(  );

        this.programmeTime = programme.getStart(  );

        this.selected = selected;

        this.highlighted = highlighted || selected;
    }

    /**
     * Test programme for matches selection.
     *
     * @param programme programme for test
     *
     * @return true if matched
     */
    public boolean matches( final TVProgramme programme )
    {
        return ( programmeTime == programme.getStart(  ) )
        && channelID.equals( programme.getChannel(  ).getID(  ) );
    }

    /**
     * Get Highlight
     *
     * @return Returns the highlighted status.
     */
    public boolean isHighlighted(  )
    {
        return highlighted;

    }

    /**
     * Set Highlight
     *
     * @param highlighted the highlighted to set.
     */
    public void setHighlighted( final boolean highlighted )
    {
        this.highlighted = highlighted;

    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the selected status.
     */
    public boolean isSelected(  )
    {
        return selected;

    }

    /**
     * DOCUMENT ME!
     *
     * @param selected The selected to set.
     */
    public void setSelected( final boolean selected )
    {
        this.selected = selected;

    }
}

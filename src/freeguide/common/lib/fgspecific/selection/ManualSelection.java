package freeguide.common.lib.fgspecific.selection;

import freeguide.common.lib.fgspecific.data.TVProgramme;

/**
 * Class for store manual selection or deselection of TV programme.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ManualSelection
{
    /** DOCUMENT ME! */
    public String channelID;

    /** DOCUMENT ME! */
    public long programmeTime;

    /** DOCUMENT ME! */
    public boolean selected;

    /** PROGRAMM IS HIGHLIGHTED */
    public boolean highlighted;

/**
     * Creates a new ManualSelection object.
     */
    public ManualSelection(  )
    {
    }

/**
     * Creates a new ManualSelection object.
     *
     * @param programme DOCUMENT ME!
     * @param selected DOCUMENT ME!
     */
    public ManualSelection( 
        final TVProgramme programme, final boolean selected, final boolean highlighted )
    {
        this.channelID = programme.getChannel(  ).getID(  );

        this.programmeTime = programme.getStart(  );

        this.selected = selected;

        this.highlighted = highlighted || selected;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean matches( final TVProgramme programme )
    {
        if( 
            channelID.equals( programme.getChannel(  ).getID(  ) )
                && ( programmeTime == programme.getStart(  ) ) )
        {
            return true;

        }

        return false;

    }

    /**
     * Get Highlight
     * 
     * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
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
     * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
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

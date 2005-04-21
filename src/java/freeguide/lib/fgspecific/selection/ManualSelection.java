package freeguide.lib.fgspecific.selection;

import freeguide.lib.fgspecific.data.TVProgramme;

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
        final TVProgramme programme, final boolean selected )
    {
        this.channelID = programme.getChannel(  ).getID(  );

        this.programmeTime = programme.getStart(  );

        this.selected = selected;

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

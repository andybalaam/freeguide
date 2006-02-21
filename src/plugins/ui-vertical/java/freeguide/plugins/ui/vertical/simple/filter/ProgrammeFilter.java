package freeguide.plugins.ui.vertical.simple.filter;

import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ui.vertical.simple.ProgrammeFilterModel;

/**
 * Interface for tv program filters
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public abstract class ProgrammeFilter
{

    /** The title in the list */
    String strTitle = "unnamed";

    /** The model which we filter */
    ProgrammeFilterModel filterModel = null;

    /**
     * If the given program shall be displayed or not
     *
     * @return true if the program shall be displayed
     */
    public abstract boolean showProgramme( TVProgramme programme );

    /**
     * DOCUMENT_ME!
     *
     * @param strTitle DOCUMENT_ME!
     */
    public void setTitle( String strTitle )
    {
        this.strTitle = strTitle;
    }

    //public void setTitle(String strTitle)

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getTitle(  )
    {

        return this.strTitle;
    }

    //public String getTitle()

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {

        return this.strTitle;
    }

    //public String toString()

    /**
     * Used by the model, so that the filter change notification can work
     *
     * @param filterModel DOCUMENT ME!
     */
    public void setModel( ProgrammeFilterModel filterModel )
    {
        this.filterModel = filterModel;
    }

    //public void setModel(ProgrammeFilterModel filterModel)

    /**
     * Filters have to call this method if some filter criteria change, so
     * that the model can re-apply the filters
     */
    public void notifyFilterChange(  )
    {

        if( this.filterModel != null )
        {
            this.filterModel.filterChanged(  );
        }
    }

    //public void notifyFilterChange()

    /**
     * DOCUMENT_ME!
     */
    public void deactivate(  )
    {

        //Override me!
        System.err.println( 
            "The deactivate() method of ProgrammeFilter in "
            + getClass(  ).getName(  ) + " should be overridden" );
    }

    //public void deactivate()

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String exportSettings(  )
    {

        //Override me!
        System.err.println( 
            "The exportSettings() method of ProgrammeFilter in "
            + getClass(  ).getName(  ) + " should be overridden" );

        return "";
    }

    /**
     * DOCUMENT_ME!
     *
     * @param strSettings DOCUMENT_ME!
     */
    public void importSettings( String strSettings )
    {

        //Override me!
        System.err.println( 
            "The importSettings() method of ProgrammeFilter in "
            + getClass(  ).getName(  ) + " should be overridden" );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isDeactivated(  )
    {

        //Override me!
        System.err.println( 
            "The isDeactivated() method of ProgrammeFilter in "
            + getClass(  ).getName(  ) + " should be overridden" );

        return false;
    }
}


//public abstract class ProgrammeFilter

package freeguide.plugins.ui.vertical.simple.filter;

import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.plugins.ui.vertical.simple.ProgrammeFilterModel;


/**
 * Interface for tv program filters
 *
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public abstract class ProgrammeFilter
{
    /**
     * The title in the list
     */
    String strTitle = "unnamed";

    /**
     * The model which we filter
     */
    ProgrammeFilterModel filterModel = null;


    /**
     * If the given program shall be displayed or not
     * @return true if the program shall be displayed
     */
    public abstract boolean showProgramme(TVProgramme programme);



    public void setTitle(String strTitle)
    {
        this.strTitle = strTitle;
    }//public void setTitle(String strTitle)



    public String getTitle()
    {
        return this.strTitle;
    }//public String getTitle()



    public String toString()
    {
        return this.strTitle;
    }//public String toString()



    /**
     * Used by the model, so that the filter change
     * notification can work
     */
    public void setModel(ProgrammeFilterModel filterModel)
    {
        this.filterModel = filterModel;
    }//public void setModel(ProgrammeFilterModel filterModel)



    /**
     * Filters have to call this method
     * if some filter criteria change, so that
     * the model can re-apply the filters
     */
    public void notifyFilterChange()
    {
        if (this.filterModel != null) {
            this.filterModel.filterChanged();
        }
    }//public void notifyFilterChange()



    public void deactivate()
    {
        //Override me!
        System.err.println("The deactivate() method of ProgrammeFilter in " + getClass().getName() + " should be overridden");
    }//public void deactivate()



    public String exportSettings()
    {
        //Override me!
        System.err.println("The exportSettings() method of ProgrammeFilter in " + getClass().getName() + " should be overridden");
        return "";
    }



    public void importSettings(String strSettings)
    {
        //Override me!
        System.err.println("The importSettings() method of ProgrammeFilter in " + getClass().getName() + " should be overridden");
    }



    public boolean isDeactivated()
    {
        //Override me!
        System.err.println("The isDeactivated() method of ProgrammeFilter in " + getClass().getName() + " should be overridden");
        return false;
    }

}//public abstract class ProgrammeFilter

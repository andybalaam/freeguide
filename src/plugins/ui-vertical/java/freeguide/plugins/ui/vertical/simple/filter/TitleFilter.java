package freeguide.plugins.ui.vertical.simple.filter;

import freeguide.lib.fgspecific.data.TVProgramme;


/**
 * Filters tv programs by title
 *
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class TitleFilter extends ProgrammeFilter
{
    protected String searchString = null;



    public TitleFilter()
    {
    }//public TitleFilter()



    public void setSearchString(String searchString)
    {
        this.setSearchString(searchString, true);
    }//public void setSearchString(String searchString)



    public void setSearchString(String searchString, boolean notify)
    {
        if (searchString != null && searchString.equals("")) {
            searchString = null;
        }
        this.searchString = searchString;
        //TODO: Translation
        this.setTitle("Search for \"" + this.searchString + "\"");

        if (notify) {
            this.notifyFilterChange();
        }
    }//public void setSearchString(String searchString, boolean notify)



    public String getSearchString()
    {
        return this.searchString;
    }//public String getSearchString()



    public boolean showProgramme(TVProgramme programme)
    {
        //TODO: Make search case insensitive
        return this.searchString == null ||programme.getTitle().indexOf(this.searchString) != -1;
    }//public boolean showProgramme(TVProgramme programme)



    public boolean isDeactivated()
    {
        return this.searchString == null;
    }//public boolean isDeactivated()



    public void deactivate()
    {
        this.searchString = null;
        this.notifyFilterChange();
    }//public void deactivate()



    public String exportSettings()
    {
        return this.searchString;
    }//public String exportSettings()



    public void importSettings(String strSettings)
    {
        this.setSearchString(strSettings);
    }//public void importSettings(String strSettings)

}//public class TitleFilter extends ProgrammeFilter
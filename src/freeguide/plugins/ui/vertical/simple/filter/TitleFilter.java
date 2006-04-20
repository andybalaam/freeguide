package freeguide.plugins.ui.vertical.simple.filter;

import freeguide.common.lib.fgspecific.data.TVProgramme;

/**
 * Filters tv programs by title
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class TitleFilter extends ProgrammeFilter
{

    protected String searchString = null;

    /**
     * Creates a new TitleFilter object.
     */
    public TitleFilter(  )
    {
    }

    //public TitleFilter()

    /**
     * DOCUMENT_ME!
     *
     * @param searchString DOCUMENT_ME!
     */
    public void setSearchString( String searchString )
    {
        this.setSearchString( searchString, true );
    }

    //public void setSearchString(String searchString)

    /**
     * DOCUMENT_ME!
     *
     * @param searchString DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void setSearchString( String searchString, boolean notify )
    {

        if( ( searchString != null ) && searchString.equals( "" ) )
        {
            searchString = null;
        }

        this.searchString = searchString;

        //TODO: Translation
        this.setTitle( "Search for \"" + this.searchString + "\"" );

        if( notify )
        {
            this.notifyFilterChange(  );
        }
    }

    //public void setSearchString(String searchString, boolean notify)

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getSearchString(  )
    {

        return this.searchString;
    }

    //public String getSearchString()

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean showProgramme( TVProgramme programme )
    {

        //TODO: Make search case insensitive
        return ( this.searchString == null )
        || ( programme.getTitle(  ).indexOf( this.searchString ) != -1 );
    }

    //public boolean showProgramme(TVProgramme programme)

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isDeactivated(  )
    {

        return this.searchString == null;
    }

    //public boolean isDeactivated()

    /**
     * DOCUMENT_ME!
     */
    public void deactivate(  )
    {
        this.searchString = null;
        this.notifyFilterChange(  );
    }

    //public void deactivate()

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String exportSettings(  )
    {

        return this.searchString;
    }

    //public String exportSettings()

    /**
     * DOCUMENT_ME!
     *
     * @param strSettings DOCUMENT_ME!
     */
    public void importSettings( String strSettings )
    {
        this.setSearchString( strSettings );
    }

    //public void importSettings(String strSettings)
}


//public class TitleFilter extends ProgrammeFilter

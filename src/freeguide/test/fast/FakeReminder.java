package freeguide.test.fast;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleReminder;

public class FakeReminder implements IModuleReminder
{
    public ArrayList<TVProgramme> selectedProgs = new ArrayList<TVProgramme>();

    public void addFavourite( Favourite favourite )
    {

    }

    public void addItemsToMenu( JMenu menu )
    {

    }

    public void addItemsToPopupMenu( TVProgramme programme, JPopupMenu menu )
    {

    }

    public Favourite getFavourite( TVProgramme programme )
    {
        return null;
    }

    public boolean isSelected( TVProgramme programme )
    {
        return selectedProgs.contains( programme );
    }

    public void removeFavourite( Favourite favourite )
    {

    }

    public void reschedule()
    {

    }

    public void setProgrammeSelection( TVProgramme programme,
        boolean newSelection )
    {

    }

    public void start()
    {
    }

    public void stop()
    {
    }

    public Object getConfig()
    {
        return null;
    }

    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return null;
    }

    public String getI18nName()
    {
        return null;
    }

    public ResourceBundle getLocalizer()
    {
        return null;
    }

    public void reloadResourceBundle() throws Exception
    {
    }

}

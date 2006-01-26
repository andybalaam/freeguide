package freeguide.plugins.ui.vertical.simple.filter.gui.helper;

import freeguide.plugins.ui.vertical.simple.filter.ProgrammeFilter;


/**
 * An interface for setting dialogs that shall be used from
 * a GenericFilterMenu
 *
 * At first the dialog is instantiated, then setFilter() is
 * called, and then the init() method. After that has happened,
 * the dialog may be shown.
 *
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public interface SettingDialog
{
    public boolean isClosedWithOk();
    public void init();
    public void setFilter(ProgrammeFilter filter);
    public void show();
}//public interface SettingDialog

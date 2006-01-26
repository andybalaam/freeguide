package freeguide.plugins.ui.vertical.simple.filter.gui;

import freeguide.plugins.ui.vertical.simple.filter.gui.helper.SettingDialog;
import freeguide.plugins.ui.vertical.simple.filter.ProgrammeFilter;
import freeguide.plugins.ui.vertical.simple.filter.TitleFilter;
import freeguide.plugins.ui.vertical.simple.VerticalViewer;
import freeguide.lib.fgspecific.Application;

import javax.swing.*;


/**
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class TitleDialog implements SettingDialog
{
    protected TitleFilter filter;
    protected boolean bClosedWithOk = false;



    public boolean isClosedWithOk()
    {
        return this.bClosedWithOk;
    }//public boolean isClosedWithOk()



    public void init() {}



    public void setFilter(ProgrammeFilter filter)
    {
        this.filter = (TitleFilter)filter;
    }//public void setFilter(ProgrammeFilter filter)



    public void show()
    {
        String strAnswer = JOptionPane.showInputDialog(
                VerticalViewer.getInstance().getLocalizedMessage("titledialog.question"),
                this.filter.getSearchString()
        );
        this.filter.setSearchString(strAnswer);
        this.bClosedWithOk = (strAnswer != null);
    }//public void show()

}//public class TitleDialog

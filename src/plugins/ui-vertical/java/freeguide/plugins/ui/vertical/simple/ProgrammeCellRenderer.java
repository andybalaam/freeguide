package freeguide.plugins.ui.vertical.simple;

import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.lib.fgspecific.Application;
import freeguide.plugins.IModuleReminder;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import java.awt.*;


/**
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class ProgrammeCellRenderer extends DefaultTableCellRenderer
{

    private IModuleReminder reminder = null;



    public void init()
    {
        IModuleReminder[] rems = Application.getInstance().getReminders();
        if (rems.length > 0) {
            this.reminder = rems[0];
        }
    }//public void init()



    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        Component c = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column);
        TVProgramme programme = (TVProgramme)table.getModel().getValueAt(row, TvTableModel.COL_PROGRAMME);

        if (isSelected || hasFocus) {
            //c.setBackground(VerticalViewerConfig.colorSelected);
        } else if (this.reminder != null) {
            if (this.reminder.isSelected(programme)) {
                c.setBackground(VerticalViewerConfig.colorTicked);
            } else if (programme.getIsMovie()) {
                c.setBackground(VerticalViewerConfig.colorMovie);
            } else {
                c.setBackground(VerticalViewerConfig.colorNonTicked);
            }
        }

        //TODO: Nifty percentage as maxemum tv guide does it

        return c;
    }//public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
}//public class ProgrammeCellRenderer extends DefaultTableCellRenderer

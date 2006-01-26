package freeguide.plugins.ui.vertical.simple;

import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.lib.fgspecific.Application;

import javax.swing.table.AbstractTableModel;
import java.util.Date;


/**
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class TvTableModel extends AbstractTableModel
{
    public static String[] arColNames = {"Date", "Time", "Title", "Channel"};
    public static final int COL_PROGRAMME = -1;
    public static final int COL_DATE    = 0;
    public static final int COL_TIME    = 1;
    public static final int COL_TITLE   = 2;
    public static final int COL_CHANNEL = 3;
    protected TVProgramme[] arProgrammes = new TVProgramme[0];
    protected int nCurrentTransferProgram = 0;



    public TvTableModel()
    {
        /*
        arColNames = new String[] {
            VerticalViewer.getInstance().getLocalizedMessage("table.column.date"),
            VerticalViewer.getInstance().getLocalizedMessage("table.column.time"),
            VerticalViewer.getInstance().getLocalizedMessage("table.column.title"),
            VerticalViewer.getInstance().getLocalizedMessage("table.column.channel"),
        };*/
    }



    /**
     * How many columns we have
     */
    public int getColumnCount()
    {
        return arColNames.length;
    }//public int getColumnCount()



    /**
     * How many rows we have
     */
    public int getRowCount()
    {
        return this.arProgrammes.length;
    }//public int getRowCount()



    /**
     * Get the value at the given column of the given row
     * for display in the list
     */
    public Object getValueAt(int row, int col)
    {
        if (arProgrammes[row] == null) {
            return "-";
        }

        switch (col) {
            case COL_TITLE:
                return arProgrammes[row].getTitle();
            case COL_TIME:
                return VerticalViewerConfig.listTimeFormat24Hour.format(new Date(arProgrammes[row].getStart()))
                        + " - "
                        + VerticalViewerConfig.listTimeFormat24Hour.format(new Date(arProgrammes[row].getEnd()));
            case COL_DATE:
                return VerticalViewerConfig.listDateFormat.format(new Date(arProgrammes[row].getStart()));
            case COL_CHANNEL:
                return arProgrammes[row].getChannel().getDisplayName();
            case COL_PROGRAMME:
                return arProgrammes[row];
            default:
                System.err.println("Unknown column #" + col);
                return "?";
        }
    }//public Object getValueAt(int row, int col)



    /**
     * Returns the questioned column name
     */
    public String getColumnName(int col)
    {
        return arColNames[col];
    }//public String getColumnName(int col)



    /**
     * Add a single tv program to the model.
     */
    public void addProgramme(TVProgramme programme)
    {
        this.arProgrammes[this.nCurrentTransferProgram] = programme;
        this.nCurrentTransferProgram++;
    }//public void addProgramme(TVProgramme programme)



    /**
     * Before the programs are transferred to this model,
     * we need to prepare the array
     */
    public void prepareRows(int programmesCount)
    {
        this.arProgrammes = new TVProgramme[programmesCount];
        this.nCurrentTransferProgram = 0;
    }//public void prepareRows(int programmesCount)



    /**
     * The programs have been transferred, so
     * we can sort them now by time.
     */
    public void postpare()
    {
        //Sort them by time
        java.util.Arrays.sort(this.arProgrammes, new ProgrammeTimeComparator());
    }//public void postpare()

}//public class TvTableModel extends AbstractTableModel
package freeguide.plugins.ui.vertical.simple;

import freeguide.plugins.ui.vertical.simple.filter.ProgrammeFilter;
import freeguide.lib.fgspecific.data.TVProgramme;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Little model which filters tv programmes by filters
 *
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class ProgrammeFilterModel extends AbstractTableModel
{
    protected TvTableModel model = null;
    protected TvList list = null;

    /**
     * List with all filters
     */
    protected ArrayList filters = new ArrayList();

    /**
     * how many rows (after filtering) we have
     */
    protected int nRowCount = 0;

    /**
     * Association of local row numbers to submodel row numbers
     */
    protected int[] arRowAssociation;



    public ProgrammeFilterModel(TvTableModel model, TvList list)
    {
        this.model = model;
        this.list = list;
    }//public ProgrammeFilterModel(TvTableModel model)



    /**
     * Add a filter to the filter list.
     */
    public void addFilter(ProgrammeFilter filter)
    {
        this.filters.add(filter);
        filter.setModel(this);
    }//public void addFilter(ProgrammeFilter filter)



    /**
     * Return the first filter of the given class
     */
    public ProgrammeFilter getFilter(Class filterClass)
    {
        Iterator it = this.filters.iterator();
        ProgrammeFilter filter;
        while (it.hasNext()) {
            filter = (ProgrammeFilter)it.next();
            if (filter.getClass().equals(filterClass)) {
                return filter;
            }
        }
        return null;
    }//public ProgrammeFilter getFilter(Class filterClass)



    /**
     * Remove the filter from the filter list
     */
    public void removeFilter(ProgrammeFilter filter)
    {
        this.filters.remove(filter);
    }//public void removeFilter(ProgrammeFilter filter)



    /**
     * The working horse: The filters are applied here.
     * This method has to be called whenever a filter has
     * been changed.
     */
    public void applyFilter()
    {
        int nModelRowCount         = this.model.getRowCount();
        this.arRowAssociation      = new int[nModelRowCount];
        this.nRowCount             = 0;

        ProgrammeFilter[] arFilter = this.getAllFilters();
        TVProgramme programme;
        boolean bShowProgramme;

        if (arFilter.length > 0) {
            //apply the filters
            for (int nA = 0; nA < nModelRowCount; nA++) {
                programme = (TVProgramme)this.model.getValueAt(nA, TvTableModel.COL_PROGRAMME);
                bShowProgramme = true;
                for (int nFilter = 0; nFilter < arFilter.length; nFilter++) {
                    if (!arFilter[nFilter].showProgramme(programme)) {
                        bShowProgramme = false;
                        break;
                    }
                }
                if (bShowProgramme) {
                    this.arRowAssociation[this.nRowCount++] = nA;
                }
            }
        } else {
            //no filters? pass through.
            this.nRowCount = nModelRowCount;
            for (int nA = 0; nA < nModelRowCount; nA++) {
                this.arRowAssociation[nA] = nA;
            }
        }
    }//public void applyFilter()



    /**
     * Get an array of all filters.
     */
    public ProgrammeFilter[] getAllFilters()
    {
        ProgrammeFilter[] arFilter = new ProgrammeFilter[this.filters.size()];
        Iterator it = this.filters.iterator();
        int nA = 0;
        while (it.hasNext()) {
            arFilter[nA++] = (ProgrammeFilter)it.next();
        }
        return arFilter;
    }//public ProgrammeFilter[] getAllFilters()



    public int getColumnCount()
    {
        return this.model.getColumnCount();
    }//public int getColumnCount()



    public int getRowCount()
    {
        return this.nRowCount;
    }//public int getRowCount()



    public Object getValueAt(int row, int col)
    {
        return this.model.getValueAt(this.arRowAssociation[row], col);
    }//public Object getValueAt(int row, int col)



    public TvTableModel getModel()
    {
        return this.model;
    }//public TvTableModel getModel()



    public String getColumnName(int col)
    {
        return this.model.getColumnName(col);
    }//public String getColumnName(int col)



    /**
     * A filter notifies the model that it has been
     * changed and filters need to be re-applied
     */
    public void filterChanged()
    {
        this.applyFilter();
        this.list.updateUI();
    }//public void filterChanged()

}//public class ProgrammeFilterModel extends AbstractTableModel
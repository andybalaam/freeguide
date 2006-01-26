package freeguide.plugins.ui.vertical.simple;

import freeguide.plugins.*;
import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.*;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;


/**
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class VerticalViewer extends BaseModule implements IModuleViewer
{
    protected VerticalViewerConfig config = new VerticalViewerConfig();
    protected JPanel pnlMain;
    protected TvList list;
    protected TvTableModel model;
    protected ProgrammeFilterModel filterModel;

    protected static VerticalViewer instance;

    /** Day in milliseconds. */
    public static final long MILLISECONDS_PER_DAY = 24L * 60L * 60L * 1000L;

    /**
     * The current date in milliseconds
     */
    private long theDate;



    public VerticalViewer()
    {
        VerticalViewer.instance = this;
        this.pnlMain     = new JPanel(new BorderLayout());
        this.list        = new TvList();
        this.filterModel = (ProgrammeFilterModel)this.list.getModel();
        this.model       = this.filterModel.getModel();

        JScrollPane sp = new JScrollPane(this.list);
        this.pnlMain.add(sp, BorderLayout.CENTER);
    }//public VerticalViewer()



    public Object getConfig()
    {
        return config;
    }//public Object getConfig()



    /**
     * Return the main UI panel
     * @return The panel
     */
    public JPanel getPanel()
    {
        return this.pnlMain;
    }



    /**
     * Start viewer.
     */
    public void open()
    {
        theDate = System.currentTimeMillis();
        onDataChanged();
    }//public void open()



    /**
     * Close viewer.
     */
    public void close()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }



    /**
     * The data have been changed.
     */
    public void onDataChanged()
    {
        this.loadData();
    }//public void onDataChanged()



    /**
     * Load the data into the model
     */
    protected void loadData()
    {
        synchronized( this )
        {
            TVData currentData = null;

            try {
                //get the data
                currentData =
                    Application.getInstance(  ).getDataStorage(  ).get(
                        Application.getInstance().getDataStorage().getInfo().allChannels,
                        theDate, theDate + MILLISECONDS_PER_DAY );

                //prepare the model
                model.prepareRows(currentData.getProgrammesCount());

                //transfer the programs to the model
                currentData.iterate(
                    new TVIteratorProgrammes() {
                        protected void onChannel(TVChannel channel) {}

                        public void onProgramme( TVProgramme programme )
                        {
                            model.addProgramme(programme);
                        }
                    } );

                //let the model do some things after the transfer
                this.model.postpare();
                this.filterModel.applyFilter();
                this.list.postpare();

                //done, just update the view
                this.list.updateUI();

            } catch (Exception ex) {
                Application.getInstance().getLogger().log(
                    Level.WARNING, "Error reading TV data", ex);
            }

            if (currentData != null && currentData.getChannelsCount() == 0) {
                askForLoadData(  );
            }
        }
    }//protected void loadData()



    /**
     * Ask the user if he wants to load the data
     * because there are none.
     * Shamelessly copied from HorizontalViewer.java
     */
    protected void askForLoadData(  )
    {

        int r =
            JOptionPane.showConfirmDialog(
                Application.getInstance(  ).getCurrentFrame(  ),
                Application.getInstance(  ).getLocalizedMessage(
                    "there_are_missing_listings_for_today" ),
                Application.getInstance(  ).getLocalizedMessage(
                    "download_listings_q" ), JOptionPane.YES_NO_OPTION );

        if( r == 0 )
        {
            Application.getInstance(  ).doStartGrabbers(  );
        }
    }//protected void askForLoadData(  )



    public void onChannelsSetsChanged()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }



    public void redraw()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }



    public void redrawCurrentProgramme()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }



    public void printHTML()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }



    public JButton getDefaultButton()
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }



    public static VerticalViewer getInstance()
    {
        return VerticalViewer.instance;
    }


    public String getLocalizedMessage(String key)
    {
        return this.getLocalizer().getLocalizedMessage(key);
    }//public String getLocalizedMessage(String key)

}//public class VerticalViewer extends BaseModule implements IModuleViewer

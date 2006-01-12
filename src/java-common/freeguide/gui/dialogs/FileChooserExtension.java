package freeguide.gui.dialogs;

import freeguide.lib.fgspecific.Application;

import freeguide.plugins.IModuleStorage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class FileChooserExtension extends JPanel
{

    private JCheckBox cbSelected = null;
    private JCheckBox cbToday = null;
    private JCheckBox cbChannelsList = null;

    /**
     * This is the default constructor
     */
    public FileChooserExtension(  )
    {
        super(  );
        initialize(  );
    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {
        this.setLayout( new GridBagLayout(  ) );
        this.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        addComponent( getCbSelected(  ) );
        addComponent( getCbToday(  ) );
        addComponent( getCbChannelsList(  ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     */
    public void addComponent( final JComponent comp )
    {

        final GridBagConstraints cst = new GridBagConstraints(  );
        cst.gridx = 0;
        cst.anchor = GridBagConstraints.WEST;
        add( comp, cst );
    }

    /**
     * This method initializes cbSelected
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCbSelected(  )
    {

        if( cbSelected == null )
        {
            cbSelected = new JCheckBox(  );
            cbSelected.setText( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "File.Chooser.SelectedOnly" ) );
        }

        return cbSelected;
    }

    /**
     * This method initializes cbToday
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCbToday(  )
    {

        if( cbToday == null )
        {
            cbToday = new JCheckBox(  );
            cbToday.setText( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "File.Chooser.TodayOnly" ) );
        }

        return cbToday;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isSelectedOnly(  )
    {

        return getCbSelected(  ).isSelected(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isTodayOnly(  )
    {

        return getCbToday(  ).isSelected(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isChannelsList(  )
    {

        return getCbChannelsList(  ).isSelected(  );
    }

    /**
     * This method initializes cbChannelsList
     *
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCbChannelsList(  )
    {

        if( cbChannelsList == null )
        {
            cbChannelsList = new JCheckBox(  );
            cbChannelsList.setText( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "File.Chooser.ChannelsList" ) );
        }

        return cbChannelsList;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleStorage.Info getSaveInfo(  )
    {

        final IModuleStorage.Info result =
            Application.getInstance(  ).getDataStorage(  ).getInfo(  )
                       .cloneInfo(  );
        final IModuleStorage.Info displayedInfo =
            Application.getInstance(  ).getViewer(  ).getDisplayedInfo(  );

        if( isChannelsList(  ) )
        {
            result.channelsList = displayedInfo.channelsList;
        }

        if( isTodayOnly(  ) )
        {
            result.minDate = displayedInfo.minDate;
            result.maxDate = displayedInfo.maxDate;
        }

        return result;
    }
}

package freeguide.common.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import freeguide.common.plugininterfaces.IApplication;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.IModuleViewer;

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

    private IModuleStorage storage;

    private IModuleViewer viewer;

    private IApplication application;

    /**
     * This is the default constructor
     */
    public FileChooserExtension( IModuleStorage storage, IModuleViewer viewer,
        IApplication application )
    {
        super();
        this.storage = storage;
        this.viewer = viewer;
        this.application = application;
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        this.setLayout( new GridBagLayout() );
        this.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        addComponent( getCbSelected() );
        addComponent( getCbToday() );
        addComponent( getCbChannelsList() );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp
     *            DOCUMENT_ME!
     */
    public void addComponent( final JComponent comp )
    {
        final GridBagConstraints cst = new GridBagConstraints();
        cst.gridx = 0;
        cst.anchor = GridBagConstraints.WEST;
        add( comp, cst );
    }

    /**
     * This method initializes cbSelected (public for test)
     *
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getCbSelected()
    {
        if( cbSelected == null )
        {
            cbSelected = new JCheckBox();
            cbSelected.setText( application
                .getLocalizedMessage( "File.Chooser.SelectedOnly" ) );
        }

        return cbSelected;
    }

    /**
     * This method initializes cbToday (public for test)
     *
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getCbToday()
    {
        if( cbToday == null )
        {
            cbToday = new JCheckBox();
            cbToday.setText( application
                .getLocalizedMessage( "File.Chooser.TodayOnly" ) );
        }

        return cbToday;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isSelectedOnly()
    {
        return getCbSelected().isSelected();
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isTodayOnly()
    {
        return getCbToday().isSelected();
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isChannelsList()
    {
        return getCbChannelsList().isSelected();
    }

    /**
     * This method initializes cbChannelsList (public for test)
     *
     * @return javax.swing.JCheckBox
     */
    public JCheckBox getCbChannelsList()
    {
        if( cbChannelsList == null )
        {
            cbChannelsList = new JCheckBox();
            cbChannelsList.setText( application
                .getLocalizedMessage( "File.Chooser.ChannelsList" ) );
        }

        return cbChannelsList;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleStorage.Info getSaveInfo()
    {
        final IModuleStorage.Info result = storage.getInfo().cloneInfo();
        final IModuleStorage.Info displayedInfo = viewer.getDisplayedInfo();

        if( isChannelsList() )
        {
            result.channelsList = displayedInfo.channelsList;
        }

        if( isTodayOnly() )
        {
            result.minDate = displayedInfo.minDate;
            result.maxDate = displayedInfo.maxDate;
        }

        return result;
    }
}

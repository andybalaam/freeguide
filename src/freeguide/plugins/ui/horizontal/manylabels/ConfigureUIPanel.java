package freeguide.plugins.ui.horizontal.manylabels;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * Panel for edit horizontal viewer options.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 *
 * This code was re-organized and simplified by:
 *    Michael McLagan (mmclagan at invlogic.com)
 *
 */
public class ConfigureUIPanel extends JPanel
{
    protected ResourceBundle localizer;

    // Grid line 0
    private   JTextField     textHeight;
    private   JSlider        sliderHeight;

    // Grid line 1
    private   JTextField     textWidth;
    private   JSlider        sliderWidth;

    // Grid line 2
    private   JTextField     textFont;
    private   JButton        btnFont;

    // Grid line 3
    private   JPanel         panelColorNormal;
    private   JButton        btnColorNormal;

    // Grid line 4
    private   JPanel         panelColorNew;
    private   JButton        btnColorNew;

    // Grid line 5
    private   JPanel         panelColorSelected;
    private   JButton        btnColorSelected;

    // Grid line 6
    private   JPanel         panelColorMovie;
    private   JButton        btnColorMovie;

    // Grid line 7
    private   JPanel         panelColorChannel;
    private   JButton        btnColorChannel;

    // Grid line 8
    private   JTextField     dayStart;

    // Grid line 9
    private   JRadioButton   rbTime12;
    private   JRadioButton   rbTime24;

    // Grid line 10
    private   JCheckBox      cbDrawTime;

    // Grid line 11
    private   JCheckBox      cbAlignLeft;

    // Grid line 12
    private   JCheckBox      cbDisplayTooltips;

    private   JPanel         jPanel;
    private   JPanel         panelTimeFormat;
    private   JPanel         panelSpacer;

    /**
     * This is the default constructor
     *
     * @param localizer DOCUMENT ME!
     */
    public ConfigureUIPanel(final ResourceBundle localizer)
    {
        super();

        this.localizer = localizer;

        initialize();

    }

    /**
    private JLabel jLabel;
    private JLabel labelHeight;
    private JLabel labelWidth;
    private JLabel labelFont;
    private JLabel labelColorNormal;
    private JLabel labelColorNew;
    private JLabel labelColorMovie;
    private JLabel labelColorChannel;
    private JLabel labelColorSelected;
    private JLabel labelTimeFormat;
     * Fill in the panel with the desired controls
     */
    private void initialize()
    {
        GridBagConstraints gbc;
        JLabel             label;

        // Set the layout engine
        setLayout(new GridBagLayout());

        // Grid line 0
        label = new JLabel();
        label.setText(localizer.getString("channel_height") + ":");
        label.setLabelFor(getSliderHeight());
        label.setDisplayedMnemonic(KeyEvent.VK_H);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getTextHeight(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getSliderHeight(), gbc);

        // Grid line 1
        label = new JLabel();
        label.setText(localizer.getString("width_of_1hr") + ":");
        label.setLabelFor(getSliderWidth());
        label.setDisplayedMnemonic(KeyEvent.VK_W);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getTextWidth(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getSliderWidth(), gbc);

        // Grid line 2
        label = new JLabel();
        label.setText(localizer.getString("ui_horiz_font") + ":");
        label.setLabelFor(btnFont);
        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_F);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getTextFont(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getBtnFont(), gbc);

        // Grid line 3
        label = new JLabel();
        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_N);
        label.setLabelFor(getBtnColorNormal());
        label.setText(localizer.getString("normal_prog_colour"));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getPanelColorNormal(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getBtnColorNormal(), gbc);

        // Grid line 4
        label = new JLabel();
        label.setLabelFor(getBtnColorSelected());
        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_E);
        label.setText(localizer.getString("new_prog_colour"));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getPanelColorNew(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getBtnColorNew(), gbc);

        // Grid line 5
        label = new JLabel();
        label.setLabelFor(getBtnColorSelected());
        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_A);
        label.setText(localizer.getString("selected_colour"));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getPanelColorSelected(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getBtnColorSelected(), gbc);

        // Grid line 6
        label = new JLabel();
        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_M);
        label.setLabelFor(getBtnColorMovie());
        label.setText(localizer.getString("movie_colour"));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getPanelColorMovie(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getBtnColorMovie(), gbc);

        // Grid line 7
        label = new JLabel();
        label.setLabelFor(getBtnColorChannel());
        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_C);
        label.setText(localizer.getString("channel_colour"));

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridwidth = 2;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(getPanelColorChannel(), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getBtnColorChannel(), gbc);

        // Grid line 9
//        gbc.gridx = 1;
//        gbc.gridy = 9;
//        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        gbc.weightx = 0.1D;
//        add(getJPanel(), gbc);

//        gbc.gridx = 2;
//        gbc.gridy = 9;
//        gbc.weightx = 1.0D;
//        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
//        add(getPanelSpacer(), gbc);

        // Grid line 10
        label = new JLabel();
        label.setLabelFor(getDayStart());
        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_S);
        label.setText(localizer.getString("day_starts_at") + ":");

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getDayStart(), gbc);

        // Grid line 11
        label = new JLabel();
        label.setLabelFor(getRbTime12());
        label.setText(localizer.getString("time_format") + ":");

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 0);
        add(label, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.gridwidth = 3;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getPanelTimeFormat(), gbc);

        // Grid line 12
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 4;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getCbDrawTime(), gbc);

        // Grid line 13
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.gridwidth = 4;
        gbc.insets = new java.awt.Insets(5, 5, 0, 5);
        add(getCbAlignLeft(), gbc);

        // Grid line 14
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 4;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        add(getCbDisplayTooltips(), gbc);

        validate();

        panelColorNormal.setMinimumSize(panelColorNormal.getSize());
        panelColorNew.setMinimumSize(panelColorNew.getSize());
        panelColorSelected.setMinimumSize(panelColorSelected.getSize());
        panelColorMovie.setMinimumSize(panelColorMovie.getSize());
        panelColorChannel.setMinimumSize(panelColorChannel.getSize());
    }

    /**
     * This method initializes jTextField2
     *
     * @return javax.swing.JTextField
     */
    protected JTextField getTextFont()
    {
        if(textFont == null)
        {
            textFont = new JTextField();

            textFont.setEditable(false);

        }

        return textFont;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnFont()
    {
        if(btnFont == null)
        {
            btnFont = new JButton();

            btnFont.setText("...");

        }

        return btnFont;

    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelSpacer()
    {
        if(panelSpacer == null)
        {
            panelSpacer = new JPanel();

            panelSpacer.setPreferredSize(new java.awt.Dimension(100, 6));
            panelSpacer.setMinimumSize(new java.awt.Dimension(100, 5));
        }

        return panelSpacer;

    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    protected JTextField getTextHeight()
    {
        if(textHeight == null)
        {
            textHeight = new JTextField();

            textHeight.setColumns(5);

            textHeight.setEditable(false);

        }

        return textHeight;

    }

    /**
     * This method initializes jTextField1
     *
     * @return javax.swing.JTextField
     */
    protected JTextField getTextWidth()
    {
        if(textWidth == null)
        {
            textWidth = new JTextField();

            textWidth.setColumns(5);

            textWidth.setEditable(false);

        }

        return textWidth;

    }

    /**
     * This method initializes jSlider
     *
     * @return javax.swing.JSlider
     */
    protected JSlider getSliderWidth()
    {
        if(sliderWidth == null)
        {
            sliderWidth = new JSlider();

            sliderWidth.setPreferredSize(new java.awt.Dimension(40, 16));

            sliderWidth.getAccessibleContext()
                       .setAccessibleName(
                localizer.getString("width_of_1hr"));

            sliderWidth.setMinimum(100);

            sliderWidth.setMaximum(1000);

        }

        return sliderWidth;

    }

    /**
     * This method initializes jSlider1
     *
     * @return javax.swing.JSlider
     */
    protected JSlider getSliderHeight()
    {
        if(sliderHeight == null)
        {
            sliderHeight = new JSlider();

            sliderHeight.setPreferredSize(new java.awt.Dimension(40, 16));

            sliderHeight.getAccessibleContext()
                        .setAccessibleName(
                localizer.getString("channel_height"));

            sliderHeight.setMinimum(10);

        }

        return sliderHeight;

    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorNormal()
    {
        if(panelColorNormal == null)
        {
            panelColorNormal = new JPanel();

        }

        return panelColorNormal;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorNormal()
    {
        if(btnColorNormal == null)
        {
            btnColorNormal = new JButton();

            btnColorNormal.setText("...");

        }

        return btnColorNormal;

    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorNew()
    {
        if(panelColorNew == null)
        {
            panelColorNew = new JPanel();

        }

        return panelColorNew;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorNew()
    {
        if(btnColorNew == null)
        {
            btnColorNew = new JButton();

            btnColorNew.setText("...");

        }

        return btnColorNew;

    }

    /**
     * Return the movie colour box.
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorMovie()
    {
        if(panelColorMovie == null)
        {
            panelColorMovie = new JPanel();
        }

        return panelColorMovie;
    }

    /**
     * Return the favourite colout box
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorSelected()
    {
        if(panelColorSelected == null)
        {
            panelColorSelected = new JPanel();
        }

        return panelColorSelected;
    }

    /**
     * This method initializes jPanel5
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorChannel()
    {
        if(panelColorChannel == null)
        {
            panelColorChannel = new JPanel();

        }

        return panelColorChannel;

    }

    /**
     * This method initializes jButton2
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorMovie()
    {
        if(btnColorMovie == null)
        {
            btnColorMovie = new JButton();

            btnColorMovie.setText("...");

        }

        return btnColorMovie;

    }

    /**
     * Returns the "..." button to change channel colour
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorChannel()
    {
        if(btnColorChannel == null)
        {
            btnColorChannel = new JButton();
            btnColorChannel.setText("...");
        }

        return btnColorChannel;
    }

    /**
     * Returns the "..." button to change favourite colour
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorSelected()
    {
        if(btnColorSelected == null)
        {
            btnColorSelected = new JButton();
            btnColorSelected.setText("...");
        }

        return btnColorSelected;
    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    public JTextField getDayStart()
    {
        if(dayStart == null)
        {
            dayStart = new JTextField();

            dayStart.setColumns(5);
        }

        return dayStart;

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelTimeFormat()
    {
        if(panelTimeFormat == null)
        {
            FlowLayout flowLayout8 = new FlowLayout();

            panelTimeFormat = new JPanel();

            panelTimeFormat.setLayout(flowLayout8);

            flowLayout8.setAlignment(java.awt.FlowLayout.LEFT);

            ButtonGroup g = new ButtonGroup();

            g.add(getRbTime12());

            g.add(getRbTime24());

            panelTimeFormat.add(getRbTime12(), null);

            panelTimeFormat.add(getRbTime24(), null);

        }

        return panelTimeFormat;

    }

    /**
     * This method initializes jRadioButton
     *
     * @return javax.swing.JRadioButton
     */
    protected JRadioButton getRbTime12()
    {
        if(rbTime12 == null)
        {
            rbTime12 = new JRadioButton();

            rbTime12.setMnemonic(java.awt.event.KeyEvent.VK_1);
            rbTime12.setText(localizer.getString("12_hour"));

        }

        return rbTime12;

    }

    /**
     * This method initializes jRadioButton1
     *
     * @return javax.swing.JRadioButton
     */
    protected JRadioButton getRbTime24()
    {
        if(rbTime24 == null)
        {
            rbTime24 = new JRadioButton();

            rbTime24.setMnemonic(java.awt.event.KeyEvent.VK_2);
            rbTime24.setText(localizer.getString("24_hour"));

        }

        return rbTime24;

    }

    /**
     * This method initializes jCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbDrawTime()
    {
        if(cbDrawTime == null)
        {
            cbDrawTime = new JCheckBox();

            cbDrawTime.setMnemonic(java.awt.event.KeyEvent.VK_O);
            cbDrawTime.setText(localizer.getString("show_programme_times"));

        }

        return cbDrawTime;

    }

    /**
     * This method initializes jCheckBox1
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbAlignLeft()
    {
        if(cbAlignLeft == null)
        {
            cbAlignLeft = new JCheckBox();

            cbAlignLeft.setMnemonic(java.awt.event.KeyEvent.VK_V);
            cbAlignLeft.setText(localizer.getString("moving_names"));

        }

        return cbAlignLeft;

    }

    /**
     * This method initializes jCheckBox3
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbDisplayTooltips()
    {
        if(cbDisplayTooltips == null)
        {
            cbDisplayTooltips = new JCheckBox();

            cbDisplayTooltips.setMnemonic(java.awt.event.KeyEvent.VK_L);
            cbDisplayTooltips.setText(localizer.getString("show_tooltips"));

        }

        return cbDisplayTooltips;

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel()
    {
        if(jPanel == null)
        {
            jPanel = new JPanel();
            jPanel.setMinimumSize(new java.awt.Dimension(50, 5));
        }

        return jPanel;
    }
}

package freeguide.plugins.ui.horizontal;

import freeguide.plugins.ILocalizer;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

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
 */
public class ConfigureUIPanel extends JPanel
{

    private JLabel labelHeight = null;
    private JLabel labelWidth = null;
    private JLabel labelFont = null;
    private JTextField textFont = null;
    private JButton btnFont = null;
    private JPanel panelSpacer = null;
    private JTextField textHeight = null;
    private JTextField textWidth = null;
    private JSlider sliderWidth = null;
    private JSlider sliderHeight = null;
    private JLabel labelColorNormal = null;
    private JPanel panelColorNormal = null;
    private JButton btnColorNormal = null;
    private JLabel labelColorInGuide = null;
    private JLabel labelColorMovie = null;
    private JLabel labelColorChannel = null;
    private JLabel labelColorHeart = null;
    private JPanel panelColorInGuide = null;
    private JPanel panelColorMovie = null;
    private JPanel panelColorChannel = null;
    private JPanel panelColorHeart = null;
    private JButton btnColorInGuide = null;
    private JButton btnColorMovie = null;
    private JButton btnColorChannel = null;
    private JButton btnColorHeart = null;
    protected ILocalizer localizer;
    private JTextField dayStart = null;
    private JLabel jLabel = null;
    private JPanel panelTimeFormat = null;
    private JLabel labelTimeFormat = null;
    private JRadioButton rbTime12 = null;
    private JRadioButton rbTime24 = null;
    private JCheckBox cbDrawTime = null;
    private JCheckBox cbAlignLeft = null;
    private JCheckBox cbPrintDelta = null;
    private JCheckBox cbDisplayTooltips = null;

    /**
     * This is the default constructor
     *
     * @param localizer DOCUMENT ME!
     */
    public ConfigureUIPanel( final ILocalizer localizer )
    {
        super(  );

        this.localizer = localizer;

        initialize(  );

    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {

        GridBagConstraints gridBagConstraints31 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints21 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints11 = new GridBagConstraints(  );

        labelTimeFormat = new JLabel(  );

        jLabel = new JLabel(  );

        GridBagConstraints gridBagConstraints42 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints51 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints61 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints72 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints92 = new GridBagConstraints(  );

        labelColorNormal = new JLabel(  );

        labelColorInGuide = new JLabel(  );

        labelColorHeart = new JLabel(  );

        labelColorChannel = new JLabel(  );

        labelColorMovie = new JLabel(  );

        GridBagConstraints gridBagConstraints20 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints19 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints18 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints17 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints16 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints15 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints14 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints131 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints81 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints71 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints6 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints91 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints101 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints111 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints12 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints5 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints41 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints3 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints2 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints13 = new GridBagConstraints(  );

        labelFont = new JLabel(  );

        labelWidth = new JLabel(  );

        labelHeight = new JLabel(  );

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints4 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints7 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints8 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints9 = new GridBagConstraints(  );

        this.setLayout( new GridBagLayout(  ) );

        this.setSize( 300, 400 );

        gridBagConstraints1.gridx = 0;

        gridBagConstraints1.gridy = 0;

        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;

        labelHeight.setText( "channel_height:" );

        labelHeight.setText( 
            localizer.getLocalizedMessage( "channel_height" ) + ":" );

        labelHeight.setLabelFor( textHeight );

        labelHeight.setDisplayedMnemonic( KeyEvent.VK_H );

        gridBagConstraints6.gridx = 0;

        gridBagConstraints6.gridy = 3;

        labelColorNormal.setText( "normal_prog_colour" );

        labelColorNormal.setText( 
            localizer.getLocalizedMessage( "normal_prog_colour" ) );

        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints71.gridx = 1;

        gridBagConstraints71.gridy = 3;

        gridBagConstraints71.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints71.gridwidth = 2;

        gridBagConstraints81.gridx = 3;

        gridBagConstraints81.gridy = 3;

        gridBagConstraints91.gridx = 0;

        gridBagConstraints91.gridy = 4;

        labelColorInGuide.setText( "in_guide_prog_colour" );

        labelColorInGuide.setText( 
            localizer.getLocalizedMessage( "in_guide_prog_colour" ) );

        gridBagConstraints91.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints101.gridx = 0;

        gridBagConstraints101.gridy = 5;

        labelColorMovie.setText( "movie_colour" );

        labelColorMovie.setText( 
            localizer.getLocalizedMessage( "movie_colour" ) );

        gridBagConstraints111.gridx = 0;

        gridBagConstraints111.gridy = 6;

        labelColorChannel.setText( "channel_colour" );

        labelColorChannel.setText( 
            localizer.getLocalizedMessage( "channel_colour" ) );

        gridBagConstraints12.gridx = 0;

        gridBagConstraints12.gridy = 7;

        labelColorHeart.setText( "heart_colour" );

        labelColorHeart.setText( 
            localizer.getLocalizedMessage( "heart_colour" ) );

        gridBagConstraints101.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints111.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints131.gridx = 1;

        gridBagConstraints131.gridy = 4;

        gridBagConstraints131.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints131.gridwidth = 2;

        gridBagConstraints14.gridx = 1;

        gridBagConstraints14.gridy = 5;

        gridBagConstraints14.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints14.gridwidth = 2;

        gridBagConstraints15.gridx = 1;

        gridBagConstraints15.gridy = 6;

        gridBagConstraints15.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints15.gridwidth = 2;

        gridBagConstraints16.gridx = 1;

        gridBagConstraints16.gridy = 7;

        gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints16.gridwidth = 2;

        gridBagConstraints17.gridx = 3;

        gridBagConstraints17.gridy = 4;

        gridBagConstraints18.gridx = 3;

        gridBagConstraints18.gridy = 5;

        gridBagConstraints19.gridx = 3;

        gridBagConstraints19.gridy = 6;

        gridBagConstraints20.gridx = 3;

        gridBagConstraints20.gridy = 7;

        this.add( labelColorMovie, gridBagConstraints101 );

        this.add( labelColorChannel, gridBagConstraints111 );

        this.add( labelColorHeart, gridBagConstraints12 );

        this.add( getPanelColorInGuide(  ), gridBagConstraints131 );

        this.add( getBtnColorInGuide(  ), gridBagConstraints17 );

        this.add( getBtnColorChannel(  ), gridBagConstraints19 );

        this.add( getBtnColorHeart(  ), gridBagConstraints20 );

        this.add( labelColorInGuide, gridBagConstraints91 );

        this.add( labelColorNormal, gridBagConstraints6 );

        this.add( getPanelColorNormal(  ), gridBagConstraints71 );

        this.add( getBtnColorNormal(  ), gridBagConstraints81 );

        gridBagConstraints4.gridx = 0;

        gridBagConstraints4.gridy = 1;

        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;

        labelWidth.setText( "width_of_1hr:" );

        labelWidth.setText( 
            localizer.getLocalizedMessage( "width_of_1hr" ) + ":" );

        labelWidth.setLabelFor( textWidth );

        labelWidth.setDisplayedMnemonic( KeyEvent.VK_W );

        gridBagConstraints7.gridx = 0;

        gridBagConstraints7.gridy = 2;

        gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;

        labelFont.setText( "font:" );

        labelFont.setText( localizer.getLocalizedMessage( "font" ) + ":" );

        labelFont.setLabelFor( btnFont );

        gridBagConstraints8.gridx = 1;

        gridBagConstraints8.gridy = 2;

        gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints8.gridwidth = 2;

        gridBagConstraints9.gridx = 3;

        gridBagConstraints9.gridy = 2;

        gridBagConstraints13.gridx = 2;

        gridBagConstraints13.gridy = 9;

        gridBagConstraints13.weightx = 1.0D;

        gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints2.gridx = 1;

        gridBagConstraints2.gridy = 0;

        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints3.gridx = 1;

        gridBagConstraints3.gridy = 1;

        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints41.gridx = 2;

        gridBagConstraints41.gridy = 1;

        gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints41.gridwidth = 2;

        gridBagConstraints5.gridx = 2;

        gridBagConstraints5.gridy = 0;

        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints5.gridwidth = 2;

        gridBagConstraints42.gridx = 1;

        gridBagConstraints42.gridy = 10;

        gridBagConstraints42.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints51.gridx = 0;

        gridBagConstraints51.gridy = 10;

        gridBagConstraints51.anchor = java.awt.GridBagConstraints.WEST;

        jLabel.setText( "day_starts_at:" );

        jLabel.setText( 
            localizer.getLocalizedMessage( "day_starts_at" ) + ":" );

        gridBagConstraints61.gridx = 1;

        gridBagConstraints61.gridy = 11;

        gridBagConstraints61.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints61.gridwidth = 3;

        gridBagConstraints72.gridx = 0;

        gridBagConstraints72.gridy = 11;

        gridBagConstraints72.anchor = java.awt.GridBagConstraints.WEST;

        labelTimeFormat.setText( "time_format:" );

        labelTimeFormat.setText( 
            localizer.getLocalizedMessage( "time_format" ) + ":" );

        gridBagConstraints92.gridx = 0;

        gridBagConstraints92.gridy = 12;

        gridBagConstraints92.gridwidth = 4;

        gridBagConstraints92.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints11.gridx = 0;

        gridBagConstraints11.gridy = 13;

        gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints11.gridwidth = 4;

        gridBagConstraints21.gridx = 0;

        gridBagConstraints21.gridy = 14;

        gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints21.gridwidth = 4;

        gridBagConstraints31.gridx = 0;

        gridBagConstraints31.gridy = 15;

        gridBagConstraints31.gridwidth = 4;

        gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;

        this.add( getPanelColorMovie(  ), gridBagConstraints14 );

        this.add( getBtnColorMovie(  ), gridBagConstraints18 );

        this.add( getPanelColorChannel(  ), gridBagConstraints15 );

        this.add( getPanelColorHeart(  ), gridBagConstraints16 );

        this.add( labelHeight, gridBagConstraints1 );

        this.add( labelWidth, gridBagConstraints4 );

        this.add( getTextFont(  ), gridBagConstraints8 );

        this.add( labelFont, gridBagConstraints7 );

        this.add( getBtnFont(  ), gridBagConstraints9 );

        this.add( getPanelSpacer(  ), gridBagConstraints13 );

        this.add( getTextHeight(  ), gridBagConstraints2 );

        this.add( getTextWidth(  ), gridBagConstraints3 );

        this.add( getSliderWidth(  ), gridBagConstraints41 );

        this.add( getSliderHeight(  ), gridBagConstraints5 );

        this.add( getDayStart(  ), gridBagConstraints42 );

        this.add( jLabel, gridBagConstraints51 );

        this.add( getPanelTimeFormat(  ), gridBagConstraints61 );

        this.add( labelTimeFormat, gridBagConstraints72 );

        this.add( getCbDrawTime(  ), gridBagConstraints92 );

        this.add( getCbAlignLeft(  ), gridBagConstraints11 );

        this.add( getCbPrintDelta(  ), gridBagConstraints21 );

        this.add( getCbDisplayTooltips(  ), gridBagConstraints31 );

    }

    /**
     * This method initializes jTextField2
     *
     * @return javax.swing.JTextField
     */
    protected JTextField getTextFont(  )
    {

        if( textFont == null )
        {
            textFont = new JTextField(  );

            textFont.setEditable( false );

        }

        return textFont;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnFont(  )
    {

        if( btnFont == null )
        {
            btnFont = new JButton(  );

            btnFont.setText( "..." );

        }

        return btnFont;

    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelSpacer(  )
    {

        if( panelSpacer == null )
        {
            panelSpacer = new JPanel(  );

            panelSpacer.setPreferredSize( new java.awt.Dimension( 10, 0 ) );

        }

        return panelSpacer;

    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    protected JTextField getTextHeight(  )
    {

        if( textHeight == null )
        {
            textHeight = new JTextField(  );

            textHeight.setColumns( 5 );

            textHeight.setEditable( false );

        }

        return textHeight;

    }

    /**
     * This method initializes jTextField1
     *
     * @return javax.swing.JTextField
     */
    protected JTextField getTextWidth(  )
    {

        if( textWidth == null )
        {
            textWidth = new JTextField(  );

            textWidth.setColumns( 5 );

            textWidth.setEditable( false );

        }

        return textWidth;

    }

    /**
     * This method initializes jSlider
     *
     * @return javax.swing.JSlider
     */
    protected JSlider getSliderWidth(  )
    {

        if( sliderWidth == null )
        {
            sliderWidth = new JSlider(  );

            sliderWidth.setPreferredSize( new java.awt.Dimension( 40, 16 ) );

            sliderWidth.getAccessibleContext(  ).setAccessibleName( 
                localizer.getLocalizedMessage( "width_of_1hr" ) );

            sliderWidth.setMinimum( 100 );

            sliderWidth.setMaximum( 1000 );

        }

        return sliderWidth;

    }

    /**
     * This method initializes jSlider1
     *
     * @return javax.swing.JSlider
     */
    protected JSlider getSliderHeight(  )
    {

        if( sliderHeight == null )
        {
            sliderHeight = new JSlider(  );

            sliderHeight.setPreferredSize( new java.awt.Dimension( 40, 16 ) );

            sliderHeight.getAccessibleContext(  ).setAccessibleName( 
                localizer.getLocalizedMessage( "channel_height" ) );

            sliderHeight.setMinimum( 10 );

        }

        return sliderHeight;

    }

    /**
     * This method initializes jPanel2
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorNormal(  )
    {

        if( panelColorNormal == null )
        {
            panelColorNormal = new JPanel(  );

        }

        return panelColorNormal;

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorNormal(  )
    {

        if( btnColorNormal == null )
        {
            btnColorNormal = new JButton(  );

            btnColorNormal.setText( "..." );

        }

        return btnColorNormal;

    }

    /**
     * This method initializes jPanel3
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorInGuide(  )
    {

        if( panelColorInGuide == null )
        {
            panelColorInGuide = new JPanel(  );

        }

        return panelColorInGuide;

    }

    /**
     * This method initializes jPanel4
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorMovie(  )
    {

        if( panelColorMovie == null )
        {
            panelColorMovie = new JPanel(  );

        }

        return panelColorMovie;

    }

    /**
     * This method initializes jPanel5
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorChannel(  )
    {

        if( panelColorChannel == null )
        {
            panelColorChannel = new JPanel(  );

        }

        return panelColorChannel;

    }

    /**
     * This method initializes jPanel6
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorHeart(  )
    {

        if( panelColorHeart == null )
        {
            panelColorHeart = new JPanel(  );

        }

        return panelColorHeart;

    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorInGuide(  )
    {

        if( btnColorInGuide == null )
        {
            btnColorInGuide = new JButton(  );

            btnColorInGuide.setText( "..." );

        }

        return btnColorInGuide;

    }

    /**
     * This method initializes jButton2
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorMovie(  )
    {

        if( btnColorMovie == null )
        {
            btnColorMovie = new JButton(  );

            btnColorMovie.setText( "..." );

        }

        return btnColorMovie;

    }

    /**
     * This method initializes jButton3
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorChannel(  )
    {

        if( btnColorChannel == null )
        {
            btnColorChannel = new JButton(  );

            btnColorChannel.setText( "..." );

        }

        return btnColorChannel;

    }

    /**
     * This method initializes jButton4
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorHeart(  )
    {

        if( btnColorHeart == null )
        {
            btnColorHeart = new JButton(  );

            btnColorHeart.setText( "..." );

        }

        return btnColorHeart;

    }

    /**
     * This method initializes jTextField
     *
     * @return javax.swing.JTextField
     */
    public JTextField getDayStart(  )
    {

        if( dayStart == null )
        {
            dayStart = new JTextField(  );

            dayStart.setColumns( 5 );
        }

        return dayStart;

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getPanelTimeFormat(  )
    {

        if( panelTimeFormat == null )
        {

            FlowLayout flowLayout8 = new FlowLayout(  );

            panelTimeFormat = new JPanel(  );

            panelTimeFormat.setLayout( flowLayout8 );

            flowLayout8.setAlignment( java.awt.FlowLayout.LEFT );

            ButtonGroup g = new ButtonGroup(  );

            g.add( getRbTime12(  ) );

            g.add( getRbTime24(  ) );

            panelTimeFormat.add( getRbTime12(  ), null );

            panelTimeFormat.add( getRbTime24(  ), null );

        }

        return panelTimeFormat;

    }

    /**
     * This method initializes jRadioButton
     *
     * @return javax.swing.JRadioButton
     */
    protected JRadioButton getRbTime12(  )
    {

        if( rbTime12 == null )
        {
            rbTime12 = new JRadioButton(  );

            rbTime12.setText( "12 hour" );

            rbTime12.setText( localizer.getLocalizedMessage( "12_hour" ) );

        }

        return rbTime12;

    }

    /**
     * This method initializes jRadioButton1
     *
     * @return javax.swing.JRadioButton
     */
    protected JRadioButton getRbTime24(  )
    {

        if( rbTime24 == null )
        {
            rbTime24 = new JRadioButton(  );

            rbTime24.setText( "24 hour" );

            rbTime24.setText( localizer.getLocalizedMessage( "24_hour" ) );

        }

        return rbTime24;

    }

    /**
     * This method initializes jCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbDrawTime(  )
    {

        if( cbDrawTime == null )
        {
            cbDrawTime = new JCheckBox(  );

            cbDrawTime.setText( "Show programme time ?" );

            cbDrawTime.setText( 
                localizer.getLocalizedMessage( "show_programme_times" ) );

        }

        return cbDrawTime;

    }

    /**
     * This method initializes jCheckBox1
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbAlignLeft(  )
    {

        if( cbAlignLeft == null )
        {
            cbAlignLeft = new JCheckBox(  );

            cbAlignLeft.setText( "Moving names ?" );

            cbAlignLeft.setText( 
                localizer.getLocalizedMessage( "moving_names" ) );

        }

        return cbAlignLeft;

    }

    /**
     * This method initializes jCheckBox2
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbPrintDelta(  )
    {

        if( cbPrintDelta == null )
        {
            cbPrintDelta = new JCheckBox(  );

            cbPrintDelta.setText( "Print time delta ?" );

            cbPrintDelta.setText( 
                localizer.getLocalizedMessage( "print_time_delta" ) );

        }

        return cbPrintDelta;

    }

    /**
     * This method initializes jCheckBox3
     *
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getCbDisplayTooltips(  )
    {

        if( cbDisplayTooltips == null )
        {
            cbDisplayTooltips = new JCheckBox(  );

            cbDisplayTooltips.setText( "Show tooltips ?" );

            cbDisplayTooltips.setText( 
                localizer.getLocalizedMessage( "show_tooltips" ) );

        }

        return cbDisplayTooltips;

    }
}

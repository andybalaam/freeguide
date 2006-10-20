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
 */
public class ConfigureUIPanel extends JPanel
{
    private JLabel labelHeight;
    private JLabel labelWidth;
    private JLabel labelFont;
    private JTextField textFont;
    private JButton btnFont;
    private JPanel panelSpacer;
    private JTextField textHeight;
    private JTextField textWidth;
    private JSlider sliderWidth;
    private JSlider sliderHeight;
    private JLabel labelColorNormal;
    private JPanel panelColorNormal;
    private JButton btnColorNormal;
    private JLabel labelColorMovie;
    private JLabel labelColorChannel;
    private JLabel labelColorSelected;

    /** new entries for favourite colour and guide colour */
    private JLabel labelColorFavourite;
    private JLabel labelColorGuide;
    private JPanel panelColorMovie;
    private JPanel panelColorSelected;
    private JPanel panelColorChannel;

    /** new entries for favourite colour and guide colour */
    private JPanel panelColorFavourite;
    private JPanel panelColorGuide;
    private JButton btnColorMovie;
    private JButton btnColorChannel;
    private JButton btnColorSelected;

    /** new entries for favourite colour and guide colour */
    private JButton btnColorFavourite;
    private JButton btnColorGuide;
    protected ResourceBundle localizer;
    private JTextField dayStart;
    private JLabel jLabel;
    private JPanel panelTimeFormat;
    private JLabel labelTimeFormat;
    private JRadioButton rbTime12;
    private JRadioButton rbTime24;
    private JCheckBox cbDrawTime;
    private JCheckBox cbAlignLeft;
    private JCheckBox cbDisplayTooltips;
    private JPanel jPanel = null;

/**
     * This is the default constructor
     *
     * @param localizer DOCUMENT ME!
     */
    public ConfigureUIPanel( final ResourceBundle localizer )
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
        GridBagConstraints gridBagConstraints110 = new GridBagConstraints(  );
        gridBagConstraints110.gridx = 1;
        gridBagConstraints110.gridy = 9;
        gridBagConstraints110.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints110.weightx = 0.1D;

        GridBagConstraints gridBagConstraints31 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints(  );

        labelTimeFormat = new JLabel(  );
        jLabel = new JLabel(  );

        GridBagConstraints gridBagConstraints42 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints51 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints61 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints72 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints92 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints(  ); // add

        labelColorNormal = new JLabel(  );
        labelColorChannel = new JLabel(  );
        labelColorSelected = new JLabel(  );
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        labelColorFavourite = new JLabel(  );
        labelColorGuide = new JLabel(  );

        labelColorMovie = new JLabel(  );

        GridBagConstraints gridBagConstraints19 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints20 = new GridBagConstraints(  );

        /** new entries for favourite colour and guide colour */
        GridBagConstraints gridBagConstraints22 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints23 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints18 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints15 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints14 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints16 = new GridBagConstraints(  );

        /** new entries for favourite colour and guide colour */
        GridBagConstraints gridBagConstraints17 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints118 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints81 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints71 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints101 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints111 = new GridBagConstraints(  );

        /** new entries for favourite colour and guide colour */
        GridBagConstraints gridBagConstraints112 = new GridBagConstraints(  );
        GridBagConstraints gridBagConstraints113 = new GridBagConstraints(  );

        GridBagConstraints gridBagConstraints50 = new GridBagConstraints(  );
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

        gridBagConstraints1.gridx = 0;

        gridBagConstraints1.gridy = 0;

        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints1.insets = new java.awt.Insets( 5, 5, 0, 0 );
        labelHeight.setText( localizer.getString( "channel_height" ) + ":" );

        labelHeight.setLabelFor( getSliderHeight(  ) );

        labelHeight.setDisplayedMnemonic( KeyEvent.VK_H );

        gridBagConstraints6.gridx = 0;

        gridBagConstraints6.gridy = 3;

        labelColorNormal.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_N );
        labelColorNormal.setLabelFor( getBtnColorNormal(  ) );
        labelColorNormal.setText( localizer.getString( "normal_prog_colour" ) );

        gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints6.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints71.gridx = 1;

        gridBagConstraints71.gridy = 3;

        gridBagConstraints71.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints71.gridwidth = 2;

        gridBagConstraints71.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints81.gridx = 3;

        gridBagConstraints81.gridy = 3;

        gridBagConstraints81.insets = new java.awt.Insets( 5, 5, 0, 5 );

        gridBagConstraints101.gridx = 0;
        gridBagConstraints101.gridy = 6;
        labelColorMovie.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_M );
        labelColorMovie.setLabelFor( getBtnColorMovie(  ) );
        labelColorMovie.setText( localizer.getString( "movie_colour" ) );

        gridBagConstraints111.gridx = 0;
        gridBagConstraints111.gridy = 7;
        labelColorChannel.setLabelFor( getBtnColorChannel(  ) );
        labelColorChannel.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_C );
        labelColorChannel.setText( localizer.getString( "channel_colour" ) );

        gridBagConstraints50.gridx = 0;
        gridBagConstraints50.gridy = 5;
        labelColorSelected.setLabelFor( getBtnColorSelected(  ) );
        labelColorSelected.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_A );
        labelColorSelected.setText( localizer.getString( "selected_colour" ) );
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        gridBagConstraints112.gridx = 0;
        gridBagConstraints112.gridy = 8;
        labelColorFavourite.setLabelFor( getBtnColorFavourite(  ) );
        labelColorFavourite.setDisplayedMnemonic( 
            java.awt.event.KeyEvent.VK_F );
        labelColorFavourite.setText( 
            localizer.getString( "favourite_colour" ) );

        gridBagConstraints113.gridx = 0;
        gridBagConstraints113.gridy = 9;
        labelColorGuide.setLabelFor( getBtnColorGuide(  ) );
        labelColorGuide.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_G );
        labelColorGuide.setText( localizer.getString( "guide_colour" ) );

        gridBagConstraints101.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints101.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints111.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints50.anchor = java.awt.GridBagConstraints.WEST;
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        gridBagConstraints112.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints113.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints111.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints50.insets = new java.awt.Insets( 5, 5, 0, 0 );
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        gridBagConstraints112.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints113.insets = new java.awt.Insets( 5, 5, 0, 0 );

        gridBagConstraints14.gridx = 1;
        gridBagConstraints14.gridy = 6;
        gridBagConstraints14.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints14.gridwidth = 2;
        gridBagConstraints14.insets = new java.awt.Insets( 5, 5, 0, 0 );

        gridBagConstraints16.gridx = 1;
        gridBagConstraints16.gridy = 5;
        gridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints16.gridwidth = 2;
        gridBagConstraints16.insets = new java.awt.Insets( 5, 5, 0, 0 );
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        gridBagConstraints17.gridx = 1;
        gridBagConstraints17.gridy = 8;
        gridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints17.gridwidth = 2;
        gridBagConstraints17.insets = new java.awt.Insets( 5, 5, 0, 0 );

        gridBagConstraints118.gridx = 1;
        gridBagConstraints118.gridy = 9;
        gridBagConstraints118.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints118.gridwidth = 2;
        gridBagConstraints118.insets = new java.awt.Insets( 5, 5, 0, 0 );

        gridBagConstraints15.gridx = 1;
        gridBagConstraints15.gridy = 7;
        gridBagConstraints15.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints15.gridwidth = 2;
        gridBagConstraints15.insets = new java.awt.Insets( 5, 5, 0, 0 );

        gridBagConstraints18.gridx = 3;
        gridBagConstraints18.gridy = 6;
        gridBagConstraints18.insets = new java.awt.Insets( 5, 5, 0, 5 );

        gridBagConstraints19.gridx = 3;
        gridBagConstraints19.gridy = 7;
        gridBagConstraints19.insets = new java.awt.Insets( 5, 5, 0, 5 );

        gridBagConstraints20.gridx = 3;
        gridBagConstraints20.gridy = 5;
        gridBagConstraints20.insets = new java.awt.Insets( 5, 5, 0, 5 );
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        gridBagConstraints22.gridx = 3;
        gridBagConstraints22.gridy = 8;
        gridBagConstraints22.insets = new java.awt.Insets( 5, 5, 0, 5 );

        gridBagConstraints23.gridx = 3;
        gridBagConstraints23.gridy = 9;
        gridBagConstraints23.insets = new java.awt.Insets( 5, 5, 0, 5 );

        gridBagConstraints4.gridx = 0;
        gridBagConstraints4.gridy = 1;
        gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints4.insets = new java.awt.Insets( 5, 5, 0, 0 );

        labelWidth.setText( localizer.getString( "width_of_1hr" ) + ":" );
        labelWidth.setLabelFor( getSliderWidth(  ) );
        labelWidth.setDisplayedMnemonic( KeyEvent.VK_W );

        gridBagConstraints7.gridx = 0;
        gridBagConstraints7.gridy = 2;
        gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints7.insets = new java.awt.Insets( 5, 5, 0, 0 );

        labelFont.setText( localizer.getString( "ui_horiz_font" ) + ":" );

        labelFont.setLabelFor( btnFont );

        labelFont.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_F );
        gridBagConstraints8.gridx = 1;

        gridBagConstraints8.gridy = 2;

        gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints8.gridwidth = 2;

        gridBagConstraints8.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints9.gridx = 3;

        gridBagConstraints9.gridy = 2;

        gridBagConstraints9.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints13.gridx = 2;

        gridBagConstraints13.gridy = 10;

        gridBagConstraints13.weightx = 1.0D;

        gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints2.gridx = 1;

        gridBagConstraints2.gridy = 0;

        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints2.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints3.gridx = 1;

        gridBagConstraints3.gridy = 1;

        gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints3.insets = new java.awt.Insets( 5, 5, 0, 0 );
        gridBagConstraints41.gridx = 2;

        gridBagConstraints41.gridy = 1;

        gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints41.gridwidth = 2;

        gridBagConstraints41.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints5.gridx = 2;

        gridBagConstraints5.gridy = 0;

        gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints5.gridwidth = 2;

        gridBagConstraints5.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints42.gridx = 1;

        gridBagConstraints42.gridy = 11;

        gridBagConstraints42.fill = java.awt.GridBagConstraints.HORIZONTAL;

        gridBagConstraints42.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints51.gridx = 0;

        gridBagConstraints51.gridy = 11;

        gridBagConstraints51.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints51.insets = new java.awt.Insets( 5, 5, 0, 0 );

        jLabel.setLabelFor( getDayStart(  ) );
        jLabel.setDisplayedMnemonic( java.awt.event.KeyEvent.VK_S );
        jLabel.setText( localizer.getString( "day_starts_at" ) + ":" );

        gridBagConstraints61.gridx = 1;

        gridBagConstraints61.gridy = 12;

        gridBagConstraints61.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints61.gridwidth = 3;

        gridBagConstraints61.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints72.gridx = 0;

        gridBagConstraints72.gridy = 12;

        gridBagConstraints72.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints72.insets = new java.awt.Insets( 5, 5, 0, 0 );

        labelTimeFormat.setLabelFor( getRbTime12(  ) );
        labelTimeFormat.setText( localizer.getString( "time_format" ) + ":" );

        gridBagConstraints92.gridx = 0;

        gridBagConstraints92.gridy = 13;

        gridBagConstraints92.gridwidth = 4;

        gridBagConstraints92.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints92.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints11.gridx = 0;

        gridBagConstraints11.gridy = 14;

        gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints11.gridwidth = 4;

        gridBagConstraints11.insets = new java.awt.Insets( 5, 5, 0, 5 );

        gridBagConstraints21.gridy = 15;

        gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints21.gridwidth = 4;

        gridBagConstraints21.insets = new java.awt.Insets( 5, 5, 0, 5 );
        gridBagConstraints31.gridx = 0;

        gridBagConstraints31.gridy = 16;

        gridBagConstraints31.gridwidth = 4;

        gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints31.insets = new java.awt.Insets( 5, 5, 5, 5 );
        this.add( getCbDisplayTooltips(  ), gridBagConstraints31 );
        this.add( getCbAlignLeft(  ), gridBagConstraints11 );
        this.add( getCbDrawTime(  ), gridBagConstraints92 );
        this.add( labelTimeFormat, gridBagConstraints72 );
        this.add( getPanelTimeFormat(  ), gridBagConstraints61 );
        this.add( jLabel, gridBagConstraints51 );
        this.add( getDayStart(  ), gridBagConstraints42 );
        this.add( getSliderHeight(  ), gridBagConstraints5 );
        this.add( getSliderWidth(  ), gridBagConstraints41 );
        this.add( getTextWidth(  ), gridBagConstraints3 );
        this.add( getTextHeight(  ), gridBagConstraints2 );
        this.add( getBtnFont(  ), gridBagConstraints9 );
        this.add( labelFont, gridBagConstraints7 );
        this.add( getTextFont(  ), gridBagConstraints8 );
        this.add( labelWidth, gridBagConstraints4 );
        this.add( labelHeight, gridBagConstraints1 );
        this.add( getPanelColorChannel(  ), gridBagConstraints15 );
        this.add( getBtnColorMovie(  ), gridBagConstraints18 );
        this.add( getPanelColorMovie(  ), gridBagConstraints14 );
        this.add( getPanelColorSelected(  ), gridBagConstraints16 );
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        this.add( getPanelColorFavourite(  ), gridBagConstraints17 );
        this.add( getBtnColorFavourite(  ), gridBagConstraints22 );
        this.add( getPanelColorGuide(  ), gridBagConstraints118 );
        this.add( getBtnColorGuide(  ), gridBagConstraints23 );

        this.add( getBtnColorNormal(  ), gridBagConstraints81 );
        this.add( getPanelColorNormal(  ), gridBagConstraints71 );
        this.add( labelColorNormal, gridBagConstraints6 );
        this.add( getBtnColorChannel(  ), gridBagConstraints19 );
        this.add( getBtnColorSelected(  ), gridBagConstraints20 );
        this.add( labelColorChannel, gridBagConstraints111 );
        this.add( labelColorSelected, gridBagConstraints50 );
/**
         * new entries for favourite colour and guide colour
         *
         * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         */
        this.add( labelColorFavourite, gridBagConstraints112 );
        this.add( getBtnColorFavourite(  ), gridBagConstraints22 );
        this.add( labelColorGuide, gridBagConstraints113 );
        this.add( getBtnColorGuide(  ), gridBagConstraints23 );

        this.add( labelColorMovie, gridBagConstraints101 );
        this.add( getPanelSpacer(  ), gridBagConstraints13 );
        this.add( getJPanel(  ), gridBagConstraints110 );
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

            panelSpacer.setPreferredSize( new java.awt.Dimension( 100, 6 ) );
            panelSpacer.setMinimumSize( new java.awt.Dimension( 100, 5 ) );
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

            sliderWidth.getAccessibleContext(  )
                       .setAccessibleName( 
                localizer.getString( "width_of_1hr" ) );

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

            sliderHeight.getAccessibleContext(  )
                        .setAccessibleName( 
                localizer.getString( "channel_height" ) );

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
     * Return the movie colour box.
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
     * Return the favourite colout box
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorSelected(  )
    {
        if( panelColorSelected == null )
        {
            panelColorSelected = new JPanel(  );
        }

        return panelColorSelected;
    }

    /**
     * Return the favourite colout box
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorFavourite(  )
    {
        if( panelColorFavourite == null )
        {
            panelColorFavourite = new JPanel(  );
        }

        return panelColorFavourite;
    }

    /**
     * Return the guide colout box
     *
     * @return javax.swing.JPanel
     */
    protected JPanel getPanelColorGuide(  )
    {
        if( panelColorGuide == null )
        {
            panelColorGuide = new JPanel(  );
        }

        return panelColorGuide;
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
     * Returns the "..." button to change channel colour
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
     * Returns the "..." button to change favourite colour
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorSelected(  )
    {
        if( btnColorSelected == null )
        {
            btnColorSelected = new JButton(  );
            btnColorSelected.setText( "..." );
        }

        return btnColorSelected;
    }

    /**
     * Returns the "..." button to change favourite colour
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorFavourite(  )
    {
        if( btnColorFavourite == null )
        {
            btnColorFavourite = new JButton(  );
            btnColorFavourite.setText( "..." );
        }

        return btnColorFavourite;
    }

    /**
     * Returns the "..." button to change favourite colour
     *
     * @return javax.swing.JButton
     */
    protected JButton getBtnColorGuide(  )
    {
        if( btnColorGuide == null )
        {
            btnColorGuide = new JButton(  );
            btnColorGuide.setText( "..." );
        }

        return btnColorGuide;
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

            rbTime12.setMnemonic( java.awt.event.KeyEvent.VK_1 );
            rbTime12.setText( localizer.getString( "12_hour" ) );

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

            rbTime24.setMnemonic( java.awt.event.KeyEvent.VK_2 );
            rbTime24.setText( localizer.getString( "24_hour" ) );

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

            cbDrawTime.setMnemonic( java.awt.event.KeyEvent.VK_O );
            cbDrawTime.setText( localizer.getString( "show_programme_times" ) );

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

            cbAlignLeft.setMnemonic( java.awt.event.KeyEvent.VK_V );
            cbAlignLeft.setText( localizer.getString( "moving_names" ) );

        }

        return cbAlignLeft;

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

            cbDisplayTooltips.setMnemonic( java.awt.event.KeyEvent.VK_L );
            cbDisplayTooltips.setText( localizer.getString( "show_tooltips" ) );

        }

        return cbDisplayTooltips;

    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel(  )
    {
        if( jPanel == null )
        {
            jPanel = new JPanel(  );
            jPanel.setMinimumSize( new java.awt.Dimension( 50, 5 ) );
        }

        return jPanel;
    }
}

package freeguide.plugins.ui.horizontal.manylabels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * The form that displays the listings information. Now contains only the GUI
 * code with everything else moved out.
 *
 * @author Andy Balaam
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ViewerFrame extends JPanel
{

    /** true if user doesn't want to download missing files */

    // public boolean dontDownload;

    /** The listener for when a scroll event happens */
    private AdjustmentListener comProgramScrollListener;

    /** Combobox containing the date we are viewing */
    public javax.swing.JComboBox comTheDate;

    /** The combobox showing the channel set we are using */
    public javax.swing.JComboBox comChannelSet;

    /** The panel showing the timeline */
    public TimePanel timePanel;

    /** The side panel showing programme details */
    protected JEditorPane detailsPanel;

    /** The panel containing the channel names */
    public javax.swing.JPanel channelNamePanel;

    /** The scrollpane that contains the names of channels */
    public javax.swing.JScrollPane channelNameScrollPane;

    /** The panel containing the programmes */
    public JPanel programmesPanel;

    /** The Scrollpane showing programmes */
    public javax.swing.JScrollPane programmesScrollPane;

    // }}}
    // {{{ Static GUI

    /** The JEditorPane where the printedGuide is shown */
    public JEditorPane printedGuideArea;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JPanel topButtonsPanel;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JButton butPrint;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JButton butDownload;

    /**
     * The splitpane splitting the main panel from the printed guide and
     * programme details
     */
    public javax.swing.JSplitPane splitPaneMainDet;

    /** The splitpane splitting the printed guide from programme details */
    public javax.swing.JSplitPane splitPaneGuideDet;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JButton butRevertToFavourites;

    /** The splitpane splitting the channels from programmes */
    public javax.swing.JSplitPane splitPaneChanProg;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JButton butNextDay;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JButton butPreviousDay;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JButton butGoToNow;

    /** ToDo: DOCUMENT ME! */
    private javax.swing.JScrollPane printedGuideScrollPane;

    /** Constructor for the FreeGuideViewer object */
    HorizontalViewer parent;

    /**
     * Creates a new ViewerFrame object.
     *
     * @param parent DOCUMENT ME!
     */
    public ViewerFrame( HorizontalViewer parent )
    {
        this.parent = parent;

        // Set up the static elements of the GUI
        initialize(  );

    }

    /**
     * Set up the entire static bit of the GUI
     */
    private void initialize(  )
    {

        // {{{ Initialisations
        java.awt.GridBagConstraints gridBagConstraints;

        topButtonsPanel = new javax.swing.JPanel(  );

        butGoToNow = new javax.swing.JButton(  );

        butPreviousDay = new javax.swing.JButton(  );

        comTheDate = new javax.swing.JComboBox(  );

        comChannelSet = new javax.swing.JComboBox(  );

        butNextDay = new javax.swing.JButton(  );

        splitPaneMainDet = new javax.swing.JSplitPane(  );

        printedGuideScrollPane = new FocusJScrollPane(  );

        printedGuideArea = new JEditorPane(  );

        detailsPanel = new JEditorPane(  );
        detailsPanel.setEditable( false );
        detailsPanel.setContentType( "text/html" );

        splitPaneChanProg = new javax.swing.JSplitPane(  );

        splitPaneGuideDet = new javax.swing.JSplitPane(  );

        channelNameScrollPane = new FocusJScrollPane(  );

        channelNamePanel = new javax.swing.JPanel( null );

        programmesScrollPane = new FocusJScrollPane(  );

        //programmesPanel = new JPanel(new ProgrammesPanelLayout());
        programmesPanel = new JPanel( null );
        timePanel = new TimePanel( parent.config );

        butRevertToFavourites = new javax.swing.JButton(  );

        butPrint = new javax.swing.JButton(  );

        butDownload = new javax.swing.JButton(  );

        // }}}
        // {{{ Main Window
        setLayout( new java.awt.GridBagLayout(  ) );

        // }}}
        // {{{ topButtonsPanel
        topButtonsPanel.setLayout( new java.awt.GridBagLayout(  ) );

        butGoToNow.setFont( new java.awt.Font( "Dialog", 0, 10 ) );
        butGoToNow.setText( 
            parent.getLocalizer(  ).getLocalizedMessage( "go_to_now" ) );
        butGoToNow.setMnemonic( KeyEvent.VK_N );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );

        topButtonsPanel.add( butGoToNow, gridBagConstraints );

        butPreviousDay.setText( 
            parent.getLocalizer(  ).getLocalizedMessage( "minus" ) );

        butPreviousDay.setMnemonic( KeyEvent.VK_MINUS );

        butPreviousDay.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butPreviousDayActionPerformed( evt );

                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 2;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );

        topButtonsPanel.add( butPreviousDay, gridBagConstraints );

        // **** Rob **** I think this should be false, if not, why?
        comTheDate.setEditable( false );

        comTheDate.setFont( new java.awt.Font( "Dialog", 0, 10 ) );

        comTheDate.setMinimumSize( new java.awt.Dimension( 120, 25 ) );

        comTheDate.setPreferredSize( new java.awt.Dimension( 120, 25 ) );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 3;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );

        topButtonsPanel.add( comTheDate, gridBagConstraints );

        butNextDay.setText( 
            parent.getLocalizer(  ).getLocalizedMessage( "plus" ) );

        butNextDay.setMnemonic( KeyEvent.VK_EQUALS );

        butNextDay.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butNextDayActionPerformed( evt );

                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 4;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );

        topButtonsPanel.add( butNextDay, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.gridwidth = 2;

        gridBagConstraints.weightx = 0.9;

        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;

        gridBagConstraints.insets = new java.awt.Insets( 0, 0, 0, 2 );

        add( topButtonsPanel, gridBagConstraints );

        // }}}
        // {{{ Other top buttons
        comChannelSet.setEditable( false );

        comChannelSet.setFont( new java.awt.Font( "Dialog", 0, 10 ) );

        comChannelSet.setMinimumSize( new java.awt.Dimension( 170, 25 ) );

        comChannelSet.setPreferredSize( new java.awt.Dimension( 140, 25 ) );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 3;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;

        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );

        add( comChannelSet, gridBagConstraints );

        butDownload.setFont( new java.awt.Font( "Dialog", 0, 10 ) );

        butDownload.setText( 
            parent.getLocalizer(  ).getLocalizedMessage( 
                "HorizontalViewer.Download" ) );

        butDownload.setMnemonic( KeyEvent.VK_D );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.insets = new java.awt.Insets( 2, 0, 0, 0 );

        add( butDownload, gridBagConstraints );

        // }}}
        // {{{ Split panes etc
        splitPaneMainDet.setOneTouchExpandable( true );

        splitPaneMainDet.setOrientation( 
            javax.swing.JSplitPane.VERTICAL_SPLIT );

        splitPaneGuideDet.setOneTouchExpandable( true );

        splitPaneGuideDet.setOrientation( 
            javax.swing.JSplitPane.HORIZONTAL_SPLIT );

        printedGuideArea.setEditable( false );

        printedGuideArea.setContentType( "text/html" );

        printedGuideScrollPane.setViewportView( printedGuideArea );

        splitPaneMainDet.setRightComponent( splitPaneGuideDet );

        splitPaneGuideDet.setLeftComponent( printedGuideScrollPane );

        splitPaneGuideDet.setRightComponent( new JScrollPane( detailsPanel ) );

        channelNameScrollPane.setBorder( null );

        channelNameScrollPane.setVerticalScrollBarPolicy( 
            javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER );

        channelNameScrollPane.setMinimumSize( 
            new java.awt.Dimension( 10, 10 ) );

        channelNameScrollPane.setPreferredSize( 
            new java.awt.Dimension( 10, 10 ) );

        Color bg = new java.awt.Color( 245, 245, 255 );

        channelNamePanel.setBackground( bg );

        JPanel tmpJPanel = new JPanel(  );

        tmpJPanel.setPreferredSize( new java.awt.Dimension( 24, 24 ) );

        tmpJPanel.setBackground( bg );

        channelNameScrollPane.setColumnHeaderView( tmpJPanel );

        channelNameScrollPane.setViewportView( channelNamePanel );

        splitPaneChanProg.setLeftComponent( channelNameScrollPane );

        programmesScrollPane.setBorder( null );

        programmesScrollPane.setColumnHeaderView( timePanel );

        programmesPanel.setBackground( bg );

        /*


        * TODO FreeGuide.prefs.favourites.addFGPreferenceChangeListener( new


        * FGPreferenceChangeListener( ) { public void preferenceChange(


        * FGPreferenceChangeEvent evt ) {


        *


        * //TODO: maybe repaint() programmesPanel.invalidate( ); } } );


        */
        /*


        * FreeGuide.prefs.chosen_progs.addFGPreferenceChangeListener( new


        * FGPreferenceChangeListener( ) { public void preferenceChange(


        * FGPreferenceChangeEvent evt ) {


        *


        * //TODO: maybe repaint() programmesPanel.invalidate( ); } } );


        */
        programmesScrollPane.setViewportView( programmesPanel );

        timePanel.setPreferredSize( new java.awt.Dimension( 24, 24 ) );

        timePanel.setLayout( null );

        timePanel.setBackground( bg );

        splitPaneChanProg.setRightComponent( programmesScrollPane );

        splitPaneChanProg.setFocusable( false );

        splitPaneChanProg.addFocusListener( 
            new BorderChanger( splitPaneChanProg ) );

        splitPaneMainDet.setLeftComponent( splitPaneChanProg );

        splitPaneMainDet.addFocusListener( 
            new BorderChanger( splitPaneMainDet ) );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 2;

        gridBagConstraints.gridwidth = 4;

        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints.weightx = 0.9;

        gridBagConstraints.weighty = 0.9;

        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );

        add( splitPaneMainDet, gridBagConstraints );

        /*


        * gridBagConstraints = new java.awt.GridBagConstraints();


        * gridBagConstraints.gridx = 2; gridBagConstraints.gridy = 4;


        * gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;


        * gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);


        * getContentPane().add(progressBar, gridBagConstraints);


        */
        // }}}
        // {{{ Bottom buttons
        butRevertToFavourites.setFont( new java.awt.Font( "Dialog", 0, 10 ) );

        butRevertToFavourites.setText( 
            parent.getLocalizer(  ).getLocalizedMessage( "reset_programmes" ) );

        butRevertToFavourites.setMnemonic( KeyEvent.VK_R );

        butRevertToFavourites.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butRevertToFavouritesActionPerformed( evt );

                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 4;

        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;

        gridBagConstraints.insets = new java.awt.Insets( 0, 0, 2, 0 );

        add( butRevertToFavourites, gridBagConstraints );

        butPrint.setFont( new java.awt.Font( "Dialog", 0, 10 ) );

        butPrint.setText( 
            parent.getLocalizer(  ).getLocalizedMessage( 
                "HorizontalViewer.Print" ) );

        butPrint.setMnemonic( KeyEvent.VK_P );

        butPrint.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butPrintActionPerformed( evt );

                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 3;

        gridBagConstraints.gridy = 4;

        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;

        gridBagConstraints.insets = new java.awt.Insets( 0, 0, 2, 0 );

        add( butPrint, gridBagConstraints );

        // If we have a grabber command, add a listener, otherwise dull the
        // button.
        /*


        * if( FreeGuide.prefs.commandline.get( "tv_config.1", null ) != null ) {


        * mbtConfigure.addActionListener( new java.awt.event.ActionListener( ) {


        * public void actionPerformed( ActionEvent evt ) {


        * mbtConfigureActionPerformed( evt ); } } ); } else {


        * mbtConfigure.setEnabled( false ); }


        */
        // getRootPane( ).setDefaultButton( butGoToNow );
        // }}}
        // {{{ Event listeners
        // Do the listeners
        programmesScrollPane.getVerticalScrollBar(  ).addAdjustmentListener( 
            new java.awt.event.AdjustmentListener(  )
            {
                public void adjustmentValueChanged( 
                    java.awt.event.AdjustmentEvent evt )
                {
                    programmesScrollPaneVerAdjust( evt );

                }
            } );

        boolean alignTextToLeftOfScreen = true;

        // FreeGuide.prefs.screen.getBoolean( "align_text_to_left", true );
        if( alignTextToLeftOfScreen )
        {

            /**
             * Listen for scroll events to make programmes off the left still
             * visible.
             */
            comProgramScrollListener =
                new AdjustmentListener(  )
                    {
                        public void adjustmentValueChanged( AdjustmentEvent e )
                        {
                            programScrolled( e );

                        }
                    };

            programmesScrollPane.getHorizontalScrollBar(  )
                                .addAdjustmentListener( 
                comProgramScrollListener );

        }

        // }}}
    }

    /**
     * In future this will launch the config step of the grabber. Currently
     * unimplemented.
     *
     * @param evt The event object
     */

    /*


    * public void mbtConfigureActionPerformed( java.awt.event.ActionEvent evt ) {


    *


    * String preconfig_message = null;


    *


    * //FreeGuide.prefs.misc.get( "preconfig_message" ); if( preconfig_message !=


    * null ) { JOptionPane.showMessageDialog( this, preconfig_message );


    *  }


    *


    * Calendar cal = GregorianCalendar.getInstance( ); cal.setTimeInMillis(


    * theDate );


    *


    * //new GrabberController( ).grabXMLTV( // parent,


    * FreeGuide.prefs.getCommands( "tv_config" ), //FreeGuide.msg.getString(


    * "configuring" ), cal ); }


    */

    /**
     * Event handler for when the Reset button is pressed
     *
     * @param evt The event object
     */
    public void butRevertToFavouritesActionPerformed( 
        java.awt.event.ActionEvent evt )
    {

        // Tell the prefs we've got no programmes for today
        // FreeGuide.prefs.somethingInGuide( theDate, false );
        resetSelections(  );

        //---        printedGuideArea.update(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetSelections(  )
    {

        /*


        * TODO FavouritesList favouritesList = FavouritesList.getInstance( );


        *


        * for( Iterator i = ( (ProgrammeStripModel)programmesPanel.getModel( )


        * ).getAll( ) .iterator( ); i.hasNext( ); ) {


        *


        * Programme programme = (Programme)( i.next( ) );


        *


        * programme.setInGuide( favouritesList.isFavourite( programme ) );


        *  }


        */
    }

    /**
     * Event handler for the channel -> change icon menu entry
     *
     * @param evt
     */
    /**
     * Event handler for when the "Print" button is pressed
     *
     * @param evt The event object
     */
    public void butPrintActionPerformed( java.awt.event.ActionEvent evt )
    {

        //---        printedGuideArea.writeOutAsHTML(  );
    }

    /**
     * Event handler for when the "Next" button is clicked
     *
     * @param evt The event object
     */
    public void butNextDayActionPerformed( java.awt.event.ActionEvent evt )
    {

        if( 
            ( comTheDate.getSelectedIndex(  ) + 1 ) < comTheDate.getItemCount(  ) )
        {
            comTheDate.setSelectedIndex( comTheDate.getSelectedIndex(  ) + 1 );
        }
        else
        {
            parent.askForLoadData(  );
        }
    }

    /**
     * Event handler for when the "Previous" button is clicked
     *
     * @param evt The event object
     */
    public void butPreviousDayActionPerformed( java.awt.event.ActionEvent evt )
    {

        if( comTheDate.getSelectedIndex(  ) > 0 )
        {
            comTheDate.setSelectedIndex( comTheDate.getSelectedIndex(  ) - 1 );
        }
        else
        {
            parent.askForLoadData(  );
        }
    }

    /**
     * The event procedure for the vertical scrollpane listener - just calls
     * the scrollChannelNames method.
     *
     * @param evt The event object
     */
    public void programmesScrollPaneVerAdjust( 
        java.awt.event.AdjustmentEvent evt )
    {
        scrollChannelNames(  );

    }

    // }}}
    // {{{ Reactions to events

    /**
     * Scrolls the channel names to the same y-position as the main panel.
     */
    public void scrollChannelNames(  )
    {
        channelNameScrollPane.getVerticalScrollBar(  ).setValue( 
            programmesScrollPane.getVerticalScrollBar(  ).getValue(  ) );

    }

    /**
     * ToDo: DOCUMENT ME!
     *
     * @param reference ToDo: DOCUMENT ME!
     */
    void scrollToReference( String reference )
    {

        // TODO should focus the programme referred by reference
        getPrintedGuideArea(  ).scrollToReference( reference );

    }

    /**
     * When a scoll event happens, repaint the main panel, to allow the text
     * to be adjusted to be visible even if the programme starts off to the
     * left.
     *
     * @param e DOCUMENT ME!
     */
    private void programScrolled( AdjustmentEvent e )
    {
        programmesPanel.repaint(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param showTime DOCUMENT_ME!
     */
    public void scrollTo( Calendar showTime )
    {

        //---   getProgrammesPanel(  ).focus( showTime.getTimeInMillis(  ) );
        getProgrammesScrollPane(  ).getHorizontalScrollBar(  ).setValue( 
            getTimePanel(  ).getScrollValue( showTime ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JComboBox getComboDate(  )
    {

        return comTheDate;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JComboBox getComboChannelsSet(  )
    {

        return comChannelSet;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JPanel getProgrammesPanel(  )
    {

        return programmesPanel;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JScrollPane getProgrammesScrollPane(  )
    {

        return programmesScrollPane;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JEditorPane getPrintedGuideArea(  )
    {

        return printedGuideArea;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JPanel getChannelNamePanel(  )
    {

        return channelNamePanel;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public TimePanel getTimePanel(  )
    {

        return timePanel;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JEditorPane getDetailsPanel(  )
    {

        return detailsPanel;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JButton getButtonGoToNow(  )
    {

        return butGoToNow;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JButton getButtonDownload(  )
    {

        return butDownload;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JButton getDefaultButton(  )
    {

        return butGoToNow;
    }

    static class BorderChanger implements FocusListener
    {

        /** ToDo: DOCUMENT ME! */
        static final Border focusedBorder = new LineBorder( Color.black, 2 );

        /** ToDo: DOCUMENT ME! */
        static final Border unfocusedBorder = new EmptyBorder( 2, 2, 2, 2 );

        /** ToDo: DOCUMENT ME! */
        JComponent borderChangee;

        /**
         * Creates a new BorderChanger object.
         *
         * @param borderChangee DOCUMENT ME!
         */
        public BorderChanger( JComponent borderChangee )
        {
            this.borderChangee = borderChangee;

        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void focusGained( FocusEvent e )
        {
            borderChangee.setBorder( focusedBorder );

        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void focusLost( FocusEvent e )
        {
            borderChangee.setBorder( unfocusedBorder );

        }
    }

    static class FocusJScrollPane extends JScrollPane
    {

        /**
         * Creates a new FocusJScrollPane object. ToDo: DOCUMENT ME!
         */
        FocusJScrollPane(  )
        {
            super(  );

            this.addFocusListener( new BorderChanger( this ) );

        }

        /*
        * Overridden to be able to add a BorderChanger to the view (not for
        * general use, leaks when called repeatedly, should call
        * removeFocusListener too)
        *
        * @see javax.swing.JScrollPane#setViewportView(java.awt.Component)
        */
        public void setViewportView( Component view )
        {
            super.setViewportView( view );

            view.addFocusListener( new BorderChanger( this ) );

        }
    }

    // }}}
}

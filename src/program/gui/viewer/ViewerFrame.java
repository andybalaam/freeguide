/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
 
//{{{ Imports

package freeguide.gui.viewer;

import freeguide.*;
import freeguide.gui.*;
import freeguide.gui.dialogs.*;
import freeguide.gui.options.*;
import freeguide.gui.wizard.*;
import freeguide.lib.fgspecific.*;
import freeguide.lib.general.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.util.regex.*;
import javax.swing.filechooser.*;
import javax.swing.text.*;

//}}}

/**
 *  The form that displays the listings information. Now contains only the GUI
 *  code with everything else moved out.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    22
 */
public class ViewerFrame extends javax.swing.JFrame implements Progressor {

    static class BorderChanger implements FocusListener {
        static final Border focusedBorder = new LineBorder(Color.black, 2);

        static final Border unfocusedBorder = new EmptyBorder(2, 2, 2, 2);

        JComponent borderChangee;

        public BorderChanger(JComponent borderChangee) {
            this.borderChangee = borderChangee;
        }

        public void focusGained(FocusEvent e) {
            borderChangee.setBorder(focusedBorder);
        }

        public void focusLost(FocusEvent e) {
            borderChangee.setBorder(unfocusedBorder);
        }
    }

    static class FocusJScrollPane extends JScrollPane {

        FocusJScrollPane() {
            super();
            this.addFocusListener(new BorderChanger(this));
        }

        /*
         * Overridden to be able to add a BorderChanger to the view (not for
         * general use, leaks when called repeatedly, should call
         * removeFocusListener too)
         * 
         * @see javax.swing.JScrollPane#setViewportView(java.awt.Component)
         */
        public void setViewportView(Component view) {
            super.setViewportView(view);
            view.addFocusListener(new BorderChanger(this));
        }
    }
    
    public MessageDialogTimer reminderTimer;
    
    //{{{ Constructor
    /**
     *  Constructor for the FreeGuideViewer object
     *
     * @param newLauncher
     *            What screen launched this screen
     * @param pleaseWait
     *            The window saying "Please Wait"
     */
    public ViewerFrame(PleaseWaitFrame pleaseWait) {

        // Set the look and feel
        setLookAndFeel(); 

        // Set up basic variables
        progressor = pleaseWait;
        xmltvLoader = new ViewerFrameXMLTVLoader();
        
        // Set up the channel sets and dates available
        findChannelSets();
        setupAndFindDates();

        // Get the channel set from preferences
        findInitialChannelSet();
        
        // Find out what date it is today
        findInitialDate();

        // Set the progress meter to 5%
        progressor.setProgress( 5 );
        
        // Load in all the XMLTV data into an XMLTVLoader object
        xmltvLoader.loadProgrammeData( theDate );

        // Set up the static elements of the GUI
        drawStaticGUI();

        // Draw the dates and channel sets
        drawDateComboList();
        drawChannelSetComboList();
        
        // Draw the programmes
        drawProgrammes();
        
        // Show the printed guide
        printedGuideArea.update();
        
        detailsPanel.updateProgramme( null );

        // Get rid of the "Please Wait" window if it is visible
        if (pleaseWait != null) {
            pleaseWait.dispose();
            progressor = this;
        }

        // Show the window
        //
        // We call invokeLater() because realized components should be
        // manipulated only from the event-dispatching thread. See e.g.
        // http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                pack();

                java.awt.Dimension screenSize =
                    java.awt.Toolkit.getDefaultToolkit().getScreenSize();

                // Load the window size and position etc.
                // --------------------------------------
                setSize(
                    FreeGuide.prefs.screen.getInt("viewer_width", 640),
                    FreeGuide.prefs.screen.getInt("viewer_height", 400)
                );

                setLocation(
                    FreeGuide.prefs.screen.getInt(
                        "viewer_left", (screenSize.width - 640) / 2),
                    FreeGuide.prefs.screen.getInt(
                        "viewer_top", (screenSize.height - 400) / 2)
                );

                splitPaneChanProg.setDividerLocation(
                    FreeGuide.prefs.screen.getInt(
                        "viewer_splitpane_vertical", 100)
                );
                splitPaneMainDet.setDividerLocation(
                    FreeGuide.prefs.screen.getInt(
                        "viewer_splitpane_horizontal", 150)
                );
                splitPaneGuideDet.setDividerLocation(
                    FreeGuide.prefs.screen.getInt(
                        "viewer_splitpane_horizontal_bottom", 400)
                );

                setVisible( true );
                programmesPanel.requestFocusInWindow();
                
                //Scroll to the correct time
                scrollToNow();
                scrollToNow();
                
            }
        });

        // Check the FreeGuide version
        if( !FreeGuide.prefs.misc.get( "privacy", "no" ).equals( "no" ) ) {
            
            // Run the check in a separate thread to avoid blocking.
            new VersionCheckerThread( this ).start();
            
        }
        
        // Ask the user to download more data if it is missing
        checkForNoData( pleaseWait );
        
    }

    //}}}
    
    //{{{ Initialisation methods
    
    /**
     * Checks whether the XMLTVLoader managed to get any data, and asks the user
     * to download more if not.
     */
    private void checkForNoData( PleaseWaitFrame pleaseWait ) {
        
        if( !xmltvLoader.hasData() ) {
                
            Object[] oa = new Object[2];
            oa[0] = FreeGuide.msg.getString(
                "there_are_missing_listings_for_today.1" );
            oa[1] = FreeGuide.msg.getString(
                "there_are_missing_listings_for_today.2" );
            int r = JOptionPane.showConfirmDialog(this, oa,
                FreeGuide.msg.getString( "download_listings_q" ),
                JOptionPane.YES_NO_OPTION );
                
            if(r==0) {

                pleaseWait.dispose();
                downloadListings();
                
            } 
        }    
    }
    
    /**
     * Find the dates available by what files exist.  This sets up a datelister
     * for later so you can just call findDates() later.
     */
    private void setupAndFindDates() {
        
        // Find out what dates exist, from the filenames
        dateFilesExistList = new DateFilesExistList(
            FreeGuide.prefs.performSubstitutions(
                    FreeGuide.prefs.misc.get( "working_directory" ) ),
            "^tv-\\d{8}\\.xmltv$"
        );
        
    }

    /**
     * Find out today's date
     */
    private void findInitialDate() {
        
        // Set the date to today
        theDate = GregorianCalendar.getInstance();
        Time nowTime = new Time(theDate);
        Time day_start_time = FreeGuide.prefs.misc.getTime(
                "day_start_time", new Time( 0, 0 ));

        if (nowTime.before(day_start_time, new Time(0, 0))) {

            theDate.add(Calendar.DAY_OF_YEAR, -1);

        }
        
    }
    
    /**
     * Find out what channel set is saved in the preferences
     */
    private void findInitialChannelSet() {
        
        String currentChannelSetString = FreeGuide.prefs.screen.get(
            "viewer_channel_set", CHANNEL_SET_ALL_CHANNELS );
        
        currentChannelSet = getChannelSetInterfaceFromName(
            currentChannelSetString );
        
    }
    
    //}}}
    
    //{{{ Static GUI code 
    
    /**
     *  Set up the entire static bit of the GUI
     */
    private void drawStaticGUI() {

        //{{{ Initialisations
        
        java.awt.GridBagConstraints gridBagConstraints;

        progressBar = new javax.swing.JProgressBar( 0, 100 );
        
        popMenuChannel = new javax.swing.JPopupMenu();
        mbtChangeIcon = new javax.swing.JMenuItem();
        mbtResetIcon = new javax.swing.JMenuItem();
        topButtonsPanel = new javax.swing.JPanel();
        butGoToNow = new javax.swing.JButton();
        butPreviousDay = new javax.swing.JButton();
        comTheDate = new javax.swing.JComboBox();
        comChannelSet = new javax.swing.JComboBox();
        butNextDay = new javax.swing.JButton();
        splitPaneMainDet = new javax.swing.JSplitPane();
        printedGuideScrollPane = new FocusJScrollPane();
        printedGuideArea = new ViewerFrameHTMLGuide( this );
        detailsPanel = new ProgrammeDetailsJPanel( this );
        splitPaneChanProg = new javax.swing.JSplitPane();
        splitPaneGuideDet = new javax.swing.JSplitPane();
        channelNameScrollPane = new FocusJScrollPane();
        channelNamePanel = new javax.swing.JPanel();
        programmesScrollPane = new FocusJScrollPane();
        programmesPanel = new StripView();
        timePanel = new TimePanel();
        butRevertToFavourites = new javax.swing.JButton();
        butPrint = new javax.swing.JButton();
        butDownload = new javax.swing.JButton();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        mbtDownload = new javax.swing.JMenuItem();
        mbtPrint = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        mbtQuit = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        mbtConfigure = new javax.swing.JMenuItem();
        mbtFavourites = new javax.swing.JMenuItem();
        mbtChannelSets = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        mbtOptions = new javax.swing.JMenuItem();
        mbtFirstTime = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        mbtUserGuide = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        mbtAbout = new javax.swing.JMenuItem();

        //}}}
        
        //{{{ popMenuChannel
        
        popMenuChannel.addPopupMenuListener(
            new javax.swing.event.PopupMenuListener() {
                public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                    popMenuChannelPopupMenuWillBecomeVisible(evt);
                }


                public void popupMenuWillBecomeInvisible(
                        javax.swing.event.PopupMenuEvent evt) { }


                public void popupMenuCanceled(
                        javax.swing.event.PopupMenuEvent evt) { }
            });

        mbtChangeIcon.setText( FreeGuide.msg.getString( "change_icon" ) );
        mbtChangeIcon.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtChangeIconActionPerformed(evt);
                }
            });

        popMenuChannel.add( mbtChangeIcon);

        mbtResetIcon.setText( FreeGuide.msg.getString(
            "reset_to_default_icon" ) );
        mbtResetIcon.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtResetIconActionPerformed(evt);
                }
            });
        
        
        //}}}

        //{{{ Main Window

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle( "FreeGuide " + FreeGuide.version.getDotFormat() );
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            }
        );
        
        java.awt.Dimension screenSize =
            java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        // Load the window size and position etc.
        // --------------------------------------
        setSize(
                FreeGuide.prefs.screen.getInt("viewer_width", 640),
            FreeGuide.prefs.screen.getInt("viewer_height", 400)
        );

        setLocation(
            FreeGuide.prefs.screen.getInt(
                "viewer_left", (screenSize.width - 640) / 2),
            FreeGuide.prefs.screen.getInt(
                "viewer_top", (screenSize.height - 400) / 2)
        );

        splitPaneChanProg.setDividerLocation(
            FreeGuide.prefs.screen.getInt("viewer_splitpane_vertical", 100));
        splitPaneMainDet.setDividerLocation(
            FreeGuide.prefs.screen.getInt("viewer_splitpane_horizontal", 150));
        splitPaneGuideDet.setDividerLocation(
            FreeGuide.prefs.screen.getInt(
                "viewer_splitpane_horizontal_bottom", 400)
        );
        //}}}

        //{{{ topButtonsPanel
        
        topButtonsPanel.setLayout(new java.awt.GridBagLayout());

        butGoToNow.setFont(new java.awt.Font("Dialog", 0, 10));
        butGoToNow.setText( FreeGuide.msg.getString( "go_to_now" ) );
        butGoToNow.setMnemonic( KeyEvent.VK_N );
        butGoToNow.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butGoToNowActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        topButtonsPanel.add(butGoToNow, gridBagConstraints);

        butPreviousDay.setText( FreeGuide.msg.getString( "minus" ) );
        butPreviousDay.setMnemonic( KeyEvent.VK_MINUS );
        butPreviousDay.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butPreviousDayActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        topButtonsPanel.add(butPreviousDay, gridBagConstraints);

        comTheDate.setEditable(true);
        comTheDate.setFont(new java.awt.Font("Dialog", 0, 10));
        comTheDate.setMinimumSize(new java.awt.Dimension(120, 25));
        comTheDate.setPreferredSize(new java.awt.Dimension(120, 25));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        topButtonsPanel.add(comTheDate, gridBagConstraints);

        butNextDay.setText( FreeGuide.msg.getString( "plus" ) );
        butNextDay.setMnemonic( KeyEvent.VK_EQUALS );
        butNextDay.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butNextDayActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        topButtonsPanel.add(butNextDay, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        getContentPane().add(topButtonsPanel, gridBagConstraints);

        //}}}

        //{{{ Other top buttons
        
        comChannelSet.setEditable(false);
        comChannelSet.setFont(new java.awt.Font("Dialog", 0, 10));
        comChannelSet.setMinimumSize(new java.awt.Dimension(170, 25));
        comChannelSet.setPreferredSize(new java.awt.Dimension(140, 25));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(comChannelSet, gridBagConstraints);

        butDownload.setFont(new java.awt.Font("Dialog", 0, 10));
        butDownload.setText( FreeGuide.msg.getString( "download_listings" ) );
        butDownload.setMnemonic( KeyEvent.VK_D );
        butDownload.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butDownloadActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        getContentPane().add(butDownload, gridBagConstraints);
        
        //}}}
        
        //{{{ Split panes etc
        
        splitPaneMainDet.setOneTouchExpandable(true);
        splitPaneMainDet.setOrientation(
            javax.swing.JSplitPane.VERTICAL_SPLIT );
        
        splitPaneGuideDet.setOneTouchExpandable(true);
        splitPaneGuideDet.setOrientation(
            javax.swing.JSplitPane.HORIZONTAL_SPLIT );
        
        printedGuideArea.setEditable(false);
        printedGuideArea.setContentType("text/html");
        
        favouritesChangeListener =
            new FGPreferenceChangeListener() {
                public void preferenceChange( FGPreferenceChangeEvent evt ) {
                    printedGuideArea.update();
                }
            };
        
        FreeGuide.prefs.favourites.addFGPreferenceChangeListener(
            favouritesChangeListener );
            

        // DEBUG
        FreeGuide.prefs.chosen_progs.addFGPreferenceChangeListener(
            new FGPreferenceChangeListener() {
                public void preferenceChange( FGPreferenceChangeEvent evt ) {
                    printedGuideArea.update();
                }
            }
        );

        printedGuideScrollPane.setViewportView(printedGuideArea);

        splitPaneMainDet.setRightComponent(splitPaneGuideDet);
        
        splitPaneGuideDet.setLeftComponent( printedGuideScrollPane );
        splitPaneGuideDet.setRightComponent( detailsPanel );

        channelNameScrollPane.setBorder(null);
        channelNameScrollPane.setVerticalScrollBarPolicy(
            javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        channelNameScrollPane.setMinimumSize(new java.awt.Dimension(10, 10));
        channelNameScrollPane.setPreferredSize(new java.awt.Dimension(10, 10));
        channelNamePanel.setLayout(null);

        Color bg = new java.awt.Color(245, 245, 255);
        channelNamePanel.setBackground(bg);

        JPanel tmpJPanel = new JPanel();
        tmpJPanel.setPreferredSize(new java.awt.Dimension(24, 24));
        tmpJPanel.setBackground(bg);
        channelNameScrollPane.setColumnHeaderView(tmpJPanel);

        channelNameScrollPane.setViewportView(channelNamePanel);

        splitPaneChanProg.setLeftComponent(channelNameScrollPane);
        
        programmesScrollPane.setBorder(null);
        programmesScrollPane.setColumnHeaderView(timePanel);

        programmesPanel.setLayout(null);
        programmesPanel.setBackground(bg);

        FreeGuide.prefs.favourites.addFGPreferenceChangeListener(
            new FGPreferenceChangeListener() {
                public void preferenceChange( FGPreferenceChangeEvent evt ) {
                    //TODO: maybe repaint()
                    programmesPanel.invalidate();
                }
            }
        );

        FreeGuide.prefs.chosen_progs.addFGPreferenceChangeListener(
            new FGPreferenceChangeListener() {
                public void preferenceChange( FGPreferenceChangeEvent evt ) {
                    //TODO: maybe repaint()
                    programmesPanel.invalidate();
                }
            }
        );

        programmesScrollPane.setViewportView(programmesPanel);

        timePanel.setPreferredSize(new java.awt.Dimension(24, 24));
        timePanel.setLayout(null);
        timePanel.setBackground(bg);

        splitPaneChanProg.setRightComponent(programmesScrollPane);
        splitPaneChanProg.setFocusable(false);
        splitPaneChanProg.addFocusListener(new BorderChanger(splitPaneChanProg));

        splitPaneMainDet.setLeftComponent(splitPaneChanProg);
        splitPaneMainDet.addFocusListener(new BorderChanger(splitPaneMainDet));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(splitPaneMainDet, gridBagConstraints);

        /*gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(progressBar, gridBagConstraints);*/
        
        //}}}
        
        //{{{ Bottom buttons
        
        butRevertToFavourites.setFont(new java.awt.Font("Dialog", 0, 10));
        butRevertToFavourites.setText( FreeGuide.msg.getString(
            "reset_programmes" ) );
        butRevertToFavourites.setMnemonic( KeyEvent.VK_R );
        butRevertToFavourites.addActionListener(new ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butRevertToFavouritesActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(butRevertToFavourites, gridBagConstraints);

        butPrint.setFont(new java.awt.Font("Dialog", 0, 10));
        butPrint.setText( FreeGuide.msg.getString(
            "print_this_personalised_listing" ) );
        butPrint.setMnemonic( KeyEvent.VK_P );
        butPrint.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butPrintActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        getContentPane().add(butPrint, gridBagConstraints);
        //}}}

        //{{{ Menus
        
        fileMenu.setText("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        mbtDownload.setText( FreeGuide.msg.getString( "download_listings" ) );
        mbtDownload.setMnemonic(KeyEvent.VK_D);
        mbtDownload.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_D, InputEvent.CTRL_MASK));
        mbtDownload.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtDownloadActionPerformed(evt);
                }
            });

        fileMenu.add(mbtDownload);

        mbtPrint.setText( FreeGuide.msg.getString(
            "print_this_personalised_listing" ) );
        mbtPrint.setMnemonic(KeyEvent.VK_P);
        mbtPrint.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_P, InputEvent.CTRL_MASK));
        mbtPrint.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtPrintActionPerformed(evt);
                }
            });

        fileMenu.add(mbtPrint);

        fileMenu.add(jSeparator5);

        mbtQuit.setText( FreeGuide.msg.getString( "quit" ) );
        mbtQuit.setMnemonic(KeyEvent.VK_Q);
        mbtQuit.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        mbtQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                    mbtQuitActionPerformed(evt);
                }
            });

        fileMenu.add(mbtQuit);

        mainMenuBar.add(fileMenu);

        toolsMenu.setText( FreeGuide.msg.getString( "tools" ) );
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        mbtConfigure.setText( FreeGuide.msg.getString( "choose_channels" ) );
        mbtConfigure.setMnemonic(KeyEvent.VK_C);
        mbtConfigure.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C,
            InputEvent.CTRL_MASK ) );
        
        // If we have a grabber command, add a listener, otherwise dull the
        // button.
        if (FreeGuide.prefs.commandline.get("tv_config.1", null) != null) {
            mbtConfigure.addActionListener(
                new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        mbtConfigureActionPerformed(evt);
                    }
                }
            );
            } else {
                mbtConfigure.setEnabled( false );
            }

        toolsMenu.add(mbtConfigure);

        mbtFavourites.setText( FreeGuide.msg.getString( "favourites_dot" ) );
        mbtFavourites.setMnemonic(KeyEvent.VK_F);
        mbtFavourites.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_F, InputEvent.CTRL_MASK));
        mbtFavourites.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtFavouritesActionPerformed(evt);
                }
            });

        toolsMenu.add(mbtFavourites);

        mbtChannelSets.setText( FreeGuide.msg.getString( "channel_sets_dot" ) );
        mbtChannelSets.setMnemonic(KeyEvent.VK_H);
        mbtChannelSets.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_H, InputEvent.CTRL_MASK));
        mbtChannelSets.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtChannelSetsActionPerformed(evt);
                }
            });

        toolsMenu.add(mbtChannelSets);
        toolsMenu.add(new javax.swing.JSeparator());

        mbtFirstTime.setText( FreeGuide.msg.getString( "first_time_wizard" ) );
        mbtFirstTime.setMnemonic(KeyEvent.VK_F);
        mbtOptions.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_W,
            InputEvent.CTRL_MASK ) );
        mbtFirstTime.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtFirstTimeActionPerformed(evt);
                }
            });
        
        toolsMenu.add(mbtFirstTime);
        toolsMenu.add(jSeparator1);
        
        mbtOptions.setText( FreeGuide.msg.getString( "options_dot" ) );
        mbtOptions.setMnemonic(KeyEvent.VK_O);
        mbtOptions.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_O, InputEvent.CTRL_MASK));
        mbtOptions.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtOptionsActionPerformed(evt);
                }
            });

        toolsMenu.add(mbtOptions);

        mainMenuBar.add(toolsMenu);

        helpMenu.setText( FreeGuide.msg.getString( "help" ) );
        helpMenu.setMnemonic(KeyEvent.VK_H);
        mbtUserGuide.setText( FreeGuide.msg.getString( "user_guide" ) );
        mbtUserGuide.setMnemonic(KeyEvent.VK_U);
        mbtUserGuide.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtUserGuideActionPerformed(evt);
                }
            });

        helpMenu.add(mbtUserGuide);

        helpMenu.add(jSeparator4);

        mbtAbout.setText( FreeGuide.msg.getString( "about" ) );
        mbtAbout.setMnemonic(KeyEvent.VK_A);
        mbtAbout.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_A, InputEvent.CTRL_MASK));
        mbtAbout.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtAboutActionPerformed(evt);
                }
            });

        helpMenu.add(mbtAbout);

        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        getRootPane().setDefaultButton( butGoToNow );
        
        //}}}

        //{{{ Event listeners
        
        //  Do the listeners
        programmesScrollPane.getVerticalScrollBar().addAdjustmentListener(
            new java.awt.event.AdjustmentListener() {
                public void adjustmentValueChanged(
                        java.awt.event.AdjustmentEvent evt) {

                    programmesScrollPaneVerAdjust(evt);

                }
            });

        comTheDateItemListener = (
            new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    comTheDateItemStateChanged(evt);
                }
            });
        
        comTheDate.addItemListener( comTheDateItemListener );
        
        comChannelSetItemListener = (
            new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent evt) {
                    comChannelSetItemStateChanged(evt);
                }
            });

        comChannelSet.addItemListener( comChannelSetItemListener );
  
        boolean alignTextToLeftOfScreen = FreeGuide.prefs.screen.getBoolean(
            "align_text_to_left", true );
            
        if( alignTextToLeftOfScreen ) {
  
            /**
            * Listen for scroll events to make programmes off the left still
            * visible.
            */
            comProgramScrollListener = new AdjustmentListener() {
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    programScrolled(e);
                }
            };
            
            programmesScrollPane.getHorizontalScrollBar()
                    .addAdjustmentListener(comProgramScrollListener);
            
        }
        
         //}}}
        
    }

    //}}}

    //{{{ (re)-Initialisation methods
    
    private void setLookAndFeel() {
        LookAndFeel currentLAF = UIManager.getLookAndFeel();
        String defaultLAFName = "Metal";
        String currentLAFClassName = null;
        if (currentLAF != null) {
            defaultLAFName = currentLAF.getName();
            currentLAFClassName = currentLAF.getClass().getName();
        }
        String requestedLookAndFeel = FreeGuide.prefs.screen.get(
                    "look_and_feel", defaultLAFName);
        if ((!requestedLookAndFeel.equals(defaultLAFName))
                && (!(requestedLookAndFeel.equals(currentLAFClassName)))) {
            String className = LookAndFeelManager
                    .getLookAndFeelClassName(requestedLookAndFeel);
            if (className == null) {
                // Assume that the pref specifies the classname
                // and do our best
                className = requestedLookAndFeel; 
            }
            try {
                UIManager.setLookAndFeel(className);
                SwingUtilities.updateComponentTreeUI(this);
            } catch (ClassNotFoundException e) {
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (UnsupportedLookAndFeelException e) {
            }

        }
    }

    /**
     * Find the list of channel sets available
     */
    private void findChannelSets() {
        
        // Find out what channel sets exist, from the preferences
        channelSetsList = FreeGuide.prefs.getChannelSets();
        
    }
    
    /**
     * Find the dates available from an already-set-up datelister
     */
    private void findDates() {
        
        dateFilesExistList.updateDates();
        
    }
    
    /**
     * Create the combobox lists for the dates and channel sets
     */
    private void drawDateComboList() {
        
        // Stop listening to item events temporarily while we mess about
        comTheDate.removeItemListener( comTheDateItemListener );
        
        // Remove all the items from the combo box:
        // Working around a bug with JComboBox.removeAllItems()
        //comTheDate.removeAllItems();
        int i;
        int itemCount = comTheDate.getItemCount();
        for ( i=0; i<itemCount; i++ ) {
            
            comTheDate.removeItemAt( 0 );
            
        }
        
        for ( i=0; i < dateFilesExistList.size(); i++ ) {

            comTheDate.insertItemAt(
                comboBoxDateFormat.format(dateFilesExistList.get(i)), i);
        }
        
        goToDate( theDate.getTime() ); 
        
        // Restart listening to item events
        comTheDate.addItemListener( comTheDateItemListener );
        
    }
        
    private void drawChannelSetComboList() {

        // Stop listening to item events temporarily while we mess about
        comChannelSet.removeItemListener( comChannelSetItemListener );
        
        // Remove all the items from the combo box:
        // Working around a bug with JComboBox.removeAllItems()
        int i;
        int itemCount = comChannelSet.getItemCount();
        for ( i=0; i<itemCount; i++ ) {
            comChannelSet.removeItemAt( 0 );
        }
        
        // Add the "All Channels" item
        comChannelSet.insertItemAt( CHANNEL_SET_ALL_CHANNELS, 0);
        
        for ( i=0; i < channelSetsList.length; i++ ) {
            
            comChannelSet.insertItemAt(
                channelSetsList[i].getChannelSetName(), i+1 );

            channelSetsList[i].updateChannelNames( xmltvLoader );
            
        }
        
        comChannelSet.insertItemAt( CHANNEL_SET_EDIT_SETS, i+1);
        comChannelSet.setSelectedItem( currentChannelSet.getChannelSetName() );
        
        // Restart listening to item events
        comChannelSet.addItemListener( comChannelSetItemListener );
        validate();
        
    }
    
    private void scrollToNow() {
        long now = System.currentTimeMillis();
        programmesPanel.focus(now);
        
        programmesScrollPane.getHorizontalScrollBar().setValue(
                timePanel.getScrollValue(now) - 100);
    }
    
    void scrollTo(Calendar showTime) {
        programmesPanel.focus(showTime.getTimeInMillis());
        
        programmesScrollPane.getHorizontalScrollBar().setValue(
                timePanel.getScrollValue(showTime));
    }

        void scrollToReference(String reference) {
        // TODO should focus the programme referred by reference
          printedGuideArea.scrollToReference(reference);
        }

    //}}}
    
    //{{{ drawProgrammes
    /**
     * Draw all the programmes and channels on screen
     */
    private void drawProgrammes() {
        
        //FreeGuide.log.info( "drawProgrammes - begin" );
        
        //{{{ Set up variables
        
        String lineBreak = System.getProperty("line.separator");

        // Read in viewing options
        int channelHeight = FreeGuide.prefs.screen.getInt("channel_height",
                FreeGuide.CHANNEL_HEIGHT);
        int halfVerGap = FreeGuide.prefs.screen.getInt("vertical_gap",
                FreeGuide.VERTICAL_GAP);
        int halfHorGap = FreeGuide.prefs.screen.getInt("horizontal_gap",
                FreeGuide.HORIZONTAL_GAP);
        int panelWidth = FreeGuide.prefs.screen.getInt("panel_width",
                FreeGuide.PANEL_WIDTH);

        ProgrammeJLabel.setTickedColour( FreeGuide.prefs.screen.getColor(
            "programme_chosen_colour", FreeGuide.PROGRAMME_INGUIDE_COLOUR ) );
        
        ProgrammeJLabel.setMovieColour( FreeGuide.prefs.screen.getColor(
            "programme_movie_colour", FreeGuide.PROGRAMME_MOVIE_COLOUR ) );
            
        ProgrammeJLabel.setNonTickedColour( FreeGuide.prefs.screen.getColor(
            "programme_normal_colour", FreeGuide.PROGRAMME_NOTINGUIDE_COLOUR ) );
        
        ProgrammeJLabel.setHeartColour( FreeGuide.prefs.screen.getColor(
            "programme_heart_colour", FreeGuide.PROGRAMME_HEART_COLOUR ) );

        ProgrammeJLabel.setAlignTextToLeftOfScreen(FreeGuide.prefs.screen
                .getBoolean("align_text_to_left", true));
        
        Color channelColour = FreeGuide.prefs.screen.getColor(
                "channel_colour", FreeGuide.CHANNEL_COLOUR);
        
        String fontName = FreeGuide.prefs.screen.get("font_name", "Dialog");
        int fontStyle = FreeGuide.prefs.screen.getInt("font_style", Font.PLAIN);
        int fontSize = FreeGuide.prefs.screen.getInt("font_size", 12);

        boolean drawTime = FreeGuide.prefs.screen.getBoolean(
                "display_programme_time", true);
    
        boolean draw24time = FreeGuide.prefs.screen.getBoolean(
                "display_24hour_time", true);
        
        timeFormat = !drawTime ? null : (draw24time ? timeFormat24Hour
            : timeFormat12Hour);
        
        Font channelFont = new Font(fontName, Font.BOLD, fontSize);
        Font font = new Font(fontName, fontStyle, fontSize);

        int channelPanelWidth = FreeGuide.prefs.screen.getInt(
                "channel_panel_width", FreeGuide.CHANNEL_PANEL_WIDTH);

        //}}}
        
        //{{{ Draw the channels
        
        // Delete the old ones
        channelNamePanel.removeAll();

        int num_chans = currentChannelSet.getNoChannels();

        // First using the FontMetrics system work out
        // the actual width of the text
        int maxChannelWidth = 0;

        /*for (int c = 0; c < num_chans; c++) {
            FontMetrics myFM = channelNamePanel.getFontMetrics(font);
            int myChanWidth = myFM.stringWidth( currentChannelSet.getChannelName(c) );

            if( myChanWidth > maxChannelWidth ) {
              maxChannelWidth = myChanWidth;
            }
        }*/

        ChannelJLabel[] tmpChannels = new ChannelJLabel[num_chans];
        
        // Create all the JLabels for channels, and set them up
        for( int c = 0; c < num_chans; c++ ) {

            progressor.setProgress( 10 + (c*10) / num_chans );
            
            Channel curChan = 
                currentChannelSet.getChannel(c);
            ChannelJLabel ctxt = new ChannelJLabel(
                    curChan
            );

            ctxt.setBackground( channelColour );
            ctxt.setFont( font );
            ctxt.setBorder(
                BorderFactory.createLineBorder(Color.BLACK));
            ctxt.setHorizontalAlignment( JLabel.LEFT );
            ctxt.setOpaque( true );

            // Get the URL of this channel icon
            String iconURLstr = ctxt.getChannel().getIconURL();
            if ( iconURLstr != null
                 || FreeGuide.prefs.screen.get( "customIcon."
                    + ctxt.getChannel().getID()) != null )
            {
                try {
                    ImageIcon iconImg = null;
                    // If a custom icon is set use it !
                    File iconFile = null;
                    ImageIcon tmpImg;
                    if( FreeGuide.prefs.screen.get( "customIcon."
                        + ctxt.getChannel().getID() ) != null )
                    {
                        iconFile = new File( FreeGuide.prefs.screen.get(
                            "customIcon." + ctxt.getChannel().getID() ) );
                    } else {
                        // First convert the id to a suitable (and safe!!)
                        // filename
                        File cache = new File(ctxt.getChannel().getIconFileName());
                        // then verify if the file is in the cache
                        if (!cache.canRead()) {
                            // if not, we try to fetch it from the url
                            URL iconURL = new URL(iconURLstr);
                            InputStream i = iconURL.openStream();
                            FileOutputStream o = new FileOutputStream(cache);
                            byte buffer[] = new byte[4096];
                            int bCount;
                            while ((bCount = i.read(buffer)) != -1)
                                o.write(buffer, 0, bCount);
                            o.close();
                            i.close();
                        }
                        iconFile = cache;
                    }
                    /* We then try to read the file which should be in
                     * the cache If it's not, it doesn't matter,
                     * either the URL is not valid or the file
                     * couldn't be read and we should have left the
                     * try anyway, or we will when we try to read it
                     * Thus the icon will still be equal to null and
                     * we won't show one
                     */                    
                    ctxt.setIcon(iconFile.getCanonicalPath());
                
                } catch (MalformedURLException e) {
                    // Do nothing
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            // TODO Give it a default icon if one is not available
            /*if( ctxt.getIcon() == null ) {
                ctxt.setIcon( FreeGuide.prefs.performSubstitutions(
                    FreeGuide.prefs.misc.get( "channel_icon_default" ) ) );
            }*/
            
            int myChanWidth = ctxt.getRequiredWidth();
            if( myChanWidth > maxChannelWidth ) {
                maxChannelWidth = myChanWidth;
            }
            
            ctxt.addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }

                public void mousePressed(MouseEvent e) {
                    maybeShowPopup( e );
                }

                public void mouseReleased(MouseEvent e) {
                    maybeShowPopup( e );
                }
                private void maybeShowPopup( java.awt.event.MouseEvent evt ) {
                    
                    if( evt.isPopupTrigger() ) {
                        
                        rightClickedChannel
                        = (ChannelJLabel)evt.getComponent();
                        
                        
                        popMenuChannel.show(
                                evt.getComponent(), evt.getX(), evt.getY() );
                    }
                }
                
            });
            channelNamePanel.add( ctxt );
            
            tmpChannels[c] = ctxt;
            
        }
        
        // Then add a reasonable amount of space as a border
        maxChannelWidth += 5;
        
        for( int c = 0; c < num_chans; c++ ) {
            
            ChannelJLabel ctxt = tmpChannels[c];
        
            ctxt.setBounds(
                0,
                (halfVerGap * 2) + (c * channelHeight) - 1,
                maxChannelWidth,
                channelHeight - (halfVerGap * 4) );
                
        }
        
        // Resize the area
        channelNamePanel.setPreferredSize(
            new Dimension(
                maxChannelWidth,
                currentChannelSet.getNoChannels()
                    * channelHeight + 50 ) );
       
        //}}}
        
        //{{{ Draw the programmes
        
        // Set up the programme and time panels

        Dimension tmp = new Dimension(
            panelWidth,
            currentChannelSet.getNoChannels() * channelHeight
        );
        programmesPanel.setPreferredSize(tmp);
        programmesPanel.setMinimumSize(tmp);
        programmesPanel.setMaximumSize(tmp);

        // Temporal width in millisecs
        programmesPanel.setHorizontalRange(
            xmltvLoader.earliest.getTimeInMillis(),
            xmltvLoader.latest.getTimeInMillis()
        );

        programmesPanel.setIntercellSpacing(halfHorGap * 2, halfVerGap * 2);

        tmp = new Dimension(panelWidth, timePanel.getPreferredSize().height);
        timePanel.setPreferredSize(tmp);
        timePanel.setMinimumSize(tmp);
        timePanel.setMaximumSize(tmp);
            
        // Draw the programmes
        Vector v = FreeGuide.prefs.getInGuideProgrammes(theDate);
        Set programmes = v != null ? new HashSet(v) : new HashSet();
                    
        programmesPanel.setRenderer( new ProgrammeRenderer(
            timeFormat, font, programmesPanel, this ) );
        programmesPanel.setEditor( new ProgrammeRenderer(
            timeFormat, font, programmesPanel, this ) );

        ProgrammeStripModel m = new ProgrammeStripModel(
            currentChannelSet, xmltvLoader);
        programmesPanel.setModel(m);

        printedGuideArea.setModel(m);

        timePanel.setTimes(xmltvLoader.earliest, xmltvLoader.latest);

        //}}}
                
        //{{{ Repaint everything
        
        timePanel.revalidate();
        timePanel.repaint();

        programmesPanel.revalidate();
        programmesPanel.repaint();

        channelNamePanel.revalidate();
        channelNamePanel.repaint();

        //}}}
        
        //FreeGuide.log.info( "drawProgrammes - end" );
        
    }

    //}}}
    
    //{{{ Utilities
    
    /**
     * Check that the chosen channel set exists, and if not, sets it
     * to "- All Channels -".
     */
    private void checkCurrentChannelSet() {
        
        // If it's the default channelset, no problem
        if( currentChannelSet == xmltvLoader ) {
            
            return;
            
        }
            
        // If it's in the list, no problem
        for( int i=0; i<channelSetsList.length; i++ ) {
            
            if( currentChannelSet.getChannelSetName().equals(
                    channelSetsList[i].getChannelSetName() ) ) {
                    
                return;
                
            }
            
        }
        
        FreeGuide.log.info( currentChannelSet.getChannelSetName()
            + FreeGuide.msg.getString( "not_matched" ) );
            
        currentChannelSet = xmltvLoader;
        
    }
    
    /**
     * Change the date combo to the given date. Will trigger an event causing a
     * repaint of all the programmes.
     */
    private void goToDate( Date newDate ) {
        
        comTheDate.setSelectedItem( comboBoxDateFormat.format( newDate ) );
        
    }
    
    /**
     * Find channel set interface given its name
     */
    private ChannelSetInterface getChannelSetInterfaceFromName(
            String channelSetName ) {
        
        // It's either All Channels ...
        
        if( channelSetName.equals( CHANNEL_SET_ALL_CHANNELS ) ) {
            
            return xmltvLoader;
            
        }
        
        // ... or a specific channel set.
        
        // Step through the channel sets
        for (int i = 0; i < channelSetsList.length; i++) {
            
            // Checking whether their name matches the required one.
            if (channelSetsList[i].getChannelSetName().equals(channelSetName)) {

                // If so, return this one.
                return channelSetsList[i];
                
            }
        }
        
        FreeGuide.log.info(
            FreeGuide.msg.getString(
                "the_name_of_the_channel_set_didnt_match_any_known_set" ) );
        
        return xmltvLoader;
        
    }
    
    //}}}
    
    //{{{ Launcher interface code
    
    /**
     *  Unhides this window after being hidden while launching another screen.
     */
    public void reShow() {
        
        // Redraw the channel sets combo
        findChannelSets();
        
        // Get a reference to the current channel set
        currentChannelSet = getChannelSetInterfaceFromName(
            currentChannelSet.getChannelSetName() );
        
        // Redraw the dates combo
        findDates();
        
        // Check that the current channel set is still ok (date will be fine,
        // since all dates are allowable.)
        checkCurrentChannelSet();
        
        // Set the progress meter to 5%
        progressor.setProgress( 5 );
        
        // Load in all the XMLTV data into an XMLTVLoader object
        xmltvLoader.loadProgrammeData( theDate );
        
        // Draw the dates and channel sets
        drawDateComboList();
        drawChannelSetComboList();
        
        // Draw the programmes
        drawProgrammes();
        
        // Show the printed guide
        //FreeGuide.log.info( "DEBUG - 5" );
        printedGuideArea.update();
        
        detailsPanel.updateProgramme( null );
        
        // Show the screen
        setVisible( true );
        
        progressor.setProgress( 0 );

    }

    //}}}

    //{{{ Progressor interface code
    
    public void setProgress( int percent ) {
        
        //progressBar.setValue( percent );
        
    }
    
    //}}}
    
    //{{{ Event Handlers
    
    /**
     *  Event handler when the popup menu of a channel is going to be displayed
     *
     *@param  evt  The event object
     */
    public void popMenuChannelPopupMenuWillBecomeVisible(
            javax.swing.event.PopupMenuEvent evt ) {

        String customIcon = FreeGuide.prefs.screen.get("customIcon."+rightClickedChannel.getChannel().getID());
        
        int menuLength = popMenuChannel.getSubElements().length;
        
        if(customIcon != null && menuLength == 1 ) {
            popMenuChannel.add( mbtResetIcon );
        }
        
        if(customIcon == null && menuLength > 1) {
            popMenuChannel.remove( menuLength-1);
        }
        
    }

    /**
     *  Called when the download button is clicked.
     *
     *@param  evt  The event object
     */
    public void butDownloadActionPerformed(java.awt.event.ActionEvent evt) {

        downloadListings();

    }

    /**
     *  In future this will launch the config step of the grabber.  Currently
     *  unimplemented.
     *
     *@param  evt  The event object
     */
    public void mbtConfigureActionPerformed(java.awt.event.ActionEvent evt) {

        String preconfig_message = FreeGuide.prefs.misc.get(
            "preconfig_message" );
       
        if( preconfig_message != null ) {
                
            JOptionPane.showMessageDialog( this, preconfig_message );
                
        }
        
        Utils.execAndWait( this,
            FreeGuide.prefs.getCommands("tv_config"),
                FreeGuide.msg.getString( "configuring" ), theDate );

    }


    /**
     *  Event handler for when the Reset button is pressed 
     *
     *@param  evt  The event object
     */
    public void butRevertToFavouritesActionPerformed(
        java.awt.event.ActionEvent evt )
    {
        
        // Tell the prefs we've got no programmes for today
        //FreeGuide.prefs.somethingInGuide( theDate, false );

        resetSelections();
        
        printedGuideArea.update();

    }

    public void resetSelections() {
        
        FavouritesList favouritesList = FavouritesList.getInstance();
        
        for( Iterator i = ( (ProgrammeStripModel)programmesPanel.getModel()
            ).getAll().iterator(); i.hasNext(); )
        {
            
            Programme programme = (Programme)( i.next() );
            
            programme.setInGuide( favouritesList.isFavourite( programme ) );
            
        }
        
    }
    
    /**
     *  Event handler for when the Channel Set combo box is changed 
     *
     *@param  evt  The event object
     */
    public void comChannelSetItemStateChanged( java.awt.event.ItemEvent evt ) {
    
        // Do nothing if this isn't an item selection
        if ( evt.getStateChange() != ItemEvent.SELECTED ) {
            
            return;
            
        }
        
        // Set currentChannelSet to the chosen set.
        String channelSetString = (String)comChannelSet.getSelectedItem();
        
        if( channelSetString.equals( CHANNEL_SET_EDIT_SETS ) ) {
            
            editChannelSets();
            return;
            
        } else if( channelSetString.equals( CHANNEL_SET_ALL_CHANNELS ) ) {
            
            currentChannelSet = xmltvLoader;
            
        } else {
        
            int i;
            for( i=0; i<channelSetsList.length; i++ ) {
            
                if( channelSetString.equals(
                    channelSetsList[i].getChannelSetName())
                ) {
                    currentChannelSet = channelSetsList[i];
                    break;
                }
            
            }
        
            // If we didn't find one, set it to the default
            if( i == channelSetsList.length ) {
            
                FreeGuide.log.info( FreeGuide.msg.getString(
                    "channel_set_name_not_found" ) + " "
                    + channelSetString );
            
                currentChannelSet = xmltvLoader;
            
            }
            
        }
            
        // Refresh the programmes
        drawProgrammes();
        
        // Refresh the printed guide
        printedGuideArea.update();
        detailsPanel.updateProgramme( null );
        
        progressor.setProgress( 0 );

    }

    /**
     *  Event handler for when the Date combo box is changed 
     *
     *@param  evt  The event object
     */
    public void comTheDateItemStateChanged(java.awt.event.ItemEvent evt) {
        
        // Do nothing if this isn't an item selection
        if ( evt.getStateChange() != ItemEvent.SELECTED ) {
            
            return;
            
        }
        
        // Set theDate to the date chosen
        try {
            
            theDate.setTime( comboBoxDateFormat.parse(
                (String)comTheDate.getSelectedItem() ) );
                
        } catch( java.text.ParseException e ) {
            
            e.printStackTrace();
            
        }
            
        // Load this day's programmes
        xmltvLoader.loadProgrammeData( theDate );
        
        // Refresh the programmes
        drawProgrammes();
        
        // Refresh the printed guide
        printedGuideArea.update();
        detailsPanel.updateProgramme( null );
        
        progressor.setProgress( 0 );
        
    }

    /**
     *  Event handler for when the "Print" menu item is pressed 
     *
     *@param  evt  The event object
     */
    public void mbtPrintActionPerformed(java.awt.event.ActionEvent evt) {

        printedGuideArea.writeOutAsHTML();

    }

    /**
     *  Event handler for when the "Go To Now" button is pressed 
     *
     *@param  evt  The event object
     */
    public void butGoToNowActionPerformed(java.awt.event.ActionEvent evt) {

        goToNow();

    }

    /**
     *  Event handler for when the "User Guide" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtUserGuideActionPerformed(java.awt.event.ActionEvent evt) {

        String fs = System.getProperty("file.separator");
        
        String[] cmds = Utils.substitute(
            FreeGuide.prefs.commandline.getStrings( "browser_command" ),
            "%filename%",
            "%misc.doc_directory%" + fs + "userguide.html" );
            
        Utils.execNoWait(cmds);

    }
    
    /**
     *  Event handler for when the "About" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtAboutActionPerformed(java.awt.event.ActionEvent evt) {

        new AboutFrame( this, true ).setVisible( true );

    }

    /**
     *  Event handler for when the "First Time Wizard" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtFirstTimeActionPerformed(java.awt.event.ActionEvent evt) {

        new FirstTimeWizard( null, true );

    }
    


    /**
     *  Event handler for when the "Options" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtOptionsActionPerformed(java.awt.event.ActionEvent evt) {

        boolean updated = centreDialogAndRun( new OptionsDialog( this ) );

        if( updated ) {
            // Set the look and feel - Don't want this in reShow()
            setLookAndFeel(); 
            reShow();
        }
        
    }

    private boolean centreDialogAndRun( FGDialog dialog ) {
        
        Dimension thisSize = getSize();
        Dimension dialogSize = dialog.getSize();
        Point thisLocation = getLocation();
        
        dialog.setLocation(
            thisLocation.x + ( ( thisSize.width  - dialogSize.width  ) / 2 ),
            thisLocation.y + ((thisSize.height - dialogSize.height) / 2)
        );
        
        return dialog.showDialog();
        
    }

    /**
     *  Event handler for when the "Channel Sets" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtChannelSetsActionPerformed(java.awt.event.ActionEvent evt) {
        
        editChannelSets();
        
    }
        
    private void editChannelSets() {
    
        boolean updated = centreDialogAndRun(
            new ChannelSetListDialog( this, xmltvLoader ) );
         
        if( updated ) {
            
            channelSetsList = FreeGuide.prefs.getChannelSets();
            drawChannelSetComboList();                        
            
        }
                
    }

    /**
     *  Event handler for when the "Favourites" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtFavouritesActionPerformed(java.awt.event.ActionEvent evt) {

        // DEBUG - do we still need to do this?
        FreeGuide.prefs.favourites.removeFGPreferenceChangeListener(
            favouritesChangeListener );
        
        boolean updated = centreDialogAndRun( new FavouritesListDialog(this) );

        if( updated ) {
            printedGuideArea.update();
            drawProgrammes();
        }

        FreeGuide.prefs.favourites.addFGPreferenceChangeListener(
            favouritesChangeListener );
        
    }

    /**
     *  Event handler for when the "Download" button is pressed
     *
     *@param  evt  The event object
     */
    public void mbtDownloadActionPerformed(java.awt.event.ActionEvent evt) {

        downloadListings();

    }

    /**
     *  Event handler for when the "Quit" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtQuitActionPerformed(java.awt.event.ActionEvent evt) {

        quit();
    }
    
    /**
     * Event handler for the channel -> change icon menu entry
     * @param evt
     */
    
    public void mbtChangeIconActionPerformed(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter () {
            
            private Pattern images = null;

            public boolean accept(File f) {
                if (images == null)
                    images = Pattern.compile("\\.(?i)(?:jpe?g|gif|png|JPG)$");
                return f.isDirectory() || images.matcher(f.getName()).find();
            }

            public String getDescription() {
                return FreeGuide.msg.getString( "images_gif_jpeg_png" );
            }
            
        });
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            FreeGuide.prefs.screen.put("customIcon."+rightClickedChannel.getChannel().getID(),chooser.getSelectedFile().getAbsolutePath());
            rightClickedChannel.setIcon(chooser.getSelectedFile().getAbsolutePath());
        }
        
    }

    /**
     * Event handler for the channel -> reset icon menu entry
     * @param evt
     */
    
    public void mbtResetIconActionPerformed(ActionEvent evt) {
        FreeGuide.prefs.screen.remove("customIcon."+rightClickedChannel.getChannel().getID());
        rightClickedChannel.setDefaultIcon();
    }

    /**
     *  Event handler for when the "Print" button is pressed
     *
     *@param  evt  The event object
     */
    public void butPrintActionPerformed(java.awt.event.ActionEvent evt) {

        printedGuideArea.writeOutAsHTML();

    }

    /**
     *  Event handler for when the "Next" button is clicked
     *
     *@param  evt  The event object
     */
    public void butNextDayActionPerformed(java.awt.event.ActionEvent evt) {

        Calendar tmpDate = GregorianCalendar.getInstance();
        
        tmpDate.setTimeInMillis( theDate.getTimeInMillis() );

        tmpDate.add( Calendar.DAY_OF_YEAR, 1 );

        goToDate( tmpDate.getTime() );

    }

    /**
     *  Event handler for when the "Previous" button is clicked
     *
     *@param  evt  The event object
     */
    public void butPreviousDayActionPerformed(java.awt.event.ActionEvent evt) {

        Calendar tmpDate = GregorianCalendar.getInstance();
        tmpDate.setTimeInMillis( theDate.getTimeInMillis() );

        tmpDate.add( Calendar.DAY_OF_YEAR, -1 );

        goToDate( tmpDate.getTime() );

    }

    /**
     * The event procedure for the vertical scrollpane listener - just calls the
     * scrollChannelNames method.
     *
     *@param  evt  The event object
     */
    public void programmesScrollPaneVerAdjust(java.awt.event.AdjustmentEvent evt) {
        
        scrollChannelNames();
        
    }

    //}}}
    
    //{{{ Reactions to events
    
    /**
     *  Scrolls the channel names to the same y-position as the main panel.
     */
    public void scrollChannelNames() {
        
        channelNameScrollPane.getVerticalScrollBar().setValue(
                programmesScrollPane.getVerticalScrollBar().getValue() );
                
    }
    
    /**
     *  Download listings XMLTV from the web
     */
    public void downloadListings() {
        
        Utils.execAndWait(this, FreeGuide.prefs.getCommands("tv_grab"),
            FreeGuide.msg.getString( "downloading" ), theDate);

        reShow();
            
    }
    
    /**
     * When a scoll event happens, repaint the main panel, to allow the text to
     * be adjusted to be visible even if the programme starts off to the left.
     */
    private void programScrolled( AdjustmentEvent e ) {
        programmesPanel.repaint();
    }
    
    /**
     *  Scroll to now on the time line, and update the screen if this involves
     *  changing the day.
     */
    public void goToNow() {

        // Remember what day we were one        
        int oldDay  = theDate.get( Calendar.DAY_OF_YEAR );
        int oldYear = theDate.get( Calendar.YEAR        );

        // Go to today
        findInitialDate();

        // I suspect, panel is not updated before scroll position is set
        // This seems to fix things in the case of a day change
        if (!(
            oldDay == theDate.get(Calendar.DAY_OF_YEAR) &&
            oldYear == theDate.get(Calendar.YEAR)
        )) {

            goToDate( theDate.getTime() );

        }

        scrollToNow();

    }
    
    /**
     *  The event listener for the form closing event - calls the quit method
     *
     *@param  evt  The event object
     */
    public void exitForm(java.awt.event.WindowEvent evt) {
        quit();
    }

    /**
     *  Quits the program.
     */
    public void quit() {

        // Save the window size and position etc.
        // --------------------------------------
        FreeGuide.prefs.screen.putInt("viewer_left", getX());
        FreeGuide.prefs.screen.putInt("viewer_top", getY());
        FreeGuide.prefs.screen.putInt("viewer_width", getWidth());
        FreeGuide.prefs.screen.putInt("viewer_height", getHeight());
        FreeGuide.prefs.screen.putInt("viewer_splitpane_vertical",
                splitPaneChanProg.getDividerLocation());
        FreeGuide.prefs.screen.putInt("viewer_splitpane_horizontal",
                splitPaneMainDet.getDividerLocation());
        FreeGuide.prefs.screen.putInt("viewer_splitpane_horizontal_bottom",
                splitPaneGuideDet.getDividerLocation());
        FreeGuide.prefs.screen.put( "viewer_channel_set",
                (String)(comChannelSet.getSelectedItem()) );

        // Delete old .xmltv files
        // ---------------------

        String fs = System.getProperty("file.separator");

        // Get the date last week
        Calendar lastWeek = GregorianCalendar.getInstance();
        lastWeek.add(Calendar.WEEK_OF_YEAR, -1);
        Calendar cal = GregorianCalendar.getInstance();

        File wd = new File(FreeGuide.prefs.performSubstitutions(
                FreeGuide.prefs.misc.get("working_directory")));

        if (wd.isDirectory()) {

            String[] files = wd.list();
            for (int i = 0; i < files.length; i++) {

                // If it's an xmltv file, we may want to delete it
                if (files[i].matches(".*-\\d{8}.xmltv")) {

                    int len = files[i].length();
                    String dateStr = files[i].substring(3, 11);

                    try {

                        cal.setTime(fileDateFormat.parse(dateStr));
                        
                        if (cal.before(lastWeek)) {

                            new File(wd + fs + files[i]).delete();

                        }
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            // for

        }
        // if

        // Delete old entries in programmes preferences
        Calendar[] choiceDates = FreeGuide.prefs.getAllInGuideDays();
        for (int i = 0; i < choiceDates.length; i++) {
            if (choiceDates[i].before(lastWeek)) {
                FreeGuide.prefs.somethingInGuide(choiceDates[i], false);
            }
        }

        // Exit
        // ----
        FreeGuide.log.info( FreeGuide.msg.getString( "ending_normally" ) );
        System.exit(0);

    }
    
    //}}}
    
    //}}}
    
    //{{{ Variable declarations
    
    //{{{ Constants
    
    public static final String CHANNEL_SET_ALL_CHANNELS =
        FreeGuide.msg.getString( "all_channels" );

    private static final String CHANNEL_SET_EDIT_SETS =
        FreeGuide.msg.getString( "edit_channels_sets" );
    
    //}}}
    
    //{{{ Other
    
    /**
     *  The class that handles all the XMLTV stuff
     */
    public static ViewerFrameXMLTVLoader xmltvLoader;
    
    /**
     *  The list of available channel sets (all are ChannelSet objects)
     */
    public ChannelSet[] channelSetsList;
    
    /**
     *  The set of channels being displayed
     */
    public ChannelSetInterface currentChannelSet;

    /**
     *  What date we are viewing
     */
    public Calendar theDate;

    /**
     *  true if user doesn't want to download missing files
     */
    //public boolean dontDownload;

    /**
     * The channel the user last right clicked on
     */
    public ChannelJLabel rightClickedChannel;

    /**
     *  A list of dates that have data worked out from the filenames in the data
     *  dirctory.
     */
    public DateFilesExistList dateFilesExistList;
    
    //}}}
        
    //{{{ Date formatters
    
    /**
     *  Date formatter
     */
    public final static SimpleDateFormat comboBoxDateFormat =
        new SimpleDateFormat("EEEE d MMM yy");

    /**
     *  Date formatter
     */
    public final static SimpleDateFormat htmlDateFormat =
        new SimpleDateFormat("EEEE dd MMMM yyyy");

    /**
     *  The chosen time formatter
     */
    public SimpleDateFormat timeFormat;

    /**
     *  Time formatter for 12 hour clock
     */
    public final static SimpleDateFormat timeFormat12Hour =
        new SimpleDateFormat("hh:mm aa");

    /**
     *  Time formatter for 24 hour clock
     */
    public final static SimpleDateFormat timeFormat24Hour =
        new SimpleDateFormat("HH:mm");

    /**
     *  How to format dates that go in filenames
     */
    public final static SimpleDateFormat fileDateFormat =
        new SimpleDateFormat("yyyyMMdd");
             
    //}}}

    //{{{ Dynamic GUI    
    
    /**
     *  The action listener for when the item changes in the date combo
     */
    public ItemListener comTheDateItemListener;
    
    /**
     *  The action listener for when the item changes in the channelset combo
     */
    public ItemListener comChannelSetItemListener;
    
    /**
     * The listener for when a scroll event happens
     */
    private AdjustmentListener comProgramScrollListener;
    
    /**
     *  Combobox containing the date we are viewing
     */
    public javax.swing.JComboBox comTheDate;
    
    /**
     *  The combobox showing the channel set we are using
     */
    public javax.swing.JComboBox comChannelSet;
    
    /**
     *  The panel showing the timeline
     */
    public TimePanel timePanel;
    
    /**
     * The side panel showing programme details
     */
    public ProgrammeDetailsJPanel detailsPanel;
    
    private javax.swing.JMenuItem mbtPrint;

    /**
     *  The panel containing the channel names
     */
    public javax.swing.JPanel channelNamePanel;
    
    /**
     *  The scrollpane that contains the names of channels
     */
    public javax.swing.JScrollPane channelNameScrollPane;
    
    /**
     *  The panel containing the programmes
     */
    public StripView programmesPanel;

    /**
     *  The Scrollpane showing programmes
     */
    public javax.swing.JScrollPane programmesScrollPane;

    /**
     * The popup menu when a channel label is right-clicked
     */
    public javax.swing.JPopupMenu popMenuChannel;
    
    //}}}
    
    //{{{ Static GUI
    
    /**
     *  The JEditorPane where the printedGuide is shown
     */
    public ViewerFrameHTMLGuide printedGuideArea;
    
    private javax.swing.JProgressBar progressBar;
        
    private javax.swing.JMenuItem mbtDownload;
    private javax.swing.JMenuItem mbtUserGuide;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator1;

    /**
     * The menu item to change the icon
     */
    public javax.swing.JMenuItem mbtChangeIcon;
    /**
     * The menu item to reset to the default icon
     */
    public javax.swing.JMenuItem mbtResetIcon;
    private javax.swing.JPanel topButtonsPanel;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butDownload;
    private javax.swing.JMenuItem mbtFirstTime;
    private javax.swing.JMenuItem mbtOptions;

    /**
     * The splitpane splitting the main panel from the printed guide and
     * programme details
     */
    public javax.swing.JSplitPane splitPaneMainDet;
    
    /**
     *  The splitpane splitting the printed guide from programme details
     */
    public javax.swing.JSplitPane splitPaneGuideDet;
    
    private javax.swing.JMenuItem mbtFavourites;
    private javax.swing.JMenuItem mbtChannelSets;
    private javax.swing.JButton butRevertToFavourites;
    private javax.swing.JMenu toolsMenu;
    /**
     *  The splitpane splitting the channels from programmes
     */
    public javax.swing.JSplitPane splitPaneChanProg;
    private javax.swing.JMenuItem mbtAbout;
    private javax.swing.JButton butNextDay;
    private javax.swing.JButton butPreviousDay;
    private javax.swing.JButton butGoToNow;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenuItem mbtConfigure;
    private javax.swing.JScrollPane printedGuideScrollPane;
    private javax.swing.JMenuItem mbtQuit;
    private javax.swing.JMenu helpMenu;

    private Progressor progressor;
    
    /**
     * The listener that notifies us when a favourite has been changed
     */
    FGPreferenceChangeListener favouritesChangeListener;
    
    //}}}

}


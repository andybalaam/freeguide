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

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

//}}}

/**
 *  The form that displays the listings information. Now contains only the GUI
 *  code with everything else moved out.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    20
 */
public class ViewerFrame extends javax.swing.JFrame implements Progressor {

    //{{{ Constructor
	/**
     *  Constructor for the FreeGuideViewer object
     *
     *@param  newLauncher  What screen launched this screen
     *@param  pleaseWait   The window saying "Please Wait"
     */
    public ViewerFrame(PleaseWaitFrame pleaseWait) {

		// Set up basic variables
		progressor = pleaseWait;
		xmltvLoader 	= new ViewerFrameXMLTVLoader();
		
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

		// Get rid of the "Please Wait" window if it is visible
        if (pleaseWait != null) {
            pleaseWait.dispose();
			progressor = this;
        }

		// Show the screen
		setVisible( true );

		// Check the FreeGuide version
		if( !FreeGuide.prefs.misc.get( "privacy", "no" ).equals( "no" ) ) {
			
			// Run the check in a separate thread to avoid blocking.
			new VersionCheckerThread( this ).start();
			
		}
		
        //Scroll to the correct time
		scrollToNow();
		scrollToNow();

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
			oa[0]="There are missing listings for today:";
			oa[1]="Do you want to download more?";
			int r = JOptionPane.showConfirmDialog(this, oa,
				"Download listings?", JOptionPane.YES_NO_OPTION );
				
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
                "^tv-.*\\.xmltv$" );
		
	}

	/**
	 * Find out today's date
	 */
	private void findInitialDate() {
		
		// Set the date to today
        theDate = GregorianCalendar.getInstance();
        Time nowTime = new Time(theDate);
        Time day_start_time = FreeGuide.prefs.misc.getTime(
                "day_start_time");

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
		
        popMenuProgramme = new javax.swing.JPopupMenu();
        mbtAddFavourite = new javax.swing.JMenuItem();
		mbtGoToWebSite = new javax.swing.JMenuItem();
        topButtonsPanel = new javax.swing.JPanel();
        butGoToNow = new javax.swing.JButton();
        butPreviousDay = new javax.swing.JButton();
        comTheDate = new javax.swing.JComboBox();
        comChannelSet = new javax.swing.JComboBox();
        butNextDay = new javax.swing.JButton();
        horizontalSplitPane = new javax.swing.JSplitPane();
        printedGuideScrollPane = new javax.swing.JScrollPane();
        printedGuideArea = new ViewerFrameHTMLGuide( this );
        verticalSplitPane = new javax.swing.JSplitPane();
        channelNameScrollPane = new javax.swing.JScrollPane();
        channelNamePanel = new javax.swing.JPanel();
        programmesScrollPane = new javax.swing.JScrollPane();
        programmesPanel = new InnerPanel();
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
        helpMenu = new javax.swing.JMenu();
        mbtUserGuide = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        mbtAbout = new javax.swing.JMenuItem();

		//}}}
		
		//{{{ popMenuProgramme
		
        popMenuProgramme.addPopupMenuListener(
            new javax.swing.event.PopupMenuListener() {
                public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                    popMenuProgrammePopupMenuWillBecomeVisible(evt);
                }


                public void popupMenuWillBecomeInvisible(
                        javax.swing.event.PopupMenuEvent evt) { }


                public void popupMenuCanceled(
                        javax.swing.event.PopupMenuEvent evt) { }
            });

        mbtAddFavourite.setText("Add to favourites");
        mbtAddFavourite.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtAddFavouriteActionPerformed(evt);
                }
            });

        popMenuProgramme.add( mbtAddFavourite );

		mbtGoToWebSite.setText("Go to web site");
		mbtGoToWebSite.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtGoToWebSiteActionPerformed(evt);
                }
            });
		
		
		//}}}

		//{{{ Main Window

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle( "FreeGuide " + FreeGuide.getVersion() );
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });
		
        pack();
        java.awt.Dimension screenSize
                 = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        // Load the window size and position etc.
        // --------------------------------------
        setSize(
                FreeGuide.prefs.screen.getInt("viewer_width", 640),
                FreeGuide.prefs.screen.getInt("viewer_height", 400));

        setLocation(
                FreeGuide.prefs.screen.getInt("viewer_left", (
                screenSize.width - 640) / 2),
                FreeGuide.prefs.screen.getInt("viewer_top", (
                screenSize.height - 400) / 2));

        verticalSplitPane.setDividerLocation(FreeGuide.prefs.screen.getInt(
                "viewer_splitpane_vertical", 100));
        horizontalSplitPane.setDividerLocation(FreeGuide.prefs.screen.getInt(
                "viewer_splitpane_horizontal", 150));

		//}}}

		//{{{ topButtonsPanel
		
        topButtonsPanel.setLayout(new java.awt.GridBagLayout());

        butGoToNow.setFont(new java.awt.Font("Dialog", 0, 10));
        butGoToNow.setText("Go To Now");
		butGoToNow.setMnemonic( KeyEvent.VK_N );
        butGoToNow.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butGoToNowActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        topButtonsPanel.add(butGoToNow, gridBagConstraints);

        butPreviousDay.setText("-");
		butPreviousDay.setMnemonic( KeyEvent.VK_MINUS );
        butPreviousDay.addActionListener(
            new java.awt.event.ActionListener() {
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

        butNextDay.setText("+");
		butNextDay.setMnemonic( KeyEvent.VK_EQUALS );
        butNextDay.addActionListener(
            new java.awt.event.ActionListener() {
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
        butDownload.setText("Download Listings");
		butDownload.setMnemonic( KeyEvent.VK_D );
        butDownload.addActionListener(
            new java.awt.event.ActionListener() {
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
		
        horizontalSplitPane.setOneTouchExpandable(true);
        horizontalSplitPane.setOrientation(
			javax.swing.JSplitPane.VERTICAL_SPLIT );
		
        printedGuideArea.setEditable(false);
        printedGuideArea.setContentType("text/html");
        printedGuideScrollPane.setViewportView(printedGuideArea);

        horizontalSplitPane.setRightComponent(printedGuideScrollPane);

        channelNameScrollPane.setBorder(null);
        channelNameScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        channelNameScrollPane.setMinimumSize(new java.awt.Dimension(100, 100));
        channelNameScrollPane.setPreferredSize(new java.awt.Dimension(100, 100));
        channelNamePanel.setLayout(null);

        channelNamePanel.setBackground(new java.awt.Color(245, 245, 255));

        javax.swing.JPanel tmpJPanel = new javax.swing.JPanel();
        tmpJPanel.setPreferredSize(new java.awt.Dimension(24, 24));
        tmpJPanel.setBackground(new java.awt.Color(245, 245, 255));
        channelNameScrollPane.setColumnHeaderView(tmpJPanel);

        channelNameScrollPane.setViewportView(channelNamePanel);

        verticalSplitPane.setLeftComponent(channelNameScrollPane);
        
        programmesScrollPane.setBorder(null);
        programmesScrollPane.setColumnHeaderView(timePanel);
        programmesPanel.setLayout(null);

        programmesPanel.setBackground(new java.awt.Color(245, 245, 255));
        programmesScrollPane.setViewportView(programmesPanel);

		timePanel.setPreferredSize(new java.awt.Dimension(24, 24));
        timePanel.setLayout(null);
        timePanel.setBackground(new java.awt.Color(245, 245, 255));

        verticalSplitPane.setRightComponent(programmesScrollPane);

        horizontalSplitPane.setLeftComponent(verticalSplitPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(horizontalSplitPane, gridBagConstraints);

		/*gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        getContentPane().add(progressBar, gridBagConstraints);*/
		
		//}}}
		
		//{{{ Bottom buttons
		
        butRevertToFavourites.setFont(new java.awt.Font("Dialog", 0, 10));
        butRevertToFavourites.setText("Reset choices");
		butRevertToFavourites.setMnemonic( KeyEvent.VK_R );
        butRevertToFavourites.addActionListener(
            new java.awt.event.ActionListener() {
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
        butPrint.setText("Print this personalised listing");
		butPrint.setMnemonic( KeyEvent.VK_P );
        butPrint.addActionListener(
            new java.awt.event.ActionListener() {
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
        mbtDownload.setText("Download Listings");
		mbtDownload.setMnemonic(KeyEvent.VK_D);
		mbtDownload.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D,
			InputEvent.CTRL_MASK ) );
        mbtDownload.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtDownloadActionPerformed(evt);
                }
            });

        fileMenu.add(mbtDownload);

        mbtPrint.setText("Print this personalised listing");
		mbtPrint.setMnemonic(KeyEvent.VK_P);
		mbtPrint.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P,
			InputEvent.CTRL_MASK ) );
        mbtPrint.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtPrintActionPerformed(evt);
                }
            });

        fileMenu.add(mbtPrint);

        fileMenu.add(jSeparator5);

        mbtQuit.setText("Quit");
		mbtQuit.setMnemonic(KeyEvent.VK_Q);
		mbtQuit.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q,
			InputEvent.CTRL_MASK ) );
        mbtQuit.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtQuitActionPerformed(evt);
                }
            });

        fileMenu.add(mbtQuit);

        mainMenuBar.add(fileMenu);

        toolsMenu.setText("Tools");
		toolsMenu.setMnemonic(KeyEvent.VK_T);

        mbtConfigure.setText("Configure Grabber...");
		mbtConfigure.setMnemonic(KeyEvent.VK_C);
		mbtConfigure.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C,
			InputEvent.CTRL_MASK ) );
        mbtConfigure.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtConfigureActionPerformed(evt);
                }
            });
		//mbtConfigure.setEnabled(false);

        toolsMenu.add(mbtConfigure);

        mbtFavourites.setText("Favourites...");
		mbtFavourites.setMnemonic(KeyEvent.VK_F);
		mbtFavourites.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F,
			InputEvent.CTRL_MASK ) );
        mbtFavourites.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtFavouritesActionPerformed(evt);
                }
            });

        toolsMenu.add(mbtFavourites);

        mbtChannelSets.setText("Channel Sets...");
		mbtChannelSets.setMnemonic(KeyEvent.VK_H);
		mbtChannelSets.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H,
			InputEvent.CTRL_MASK ) );
        mbtChannelSets.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtChannelSetsActionPerformed(evt);
                }
            });

        toolsMenu.add(mbtChannelSets);
        toolsMenu.add(jSeparator1);

        mbtOptions.setText("Options...");
		mbtOptions.setMnemonic(KeyEvent.VK_O);
		mbtOptions.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O,
			InputEvent.CTRL_MASK ) );
        mbtOptions.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mbtOptionsActionPerformed(evt);
                }
            });

        toolsMenu.add(mbtOptions);

        mainMenuBar.add(toolsMenu);

        helpMenu.setText("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
        mbtUserGuide.setText("User Guide...");
		mbtUserGuide.setMnemonic(KeyEvent.VK_U);
        mbtUserGuide.setEnabled(false);
        helpMenu.add(mbtUserGuide);

        helpMenu.add(jSeparator4);

        mbtAbout.setText("About...");
		mbtAbout.setMnemonic(KeyEvent.VK_A);
		mbtAbout.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A,
			InputEvent.CTRL_MASK ) );
        mbtAbout.addActionListener(
            new java.awt.event.ActionListener() {
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

		//}}}
		
    }

	//}}}

	//{{{ (re)-Initialisation methods
	
	/**
	 * Find the list of channel sets available
	 */
	private void findChannelSets() {
		
		// Find out what channel sets exist, from the preferences
		channelSetsList = FreeGuide.prefs.getChannelSets();
		
	}
	
	/**
	 * Find the dates avialable from an already-set-up datelister
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
				comboBoxDateFormat.format(
					dateFilesExistList.get(i) ), i );
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
		
		programmesScrollPane.getHorizontalScrollBar().
                setValue( timePanel.getNowScroll() - 100 );
		
	}
	
	void scrollTo(Calendar showTime) {
		
		programmesScrollPane.getHorizontalScrollBar().
                setValue( timePanel.getScrollValue(showTime) );
		
	}

        void scrollToReference(String reference) {
          printedGuideArea.scrollToReference(reference);
        }

	//}}}
	
	//{{{ drawProgrammes
	/**
	 * Draw all the programmes and channels on screen
	 */
	private void drawProgrammes() {
		
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

		tickedColour = FreeGuide.prefs.screen.getColor(
			"programme_chosen_colour", FreeGuide.PROGRAMME_CHOSEN_COLOUR );
		movieColour = FreeGuide.prefs.screen.getColor(
			"programme_movie_colour", FreeGuide.PROGRAMME_MOVIE_COLOUR );
			
		nonTickedColour = FreeGuide.prefs.screen.getColor(
			"programme_normal_colour", FreeGuide.PROGRAMME_NORMAL_COLOUR );
		heartColour = FreeGuide.prefs.screen.getColor(
			"programme_heart_colour", FreeGuide.PROGRAMME_HEART_COLOUR );

		Color channelColour = FreeGuide.prefs.screen.getColor(
                "channel_colour", FreeGuide.CHANNEL_COLOUR);
		
        String fontName = FreeGuide.prefs.screen.get("font_name", "Dialog");
        int fontStyle = FreeGuide.prefs.screen.getInt("font_style", Font.PLAIN);
        int fontSize = FreeGuide.prefs.screen.getInt("font_size", 12);

        boolean drawTime = FreeGuide.prefs.screen.getBoolean(
                "display_programme_time", true);
	
        boolean draw24time = FreeGuide.prefs.screen.getBoolean(
                "display_24hour_time", true);
		
        timeFormat = (
            draw24time ? timeFormat24Hour :
				timeFormat12Hour );
		
        Font channelFont = new Font(fontName, Font.BOLD, fontSize);
        Font font = new Font(fontName, fontStyle, fontSize);

        int channelPanelWidth = FreeGuide.prefs.screen.getInt(
                "channel_panel_width", FreeGuide.CHANNEL_PANEL_WIDTH);

		// Temporal width in millisecs
        long temporalWidth = xmltvLoader.latest.getTimeInMillis() -
                xmltvLoader.earliest.getTimeInMillis();
		
		// Find the multiplier to help us position programmes
        double widthMultiplier = (double) panelWidth / (double) temporalWidth;
		
		//}}}
		
		//{{{ Draw the channels
		
		// Delete the old ones
		channelNamePanel.removeAll();

		// Resize the area
        channelNamePanel.setPreferredSize(
            new Dimension(
            	channelPanelWidth,
                currentChannelSet.getNoChannels()
					* channelHeight + 50 ) );
					
		int num_chans = currentChannelSet.getNoChannels();
					
		// Create all the JLabels for channels, and set them up
        for (int c = 0; c < num_chans; c++) {

			progressor.setProgress( 10 + (c*10) / num_chans );
			
            JLabel ctxt = new JLabel(
				currentChannelSet.getChannelName(c) );

            ctxt.setBounds(
				0,
				(halfVerGap * 2) + (c * channelHeight) - 1,
				channelNamePanel.getPreferredSize().width - 1,
				channelHeight - (halfVerGap * 4) );

            ctxt.setBackground( channelColour );
            ctxt.setFont( font );
            ctxt.setBorder( new javax.swing.border.LineBorder(
				java.awt.Color.black ) );
            ctxt.setHorizontalAlignment( JLabel.LEFT );
            ctxt.setOpaque( true );

            channelNamePanel.add( ctxt );

        }
		
		//}}}
		
		//{{{ Draw the programmes
		
        // Delete all the old programmes
        programmesPanel.removeAll();

		// Set up the programme and time panels
        int tmpH = currentChannelSet.getNoChannels()
			* channelHeight;
        programmesPanel.setPreferredSize(
			new java.awt.Dimension( panelWidth, tmpH ) );
        programmesPanel.setMinimumSize(
			new java.awt.Dimension( panelWidth, tmpH ) );
        programmesPanel.setMaximumSize(
			new java.awt.Dimension( panelWidth, tmpH ) );

        tmpH = timePanel.getPreferredSize().height;
        timePanel.setPreferredSize(
			new java.awt.Dimension( panelWidth, tmpH ) );
        timePanel.setMinimumSize(
			new java.awt.Dimension( panelWidth, tmpH ) );
        timePanel.setMaximumSize(
			new java.awt.Dimension( panelWidth, tmpH ) );        

		// All the programmeJLabels containing programmes
        programmeJLabels = new Vector();

		// Draw the programmes
		Vector choices = FreeGuide.prefs.getChosenProgs( theDate );
		int num_progs = xmltvLoader.programmes.size();
		
        for (int p = 0; p < num_progs; p++) {
			
			progressor.setProgress( 20 + (p*80) / num_progs );
			
            Programme prog =
                    (Programme)xmltvLoader.programmes.get(p);

			ProgrammeJLabel programmeJLabel = new ProgrammeJLabel( prog,
				timeFormat, drawTime,
				halfHorGap, widthMultiplier, halfVerGap,
				channelHeight, font, this, choices );
					
            programmeJLabels.add( programmeJLabel );

            programmesPanel.add( programmeJLabel );

        }

        timePanel.setTimes(
			xmltvLoader.earliest, xmltvLoader.latest );

		//}}}
				
		//{{{ Repaint everything
		
        timePanel.revalidate();
        timePanel.repaint();

        programmesPanel.revalidate();
        programmesPanel.repaint();

        channelNamePanel.revalidate();
        channelNamePanel.repaint();

		//}}}
		
	}
	//}}}
	
	//{{{ Utilities
	
	/**
	 * Check that the chosen channel set exists, and if not, sets it to
	 * "- All Channels -".
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
			+ " not matched!" );
			
		currentChannelSet = xmltvLoader;
		
	}
	
	/**
	 * Change the date combo to the given date.  Will trigger an event causing
	 * a repaint of all the programmes.
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
            if ( channelSetsList[i].getChannelSetName().equals(
					channelSetName ) ) {

				// If so, return this one.
				return channelSetsList[i];
				
            }
        }
		
		FreeGuide.log.info( "The name of the channel set didn't match any "
			+ "known set." );
		
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
		printedGuideArea.update();
		
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
     *  Event handler when the popup menu is going to be displayed
     *
     *@param  evt  The event object
     */
    public void popMenuProgrammePopupMenuWillBecomeVisible(
			javax.swing.event.PopupMenuEvent evt ) {

        if( rightClickedProg.isFavourite ) {
			
            mbtAddFavourite.setText("Remove from favourites");
			
        } else {
			
            mbtAddFavourite.setText("Add to favourites");
			
        }

		int popMenuProgrammeSize = popMenuProgramme.getSubElements().length;
		URL link = rightClickedProg.programme.getLink();
		
		if( link != null && popMenuProgrammeSize < 2 ) {
			popMenuProgramme.add( mbtGoToWebSite );
		}
		
		if(link == null && popMenuProgrammeSize > 1) {
			popMenuProgramme.remove( popMenuProgrammeSize-1 );
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

		Utils.execAndWait( this,
			FreeGuide.prefs.getCommands("tv_config"),
				"Configuring", theDate );

    }


    /**
     *  Event handler for when the Go to web site popup menu item is chosen
     *
     *@param  evt  The event object
     */
    public void mbtGoToWebSiteActionPerformed(java.awt.event.ActionEvent evt) {

		ProgrammeJLabel programmeJLabel = rightClickedProg;
		Programme programme = programmeJLabel.programme;
		
		String[] cmds = Utils.substitute(
			FreeGuide.prefs.commandline.getStrings( "browser_command" ),
			"%filename%",
			programme.getLink().toString() );
			
            Utils.execNoWait(cmds);

    }
	
    /**
     *  Event handler for when the Add to Favourites popup menu item is chosen
     *
     *@param  evt  The event object
     */
    public void mbtAddFavouriteActionPerformed(java.awt.event.ActionEvent evt) {

		ProgrammeJLabel programmeJLabel = rightClickedProg;
		Programme programme = programmeJLabel.programme;
		

        if( programmeJLabel.isFavourite ) {
			// Remove from favourites
			
			Favourite[] favourites = FreeGuide.prefs.getFavourites();
			
            // Find out which favourite the programme matches
            for (int i = 0; i < favourites.length; i++) {

                if ( favourites[i].matches( programme ) ) {

                    int r = JOptionPane.showConfirmDialog( this,
						"Remove favourite \""
						+ favourites[i].getName() + "\"?",
						"Remove favourite?", JOptionPane.YES_NO_OPTION );

                    if (r == 0) {

                        FreeGuide.prefs.favourites.removeFavourite(	i + 1 );

						programmeJLabel.isFavourite = false;
						programmeJLabel.setSelected( false );
							
                        // Update the guide
                        printedGuideArea.update();

                    }
                }
            }
        } else {
			// Add to favourites
		
            Favourite fav = new Favourite();
			
            String title = programme.getTitle();
			
            fav.setTitleString( title );
            fav.setName( title );

            // Remember the favourite
            FreeGuide.prefs.favourites.appendFavourite( fav );

			programmeJLabel.isFavourite = true;
			programmeJLabel.setSelected( true );
			
        }

    }

    /**
     *  Event handler for when the Reset button is pressed 
     *
     *@param  evt  The event object
     */
    public void butRevertToFavouritesActionPerformed(java.awt.event.ActionEvent evt) {
		
        // Tell the prefs we've got no choices for today
        FreeGuide.prefs.chosenSomething( theDate, false );

		Vector choices = null;
		
		for( int i=0; i<programmeJLabels.size(); i++ ) {
			
			( (ProgrammeJLabel)programmeJLabels.get(i) )
				.findOutSelectedness( choices );
			
		}
		
        printedGuideArea.update();

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
						channelSetsList[i].getChannelSetName() ) ) {
				
					currentChannelSet = channelSetsList[i];
					break;
				
				}
			
			}
		
			// If we didn't find one, set it to the default
			if( i == channelSetsList.length ) {
			
				FreeGuide.log.info( "Channel set name not found: "
					+ channelSetString );
			
				currentChannelSet = xmltvLoader;
			
			}
			
		}
			
        // Refresh the programmes
		drawProgrammes();
		
		// Refresh the printed guide
		printedGuideArea.update();
		
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
     *  Event handler for when the "About" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtAboutActionPerformed(java.awt.event.ActionEvent evt) {

        new AboutFrame( this, true ).setVisible( true );

    }


    /**
     *  Event handler for when the "User Guide" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtUserGuideActionPerformed(java.awt.event.ActionEvent evt) {

		// FIXME: Does nothing at the moment
		
    }



    /**
     *  Event handler for when the "Options" menu option is chosen 
     *
     *@param  evt  The event object
     */
    public void mbtOptionsActionPerformed(java.awt.event.ActionEvent evt) {

		boolean updated = centreDialogAndRun( new OptionsDialog( this ) );

		if( updated ) {
			reShow();
		}
		
    }

	private boolean centreDialogAndRun( FGDialog dialog ) {
		
		Dimension thisSize = getSize();
		Dimension dialogSize = dialog.getSize();
		Point thisLocation = getLocation();
		
		dialog.setLocation(
			thisLocation.x + ( ( thisSize.width  - dialogSize.width  ) / 2 ),
			thisLocation.y + ( ( thisSize.height - dialogSize.height ) / 2 ) );
		
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
    
    	boolean updated = centreDialogAndRun( new ChannelSetListDialog(this,
			xmltvLoader) );
         
    	if (updated) {
			
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

    	boolean updated = centreDialogAndRun( new FavouritesListDialog(this) );
		    	
    	if (updated) {
        	drawProgrammes();
        }

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
     *  The event procedure for a ProgrammeJLabel when it is clicked.
     *
     *@param  evt  The event object
     */
    public void programmeJLabelClicked( java.awt.event.MouseEvent evt ) {

        ProgrammeJLabel programmeJLabel = (ProgrammeJLabel)evt.getSource();
			
		programmeJLabel.setSelected( !programmeJLabel.isSelected );

        printedGuideArea.update();

    }

    /**
     *  The event procedure for the vertical scrollpane listener - just calls
     *  the scrollChannelNames method.
     *
     *@param  evt  The event object
     */
    public void programmesScrollPaneVerAdjust(
			java.awt.event.AdjustmentEvent evt) {
		
        scrollChannelNames();
		
    }

	//}}}
	
	//{{{ Reactions to events
	
	/**
     *  Scrolls the channel names to the same y-position as the main panel.
     */
    public void scrollChannelNames() {
		
		//FreeGuide.log.info( "begin" );
		
        channelNameScrollPane.getVerticalScrollBar()
			.setValue(
				programmesScrollPane.getVerticalScrollBar().getValue() );
    }
	
	/**
     *  Download listings XMLTV from the web
     */
    public void downloadListings() {
		
		//setVisible( false );
        Utils.execAndWait( this,
			FreeGuide.prefs.getCommands("tv_grab"),
			"Downloading",
			theDate );

		reShow();
			
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
        if ( !( oldDay == theDate.get( Calendar.DAY_OF_YEAR ) &&
                oldYear == theDate.get( Calendar.YEAR ) ) ) {

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
                verticalSplitPane.getDividerLocation());
        FreeGuide.prefs.screen.putInt("viewer_splitpane_horizontal",
                horizontalSplitPane.getDividerLocation());
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
                if (files[i].matches("tv-\\d{8}.xmltv")) {

                    int len = files[i].length();
                    String dateStr = files[i].substring(3, 11);

                    try {

                        cal.setTime(
							fileDateFormat.parse(dateStr) );
						
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

        // Delete old entries in choices preferences
        Calendar[] choiceDates = FreeGuide.prefs.getAllChosenDays();
        for (int i = 0; i < choiceDates.length; i++) {
            if (choiceDates[i].before(lastWeek)) {
                FreeGuide.prefs.chosenSomething(choiceDates[i], false);
            }
        }

        // Exit
        // ----
        FreeGuide.log.info("FreeGuide - Ending normally.");
        System.exit(0);

    }
	
	//}}}
	
	//}}}
	
	//{{{ Variable declarations
	
	//{{{ Constants
	
	public static final String CHANNEL_SET_ALL_CHANNELS = "- All Channels -";
	private static final String CHANNEL_SET_EDIT_SETS = "Edit channels sets...";
	
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
     *  Stores references to all the ProgrammeJLabels shown
     */
    public Vector programmeJLabels;
	
	/**
     *  true if user doesn't want to download missing files
     */
    //public boolean dontDownload;

    /**
     *  The programme the user last right clicked on
     */
    public ProgrammeJLabel rightClickedProg;

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
    public final static SimpleDateFormat comboBoxDateFormat
             = new SimpleDateFormat("EEEE d MMM yy");
    /**
     *  Date formatter
     */
    public final static SimpleDateFormat htmlDateFormat
             = new SimpleDateFormat("EEEE dd MMMM yyyy");

    /**
     *  The chosen time formatter
     */
    public SimpleDateFormat timeFormat;
    /**
     *  Time formatter for 12 hour clock
     */
    public final static SimpleDateFormat timeFormat12Hour
             = new SimpleDateFormat("hh:mm aa");
    /**
     *  Time formatter for 24 hour clock
     */
    public final static SimpleDateFormat timeFormat24Hour
             = new SimpleDateFormat("HH:mm");
    /**
     *  How to format dates that go in filenames
     */
    public final static SimpleDateFormat fileDateFormat
             = new SimpleDateFormat("yyyyMMdd");

	//}}}

	//{{{ Colours
	
    /**
     *  The colour of the hearts that indicate favourites
     */
    public Color heartColour;
    /**
     *  Description of the Field
     */
    public Color tickedColour;
    /**
     *  Description of the Field
     */
    public Color movieColour;
    /**
     *  Description of the Field
     */
    public Color nonTickedColour;
	
	//}}}
	
	//{{{ Dynamic GUI
	
	
	/**
     *  The action listener for when the item changes in the channelset combo
     */
    public ItemListener comTheDateItemListener;
	
	/**
     *  The action listener for when the item changes in the channelset combo
     */
    public ItemListener comChannelSetItemListener;
	
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
    public InnerPanel programmesPanel;
    /**
     *  The Scrollpane showing programmes
     */
    public javax.swing.JScrollPane programmesScrollPane;
    /**
     *  The popup menu when you right-click a programme
     */
    public javax.swing.JPopupMenu popMenuProgramme;
	
	//}}}
	
	//{{{ Static GUI
	
	/**
     *  The JEditorPane where the printedGuide is shown
     */
    private ViewerFrameHTMLGuide printedGuideArea;
	
	private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem mbtDownload;
    private javax.swing.JMenuItem mbtUserGuide;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator1;
    /**
     *  The menu item to add a favourite
     */
    public javax.swing.JMenuItem mbtAddFavourite;
	/**
     *  The menu item to view a link
     */
	public javax.swing.JMenuItem mbtGoToWebSite;
    private javax.swing.JPanel topButtonsPanel;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butDownload;
    private javax.swing.JMenuItem mbtOptions;
    /**
     *  The splitpane splitting the inner panel from the printed guide
     */
    public javax.swing.JSplitPane horizontalSplitPane;
    private javax.swing.JMenuItem mbtFavourites;
    private javax.swing.JMenuItem mbtChannelSets;
    private javax.swing.JButton butRevertToFavourites;
    private javax.swing.JMenu toolsMenu;
    /**
     *  The splitpane splitting the channels from programmes?
     */
    public javax.swing.JSplitPane verticalSplitPane;
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
	
	//}}}

	//}}}
	
}


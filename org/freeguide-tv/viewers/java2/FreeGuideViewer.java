/*
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A form that displays and prints TV listings.
 *
 * @author  Andy Balaam
 * @version 3
 */
public class FreeGuideViewer extends javax.swing.JFrame {

    public FreeGuideViewer() {
		
		// Set up UI
        initComponents();
		
		// Set the date to today
		theDate = new Date();
		
		// Set up custom UI elements
		initMyComponents();
		
		// Draw the programmes on the screen
        updatePanel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
	private void initComponents() {//GEN-BEGIN:initComponents
		menuBar = new javax.swing.JMenuBar();
		jMenuBar2 = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		menPrint = new javax.swing.JMenuItem();
		jSeparator5 = new javax.swing.JSeparator();
		menQuit = new javax.swing.JMenuItem();
		toolsMenu = new javax.swing.JMenu();
		menDownload = new javax.swing.JMenuItem();
		jSeparator3 = new javax.swing.JSeparator();
		menFavourites = new javax.swing.JMenuItem();
		menOptions = new javax.swing.JMenuItem();
		helpMenu = new javax.swing.JMenu();
		menUserGuide = new javax.swing.JMenuItem();
		jSeparator4 = new javax.swing.JSeparator();
		menAbout = new javax.swing.JMenuItem();
		topPanel = new javax.swing.JPanel();
		butGoToNow = new javax.swing.JButton();
		butPrev = new javax.swing.JButton();
		comTheDate = new javax.swing.JComboBox();
		butNext = new javax.swing.JButton();
		butPrint = new javax.swing.JButton();
		splitPane = new javax.swing.JSplitPane();
		outerPanel = new javax.swing.JPanel();
		innerScrollPane = new javax.swing.JScrollPane();
		innerPanel = new javax.swing.JPanel();
		channelNameScrollPane = new javax.swing.JScrollPane();
		channelNamePanel = new javax.swing.JPanel();
		timeScrollPane = new javax.swing.JScrollPane();
		timePanel = new FreeGuideTimePanel();
		printedGuideScrollPane = new javax.swing.JScrollPane();
		printedGuideArea = new javax.swing.JTextArea();
		labStatus = new javax.swing.JLabel();
		progressBar = new javax.swing.JProgressBar();
		
		fileMenu.setText("File");
		menPrint.setText("Print Listing");
		fileMenu.add(menPrint);
		fileMenu.add(jSeparator5);
		menQuit.setText("Quit");
		menQuit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menQuitActionPerformed(evt);
			}
		});
		
		fileMenu.add(menQuit);
		jMenuBar2.add(fileMenu);
		toolsMenu.setText("Tools");
		menDownload.setText("Download Listings...");
		menDownload.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menDownloadActionPerformed(evt);
			}
		});
		
		toolsMenu.add(menDownload);
		toolsMenu.add(jSeparator3);
		menFavourites.setText("Favourites...");
		toolsMenu.add(menFavourites);
		menOptions.setText("Options...");
		menOptions.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menOptionsActionPerformed(evt);
			}
		});
		
		toolsMenu.add(menOptions);
		jMenuBar2.add(toolsMenu);
		helpMenu.setText("Help");
		menUserGuide.setText("User Guide...");
		helpMenu.add(menUserGuide);
		helpMenu.add(jSeparator4);
		menAbout.setText("About...");
		helpMenu.add(menAbout);
		jMenuBar2.add(helpMenu);
		
		getContentPane().setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints gridBagConstraints1;
		
		setTitle("FreeGuide J2 0.1");
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				exitForm(evt);
			}
		});
		
		butGoToNow.setText("Go To Now");
		butGoToNow.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				butGoToNowActionPerformed(evt);
			}
		});
		
		topPanel.add(butGoToNow);
		
		butPrev.setText("-");
		butPrev.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				butPrevActionPerformed(evt);
			}
		});
		
		topPanel.add(butPrev);
		
		comTheDate.setEditable(true);
		comTheDate.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				comTheDateActionPerformed(evt);
			}
		});
		
		topPanel.add(comTheDate);
		
		butNext.setText("+");
		butNext.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				butNextActionPerformed(evt);
			}
		});
		
		topPanel.add(butNext);
		
		butPrint.setText("Print Listing");
		butPrint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				butPrintActionPerformed(evt);
			}
		});
		
		topPanel.add(butPrint);
		
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 0, 2);
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
		getContentPane().add(topPanel, gridBagConstraints1);
		
		splitPane.setDividerLocation(250);
		splitPane.setDividerSize(5);
		splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		outerPanel.setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints gridBagConstraints2;
		
		outerPanel.setBackground(new java.awt.Color(245, 245, 255));
		innerScrollPane.setBorder(null);
		innerPanel.setLayout(null);
		
		innerPanel.setBackground(new java.awt.Color(245, 245, 255));
		innerPanel.setPreferredSize(new java.awt.Dimension(0, 0));
		innerPanel.setMinimumSize(new java.awt.Dimension(0, 0));
		innerPanel.setMaximumSize(new java.awt.Dimension(0, 0));
		innerScrollPane.setViewportView(innerPanel);
		
		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.weightx = 0.9;
		gridBagConstraints2.weighty = 0.9;
		outerPanel.add(innerScrollPane, gridBagConstraints2);
		
		channelNameScrollPane.setBorder(null);
		channelNameScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		channelNameScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		channelNameScrollPane.setPreferredSize(new java.awt.Dimension(100, 100));
		channelNameScrollPane.setMinimumSize(new java.awt.Dimension(100, 100));
		channelNameScrollPane.setMaximumSize(new java.awt.Dimension(100, 100));
		channelNamePanel.setLayout(null);
		
		channelNamePanel.setBackground(new java.awt.Color(245, 245, 255));
		channelNamePanel.setPreferredSize(new java.awt.Dimension(100, 100));
		channelNamePanel.setMinimumSize(new java.awt.Dimension(100, 100));
		channelNamePanel.setMaximumSize(new java.awt.Dimension(100, 100));
		channelNameScrollPane.setViewportView(channelNamePanel);
		
		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.weighty = 0.9;
		outerPanel.add(channelNameScrollPane, gridBagConstraints2);
		
		timeScrollPane.setBorder(null);
		timeScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		timeScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		timeScrollPane.setPreferredSize(new java.awt.Dimension(24, 24));
		timeScrollPane.setMinimumSize(new java.awt.Dimension(24, 24));
		timeScrollPane.setMaximumSize(new java.awt.Dimension(24, 24));
		timePanel.setBackground(new java.awt.Color(245, 245, 255));
		timeScrollPane.setViewportView(timePanel);
		
		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.weightx = 0.9;
		outerPanel.add(timeScrollPane, gridBagConstraints2);
		
		splitPane.setLeftComponent(outerPanel);
		
		printedGuideArea.setLineWrap(true);
		printedGuideArea.setEditable(false);
		printedGuideArea.setBackground(new java.awt.Color(230, 230, 230));
		printedGuideArea.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
		printedGuideScrollPane.setViewportView(printedGuideArea);
		
		splitPane.setRightComponent(printedGuideScrollPane);
		
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(2, 2, 2, 2);
		gridBagConstraints1.weightx = 0.9;
		gridBagConstraints1.weighty = 0.9;
		getContentPane().add(splitPane, gridBagConstraints1);
		
		labStatus.setText("Welcome.");
		labStatus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		labStatus.setFont(new java.awt.Font("Dialog", 0, 12));
		labStatus.setPreferredSize(new java.awt.Dimension(50, 20));
		labStatus.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 4;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(0, 2, 2, 2);
		gridBagConstraints1.weightx = 0.9;
		getContentPane().add(labStatus, gridBagConstraints1);
		
		progressBar.setFont(new java.awt.Font("Dialog", 0, 12));
		progressBar.setPreferredSize(new java.awt.Dimension(100, 20));
		progressBar.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
		progressBar.setMinimumSize(new java.awt.Dimension(100, 20));
		progressBar.setMaximumSize(new java.awt.Dimension(100, 20));
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 4;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(0, 0, 2, 2);
		gridBagConstraints1.weightx = 0.1;
		getContentPane().add(progressBar, gridBagConstraints1);
		
		setJMenuBar(jMenuBar2);
		pack();
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new java.awt.Dimension(600, 400));
		setLocation((screenSize.width-600)/2,(screenSize.height-400)/2);
	}//GEN-END:initComponents

	private void butGoToNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGoToNowActionPerformed
		
		goToNow();
		
	}//GEN-LAST:event_butGoToNowActionPerformed

	private void menAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAboutActionPerformed
		
		
		
	}//GEN-LAST:event_menAboutActionPerformed

	private void menUserGuideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menUserGuideActionPerformed
		
		
		
	}//GEN-LAST:event_menUserGuideActionPerformed

	private void menOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menOptionsActionPerformed
		
		hide();
		new FreeGuideOptions().show();
		dispose();
		
	}//GEN-LAST:event_menOptionsActionPerformed

	private void menFavouritesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menFavouritesActionPerformed
		
		/*
		hide();
		new FreeGuideFavourites().show();
		dispose(); 
		 */
		
	}//GEN-LAST:event_menFavouritesActionPerformed

	private void menDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menDownloadActionPerformed
		
		hide();
		new FreeGuideDownloader().show();
		dispose();
		
	}//GEN-LAST:event_menDownloadActionPerformed

	private void menQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menQuitActionPerformed
		
		quit();
		
	}//GEN-LAST:event_menQuitActionPerformed

	private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
		
		
		
	}//GEN-LAST:event_butPrintActionPerformed

	private void butNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNextActionPerformed
		
		theDate.setDate(theDate.getDate()+1);
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
		String datestr = fmt.format(theDate);
		
		comTheDate.setSelectedItem(datestr);
		
		updatePanel();
		
	}//GEN-LAST:event_butNextActionPerformed

	private void comTheDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comTheDateActionPerformed
		
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
				
		try {
			
			theDate = fmt.parse((String)comTheDate.getSelectedItem());
			
		} catch(ParseException e) {
			
			e.printStackTrace();
			
		}//try

		updatePanel();
		
	}//GEN-LAST:event_comTheDateActionPerformed

	private void butPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrevActionPerformed
		
		theDate.setDate(theDate.getDate()-1);
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
		String datestr = fmt.format(theDate);

		comTheDate.setSelectedItem(datestr);
		
		updatePanel();
		
	}//GEN-LAST:event_butPrevActionPerformed

	/**
	 * Add listeners to the two scrollbars in the GUI and put the relevant
	 * dates in the date list
	 */
    private void initMyComponents() {
	
		//  Do the listeners
		
		innerScrollPane.getHorizontalScrollBar().addAdjustmentListener(new java.awt.event.AdjustmentListener() {
			public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
				innerScrollPaneHorAdjust(evt);
			}
		});
	
		innerScrollPane.getVerticalScrollBar().addAdjustmentListener(new java.awt.event.AdjustmentListener() {
			public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
				innerScrollPaneVerAdjust(evt);
			}
		});
		
		// Make the dates list
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
		
		for(int i=0;i<14;i++) {
		
			Date tmpDate = new Date(theDate.getTime());
			tmpDate.setDate(tmpDate.getDate() + i);
			
			 comTheDate.addItem(fmt.format(tmpDate));
			
		}
		
		// Choose the current date
		String datestr = fmt.format(theDate);
		comTheDate.setSelectedItem(datestr);
	
    }//initMyComponents
    
	private void goToNow() {
	
		// TO ADD: go to today if not already there.
		
		int tmpScr = timePanel.getNowScroll();
		
		timeScrollPane.getHorizontalScrollBar().setValue(tmpScr);
		innerScrollPane.getHorizontalScrollBar().setValue(tmpScr);
		
	}
	
	/**
	 * The event procedure for the horizontal scrollpane listener - just
	 * calls the scrollTime method.
	 */
    private void innerScrollPaneHorAdjust(java.awt.event.AdjustmentEvent evt) {
		scrollTime();
    }//innerScrollPaneHorAdjust
    
	/**
	 * The event procedure for the vertical scrollpane listener - just
	 * calls the scrollChannelNames method.
	 */
    private void innerScrollPaneVerAdjust(java.awt.event.AdjustmentEvent evt) {
		scrollChannelNames();
    }//innerScrollPaneVerAdjust
    
	/**
	 * The event listener for the form closing event - calls the quit method
	 */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        quit();
    }//GEN-LAST:event_exitForm

	/**
	 * Asks the user whether she wants to quit, and then quits if so.
	 */
	private void quit() {
		
		// Ask the user whether to quit or not
		
		if(JOptionPane.showConfirmDialog(this, "Are you sure you want to quit FreeGuide?", "Quit?", JOptionPane.YES_NO_OPTION)==0) {
			System.exit(0);
		}
		
	}//quit
	
	/**
	 * Gets the channels required from the config file and puts them
	 * in the channelNames array
	 */
    private void getChannelNames() {

		/*
        // Alter to exact names of required channels with underscore
        // instead of space
        
        // Will be replaced with reading a config file.
		channelNames = new String[7];
        channelNames[0]="BBC1";
		channelNames[1]="BBC2";
        channelNames[2]="Channel_4";
		channelNames[3]="Sky_One";
		channelNames[4]="FilmFour";
		channelNames[5]="ITV2";
        channelNames[6]="BBC_Choice";
		 */
		
		Vector tmpChannels = FreeGuide.config.getListValue("channels");
		
		channelNames = new String[tmpChannels.size()];
		
		// INSERT HERE a better algorithm
		
		for(int i=0;i<channelNames.length;i++) {
			
			channelNames[i] = (String)tmpChannels.get(i);
			
		}
    }
     
	/**
	 * Loads the programme data from a file and stores it in a class
	 * structure ready for display on the screen.
	 */
    private void loadProgrammeData() {
		
		for(curChan=0;curChan<channelNames.length;curChan++) {
		
			channels[curChan] = new FreeGuideChannelDay(channelNames[curChan]);
        
			freeGuideHomeDir = FreeGuide.config.getValue("freeguideDir");
	
			int hMFNL = (int)(MAX_FILENAME_LENGTH/2);
	
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			String datestr = fmt.format(theDate);
	
			// Find what the filename ought to be		
			String tmp = channelNames[curChan].toLowerCase().replace(' ', '_');
			tmp = FreeGuide.makeRightFilenameLength(tmp);
			String xmlFilename = freeGuideHomeDir+"data/"+tmp+"-"+datestr+".fgd";
	    
			if((new File(xmlFilename).exists())) {	// If the file exists
				
				// Load it into memory and process it
				
				try {//ParserExceptions etc
	    
					DefaultHandler handler = new FreeGuideSAXHandler(this);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxParser = factory.newSAXParser();
		
					// Will be a different name for each channel and whatever
					// date we're on obviously
					saxParser.parse(xmlFilename, handler);
	    
				} catch(ParserConfigurationException e) {
					e.printStackTrace();
				} catch(SAXException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}//try
				
			} else { // If no file exists
				
				// Do nothing because no file exists
	    
			}//if
			
		}//for
		
	}//loadProgrammeData
    
	/**
	 * Finds the earliest and latest programmes so the screen can be
	 * scaled to fit everything.
	 *
	 */
    private void initForDraw() {
	
		// Find out the start time of this day
		// -----------------------------------
	
		earliest = null;
	
		// Get the start time of each channel
		for(int i=0;i<channels.length;i++) {
	    
		    Date tmp = channels[i].getStart();
		    if(tmp!=null && (earliest==null || tmp.before(earliest))) {
				earliest = new Date(tmp.getTime());
			}
	    
		}
	
		// If we failed to get an earliest time
		if(earliest==null) {
			FreeGuide.log.writeLine("FreeGuideViewer:: initForDraw - earliest was null.");
			// Set it to 24 hours before the day we're doing
			earliest = new Date(theDate.getTime() - 1000*60*60*24);
		}
	
		// Find out the end time of this day
		// ---------------------------------
	
		latest = null;
	
		// Get the end time of each channel
		for(int i=0;i<channels.length;i++) {
	    
			Date tmp = channels[i].getEnd();
			if(tmp!=null && (latest==null || tmp.after(latest))) {
				latest = new Date(tmp.getTime());
			}
	    
		}
	
		// If we failed to get a latest time
		if(latest==null) {
		    FreeGuide.log.writeLine("FreeGuideViewer:: initForDraw - latest was null.");
		    // Set it to 24 hours after the day we're doing
			latest = new Date(theDate.getTime() + 1000*60*60*24);
		}
	
	}//initForDraw
    
	/**
	 * Given the checkbox displaying it, returns the programme.
	 *
	 * @param bx the checkbox for a programme
	 * @returns  the programme referred to
	 */
	private FreeGuideProgramme getProgFromCheckbox(JCheckBox bx) {
		int i = chks.indexOf(bx);
		return (FreeGuideProgramme)progRefs.get(i);
	}//getProgFromCheckbox

	/**
	 * The event procedure for a checkbox - updates the printout
	 */
	private void chkItemStateChanged(java.awt.event.ItemEvent evt) {
		updatePrintedGuide();
	}//chkItemStateChanged
    
	/**
	 * Scrolls the time panel to the same scroll height as the main panel.
	 */
    private void scrollTime() {
		timeScrollPane.getHorizontalScrollBar().setValue(innerScrollPane.getHorizontalScrollBar().getValue());
    }//scrollTime
    
	/**
	 * Scrolls the channel names to the same scroll height as the main panel.
	 */
    private void scrollChannelNames() {
		channelNameScrollPane.getVerticalScrollBar().setValue(innerScrollPane.getVerticalScrollBar().getValue());	
    }//scrollChannelNames
    
	/**
	 * Does the main work of displaying the stored programems on screen.
	 */

	private void drawChannels() {

		String lineBreak = System.getProperty("line.separator");
		
		//NOTE: MUST CHANGE
		// Read from options file
		int channelHeight = 60;
		int halfVerGap = 1;
		int halfHorGap=1;
		int panelWidth = 7500;
	
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
	
		// Delete any old boxes
		if(chks!=null) {
			for(int i=0;i<chks.size();i++) {
				innerPanel.remove((JCheckBox)chks.get(i));
				innerPanel.remove((JTextArea)txts.get(i));
			}//for
		}//if
		//timePanel.removeAll();
		channelNamePanel.removeAll();
	
		// References which allow us to relate a checkbox to a programme:
		chks = new Vector();
		txts = new Vector();
		progRefs = new Vector();
	
		// Temporal width in millisecs
		long temporalWidth = latest.getTime()-earliest.getTime();
	
		// Find the multiplier to help us position programmes
		double widthMultiplier = (double)panelWidth/(double)temporalWidth;
	
		channelNamePanel.setPreferredSize(new Dimension(0, channels.length*channelHeight+50));
		channelNamePanel.setMinimumSize(new Dimension(0, channels.length*channelHeight+50));
		channelNamePanel.setMaximumSize(new Dimension(0, channels.length*channelHeight+50));
	
		/*long hourCount = earliest.getTime();
	
		hourCount = hourCount - (hourCount % (60*60*1000));
	
		for(long h=hourCount;h<latest.getTime();h+=(60*60*1000)) {
	    
			JLabel lab = new JLabel(fmt.format(new Date(h)));
			int tmp = (int)((h-earliest.getTime())*widthMultiplier);
			lab.setBounds(tmp, 0, tmp+(int)(1000*60*60*widthMultiplier)+1, timePanel.getHeight());
			lab.setFont(new java.awt.Font("Dialog", 0, 12));
			lab.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
	    
		    timePanel.add(lab);
		    //txts.add(lab);
	    
		}
	
		timePanel.setPreferredSize(new Dimension((int)((latest.getTime()-earliest.getTime())*widthMultiplier)+50,0));
		timePanel.setMinimumSize(new Dimension((int)((latest.getTime()-earliest.getTime())*widthMultiplier)+50,0));
		timePanel.setMaximumSize(new Dimension((int)((latest.getTime()-earliest.getTime())*widthMultiplier)+50,0));
		 */
	
		for(int ch=0;ch<channels.length;ch++) {
	    
			Vector progs = channels[ch].getProgrammes();
	    
			JLabel ctxt = new JLabel(channels[ch].getName());
			ctxt.setBounds(0, (halfVerGap*2)+(ch*channelHeight), channelNamePanel.getWidth()-1, channelHeight-(halfVerGap*4));
	    
		    ctxt.setBackground(Color.yellow);
		    //ctxt.setLineWrap(true);
		    //ctxt.setEditable(false);
			ctxt.setFont(new java.awt.Font("Dialog", 1, 12));
			ctxt.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
			//ctxt.setSelectionColor(java.awt.Color.yellow);
			ctxt.setHorizontalAlignment(JLabel.CENTER);
			ctxt.setOpaque(true);
	    
			channelNamePanel.add(ctxt);
			//txts.add(ctxt);
	    
			for(int pr=0;pr<progs.size();pr++) {
		
				FreeGuideProgramme prog = (FreeGuideProgramme)progs.get(pr);
		
				Date st = prog.getStart();
				Date ed = prog.getSomeEnd();
		
				JCheckBox chk = new JCheckBox("");
				JTextArea txt = new JTextArea("    " + fmt.format(st)+ " " + prog.getTitle() + lineBreak + prog.getDesc());
				txt.setToolTipText(fmt.format(st)+"-"+fmt.format(ed) + " " + prog.getChannel() + " - " + prog.getTitle() + " - " + prog.getDesc());
				chk.setToolTipText(fmt.format(st)+"-"+fmt.format(ed) + " " + prog.getChannel() + " - " + prog.getTitle() + " - " + prog.getDesc());
		
				int left = halfHorGap+(int)((st.getTime()-earliest.getTime())*widthMultiplier);
				int right = ((int)((ed.getTime()-earliest.getTime())*widthMultiplier)) - (halfHorGap*2);
				int top = halfVerGap+(ch*channelHeight);
				int bottom = ((ch+1)*channelHeight)-(halfVerGap*2);
		
				txt.setBounds(left, top, right-left, bottom-top);
				
				chk.setBounds(left, top, 12, 12);
				//txt.setText(lineBreak + txt.getText());
				
/*				if(right-left<54) {
					chk.setBounds(left+((right-left)/2)-6, top+4, 12, 12);
					txt.setText(lineBreak + txt.getText());
				} else {
					chk.setBounds(left+41, top+4, 12, 12);
				}*/
				txt.setBackground(Color.white);
				chk.setBackground(Color.white);
		
				txt.setLineWrap(true);
				txt.setEditable(false);
				txt.setFont(new java.awt.Font("Dialog", 0, 12));
				txt.setBorder(new javax.swing.border.LineBorder(Color.black));
				txt.setSelectionColor(Color.white);
		
				chk.setBorder(new javax.swing.border.LineBorder(Color.black));
				
				chk.addItemListener(new java.awt.event.ItemListener() {
					public void itemStateChanged(java.awt.event.ItemEvent evt) {
						chkItemStateChanged(evt);
					}
				});
		
				innerPanel.add(chk);
				innerPanel.add(txt);
		
				chks.add(chk);
				txts.add(txt);
				progRefs.add(prog);
		
		    }
	    
			// Following code is supposed to scroll us to the time now.
		
			/*Date h = new Date();
		
			int tmp = (int)((h.getTime()-earliest.getTime())*widthMultiplier);
		
			timeScrollPane.getHorizontalScrollBar().setValue(tmp);
			innerScrollPane.getHorizontalScrollBar().setValue(tmp);
		
			timeScrollPane.getHorizontalScrollBar().setValue(100);*/
		
		    //lab.setBounds(tmp, 0, tmp+(int)(1000*60*60*widthMultiplier)+1, timePanel.getHeight());
		
		}
	
		innerPanel.setPreferredSize(new java.awt.Dimension(panelWidth, channels.length*channelHeight));
		innerPanel.setMinimumSize(new java.awt.Dimension(panelWidth, channels.length*channelHeight));
		innerPanel.setMaximumSize(new java.awt.Dimension(panelWidth, channels.length*channelHeight));

		int tmpH = timePanel.getPreferredSize().height;
		
		timePanel.setPreferredSize(new java.awt.Dimension(panelWidth, tmpH));
		timePanel.setMinimumSize(new java.awt.Dimension(panelWidth, tmpH));
		timePanel.setMaximumSize(new java.awt.Dimension(panelWidth, tmpH));
		
		timePanel.setTimes(earliest, latest, new Date());
		
		timePanel.repaint();
		
		goToNow();
		
	}//drawChannels
	
    private void updatePanel() {
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE yyyy-MM-dd");
	
		getChannelNames();
	
		// Count the channels
		int noChans=channelNames.length;
	
		// Make an array of FreeGuideChannelDay objects
		channels = new FreeGuideChannelDay[noChans];
	
		labStatus.setText("Loading programme info...");
	
		loadProgrammeData();
	
		labStatus.setText("Drawing...");
	
		initForDraw();
		drawChannels();
		updatePrintedGuide();
		
		labStatus.setText("Done.");
		
    }
    
    private void updatePrintedGuide() {

		String lineBreak = System.getProperty("line.separator");
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEEE dd MMMM yyyy");
	
		printedGuideArea.setText("Your TV Guide for "+fmt.format(theDate)+":"+lineBreak);
	
		// Get all ticked programmes into a vector
		// ---------------------------------------
	
		Vector listedProgs = new Vector();
	
		for(int i=0;i<chks.size();i++) {
	    
			if(((JCheckBox)chks.get(i)).isSelected()) {
		
			listedProgs.add((FreeGuideProgramme)progRefs.get(i));
		
			}
	    
		}
	
		// Sort by start time
		// ------------------
		Collections.sort(listedProgs, new FreeGuideProgrammeStartTimeComparator());
	
		// Add them to the printer list
		// ----------------------------
	
		for(int i=0;i<listedProgs.size();i++) {
	
			FreeGuideProgramme prog = (FreeGuideProgramme)listedProgs.get(i);
	
			fmt = new SimpleDateFormat("HH:mm");
	    
			printedGuideArea.append(lineBreak+fmt.format(prog.getStart())+"-"+fmt.format(prog.getSomeEnd())+" "+prog.getChannel()+" - "+prog.getTitle()+lineBreak);
			printedGuideArea.append(prog.getDesc()+lineBreak);
	    
		}
	
    }
    
    public void startDocument() {  
		saxLoc = new String();
	}
    
    public void endDocument() {
		saxLoc=null;
    }
    
    public void startElement(String name, org.xml.sax.Attributes attrs) {
		saxLoc+=":"+name;
	
		if(saxLoc.equals(":tv:programme")) {
	    
			String start = attrs.getValue("start");
	    
			if(attrs.getIndex("end")!=-1) {

			String end = attrs.getValue("end");
			channels[curChan].addProgramme(start, end);
		
			} else {

			channels[curChan].addProgramme(start);

			}
	    
		}
    }
    
    public void endElement(String name) {
	
		if(saxLoc.endsWith(name)) {
	    
			saxLoc=saxLoc.substring(0, saxLoc.length()-(name.length()+1));
	    
		} else {
			parseError();
		}
	
    }
    
    public void characters(String data) {
	
		if(saxLoc.equals(":tv:programme:title")) {
	    
			channels[curChan].getLatestProg().setTitle(data);
	    
		} else if (saxLoc.equals(":tv:programme:desc")) {
	    
			channels[curChan].getLatestProg().setDesc(data);
	    
		} else if (saxLoc.equals(":tv:programme:category")) {
	    
			channels[curChan].getLatestProg().setCategory(data);
	    
		}
	
    }
    
    private void parseError() {
		FreeGuide.log.writeLine("FreeGuide - Error parsing XML.");
		System.exit(1);
    }


	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenuBar jMenuBar2;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenuItem menPrint;
	private javax.swing.JSeparator jSeparator5;
	private javax.swing.JMenuItem menQuit;
	private javax.swing.JMenu toolsMenu;
	private javax.swing.JMenuItem menDownload;
	private javax.swing.JSeparator jSeparator3;
	private javax.swing.JMenuItem menFavourites;
	private javax.swing.JMenuItem menOptions;
	private javax.swing.JMenu helpMenu;
	private javax.swing.JMenuItem menUserGuide;
	private javax.swing.JSeparator jSeparator4;
	private javax.swing.JMenuItem menAbout;
	private javax.swing.JPanel topPanel;
	private javax.swing.JButton butGoToNow;
	private javax.swing.JButton butPrev;
	private javax.swing.JComboBox comTheDate;
	private javax.swing.JButton butNext;
	private javax.swing.JButton butPrint;
	private javax.swing.JSplitPane splitPane;
	private javax.swing.JPanel outerPanel;
	private javax.swing.JScrollPane innerScrollPane;
	private javax.swing.JPanel innerPanel;
	private javax.swing.JScrollPane channelNameScrollPane;
	private javax.swing.JPanel channelNamePanel;
	private javax.swing.JScrollPane timeScrollPane;
	private FreeGuideTimePanel timePanel;
	private javax.swing.JScrollPane printedGuideScrollPane;
	private javax.swing.JTextArea printedGuideArea;
	private javax.swing.JLabel labStatus;
	private javax.swing.JProgressBar progressBar;
	// End of variables declaration//GEN-END:variables

    private static final int MAX_FILENAME_LENGTH=16;
    
    private String[] channelNames;
	// The names of the channels the user has chosen
    private FreeGuideChannelDay[] channels;
	// The FreeGuideChannelDay objects that hold the prog. info
    
    private String saxLoc;  // Holds our current pos in the XML hierarchy

    private int curChan;    // The channel we're doing now
    
    private Date earliest;  // The beginning of this day
    private Date latest;    // The end of this day
    
    private Date theDate;   // The date for which we are listing programmes
    
    private Vector chks;	// Stores references to all the checkboxes shown
    private Vector txts;	// Stores references to all the textareas shown
    private Vector progRefs;	// The programmes to which each checkbox refers
    
    private static String freeGuideHomeDir;	// The home dir/root path of this prog
    
}

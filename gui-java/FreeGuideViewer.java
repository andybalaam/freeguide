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
import javax.swing.text.JTextComponent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A form that displays and prints TV listings.
 *
 * @author  Andy Balaam
 * @version 8
 */
public class FreeGuideViewer extends javax.swing.JFrame implements FreeGuideLauncher, FreeGuideSAXInterface {

    public FreeGuideViewer(FreeGuideLauncher newLauncher) {
		
		launcher = newLauncher;
		ready=false;
		
		// Set up UI
        initComponents();
		
		// Set the date to today
		theDate = new Date();
		
		// Set up custom UI elements
		initMyComponents();
		
		ready=true;
		
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
		printedGuideAreaOld = new javax.swing.JTextArea();
		printedGuideArea = new javax.swing.JEditorPane();
		
		fileMenu.setText("File");
		menPrint.setText("Print Listing");
		menPrint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menPrintActionPerformed(evt);
			}
		});
		
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
		menFavourites.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				menFavouritesActionPerformed(evt);
			}
		});
		
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
		comTheDate.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				comTheDateItemStateChanged(evt);
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
		timePanel.setEnabled(false);
		timeScrollPane.setViewportView(timePanel);
		
		gridBagConstraints2 = new java.awt.GridBagConstraints();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 0;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints2.weightx = 0.9;
		outerPanel.add(timeScrollPane, gridBagConstraints2);
		
		splitPane.setLeftComponent(outerPanel);
		
		printedGuideAreaOld.setLineWrap(true);
		printedGuideAreaOld.setEditable(false);
		printedGuideAreaOld.setBackground(new java.awt.Color(230, 230, 230));
		printedGuideAreaOld.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
		printedGuideScrollPane.setViewportView(printedGuideAreaOld);
		
		printedGuideArea.setEditable(false);
		printedGuideArea.setFont(new java.awt.Font("Dialog", 0, 8));
		printedGuideArea.setContentType("text/html");
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
		
		setJMenuBar(jMenuBar2);
		pack();
		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setSize(new java.awt.Dimension(600, 400));
		setLocation((screenSize.width-600)/2,(screenSize.height-400)/2);
	}//GEN-END:initComponents

	private void comTheDateItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comTheDateItemStateChanged
		
		updateIfDateChanged();
			
	}//GEN-LAST:event_comTheDateItemStateChanged

	private void menPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menPrintActionPerformed
		
		writeOutAsHTML();
		
	}//GEN-LAST:event_menPrintActionPerformed

	private void butGoToNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGoToNowActionPerformed
		
		goToNow();
		
	}//GEN-LAST:event_butGoToNowActionPerformed

	private void menAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menAboutActionPerformed
		
		
		
	}//GEN-LAST:event_menAboutActionPerformed

	private void menUserGuideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menUserGuideActionPerformed
		
		
		
	}//GEN-LAST:event_menUserGuideActionPerformed

	private void menOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menOptionsActionPerformed
		
		hide();
		new FreeGuideOptions(this).show();
		
	}//GEN-LAST:event_menOptionsActionPerformed

	private void menFavouritesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menFavouritesActionPerformed
				
		hide();
		new FreeGuideFavourites(this).show();
		
	}//GEN-LAST:event_menFavouritesActionPerformed

	private void menDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menDownloadActionPerformed
		
		hide();
		new FreeGuideDownloader(this).show();
		
	}//GEN-LAST:event_menDownloadActionPerformed

	private void menQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menQuitActionPerformed
		
		quit();
		
	}//GEN-LAST:event_menQuitActionPerformed

	private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
		
		writeOutAsHTML();
		
	}//GEN-LAST:event_butPrintActionPerformed

	private void butNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNextActionPerformed
		
		Date tmpDate = new Date(theDate.getTime());
		tmpDate.setDate(tmpDate.getDate()+1);
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
		String datestr = fmt.format(tmpDate);
		
		comTheDate.setSelectedItem(datestr);
		
	}//GEN-LAST:event_butNextActionPerformed

	private void butPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrevActionPerformed
		
		Date tmpDate = new Date(theDate.getTime());
		tmpDate.setDate(tmpDate.getDate()-1);
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
		String datestr = fmt.format(tmpDate);

		comTheDate.setSelectedItem(datestr);
		
	}//GEN-LAST:event_butPrevActionPerformed

	/**
	 * Add listeners to the two scrollbars in the GUI and put the relevant
	 * dates in the date list
	 */
    private void initMyComponents() {
	
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
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
	
		// Go to today if not already there.
		
		Date now = new Date();
		
		// If we're not on today
		if(theDate.getDate()!=now.getDate() || theDate.getMonth()!=now.getMonth() || theDate.getYear()!=now.getYear()) {
			
			// Go to today
			theDate = now;
			
			SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
			String datestr = fmt.format(theDate);
		
			comTheDate.setSelectedItem(datestr);
			
		}
		
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
		//if(JOptionPane.showConfirmDialog(this, "Are you sure you want to quit FreeGuide?", "Quit?", JOptionPane.YES_NO_OPTION)==0) {
			System.exit(0);
		//}
		
	}//quit
	
	/**
	 * Gets the channels required from the config file and puts them
	 * in the channelNames array
	 */
    private void getChannelNames() {
		
		Vector tmpChannels = FreeGuide.config.getListValue("channels");
		
		channelNames = new String[tmpChannels.size()];
		channelIDs = new String[tmpChannels.size()];
		
		// INSERT HERE a better algorithm
		
		for(int i=0;i<channelNames.length;i++) {
			
			channelNames[i] = (String)tmpChannels.get(i);
			
		}
		
		// Get the channel IDs from the channels file
		String xmlFilename = FreeGuide.config.getValue("channelsFile");		
		if((new File(xmlFilename).exists())) {	// If the file exists
				
				// Load it into memory and process it
			
				try {//ParserExceptions etc
	    
					//FreeGuide.log.writeLine("channels file is "+xmlFilename);
					
					DefaultHandler handler = new FreeGuideSAXHandler(this);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					SAXParser saxParser = factory.newSAXParser();
					saxParser.parse(xmlFilename, handler);
	    
				} catch(ParserConfigurationException e) {
					e.printStackTrace();
				} catch(SAXException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}//try
				
		} else { // If no file exists
				
			// No channels file exists!
			FreeGuide.log.writeLine("Channels file not found: "+xmlFilename);
				
		}//if

    }
     
	/**
	 * Loads the programme data from a file and stores it in a class
	 * structure ready for display on the screen.
	 */
    private void loadProgrammeData() {
		
		for(curChan=0;curChan<channelNames.length;curChan++) {
		
			channels[curChan] = new FreeGuideChannelDay(channelNames[curChan]);
        
			String wkDir = FreeGuide.config.getValue("workingDirectory");
	
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			String datestr = fmt.format(theDate);
	
			// Find what the filename ought to be		
			//String tmp = channelNames[curChan].toLowerCase().replace(' ', '_');
			//tmp = FreeGuide.makeRightFilenameLength(tmp);
			String xmlFilename = wkDir+"data/"+channelIDs[curChan]+"-"+datestr+".fgd";
	    
			if((new File(xmlFilename).exists())) {	// If the file exists
				
				//FreeGuide.log.writeLine("Opening programmes file "+xmlFilename);
				
				// Load it into memory and process it
				
				try {//ParserExceptions etc
	    
					DefaultHandler handler = new FreeGuideSAXHandler(this);
					SAXParserFactory factory = SAXParserFactory.newInstance();
					
					SAXParser saxParser = factory.newSAXParser();
		
					doingProgs=true;
		
					// Will be a different name for each channel and whatever
					// date we're on obviously
					saxParser.parse(xmlFilename, handler);
					
					doingProgs=false;
	    
				} catch(ParserConfigurationException e) {
					e.printStackTrace();
				} catch(SAXException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}//try
				
			} else { // If no file exists
				
				// Do nothing because no file exists
	    
				FreeGuide.log.writeLine("Listings file not found: "+xmlFilename);
				
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
	 * and sets the textarea's colour
	 */
	private void chkItemStateChanged(java.awt.event.ItemEvent evt) {
		
		JCheckBox chk = (JCheckBox)evt.getItem();
		
		JTextComponent txt = (JTextComponent)txts.get(chks.indexOf(chk));
		
		if(chk.isSelected()) {
		
			txt.setBackground(new Color(220,220,220));
			
		} else {
			
			txt.setBackground(new Color(255,255,255));
			
		}
		
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
		
		// Read in viewing options
		int channelHeight = Integer.parseInt(FreeGuide.config.getValue("channelHeight"));
		int halfVerGap = Integer.parseInt(FreeGuide.config.getValue("verticalGap"));
		int halfHorGap= Integer.parseInt(FreeGuide.config.getValue("horizontalGap"));
		int panelWidth= Integer.parseInt(FreeGuide.config.getValue("panelWidth"));
	
		SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
	
		innerPanel.removeAll();
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
	
		for(int ch=0;ch<channels.length;ch++) {
	    
			Vector progs = channels[ch].getProgrammes();
	    
			JLabel ctxt = new JLabel(channels[ch].getName());
			ctxt.setBounds(0, (halfVerGap*2)+(ch*channelHeight), channelNamePanel.getWidth()-1, channelHeight-(halfVerGap*4));
	    
		    ctxt.setBackground(new Color(245,245,255));
			ctxt.setFont(new java.awt.Font("Dialog", 1, 12));
			ctxt.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
			//ctxt.setSelectionColor(java.awt.Color.yellow);
			ctxt.setHorizontalAlignment(JLabel.CENTER);
			ctxt.setOpaque(true);
	    
			channelNamePanel.add(ctxt);
	    
			for(int pr=0;pr<progs.size();pr++) {
		
				FreeGuideProgramme prog = (FreeGuideProgramme)progs.get(pr);
		
				Date st = prog.getStart();
				Date ed = prog.getSomeEnd();
		
				String progDesc = prog.getDesc();
				String progTitle = prog.getTitle();
				
				JCheckBox chk = new JCheckBox("");
				JTextArea txt = new JTextArea("    " + fmt.format(st)+ " " + progTitle);
				
				// TO DO: option to add editor pane styled text with description
				//JEditorPane txt = new JEditorPane("text/html", "    " + fmt.format(st)+ " " + prog.getTitle() + lineBreak + prog.getDesc());
				
				txt.setBackground(Color.white);
				chk.setBackground(Color.white);
				
				// Check if this is a favourite and tick it if so
				Vector favourites = FreeGuide.config.getListValue("favourites");
				if(favourites!=null) {
					
					for(int i=0;i<favourites.size();i++) {
						
						if(progTitle.toLowerCase().indexOf(((String)favourites.get(i)).toLowerCase())>-1) {
							
							chk.setSelected(true);
							txt.setBackground(new Color(220,220,220));
							
							break;
							
						}
						
					}
					
				}
				
				txt.setToolTipText(progTitle + " - " + progDesc);
				chk.setToolTipText(progTitle + " - " + progDesc);
		
				int left = halfHorGap+(int)((st.getTime()-earliest.getTime())*widthMultiplier);
				int right = ((int)((ed.getTime()-earliest.getTime())*widthMultiplier)) - (halfHorGap*2);
				int top = halfVerGap+(ch*channelHeight);
				int bottom = ((ch+1)*channelHeight)-(halfVerGap*2);
		
				txt.setBounds(left, top, right-left, bottom-top);
				
				chk.setBounds(left, top, 12, 12);
		
				txt.setLineWrap(true);
				txt.setEditable(false);
				txt.setFont(new java.awt.Font("Dialog", 0, 10));
				txt.setBorder(new javax.swing.border.LineBorder(Color.black));
				txt.setSelectionColor(Color.white);
				txt.setOpaque(true);
		
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
		
		}
	
		innerPanel.setPreferredSize(new java.awt.Dimension(panelWidth, channels.length*channelHeight));
		innerPanel.setMinimumSize(new java.awt.Dimension(panelWidth, channels.length*channelHeight));
		innerPanel.setMaximumSize(new java.awt.Dimension(panelWidth, channels.length*channelHeight));

		int tmpH = timePanel.getPreferredSize().height;
		
		timePanel.setPreferredSize(new java.awt.Dimension(panelWidth, tmpH));
		timePanel.setMinimumSize(new java.awt.Dimension(panelWidth, tmpH));
		timePanel.setMaximumSize(new java.awt.Dimension(panelWidth, tmpH));
		
		timePanel.setTimes(earliest, latest, new Date());
		
		updatePrintedGuide();
		
		timePanel.revalidate();
		timePanel.repaint();
		
	}//drawChannels
	
	private void updateIfDateChanged() {
		
		SimpleDateFormat fmt = new SimpleDateFormat("EEE dd MMMM yyyy");
		
		if(ready && !fmt.format(theDate).equals((String)comTheDate.getSelectedItem())) {
		
			try {
			
				theDate = fmt.parse((String)comTheDate.getSelectedItem());
			
			} catch(ParseException e) {
			
				e.printStackTrace();
			
			}//try

			updatePanel();
		
		}//if
		
	}
	
    private void updatePanel() {
	
		getChannelNames();
	
		// Count the channels
		int noChans=channelNames.length;
	
		// Make an array of FreeGuideChannelDay objects
		channels = new FreeGuideChannelDay[noChans];
	
		loadProgrammeData();
	
		initForDraw();
		drawChannels();
		updatePrintedGuide();
		
    }
    
    private void updatePrintedGuide() {

		printedGuideArea.setText(constructHTMLGuide(true));
	
    }
    
	/*
	 * Saves out the listings as an HTML file to be printed.
	 */
	private void writeOutAsHTML() {
		
		// Make a file in the default location
		File f = new File(FreeGuide.config.getValue("freeguideDir")+"guide.html");
		
		try {//IOException
			
			BufferedWriter buffy = new BufferedWriter(new FileWriter(f));

			buffy.write(constructHTMLGuide(false));

			buffy.close();
			
			JTextArea fb = new JTextArea();
			
			if(System.getProperty("os.name").equals("Windows")) {
			
				FreeGuide.execExternal(FreeGuide.config.getValue("browserCommandLine")+" \""+f.getAbsolutePath()+"\"", true, fb);
				
			} else {
				
				FreeGuide.execExternal(FreeGuide.config.getValue("browserCommandLine")+" "+f.getAbsolutePath(), false, fb);
				
			}
		
		} catch(IOException e) {
			e.printStackTrace();
		}//try
		
	}//writeOutAsHTML
	
	/**
	 * Makes a TV Guide in HTML format and returns it as a string.
	 *
	 * @return the TV guide as a string of html
	 */
	private String constructHTMLGuide(boolean onScreen) {
		
		// The string we shall return
		String ans = "";
		
		// Set up some constants
		SimpleDateFormat fmt = new SimpleDateFormat("EEEE dd MMMM yyyy");
		String lineBreak = System.getProperty("line.separator");

		ans+="<html>"+lineBreak;
		ans+="<head>"+lineBreak;
		ans+="  <title>TV Guide for "+fmt.format(theDate)+"</title>"+lineBreak;
		ans+="  <link rel='StyleSheet' href='"+FreeGuide.config.getValue("cssFile")+"' type='text/css'"+lineBreak;
		ans+="</head>"+lineBreak;
		ans+="<body>"+lineBreak;
		ans+="  <h1>";
		
		if(onScreen) {ans+="<font face='helvetica, helv, arial, sans serif' size=3>";}
		
		ans+="TV Guide for "+fmt.format(theDate);
		
		if(onScreen) {ans+="</font>";}
		
		ans+="</h1>"+lineBreak;

		// Get the programmes
		Vector listedProgs = getTickedProgrammes();

		// Add them to the HTML list
		// ----------------------------

		if(onScreen) {ans+="<font face='helvetica, helv, arial, sans serif' size=1>";}
		
		for(int i=0;i<listedProgs.size();i++) {

			FreeGuideProgramme prog = (FreeGuideProgramme)listedProgs.get(i);

			fmt = new SimpleDateFormat("HH:mm");
    
			ans+="  <p><b>"+fmt.format(prog.getStart())+" - "+prog.getTitle()+"</b><br>"+prog.getChannel()+", ends "+fmt.format(prog.getSomeEnd())+"<br>"+prog.getDesc()+"</p>"+lineBreak;
    
		}//for

		if(onScreen) {ans+="</font>";}
		
		ans+="<hr>"+lineBreak;
		ans+="<address>";
		
		if(onScreen) {ans+="<font face='helvetica, helv, arial, sans serif' size=1>";}
		
		ans+="FreeGuide Copyright &copy;2001 by Andy Balaam<br>Free software released under the GNU General Public Licence<br>http://freeguide-tv.sourceforge.net<br>freeguide@artificialworlds.net";
		
		if(onScreen) {ans+="</font>";}
		
		ans+="</address>"+lineBreak;
			
		ans+="</body>"+lineBreak;
		ans+="</html>"+lineBreak;

		return ans;
		
	}
	
	//------------------------------------------------------------------------
	
    public void startDocument() {  
		saxLoc = new String();
	}//startDocument
    
    public void endDocument() {
		saxLoc=null;
    }//endDocument
    
    public void startElement(String name, org.xml.sax.Attributes attrs) {
		saxLoc+=":"+name;
	
		if(doingProgs && saxLoc.equals(":tv:programme")) {
	    
			String start = attrs.getValue("start");
	    
			//FreeGuide.log.writeLine(start);
			
			if(attrs.getIndex("stop")!=-1) {

				String end = attrs.getValue("stop");
				channels[curChan].addProgramme(start, end);
				
				//FreeGuide.log.writeLine(end);
		
			} else {

				channels[curChan].addProgramme(start);

			}//if
	    
		} else if(saxLoc.equals(":tv:channel")) {
			
			String id = attrs.getValue("id");
			//FreeGuide.log.writeLine("ID "+id);
			tmpID = id;
			
		}//if
		
    }//startElement
    
    public void endElement(String name) {
	
		if(saxLoc.endsWith(name)) {
	    
			saxLoc=saxLoc.substring(0, saxLoc.length()-(name.length()+1));
	    
		} else {
			parseError();
		}//if
	
    }//endElement
    
    public void characters(String data) {
	
		if(doingProgs && saxLoc.equals(":tv:programme:title")) {
	    
			channels[curChan].getLatestProg().setTitle(data);
	    
		} else if (doingProgs && saxLoc.equals(":tv:programme:desc")) {
	    
			channels[curChan].getLatestProg().setDesc(data);
	    
		} else if (doingProgs && saxLoc.equals(":tv:programme:category")) {
	    
			channels[curChan].getLatestProg().setCategory(data);
	    
		} else if (saxLoc.equals(":tv:channel:display-name")) {
			
			// If this has a name we know, store its id
			for(int i=0;i<channelNames.length;i++) {
				if(channelNames[i].equals(data)) {
					//FreeGuide.log.writeLine("Data "+data);
					channelIDs[i] = tmpID;
				}
			}
			
		}//if
	
    }//characters
    
	/**
	 * Unhides this window after being hidden while launching another
	 * screen.
	 */
	public void reShow() {
		
		updatePanel();
		show();
		
	}//reShow
	
	//------------------------------------------------------------------------
	
	private Vector getTickedProgrammes() {
	
		Vector ans = new Vector();
		
		// Get all ticked programmes into a vector
		// ---------------------------------------
	
		for(int i=0;i<chks.size();i++) {
	    
			if(((JCheckBox)chks.get(i)).isSelected()) {
		
				ans.add((FreeGuideProgramme)progRefs.get(i));
		
			}//if
	    
		}//for
	
		// Sort by start time
		// ------------------
		Collections.sort(ans, new FreeGuideProgrammeStartTimeComparator());
		
		// Return the answer
		return ans;
		
	}//getTickedProgrammes
	
    private void parseError() {
		FreeGuide.log.writeLine("FreeGuideViewer - Error parsing XML.");
		System.exit(1);
    }//parseError


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
	private javax.swing.JTextArea printedGuideAreaOld;
	private javax.swing.JEditorPane printedGuideArea;
	// End of variables declaration//GEN-END:variables
    
	
	private String[] channelIDs;
	// The IDs of the channels the user has chosen
    private String[] channelNames;
	// The names of the channels the user has chosen
    private FreeGuideChannelDay[] channels;
	// The FreeGuideChannelDay objects that hold the prog. info
    
    private String saxLoc;  // Holds our current pos in the XML hierarchy

    private int curChan;    // The channel we're doing now
	private boolean doingProgs=false;	// Are we loading the programmes?
    
    private Date earliest;  // The beginning of this day
    private Date latest;    // The end of this day
    
    private Date theDate;   // The date for which we are listing programmes
    
    private Vector chks;	// Stores references to all the checkboxes shown
    private Vector txts;	// Stores references to all the textareas shown
    private Vector progRefs;	// The programmes to which each checkbox refers
    
    //private static String wkDir;	// The home dir/root path of this prog
    
	private FreeGuideLauncher launcher;	// The screen that launched this one
	
	private boolean ready;	// Have we prepared the screen yet?
	
	String tmpID;	// A temporary variable storing the channel ID
	
}

/*
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Logger;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * FreeGuideOptions
 *
 * The Options Screen for FreeGuide
 *
 * @author  Andy Balaam
 * @version 2
 */

public class FreeGuideOptions extends javax.swing.JFrame implements FreeGuideLauncher  {
	
	/** Creates new form FreeGuideOptions */
	public FreeGuideOptions(FreeGuideLauncher launcher) {
		this.launcher = launcher;
		initComponents();
		initMyComponents();
	}
	
	private void initMyComponents() {
		
		initChannels();
		initMisc();
		initScreen();
		initCommands();
		
	}
	
	private void initCommands() {
		
		String[] cmds;
		
		cmds = FreeGuide.prefs.commandline.getStrings("tv_grab");
		fillTextAreaFromArray( txaTvGrab, cmds );
		
		cmds = FreeGuide.prefs.commandline.getStrings("browser_command");
		fillTextAreaFromArray( txaBrowserCommand, cmds );

	}

	private void fillTextAreaFromArray(JTextArea txt, String[] cmds) {
	
		txt.setText("");
		
		String lb = System.getProperty("line.separator");
		
		for(int i=0;i<cmds.length;i++) {
			txt.append(cmds[i] + lb);
		}
		
	}
	
	private void initScreen() {
		
		Color col;
		
		col = FreeGuide.prefs.screen.getColor("channel_colour", FreeGuide.CHANNEL_COLOUR);
		fillTextAreaFromColor(txtChannelColour, col);
		
		txtChannelHeight.setText( FreeGuide.prefs.screen.get("channel_height", String.valueOf(FreeGuide.CHANNEL_HEIGHT)) );
		txtChannelPanelWidth.setText( FreeGuide.prefs.screen.get("channel_panel_width", String.valueOf(FreeGuide.CHANNEL_PANEL_WIDTH)) );
		txtHorizontalGap.setText( FreeGuide.prefs.screen.get("horizontal_gap", String.valueOf(FreeGuide.HORIZONTAL_GAP)) );
		txtVerticalGap.setText( FreeGuide.prefs.screen.get("vertical_gap", String.valueOf(FreeGuide.VERTICAL_GAP)) );
		txtPanelWidth.setText( FreeGuide.prefs.screen.get("panel_width", String.valueOf(FreeGuide.PANEL_WIDTH)) );
		
		col = FreeGuide.prefs.screen.getColor("programme_chosen_colour", FreeGuide.PROGRAMME_CHOSEN_COLOUR);
		fillTextAreaFromColor(txtProgrammeChosenColour, col);
		
		col = FreeGuide.prefs.screen.getColor("programme_normal_colour", FreeGuide.PROGRAMME_NORMAL_COLOUR);
		fillTextAreaFromColor(txtProgrammeNormalColour, col);
	}
	
	private void fillTextAreaFromColor(JTextField txt, Color col) {
		
		txt.setBackground(col);
		txt.setText( "(" + col.getRed() + ", " + col.getGreen() + ", " + col.getBlue() + ")");
		
	}
	
	private void initMisc() {
		
		txtXmltvDirectory.setText( FreeGuide.prefs.misc.get("xmltv_directory") );
        txtWorkingDirectory.setText( FreeGuide.prefs.misc.get("working_directory") );
        txtCssFile.setText( FreeGuide.prefs.misc.get("css_file", "") );
		txtGrabberConfig.setText( FreeGuide.prefs.misc.get("grabber_config") );
		
	}
	
	private void initChannels() {
		
		panChannels.removeAll();
		
		java.awt.GridBagConstraints gridBagConstraints;
		JCheckBox box;
		
		String[] channelIDs = FreeGuide.prefs.getAllChannelIDs();
		
		int i;
		for(i=0;i<channelIDs.length;i++) {
			
			// If it's commented out, non-ticked box, otherwise ticked
			String thisChan = channelIDs[i];
			if(thisChan.charAt(0)=='#') {
				box = new JCheckBox(thisChan.substring(1), false);
			} else {
				box = new JCheckBox(thisChan, !FreeGuide.prefs.channels.get(thisChan, "").equals("#"));
			}
			
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = i;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
			panChannels.add(box, gridBagConstraints);
			
		}
		
		butEditChannels = new JButton("Refresh Channels");
		butEditChannels.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = i;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		panChannels.add(butEditChannels, gridBagConstraints);
		
	}
	
	private void doColorDialog(JTextField txt) {
		
		Color col = JColorChooser.showDialog(this, "Choose a Colour", txt.getBackground());
		if(col!=null) {
			fillTextAreaFromColor(txt, col);
		}
		
	}
	
	// -----------------------------------------------------------------------
	
	/** 
	 * Puts each line of a JTextArea into an array of Strings.
	 *
	 * @param txtArea the jtextarea
	 * @return        the String array for each line of txtArea
	 */
	private String[] getArrayFromJTextArea(JTextArea txtArea) {
		
		// Get the system line break character
		String lineBreak = System.getProperty("line.separator");
		
		// Get the text out of the text box
		String txt = txtArea.getText();
		
		// Initialise the vector we'll return
		Vector ans = new Vector();
		
		// Find the first line break
		int i = txt.indexOf(lineBreak);
		
		// Loop until no more line breaks were found
		while(i>-1) {
			
			// Cut out blank lines
			if(i>0) {
			
				// Add the first line in the string to the vector
				ans.add(txt.substring(0, i));
				
			}//if
			
			// Cut this line out of the vector
			txt = txt.substring(i+lineBreak.length());
			
			// Find the next line break
			i = txt.indexOf(lineBreak);
			
		}//while
		
		if(!txt.equals("")) {
			ans.add(txt);
		}
		
		return FreeGuideUtils.arrayFromVector_String(ans);
		
	}//getArrayFromJTextArea
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPanel = new javax.swing.JTabbedPane();
        panDefaults = new javax.swing.JPanel();
        labDefaultChooseOS = new javax.swing.JLabel();
        cmbDefaultChooseOS = new javax.swing.JComboBox();
        butDefaultDoIt = new javax.swing.JButton();
        labDefaultChooseCountry = new javax.swing.JLabel();
        cmbDefaultChooseCountry = new javax.swing.JComboBox();
        labDefaultChooseBrowser = new javax.swing.JLabel();
        cmbDefaultChooseBrowser = new javax.swing.JComboBox();
        tabChannels = new javax.swing.JPanel();
        scrChannels = new javax.swing.JScrollPane();
        panChannels = new javax.swing.JPanel();
        tabMisc = new javax.swing.JPanel();
        scrMisc = new javax.swing.JScrollPane();
        panMisc = new javax.swing.JPanel();
        labXmltvDirectory = new javax.swing.JLabel();
        txtXmltvDirectory = new javax.swing.JTextField();
        labWorkingDirectory = new javax.swing.JLabel();
        txtWorkingDirectory = new javax.swing.JTextField();
        labCssFile = new javax.swing.JLabel();
        txtCssFile = new javax.swing.JTextField();
        labGrabberConfig = new javax.swing.JLabel();
        txtGrabberConfig = new javax.swing.JTextField();
        tabScreen = new javax.swing.JPanel();
        scrScreen = new javax.swing.JScrollPane();
        panScreen = new javax.swing.JPanel();
        labChannelColour = new javax.swing.JLabel();
        txtChannelColour = new javax.swing.JTextField();
        labProgrammeNormalColour = new javax.swing.JLabel();
        txtProgrammeNormalColour = new javax.swing.JTextField();
        labProgrammeChosenColour = new javax.swing.JLabel();
        txtProgrammeChosenColour = new javax.swing.JTextField();
        labChannelHeight = new javax.swing.JLabel();
        txtChannelHeight = new javax.swing.JTextField();
        labVerticalGap = new javax.swing.JLabel();
        txtVerticalGap = new javax.swing.JTextField();
        labHorizontalGap = new javax.swing.JLabel();
        txtHorizontalGap = new javax.swing.JTextField();
        labPanelWidth = new javax.swing.JLabel();
        txtPanelWidth = new javax.swing.JTextField();
        labChannelPanelWidth = new javax.swing.JLabel();
        txtChannelPanelWidth = new javax.swing.JTextField();
        tabCommandLine = new javax.swing.JPanel();
        scrCommandLine = new javax.swing.JScrollPane();
        panCommandLine = new javax.swing.JPanel();
        labTvGrab = new javax.swing.JLabel();
        scrTvGrab = new javax.swing.JScrollPane();
        txaTvGrab = new javax.swing.JTextArea();
        labBrowserCommand = new javax.swing.JLabel();
        scrBrowserCommand = new javax.swing.JScrollPane();
        txaBrowserCommand = new javax.swing.JTextArea();
        panButtons = new javax.swing.JPanel();
        butOK = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("FreeGuide Options Screen");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        panDefaults.setLayout(new java.awt.GridBagLayout());

        labDefaultChooseOS.setText("Choose OS:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDefaults.add(labDefaultChooseOS, gridBagConstraints);

        FreeGuideUtils.addOSsToComboBox(cmbDefaultChooseOS);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDefaults.add(cmbDefaultChooseOS, gridBagConstraints);

        butDefaultDoIt.setText("Set default options");
        butDefaultDoIt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDefaultDoItActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDefaults.add(butDefaultDoIt, gridBagConstraints);

        labDefaultChooseCountry.setText("Choose Browser:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDefaults.add(labDefaultChooseCountry, gridBagConstraints);

        FreeGuideUtils.addCountriesToComboBox(cmbDefaultChooseCountry);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDefaults.add(cmbDefaultChooseCountry, gridBagConstraints);

        labDefaultChooseBrowser.setText("Choose Region:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDefaults.add(labDefaultChooseBrowser, gridBagConstraints);

        FreeGuideUtils.addBrowsersToComboBox(cmbDefaultChooseBrowser);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDefaults.add(cmbDefaultChooseBrowser, gridBagConstraints);

        tabbedPanel.addTab("Defaults", panDefaults);

        tabChannels.setLayout(new java.awt.BorderLayout());

        panChannels.setLayout(new java.awt.GridBagLayout());

        scrChannels.setViewportView(panChannels);

        tabChannels.add(scrChannels, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Channels", tabChannels);

        tabMisc.setLayout(new java.awt.BorderLayout());

        panMisc.setLayout(new java.awt.GridBagLayout());

        labXmltvDirectory.setText("XMLTV Directory");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(labXmltvDirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(txtXmltvDirectory, gridBagConstraints);

        labWorkingDirectory.setText("Working Directory");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(labWorkingDirectory, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(txtWorkingDirectory, gridBagConstraints);

        labCssFile.setText("Stylesheet file");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(labCssFile, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(txtCssFile, gridBagConstraints);

        labGrabberConfig.setText("Grabber Config File");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(labGrabberConfig, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMisc.add(txtGrabberConfig, gridBagConstraints);

        scrMisc.setViewportView(panMisc);

        tabMisc.add(scrMisc, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Misc", tabMisc);

        tabScreen.setLayout(new java.awt.BorderLayout());

        panScreen.setLayout(new java.awt.GridBagLayout());

        labChannelColour.setText("Channel Background Colour");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labChannelColour, gridBagConstraints);

        txtChannelColour.setEditable(false);
        txtChannelColour.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtChannelColour.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtChannelColourMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtChannelColour, gridBagConstraints);

        labProgrammeNormalColour.setText("Programme Background Colour");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labProgrammeNormalColour, gridBagConstraints);

        txtProgrammeNormalColour.setEditable(false);
        txtProgrammeNormalColour.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtProgrammeNormalColour.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtProgrammeNormalColourMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtProgrammeNormalColour, gridBagConstraints);

        labProgrammeChosenColour.setText("Chosen Programme Colour");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labProgrammeChosenColour, gridBagConstraints);

        txtProgrammeChosenColour.setEditable(false);
        txtProgrammeChosenColour.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtProgrammeChosenColour.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtProgrammeChosenColourMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtProgrammeChosenColour, gridBagConstraints);

        labChannelHeight.setText("Channel Height");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labChannelHeight, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtChannelHeight, gridBagConstraints);

        labVerticalGap.setText("Vertical Gap");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labVerticalGap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtVerticalGap, gridBagConstraints);

        labHorizontalGap.setText("Horizontal Gap");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labHorizontalGap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtHorizontalGap, gridBagConstraints);

        labPanelWidth.setText("Panel Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labPanelWidth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtPanelWidth, gridBagConstraints);

        labChannelPanelWidth.setText("Channel Panel Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(labChannelPanelWidth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panScreen.add(txtChannelPanelWidth, gridBagConstraints);

        scrScreen.setViewportView(panScreen);

        tabScreen.add(scrScreen, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Screen", tabScreen);

        tabCommandLine.setLayout(new java.awt.BorderLayout());

        panCommandLine.setLayout(new java.awt.GridBagLayout());

        panCommandLine.setMaximumSize(new java.awt.Dimension(0, 0));
        panCommandLine.setMinimumSize(new java.awt.Dimension(0, 0));
        panCommandLine.setPreferredSize(new java.awt.Dimension(0, 0));
        labTvGrab.setText("Grab TV listings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panCommandLine.add(labTvGrab, gridBagConstraints);

        scrTvGrab.setMaximumSize(new java.awt.Dimension(200, 75));
        scrTvGrab.setMinimumSize(new java.awt.Dimension(0, 0));
        scrTvGrab.setPreferredSize(new java.awt.Dimension(200, 75));
        txaTvGrab.setColumns(1000);
        txaTvGrab.setRows(10);
        txaTvGrab.setMinimumSize(new java.awt.Dimension(0, 0));
        txaTvGrab.setPreferredSize(new java.awt.Dimension(11000, 1600));
        scrTvGrab.setViewportView(txaTvGrab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panCommandLine.add(scrTvGrab, gridBagConstraints);

        labBrowserCommand.setText("Launch web browser");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panCommandLine.add(labBrowserCommand, gridBagConstraints);

        scrBrowserCommand.setMaximumSize(new java.awt.Dimension(200, 75));
        scrBrowserCommand.setPreferredSize(new java.awt.Dimension(200, 75));
        txaBrowserCommand.setColumns(1000);
        txaBrowserCommand.setRows(10);
        txaBrowserCommand.setMinimumSize(new java.awt.Dimension(0, 0));
        scrBrowserCommand.setViewportView(txaBrowserCommand);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panCommandLine.add(scrBrowserCommand, gridBagConstraints);

        scrCommandLine.setViewportView(panCommandLine);

        tabCommandLine.add(scrCommandLine, java.awt.BorderLayout.CENTER);

        tabbedPanel.addTab("Commands", tabCommandLine);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        getContentPane().add(tabbedPanel, gridBagConstraints);

        panButtons.setLayout(new java.awt.GridBagLayout());

        butOK.setText("OK");
        butOK.setMinimumSize(new java.awt.Dimension(88, 26));
        butOK.setPreferredSize(new java.awt.Dimension(88, 26));
        butOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOKActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panButtons.add(butOK, gridBagConstraints);

        butCancel.setText("Cancel");
        butCancel.setMinimumSize(new java.awt.Dimension(88, 26));
        butCancel.setPreferredSize(new java.awt.Dimension(88, 26));
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons.add(butCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(panButtons, gridBagConstraints);

        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(400, 300));
        setLocation((screenSize.width-400)/2,(screenSize.height-300)/2);
    }//GEN-END:initComponents

	private void butDefaultDoItActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDefaultDoItActionPerformed
		
		FreeGuideUtils.setDefaultOptions( (String)cmbDefaultChooseOS.getSelectedItem(), (String)cmbDefaultChooseCountry.getSelectedItem(), (String)cmbDefaultChooseBrowser.getSelectedItem() );
		initMyComponents();
		
	}//GEN-LAST:event_butDefaultDoItActionPerformed

	private void txtChannelColourMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtChannelColourMouseClicked
		doColorDialog(txtChannelColour);
	}//GEN-LAST:event_txtChannelColourMouseClicked

	private void txtProgrammeChosenColourMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProgrammeChosenColourMouseClicked
		doColorDialog(txtProgrammeChosenColour);
	}//GEN-LAST:event_txtProgrammeChosenColourMouseClicked

	private void txtProgrammeNormalColourMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProgrammeNormalColourMouseClicked
		doColorDialog(txtProgrammeNormalColour);
	}//GEN-LAST:event_txtProgrammeNormalColourMouseClicked

	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
		quit();
	}//GEN-LAST:event_butCancelActionPerformed

	private void butOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOKActionPerformed
		savePrefs();
		quit();
	}//GEN-LAST:event_butOKActionPerformed
	
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
		quit();
	}//GEN-LAST:event_exitForm
	
	/**
	 * Go through and take the values from each box and write them out to the
	 * preferences.
	 */
	private void savePrefs() {
		
		saveChannels();
		saveMisc();
		saveScreen();
		saveCommands();
		
		FreeGuide.prefs.flushAll();
		
	}
	
	private void saveCommands() {
		
		FreeGuide.prefs.commandline.putStrings("tv_grab", getArrayFromJTextArea(txaTvGrab));
		FreeGuide.prefs.commandline.putStrings("browser_command", getArrayFromJTextArea(txaBrowserCommand));
		
	}
	
	private void saveScreen() {
		
		FreeGuide.prefs.screen.putColor("channel_colour", txtChannelColour.getBackground());
		
		FreeGuide.prefs.screen.putInt("channel_height", Integer.parseInt(txtChannelHeight.getText()));
		FreeGuide.prefs.screen.putInt("channel_panel_width", Integer.parseInt(txtChannelPanelWidth.getText()));
		FreeGuide.prefs.screen.putInt("horizontal_gap", Integer.parseInt(txtHorizontalGap.getText()));
		FreeGuide.prefs.screen.putInt("vertical_gap", Integer.parseInt(txtVerticalGap.getText()));
		FreeGuide.prefs.screen.putInt("panel_width", Integer.parseInt(txtPanelWidth.getText()));
		
		FreeGuide.prefs.screen.putColor("programme_chosen_colour", txtProgrammeChosenColour.getBackground());
		FreeGuide.prefs.screen.putColor("programme_normal_colour", txtProgrammeNormalColour.getBackground());
		
	}
	
	private void saveMisc() {
		
		FreeGuide.prefs.misc.put("xmltv_directory", txtXmltvDirectory.getText());
        FreeGuide.prefs.misc.put("working_directory", txtWorkingDirectory.getText());
        FreeGuide.prefs.misc.put("css_file", txtCssFile.getText());
		FreeGuide.prefs.misc.put("grabber_config", txtGrabberConfig.getText());
		
	}
	
	private void saveChannels() {
		
		Component[] chks = panChannels.getComponents();
		
		Vector channelIDs = new Vector();
		Vector commented = new Vector();
		
		for(int i=0;i<chks.length;i++) {
			
			if(chks[i] instanceof JCheckBox) {
			
				JCheckBox bx = (JCheckBox)chks[i];
			
				channelIDs.add(bx.getText());
			
				if(bx.isSelected()) {
					commented.add("");
				} else {
					commented.add("#");
				}
			
			}//for
		
		}
			
		FreeGuide.prefs.putAllChannelIDs(FreeGuideUtils.arrayFromVector_String(channelIDs), FreeGuideUtils.arrayFromVector_String(commented));
		
	}
	
	/** 
	 * Closes the form and goes back to the viewer.
	 */
	private void quit() {
		
		hide();
		launcher.reShow();
		dispose();

	}
	
	public FreeGuideLauncher getLauncher() {
		return launcher;
	}
	
	public void reShow() {
		show();
	}
	
	/*public static void main(String[] args) {
		new FreeGuideOptions(null).show();
	}*/
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labDefaultChooseBrowser;
    private javax.swing.JLabel labChannelHeight;
    private javax.swing.JTextField txtChannelHeight;
    private javax.swing.JLabel labChannelPanelWidth;
    private javax.swing.JTextField txtChannelPanelWidth;
    private javax.swing.JTextField txtVerticalGap;
    private javax.swing.JLabel labBrowserCommand;
    private javax.swing.JLabel labDefaultChooseCountry;
    private javax.swing.JLabel labWorkingDirectory;
    private javax.swing.JPanel panScreen;
    private javax.swing.JPanel tabScreen;
    private javax.swing.JScrollPane scrCommandLine;
    private javax.swing.JButton butCancel;
    private javax.swing.JLabel labTvGrab;
    private javax.swing.JLabel labGrabberConfig;
    private javax.swing.JTextField txtGrabberConfig;
    private javax.swing.JLabel labCssFile;
    private javax.swing.JLabel labDefaultChooseOS;
    private javax.swing.JTextField txtWorkingDirectory;
    private javax.swing.JScrollPane scrBrowserCommand;
    private javax.swing.JComboBox cmbDefaultChooseOS;
    private javax.swing.JLabel labProgrammeNormalColour;
    private javax.swing.JTextArea txaBrowserCommand;
    private javax.swing.JTextField txtProgrammeNormalColour;
    private javax.swing.JButton butDefaultDoIt;
    private javax.swing.JLabel labVerticalGap;
    private javax.swing.JTextField txtPanelWidth;
    private javax.swing.JScrollPane scrTvGrab;
    private javax.swing.JScrollPane scrMisc;
    private javax.swing.JScrollPane scrScreen;
    private javax.swing.JTextArea txaTvGrab;
    private javax.swing.JPanel panMisc;
    private javax.swing.JComboBox cmbDefaultChooseBrowser;
    private javax.swing.JPanel panButtons;
    private javax.swing.JTextField txtXmltvDirectory;
    private javax.swing.JLabel labChannelColour;
    private javax.swing.JPanel tabMisc;
    private javax.swing.JTextField txtChannelColour;
    private javax.swing.JButton butOK;
    private javax.swing.JTabbedPane tabbedPanel;
    private javax.swing.JComboBox cmbDefaultChooseCountry;
    private javax.swing.JScrollPane scrChannels;
    private javax.swing.JPanel panDefaults;
    private javax.swing.JTextField txtCssFile;
    private javax.swing.JLabel labPanelWidth;
    private javax.swing.JPanel panChannels;
    private javax.swing.JLabel labProgrammeChosenColour;
    private javax.swing.JTextField txtProgrammeChosenColour;
    private javax.swing.JPanel tabCommandLine;
    private javax.swing.JPanel tabChannels;
    private javax.swing.JLabel labHorizontalGap;
    private javax.swing.JTextField txtHorizontalGap;
    private javax.swing.JPanel panCommandLine;
    private javax.swing.JLabel labXmltvDirectory;
    // End of variables declaration//GEN-END:variables
	
	private FreeGuideLauncher launcher;
	
	private JButton butEditChannels;
	
}

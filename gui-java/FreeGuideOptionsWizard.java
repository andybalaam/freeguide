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

import java.awt.Component;
import java.io.File;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/*
 * Configuration via a simple wizard interface.  Used on initial startup, 
 * when there's a problem on startup, or when the user chooses "Options".
 *
 * @author  Andy Balaam
 * @version 3
 */
public class FreeGuideOptionsWizard extends javax.swing.JFrame {
	
	public FreeGuideOptionsWizard(FreeGuideLauncher launcher) {
		this(launcher, SCREEN_OPTIONS, true, true, true, true, true, true, true, true, true, true);
	}
	
	public FreeGuideOptionsWizard(FreeGuideLauncher launcher, int frontScreen, 
		boolean failOS, boolean failCountry, boolean failBrowserName, 
		boolean failXMLTVCmdDir, boolean failWorkingDir, boolean failGrabber,
		boolean failBrowser, boolean failDayStartTime,
		boolean failStyleSheet, boolean failXMLTVCfg) {
	
		this.launcher = launcher;
		prevTab = 0;
		
		initComponents();
		
		// Set up the front screen
		switch(frontScreen) {
			case SCREEN_FIRST_TIME:	// First time wizard
				tabs.remove(panProblem);
				tabs.remove(panOptions);
				tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
				break;
			case SCREEN_PROBLEM:
				tabs.remove(panFirstTime);
				tabs.remove(panOptions);
				tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
				break;
			case SCREEN_OPTIONS:
				tabs.remove(panFirstTime);
				tabs.remove(panProblem);
				break;
			default:
				System.out.println("Wrong front screen option offered to FreeGuideOptionsWizard constructor");
		}
		
		if(!failOS)				{tabs.remove(panOS);}
		if(!failCountry)		{tabs.remove(panCountry);}
		if(!failBrowserName)	{tabs.remove(panBrowserName);}
		if(!failXMLTVCmdDir)	{tabs.remove(panXMLTVCmdDir);}
		if(!failWorkingDir)		{tabs.remove(panWorkingDir);}
		if(!failGrabber)		{tabs.remove(panGrabber);}
		if(!failBrowser)		{tabs.remove(panBrowser);}
		if(!failDayStartTime)	{tabs.remove(panDayStartTime);}
		if(!failStyleSheet)		{tabs.remove(panStyleSheet);}
		if(!failXMLTVCfg)		{tabs.remove(panXMLTVCfg);}
		
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tabs = new javax.swing.JTabbedPane();
        panFirstTime = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        panProblem = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel211 = new javax.swing.JLabel();
        panOptions = new javax.swing.JPanel();
        jLabel117 = new javax.swing.JLabel();
        jLabel237 = new javax.swing.JLabel();
        jLabel2117 = new javax.swing.JLabel();
        panOS = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        cmbOS = new javax.swing.JComboBox();
        panCountry = new javax.swing.JPanel();
        jLabel222 = new javax.swing.JLabel();
        cmbCountry = new javax.swing.JComboBox();
        panBrowserName = new javax.swing.JPanel();
        jLabel2221 = new javax.swing.JLabel();
        cmbBrowserName = new javax.swing.JComboBox();
        panWorkingDir = new javax.swing.JPanel();
        jLabel1112 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        txtWorkingDir = new javax.swing.JTextField();
        butBrowseWorkingDir = new javax.swing.JButton();
        jLabel11121 = new javax.swing.JLabel();
        panXMLTVCmdDir = new javax.swing.JPanel();
        jLabel111 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        txtXMLTVCmdDir = new javax.swing.JTextField();
        butBrowseXMLTVCmdDir = new javax.swing.JButton();
        panGrabber = new javax.swing.JPanel();
        jLabel112 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaGrabber = new javax.swing.JTextArea();
        jLabel2112 = new javax.swing.JLabel();
        panBrowser = new javax.swing.JPanel();
        jLabel1121 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        txaBrowser = new javax.swing.JTextArea();
        jLabel21121 = new javax.swing.JLabel();
        panDayStartTime = new javax.swing.JPanel();
        jLabel11122 = new javax.swing.JLabel();
        jPanel121 = new javax.swing.JPanel();
        txtDayStartTime = new javax.swing.JTextField();
        jLabel111211 = new javax.swing.JLabel();
        panStyleSheet = new javax.swing.JPanel();
        jLabel11112 = new javax.swing.JLabel();
        jPanel111 = new javax.swing.JPanel();
        txtStyleSheet = new javax.swing.JTextField();
        butBrowseXMLTVCfg1 = new javax.swing.JButton();
        panXMLTVCfg = new javax.swing.JPanel();
        jLabel1111 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        txtXMLTVCfg = new javax.swing.JTextField();
        butBrowseXMLTVCfg = new javax.swing.JButton();
        jLabel11111 = new javax.swing.JLabel();
        panFinish = new javax.swing.JPanel();
        jLabel116 = new javax.swing.JLabel();
        jLabel236 = new javax.swing.JLabel();
        jLabel2116 = new javax.swing.JLabel();
        panButtons = new javax.swing.JPanel();
        butCancel = new javax.swing.JButton();
        butBack = new javax.swing.JButton();
        butNext = new javax.swing.JButton();
        butFinish = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("FreeGuide Options Wizard");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        tabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tabsStateChanged(evt);
            }
        });

        panFirstTime.setLayout(new java.awt.GridLayout(3, 0));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome to FreeGuide!");
        panFirstTime.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("I need to ask you some quick questions before we begin.");
        panFirstTime.add(jLabel2);

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Click \"Next\" to continue.");
        panFirstTime.add(jLabel21);

        tabs.addTab("Welcome", panFirstTime);

        panProblem.setLayout(new java.awt.GridLayout(3, 0));

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Configuration Problem");
        panProblem.add(jLabel11);

        jLabel23.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Something has gone strange with your configuration.");
        panProblem.add(jLabel23);

        jLabel211.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel211.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel211.setText("Please answer the following question(s) to fix it up (click \"Next\").");
        panProblem.add(jLabel211);

        tabs.addTab("Welcome", panProblem);

        panOptions.setLayout(new java.awt.GridLayout(3, 0));

        jLabel117.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel117.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel117.setText("Options Wizard");
        panOptions.add(jLabel117);

        jLabel237.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel237.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel237.setText("Answer the questions or use the tabs to alter your setup.");
        panOptions.add(jLabel237);

        jLabel2117.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2117.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2117.setText("Click \"Next\" to begin.");
        panOptions.add(jLabel2117);

        tabs.addTab("Welcome", panOptions);

        panOS.setLayout(new java.awt.GridBagLayout());

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("What operating system are you using?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 0.5;
        panOS.add(jLabel22, gridBagConstraints);

        cmbOS.setFont(new java.awt.Font("Dialog", 0, 12));
        cmbOS.setMinimumSize(new java.awt.Dimension(200, 25));
        cmbOS.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.5;
        panOS.add(cmbOS, gridBagConstraints);

        tabs.addTab("OS", panOS);

        panCountry.setLayout(new java.awt.GridBagLayout());

        jLabel222.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel222.setText("What region are you in?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 0.5;
        panCountry.add(jLabel222, gridBagConstraints);

        cmbCountry.setFont(new java.awt.Font("Dialog", 0, 12));
        cmbCountry.setMinimumSize(new java.awt.Dimension(200, 25));
        cmbCountry.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.5;
        panCountry.add(cmbCountry, gridBagConstraints);

        tabs.addTab("Country", panCountry);

        panBrowserName.setLayout(new java.awt.GridBagLayout());

        jLabel2221.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2221.setText("What browser do you use?");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weighty = 0.5;
        panBrowserName.add(jLabel2221, gridBagConstraints);

        cmbBrowserName.setFont(new java.awt.Font("Dialog", 0, 12));
        cmbBrowserName.setMinimumSize(new java.awt.Dimension(200, 25));
        cmbBrowserName.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 0.5;
        panBrowserName.add(cmbBrowserName, gridBagConstraints);

        tabs.addTab("Browser", panBrowserName);

        panWorkingDir.setLayout(new java.awt.GridLayout(3, 0));

        jLabel1112.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1112.setText("Choose a writeable directory as FreeGuide's working space:");
        panWorkingDir.add(jLabel1112);

        jPanel12.setLayout(new java.awt.GridBagLayout());

        txtWorkingDir.setMinimumSize(new java.awt.Dimension(4, 26));
        txtWorkingDir.setPreferredSize(new java.awt.Dimension(69, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtWorkingDir, gridBagConstraints);

        butBrowseWorkingDir.setFont(new java.awt.Font("Dialog", 0, 12));
        butBrowseWorkingDir.setText("Browse...");
        butBrowseWorkingDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrowseWorkingDirActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel12.add(butBrowseWorkingDir, gridBagConstraints);

        panWorkingDir.add(jPanel12);

        jLabel11121.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel11121.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11121.setText("This directory will be created if it doesn't exist.");
        panWorkingDir.add(jLabel11121);

        tabs.addTab("Working Directory", panWorkingDir);

        panXMLTVCmdDir.setLayout(new java.awt.GridLayout(2, 0));

        jLabel111.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel111.setText("Choose the directory into which the XMLTV tools were installed:");
        panXMLTVCmdDir.add(jLabel111);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        txtXMLTVCmdDir.setMinimumSize(new java.awt.Dimension(4, 26));
        txtXMLTVCmdDir.setPreferredSize(new java.awt.Dimension(69, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtXMLTVCmdDir, gridBagConstraints);

        butBrowseXMLTVCmdDir.setFont(new java.awt.Font("Dialog", 0, 12));
        butBrowseXMLTVCmdDir.setText("Browse...");
        butBrowseXMLTVCmdDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrowseXMLTVCmdDirActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel1.add(butBrowseXMLTVCmdDir, gridBagConstraints);

        panXMLTVCmdDir.add(jPanel1);

        tabs.addTab("XMLTV Directory", panXMLTVCmdDir);

        panGrabber.setLayout(new java.awt.GridLayout(3, 0));

        jLabel112.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel112.setText("Choose the command to download listings:");
        panGrabber.add(jLabel112);

        jScrollPane1.setViewportView(txaGrabber);

        panGrabber.add(jScrollPane1);

        jLabel2112.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2112.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2112.setText("You probably won't need to alter this.");
        panGrabber.add(jLabel2112);

        tabs.addTab("Grabber Command", panGrabber);

        panBrowser.setLayout(new java.awt.GridLayout(3, 0));

        jLabel1121.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1121.setText("Choose the command to launch your web browser:");
        panBrowser.add(jLabel1121);

        jScrollPane11.setViewportView(txaBrowser);

        panBrowser.add(jScrollPane11);

        jLabel21121.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel21121.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21121.setText("You probably won't need to alter this.");
        panBrowser.add(jLabel21121);

        tabs.addTab("Browser Command", panBrowser);

        panDayStartTime.setLayout(new java.awt.GridLayout(3, 0));

        jLabel11122.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11122.setText("Choose a time when days start and end:");
        panDayStartTime.add(jLabel11122);

        jPanel121.setLayout(new java.awt.GridBagLayout());

        txtDayStartTime.setMinimumSize(new java.awt.Dimension(4, 26));
        txtDayStartTime.setPreferredSize(new java.awt.Dimension(69, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel121.add(txtDayStartTime, gridBagConstraints);

        panDayStartTime.add(jPanel121);

        jLabel111211.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel111211.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel111211.setText("Time format is hh:mm");
        panDayStartTime.add(jLabel111211);

        tabs.addTab("Day Start", panDayStartTime);

        panStyleSheet.setLayout(new java.awt.GridLayout(2, 0));

        jLabel11112.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11112.setText("Choose a style sheet file for your TV listings:");
        panStyleSheet.add(jLabel11112);

        jPanel111.setLayout(new java.awt.GridBagLayout());

        txtStyleSheet.setMinimumSize(new java.awt.Dimension(4, 26));
        txtStyleSheet.setPreferredSize(new java.awt.Dimension(69, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel111.add(txtStyleSheet, gridBagConstraints);

        butBrowseXMLTVCfg1.setFont(new java.awt.Font("Dialog", 0, 12));
        butBrowseXMLTVCfg1.setText("Browse...");
        butBrowseXMLTVCfg1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrowseXMLTVCfg1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel111.add(butBrowseXMLTVCfg1, gridBagConstraints);

        panStyleSheet.add(jPanel111);

        tabs.addTab("Style Sheet", panStyleSheet);

        panXMLTVCfg.setLayout(new java.awt.GridLayout(3, 0));

        jLabel1111.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1111.setText("Choose the XMLTV grabber config file:");
        panXMLTVCfg.add(jLabel1111);

        jPanel11.setLayout(new java.awt.GridBagLayout());

        txtXMLTVCfg.setMinimumSize(new java.awt.Dimension(4, 26));
        txtXMLTVCfg.setPreferredSize(new java.awt.Dimension(69, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(txtXMLTVCfg, gridBagConstraints);

        butBrowseXMLTVCfg.setFont(new java.awt.Font("Dialog", 0, 12));
        butBrowseXMLTVCfg.setText("Browse...");
        butBrowseXMLTVCfg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrowseXMLTVCfgActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel11.add(butBrowseXMLTVCfg, gridBagConstraints);

        panXMLTVCfg.add(jPanel11);

        jLabel11111.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel11111.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11111.setText("To make this you need to run your grabber with the --configure option.");
        panXMLTVCfg.add(jLabel11111);

        tabs.addTab("XMLTV Config", panXMLTVCfg);

        panFinish.setLayout(new java.awt.GridLayout(3, 0));

        jLabel116.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel116.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel116.setText("Congratulations!");
        panFinish.add(jLabel116);

        jLabel236.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel236.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel236.setText("You've finished the configuration process.");
        panFinish.add(jLabel236);

        jLabel2116.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2116.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2116.setText("If something doesn't work, click \"Tools\" and then \"Options\" to fix it.");
        panFinish.add(jLabel2116);

        tabs.addTab("Finish", panFinish);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        getContentPane().add(tabs, gridBagConstraints);

        butCancel.setFont(new java.awt.Font("Dialog", 0, 12));
        butCancel.setText("Exit");
        butCancel.setMaximumSize(new java.awt.Dimension(85, 26));
        butCancel.setMinimumSize(new java.awt.Dimension(85, 26));
        butCancel.setPreferredSize(new java.awt.Dimension(85, 26));
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        panButtons.add(butCancel);

        butBack.setFont(new java.awt.Font("Dialog", 0, 12));
        butBack.setText("<< Back");
        butBack.setEnabled(false);
        butBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBackActionPerformed(evt);
            }
        });

        panButtons.add(butBack);

        butNext.setFont(new java.awt.Font("Dialog", 0, 12));
        butNext.setText("Next >>");
        butNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNextActionPerformed(evt);
            }
        });

        panButtons.add(butNext);

        butFinish.setFont(new java.awt.Font("Dialog", 0, 12));
        butFinish.setText("Finish");
        butFinish.setMaximumSize(new java.awt.Dimension(85, 26));
        butFinish.setMinimumSize(new java.awt.Dimension(85, 26));
        butFinish.setPreferredSize(new java.awt.Dimension(85, 26));
        butFinish.setEnabled(false);
        butFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFinishActionPerformed(evt);
            }
        });

        panButtons.add(butFinish);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(panButtons, gridBagConstraints);

        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(500, 350));
        setLocation((screenSize.width-500)/2,(screenSize.height-350)/2);
    }//GEN-END:initComponents

	private void butBrowseXMLTVCfg1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrowseXMLTVCfg1ActionPerformed
		browse(txtStyleSheet, false);
	}//GEN-LAST:event_butBrowseXMLTVCfg1ActionPerformed

	private void butBrowseXMLTVCfgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrowseXMLTVCfgActionPerformed
		browse(txtXMLTVCfg, false);
	}//GEN-LAST:event_butBrowseXMLTVCfgActionPerformed

	private void butBrowseXMLTVCmdDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrowseXMLTVCmdDirActionPerformed
		browse(txtXMLTVCmdDir, true);
	}//GEN-LAST:event_butBrowseXMLTVCmdDirActionPerformed

	private void butBrowseWorkingDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrowseWorkingDirActionPerformed
		browse(txtWorkingDir, true);
	}//GEN-LAST:event_butBrowseWorkingDirActionPerformed

	private boolean panStyleSheetDeparted() {
		txtTabDeparted(txtStyleSheet, "css_file");
		return true;
	}

	private void panStyleSheetArrived() {
		txtTabArrived(txtStyleSheet, "css_file");
	}

	private boolean panDayStartTimeDeparted() {
		FreeGuide.prefs.misc.putFreeGuideTime("day_start_time", new FreeGuideTime(txtDayStartTime.getText()));
		return true;
	}

	private void panDayStartTimeArrived() {
		txtDayStartTime.setText(FreeGuide.prefs.misc.getFreeGuideTime("day_start_time", new FreeGuideTime(6,00)).getHHMMString());
	}

	private boolean panWorkingDirDeparted() {
		
		// Create this dir if it doesn't exist
		File workDir = new File(FreeGuide.prefs.performSubstitutions(txtWorkingDir.getText()));
		
		if(!workDir.exists()) {
			//try {
			workDir.mkdirs();
			//} catch(java.io.IOException e) {
//				e.printStackTrace();
//			}
		}
		
		// Save it to the preferences
		txtTabDeparted(txtWorkingDir, "working_directory");
		
		// Check this is right
		if(!FreeGuideStartupChecker.checkWorkingDir()) {
			
			String msg = "The working directory you chose either doesn't exist\n" +
				"or isn't writeable.  Do you want to continue anyway?";
			int ignore = JOptionPane.showConfirmDialog(this, msg, "Error", JOptionPane.YES_NO_OPTION );
			
			if(ignore == JOptionPane.NO_OPTION) {
				return false;
			}
		}
		return true;
	}

	private void panWorkingDirArrived() {
		txtTabArrived(txtWorkingDir, "working_directory");
	}

	private boolean panXMLTVCfgDeparted() {
		// Save it to the preferences
		txtTabDeparted(txtXMLTVCfg, "grabber_config");
		
		// Check this is right
		if(!FreeGuideStartupChecker.checkXMLTVCfg()) {
			
			String msg = "The config file you chose either doesn't exist\n" +
				"or doesn't contain any channels.  Do you want to continue anyway?";
			int ignore = JOptionPane.showConfirmDialog(this, msg, "Error", JOptionPane.YES_NO_OPTION );
			
			if(ignore == JOptionPane.NO_OPTION) {
				return false;
			}
			
		}
		return true;
	}

	private void panXMLTVCfgArrived() {
		txtTabArrived(txtXMLTVCfg, "grabber_config");
	}

	private boolean panBrowserDeparted() {
		txaTabDeparted(txaBrowser, "browser_command");
		return true;
	}

	private void panBrowserArrived() {
		txaTabArrived(txaBrowser, "browser_command");
	}

	private boolean panGrabberDeparted() {
		txaTabDeparted(txaGrabber, "tv_grab");
		return true;
	}

	private void panGrabberArrived() {
		txaTabArrived(txaGrabber, "tv_grab");
	}

	private boolean panXMLTVCmdDirDeparted() {
		// Save it to the preferences
		txtTabDeparted(txtXMLTVCmdDir, "xmltv_directory");
		
		// Check this is right
		if(!FreeGuideStartupChecker.checkXMLTVCmdDir()) {
			
			String msg = "This directory does not exist or does not contain the\n" + 
				"tv_split command, which is needed by FreeGuide.\n" + 
				"Do you want to continue anyway?";
			int ignore = JOptionPane.showConfirmDialog(this, msg, "Error", JOptionPane.YES_NO_OPTION );
			
			if(ignore == JOptionPane.NO_OPTION) {
				return false;
			}
			
		}
		return true;
	}

	private void panXMLTVCmdDirArrived() {
		txtTabArrived(txtXMLTVCmdDir, "xmltv_directory");
	}

	private boolean panBrowserNameDeparted() {
		comboTabDeparted(cmbBrowserName, "browser_name");
		return true;
	}

	private void panBrowserNameArrived() {
		comboTabArrived(cmbBrowserName, "browser_name");
	}

	private boolean panCountryDeparted() {
		comboTabDeparted(cmbCountry, "country");		
		return true;
	}

	private void panCountryArrived() {
		comboTabArrived(cmbCountry, "country");
	}

	private boolean panOSDeparted() {
		comboTabDeparted(cmbOS, "os");
		return true;
	}

	private void panOSArrived() {
		comboTabArrived(cmbOS, "os");
	}
	
	private boolean txaTabDeparted(JTextArea txa, String name) {
		FreeGuide.prefs.commandline.putStrings(name, getArrayFromJTextArea(txa));
		return true;
	}
	
	private void txaTabArrived(JTextArea txa, String name) {
				
		// Get the current setting
		String pref = FreeGuide.prefs.misc.get(name);
		
		// Use the setting or a default if it's blank
		if(pref!=null) {
			txa.setText(pref);
		} else {
			txa.setText(FreeGuideUtils.getDefault(name));
		}
		
	}
	
	private boolean txtTabDeparted(JTextField txt, String name) {
		FreeGuide.prefs.misc.put(name, txt.getText());
		return true;
	}
	
	private void txtTabArrived(JTextField txt, String name) {
				
		// Get the current setting
		String pref = FreeGuide.prefs.misc.get(name);
		
		// Use the setting or a default if it's blank
		if(pref!=null) {
			txt.setText(pref);
		} else {
			txt.setText(FreeGuideUtils.getDefault(name));
		}
		
	}
	
	private boolean comboTabDeparted(JComboBox cmb, String name) {
		FreeGuide.prefs.misc.put(name, (String)cmb.getSelectedItem());
		return true;
	}
	
	private void comboTabArrived(JComboBox cmb, String name) {
		
		// Provide the options in the combobox
		FreeGuideUtils.fillComboBox(cmb, name);
		
		// Get the current setting
		String pref = FreeGuide.prefs.misc.get(name);
		
		// Use the setting or a default if it's blank
		if(pref!=null) {
			cmb.setSelectedItem(pref);
		} else {
			cmb.setSelectedItem(FreeGuideUtils.getDefault(name));
		}
		
	}
	
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
	
	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
		
		quit();
		
	}//GEN-LAST:event_butCancelActionPerformed

	private void butFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFinishActionPerformed
		
		new FreeGuideViewer(launcher).setVisible(true);
		quit();
		
	}//GEN-LAST:event_butFinishActionPerformed

	private void butBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBackActionPerformed
		
		int i=tabs.getSelectedIndex();
		
		if(i>0) {
			tabs.setSelectedIndex(i-1);
		}
		
	}//GEN-LAST:event_butBackActionPerformed

	private void tabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabsStateChanged
		
		// AllowChange says whether we should let this tab change go ahead -
		// this is true if the contents of the tab we're leaving are ok, or
		// the user has chosen to ignore this.
		boolean allowChange = true;
		
		// Check whether the contents of the tab we're leaving are ok.
		if(prevTab!=-1) {
			Component fromTab = tabs.getComponentAt(prevTab);
				 if(fromTab==panOS)				{allowChange = panOSDeparted();}
			else if(fromTab==panCountry)		{allowChange = panCountryDeparted();}
			else if(fromTab==panBrowserName)	{allowChange = panBrowserNameDeparted();}
			else if(fromTab==panXMLTVCmdDir)	{allowChange = panXMLTVCmdDirDeparted();}
			else if(fromTab==panWorkingDir)		{allowChange = panWorkingDirDeparted();}
			else if(fromTab==panGrabber)		{allowChange = panGrabberDeparted();}
			else if(fromTab==panBrowser)		{allowChange = panBrowserDeparted();}
			else if(fromTab==panDayStartTime)	{allowChange = panDayStartTimeDeparted();}
			else if(fromTab==panStyleSheet)		{allowChange = panStyleSheetDeparted();}
			else if(fromTab==panXMLTVCfg)		{allowChange = panXMLTVCfgDeparted();}
		}
		
		// If all is ok, set up the new tab
		if(allowChange) {
			Component toTab = tabs.getSelectedComponent();
				 if(toTab==panOS)			{panOSArrived();}
			else if(toTab==panCountry)		{panCountryArrived();}
			else if(toTab==panBrowserName)	{panBrowserNameArrived();}
			else if(toTab==panXMLTVCmdDir)	{panXMLTVCmdDirArrived();}
			else if(toTab==panWorkingDir)	{panWorkingDirArrived();}
			else if(toTab==panGrabber)		{panGrabberArrived();}
			else if(toTab==panBrowser)		{panBrowserArrived();}
			else if(toTab==panDayStartTime)	{panDayStartTimeArrived();}
			else if(toTab==panStyleSheet)	{panStyleSheetArrived();}
			else if(toTab==panXMLTVCfg)		{panXMLTVCfgArrived();}
			prevTab = tabs.getSelectedIndex();
		} else {
			// Otherwise, go back to the last tab
			int oldPrevTab = prevTab;
			prevTab=-1;
			tabs.setSelectedIndex(oldPrevTab);
		}
		
		refreshButtons();
			
	}//GEN-LAST:event_tabsStateChanged

	private void butNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNextActionPerformed
		
		int i=tabs.getSelectedIndex();
		
		if(i<tabs.getTabCount()-1) {
			tabs.setSelectedIndex(i+1);
		}
		
	}//GEN-LAST:event_butNextActionPerformed
		
	private void refreshButtons() {
		
		int i = tabs.getSelectedIndex();
		
		if(i==0) {
			butBack.setEnabled(false);
			butNext.setEnabled(true);
			butFinish.setEnabled(false);
		} else if( i==(tabs.getTabCount()-1) ) {
			butBack.setEnabled(true);
			butNext.setEnabled(false);
			butFinish.setEnabled(true);
		} else {
			butBack.setEnabled(true);
			butNext.setEnabled(true);
			butFinish.setEnabled(false);
		}
		
	}
	
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
		quit();	
	}//GEN-LAST:event_exitForm
	
	private void browse(JTextField txtFilename, boolean directory) {
		
		JFileChooser chooser = new JFileChooser();
    
		chooser.setCurrentDirectory( new File(txtFilename.getText()) );
		
		int returnVal;
		
		if(directory) {
			chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
			returnVal = chooser.showDialog(this, "Choose Directory");
		} else {
			returnVal = chooser.showDialog(this, "Choose File");
		}
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			
			txtFilename.setText(chooser.getSelectedFile().getAbsolutePath());
			
		}

	}
	
	private void quit() {
		setVisible(false);
		if(launcher!=null) {launcher.reShow();}
		dispose();
	}
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JComboBox cmbBrowserName;
    private javax.swing.JPanel panBrowserName;
    private javax.swing.JButton butFinish;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel1112;
    private javax.swing.JLabel jLabel1111;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel211;
    private javax.swing.JButton butBrowseWorkingDir;
    private javax.swing.JPanel panGrabber;
    private javax.swing.JButton butCancel;
    private javax.swing.JTextField txtXMLTVCfg;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JButton butBrowseXMLTVCfg;
    private javax.swing.JPanel panDayStartTime;
    private javax.swing.JLabel jLabel2117;
    private javax.swing.JPanel panXMLTVCmdDir;
    private javax.swing.JLabel jLabel2116;
    private javax.swing.JLabel jLabel111211;
    private javax.swing.JPanel panBrowser;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2112;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel panProblem;
    private javax.swing.JPanel panOptions;
    private javax.swing.JPanel panOS;
    private javax.swing.JButton butBrowseXMLTVCmdDir;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JTextField txtStyleSheet;
    private javax.swing.JComboBox cmbCountry;
    private javax.swing.JPanel panCountry;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel121;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel237;
    private javax.swing.JTextField txtWorkingDir;
    private javax.swing.JLabel jLabel236;
    private javax.swing.JPanel panStyleSheet;
    private javax.swing.JLabel jLabel2221;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel21121;
    private javax.swing.JLabel jLabel11122;
    private javax.swing.JLabel jLabel11121;
    private javax.swing.JTextArea txaGrabber;
    private javax.swing.JPanel panButtons;
    private javax.swing.JTextField txtDayStartTime;
    private javax.swing.JComboBox cmbOS;
    private javax.swing.JTextField txtXMLTVCmdDir;
    private javax.swing.JButton butBrowseXMLTVCfg1;
    private javax.swing.JPanel panWorkingDir;
    private javax.swing.JPanel jPanel111;
    private javax.swing.JButton butNext;
    private javax.swing.JPanel panFinish;
    private javax.swing.JTextArea txaBrowser;
    private javax.swing.JLabel jLabel1121;
    private javax.swing.JButton butBack;
    private javax.swing.JPanel panXMLTVCfg;
    private javax.swing.JLabel jLabel11112;
    private javax.swing.JLabel jLabel222;
    private javax.swing.JLabel jLabel11111;
    private javax.swing.JPanel panFirstTime;
    // End of variables declaration//GEN-END:variables
	
	public static final int SCREEN_FIRST_TIME = 0;
	public static final int SCREEN_PROBLEM = 1;
	public static final int SCREEN_OPTIONS = 2;
	
	private FreeGuideLauncher launcher;
	private int prevTab;
	
}
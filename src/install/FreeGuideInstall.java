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

import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.swing.JOptionPane;

/*
 * An installer for FreeGuide
 *
 * @author  Andy Balaam
 * @version 3
 */
public class FreeGuideInstall extends javax.swing.JFrame {
	
	public FreeGuideInstall() {
		
		try {
		
			// Load the install.props file
			props = new Properties();
			props.load(new BufferedInputStream(getClass().getResourceAsStream("/install.props")));
		
			installDir = props.getProperty("default_dir");
			if(installDir.equals("")) {
				installDir = System.getProperty("user.home") + fs + "freeguide-tv" + fs;
			}
		
			// Do the UI
			initComponents();
			
		} catch(java.io.IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		
		new FreeGuideInstall().setVisible(true);
		
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
        panInstallDir = new javax.swing.JPanel();
        jLabel1112 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        txtInstallDir = new javax.swing.JTextField();
        butBrowseInstallDir = new javax.swing.JButton();
        jLabel11121 = new javax.swing.JLabel();
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

        panFirstTime.setLayout(new java.awt.GridLayout(2, 0));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome to FreeGuide!");
        panFirstTime.add(jLabel1);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Click \"Next\" to begin installation.");
        panFirstTime.add(jLabel2);

        tabs.addTab("Welcome", panFirstTime);

        panInstallDir.setLayout(new java.awt.GridLayout(3, 0));

        jLabel1112.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1112.setText("Choose a directory to install FreeGuide into:");
        panInstallDir.add(jLabel1112);

        jPanel12.setLayout(new java.awt.GridBagLayout());

        txtInstallDir.setMinimumSize(new java.awt.Dimension(4, 26));
        txtInstallDir.setPreferredSize(new java.awt.Dimension(69, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(txtInstallDir, gridBagConstraints);

        butBrowseInstallDir.setFont(new java.awt.Font("Dialog", 0, 12));
        butBrowseInstallDir.setText("Browse...");
        butBrowseInstallDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrowseInstallDirActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel12.add(butBrowseInstallDir, gridBagConstraints);

        panInstallDir.add(jPanel12);

        jLabel11121.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel11121.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11121.setText("This directory will be created if it doesn't exist.");
        panInstallDir.add(jLabel11121);

        tabs.addTab("Installation Directory", panInstallDir);

        panFinish.setLayout(new java.awt.GridLayout(3, 0));

        jLabel116.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel116.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel116.setText("Congratulations!");
        panFinish.add(jLabel116);

        jLabel236.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel236.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel236.setText("FreeGuide will be installed when you click \"Finish\".");
        panFinish.add(jLabel236);

        jLabel2116.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabel2116.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2116.setText("Read the README.html file to get started (in the installation directory).");
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

	private void butBrowseInstallDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrowseInstallDirActionPerformed
		browse(txtInstallDir, true);
	}//GEN-LAST:event_butBrowseInstallDirActionPerformed

	private void panInstallDirDeparted() {
		
		installDir = txtInstallDir.getText();
		
	}

	private void panInstallDirArrived() {
		txtInstallDir.setText(installDir);
	}

	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
		
		quit();
		
	}//GEN-LAST:event_butCancelActionPerformed

	private void butFinishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butFinishActionPerformed
		
		try {
		
			finish();
			
		} catch(java.io.IOException e) {
			
			JOptionPane.showMessageDialog(this, "The following error has occurred:" + lb + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			
			e.printStackTrace();
			
		}
		
	}//GEN-LAST:event_butFinishActionPerformed

	private void butBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBackActionPerformed
		
		int i=tabs.getSelectedIndex();
		
		if(i>0) {
			tabs.setSelectedIndex(i-1);
		}
		
	}//GEN-LAST:event_butBackActionPerformed

	private void tabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tabsStateChanged
		
		// Check whether the contents of the tab we're leaving are ok.
		if(prevTab!=-1) {
			java.awt.Component fromTab = tabs.getComponentAt(prevTab);
			if(fromTab==panInstallDir)		{panInstallDirDeparted();}
		}
		
		java.awt.Component toTab = tabs.getSelectedComponent();
		if(toTab==panInstallDir)	{panInstallDirArrived();}
		prevTab = tabs.getSelectedIndex();
		
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
	
	private void browse(javax.swing.JTextField txtFilename, boolean directory) {
		
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
    
		chooser.setCurrentDirectory( new File(txtFilename.getText()) );
		
		int returnVal;
		
		if(directory) {
			chooser.setFileSelectionMode( javax.swing.JFileChooser.DIRECTORIES_ONLY );
			returnVal = chooser.showDialog(this, "Choose Directory");
		} else {
			returnVal = chooser.showDialog(this, "Choose File");
		}
		
		if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			
			txtFilename.setText(chooser.getSelectedFile().getAbsolutePath());
			
		}

	}
	
	private void quit() {
		setVisible(false);
		dispose();
	}
	
	
	private void finish() throws java.io.IOException {
		
		FreeGuidePreferencesGroup prefs = new FreeGuidePreferencesGroup();
		
		if(!installDir.endsWith(fs)) {
			installDir += fs;
		}
		String working_directory = installDir + props.getProperty("working_dir") + fs;
		
		boolean full_paths = props.getProperty("full_paths").equals("true");
		
		String xmltv_directory;
		if(full_paths) {
			xmltv_directory = installDir + props.getProperty("xmltv_dir") + fs;
		} else {
			xmltv_directory = props.getProperty("xmltv_dir") + fs;
		}
		
		// Make the required directories
		new File(installDir).mkdirs();
		new File(xmltv_directory).mkdirs();
		new File(working_directory).mkdirs();
		
		// Copy in the files
		int i=1;
		String filename="";
		while( (filename=props.getProperty("file."+i)) != null ) {
			installFile(filename);
			i++;
		}
		
		// Do the shared files (Win only)
		i=1;
		filename="";
		while( (filename=props.getProperty("share."+i)) != null ) {
			installFile(filename, "C:\\Perl\\");
			i++;
		}
		
		// Set up registry
		prefs.misc.put("os", props.getProperty("os"));
		prefs.misc.put("country", props.getProperty("country"));
		prefs.misc.put("browser_name", props.getProperty("browser_name"));
		prefs.misc.put("working_directory", working_directory);
		prefs.misc.put("xmltv_directory", xmltv_directory);
		
		String grabber_exe = props.getProperty("grabber_exe");
		String grabber_config = props.getProperty("grabber_config");
		String splitter_exe = props.getProperty("splitter_exe");
		String browser_exe = props.getProperty("browser_exe");
		
		String[] grabber = new String[2];
		String[] browser = new String[1];
		
		if(full_paths) {
		
			grabber[0] = "\"" + xmltv_directory + grabber_exe + "\" --config-file \"" + xmltv_directory + grabber_config + "\"" + " --output \"" + working_directory + "listings_unprocessed.xml\"";
			grabber[1] = "\"" + xmltv_directory + splitter_exe + "\" --output \"" + working_directory + "%%channel-%%Y%%m%%d.fgd\" --day_start_time 06:00 \"" + working_directory + "listings_unprocessed.xml\"";
			
			browser[0] = "\"" + browser_exe +"\" \"%filename%\"";
			
			prefs.misc.put("grabber_config", xmltv_directory + grabber_config );
			
		} else {
			
			grabber[0] = grabber_exe + " --output " + working_directory + "listings_unprocessed.xml";
			grabber[1] = splitter_exe + " --output " + working_directory + "%%channel-%%Y%%m%%d.fgd --day_start_time 06:00 " + working_directory + "listings_unprocessed.xml";
			
			browser[0] = browser_exe + " %filename%";
			
			prefs.misc.put("grabber_config", "%home%" + fs + ".xmltv" + fs + grabber_config );
			
		}
		
		prefs.commandline.putStrings("tv_grab", grabber);
		prefs.commandline.putStrings("browser_command", browser);
		prefs.misc.putFreeGuideTime("day_start_time", new FreeGuideTime(6,0));
		
		quit();
		
	}
	
	private void installFile(String name) throws java.io.IOException {
		installFile(name, installDir);
	}
		
	private void installFile(String name, String destDir) throws java.io.IOException {

		byte[] buf = new byte[32768];
		
		// make the directory if it doesn't exist
		String s = destDir + name;
		int i = s.lastIndexOf(File.separatorChar);
		if(i>-1) {
			new File(s.substring(0, i)).mkdirs();
		}
		
		BufferedInputStream in = new BufferedInputStream(getClass().getResourceAsStream("/" + name));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destDir + name));

		int count;
		while((count = in.read(buf, 0, buf.length)) > -1) {
			out.write(buf,0,count);
		}
			
		in.close();
		out.close();
		
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JButton butBrowseInstallDir;
    private javax.swing.JButton butFinish;
    private javax.swing.JPanel panInstallDir;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel236;
    private javax.swing.JLabel jLabel1112;
    private javax.swing.JLabel jLabel11121;
    private javax.swing.JPanel panButtons;
    private javax.swing.JButton butCancel;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JButton butNext;
    private javax.swing.JPanel panFinish;
    private javax.swing.JLabel jLabel2116;
    private javax.swing.JButton butBack;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField txtInstallDir;
    private javax.swing.JPanel panFirstTime;
    // End of variables declaration//GEN-END:variables
	
	private Properties props;
	private String installDir;
	private int prevTab;
	private FreeGuidePreferencesGroup prefs;
	private String fs = System.getProperty("file.separator");
	private String lb = System.getProperty("line.separator"); 
	
}

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
import javax.swing.JColorChooser;
import javax.swing.JTextField;
import java.awt.Font;

/**
 * FreeGuideCustomiser
 *
 * The Colour options screen for FreeGuide
 *
 * @author  Andy Balaam
 * @version 2
 */
public class FreeGuideCustomiser extends javax.swing.JFrame {
	
	/** Creates new form FreeGuideCustomiser */
	public FreeGuideCustomiser(FreeGuideLauncher launcher) {
		this.launcher = launcher;
		initComponents();
		initScreen();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        scrScreen = new javax.swing.JScrollPane();
        panScreen = new javax.swing.JPanel();
        labChannelColour = new javax.swing.JLabel();
        txtChannelColour = new javax.swing.JTextField();
        labProgrammeMovieColour = new javax.swing.JLabel();
        txtProgrammeMovieColour = new javax.swing.JTextField();
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
		labFont = new javax.swing.JLabel();
		butFont = new javax.swing.JButton();
        panButtons = new javax.swing.JPanel();
        butOK = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();
        timebuttongroup = new javax.swing.ButtonGroup();
        time12button = new javax.swing.JRadioButton();
        time24button = new javax.swing.JRadioButton();
        timeLabel = new javax.swing.JLabel();
        timeCBLabel = new javax.swing.JLabel();
        timeCheckBox = new javax.swing.JCheckBox();

		

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("FreeGuide Customiser");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        panScreen.setLayout(new java.awt.GridBagLayout());

        labChannelColour.setText("Channel Background Colour");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtChannelColour, gridBagConstraints);

        labProgrammeNormalColour.setText("Programme Background Colour");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtProgrammeNormalColour, gridBagConstraints);

        labProgrammeChosenColour.setText("Chosen Programme Colour");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtProgrammeChosenColour, gridBagConstraints);

        labChannelHeight.setText("Channel Height");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(labChannelHeight, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtChannelHeight, gridBagConstraints);

        labVerticalGap.setText("Vertical Gap");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(labVerticalGap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtVerticalGap, gridBagConstraints);

        labHorizontalGap.setText("Horizontal Gap");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(labHorizontalGap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtHorizontalGap, gridBagConstraints);

        labPanelWidth.setText("Panel Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(labPanelWidth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtPanelWidth, gridBagConstraints);

        labChannelPanelWidth.setText("Channel Panel Width");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(labChannelPanelWidth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtChannelPanelWidth, gridBagConstraints);

		labFont.setText("Font");
		gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(labFont, gridBagConstraints);
		
		butFont.setText("Choose Font");
        butFont.setMinimumSize(new java.awt.Dimension(88, 26));
        butFont.setPreferredSize(new java.awt.Dimension(88, 26));
        butFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butFontActionPerformed(evt);
            }
        });
		gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(butFont, gridBagConstraints);
		
        labProgrammeMovieColour.setText("Movie Background Colour");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(labProgrammeMovieColour, gridBagConstraints);

        txtProgrammeMovieColour.setEditable(false);
        txtProgrammeMovieColour.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtProgrammeMovieColour.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtProgrammeMovieColourMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        panScreen.add(txtProgrammeMovieColour, gridBagConstraints);


        time12button.setText("2:30 PM");
        timebuttongroup.add(time12button);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panScreen.add(time12button, gridBagConstraints);

        time24button.setText("14:30");
        timebuttongroup.add(time24button);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panScreen.add(time24button, gridBagConstraints);

        timeLabel.setText("Time Display:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(timeLabel, gridBagConstraints);

        timeCBLabel.setText("Show time in grid:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panScreen.add(timeCBLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panScreen.add(timeCheckBox, gridBagConstraints);

        scrScreen.setViewportView(panScreen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        getContentPane().add(scrScreen, gridBagConstraints);

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

		changedFont = false;
		
        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(400, 450));
        setLocation((screenSize.width-400)/2,(screenSize.height-400)/2);
		
		FreeGuidePreferences scr = FreeGuide.prefs.screen;
		
		fontDialog = new FontChooserDialog(this, "Choose Font", true,
			new Font(
				scr.get("font_name", "Dialog"),
				scr.getInt("font_style", Font.PLAIN),
				scr.getInt("font_size", 12)) );

			
        fontDialog.setSize(new java.awt.Dimension(300, 200));
        fontDialog.setLocation( (screenSize.width-300)/2,
			(screenSize.height-200)/2 );
		
		
    }//GEN-END:initComponents

	private void butOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOKActionPerformed
		saveScreen();
		quit();
	}//GEN-LAST:event_butOKActionPerformed

	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
		quit();
	}//GEN-LAST:event_butCancelActionPerformed

	private void butFontActionPerformed(java.awt.event.ActionEvent evt) {
		
		changedFont = true;
		fontDialog.setVisible(true);
		
	}
	
	private void txtProgrammeMovieColourMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProgrammeMovieColourMouseClicked
		doColorDialog(txtProgrammeMovieColour);
	}//GEN-LAST:event_txtProgrammeMovieColourMouseClicked

	private void txtProgrammeNormalColourMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProgrammeNormalColourMouseClicked
		doColorDialog(txtProgrammeNormalColour);
	}//GEN-LAST:event_txtProgrammeNormalColourMouseClicked

	private void txtProgrammeChosenColourMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtProgrammeChosenColourMouseClicked
		doColorDialog(txtProgrammeChosenColour);
	}//GEN-LAST:event_txtProgrammeChosenColourMouseClicked

	private void txtChannelColourMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtChannelColourMouseClicked
		doColorDialog(txtChannelColour);
	}//GEN-LAST:event_txtChannelColourMouseClicked
	
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
		quit();
	}//GEN-LAST:event_exitForm
	
	private void saveScreen() {
		
		FreeGuide.prefs.screen.putColor("channel_colour", txtChannelColour.getBackground());
		
		FreeGuide.prefs.screen.putInt("channel_height", Integer.parseInt(txtChannelHeight.getText()));
		FreeGuide.prefs.screen.putInt("channel_panel_width", Integer.parseInt(txtChannelPanelWidth.getText()));
		FreeGuide.prefs.screen.putInt("horizontal_gap", Integer.parseInt(txtHorizontalGap.getText()));
		FreeGuide.prefs.screen.putInt("vertical_gap", Integer.parseInt(txtVerticalGap.getText()));
		FreeGuide.prefs.screen.putInt("panel_width", Integer.parseInt(txtPanelWidth.getText()));
		
		FreeGuide.prefs.screen.putColor("programme_chosen_colour", txtProgrammeChosenColour.getBackground());
		FreeGuide.prefs.screen.putColor("programme_normal_colour", txtProgrammeNormalColour.getBackground());
		FreeGuide.prefs.screen.putColor("programme_movie_colour", txtProgrammeMovieColour.getBackground());
		FreeGuide.prefs.screen.putBoolean("display_programme_time", timeCheckBox.isSelected());
                FreeGuide.prefs.screen.putBoolean("display_24hour_time", timebuttongroup.getSelection().equals(time24button.getModel()));
		if(changedFont) {
			
			Font f = fontDialog.getSelectedFont();
			FreeGuide.prefs.screen.put( "font_name", f.getName() );
			FreeGuide.prefs.screen.putInt( "font_style", f.getStyle() );
			FreeGuide.prefs.screen.putInt( "font_size", f.getSize() );
			
		}
		
	}
	
	private void doColorDialog(JTextField txt) {
		
		Color col = JColorChooser.showDialog(this, "Choose a Colour", txt.getBackground());
		if(col!=null) {
			fillTextAreaFromColor(txt, col);
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
		
		col = FreeGuide.prefs.screen.getColor("programme_movie_colour", FreeGuide.PROGRAMME_CHOSEN_COLOUR);
		fillTextAreaFromColor(txtProgrammeMovieColour, col);
		col = FreeGuide.prefs.screen.getColor("programme_chosen_colour", FreeGuide.PROGRAMME_CHOSEN_COLOUR);
		fillTextAreaFromColor(txtProgrammeChosenColour, col);
		
		col = FreeGuide.prefs.screen.getColor("programme_normal_colour", FreeGuide.PROGRAMME_NORMAL_COLOUR);
		fillTextAreaFromColor(txtProgrammeNormalColour, col);
                boolean progtime = FreeGuide.prefs.screen.getBoolean("display_programme_time",true);
                timeCheckBox.setSelected(progtime);
                boolean time24 =   FreeGuide.prefs.screen.getBoolean("display_24hour_time", false);
                if (time24)
                    timebuttongroup.setSelected(time24button.getModel(),true);
                else
                    timebuttongroup.setSelected(time12button.getModel(),true);
              
                

		
		//FreeGuidePreferences scr = FreeGuide.prefs.screen;
		//butFont.setText( scr.get("font_name", "Dialog") +
		//	" " + scr.get("font_size", "12") );
		
	}
	
	private void fillTextAreaFromColor(JTextField txt, Color col) {
		
		txt.setBackground(col);
		txt.setText( "(" + col.getRed() + ", " + col.getGreen() + ", " + col.getBlue() + ")");
		
	}
	
	// ------------------------------------------------------------------------
	
	/** 
	 * Closes the form and goes back to the viewer.
	 */
	private void quit() {
		
		hide();
		launcher.reShow();
		dispose();

	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField txtProgrammeNormalColour;
    private javax.swing.JTextField txtProgrammeMovieColour;
    private javax.swing.JLabel labChannelHeight;
    private javax.swing.JTextField txtChannelHeight;
    private javax.swing.JLabel labChannelPanelWidth;
    private javax.swing.JTextField txtChannelPanelWidth;
    private javax.swing.JLabel labVerticalGap;
    private javax.swing.JTextField txtPanelWidth;
    private javax.swing.JTextField txtVerticalGap;
    private javax.swing.JScrollPane scrScreen;
    private javax.swing.JPanel panScreen;
    private javax.swing.JPanel panButtons;
    private javax.swing.JLabel labChannelColour;
    private javax.swing.JTextField txtChannelColour;
    private javax.swing.JButton butOK;
    private javax.swing.JButton butCancel;
    private javax.swing.JLabel labPanelWidth;
    private javax.swing.JLabel labProgrammeChosenColour;
    private javax.swing.JLabel labProgrammeMovieColour;
    private javax.swing.JTextField txtProgrammeChosenColour;
    private javax.swing.JLabel labHorizontalGap;
    private javax.swing.JTextField txtHorizontalGap;
    private javax.swing.JLabel labProgrammeNormalColour;
	private javax.swing.JButton butFont;
    private javax.swing.JLabel labFont;
    private javax.swing.JRadioButton time12button;
    private javax.swing.ButtonGroup timebuttongroup;
    private javax.swing.JRadioButton time24button;
    private javax.swing.JLabel timeLabel;
    private javax.swing.JLabel timeCBLabel;
    private javax.swing.JCheckBox timeCheckBox;
    // End of variables declaration//GEN-END:variables
	
	FreeGuideLauncher launcher;
	FontChooserDialog fontDialog;
	boolean changedFont;
	
}

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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/*
 * FreeGuideFavouriteEditor
 *
 * A screen for editing a single favourite.
 *
 * @author  Andy Balaam
 * @varion 1
 */
public class FreeGuideFavouriteEditor extends javax.swing.JFrame {
	
	/** Creates new form FreeGuideFavouriteEditor */
	public FreeGuideFavouriteEditor(FreeGuideLauncher launcher, FreeGuideFavourite favourite) {
		this.launcher = launcher;
		this.favourite= favourite;
		
		initComponents();
		fillLists();
		
	}
	
	private void fillLists() {
		
		// The combobox for the title match type
		cmbTitle.addItem("Exactly");
		cmbTitle.addItem("Regular Expression");
		
		channelIDs = ((FreeGuideViewer)launcher.getLauncher()).getChannelIDs();
		channelNames = ((FreeGuideViewer)launcher.getLauncher()).getChannelNames();
		
		cmbChannel.addItem("");
		for(int i=0;i<channelNames.length;i++) {	
			cmbChannel.addItem(channelNames[i]);
		}
		
		Calendar cal = GregorianCalendar.getInstance();
		
		cmbDayOfWeek.addItem( "" );
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cmbDayOfWeek.addItem( dayOfWeekFormat.format(cal.getTime()) );
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		cmbDayOfWeek.addItem( dayOfWeekFormat.format(cal.getTime()) );
		cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
		cmbDayOfWeek.addItem( dayOfWeekFormat.format(cal.getTime()) );
		cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
		cmbDayOfWeek.addItem( dayOfWeekFormat.format(cal.getTime()) );
		cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
		cmbDayOfWeek.addItem( dayOfWeekFormat.format(cal.getTime()) );
		cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
		cmbDayOfWeek.addItem( dayOfWeekFormat.format(cal.getTime()) );
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cmbDayOfWeek.addItem( dayOfWeekFormat.format(cal.getTime()) );
		
	}
	
	private void updateFavourite() {
		
		// Calculate the name
		calcTxtName();
		
		// Set the name
		favourite.setName( txtName.getText() );
		
		// Set the title
		if(cmbTitle.getSelectedItem().equals("Exactly")) {
			
			favourite.setTitleRegex( null );
			
			String tmp = txtTitle.getText();
			if(tmp.equals("")) {
				favourite.setTitleString( null );
			} else {
				favourite.setTitleString( tmp );
			}
			
		} else {
			
			String tmp = txtTitle.getText();
			if(tmp.equals("")) {
				favourite.setTitleRegex( null );
			} else {
				favourite.setTitleRegex( Pattern.compile(tmp) );
			}
			
			favourite.setTitleString( null );
			
		}
		
		// Set the channel
		String tmp = (String)cmbChannel.getSelectedItem();
		if( tmp.equals("") ) {
			favourite.setChannelID( null );
		} else {
			favourite.setChannelID( tmp );
		}
		
		// Set the after time
		tmp = txtAfter.getText();
		if( !tmp.equals("") && (tmp.length()==5) && tmp.charAt(2)==':') {
			
			String hhmm = tmp.substring(0,2) + tmp.substring(3);
			favourite.setAfterTime( new FreeGuideTime(hhmm) );
			
		} else {
			favourite.setAfterTime( null );
		}
		
		// Set the before time
		tmp = txtBefore.getText();
		if( !tmp.equals("") && (tmp.length()==5) && tmp.charAt(2)==':') {
			
			String hhmm = tmp.substring(0,2) + tmp.substring(3);
			favourite.setBeforeTime( new FreeGuideTime(hhmm) );
			
		} else {
			favourite.setBeforeTime( null );
		}
		
		// Set the day of the week
		tmp = (String)cmbDayOfWeek.getSelectedItem();
		
		if(!tmp.equals("")) {
			Calendar cal = GregorianCalendar.getInstance();
			try {
				cal.setTime( dayOfWeekFormat.parse(tmp) );
				favourite.setDayOfWeek( new Integer(cal.get(Calendar.DAY_OF_WEEK)) );
			} catch(java.text.ParseException e) {
				e.printStackTrace();
				favourite.setDayOfWeek( null );
			}
		
		} else {
			favourite.setDayOfWeek(null);
		}
		
	}
	
	private void calcTxtName() {
		
		if(txtName.getText().equals("")) {
			
			String name = "";
			
			String title = txtTitle.getText();
			if(!title.equals("")) {
				
				if(cmbTitle.getSelectedItem().equals("Exactly")) {
					name += "Title=\""+title+"\" ";
				} else {
					name += "Title~/"+title+"/ ";
				}
				
			}
			
			String channel = (String)cmbChannel.getSelectedItem();
			if(!channel.equals("")) {
				name += "Channel=\"" + channel + "\" ";
			}
			
			String after = txtAfter.getText();
			if(!after.equals("")) {
				name += "After " + after + " ";
			}
			
			String before = txtBefore.getText();
			if(!before.equals("")) {
				name += "Before " + before + " ";
			}
			
			String dayOfWeek = (String)cmbDayOfWeek.getSelectedItem();
			if(!dayOfWeek.equals("")) {
				name += "On " + dayOfWeek + " ";
			}
			
			if(name.equals("")) {
				name="All Programmes";
			}
			
			
			txtName.setText(name);
		}//if
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labTitle = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        cmbTitle = new javax.swing.JComboBox();
        cmbTitle.addItem("Exactly"); cmbTitle.addItem("Regular Expression");
        labChannel = new javax.swing.JLabel();
        cmbChannel = new javax.swing.JComboBox();
        labAfter = new javax.swing.JLabel();
        txtAfter = new javax.swing.JTextField();
        txtBefore = new javax.swing.JTextField();
        labBefore = new javax.swing.JLabel();
        labBlankFields = new javax.swing.JLabel();
        labTimeFormat = new javax.swing.JLabel();
        labDayOfWeek = new javax.swing.JLabel();
        labName = new javax.swing.JLabel();
        cmbDayOfWeek = new javax.swing.JComboBox();
        txtName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        butOK = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Editing Favourite");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        labTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTitle.setText("Title matches:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labTitle, gridBagConstraints);

        txtTitle.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtTitle, gridBagConstraints);

        cmbTitle.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(cmbTitle, gridBagConstraints);

        labChannel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labChannel.setText("Channel is:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labChannel, gridBagConstraints);

        cmbChannel.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(cmbChannel, gridBagConstraints);

        labAfter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labAfter.setText("On after:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labAfter, gridBagConstraints);

        txtAfter.setPreferredSize(new java.awt.Dimension(50, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtAfter, gridBagConstraints);

        txtBefore.setPreferredSize(new java.awt.Dimension(50, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtBefore, gridBagConstraints);

        labBefore.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labBefore.setText("On before:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labBefore, gridBagConstraints);

        labBlankFields.setFont(new java.awt.Font("Dialog", 0, 12));
        labBlankFields.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labBlankFields.setText("(You may leave any fields blank)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(labBlankFields, gridBagConstraints);

        labTimeFormat.setFont(new java.awt.Font("Dialog", 0, 12));
        labTimeFormat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labTimeFormat.setText("Times should be entered as \"hh:mm\"");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(labTimeFormat, gridBagConstraints);

        labDayOfWeek.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labDayOfWeek.setText("On day:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labDayOfWeek, gridBagConstraints);

        labName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labName, gridBagConstraints);

        cmbDayOfWeek.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(cmbDayOfWeek, gridBagConstraints);

        txtName.setText("All Programmes");
        txtName.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtName, gridBagConstraints);

        butOK.setText("OK");
        butOK.setPreferredSize(new java.awt.Dimension(87, 26));
        butOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOKActionPerformed(evt);
            }
        });

        jPanel1.add(butOK);

        butCancel.setText("Cancel");
        butCancel.setPreferredSize(new java.awt.Dimension(87, 26));
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        jPanel1.add(butCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
		quit();
	}//GEN-LAST:event_butCancelActionPerformed

	private void butOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOKActionPerformed
		
		updateFavourite();
		quit();
		
	}//GEN-LAST:event_butOKActionPerformed
	
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
		quit();
	}//GEN-LAST:event_exitForm
	
	private void quit() {
		hide();
		launcher.reShow();
		dispose();
	}
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labChannel;
    private javax.swing.JLabel labBefore;
    private javax.swing.JComboBox cmbChannel;
    private javax.swing.JLabel labBlankFields;
    private javax.swing.JButton butOK;
    private javax.swing.JButton butCancel;
    private javax.swing.JTextField txtName;
    private javax.swing.JLabel labDayOfWeek;
    private javax.swing.JLabel labTimeFormat;
    private javax.swing.JComboBox cmbDayOfWeek;
    private javax.swing.JTextField txtBefore;
    private javax.swing.JLabel labAfter;
    private javax.swing.JTextField txtAfter;
    private javax.swing.JLabel labTitle;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JComboBox cmbTitle;
    // End of variables declaration//GEN-END:variables
	
	String[] channelIDs;
	String[] channelNames;
	
	FreeGuideFavourite favourite;
	FreeGuideLauncher launcher;
	
	private static final SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");
	
}

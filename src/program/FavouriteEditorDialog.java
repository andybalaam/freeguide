/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Vector;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import javax.swing.*;
import java.awt.*;

/*
 *  FreeGuideFavouriteEditor
 *
 *  A screen for editing a single favourite.
 *
 *  @author     Brendan Corrigan (based on FreeGuideFavouriteEditor by Andy Balaam)
 *  @created    22nd August 2003
 *  @version    1
 */
 
public class FavouriteEditorDialog extends JDialog {
    
    /**
     * Constructor which sets the favourites editor up as a JDialog...
     *
     *@param owner - the <code>JFrame</code> from which the dialog is displayed 
     *@param title - the <code>String</code> to display in the dialog's title bar
     *@param favourite - the <code>Favourite</code> to modify
     */
    public FavouriteEditorDialog(Dialog owner, String title, Favourite favourite) {
        super(owner, title, true);
     
        this.favourite = favourite;

        initComponents();
        fillLists();
        getDetails();

    }


    /**
     *  Gets the details attribute of the FreeGuideFavouriteEditor object
     */
    private void getDetails() {

        if (favourite.getTitleString() != null) {
            txtTitle.setText(favourite.getTitleString());
            cmbTitle.setSelectedItem("Exactly");
        } else if (favourite.getTitleContains() != null) {
            txtTitle.setText(favourite.getTitleContains());
            cmbTitle.setSelectedItem("Contains");
        } else if (favourite.getTitleRegex() != null) {
            txtTitle.setText(favourite.getTitleRegex().pattern());
            cmbTitle.setSelectedItem("Regular Expression");
        }

        if (favourite.getChannelID() != null) {
            cmbChannel.setSelectedItem(getChannelNameFromID(favourite.getChannelID()));
        }

        if (favourite.getAfterTime() != null) {
            txtAfter.setText(favourite.getAfterTime().getHHMMString());
        }

        if (favourite.getBeforeTime() != null) {
            txtBefore.setText(favourite.getBeforeTime().getHHMMString());
        }

        if (favourite.getDayOfWeek() != null) {
            cmbDayOfWeek.setSelectedIndex(favourite.getDayOfWeek().intValue());
        }

        calcTxtName();

    }


    /**
     *  Description of the Method
     */
    private void fillLists() {

        // The combobox for the title match type
        cmbTitle.addItem("Exactly");
        cmbTitle.addItem("Contains");
        cmbTitle.addItem("Regular Expression");


        // ViewerFrame viewerFrame = (ViewerFrame)launcher.getLauncher();		
        // channelIDs = viewerFrame.xmltvLoader.getChannelIDs();

        channelIDs = ViewerFrame.xmltvLoader.getChannelIDs();			
        channelNames = ViewerFrame.xmltvLoader.getChannelNames();

        cmbChannel.addItem("");
        for (int i = 0; i < channelNames.size(); i++) {
            cmbChannel.addItem(channelNames.get(i));
        }

        Calendar cal = GregorianCalendar.getInstance();

        cmbDayOfWeek.addItem("");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cmbDayOfWeek.addItem(dayOfWeekFormat.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cmbDayOfWeek.addItem(dayOfWeekFormat.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        cmbDayOfWeek.addItem(dayOfWeekFormat.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        cmbDayOfWeek.addItem(dayOfWeekFormat.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        cmbDayOfWeek.addItem(dayOfWeekFormat.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        cmbDayOfWeek.addItem(dayOfWeekFormat.format(cal.getTime()));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cmbDayOfWeek.addItem(dayOfWeekFormat.format(cal.getTime()));

    }


    /**
     *  Description of the Method
     */
    private void updateFavourite() {

        // Calculate the name
        calcTxtName();

        // Set the name
        favourite.setName(txtName.getText());

        // Set the title
        if (cmbTitle.getSelectedItem().equals("Exactly")) {

            favourite.setTitleRegex(null);
            favourite.setTitleContains(null);

            String tmp = txtTitle.getText();
            if (tmp.equals("")) {
                favourite.setTitleString(null);
            } else {
                favourite.setTitleString(tmp);
            }

        } else if (cmbTitle.getSelectedItem().equals("Contains")) {

            favourite.setTitleString(null);
            favourite.setTitleRegex(null);

            String tmp = txtTitle.getText();
            if (tmp.equals("")) {
                favourite.setTitleContains(null);
            } else {
                favourite.setTitleContains(tmp);
            }

        } else {

            favourite.setTitleString(null);
            favourite.setTitleContains(null);

            String tmp = txtTitle.getText();
            if (tmp.equals("")) {
                favourite.setTitleRegex(null);
            } else {
                favourite.setTitleRegex(Pattern.compile(tmp));
            }

        }

        // Set the channel
        String tmp = (String) cmbChannel.getSelectedItem();
        if (tmp.equals("")) {
            favourite.setChannelID(null);
        } else {
            favourite.setChannelID(getChannelIDFromName(tmp));
        }

        // Set the after time
        tmp = txtAfter.getText();
        if (!tmp.equals("") && (tmp.length() == 5) && tmp.charAt(2) == ':') {

            //String hhmm = tmp.substring(0,2) + tmp.substring(3);
            favourite.setAfterTime(new Time(tmp));

        } else {
            favourite.setAfterTime(null);
        }

        // Set the before time
        tmp = txtBefore.getText();
        if (!tmp.equals("") && (tmp.length() == 5) && tmp.charAt(2) == ':') {

            //String hhmm = tmp.substring(0,2) + tmp.substring(3);
            favourite.setBeforeTime(new Time(tmp));

        } else {
            favourite.setBeforeTime(null);
        }

        // Set the day of the week
        tmp = (String) cmbDayOfWeek.getSelectedItem();

        if (!tmp.equals("")) {
            Calendar cal = GregorianCalendar.getInstance();
            try {
                cal.setTime(dayOfWeekFormat.parse(tmp));
                favourite.setDayOfWeek(new Integer(cal.get(Calendar.DAY_OF_WEEK)));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                favourite.setDayOfWeek(null);
            }

        } else {
            favourite.setDayOfWeek(null);
        }

    }


    /**
     *  Gets the channelNameFromID attribute of the FreeGuideFavouriteEditor
     *  object
     *
     *@param  id  Description of the Parameter
     *@return     The channelNameFromID value
     */
    private String getChannelNameFromID(String id) {
        int i = channelIDs.indexOf(id);
        return channelNames.get(i).toString();
    }


    /**
     *  Gets the channelIDFromName attribute of the FreeGuideFavouriteEditor
     *  object
     *
     *@param  name  Description of the Parameter
     *@return       The channelIDFromName value
     */
    private String getChannelIDFromName(String name) {
        int i = channelNames.indexOf(name);
        return channelIDs.get(i).toString();
    }


    /**
     *  Description of the Method
     */
    private void calcTxtName() {

        //if(txtName.getText().equals("All Programmes")) {

        String name = "";

        String title = txtTitle.getText();
        if (!title.equals("")) {

            if (cmbTitle.getSelectedItem().equals("Exactly")) {
                name += "" + title + " ";
            } else if (cmbTitle.getSelectedItem().equals("Contains")) {
                name += "contains " + title + " ";
            } else {
                name += "/" + title + "/ ";
            }

        }

        String channel = (String) cmbChannel.getSelectedItem();
        if (channel != null && !channel.equals("")) {
            name += "on " + channel + " ";
        }

        String after = txtAfter.getText();
        if (!after.equals("")) {
            name += "after " + after + " ";
        }

        String before = txtBefore.getText();
        if (!before.equals("")) {
            name += "before " + before + " ";
        }

        String dayOfWeek = (String) cmbDayOfWeek.getSelectedItem();
        if (dayOfWeek != null && !dayOfWeek.equals("")) {
            name += "on " + dayOfWeek + " ";
        }

        if (name.equals("")) {
            name = "All Programmes";
        }

        txtName.setText(name);

        //}//if
    }


    /**
     *  This method is called from within the constructor to initialize the
     *  form. WARNING: Do NOT modify this code. The content of this method is
     *  always regenerated by the Form Editor.
     */
    private void initComponents() {
        //GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labTitle = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        cmbTitle = new javax.swing.JComboBox();
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
        labTimeFormat1 = new javax.swing.JLabel();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("Editing Favourite");
        addWindowListener(
            new java.awt.event.WindowAdapter() {
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
        txtTitle.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtTitleActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(txtTitle, gridBagConstraints);

        cmbTitle.setPreferredSize(new java.awt.Dimension(150, 25));
        cmbTitle.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmbTitleActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
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
        cmbChannel.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmbChannelActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
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

        txtAfter.setMinimumSize(new java.awt.Dimension(50, 25));
        txtAfter.setName("null");
        txtAfter.setPreferredSize(new java.awt.Dimension(50, 25));
        txtAfter.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtAfterActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtAfter, gridBagConstraints);

        txtBefore.setMinimumSize(new java.awt.Dimension(50, 25));
        txtBefore.setPreferredSize(new java.awt.Dimension(50, 25));
        txtBefore.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtBeforeActionPerformed(evt);
                }
            });

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
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(labBlankFields, gridBagConstraints);

        labTimeFormat.setFont(new java.awt.Font("Dialog", 0, 12));
        labTimeFormat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labTimeFormat.setText("(Times should be entered");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.4;
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
        cmbDayOfWeek.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmbDayOfWeekActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(cmbDayOfWeek, gridBagConstraints);

        txtName.setEditable(false);
        txtName.setText("All Programmes");
        txtName.setPreferredSize(new java.awt.Dimension(200, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtName, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        butOK.setText("OK");
        butOK.setMinimumSize(new java.awt.Dimension(87, 26));
        butOK.setPreferredSize(new java.awt.Dimension(87, 26));
        butOK.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butOKActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(butOK, gridBagConstraints);

        butCancel.setText("Cancel");
        butCancel.setMinimumSize(new java.awt.Dimension(87, 26));
        butCancel.setPreferredSize(new java.awt.Dimension(87, 26));
        butCancel.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butCancelActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanel1.add(butCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel1, gridBagConstraints);

        labTimeFormat1.setFont(new java.awt.Font("Dialog", 0, 12));
        labTimeFormat1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labTimeFormat1.setText("as \"hh:mm\")");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.4;
        getContentPane().add(labTimeFormat1, gridBagConstraints);

        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(400, 300));
        setLocation((screenSize.width - 400) / 2, (screenSize.height - 300) / 2);
    }


    //GEN-END:initComponents

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void txtTitleActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_txtTitleActionPerformed
        calcTxtName();
    }


    //GEN-LAST:event_txtTitleActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void cmbDayOfWeekActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_cmbDayOfWeekActionPerformed
        calcTxtName();
    }


    //GEN-LAST:event_cmbDayOfWeekActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void txtBeforeActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_txtBeforeActionPerformed
        calcTxtName();
    }


    //GEN-LAST:event_txtBeforeActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void txtAfterActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_txtAfterActionPerformed
        calcTxtName();
    }


    //GEN-LAST:event_txtAfterActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void cmbChannelActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_cmbChannelActionPerformed
        calcTxtName();
    }


    //GEN-LAST:event_cmbChannelActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void cmbTitleActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_cmbTitleActionPerformed
        calcTxtName();
    }


    //GEN-LAST:event_cmbTitleActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_butCancelActionPerformed
        quit();
    }


    //GEN-LAST:event_butCancelActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butOKActionPerformed(java.awt.event.ActionEvent evt) {
        //GEN-FIRST:event_butOKActionPerformed

        updateFavourite();
        quit();

    }


    //GEN-LAST:event_butOKActionPerformed

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void exitForm(java.awt.event.WindowEvent evt) {
        //GEN-FIRST:event_exitForm
        quit();
    }


    //GEN-LAST:event_exitForm

    /**
     *  Description of the Method
     */
    private void quit() {

        hide();
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
    private javax.swing.JLabel labTimeFormat1;
    private javax.swing.JComboBox cmbTitle;
    // End of variables declaration//GEN-END:variables

    Vector channelIDs;
    Vector channelNames;
    Favourite favourite;
    Launcher launcher;

    private final static SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE");

}

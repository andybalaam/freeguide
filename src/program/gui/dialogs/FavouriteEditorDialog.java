/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.gui.dialogs;

import freeguide.*;
import freeguide.gui.viewer.*;
import freeguide.lib.fgspecific.*;
import freeguide.lib.general.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import java.awt.*;

/*
 *  FreeGuideFavouriteEditor
 *
 *  A screen for editing a single favourite.
 *
 *  @author     Brendan Corrigan (based on FreeGuideFavouriteEditor by Andy
 *              Balaam)
 *  @created    22nd August 2003
 *  @version    2
 */
 
public class FavouriteEditorDialog extends FGDialog {
    
    /**
     * Constructor which sets the favourites editor up as a JDialog...
     *
     *@param owner - the <code>JFrame</code> from which the dialog is displayed 
     *@param title - the <code>String</code> to display in the dialog's title bar
     *@param favourite - the <code>Favourite</code> to modify
     */
    public FavouriteEditorDialog(FGDialog owner, String title,
            Favourite favourite) {
        
        super(owner, title);
     
        this.favourite = favourite;

        initComponents();
        fillLists();
        getDetails();
        addActionListeners();

    }


    /**
     *  Gets the details from the favourite and fills in the UI elements
     */
    private void getDetails() {
        
        if (favourite.getTitleString() != null) {
            txtTitle.setText(favourite.getTitleString());
            cmbTitle.setSelectedItem( FreeGuide.msg.getString( "exactly" ) );
        } else if (favourite.getTitleContains() != null) {
            txtTitle.setText(favourite.getTitleContains());
            cmbTitle.setSelectedItem( FreeGuide.msg.getString( "contains" ) );
        } else if (favourite.getTitleRegex() != null) {
            txtTitle.setText(favourite.getTitleRegex().pattern());
            cmbTitle.setSelectedItem( FreeGuide.msg.getString(
                "regular_expression" ) );
        }

        if (favourite.getChannel() != null) {
            cmbChannel.setSelectedItem(favourite.getChannel());
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
        cmbTitle.addItem( FreeGuide.msg.getString( "exactly" ) );
        cmbTitle.addItem( FreeGuide.msg.getString( "contains" ) );
        cmbTitle.addItem( FreeGuide.msg.getString( "regular_expression" ) );

        channels = ViewerFrame.xmltvLoader.getChannels();			

        cmbChannel.addItem("");
        for (int i = 0; i < channels.size(); i++) {
            cmbChannel.addItem( (Channel)channels.get(i) );
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
        if( cmbTitle.getSelectedItem().equals(
            FreeGuide.msg.getString( "exactly" ) ) )
        {

            favourite.setTitleRegex(null);
            favourite.setTitleContains(null);

            String tmp = txtTitle.getText();
            if (tmp.equals("")) {
                favourite.setTitleString(null);
            } else {
                favourite.setTitleString(tmp);
            }

        } else if( cmbTitle.getSelectedItem().equals(
            FreeGuide.msg.getString( "contains" ) ) )
        {

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
        Object sel = cmbChannel.getSelectedItem();
        if (sel instanceof Channel) {
            favourite.setChannel((Channel)sel);
        } else {
            favourite.setChannel(null);
        }

        // Set the after time
        String tmp = txtAfter.getText();
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
     *  Give the favourite a name depending on its properties
     */
    private void calcTxtName() {
        
        // Find the properties of the favourite
        String title = txtTitle.getText();
        String channel = cmbChannel.getSelectedItem().toString();
        String after = txtAfter.getText();
        String before = txtBefore.getText();
        String dayOfWeek = cmbDayOfWeek.getSelectedItem().toString();
        
        // Prepare strings that will be substituted into the name template
        String equalsString = "";
        Object[] equalsArray = { title };
        
        String containsString = "";
        Object[] containsArray = { title };
        
        String regexpString = "";
        Object[] regexpArray = { title };
        
        String channelString = "";
        Object[] channelArray = { channel };
        
        String afterString = "";
        Object[] afterArray = { after };
        
        String beforeString = "";
        Object[] beforeArray = { before };
        
        String dayOfWeekString = "";
        Object[] dayOfWeekArray = { dayOfWeek };
        
        if( !title.equals( "" ) ) {
            
            if( cmbTitle.getSelectedItem().equals( 
                FreeGuide.msg.getString( "exactly" ) ) )
            {
                equalsString = FreeGuide.getCompoundMessage(
                    "favourite_name_equals_template", equalsArray );
            } else if (cmbTitle.getSelectedItem().equals(
                FreeGuide.msg.getString( "contains" ) ) )
            {
                containsString = FreeGuide.getCompoundMessage(
                    "favourite_name_contains_template", containsArray );
            } else {
                regexpString = FreeGuide.getCompoundMessage(
                    "favourite_name_regexp_template", regexpArray );
            }
            
        }
        
        if( !channel.equals("") ) {
            channelString = FreeGuide.getCompoundMessage(
                "favourite_name_channel_template", channelArray );
        }

        if( !after.equals("") ) {
            afterString = FreeGuide.getCompoundMessage(
                "favourite_name_after_template", afterArray );
        }

        if( !before.equals("") ) {
            beforeString = FreeGuide.getCompoundMessage(
                "favourite_name_before_template", beforeArray );
        }

        if( !dayOfWeek.equals("") ) {
            dayOfWeekString = FreeGuide.getCompoundMessage(
                "favourite_name_day_of_week_template", dayOfWeekArray );
        }

        Object[] nameArgs = { equalsString, containsString, regexpString,
            channelString, beforeString, afterString, dayOfWeekString };
        
        String name = FreeGuide.getCompoundMessage( "favourite_name_template",
            nameArgs );
        
        name = name.replaceAll( "\\s+", " " ).trim();
            
        if( name.equals( "" ) ) {
            name = FreeGuide.msg.getString( "all_programmes" );
        }

        txtName.setText( name );
        
    }


    /**
     * Add action listeners to the UI components
     */
    private void addActionListeners() {
        
        txtTitle.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtTitleActionPerformed(evt);
                }
            });
     
        cmbTitle.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmbTitleActionPerformed(evt);
                }
            });
     
        cmbChannel.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmbChannelActionPerformed(evt);
                }
            });

        txtAfter.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtAfterActionPerformed(evt);
                }
            });

        txtBefore.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    txtBeforeActionPerformed(evt);
                }
            });

        cmbDayOfWeek.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmbDayOfWeekActionPerformed(evt);
                }
            });

        butOK.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butOKActionPerformed(evt);
                }
            });

        butCancel.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butCancelActionPerformed(evt);
                }
            });

        
    }
     
    /**
     *  Create the UI
     */
    private void initComponents() {

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

        labTitle.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labTitle.setText( FreeGuide.msg.getString( "title_matches" ) + ":" );
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
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(txtTitle, gridBagConstraints);

        cmbTitle.setPreferredSize(new java.awt.Dimension(150, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        getContentPane().add(cmbTitle, gridBagConstraints);

        labChannel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labChannel.setText( FreeGuide.msg.getString( "channel_is" ) + ":" );
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(cmbChannel, gridBagConstraints);

        labAfter.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labAfter.setText( FreeGuide.msg.getString( "on_after" ) + ":" );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labAfter, gridBagConstraints);

        txtAfter.setMinimumSize(new java.awt.Dimension(50, 25));
        txtAfter.setName("null");
        txtAfter.setPreferredSize(new java.awt.Dimension(50, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtAfter, gridBagConstraints);

        txtBefore.setMinimumSize(new java.awt.Dimension(50, 25));
        txtBefore.setPreferredSize(new java.awt.Dimension(50, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(txtBefore, gridBagConstraints);

        labBefore.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labBefore.setText( FreeGuide.msg.getString( "on_before" ) + ":" );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labBefore, gridBagConstraints);

        labBlankFields.setFont(new java.awt.Font("Dialog", 0, 12));
        labBlankFields.setHorizontalAlignment(
            javax.swing.SwingConstants.CENTER );
        labBlankFields.setText( FreeGuide.msg.getString(
            "you_may_leave_any_fields_blank" ) );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(labBlankFields, gridBagConstraints);

        labTimeFormat.setFont(new java.awt.Font("Dialog", 0, 12));
        labTimeFormat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labTimeFormat.setText( FreeGuide.msg.getString(
            "times_should_be_entered" ) );
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
        labDayOfWeek.setText( FreeGuide.msg.getString( "on_day_label" ) + ":" );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(labDayOfWeek, gridBagConstraints);

        labName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labName.setText( FreeGuide.msg.getString( "name" ) + ":" );
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 0.9;
        getContentPane().add(cmbDayOfWeek, gridBagConstraints);

        txtName.setEditable(false);
        txtName.setText( FreeGuide.msg.getString( "all_programmes" ) );
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

        butOK.setText( FreeGuide.msg.getString( "ok" ) );
        butOK.setMinimumSize(new java.awt.Dimension(87, 26));
        butOK.setPreferredSize(new java.awt.Dimension(87, 26));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(butOK, gridBagConstraints);

        butCancel.setText( FreeGuide.msg.getString( "cancel" ) );
        butCancel.setMinimumSize(new java.awt.Dimension(87, 26));
        butCancel.setPreferredSize(new java.awt.Dimension(87, 26));
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
        labTimeFormat1.setText( FreeGuide.msg.getString( "as_hhmm" ) );
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.4;
        getContentPane().add(labTimeFormat1, gridBagConstraints);

        getRootPane().setDefaultButton( butOK );
        
        pack();
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
            .getScreenSize();
        setSize(new java.awt.Dimension(400, 300));
        setLocation((screenSize.width - 400) / 2,
            (screenSize.height - 300) / 2);
    }

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void txtTitleActionPerformed(java.awt.event.ActionEvent evt) {

        calcTxtName();
    }



    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void cmbDayOfWeekActionPerformed(java.awt.event.ActionEvent evt) {

        calcTxtName();
    }

    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void txtBeforeActionPerformed(java.awt.event.ActionEvent evt) {

        calcTxtName();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void txtAfterActionPerformed(java.awt.event.ActionEvent evt) {

        calcTxtName();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void cmbChannelActionPerformed(java.awt.event.ActionEvent evt) {

        calcTxtName();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void cmbTitleActionPerformed(java.awt.event.ActionEvent evt) {

        calcTxtName();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {

        quit();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butOKActionPerformed(java.awt.event.ActionEvent evt) {

        updateFavourite();
        quit();

    }

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

    Vector channels;
    Favourite favourite;

    private final static SimpleDateFormat dayOfWeekFormat
        = new SimpleDateFormat("EEEE");

}

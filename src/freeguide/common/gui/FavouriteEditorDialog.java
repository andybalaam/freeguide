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
package freeguide.common.gui;

import freeguide.common.gui.FGDialog;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.general.Time;
import freeguide.common.lib.general.Utils;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.JDialog;

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
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class FavouriteEditorDialog extends FGDialog
{
    private final static SimpleDateFormat dayOfWeekFormat =
        new SimpleDateFormat( "EEEE" );
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butOK;
    private javax.swing.JComboBox cmbChannel;
    private Map channels = new TreeMap(  );
    private javax.swing.JComboBox cmbDayOfWeek;
    private javax.swing.JComboBox cmbTitle;
    private javax.swing.JLabel labAfter;
    private javax.swing.JLabel labBefore;
    private javax.swing.JLabel labBlankFields;
    private javax.swing.JLabel labChannel;
    private javax.swing.JLabel labDayOfWeek;
    private javax.swing.JLabel labName;
    private javax.swing.JLabel labTimeFormat;

    //    private javax.swing.JLabel labTimeFormat1;
    private javax.swing.JLabel labTitle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtAfter;
    private javax.swing.JTextField txtBefore;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtTitle;
    Favourite favourite;

/**
     * Constructor which sets the favourites editor up as a JDialog...
     *
     * @param owner - the <code>JFrame</code> from which the dialog is
     *        displayed
     * @param title - the <code>String</code> to display in the dialog's title
     *        bar
     * @param favourite - the <code>Favourite</code> to modify
     * @param allChannelsSet DOCUMENT ME!
     */
    public FavouriteEditorDialog( 
        JDialog owner, String title, Favourite favourite,
        TVChannelsSet allChannelsSet )
    {
        super( owner, title );
        this.favourite = favourite;
        initComponents(  );
        Utils.centreDialog( owner, this );
        fillLists( allChannelsSet );
        getDetails(  );
        addActionListeners(  );
    }

    /**
     * Gets the details from the favourite and fills in the UI
     * elements
     */
    private void getDetails(  )
    {
        if( favourite.getTitleString(  ) != null )
        {
            txtTitle.setText( favourite.getTitleString(  ) );
            cmbTitle.setSelectedItem( 
                Application.getInstance(  ).getLocalizedMessage( "exactly" ) );
        }
        else if( favourite.getTitleContains(  ) != null )
        {
            txtTitle.setText( favourite.getTitleContains(  ) );
            cmbTitle.setSelectedItem( 
                Application.getInstance(  ).getLocalizedMessage( "contains" ) );
        }
        else if( favourite.getTitleRegex(  ) != null )
        {
            txtTitle.setText( favourite.getTitleRegex(  ) );
            cmbTitle.setSelectedItem( 
                Application.getInstance(  )
                           .getLocalizedMessage( "regular_expression" ) );
        }

        if( favourite.getChannelID(  ) != null )
        {
            TVChannelsSet.Channel ch =
                (TVChannelsSet.Channel)channels.get( 
                    favourite.getChannelID(  ) );

            if( ch != null )
            {
                cmbChannel.setSelectedItem( ch );
            }
        }

        if( !favourite.getAfterTime(  ).isEmpty(  ) )
        {
            txtAfter.setText( favourite.getAfterTime(  ).getHHMMString(  ) );
        }

        if( !favourite.getBeforeTime(  ).isEmpty(  ) )
        {
            txtBefore.setText( favourite.getBeforeTime(  ).getHHMMString(  ) );
        }

        if( favourite.getDayOfWeek(  ) != -1 )
        {
            cmbDayOfWeek.setSelectedIndex( favourite.getDayOfWeek(  ) );
        }

        calcTxtName(  );
    }

    /**
     * Description of the Method
     *
     * @param allChannelsSet DOCUMENT ME!
     */
    private void fillLists( TVChannelsSet allChannelsSet )
    {
        // The combobox for the title match type
        cmbTitle.addItem( 
            Application.getInstance(  ).getLocalizedMessage( "exactly" ) );
        cmbTitle.addItem( 
            Application.getInstance(  ).getLocalizedMessage( "contains" ) );
        cmbTitle.addItem( 
            Application.getInstance(  )
                       .getLocalizedMessage( "regular_expression" ) );
        cmbChannel.addItem( "" );

        Iterator it = allChannelsSet.getChannels(  ).iterator(  );

        while( it.hasNext(  ) )
        {
            TVChannelsSet.Channel ch = (TVChannelsSet.Channel)it.next(  );
            cmbChannel.addItem( ch );
            channels.put( ch.getChannelID(  ), ch );
        }

        Calendar cal = GregorianCalendar.getInstance(  );
        cmbDayOfWeek.addItem( "" );
        cal.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );
        cmbDayOfWeek.addItem( dayOfWeekFormat.format( cal.getTime(  ) ) );
        cal.set( Calendar.DAY_OF_WEEK, Calendar.MONDAY );
        cmbDayOfWeek.addItem( dayOfWeekFormat.format( cal.getTime(  ) ) );
        cal.set( Calendar.DAY_OF_WEEK, Calendar.TUESDAY );
        cmbDayOfWeek.addItem( dayOfWeekFormat.format( cal.getTime(  ) ) );
        cal.set( Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY );
        cmbDayOfWeek.addItem( dayOfWeekFormat.format( cal.getTime(  ) ) );
        cal.set( Calendar.DAY_OF_WEEK, Calendar.THURSDAY );
        cmbDayOfWeek.addItem( dayOfWeekFormat.format( cal.getTime(  ) ) );
        cal.set( Calendar.DAY_OF_WEEK, Calendar.FRIDAY );
        cmbDayOfWeek.addItem( dayOfWeekFormat.format( cal.getTime(  ) ) );
        cal.set( Calendar.DAY_OF_WEEK, Calendar.SATURDAY );
        cmbDayOfWeek.addItem( dayOfWeekFormat.format( cal.getTime(  ) ) );
    }

    /**
     * Description of the Method
     */
    private void updateFavourite(  )
    {
        // Calculate the name
        calcTxtName(  );

        // Set the name
        favourite.setName( txtName.getText(  ) );

        // Set the title
        if( 
            cmbTitle.getSelectedItem(  )
                        .equals( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "exactly" ) ) )
        {
            favourite.setTitleRegex( null );
            favourite.setTitleContains( null );

            String tmp = txtTitle.getText(  );

            if( tmp.equals( "" ) )
            {
                favourite.setTitleString( null );
            }
            else
            {
                favourite.setTitleString( tmp );
            }
        }
        else if( 
            cmbTitle.getSelectedItem(  )
                        .equals( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "contains" ) ) )
        {
            favourite.setTitleString( null );
            favourite.setTitleRegex( null );

            String tmp = txtTitle.getText(  );

            if( tmp.equals( "" ) )
            {
                favourite.setTitleContains( null );
            }
            else
            {
                favourite.setTitleContains( tmp );
            }
        }
        else
        {
            favourite.setTitleString( null );
            favourite.setTitleContains( null );

            String tmp = txtTitle.getText(  );

            if( tmp.equals( "" ) )
            {
                favourite.setTitleRegex( null );
            }
            else
            {
                favourite.setTitleRegex( tmp );
            }
        }

        // Set the channel
        Object sel = cmbChannel.getSelectedItem(  );

        if( sel instanceof TVChannelsSet.Channel )
        {
            favourite.setChannelID( 
                ( (TVChannelsSet.Channel)sel ).getChannelID(  ) );
        }
        else
        {
            favourite.setChannelID( null );
        }

        // Set the after time
        String tmp = txtAfter.getText(  );

        if( 
            !tmp.equals( "" ) && ( tmp.length(  ) == 5 )
                && ( tmp.charAt( 2 ) == ':' ) )
        {
            //String hhmm = tmp.substring(0,2) + tmp.substring(3);
            favourite.setAfterTime( new Time( tmp ) );
        }
        else
        {
            favourite.setAfterTime( new Time(  ) );
        }

        // Set the before time
        tmp = txtBefore.getText(  );

        if( 
            !tmp.equals( "" ) && ( tmp.length(  ) == 5 )
                && ( tmp.charAt( 2 ) == ':' ) )
        {
            //String hhmm = tmp.substring(0,2) + tmp.substring(3);
            favourite.setBeforeTime( new Time( tmp ) );
        }
        else
        {
            favourite.setBeforeTime( new Time(  ) );
        }

        // Set the day of the week
        tmp = (String)cmbDayOfWeek.getSelectedItem(  );

        if( !tmp.equals( "" ) )
        {
            Calendar cal = GregorianCalendar.getInstance(  );

            try
            {
                cal.setTime( dayOfWeekFormat.parse( tmp ) );
                favourite.setDayOfWeek( cal.get( Calendar.DAY_OF_WEEK ) );
            }
            catch( java.text.ParseException ex )
            {
                Application.getInstance(  ).getLogger(  )
                           .log( 
                    Level.WARNING, "Error on parse day of week", ex );
                favourite.setDayOfWeek( -1 );
            }
        }
        else
        {
            favourite.setDayOfWeek( -1 );
        }

        setSave(  );
    }

    /**
     * Give the favourite a name depending on its properties
     */
    private void calcTxtName(  )
    {
        // Find the properties of the favourite
        String title = txtTitle.getText(  );
        String channel = cmbChannel.getSelectedItem(  ).toString(  );
        String after = txtAfter.getText(  );
        String before = txtBefore.getText(  );
        String dayOfWeek = cmbDayOfWeek.getSelectedItem(  ).toString(  );

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

        if( !title.equals( "" ) )
        {
            if( 
                cmbTitle.getSelectedItem(  )
                            .equals( 
                        Application.getInstance(  )
                                       .getLocalizedMessage( "exactly" ) ) )
            {
                equalsString = Application.getInstance(  )
                                          .getLocalizedMessage( 
                        "favourite_name_equals_template", equalsArray );
            }
            else if( 
                cmbTitle.getSelectedItem(  )
                            .equals( 
                        Application.getInstance(  )
                                       .getLocalizedMessage( "contains" ) ) )
            {
                containsString = Application.getInstance(  )
                                            .getLocalizedMessage( 
                        "favourite_name_contains_template", containsArray );
            }
            else
            {
                regexpString = Application.getInstance(  )
                                          .getLocalizedMessage( 
                        "favourite_name_regexp_template", regexpArray );
            }
        }

        if( !channel.equals( "" ) )
        {
            channelString = Application.getInstance(  )
                                       .getLocalizedMessage( 
                    "favourite_name_channel_template", channelArray );
        }

        if( !after.equals( "" ) )
        {
            afterString = Application.getInstance(  )
                                     .getLocalizedMessage( 
                    "favourite_name_after_template", afterArray );
        }

        if( !before.equals( "" ) )
        {
            beforeString = Application.getInstance(  )
                                      .getLocalizedMessage( 
                    "favourite_name_before_template", beforeArray );
        }

        if( !dayOfWeek.equals( "" ) )
        {
            dayOfWeekString = Application.getInstance(  )
                                         .getLocalizedMessage( 
                    "favourite_name_day_of_week_template", dayOfWeekArray );
        }

        Object[] nameArgs =
            {
                equalsString, containsString, regexpString, channelString,
                
                beforeString, afterString, dayOfWeekString
            };
        String name =
            Application.getInstance(  )
                       .getLocalizedMessage( 
                "favourite_name_template", nameArgs );
        name = name.replaceAll( "\\s+", " " ).trim(  );

        if( name.equals( "" ) )
        {
            name = Application.getInstance(  )
                              .getLocalizedMessage( "all_programmes" );
        }

        txtName.setText( name );
    }

    /**
     * Add action listeners to the UI components
     */
    private void addActionListeners(  )
    {
        txtTitle.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    txtTitleActionPerformed( evt );
                }
            } );
        cmbTitle.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    cmbTitleActionPerformed( evt );
                }
            } );
        cmbChannel.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    cmbChannelActionPerformed( evt );
                }
            } );
        txtAfter.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    txtAfterActionPerformed( evt );
                }
            } );
        txtBefore.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    txtBeforeActionPerformed( evt );
                }
            } );
        cmbDayOfWeek.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    cmbDayOfWeekActionPerformed( evt );
                }
            } );
        butOK.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butOKActionPerformed( evt );
                }
            } );
        butCancel.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butCancelActionPerformed( evt );
                }
            } );
    }

    /**
     * Create the UI
     */
    private void initComponents(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;
        getContentPane(  ).setLayout( new java.awt.GridBagLayout(  ) );
        labTitle = new javax.swing.JLabel( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "title_matches" ) + ":", javax.swing.SwingConstants.RIGHT );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labTitle, gridBagConstraints );
        txtTitle = new javax.swing.JTextField(  );
        txtTitle.setMinimumSize( new java.awt.Dimension( 50, 25 ) );
        txtTitle.setPreferredSize( new java.awt.Dimension( 200, 25 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( txtTitle, gridBagConstraints );
        cmbTitle = new javax.swing.JComboBox(  );
        cmbTitle.setPreferredSize( new java.awt.Dimension( 150, 25 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( cmbTitle, gridBagConstraints );
        labChannel = new javax.swing.JLabel( 
                Application.getInstance(  ).getLocalizedMessage( "channel_is" )
                + ":", javax.swing.SwingConstants.RIGHT );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labChannel, gridBagConstraints );
        cmbChannel = new javax.swing.JComboBox(  );
        cmbChannel.setPreferredSize( new java.awt.Dimension( 200, 25 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.weightx = 0.9;
        getContentPane(  ).add( cmbChannel, gridBagConstraints );
        labAfter = new javax.swing.JLabel( 
                Application.getInstance(  ).getLocalizedMessage( "on_after" )
                + ":", javax.swing.SwingConstants.RIGHT );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labAfter, gridBagConstraints );
        txtAfter = new javax.swing.JTextField(  );
        txtAfter.setMinimumSize( new java.awt.Dimension( 50, 25 ) );
        txtAfter.setPreferredSize( new java.awt.Dimension( 50, 25 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        getContentPane(  ).add( txtAfter, gridBagConstraints );
        txtBefore = new javax.swing.JTextField(  );
        txtBefore.setMinimumSize( new java.awt.Dimension( 50, 25 ) );
        txtBefore.setPreferredSize( new java.awt.Dimension( 50, 25 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.9;
        getContentPane(  ).add( txtBefore, gridBagConstraints );
        labBefore = new javax.swing.JLabel( 
                Application.getInstance(  ).getLocalizedMessage( "on_before" )
                + ":", javax.swing.SwingConstants.RIGHT );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labBefore, gridBagConstraints );
        labBlankFields = new javax.swing.JLabel( 
                Application.getInstance(  )
                           .getLocalizedMessage( 
                    "you_may_leave_any_fields_blank" ),
                javax.swing.SwingConstants.CENTER );
        labBlankFields.setFont( new java.awt.Font( "Dialog", 0, 12 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane(  ).add( labBlankFields, gridBagConstraints );
        labTimeFormat = new javax.swing.JLabel( 
                "<html>"
                + Application.getInstance(  )
                             .getLocalizedMessage( 
                    "times_should_be_entered_as_hhmm" ) + "</html>",
                javax.swing.SwingConstants.LEFT );
        labTimeFormat.setFont( new java.awt.Font( "Dialog", 0, 12 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.4;
        getContentPane(  ).add( labTimeFormat, gridBagConstraints );
        labDayOfWeek = new javax.swing.JLabel( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "on_day_label" ) + ":", javax.swing.SwingConstants.RIGHT );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labDayOfWeek, gridBagConstraints );
        labName = new javax.swing.JLabel( 
                Application.getInstance(  ).getLocalizedMessage( "name" )
                + ":", javax.swing.SwingConstants.RIGHT );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labName, gridBagConstraints );
        cmbDayOfWeek = new javax.swing.JComboBox(  );
        cmbDayOfWeek.setPreferredSize( new java.awt.Dimension( 200, 25 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.weightx = 0.9;
        getContentPane(  ).add( cmbDayOfWeek, gridBagConstraints );
        txtName = new javax.swing.JTextField( 
                Application.getInstance(  )
                           .getLocalizedMessage( "all_programmes" ) );
        txtName.setEditable( false );
        txtName.setPreferredSize( new java.awt.Dimension( 200, 25 ) );
        txtName.setMinimumSize( new java.awt.Dimension( 200, 25 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.weightx = 0.9;
        getContentPane(  ).add( txtName, gridBagConstraints );
        jPanel1 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );
        butOK = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );
        butOK.setMinimumSize( new java.awt.Dimension( 87, 26 ) );
        butOK.setPreferredSize( new java.awt.Dimension( 87, 26 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        jPanel1.add( butOK, gridBagConstraints );
        butCancel = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "cancel" ) );
        butCancel.setMinimumSize( new java.awt.Dimension( 87, 26 ) );
        butCancel.setPreferredSize( new java.awt.Dimension( 87, 26 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 10 );
        jPanel1.add( butCancel, gridBagConstraints );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane(  ).add( jPanel1, gridBagConstraints );

        /*labTimeFormat1 = new javax.swing.JLabel(
        FreeGuide.msg.getString( "as_hhmm" ),
        javax.swing.SwingConstants.LEFT );
        labTimeFormat1.setFont( new java.awt.Font( "Dialog", 0, 12 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.4;
        getContentPane(  ).add( labTimeFormat1, gridBagConstraints );
        */
        getRootPane(  ).setDefaultButton( butOK );
        pack(  );

        setSize( new java.awt.Dimension( 400, 300 ) );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void txtTitleActionPerformed( java.awt.event.ActionEvent evt )
    {
        calcTxtName(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void cmbDayOfWeekActionPerformed( java.awt.event.ActionEvent evt )
    {
        calcTxtName(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void txtBeforeActionPerformed( java.awt.event.ActionEvent evt )
    {
        calcTxtName(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void txtAfterActionPerformed( java.awt.event.ActionEvent evt )
    {
        calcTxtName(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void cmbChannelActionPerformed( java.awt.event.ActionEvent evt )
    {
        calcTxtName(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void cmbTitleActionPerformed( java.awt.event.ActionEvent evt )
    {
        calcTxtName(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butCancelActionPerformed( java.awt.event.ActionEvent evt )
    {
        quit(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butOKActionPerformed( java.awt.event.ActionEvent evt )
    {
        updateFavourite(  );
        setChanged(  );
        setSave(  );
        quit(  );
    }
}

package freeguide.plugins.ui.horizontal;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleConfigurationUI;
import freeguide.plugins.IModuleStorage;
import freeguide.plugins.IModuleViewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HorizontalViewer extends BaseModule implements IModuleViewer
{

    /** Time formatter for 12 hour clock */
    public final static SimpleDateFormat timeFormat12Hour =
        new SimpleDateFormat( "hh:mm aa" );

    /** Time formatter for 24 hour clock */
    public final static SimpleDateFormat timeFormat24Hour =
        new SimpleDateFormat( "HH:mm" );

    /** How to format dates that go in filenames */
    public final static SimpleDateFormat fileDateFormat =
        new SimpleDateFormat( "yyyyMMdd" );

    /** Date formatter */
    public SimpleDateFormat comboBoxDateFormat =
        new SimpleDateFormat( "EEEE d MMM yy" );

    /** Date formatter */
    public SimpleDateFormat htmlDateFormat =
        new SimpleDateFormat( "EEEE dd MMMM yyyy" );

    /** DOCUMENT ME! */
    public HorizontalViewerConfig config = new HorizontalViewerConfig(  );
    ViewerFrame panel;
    long theDate;

    /** DOCUMENT ME! */
    public TVData currentData = new TVData(  );

    /**
     * A list of dates that have data worked out from the filenames in the
     * data dirctory.
     */
    public long[] dateExistList;

    /** The channel the user last right clicked on */
    public ChannelJLabel rightClickedChannel;

    /** DOCUMENT ME! */
    public long MILLISECONDS_PER_DAY = 24L * 60L * 60L * 1000L;

    /** The action listener for when the item changes in the date combo */
    public ItemListener comboDateItemListener =
        ( new ItemListener(  )
        {
            public void itemStateChanged( ItemEvent evt )
            {

                // Do nothing if this isn't an item selection
                if( evt.getStateChange(  ) == ItemEvent.SELECTED )
                {

                    long oldDate = theDate;

                    // Set theDate to the date chosen
                    theDate =
                        dateExistList[panel.getComboDate(  ).getSelectedIndex(  )];

                    if( oldDate != theDate )
                    {
                        loadData(  );

                        redraw(  );

                    }
                }
            }
        } );

    /** The action listener for when the item changes in the channelset combo */
    public ItemListener comboChannelsSetItemListener =
        ( new ItemListener(  )
        {
            public void itemStateChanged( ItemEvent evt )
            {

                // Do nothing if this isn't an item selection
                if( evt.getStateChange(  ) == ItemEvent.SELECTED )
                {

                    int selectedInd =
                        panel.getComboChannelsSet(  ).getSelectedIndex(  );

                    if( selectedInd == 0 )
                    { // all channels set
                        config.currentChannelSetName = null;

                        loadData(  );

                        redraw(  );

                    }

                    else if( 
                        selectedInd == ( panel.getComboChannelsSet(  )
                                                  .getItemCount(  ) - 1 ) )
                    { // edit set
                        Application.getInstance(  ).doEditChannelsSets(  );

                    }

                    else
                    { // select set
                        config.currentChannelSetName =
                            (String)panel.getComboChannelsSet(  )
                                         .getSelectedItem(  );

                        loadData(  );

                        redraw(  );

                    }
                }
            }
        } );

    /**
     * DOCUMENT_ME!
     *
     * @param locale DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void setLocale( Locale locale ) throws Exception
    {
        super.setLocale( locale );
        comboBoxDateFormat = new SimpleDateFormat( "EEEE d MMM yy", locale );
        htmlDateFormat = new SimpleDateFormat( "EEEE dd MMMM yyyy", locale );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JPanel getPanel(  )
    {

        if( panel == null )
        {
            panel = new ViewerFrame( this );

        }

        return panel;

    }

    /**
     * DOCUMENT_ME!
     */
    public void open(  )
    {
        loadConfig(  );

        theDate = System.currentTimeMillis(  );

        prepareDateList(  );

        prepareChannelsSetList(  );

        onDataChanged(  );

        goToNow(  );

        // Check the FreeGuide version
        /*if( !"no".equals( FreeGuide.config.privacyInfo ) )
        {
        
        // Run the check in a separate thread to avoid blocking.
        new VersionCheckerThread(
        Application.getInstance(  ).getApplicationFrame(  ) ).start(  );
        
        }*/
        // Ask the user to download more data if it is missing
        checkForNoData(  );

        panel.getButtonGoToNow(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent evt )
                {
                    goToNow(  );

                }
            } );

        panel.getButtonDownload(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent evt )
                {
                    Application.getInstance(  ).doStartGrabbers(  );

                }
            } );
    }

    /**
     * Checks whether the XMLTVLoader managed to get any data, and asks the
     * user to download more if not.
     */
    private void checkForNoData(  )
    {

        if( 
            !Application.getInstance(  ).getDataStorage(  ).getInfo(  )
                            .isEmpty(  ) )
        {

            return;

        }

        Object[] oa = new Object[2];

        oa[0] =
            getLocalizer(  ).getLocalizedMessage( 
                "there_are_missing_listings_for_today.1" );

        oa[1] =
            getLocalizer(  ).getLocalizedMessage( 
                "there_are_missing_listings_for_today.2" );

        int r =
            JOptionPane.showConfirmDialog( 
                getPanel(  ), oa,
                getLocalizer(  ).getLocalizedMessage( "download_listings_q" ),
                JOptionPane.YES_NO_OPTION );

        if( r == 0 )
        {
            Application.getInstance(  ).doStartGrabbers(  );

        }
    }

    /**
     * Find out today's date
     */

    /*private void findInitialDate(  )
    
    
    {
    
    
    
    
    // Set the date to today
    
    
    theDate = System.currentTimeMillis(  );
    
    
    theDate = ( theDate / MILLISECONDS_PER_DAY ) * MILLISECONDS_PER_DAY;
    
    
    
    
    Time nowTime = new Time( new Date( theDate ) );
    
    
    
    
    //TODO Time day_start_time = FreeGuide.prefs.misc.getTime( "day_start_time", new Time( 0, 0 ) );
    
    
    Time day_start_time = new Time( 0, 0 );
    
    
    
    
    if( nowTime.before( day_start_time, new Time( 0, 0 ) ) )
    
    
    {
    
    
    theDate -= MILLISECONDS_PER_DAY; //.add( Calendar.DAY_OF_YEAR, -1 );
    
    
    
    
    }
    
    
    }*/

    /**
     * DOCUMENT_ME!
     */
    public void close(  )
    {
        saveConfig(  );
    }

    protected void loadConfig(  )
    {
        loadObjectFromPreferences( config );

        panel.splitPaneChanProg.setDividerLocation( 
            config.positionSplitPaneVertical );

        panel.splitPaneMainDet.setDividerLocation( 
            config.positionSplitPaneHorizontalTop );

        panel.splitPaneGuideDet.setDividerLocation( 
            config.positionSplitPaneHorizontalBottom );

    }

    protected void saveConfig(  )
    {
        config.positionSplitPaneVertical =
            panel.splitPaneChanProg.getDividerLocation(  );

        config.positionSplitPaneHorizontalTop =
            panel.splitPaneMainDet.getDividerLocation(  );

        config.positionSplitPaneHorizontalBottom =
            panel.splitPaneGuideDet.getDividerLocation(  );

        saveObjectToPreferences( config );

    }

    /**
     * DOCUMENT_ME!
     */
    public void redraw(  )
    {

        // Refresh the programmes
        drawProgrammes(  );

        // Refresh the printed guide
        panel.getPrintedGuideArea(  ).update(  );

        panel.getDetailsPanel(  ).updateProgramme( null );

    }

    /**
     * DOCUMENT_ME!
     */
    public void redrawPersonalizedGuide(  )
    {
        panel.getPrintedGuideArea(  ).update(  );
    }

    /**
     * Scroll to now on the time line, and update the screen if this involves
     * changing the day.
     */
    public void goToNow(  )
    {
        goToDate( System.currentTimeMillis(  ) );

        scrollToNow(  );

    }

    private void scrollToNow(  )
    {

        long now = System.currentTimeMillis(  );

        panel.getProgrammesPanel(  ).focus( now );

        panel.getProgrammesScrollPane(  ).getHorizontalScrollBar(  ).setValue( 
            panel.getTimePanel(  ).getScrollValue( now ) - 100 );

    }

    protected void loadData(  )
    {

        synchronized( this )
        {

            try
            {
                currentData =
                    Application.getInstance(  ).getDataStorage(  ).get( 
                        getChannelsSetByName( config.currentChannelSetName ),
                        theDate, theDate + MILLISECONDS_PER_DAY );

            }
            catch( Exception ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING, "Error reading TV data", ex );
            }
        }
    }

    /**
     * Draw all the programmes and channels on screen
     */
    private void drawProgrammes(  )
    {

        synchronized( this )
        {

            /** The chosen time formatter */
            SimpleDateFormat timeFormat;

            String lineBreak = System.getProperty( "line.separator" );

            ProgrammeJLabel.setMovieColour( config.colorMovie );

            ProgrammeJLabel.setNonTickedColour( config.colorNonTicked );

            ProgrammeJLabel.setAlignTextToLeftOfScreen( 
                config.displayAlignToLeft );

            if( config.displayTime )
            {

                if( config.display24time )
                {
                    timeFormat = timeFormat24Hour;

                }

                else
                {
                    timeFormat = timeFormat12Hour;

                }

                timeFormat.setTimeZone( 
                    Application.getInstance(  ).getTimeZone(  ) );

            }

            else
            {
                timeFormat = null;

            }

            Font channelFont =
                new Font( config.fontName, Font.BOLD, config.fontSize );

            Font font =
                new Font( config.fontName, config.fontStyle, config.fontSize );

            panel.getChannelNamePanel(  ).removeAll(  );

            TVChannelsSet currentChannelSet =
                (TVChannelsSet)getChannelsSetByName( 
                    config.currentChannelSetName ).clone(  );

            Iterator it = currentChannelSet.getChannels(  ).iterator(  );

            while( it.hasNext(  ) )
            {

                TVChannelsSet.Channel listCh =
                    (TVChannelsSet.Channel)it.next(  );

                if( !currentData.containsChannel( listCh.getChannelID(  ) ) )
                {
                    it.remove(  );
                }
            }

            //            int num_chans = currentChannelSet.getChannels(  ).size(  );
            // First using the FontMetrics system work out
            // the actual width of the text
            int maxChannelWidth = 0;

            List tmpChannels = new ArrayList(  );

            // Create all the JLabels for channels, and set them up
            it = currentChannelSet.getChannels(  ).iterator(  );

            while( it.hasNext(  ) )
            {

                TVChannelsSet.Channel listCh =
                    (TVChannelsSet.Channel)it.next(  );

                if( !currentData.containsChannel( listCh.getChannelID(  ) ) )
                {

                    continue;
                }

                TVChannel curChan = currentData.get( listCh.getChannelID(  ) );

                ChannelJLabel ctxt = new ChannelJLabel( curChan );

                ctxt.setBackground( config.colorChannel );

                ctxt.setFont( font );

                ctxt.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );

                ctxt.setHorizontalAlignment( JLabel.LEFT );

                ctxt.setOpaque( true );

                // Get the URL of this channel icon
                String iconURLstr = ctxt.getChannel(  ).getIconURL(  );

                if( 
                    ( iconURLstr != null ) /*TODO  || ( FreeGuide.prefs.screen.get(
                    
                    
                    "customIcon." + ctxt.getChannel(  ).getID(  ) ) != null ) */ )
                {

                    try
                    {

                        ImageIcon iconImg = null;

                        // If a custom icon is set use it !
                        File iconFile = null;

                        ImageIcon tmpImg;

                        /*TODO   if(
                        
                        
                        FreeGuide.prefs.screen.get(
                        
                        
                        "customIcon." + ctxt.getChannel(  ).getID(  ) ) != null )
                        
                        
                        {
                        
                        
                        iconFile =
                        
                        
                        new File(
                        
                        
                        FreeGuide.prefs.screen.get(
                        
                        
                        "customIcon."
                        
                        
                        + ctxt.getChannel(  ).getID(  ) ) );
                        
                        
                        }
                        
                        
                        else*/
                        {

                            // First convert the id to a suitable (and safe!!)
                            // filename
                            File cache =
                                new File( 
                                    ctxt.getChannel(  ).getIconFileName(  ) );

                            // then verify if the file is in the cache
                            if( !cache.canRead(  ) )
                            {

                                // if not, we try to fetch it from the url
                                URL iconURL = new URL( iconURLstr );

                                InputStream i = iconURL.openStream(  );

                                FileOutputStream o =
                                    new FileOutputStream( cache );

                                byte[] buffer = new byte[4096];

                                int bCount;

                                while( ( bCount = i.read( buffer ) ) != -1 )
                                {
                                    o.write( buffer, 0, bCount );

                                }

                                o.close(  );

                                i.close(  );

                            }

                            iconFile = cache;

                        }

                        /* We then try to read the file which should be in
                        * the cache If it's not, it doesn't matter,
                        * either the URL is not valid or the file
                        * couldn't be read and we should have left the
                        * try anyway, or we will when we try to read it
                        * Thus the icon will still be equal to null and
                        * we won't show one
                        */
                        ctxt.setIcon( 
                            iconFile.getCanonicalPath(  ),
                            config.sizeChannelHeight
                            - ( config.sizeHalfVerGap * 4 ) );

                    }
                    catch( MalformedURLException ex )
                    {
                        Application.getInstance(  ).getLogger(  ).log( 
                            Level.FINE, "Error cache channel icon", ex );
                    }
                    catch( IOException ex )
                    {
                        Application.getInstance(  ).getLogger(  ).log( 
                            Level.FINE, "Error cache channel icon", ex );
                    }
                }

                // TODO Give it a default icon if one is not available

                /*if( ctxt.getIcon() == null ) {
                
                
                ctxt.setIcon( FreeGuide.prefs.performSubstitutions(
                
                
                FreeGuide.prefs.misc.get( "channel_icon_default" ) ) );
                
                
                }*/
                int myChanWidth = ctxt.getRequiredWidth(  );

                if( myChanWidth > maxChannelWidth )
                {
                    maxChannelWidth = myChanWidth;

                }

                ctxt.addMouseListener( 
                    new MouseListener(  )
                    {
                        public void mouseClicked( MouseEvent e )
                        {
                        }

                        public void mouseEntered( MouseEvent e )
                        {
                        }

                        public void mouseExited( MouseEvent e )
                        {
                        }

                        public void mousePressed( MouseEvent e )
                        {
                            maybeShowPopup( e );

                        }

                        public void mouseReleased( MouseEvent e )
                        {
                            maybeShowPopup( e );

                        }

                        private void maybeShowPopup( 
                            java.awt.event.MouseEvent evt )
                        {

                            if( evt.isPopupTrigger(  ) )
                            {
                                rightClickedChannel =
                                    (ChannelJLabel)evt.getComponent(  );

                                panel.getPopupMenuChannel(  ).show( 
                                    evt.getComponent(  ), evt.getX(  ),
                                    evt.getY(  ) );

                            }
                        }
                    } );

                panel.getChannelNamePanel(  ).add( ctxt );

                tmpChannels.add( ctxt );

            }

            // Then add a reasonable amount of space as a border
            maxChannelWidth += 5;

            for( int c = 0; c < tmpChannels.size(  ); c++ )
            {

                ChannelJLabel ctxt = (ChannelJLabel)tmpChannels.get( c );

                ctxt.setBounds( 
                    0,
                    ( ( config.sizeHalfVerGap * 2 )
                    + ( c * config.sizeChannelHeight ) ) - 1, maxChannelWidth,
                    config.sizeChannelHeight - ( config.sizeHalfVerGap * 4 ) );

            }

            // Resize the area
            panel.getChannelNamePanel(  ).setPreferredSize( 
                new Dimension( 
                    maxChannelWidth,
                    ( currentChannelSet.getChannels(  ).size(  ) * config.sizeChannelHeight )
                    + 50 ) );

            //}}}
            //{{{ Draw the programmes
            // Set up the programme and time panels
            Dimension tmp =
                new Dimension( 
                    config.sizeProgrammePanelWidth,
                    currentChannelSet.getChannels(  ).size(  ) * config.sizeChannelHeight );

            panel.getProgrammesPanel(  ).setPreferredSize( tmp );

            panel.getProgrammesPanel(  ).setMinimumSize( tmp );

            panel.getProgrammesPanel(  ).setMaximumSize( tmp );

            // Temporal width in millisecs
            panel.getProgrammesPanel(  ).setHorizontalRange( 
                theDate, theDate + MILLISECONDS_PER_DAY );

            //currentData.getEarliest(  ), currentData.getLatest(  ) );
            panel.getProgrammesPanel(  ).setIntercellSpacing( 
                config.sizeHalfHorGap * 2, config.sizeHalfVerGap * 2 );

            tmp = new Dimension( 
                    config.sizeProgrammePanelWidth,
                    panel.getTimePanel(  ).getPreferredSize(  ).height );

            panel.getTimePanel(  ).setPreferredSize( tmp );

            panel.getTimePanel(  ).setMinimumSize( tmp );

            panel.getTimePanel(  ).setMaximumSize( tmp );

            panel.getProgrammesPanel(  ).setRenderer( 
                new ProgrammeRenderer( timeFormat, font, this ) );

            panel.getProgrammesPanel(  ).setEditor( 
                new ProgrammeRenderer( timeFormat, font, this ) );

            ProgrammeStripModel m =
                new ProgrammeStripModel( currentChannelSet, currentData );

            panel.getProgrammesPanel(  ).setModel( m );

            panel.getTimePanel(  ).setTimes( 
                theDate, theDate + MILLISECONDS_PER_DAY );

            panel.getTimePanel(  ).revalidate(  );

            panel.getTimePanel(  ).repaint(  );

            panel.getProgrammesPanel(  ).revalidate(  );

            panel.getProgrammesPanel(  ).repaint(  );

            panel.getChannelNamePanel(  ).revalidate(  );

            panel.getChannelNamePanel(  ).repaint(  );

        }
    }

    /**
     * Change the date combo to the given date. Will trigger an event causing
     * a repaint of all the programmes.
     *
     * @param newDate DOCUMENT ME!
     */
    private void goToDate( long newDate )
    {

        for( int i = 0; i < dateExistList.length; i++ )
        {

            if( 
                ( dateExistList[i] <= newDate )
                    && ( newDate < ( dateExistList[i] + MILLISECONDS_PER_DAY ) ) )
            {
                panel.getComboDate(  ).setSelectedIndex( i );

            }
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void onChannelsSetsChanged(  )
    {
        prepareChannelsSetList(  );

    }

    /**
     * DOCUMENT_ME!
     */
    public void onDataChanged(  )
    {
        prepareDateList(  );

        loadData(  );

        goToDate( theDate );

        redraw(  );

    }

    protected void prepareChannelsSetList(  )
    {

        // Stop listening to item events temporarily while we mess about
        panel.getComboChannelsSet(  ).removeItemListener( 
            comboChannelsSetItemListener );

        // Remove all the items from the combo box:
        // Working around a bug with JComboBox.removeAllItems()
        int itemCount = panel.getComboChannelsSet(  ).getItemCount(  );

        for( int i = 0; i < itemCount; i++ )
        {
            panel.getComboChannelsSet(  ).removeItemAt( 0 );

        }

        // Add the "All Channels" item
        panel.getComboChannelsSet(  ).insertItemAt( 
            getLocalizer(  ).getLocalizedMessage( "all_channels" ), 0 );

        for( 
            int i = 0;
                i < Application.getInstance(  ).getChannelsSetsList(  ).size(  );
                i++ )
        {

            TVChannelsSet cs =
                (TVChannelsSet)Application.getInstance(  ).getChannelsSetsList(  )
                                          .get( i );

            panel.getComboChannelsSet(  ).addItem( cs.getName(  ) );

        }

        panel.getComboChannelsSet(  ).addItem( 
            getLocalizer(  ).getLocalizedMessage( "edit_channels_sets" ) );

        TVChannelsSet cs =
            getChannelsSetByName( config.currentChannelSetName );

        if( cs == null )
        {
            config.currentChannelSetName = null;

            panel.getComboChannelsSet(  ).setSelectedIndex( 0 );

        }

        else
        {
            panel.getComboChannelsSet(  ).setSelectedIndex( 
                Application.getInstance(  ).getChannelsSetsList(  ).indexOf( 
                    cs ) + 1 );

        }

        panel.getComboChannelsSet(  ).addItemListener( 
            comboChannelsSetItemListener );

    }

    protected TVChannelsSet getChannelsSetByName( 
        final String channelsSetName )
    {

        if( channelsSetName == null )
        {

            return Application.getInstance(  ).getDataStorage(  ).getInfo(  ).allChannels;

        }

        else
        {

            for( 
                int i = 0;
                    i < Application.getInstance(  ).getChannelsSetsList(  )
                                       .size(  ); i++ )
            {

                TVChannelsSet cs =
                    (TVChannelsSet)Application.getInstance(  )
                                              .getChannelsSetsList(  ).get( i );

                if( channelsSetName.equals( cs.getName(  ) ) )
                {

                    return cs;

                }
            }

            return null;

        }
    }

    protected void prepareDateList(  )
    {

        /**
         * Find the dates available by what files exist.  This sets up a
         * datelister for later so you can just call findDates() later.
         */
        IModuleStorage.Info info =
            Application.getInstance(  ).getDataStorage(  ).getInfo(  );

        Calendar cal =
            GregorianCalendar.getInstance( 
                Application.getInstance(  ).getTimeZone(  ), Locale.ENGLISH );

        cal.setTimeInMillis( info.minDate );

        cal.set( Calendar.HOUR_OF_DAY, config.dayStartTime.getHours(  ) );

        cal.set( Calendar.MINUTE, config.dayStartTime.getMinutes(  ) );

        cal.set( Calendar.SECOND, config.dayStartTime.getSeconds(  ) );

        cal.set( 
            Calendar.MILLISECOND, config.dayStartTime.getMilliseconds(  ) );

        if( cal.getTimeInMillis(  ) > info.minDate )
        {

            // need to go to previous day
            cal.add( Calendar.DATE, -1 );

        }

        List dates = new ArrayList(  );

        for( 
            ; cal.getTimeInMillis(  ) <= info.maxDate;
                cal.add( Calendar.DATE, 1 ) )
        {
            dates.add( new Long( cal.getTimeInMillis(  ) ) );

        }

        dateExistList = new long[dates.size(  )];

        for( int i = 0; i < dates.size(  ); i++ )
        {
            dateExistList[i] = ( (Long)dates.get( i ) ).longValue(  );

        }

        /**
         * Create the combobox lists for the dates and channel sets
         */
        // Stop listening to item events temporarily while we mess about
        panel.getComboDate(  ).removeItemListener( comboDateItemListener );

        // Remove all the items from the combo box:
        // Working around a bug with JComboBox.removeAllItems()
        //comTheDate.removeAllItems();
        int i;

        int itemCount = panel.getComboDate(  ).getItemCount(  );

        for( i = 0; i < itemCount; i++ )
        {
            panel.getComboDate(  ).removeItemAt( 0 );

        }

        comboBoxDateFormat.setTimeZone( 
            Application.getInstance(  ).getTimeZone(  ) );

        int ind = 0;

        for( i = 0; i < dateExistList.length; i++ )
        {
            panel.getComboDate(  ).insertItemAt( 
                comboBoxDateFormat.format( new Date( dateExistList[i] ) ), i );

            if( dateExistList[i] <= theDate )
            {
                ind = i;

            }
        }

        if( ind < dateExistList.length )
        {
            theDate = dateExistList[ind];

            panel.getComboDate(  ).setSelectedIndex( ind );

        }

        panel.getComboDate(  ).addItemListener( comboDateItemListener );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {

        return new ConfigureUIController( this, parentDialog );

    }

    /**
     * DOCUMENT_ME!
     */
    public void printHTML(  )
    {
        panel.printedGuideArea.writeOutAsHTML(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JButton getDefaultButton(  )
    {

        return panel.getDefaultButton(  );
    }
}

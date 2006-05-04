package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.TVChannelIconHelper;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.TemplateParser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleStorage;

import freeguide.common.plugininterfaces.IModuleStorage.Info;

import freeguide.common.plugininterfaces.IModuleViewer;

import freeguide.plugins.ui.horizontal.manylabels.templates.HandlerPersonalGuide;
import freeguide.plugins.ui.horizontal.manylabels.templates.HandlerProgrammeInfo;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

/**
 * Horizontal viewer plugin.
 */
public class HorizontalViewer extends BaseModule implements IModuleViewer
{

    protected static final String REMINDER_MAIN = "reminder-alarm";

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

    /** Config object. */
    protected HorizontalViewerConfig config = new HorizontalViewerConfig(  );

    /** UI object. */
    protected ViewerFrame panel;

    /** Current displayed date. */
    protected long theDate;

    /** Current displayed data. */
    public TVData currentData = new TVData(  );

    /**
     * A list of dates that have data worked out from the filenames in the
     * data dirctory.
     */
    public long[] dateExistList;

    /** Day in milliseconds. */
    public long MILLISECONDS_PER_DAY = 24L * 60L * 60L * 1000L;

    /** Handlers for handle events from UI controls. */
    protected final HorizontalViewerHandlers handlers =
        new HorizontalViewerHandlers( this );
    protected JLabelProgramme currentProgrammeLabel;

    /**
     * Get config object.
     *
     * @return config object
     */
    public Object getConfig(  )
    {

        return config;
    }

    /**
     * Set locale handler.
     *
     * @param locale new locale
     *
     * @throws Exception
     */
    public void setLocale( Locale locale ) throws Exception
    {
        super.setLocale( locale );
        comboBoxDateFormat = new SimpleDateFormat( "EEEE d MMM yy", locale );
        htmlDateFormat = new SimpleDateFormat( "EEEE dd MMMM yyyy", locale );
    }

    /**
     * Get UI panel.
     *
     * @return UI panel
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
     * Start viewer.
     */
    public void open(  )
    {
        panel.splitPaneChanProg.setDividerLocation( 
            config.positionSplitPaneVertical );

        panel.splitPaneMainDet.setDividerLocation( 
            config.positionSplitPaneHorizontalTop );

        panel.splitPaneGuideDet.setDividerLocation( 
            config.positionSplitPaneHorizontalBottom );

        theDate = System.currentTimeMillis(  );

        prepareDateList(  );

        /*if( dateExistList.length > 0 )
        {

            if( 
                ( System.currentTimeMillis(  ) < dateExistList[0] )
                    || ( System.currentTimeMillis(  ) > ( dateExistList[dateExistList.length
                    - 1] + MILLISECONDS_PER_DAY ) ) )
            {
                askForLoadData(  );
            }
        }*/

        prepareChannelsSetList(  );

        onDataChanged(  );

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
        panel.getPrintedGuideArea(  ).addHyperlinkListener( 
            new ViewerFramePersonalGuideListener( this ) );

        panel.getProgrammesScrollPane(  ).validate(  );
        goToNow(  );
    }

    /**
     * Close viewer.
     */
    public void close(  )
    {
        saveConfigNow(  );
        panel = null;
    }

    /**
     * Save config object immediatly.
     */
    protected void saveConfigNow(  )
    {
        config.positionSplitPaneVertical =
            panel.splitPaneChanProg.getDividerLocation(  );

        config.positionSplitPaneHorizontalTop =
            panel.splitPaneMainDet.getDividerLocation(  );

        config.positionSplitPaneHorizontalBottom =
            panel.splitPaneGuideDet.getDividerLocation(  );

        super.saveConfigNow(  );
    }

    /**
     * Redraw screen data.
     */
    public void redraw(  )
    {

        // set scroll units
        panel.getProgrammesScrollPane(  ).getVerticalScrollBar(  )
             .setUnitIncrement( config.sizeChannelHeight );
        panel.getProgrammesScrollPane(  ).getHorizontalScrollBar(  )
             .setUnitIncrement( config.sizeProgrammePanelWidth / 24 / 6 );

        // Refresh the programmes
        drawProgrammes(  );

        updateProgrammeInfo( null );
        updatePersonalizedGuide(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void redrawCurrentProgramme(  )
    {

        if( currentProgrammeLabel != null )
        {
            currentProgrammeLabel.setupColors(  );
            currentProgrammeLabel.repaint(  );
        }

        updatePersonalizedGuide(  );
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

        //---panel.getProgrammesPanel(  ).focus( now );
        panel.getProgrammesScrollPane(  ).getHorizontalScrollBar(  ).setValue( 
            panel.getTimePanel(  ).getScrollValue( now ) - 100 );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Info getDisplayedInfo(  )
    {

        final IModuleStorage.Info info = new IModuleStorage.Info(  );
        info.channelsList =
            getChannelsSetByName( config.currentChannelSetName );
        info.minDate = theDate;
        info.maxDate = theDate + MILLISECONDS_PER_DAY;

        return info;
    }

    /**
     * Load data for current date and selected channels set.
     */
    protected void loadData(  )
    {

        synchronized( this )
        {

            try
            {
                currentData =
                    Application.getInstance(  ).getDataStorage(  ).get( 
                        getDisplayedInfo(  ) );
            }
            catch( Exception ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING, "Error reading TV data", ex );
            }

            /*if( currentData.getChannelsCount(  ) == 0 )
            {
                askForLoadData(  );
            }*/
        }
    }

    /*protected void askForLoadData(  )
    {

        int r =
            JOptionPane.showConfirmDialog( 
                Application.getInstance(  ).getCurrentFrame(  ),
                Application.getInstance(  ).getLocalizedMessage( 
                    "there_are_missing_listings_for_today" ),
                Application.getInstance(  ).getLocalizedMessage( 
                    "download_listings_q" ), JOptionPane.YES_NO_OPTION );

        if( r == 0 )
        {
            Application.getInstance(  ).doStartGrabbers(  );
        }
    }*/

    /**
     * Draw all the programmes and channels on screen.
     */
    protected synchronized void drawProgrammes(  )
    {
        currentProgrammeLabel = null;

        JLabelProgramme.setupLabel( this );

        /** The chosen time formatter */
        DateFormat timeFormat;

        if( config.displayTime )
        {
            timeFormat = getCurrentDateFormat(  );
        }
        else
        {
            timeFormat = null;
        }

        final Font font =
            new Font( config.fontName, config.fontStyle, config.fontSize );

        final TVChannelsSet currentChannelSet =
            (TVChannelsSet)getChannelsSetByName( config.currentChannelSetName )
                               .clone(  );

        // remove non-exists channels from channels set
        Iterator it = currentChannelSet.getChannels(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            TVChannelsSet.Channel listCh = (TVChannelsSet.Channel)it.next(  );

            if( !currentData.containsChannel( listCh.getChannelID(  ) ) )
            {
                it.remove(  );
            }
        }

        panel.getProgrammesPanel(  ).init( 
            theDate, font, currentChannelSet.getChannels(  ).size(  ),
            timeFormat );

        final List channels = new ArrayList(  );

        for( it = currentChannelSet.getChannels(  ).iterator(  );
                it.hasNext(  ); )
        {

            TVChannelsSet.Channel listCh = (TVChannelsSet.Channel)it.next(  );
            TVChannel curChan = currentData.get( listCh.getChannelID(  ) );

            channels.add( curChan );
        }

        panel.getChannelNamePanel(  ).setFont( font );
        panel.getChannelNamePanel(  ).setChanels( 
            (TVChannel[])channels.toArray( new TVChannel[channels.size(  )] ) );

        // Resize the areas
        panel.getChannelNamePanel(  ).setPreferredSize( 
            new Dimension( 
                panel.getChannelNamePanel(  ).getMaxChannelWidth(  ),
                ( currentChannelSet.getChannels(  ).size(  ) * config.sizeChannelHeight )
                + 50 ) );

        Dimension tmp =
            new Dimension( 
                config.sizeProgrammePanelWidth,
                currentChannelSet.getChannels(  ).size(  ) * config.sizeChannelHeight );

        panel.getProgrammesPanel(  ).setPreferredSize( tmp );

        panel.getProgrammesPanel(  ).setMinimumSize( tmp );

        panel.getProgrammesPanel(  ).setMaximumSize( tmp );

        tmp = new Dimension( 
                config.sizeProgrammePanelWidth,
                panel.getTimePanel(  ).getPreferredSize(  ).height );

        panel.getTimePanel(  ).setPreferredSize( tmp );

        panel.getTimePanel(  ).setMinimumSize( tmp );

        panel.getTimePanel(  ).setMaximumSize( tmp );

        panel.getTimePanel(  ).setTimes( 
            theDate, theDate + MILLISECONDS_PER_DAY );

        // Create labels for all programmes
        currentData.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }

                public void onProgramme( TVProgramme programme )
                {

                    int row =
                        currentChannelSet.getChannelIndex( 
                            getCurrentChannel(  ).getID(  ) );

                    if( row != -1 )
                    {
                        panel.getProgrammesPanel(  ).addProgramme( 
                            programme, row );
                    }
                }
            } );

        panel.getProgrammesPanel(  ).sort(  );

        // Repaint screen
        panel.getTimePanel(  ).revalidate(  );
        panel.getTimePanel(  ).repaint(  );
        panel.getProgrammesPanel(  ).revalidate(  );
        panel.getProgrammesPanel(  ).repaint(  );
        panel.getChannelNamePanel(  ).revalidate(  );
        panel.getChannelNamePanel(  ).repaint(  );
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
            handlers.comboChannelsSetItemListener );

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
            handlers.comboChannelsSetItemListener );

    }

    protected TVChannelsSet getChannelsSetByName( 
        final String channelsSetName )
    {

        if( channelsSetName == null )
        {

            return Application.getInstance(  ).getDataStorage(  ).getInfo(  ).channelsList;

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
        panel.getComboDate(  ).removeItemListener( 
            handlers.comboDateItemListener );

        // Remove all the items from the combo box:
        // Working around a bug with JComboBox.removeAllItems()
        //comTheDate.removeAllItems();
        ( (DefaultComboBoxModel)panel.getComboDate(  ).getModel(  ) )
        .removeAllElements(  );

        comboBoxDateFormat.setTimeZone( 
            Application.getInstance(  ).getTimeZone(  ) );

        int ind = 0;

        for( int i = 0; i < dateExistList.length; i++ )
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

        panel.getComboDate(  ).addItemListener( 
            handlers.comboDateItemListener );

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
     *
     * @return DOCUMENT_ME!
     */
    public JButton getDefaultButton(  )
    {

        return panel.getDefaultButton(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channel DOCUMENT_ME!
     */
    public void changeIconActionPerformed( final TVChannel channel )
    {

        JFileChooser chooser = new JFileChooser(  );

        chooser.setFileFilter( 
            new FileFilter(  )
            {

                private Pattern images = null;

                public boolean accept( File f )
                {

                    if( images == null )
                    {
                        images =
                            Pattern.compile( "\\.(?i)(?:jpe?g|gif|png|JPG)$" );

                    }

                    return f.isDirectory(  )
                    || images.matcher( f.getName(  ) ).find(  );

                }

                public String getDescription(  )
                {

                    return getLocalizer(  ).getLocalizedMessage( 
                        "images_gif_jpeg_png" );

                }
            } );

        int returnVal = chooser.showOpenDialog( panel );

        if( returnVal == JFileChooser.APPROVE_OPTION )
        {

            try
            {
                FileHelper.copy( 
                    chooser.getSelectedFile(  ),
                    new File( TVChannelIconHelper.getIconFileName( channel ) ) );
                redraw(  );
            }
            catch( IOException ex )
            {
                ex.printStackTrace(  );
            }
        }
    }

    /**
     * Event handler for the channel -> reset icon menu entry
     *
     * @param channel
     */
    public void resetIconActionPerformed( final TVChannel channel )
    {
        new File( TVChannelIconHelper.getIconFileName( channel ) ).delete(  );
        redraw(  );
    }

    /**
     * Print personalized guide.
     */
    public void printHTML(  )
    {

        // Make a file in the default location
        File f =
            new File( 
                Application.getInstance(  ).getWorkingDirectory(  )
                + "/guide.html" );

        try
        {

            BufferedWriter buffy =
                new BufferedWriter( 
                    new OutputStreamWriter( 
                        new FileOutputStream( f ), "UTF-8" ) );

            /*new PersonalizedHTMLGuide(  ).createHTML(
                buffy, getLocalizer(  ), new Date( theDate ), currentData,
                htmlDateFormat,
                config.display24time ? HorizontalViewer.timeFormat24Hour
                                     : HorizontalViewer.timeFormat12Hour, false );*/
            TemplateParser parser =
                new TemplateParser( 
                    "resources/plugins/ui/horizontal/manylabels/templates/TemplatePersonalGuide.html" );
            parser.process( 
                new HandlerPersonalGuide( 
                    getLocalizer(  ), currentData, new Date( theDate ),
                    htmlDateFormat, getCurrentDateFormat(  ), true ), buffy );
            buffy.close(  );

            FileHelper.openFile( f.getPath(  ) );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING, "Error write HTML guide", ex );
        }
    }

    /**
     * Refresh personalized guide.
     */
    protected void updatePersonalizedGuide(  )
    {

        StringWriter str = new StringWriter(  );

        try
        {

            /*new PersonalizedHTMLGuide(  ).createHTML(
                str, getLocalizer(  ), new Date( theDate ), currentData,
                htmlDateFormat,
                config.display24time ? HorizontalViewer.timeFormat24Hour
                                     : HorizontalViewer.timeFormat12Hour, true );*/
            TemplateParser parser =
                new TemplateParser( 
                    "resources/plugins/ui/horizontal/manylabels/templates/TemplatePersonalGuide.html" );
            parser.process( 
                new HandlerPersonalGuide( 
                    getLocalizer(  ), currentData, new Date( theDate ),
                    htmlDateFormat, getCurrentDateFormat(  ), false ), str );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE,
                "Error construct personalized HTML guide for screen", ex );
        }

        panel.getPrintedGuideArea(  ).setText( str.toString(  ) );

        panel.getPrintedGuideArea(  ).setCaretPosition( 0 );
    }

    /**
     * Refresh programme detail information.
     *
     * @param programme
     */
    protected void updateProgrammeInfo( final TVProgramme programme )
    {

        try
        {
            final TemplateParser parser =
                new TemplateParser( 
                    "resources/plugins/ui/horizontal/manylabels/templates/TemplateProgrammeInfo.html" );
            StringWriter out = new StringWriter(  );
            parser.process( 
                new HandlerProgrammeInfo( 
                    getLocalizer(  ), programme, getCurrentDateFormat(  ) ),
                out );

            panel.getDetailsPanel(  ).setText( out.getBuffer(  ).toString(  ) );
            panel.getDetailsPanel(  ).setCaretPosition( 0 );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE, "Error construct programme info HTML for screen",
                ex );
        }
    }

    protected DateFormat getCurrentDateFormat(  )
    {

        final DateFormat result =
            config.display24time ? HorizontalViewer.timeFormat24Hour
                                 : HorizontalViewer.timeFormat12Hour;
        result.setTimeZone( Application.getInstance(  ).getTimeZone(  ) );

        return result;
    }
}

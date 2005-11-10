package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.PersonalizedHTMLGuide;
import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.TVChannelIconHelper;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.FileHelper;
import freeguide.lib.general.StringHelper;
import freeguide.lib.general.Utils;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleConfigurationUI;
import freeguide.plugins.IModuleStorage;
import freeguide.plugins.IModuleViewer;

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

        if( dateExistList.length > 0 )
        {

            if( 
                ( System.currentTimeMillis(  ) < dateExistList[0] )
                    || ( System.currentTimeMillis(  ) > ( dateExistList[dateExistList.length
                    - 1] + MILLISECONDS_PER_DAY ) ) )
            {
                askForLoadData(  );
            }
        }

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
                        getChannelsSetByName( config.currentChannelSetName ),
                        theDate, theDate + MILLISECONDS_PER_DAY );
            }
            catch( Exception ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING, "Error reading TV data", ex );
            }

            if( currentData.getChannelsCount(  ) == 0 )
            {
                askForLoadData(  );
            }
        }
    }

    protected void askForLoadData(  )
    {

        int r =
            JOptionPane.showConfirmDialog( 
                Application.getInstance(  ).getApplicationFrame(  ),
                Application.getInstance(  ).getLocalizedMessage( 
                    "there_are_missing_listings_for_today" ),
                Application.getInstance(  ).getLocalizedMessage( 
                    "download_listings_q" ), JOptionPane.YES_NO_OPTION );

        if( r == 0 )
        {
            Application.getInstance(  ).doStartGrabbers(  );
        }
    }

    /**
     * Draw all the programmes and channels on screen.
     */
    protected synchronized void drawProgrammes(  )
    {
        currentProgrammeLabel = null;

        panel.getChannelNamePanel(  ).removeAll(  );

        JLabelProgramme.setupLabel( this );

        /** The chosen time formatter */
        SimpleDateFormat timeFormat;

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

        final ProgrammeFormat textFormat =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FORMAT, timeFormat, config.displayDelta );

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
            theDate, textFormat, font,
            currentChannelSet.getChannels(  ).size(  ) );

        int maxChannelWidth = 0;

        JLabelChannel[] displayedChannels =
            new JLabelChannel[currentChannelSet.getChannels(  ).size(  )];

        // Create all the JLabels for channels, and set them up
        it = currentChannelSet.getChannels(  ).iterator(  );

        for( int i = 0; it.hasNext(  ); i++ )
        {

            TVChannelsSet.Channel listCh = (TVChannelsSet.Channel)it.next(  );
            TVChannel curChan = currentData.get( listCh.getChannelID(  ) );
            JLabelChannel ctxt = new JLabelChannel( curChan, this, font );
            maxChannelWidth =
                Math.max( ctxt.getRequiredWidth(  ), maxChannelWidth );
            panel.getChannelNamePanel(  ).add( ctxt );
            displayedChannels[i] = ctxt;
        }

        // Then add a reasonable amount of space as a border
        maxChannelWidth += 5;

        for( int i = 0; i < displayedChannels.length; i++ )
        {
            displayedChannels[i].setupBounds( maxChannelWidth, i );
        }

        // Resize the areas
        panel.getChannelNamePanel(  ).setPreferredSize( 
            new Dimension( 
                maxChannelWidth,
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
        panel.getComboDate(  ).removeItemListener( 
            handlers.comboDateItemListener );

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

            new PersonalizedHTMLGuide(  ).createHTML( 
                buffy, getLocalizer(  ), new Date( theDate ), currentData,
                htmlDateFormat,
                config.display24time ? HorizontalViewer.timeFormat24Hour
                                     : HorizontalViewer.timeFormat12Hour, false );

            buffy.close(  );

            String cmd =
                StringHelper.replaceAll( 
                    Application.getInstance(  ).getBrowserCommand(  ),
                    "%filename%", f.getPath(  ) );

            Utils.execNoWait( cmd );
        }
        catch( java.io.IOException ex )
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
            new PersonalizedHTMLGuide(  ).createHTML( 
                str, getLocalizer(  ), new Date( theDate ), currentData,
                htmlDateFormat,
                config.display24time ? HorizontalViewer.timeFormat24Hour
                                     : HorizontalViewer.timeFormat12Hour, true );

            /*TemplateParser parser =
                new TemplateParser(
                    "freeguide/plugins/ui/horizontal/manylabels/templates/TemplatePersonalGuide.html" );
            parser.process(
                new HandlerPersonalGuide(
                    getLocalizer(  ), currentData, new Date( theDate ),
                    htmlDateFormat,
                    config.display24time ? HorizontalViewer.timeFormat24Hour
                                         : HorizontalViewer.timeFormat12Hour,
                    false ), str );*/
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

        // Find out whether we're in the 24 hour clock
        boolean draw24time = true;

        //TODO FreeGuide.prefs.screen.getBoolean( "display_24hour_time", true );
        // And get the time format from that
        SimpleDateFormat timeFormat;

        if( draw24time )
        {
            timeFormat = HorizontalViewer.timeFormat24Hour;
        }
        else
        {
            timeFormat = HorizontalViewer.timeFormat12Hour;
        }

        ProgrammeFormat programmeFormat =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FORMAT, timeFormat, true );

        programmeFormat.setOnScreen( false );

        StringBuffer buff = new StringBuffer(  );

        if( programme != null )
        {

            try
            {
                buff.append( 
                    programmeFormat.formatForProgrammeDetailsJPanel( 
                        programme ) );

                /*TemplateParser parser =
                    new TemplateParser(
                        "freeguide/plugins/ui/horizontal/manylabels/templates/TemplateProgrammeInfo.html" );
                StringWriter out = new StringWriter(  );
                parser.process(
                    new HandlerProgrammeInfo( programme, timeFormat ), out );
                buff = out.getBuffer(  );*/
            }
            catch( Exception ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.SEVERE,
                    "Error construct programme info HTML for screen", ex );
            }
        }
        else
        {
            ProgrammeFormat.appendStyleSheet( buff );

            buff.append( "<p>" );

            buff.append( 
                getLocalizer(  ).getLocalizedMessage( "no_programme_selected" ) );

            buff.append( "</p></body></html>" );
        }

        panel.getDetailsPanel(  ).setText( buff.toString(  ) );
        panel.getDetailsPanel(  ).setCaretPosition( 0 );
    }
}

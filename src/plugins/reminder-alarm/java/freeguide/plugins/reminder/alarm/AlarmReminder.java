package freeguide.plugins.reminder.alarm;

import freeguide.gui.dialogs.FavouritesController;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.lib.fgspecific.selection.Favourite;
import freeguide.lib.fgspecific.selection.ManualSelection;

import freeguide.lib.general.Utils;

import freeguide.plugins.BaseModuleReminder;
import freeguide.plugins.IModuleConfigurationUI;
import freeguide.plugins.IModuleReminder;
import freeguide.plugins.IModuleStorage;

import freeguide.plugins.reminder.alarm.AlarmUIController;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import java.util.logging.Level;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

/**
 * Alarm reminder module. It works like previous reminder and just display
 * prompt.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class AlarmReminder extends BaseModuleReminder
    implements IModuleReminder
{

    private final static Shape heartShape;

    static
    {

        GeneralPath path = new GeneralPath(  );

        path.moveTo( 300, 200 );

        path.curveTo( 100, 0, 0, 400, 300, 580 );

        path.moveTo( 300, 580 );

        path.curveTo( 600, 400, 500, 0, 300, 200 );

        heartShape = path;

    }

    /** Config object. */
    final public ConfigAlarm config = new ConfigAlarm(  );
    TVProgramme scheduledProgramme;
    JDialog scheduledDialog;

    /**
     * DOCUMENT_ME!
     *
     * @param prefs DOCUMENT_ME!
     */
    public void setConfigStorage( Preferences prefs )
    {
        super.setConfigStorage( prefs );
        loadObjectFromPreferences( config );
    }

    /**
     * DOCUMENT_ME!
     */
    public void saveConfig(  )
    {
        saveObjectToPreferences( config );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToMenu( JMenu menu )
    {
        super.addItemsToMenu( menu );

        JMenuItem it = menu.getItem( menu.getItemCount(  ) - 1 );
        it.setMnemonic( KeyEvent.VK_F );
        it.setAccelerator( 
            KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK ) );
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

        return new AlarmUIController( this, parentDialog );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToPopupMenu( 
        final TVProgramme programme, final JPopupMenu menu )
    {

        final JMenuItem sel = new JMenuItem(  );
        ManualSelection ms = getManualSelection( programme );

        if( ( ms == null ) || ( ms.selected == false ) )
        {
            sel.setText( i18n.getLocalizedMessage( "popup.selection.add" ) );
            sel.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        setProgrammeSelection( programme, true );
                        Application.getInstance(  ).redrawPersonalizedGuide(  );
                    }
                } );
        }
        else
        {
            sel.setText( i18n.getLocalizedMessage( "popup.selection.del" ) );
            sel.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        setProgrammeSelection( programme, false );
                        Application.getInstance(  ).redrawPersonalizedGuide(  );
                    }
                } );
        }

        menu.add( sel );

        final JMenuItem fav = new JMenuItem(  );

        if( getFavourite( programme ) == null )
        {
            fav.setText( i18n.getLocalizedMessage( "popup.favourite.add" ) );
            fav.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {

                        Favourite f = new Favourite(  );

                        f.setTitleString( programme.getTitle(  ) );

                        f.setName( programme.getTitle(  ) );
                        addFavourite( f );
                    }
                } );
        }
        else
        {
            fav.setText( i18n.getLocalizedMessage( "popup.favourite.del" ) );
            fav.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {

                        Favourite fav = getFavourite( programme );

                        if( fav != null )
                        {

                            Object[] messageArguments =
                            { programme.getTitle(  ) };

                            int r =
                                JOptionPane.showConfirmDialog( 
                                    null, //controller.getPanel(  ),
                                    i18n.getLocalizedMessage( 
                                        "popup.favourite.del.prompt",
                                        messageArguments ),
                                    i18n.getLocalizedMessage( 
                                        "popup.favourite.del.title" ),
                                    JOptionPane.YES_NO_OPTION );

                            if( r == 0 )
                            {
                                removeFavourite( fav );
                            }
                        }
                    }
                } );
        }

        menu.add( fav );
    }

    protected void onMenuItem(  )
    {

        FavouritesController favController =
            new FavouritesController( 
                Application.getInstance(  ).getApplicationFrame(  ),
                config.favouritesList,
                Application.getInstance(  ).getDataStorage(  ).getInfo(  ).allChannels );

        Utils.centreDialog( 
            Application.getInstance(  ).getApplicationFrame(  ),
            favController.getListDialog(  ) );
        favController.getListDialog(  ).setVisible( true );

        if( favController.isChanged(  ) )
        {
            config.favouritesList = favController.getFavourites(  );

            saveConfig(  );

            Application.getInstance(  ).redraw(  );

            reschedule(  );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prog DOCUMENT_ME!
     * @param label DOCUMENT_ME!
     * @param graphics DOCUMENT_ME!
     */
    public void onPaintProgrammeLabel( 
        TVProgramme prog, JLabel label, Graphics2D graphics )
    {

        if( getFavourite( prog ) == null )
        {

            return;
        }

        AffineTransform originalTransform = graphics.getTransform(  );

        graphics.setColor( Color.RED );

        // switch on anti-aliasing
        graphics.setRenderingHint( 
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        // Scale and position appropriately--taking into account the borders
        Rectangle bounds = heartShape.getBounds(  );

        double scale = 0.45 * ( label.getHeight(  ) / bounds.getHeight(  ) );

        double right =
            label.getWidth(  ) - 2 - ( scale * bounds.getWidth(  ) );

        graphics.translate( right, 2 );

        graphics.scale( scale, scale );

        graphics.fill( heartShape );

        graphics.setTransform( originalTransform );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     * @param label DOCUMENT_ME!
     */
    public void onPaintProgrammeLabel( TVProgramme programme, JLabel label )
    {

        ManualSelection ms = getManualSelection( programme );

        if( ( ms != null ) && ( ms.selected == true ) )
        {
            label.setBorder( 
                BorderFactory.createCompoundBorder( 
                    BorderFactory.createLineBorder( Color.BLACK ),
                    BorderFactory.createLineBorder( config.colorTicked, 2 ) ) );
            label.setBackground( config.colorTicked );
        }
    }

    protected long getNextTime(  )
    {

        if( config.reminderOn )
        {

            try
            {
                scheduledProgramme =
                    Application.getInstance(  ).getDataStorage(  )
                               .findEarliest( 
                        System.currentTimeMillis(  ) + config.reminderWarning,
                        new IModuleStorage.EarliestCheckAllow(  )
                        {
                            public boolean isAllow( TVProgramme programme )
                            {

                                return isSelected( programme );

                            }
                        } );

                if( scheduledProgramme != null )
                {

                    return scheduledProgramme.getStart(  );
                }
                else
                {

                    return Long.MAX_VALUE;
                }
            }

            catch( Exception ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING, "Error find next programme", ex );
            }
        }

        return Long.MAX_VALUE;
    }

    protected void onTime(  )
    {

        String message =
            i18n.getLocalizedMessage( 
                "alarm.text", new Object[] { scheduledProgramme.getTitle(  ) } );

        JOptionPane optionPane =
            new JOptionPane( message, JOptionPane.INFORMATION_MESSAGE );

        scheduledDialog =
            optionPane.createDialog( null, i18n.getString( "alarm.title" ) );

        scheduledDialog.setModal( false );

        scheduledDialog.setVisible( true );
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class ConfigAlarm extends Config
    {

        /** Is reminder on. */
        public boolean reminderOn = true;

        /** Time in milliseconds. */
        public long reminderGiveUp = 600000L;

        /** Time in milliseconds. */
        public long reminderWarning = 300000L;

        /** Default colour of a ticked programme */
        public Color colorTicked = new Color( 204, 255, 204 );
    }
}

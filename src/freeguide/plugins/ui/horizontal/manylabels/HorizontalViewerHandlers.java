package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;

import freeguide.common.plugins.IModuleReminder;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HorizontalViewerHandlers
{

    protected final HorizontalViewer controller;

    /** The action listener for when the item changes in the date combo */
    public ItemListener comboDateItemListener =
        ( new ItemListener(  )
        {
            public void itemStateChanged( ItemEvent evt )
            {

                // Do nothing if this isn't an item selection
                if( evt.getStateChange(  ) == ItemEvent.SELECTED )
                {

                    long oldDate = controller.theDate;

                    // Set theDate to the date chosen
                    controller.theDate =
                        controller.dateExistList[controller.panel.getComboDate(  )
                                                                 .getSelectedIndex(  )];

                    if( oldDate != controller.theDate )
                    {
                        controller.loadData(  );

                        controller.redraw(  );
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
                        controller.panel.getComboChannelsSet(  )
                                        .getSelectedIndex(  );

                    if( selectedInd == 0 )
                    { // all channels set
                        controller.config.currentChannelSetName = null;

                        controller.loadData(  );

                        controller.redraw(  );

                    }

                    else if( 
                        selectedInd == ( controller.panel.getComboChannelsSet(  )
                                                             .getItemCount(  )
                            - 1 ) )
                    { // edit set
                        Application.getInstance(  ).doEditChannelsSets(  );

                    }

                    else
                    { // select set
                        controller.config.currentChannelSetName =
                            (String)controller.panel.getComboChannelsSet(  )
                                                    .getSelectedItem(  );

                        controller.loadData(  );

                        controller.redraw(  );

                    }
                }
            }
        } );

    /** Listened for handle mouse events from channel's label. */
    public MouseListener labelChannelMouseListener =
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

            protected void maybeShowPopup( MouseEvent evt )
            {

                if( evt.isPopupTrigger(  ) )
                {

                    JLabelChannel labelChannel =
                        (JLabelChannel)evt.getComponent(  );
                    new MenuChannel( labelChannel, controller ).show( 
                        evt.getComponent(  ), evt.getX(  ), evt.getY(  ) );
                }
            }
        };

    /** Listened for handle mouse events from programmes's label. */
    public MouseListener labelProgrammeMouseListener =
        new MouseListener(  )
        {
            public void mouseClicked( java.awt.event.MouseEvent evt )
            {
                evt.getComponent(  ).requestFocusInWindow(  );

                if( evt.getClickCount(  ) == 2 )
                {

                    JLabelProgramme label =
                        (JLabelProgramme)evt.getComponent(  );
                    label.getActionMap(  ).get( "select" ).actionPerformed( 
                        new ActionEvent( label, 0, "select" ) );
                }
            }

            public void mousePressed( java.awt.event.MouseEvent evt )
            {
                evt.getComponent(  ).requestFocus(  );
                maybeShowPopup( evt );

            }

            public void mouseReleased( java.awt.event.MouseEvent evt )
            {
                maybeShowPopup( evt );
            }

            public void mouseEntered( java.awt.event.MouseEvent evt )
            {
            }

            public void mouseExited( java.awt.event.MouseEvent evt )
            {
            }

            private void maybeShowPopup( java.awt.event.MouseEvent evt )
            {

                if( evt.isPopupTrigger(  ) )
                {

                    JLabelProgramme labelProgramme =
                        (JLabelProgramme)evt.getComponent(  );
                    new MenuProgramme( 
                        controller, labelProgramme.getProgramme(  ) ).show( 
                        evt.getComponent(  ), evt.getX(  ), evt.getY(  ) );
                }
            }
        };

    /** Listened for handle focus events from programmes's label. */
    public FocusListener labelProgrammeFocusListener =
        new FocusListener(  )
        {
            public void focusGained( FocusEvent e )
            {

                final JLabelProgramme labelProgramme =
                    (JLabelProgramme)e.getComponent(  );
                controller.currentProgrammeLabel = labelProgramme;
                controller.updateProgrammeInfo( 
                    labelProgramme.getProgramme(  ) );
                labelProgramme.setupColors(  );
            }

            public void focusLost( FocusEvent e )
            {

                final JLabelProgramme labelProgramme =
                    (JLabelProgramme)e.getComponent(  );
                labelProgramme.setupColors(  );
            }
        };

    /** Map for programme labels. */
    public ActionMap labelProgrammeActionMap = new LabelProgrammeActionMap(  );

    /** Map for programme labels. */
    public InputMap labelProgrammeInputMap = new LabelProgrammeInputMap(  );

    /**
     * Creates a new HorizontalViewerHandlers object.
     *
     * @param controller DOCUMENT ME!
     */
    public HorizontalViewerHandlers( final HorizontalViewer controller )
    {
        this.controller = controller;
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class LabelProgrammeActionMap extends ActionMap
    {

        /**
         * Creates a new LabelProgrammeActionMap object.
         */
        public LabelProgrammeActionMap(  )
        {
            put( 
                "up",
                new AbstractAction(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        getPanel( e ).focusMoveUp( getLabel( e ) );
                    }
                    ;
                } );
            put( 
                "down",
                new AbstractAction(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        getPanel( e ).focusMoveDown( getLabel( e ) );
                    }
                    ;
                } );
            put( 
                "left",
                new AbstractAction(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        getPanel( e ).focusMoveLeft( getLabel( e ) );
                    }
                    ;
                } );
            put( 
                "right",
                new AbstractAction(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        getPanel( e ).focusMoveRight( getLabel( e ) );
                    }
                    ;
                } );
            put( 
                "select",
                new AbstractAction(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {

                        JLabelProgramme label =
                            (JLabelProgramme)e.getSource(  );

                        IModuleReminder[] reminders =
                            Application.getInstance(  ).getReminders(  );

                        if( reminders.length < 1 )
                        {

                            return;
                        }

                        final IModuleReminder reminder = reminders[0];

                        final boolean isSelected =
                            reminder.isSelected( label.getProgramme(  ) );
                        reminder.setProgrammeSelection( 
                            label.getProgramme(  ), !isSelected );
                        label.controller.redrawCurrentProgramme(  );
                    }
                    ;
                } );
            put( 
                "favourite",
                new AbstractAction(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {

                        JLabelProgramme label =
                            (JLabelProgramme)e.getSource(  );
                        final TVProgramme programme = label.getProgramme(  );
                        IModuleReminder[] reminders =
                            Application.getInstance(  ).getReminders(  );

                        if( reminders.length < 1 )
                        {

                            return;
                        }

                        final IModuleReminder reminder = reminders[0];
                        Favourite fav = reminder.getFavourite( programme );

                        if( fav != null )
                        {
                            reminder.removeFavourite( fav );

                            JLabelProgramme labelNew =
                                ( (ViewerFrame)label.controller.getPanel(  ) ).getProgrammesPanel(  )
                                  .getLabelForProgramme( programme );

                            if( labelNew != null )
                            {
                                labelNew.requestFocus(  );
                            }
                        }
                        else
                        {
                            fav = new Favourite(  );
                            fav.setTitleString( 
                                label.getProgramme(  ).getTitle(  ) );
                            fav.setName( label.getProgramme(  ).getTitle(  ) );
                            reminder.addFavourite( fav );
                            label.controller.redraw(  );

                            JLabelProgramme labelNew =
                                ( (ViewerFrame)label.controller.getPanel(  ) ).getProgrammesPanel(  )
                                  .getLabelForProgramme( programme );

                            if( labelNew != null )
                            {
                                labelNew.requestFocus(  );
                            }
                        }
                    }
                } );
        }

        protected JPanelProgramme getPanel( ActionEvent e )
        {

            return (JPanelProgramme)getLabel( e ).getParent(  );
        }

        protected JLabelProgramme getLabel( ActionEvent e )
        {

            return (JLabelProgramme)e.getSource(  );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class LabelProgrammeInputMap extends InputMap
    {

        /**
         * Creates a new LabelProgrammeInputMap object.
         */
        public LabelProgrammeInputMap(  )
        {
            put( KeyStroke.getKeyStroke( KeyEvent.VK_UP, 0 ), "up" );
            put( KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, 0 ), "down" );
            put( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ), "left" );
            put( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ), "right" );

            put( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0 ), "select" );
            put( KeyStroke.getKeyStroke( KeyEvent.VK_F, 0 ), "favourite" );
        }
    }
}

package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.lib.fgspecific.Application;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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

                    //toggleSelection(  );
                }
            }

            public void mousePressed( java.awt.event.MouseEvent evt )
            {
                evt.getComponent(  ).requestFocusInWindow(  );
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

                JLabelProgramme labelProgramme =
                    (JLabelProgramme)e.getComponent(  );
                System.out.println( 
                    "focus " + labelProgramme.getProgramme(  ).getTitle(  ) );
                controller.updateProgrammeInfo( 
                    labelProgramme.getProgramme(  ) );
            }

            public void focusLost( FocusEvent e )
            {
            }
        };

    /**
     * Creates a new HorizontalViewerHandlers object.
     *
     * @param controller DOCUMENT ME!
     */
    public HorizontalViewerHandlers( final HorizontalViewer controller )
    {
        this.controller = controller;
    }
}

package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.data.TVChannel;

import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class JPanelChannel extends JPanel
{

    protected final HorizontalViewer controller;
    protected JLabelChannel[] displayedChannels;
    protected int maxChannelWidth;

    /**
     * Creates a new JPanelChannel object.
     *
     * @param controller DOCUMENT ME!
     */
    public JPanelChannel( final HorizontalViewer controller )
    {
        super( null );
        this.controller = controller;
    }

    /**
     * Prepare channels for display
     *
     * @param channels channels list
     */
    public void setChanels( final TVChannel[] channels )
    {
        removeAll(  );

        maxChannelWidth = 0;
        displayedChannels = new JLabelChannel[channels.length];

        for( int i = 0; i < channels.length; i++ )
        {

            JLabelChannel ctxt =
                new JLabelChannel( channels[i], controller, getFont(  ) );
            maxChannelWidth =
                Math.max( ctxt.getRequiredWidth(  ), maxChannelWidth );
            add( ctxt );
            displayedChannels[i] = ctxt;
        }

        maxChannelWidth += 5;

        for( int i = 0; i < displayedChannels.length; i++ )
        {
            setupBounds( displayedChannels[i], maxChannelWidth, i );
        }
    }

    /**
     * Get maximum channels labels width
     *
     * @return maximum channels labels width
     */
    public int getMaxChannelWidth(  )
    {

        return maxChannelWidth;
    }

    /**
     * Set size and position for channel label.
     *
     * @param ctxt DOCUMENT ME!
     * @param maxWidth width
     * @param row label row
     */
    protected void setupBounds( 
        final JLabelChannel ctxt, int maxWidth, int row )
    {

        int x = 0;
        int y =
            ( ( controller.config.sizeHalfVerGap * 2 )
            + ( row * controller.config.sizeChannelHeight ) ) - 1;
        int width = maxWidth;
        int height =
            controller.config.sizeChannelHeight
            - ( controller.config.sizeHalfVerGap * 4 );
        ctxt.setBounds( x, y, width, height );
    }

    /**
     * Get scrolling value for display channel.
     *
     * @param channelID channel ID
     *
     * @return scrolling value
     */
    public int getScrollValue( final String channelID )
    {

        for( int i = 0; i < displayedChannels.length; i++ )
        {

            if( 
                displayedChannels[i].getChannel(  ).getID(  ).equals( 
                        channelID ) )
            {

                return ( i - 2 ) * controller.config.sizeChannelHeight;
            }
        }

        return 0;
    }
}

package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.TVChannelIconHelper;
import freeguide.common.lib.fgspecific.data.TVChannel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * Label for display channel info.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class JLabelChannel extends JLabel
{
    protected static Border DEFAULT_BORDER =
        BorderFactory.createLineBorder( Color.BLACK );

    /** Channel for current label. */
    protected final TVChannel channel;

    /** Parent controller. */
    final protected HorizontalViewer controller;

/**
     * Creates a new JLabelChannel object.
     *
     * @param channel DOCUMENT ME!
     * @param main DOCUMENT ME!
     * @param font DOCUMENT ME!
     */
    public JLabelChannel( 
        final TVChannel channel, final HorizontalViewer main, final Font font )
    {
        super( channel.getDisplayName(  ) );
        this.channel = channel;
        this.controller = main;

        setBackground( main.config.colorChannel );
        setFont( font );
        setBorder( DEFAULT_BORDER );
        setHorizontalAlignment( JLabel.LEFT );
        setupIcon(  );
        setOpaque( true );

        addMouseListener( main.handlers.labelChannelMouseListener );
    }

    /**
     * Get channel for label.
     *
     * @return channel
     */
    public TVChannel getChannel(  )
    {
        return channel;
    }

    /**
     * Get width for label.
     *
     * @return width
     */
    public int getRequiredWidth(  )
    {
        FontMetrics myFM = this.getFontMetrics( getFont(  ) );
        int ans = myFM.stringWidth( getText(  ) );
        Icon ic = super.getIcon(  );

        if( ic != null )
        {
            ans += ic.getIconWidth(  );
        }

        Insets ins = getInsets(  );
        ans += ( ins.left + ins.right );

        if( ans < 10 )
        {
            ans = 10;
        }

        return ans;
    }

    /**
     * Resize icon to label's size.
     */
    protected void setupIcon(  )
    {
        ImageIcon icon = TVChannelIconHelper.getIcon( channel );

        if( icon == null )
        {
            return;
        }

        int height =
            controller.config.sizeChannelHeight
            - ( controller.config.sizeHalfVerGap * 4 );

        // Force the icon to have proportions 1.36 x 1
        int icon_width = icon.getIconWidth(  );

        int icon_height = icon.getIconHeight(  );

        int new_icon_width = -1;

        int new_icon_height = -1;

        int max_height = height;

        int max_width = (int)( 1.37 * (double)max_height );

        double new_over_old = (double)max_width / (double)icon_width;

        new_icon_width = max_width;

        new_icon_height = (int)( (double)icon_height * new_over_old );

        if( new_icon_height > max_height )
        {
            double new_over_old2 =
                (double)max_height / (double)new_icon_height;

            new_icon_height = max_height;

            new_icon_width = (int)( (double)max_width * new_over_old2 );

        }

        setIcon( 
            new ImageIcon( 
                icon.getImage(  )
                    .getScaledInstance( 
                    new_icon_width, new_icon_height, Image.SCALE_AREA_AVERAGING ) ) );
    }
}

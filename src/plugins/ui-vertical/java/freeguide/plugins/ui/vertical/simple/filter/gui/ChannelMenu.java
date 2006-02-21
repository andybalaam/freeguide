package freeguide.plugins.ui.vertical.simple.filter.gui;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannelsSet;

import freeguide.plugins.ui.vertical.simple.VerticalViewer;
import freeguide.plugins.ui.vertical.simple.filter.ChannelFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

/**
 * A menu where the use can choose the channels to be displayed
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class ChannelMenu extends JPopupMenu implements ActionListener
{

    ChannelFilter filter;
    HashMap channelMap;

    /**
     * Creates a new ChannelMenu object.
     *
     * @param filter DOCUMENT ME!
     */
    public ChannelMenu( ChannelFilter filter )
    {
        super(  );
        this.filter = filter;
    }

    //public ChannelMenu(ChannelFilter filter)

    /**
     * Initializes the menu items
     */
    public void init(  )
    {

        //Needed in case this is no the first init
        this.removeAll(  );

        //Add a "no channels" button
        ChannelsetMenuItem nullItem =
            new ChannelsetMenuItem( 
                VerticalViewer.getInstance(  ).getLocalizedMessage( 
                    "channelmenu.all" ), null );
        this.add( nullItem );
        nullItem.addActionListener( this );

        //Add channel sets
        List channelsetList =
            Application.getInstance(  ).getChannelsSetsList(  );

        for( int i = 0; i < channelsetList.size(  ); i++ )
        {

            TVChannelsSet set = (TVChannelsSet)channelsetList.get( i );
            ChannelsetMenuItem item =
                new ChannelsetMenuItem( set.getName(  ), set );
            this.add( item );
            item.addActionListener( this );
        }

        //Separator
        this.addSeparator(  );

        //Add checkbox items for the single channels
        this.channelMap = new HashMap(  );

        for( 
            Iterator it =
                Application.getInstance(  ).getDataStorage(  ).getInfo(  ).channelsList.getChannels(  )
                                                                                       .iterator(  );
                it.hasNext(  ); )
        {

            TVChannelsSet.Channel listCh = (TVChannelsSet.Channel)it.next(  );
            JCheckBoxMenuItem item =
                new ChannelCheckBoxMenuItem( 
                    listCh.getDisplayName(  ), listCh.getChannelID(  ) );
            item.addActionListener( this );
            this.add( item );
            this.channelMap.put( listCh.getChannelID(  ), item );
        }
    }

    //public void init()

    /**
     * DOCUMENT_ME!
     *
     * @param actionEvent DOCUMENT_ME!
     */
    public void actionPerformed( ActionEvent actionEvent )
    {

        Object source = actionEvent.getSource(  );

        if( source.getClass(  ).equals( ChannelCheckBoxMenuItem.class ) )
        {

            //Single channel
            ChannelCheckBoxMenuItem item = (ChannelCheckBoxMenuItem)source;

            if( item.isSelected(  ) )
            {
                this.filter.addChannel( item.channelId );
            }
            else
            {
                this.filter.removeChannel( item.channelId );
            }
        }
        else
        {

            //has to be channel set
            //remove all channel selections
            this.filter.removeAllChannels( false );

            Object[] arItems = this.channelMap.values(  ).toArray(  );

            for( int nA = 0; nA < arItems.length; nA++ )
            {
                ( (ChannelCheckBoxMenuItem)arItems[nA] ).setSelected( false );
            }

            //add the channel selection for the channel set channels
            if( ( (ChannelsetMenuItem)source ).set != null )
            {

                Object[] arChannels =
                    ( (ChannelsetMenuItem)source ).set.getChannels(  ).toArray(  );

                for( int nA = 0; nA < arChannels.length; nA++ )
                {

                    TVChannelsSet.Channel channel =
                        (TVChannelsSet.Channel)arChannels[nA];
                    ChannelCheckBoxMenuItem item =
                        (ChannelCheckBoxMenuItem)this.channelMap.get( 
                            channel.getChannelID(  ) );

                    if( item != null )
                    {
                        item.setSelected( true );
                        this.filter.addChannel( item.channelId, false );
                    }
                }
            }

            this.filter.notifyFilterChange(  );
        }
    }

    //public void actionPerformed(ActionEvent actionEvent)
    class ChannelCheckBoxMenuItem extends JCheckBoxMenuItem
    {

        /** DOCUMENT ME! */
        public String channelId = null;

        /**
         * Creates a new ChannelCheckBoxMenuItem object.
         *
         * @param title DOCUMENT ME!
         * @param channelId DOCUMENT ME!
         */
        public ChannelCheckBoxMenuItem( String title, String channelId )
        {
            super( title );
            this.channelId = channelId;
        }
    }

    //class ChannelCheckBoxMenuItem extends JCheckBoxMenuItem
    class ChannelsetMenuItem extends JMenuItem
    {

        /** DOCUMENT ME! */
        public TVChannelsSet set;

        /**
         * Creates a new ChannelsetMenuItem object.
         *
         * @param title DOCUMENT ME!
         * @param set DOCUMENT ME!
         */
        public ChannelsetMenuItem( String title, TVChannelsSet set )
        {
            super( title );
            this.set = set;
        }
    }

    //class ChannelsetMenuItem extends JMenuItem
}


//public class ChannelMenu extends JPopupMenu implements ActionListener

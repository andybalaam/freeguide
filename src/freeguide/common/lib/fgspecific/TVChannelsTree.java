package freeguide.common.lib.fgspecific;

import freeguide.common.lib.fgspecific.data.TVChannelsSelection;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * Tree for display channels.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class TVChannelsTree extends JTree
{

    protected TVChannelsSelection channels;
    protected final Map nodesByChannelID = new TreeMap(  );

    /**
     * Creates a new TVChannelsTree object.
     */
    public TVChannelsTree(  )
    {
        setShowsRootHandles( true );

        setRootVisible( false );

        setCellRenderer( new Renderer(  ) );

        addMouseListener( new NodeSelectionListener(  ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param channels DOCUMENT_ME!
     */
    public void setData( final TVChannelsSelection channels )
    {
        this.channels = channels;

        nodesByChannelID.clear(  );

        setModel( new DefaultTreeModel( getTreeByChannelsSet(  ) ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public TVChannelsSelection getData(  )
    {

        return channels;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getSelectedChannelIDs(  )
    {

        return (String[])channels.selectedChannelIDs.toArray( 
            new String[channels.selectedChannelIDs.size(  )] );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public DefaultMutableTreeNode getTreeByChannelsSet(  )
    {

        DefaultMutableTreeNode root = new DefaultMutableTreeNode( null );

        Iterator it = channels.allChannels.getChannels(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            TVChannelsSet.Channel ch = (TVChannelsSet.Channel)it.next(  );

            DefaultMutableTreeNode node =
                getNodeByPath( root, ch.getChannelID(  ) );

            node.setUserObject( ch );

        }

        return root;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param root DOCUMENT_ME!
     * @param channelID DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public DefaultMutableTreeNode getNodeByPath( 
        DefaultMutableTreeNode root, String channelID )
    {

        if( channelID == null )
        {

            return root;

        }

        DefaultMutableTreeNode node =
            (DefaultMutableTreeNode)nodesByChannelID.get( channelID );

        if( node != null )
        {

            return node;

        }

        int pos = channelID.lastIndexOf( '/' );

        String parentID = ( pos == -1 ) ? null : channelID.substring( 0, pos );

        DefaultMutableTreeNode parentNode = getNodeByPath( root, parentID );

        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(  );

        nodesByChannelID.put( channelID, newNode );

        parentNode.add( newNode );

        return newNode;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param root DOCUMENT_ME!
     * @param channel DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public DefaultMutableTreeNode getNodeByPathOld( 
        DefaultMutableTreeNode root, TVChannelsSet.Channel channel )
    {

        String[] pathElements = channel.getChannelID(  ).split( "\\/" );

        DefaultMutableTreeNode currentNode = root;

        for( int i = 0; i < pathElements.length; i++ )
        { // find child node for each path element

            DefaultMutableTreeNode nextCurrentNode = null;

            Enumeration childs = currentNode.children(  );

            while( childs.hasMoreElements(  ) )
            { // try to find in existing childs

                DefaultMutableTreeNode child =
                    (DefaultMutableTreeNode)childs.nextElement(  );

                if( pathElements[i].equals( child.getUserObject(  ) ) )
                {
                    nextCurrentNode = child;

                    break;

                }
            }

            if( nextCurrentNode == null )
            { // not created yet
                nextCurrentNode = new DefaultMutableTreeNode( channel );

                currentNode.add( nextCurrentNode );

            }

            currentNode = nextCurrentNode;

        }

        return currentNode;

    }

    protected class Renderer extends JCheckBox implements TreeCellRenderer
    {

        TVChannelsSet.Channel currentChannel;

        /**
         * Creates a new Renderer object.
         */
        public Renderer(  )
        {
            setOpaque( false );

        }

        /**
         * DOCUMENT_ME!
         *
         * @param tree DOCUMENT_ME!
         * @param value DOCUMENT_ME!
         * @param selected DOCUMENT_ME!
         * @param expanded DOCUMENT_ME!
         * @param leaf DOCUMENT_ME!
         * @param row DOCUMENT_ME!
         * @param hasFocus DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public Component getTreeCellRendererComponent( 
            JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus )
        {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

            if( !( node.getUserObject(  ) instanceof TVChannelsSet.Channel ) )
            {
                setText( "not initialized" );

                return this;

            }

            currentChannel = (TVChannelsSet.Channel)node.getUserObject(  );

            setEnabled( tree.isEnabled(  ) );

            if( currentChannel != null )
            {
                setText( currentChannel.getDisplayName(  ) );

                setSelected( 
                    channels.selectedChannelIDs.contains( 
                        currentChannel.getChannelID(  ) ) );

            }

            else
            {
                setText( "empty node" );

                setSelected( false );

                setEnabled( false );

            }

            setFont( tree.getFont(  ) );

            //setFocus(hasFocus);
            return this;

        }
    }

    class NodeSelectionListener extends MouseAdapter
    {

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseClicked( MouseEvent e )
        {

            int x = e.getX(  );

            int y = e.getY(  );

            int row = getRowForLocation( x, y );

            TreePath path = getPathForRow( row );

            if( path != null )
            {

                DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode)path.getLastPathComponent(  );

                TVChannelsSet.Channel channel =
                    (TVChannelsSet.Channel)node.getUserObject(  );

                if( channel != null )
                {

                    final String channelID = channel.getChannelID(  );

                    if( channels.selectedChannelIDs.contains( channelID ) )
                    {
                        channels.selectedChannelIDs.remove( channelID );

                    }

                    else
                    {
                        channels.selectedChannelIDs.add( channelID );

                    }

                    ( (DefaultTreeModel)getModel(  ) ).nodeChanged( node );

                }
            }
        }
    }
}

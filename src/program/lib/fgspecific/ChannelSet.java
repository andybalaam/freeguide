import java.util.*;
/**
 *  Standard implementation of FreeGuideChannelSet. XMLTVLoader is also a
 *  FreeGuideChannelSet
 *
 *@author     andy
 *@created    28 June 2003
 */
public class ChannelSet implements ChannelSetInterface {

    private Vector channelIDs;
    // The IDs of the channels
    private Vector channelNames;
    // The names of the channels
    private String name;


    /**
     *  Constructor for the FreeGuideChannelSetImpl object
     */
    public ChannelSet() {
        channelIDs = new Vector();
        channelNames = new Vector();
        name = "(New Channel Set)";
    }


    /**
     *  Gets the channelSetName attribute of the FreeGuideChannelSetImpl object
     *
     *@return    The channelSetName value
     */
    public String getChannelSetName() {
        return name;
    }


    /**
     *  Sets the channelSetName attribute of the FreeGuideChannelSetImpl object
     *
     *@param  name  The new channelSetName value
     */
    public void setChannelSetName(String name) {
        this.name = name;
    }


    /**
     *  Gets the channelIDs attribute of the FreeGuideChannelSetImpl object
     *
     *@return    The channelIDs value
     */
    public Vector getChannelIDs() {
        return channelIDs;
    }


    /**
     *  Gets the channelNames attribute of the FreeGuideChannelSetImpl object
     *
     *@return    The channelNames value
     */
    public Vector getChannelNames() {
        return channelNames;
    }


    /**
     *  convenience function, since channel set names are not available when
     *  retrieving from the Preferences
     *
     *@param  nameprovider  Description of the Parameter
     */
    public void updateChannelNames(ChannelSetInterface nameprovider) {
		
		//System.out.println( this + " . updateChannelNames" );
		
        if (nameprovider == null) {
            return;
        }
        channelNames = new Vector();
        for (int i = 0; i < channelIDs.size(); i++) {
			
            String id = (String) channelIDs.elementAt(i);
            String name = nameprovider.getChannelName(id);
			
			channelNames.add( i, name );
				
        }

    }


    /**
     *  Gets the channelName attribute of the FreeGuideChannelSetImpl object
     *
     *@param  channelID  Description of the Parameter
     *@return            The channelName value
     */
    public String getChannelName(String channelID) {

        int ch = channelIDs.indexOf(channelID);

        if (ch == -1) {

            addChannelName(channelID, channelID);

            return channelID;
        } else {

            String chName = (String) channelNames.get(ch);

            if (chName == null) {

                return channelID;
            } else {

                return chName;
            }

        }

    }


    /**
     *  Gets the channelName attribute of the FreeGuideChannelSetImpl object
     *
     *@param  i  Description of the Parameter
     *@return    The channelName value
     */
    public String getChannelName(int i) {
		//System.out.println( "getChannelName(" + i + ") = " +  channelNames.get(i));
        return (String) channelNames.get(i);
    }


    /**
     *  Gets the noChannels attribute of the FreeGuideChannelSetImpl object
     *
     *@return    The noChannels value
     */
    public int getNoChannels() {
        return channelIDs.size();
    }


    /**
     *  Gets the channelNo attribute of the FreeGuideChannelSetImpl object
     *
     *@param  channelID  Description of the Parameter
     *@return            The channelNo value
     */
    public int getChannelNo(String channelID) {

        return channelIDs.indexOf(channelID);
    }


    /**
     *  Adds a feature to the ChannelName attribute of the
     *  FreeGuideChannelSetImpl object
     *
     *@param  channelID    The feature to be added to the ChannelName attribute
     *@param  channelName  The feature to be added to the ChannelName attribute
     */
    public void addChannelName(String channelID, String channelName) {

        if (channelIDs.indexOf(channelID) == -1) {
            channelIDs.add(channelID);
            channelNames.add(channelName);
        }
    }


    /**
     *  add a set of channels and channel names from a pipe-separated String. A
     *  channel set provider (if supplied) will be used to look-up channel
     *  names.
     *
     *@param  channels      The feature to be added to the ChannelsFromString
     *      attribute
     *@param  nameprovider  The feature to be added to the ChannelsFromString
     *      attribute
     */
    public void addChannelsFromString(String channels, ChannelSetInterface nameprovider) {
        if (channels == null) {
            return;
        }
        StringTokenizer st = new StringTokenizer(channels, "|");
        while (st.hasMoreTokens()) {
            String id = st.nextToken();
            if (nameprovider != null) {
                this.addChannelName(id, nameprovider.getChannelName(id));
            } else {
                this.addChannelName(id, id);
            }
        }

    }


    /**
     *  Write out a pipe-separated string of channel IDs. This is primarily for
     *  persisting into a preferences string
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        return toString(channelIDs);
    }


    /**
     *  Description of the Method
     *
     *@param  channelids  Description of the Parameter
     *@return             Description of the Return Value
     */
    public static String toString(Vector channelids) {
        StringBuffer sb = new StringBuffer();
        Iterator it = channelids.iterator();
        while (it.hasNext()) {
            sb.append((String) it.next()).append("|");
        }
        return sb.toString();
    }


    /**
     *  Description of the Method
     */
    public void clearChannels() {
        this.channelIDs = new Vector();
        this.channelNames = new Vector();
    }

}

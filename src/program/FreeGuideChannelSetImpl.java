import java.util.*;
/** Standard implementation of FreeGuideChannelSet.
 *  XMLTVLoader is also a FreeGuideChannelSet
 **/
public class FreeGuideChannelSetImpl implements FreeGuideChannelSet
{

	private Vector channelIDs;	// The IDs of the channels
	private Vector channelNames;	// The names of the channels
	private String name;


	public FreeGuideChannelSetImpl()
	{
            channelIDs = new Vector();
            channelNames = new Vector();
            name = "(New Channel Set)";
	}

	public String getChannelSetName() { return name; }
        
	public void setChannelSetName(String name)
	{
	  this.name = name;
	}

        public Vector getChannelIDs()
	{
	  return channelIDs;
	}
        public Vector getChannelNames()
	{
	  return channelNames;
	}
        
        /** convenience function, since channel set names are not
         *available when retreiving from the Preferences
        ***/
        public void updateChannelNames(FreeGuideChannelSet nameprovider)
        {
            if (nameprovider == null) return;
            channelNames = new Vector();
            for (int i = 0; i < channelIDs.size(); i++)
            {
                String id = (String)channelIDs.elementAt(i);
                String name = nameprovider.getChannelName(id);
                channelNames.add(name);
            }
            
        }

        public String getChannelName( String channelID )
	{

		int ch = channelIDs.indexOf( channelID );
		
		if( ch == -1 ) {

			addChannelName( channelID, channelID );
			
			return channelID;
			
		} else {
			
			String chName = (String)channelNames.get( ch );
			
			if( chName == null ) {
			
				return channelID;
			
			} else {
				
				return chName;
				
			}
			
		}
		
	}

        public String getChannelName( int i )
	{
	  return (String)channelNames.get( i );
	}
        
        public int getNoChannels()
	{
	  return channelIDs.size();
	}
    
        public int getChannelNo( String channelID ) {
		
		return channelIDs.indexOf( channelID );
		
	}

	public void addChannelName( String channelID, String channelName ) 
	{
			
            if( channelIDs.indexOf( channelID ) == -1 )
            {
                    channelIDs.add( channelID );
                    channelNames.add( channelName );
            }

        }
        
        /** add a set of channels and channel names from a pipe-separated String.
         *  A channel set provider (if supplied) will be used to look-up channel
         *  names.
         **/
        public void addChannelsFromString(String channels, FreeGuideChannelSet nameprovider)
        {
          if (channels == null) return;
          StringTokenizer st = new StringTokenizer(channels,"|");
          while (st.hasMoreTokens())
          {
              String id = st.nextToken();
              if (nameprovider != null)
              {
                  this.addChannelName(id, nameprovider.getChannelName(id));
              }
              else
                  this.addChannelName(id,id);
          }
            
        }
        
        /** Write out a pipe-separated string of channel IDs.  This is
         *primarily for persisting into a preferences string
         **/
        public String toString()
        {
            return toString(channelIDs);
        }
        
        public static String toString(Vector channelids)
        {
            StringBuffer sb = new StringBuffer();
            Iterator it = channelids.iterator();
            while (it.hasNext())
            {
                sb.append((String)it.next()).append("|");
            }
            return sb.toString(); 
        }
        
        public void clearChannels()
        {
            this.channelIDs = new Vector();
            this.channelNames = new Vector();
        }
	
}

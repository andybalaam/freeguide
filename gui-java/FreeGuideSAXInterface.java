/*
 * FreeGuide
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

/**
 * Provides a simple interface to allow a class to receive SAX info from
 * a FreeGuideSAXHandler.
 *
 * @author  Andy Balaam
 * @version 1
 */
public interface FreeGuideSAXInterface {

	public void startDocument();
	
    public void endDocument();
    
    public void startElement(String name, org.xml.sax.Attributes attrs);
    
    public void endElement(String name);
    
    public void characters(String data);
	
}


/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */

package freeguidetv.gui.viewer;

import freeguidetv.lib.fgspecific.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import javax.swing.event.*;

/**
 * Handles HyperlinkEvents created by the HTML document displayed in the
 * HTML program guide.
 *
 *@author     Mike Dean
 *@created    08 January 2004
 *@version    1
 */
public class HTMLGuideListener implements HyperlinkListener {

/**
 * Format used for dates in the HTML links.
 */
  public static SimpleDateFormat LinkDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

  private ViewerFrame parentViewerFrame;

  public HTMLGuideListener(ViewerFrame parentViewerFrame) {
    this.parentViewerFrame = parentViewerFrame;
  }

  /**
   * Requests the parentViewerFrame scroll the program listing to the time
   * encoded in the link's hypertext reference.  The method is called when
   * a hypertext link is updated ("ACTIVATED", "ENTERED", or "EXITED").  Only
   * events of type "ACTIVATED" are important.
   */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
      GregorianCalendar showTime = new GregorianCalendar();
      showTime.setTime(LinkDateFormat.parse(e.getDescription(),
                                            new ParsePosition(1)));
      parentViewerFrame.scrollTo(showTime);
    } 
  }

/**
 * Utility method to create a unique ASCII-only name (reference) to identify
 * each program in the HTML program guide.
 */
  public static String createLinkReference(Programme programme) {
    String reference = null;
    // According to HTML spec, name must be unique and use only ASCII chars
    StringBuffer ref = new StringBuffer(HTMLGuideListener.LinkDateFormat.format(programme.getStart().getTime()));
    ref.append(programme.getChannelID());
    ref.append(programme.getTitle());
    ref.append(programme.getSubTitle());
    try {
      reference = URLEncoder.encode(ref.toString(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      // Won't happen.  All JVM's must support "UTF-8"
      // (and it's the character set recommended by the W3C).
    }
    return reference;
  }

}

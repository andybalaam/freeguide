package freeguide.plugins.grabber.rbc;

import freeguide.lib.fgspecific.data.TVData;

import freeguide.lib.grabber.HttpBrowser;

import freeguide.plugins.BaseModule;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import java.text.NumberFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabberRbc extends BaseModule implements IModuleGrabber
{

    protected Pattern reChannel =
        Pattern.compile( 
            "^(.+)</b></font><.+>(\\p{L}+),\\s+(\\d{1,2})\\s+(\\p{L}+)\\s+(\\d{4})",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected Pattern reProgram =
        Pattern.compile( 
            "^<b>(\\d{1,2}:\\d{2})</b>\\s*(.+?)<",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected Pattern reAnn =
        Pattern.compile( 
            "my=window.open\\('(http://tv.rbc.ru/.+shtml)','anonse'",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected TimeZone tz = TimeZone.getTimeZone( "Europe/Moscow" );
    protected HashMap anns = null;

    /**
     * DOCUMENT_ME!
     */
    public void start(  )
    {
    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {

        return null;
    }

    /**
     * DOCUMENT_ME!
     */
    public void stopGrabbing(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData grabData( IProgress progress, ILogger logger )
        throws Exception
    {

        /*        NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumIntegerDigits(2);

                Calendar cal;
                boolean res;

                HttpBrowser browser = new HttpBrowser();

                    anns = new HashMap();

                    DataParser parser = new DataParser();

                for (int i = 1; i <= 3; i++) {
                    cal = Calendar.getInstance();
                    while (true) {
                        String url = "http://tv.rbc.ru/?d" + cal.get(Calendar.YEAR) + nf.format(cal.get(Calendar.MONTH) + 1) + nf.format(cal.get(Calendar.DAY_OF_MONTH)) + "g" + i;
                        System.out.print("Loading " + cal.get(Calendar.YEAR) + nf.format(cal.get(Calendar.MONTH) + 1) + nf.format(cal.get(Calendar.DAY_OF_MONTH)) + " " + i + "/3... ");
                        browser.loadURL(url);
                        browser.parse(parser);
                        if (parser.wasEmpty()) {
                            break;
                        }
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
                if (anns != null) {
                    int i = 1;
                    String url,str;
                    Iterator it = anns.keySet().iterator();
                    while (it.hasNext()) {
                        url = (String) it.next();
                        System.out.print("Loading announce " + i + "/" + anns.size() + "... ");
                        browser.loadURL(url);
                            browser.parse(new AnnParser());
                            while ((str = browser.readLine()) != null) {
                                if (str.trim().toLowerCase().equals("<font size=\"-1\">")) {
                                    str = tr.readLine();
                                    if (str != null)
                                        ((ParseProg) anns.get(url)).addAnnouncement(str);
                                    break;
                                }
                            }
                        i++;
                    }
                }
                anns = null;*/
        return null;
    }

    /*    protected boolean parsePage(){
    ParseOneChannel currcp = null;
    ParseProg p = null;
    boolean result = false;
    String str;
    Matcher m;
    long basedate = 0,curtime;
    int b;

    while ((str = tr.readLine()) != null) {
        if ((m = reAnn.matcher(str)).find() && anns != null && p != null) {
            anns.put(m.group(1), p);
        } else if ((b = str.indexOf("</b></font>")) != -1) {//!!! bad regexp
            if ((m = reChannel.matcher(str)).find()) {
                p = null;
                if (!doList) {
                    currcp = new ParseOneChannel(m.group(1));
                    list.findcreate("rbcru").add(currcp);
                    basedate = TimeEngine.parseDate(m.group(3), m.group(4), m.group(5), m.group(2));
                } else
                    System.out.println(m.group(1));
            }
        } else if ((b = str.indexOf("<b>")) != -1) {//!!! bad regexp
            str = str.substring(b);
            if ((m = reProgram.matcher(str)).find() && currcp != null) {
                curtime = TimeEngine.parseTime(m.group(1), tz, basedate) + basedate;
                currcp.add(p = new ParseProg(true, curtime, 0, m.group(2)));
                result = true;
            }
        }
    }

    return result;
    }*/
    public static class DataParser extends DefaultHandler
    {

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean wasEmpty(  )
        {

            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class AnnParser extends DefaultHandler
    {
    }
}

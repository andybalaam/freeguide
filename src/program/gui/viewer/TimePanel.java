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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.beans.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;

/**
 *  A panel that displays a ruler-like time line.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    4
 */
public class TimePanel extends javax.swing.JPanel {

    /**
     *  Constructor for the TimePanel object
     */
    public TimePanel() {

        display = false;

        initComponents();

    }


    /**
     *  This method is called from within the constructor to initialize the
     *  form. WARNING: Do NOT modify this code. The content of this method is
     *  always regenerated by the Form Editor.
     */
    private void initComponents() {
        //GEN-BEGIN:initComponents

        setLayout(new java.awt.BorderLayout());

    }
    //GEN-END:initComponents


    /**
     *  Description of the Method
     *
     *@param  g  Description of the Parameter
     */
    public void paintComponent(Graphics g) {

        //FreeGuide.log.info("Painting Time Panel.");

        if (display) {

            super.paintComponent(g);

            int wid = this.getPreferredSize().width;
            SimpleDateFormat fmt;

            //DMT use preferences for 24 hour or 12 hour display
            if (FreeGuide.prefs.screen.getBoolean("display_24hour_time", true)) {
                fmt = time24format;
            } else {
                fmt = timeformat;
            }

            if (wid > 0) {

                Rectangle drawHere = g.getClipBounds();

                //Rectangle viewable = new Rectangle();
                //computeVisibleRect(viewable);

                multiplier = (double) (endTime.getTimeInMillis() - startTime.getTimeInMillis()) / (double) (wid);

                Calendar tmpTime = GregorianCalendar.getInstance();
                tmpTime.setTimeInMillis(startTime.getTimeInMillis());

                // Forget about seconds and milliseconds
                tmpTime.set(Calendar.SECOND, 0);
                tmpTime.set(Calendar.MILLISECOND, 0);

                // Round to the nearest 5 mins
                tmpTime.set(Calendar.MINUTE, ((int) (tmpTime.get(Calendar.MINUTE) / 5)) * 5);

                // Step through each 5 mins
                while (tmpTime.before(endTime)) {

                    int xPos = (int) ((tmpTime.getTimeInMillis() - startTime.getTimeInMillis()) / multiplier);

                    // If this time is on screen, draw a mark
                    if (xPos + 50 >= drawHere.x && xPos - 50 <= (drawHere.x + drawHere.width)) {

                        // Make a mark
                        if (tmpTime.get(Calendar.MINUTE) == 0) {
                            // Hours

                            g.drawLine(xPos, 0, xPos, 10);
                            g.drawLine(xPos + 1, 0, xPos + 1, 10);
                            g.drawString(fmt.format(tmpTime.getTime()), xPos - 17, 21);

                        } else if (tmpTime.get(Calendar.MINUTE) == 30) {
                            // Half hours

                            g.drawLine(xPos, 0, xPos, 7);
                            g.drawString(fmt.format(tmpTime.getTime()), xPos - 17, 21);

                        } else if ((tmpTime.get(Calendar.MINUTE) % 10) == 0) {
                            // 10 mins

                            g.drawLine(xPos, 0, xPos, 4);

                        } else {

                            g.drawLine(xPos, 0, xPos, 1);

                        }
                    }

                    // Add another 5 mins
                    tmpTime.add(Calendar.MINUTE, 5);

                }
                //while

                // Draw the "now" line
                int xPos = getNowScroll();
				drawNowLine(g, xPos);

            }
            //if

        }
        //if

    }
    //paintComponent


	/** Draws the "now line" on this panel as a black triangle.
     * 
     * @param g The graphics context to draw on.
     * @param xPos The x coordinate
     */
    protected void drawNowLine(final Graphics g, final int xPos) {
        int [] xPoints = { xPos - 5, xPos + 5, xPos };
        int [] yPoints = { 0, 0, 25 };
        int nPoints = 3;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillPolygon(xPoints, yPoints, nPoints);
    }
	
    /**
     *  Gets the nowScroll attribute of the TimePanel object
     *
     *@return    The nowScroll value
     */
    public int getNowScroll() {

        if (display) {

            Calendar nowTime = GregorianCalendar.getInstance();
            return (int) ((nowTime.getTimeInMillis() - startTime.getTimeInMillis()) / multiplier);
        }

        return 0;
    }


    /**
     *  Sets the times attribute of the TimePanel object
     *
     *@param  newStartTime  The new times value
     *@param  newEndTime    The new times value
     */
    public void setTimes(Calendar newStartTime, Calendar newEndTime) {

        startTime = GregorianCalendar.getInstance();
        startTime.setTimeInMillis(newStartTime.getTimeInMillis());

        endTime = GregorianCalendar.getInstance();
        endTime.setTimeInMillis(newEndTime.getTimeInMillis());

        int wid = this.getPreferredSize().width;
        multiplier = (double) (endTime.getTimeInMillis() - startTime.getTimeInMillis()) / (double) (wid);

        display = true;

        repaint();

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    private Calendar startTime;
    // The time on the left hand side of the panel
    private Calendar endTime;
    // The time on the right hand side of the panel
    private double multiplier;
    // The no. millisecs over the no. pixels

    private boolean display = false;
    private final SimpleDateFormat time24format = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat timeformat = new SimpleDateFormat("h:mm aa");

}

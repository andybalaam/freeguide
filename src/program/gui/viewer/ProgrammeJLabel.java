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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JToolTip;
/* Also using (but can't import because of java.util.Timer)
import javax.swing.Timer;
 */
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * A JLabel that displays a TV programme
 *
 *@author     Andy Balaam
 *@created    3 July 2003
 *@version    4
 */
public class ProgrammeJLabel extends javax.swing.JLabel {

	/**
	 * Construct a ProgrammeJLabel make it selected, favourite, etc as
	 * appropriate.
	 *
	 * @param programme  the programme shown in this label
	 * @param timeFormat the format in which to display the time
	 * @param drawTime   Do we draw the prog's start time in the label?
	 * @param halfHorGap Half the gap to be left between programmes
	 * @param widthMultiplier How much top multiply time by to get position
	 * @param channelHeight   How high each channel is
	 * @param font            The font to use
	 * @param viewerFrame     The originating ViewerFrame
	 * @param choices    a vector of the chosen programmes for this day.  NOTE:
	 *                   this vector will be altered - if this programmeJLabel
	 *                   is for a program included in this vector, it will be
	 *                   removed from it.
	 */
	ProgrammeJLabel( Programme programme, SimpleDateFormat timeFormat,
			boolean drawTime, int halfHorGap, double widthMultiplier,
			int halfVerGap, int channelHeight, Font font,
			final ViewerFrame viewerFrame, Vector choices ) {
		
		super();
		
		this.viewerFrame = viewerFrame;
		this.programme = programme;
		
		ToolTipManager tipManager = ToolTipManager.sharedInstance();
		// Register this component for tooltip management.
		// This is normally done when by setting the tooltip text,
		// but we want to "lazily" evaluate tooltip text--we defer
		// creation of the tip text until the tip is actually needed.
		// (see the getToolTipText() method below)
		tipManager.registerComponent(this);
                // Create a timer to scroll the HTML guide when the user
                // hovers over the selected program.
                // Using the same timeout as ToolTips so that if we add an
                // option, one setting will apply to both.
                scrollHTMLTimer = new javax.swing.Timer(
                                                 tipManager.getInitialDelay(),
                                                      new ScrollHTMLAction());
                scrollHTMLTimer.setRepeats(false);
		
		Calendar programmeStart = programme.getStart();
        Calendar programmeEnd = programme.getEnd();

        // Find the channel number
        String channelID = programme.getChannelID();
        int channelNo = viewerFrame.currentChannelSet.getChannelNo( channelID );

		int left = halfHorGap + (int)( (programmeStart.getTimeInMillis() -
            viewerFrame.xmltvLoader.earliest.getTimeInMillis() )
				* widthMultiplier );

        int right = ( (int)( (programmeEnd.getTimeInMillis() -
            viewerFrame.xmltvLoader.earliest.getTimeInMillis() )
				* widthMultiplier) ) - (halfHorGap * 2);
		
		int width = right - left;
		
		int top = halfVerGap + (channelNo * channelHeight);
        int bottom = ((channelNo + 1) * channelHeight) - (halfVerGap * 2);
		
		ProgrammeFormat pf;
        if( drawTime ) {
			pf = new ProgrammeFormat(ProgrammeFormat.TEXT_FORMAT,
						 timeFormat);
		} else {
			pf = new ProgrammeFormat(ProgrammeFormat.TEXT_FORMAT);
        }
		String labelText = pf.shortFormat(programme);

		setFont( font );

		setBorder( nonTickedBorder );
		
        setOpaque(true);

        setBounds(left, top, width, bottom - top);

		findOutSelectedness( choices );
		
        addMouseListener(
            new java.awt.event.MouseListener() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    viewerFrame.programmeJLabelClicked(evt);
                }

                public void mousePressed(java.awt.event.MouseEvent evt) {
                    maybeShowPopup( evt );
                }

                public void mouseReleased(java.awt.event.MouseEvent evt) {
                    maybeShowPopup( evt );
                }

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                  if (scrollHTMLTimer.isRunning()) {
                    scrollHTMLTimer.restart();
                  } else {
                    scrollHTMLTimer.start();
                  }
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                  if (scrollHTMLTimer.isRunning()) {
                    scrollHTMLTimer.stop();
                  }
                }

                private void maybeShowPopup( java.awt.event.MouseEvent evt ) {
					
                    if( evt.isPopupTrigger() ) {
						
                        viewerFrame.rightClickedProg
							= (ProgrammeJLabel)evt.getSource();

							
                        viewerFrame.popMenuProgramme.show(
							evt.getComponent(), evt.getX(), evt.getY() );
                    }
                }
            });
		
		this.setText( labelText );
	}

	/**
	 * Work out whether or not this programme should be selected.
	 *
	 * @param choices a vector of the chosen programmes for this day.  NOTE:
	 *                this vector will be altered - if this programmeJLabel
	 *                is for a program included in this vector, it will be
	 *                removed from it.
	 */
	public void findOutSelectedness( Vector choices ) {
		
		FavouritesList favouritesList = FavouritesList.getInstance();
		isFavourite = favouritesList.isFavourite( programme );
		
		if( choices == null ) {
			// Use the favourites to work out whether we're selected
						
			setSelected( isFavourite );
			
		} else {
            // Normally, we use the choices

			boolean isChoice = false;
			
            for (int i = 0; i < choices.size(); i++) {

                if ( choices.get(i).equals( programme ) ) {
					
                    isChoice = true;
					choices.remove( i );
                    break;
					
                } 
			 
			}
			 setSelected( isChoice, false );
		}
	
	}
	
	protected void paintComponent(Graphics g) {
		
		super.paintComponent(g);
	  
	  	if( isFavourite ) {	  
			drawFavouriteIcon(g);			
		}
		
		URL link = programme.getLink();
		if( link != null ) {
			
			g.setColor( Color.BLUE );
			
			int width = getWidth();
			int height = getHeight();
			
			g.fillRect( width-4, height-4, width-1, height-1 );
			
		}
    }
	
	/** Draws the favourite icon on this panel.
	 * @param g The Graphics context to draw on.
	 */
	protected void drawFavouriteIcon(final Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
	AffineTransform originalTransform = g2.getTransform();

        g2.setColor( heartColour );
        
        // switch on anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
	
	// Scale and position appropriately--taking into account the borders
	Rectangle bounds = heartShape.getBounds();
	double scale = 0.45 * (getHeight()/bounds.getHeight());
	double right = getWidth() - 2 - (scale * bounds.getWidth());
	g2.translate(right, 2);
	g2.scale(scale, scale);
	g2.fill( heartShape );
	g2.setTransform(originalTransform);
    }


    public void setSelected( boolean isSelected ) {
		setSelected( isSelected, true );
	}
		
	public void setSelected( boolean isSelected, boolean updatePrefs ) {
		
		this.isSelected = isSelected;
			
		if( isSelected ) {
			
			if( updatePrefs ) {
				FreeGuide.prefs.addChoice( programme, viewerFrame.theDate );
			}
			
			if( FreeGuide.prefs.misc.getBoolean( "reminders_on", true ) ) {
			
				// Set up a reminder here if it's after now
				Date startTime = programme.getStart().getTime();
				long warningSecs = FreeGuide.prefs.misc.getLong(
					"reminders_warning_secs", 300 );
				long giveUpSecs = FreeGuide.prefs.misc.getLong(
					"reminders_give_up_secs", 600 );
				
				if( startTime.after( new Date() ) ) {
				
					// Find out when we will remind
					Date reminderStartTime = new Date( startTime.getTime()
						- warningSecs*1000 );
					
					Date nowDate = new Date();
					
					// If it's immediately, make it in 10 secs time
					if( reminderStartTime.before( nowDate ) ) {
						
						reminderStartTime.setTime( nowDate.getTime() + 10000 );
						
					}
					
					// Set the ending time to be a certain time after the
					// beginning.
					Date reminderEndTime = new Date( reminderStartTime.getTime()
							+ giveUpSecs*1000 );
				
					if( reminderTimer != null ) {
						reminderTimer.cancel();
					}
					reminderTimer = new MessageDialogTimer();
					reminderTimer.schedule(
						programme.getTitle() + " is starting soon.",
						reminderStartTime, reminderEndTime );
				
				}
				
			}
			
			setBorder ( tickedBorder );
			setBackground( tickedColour );
			
		} else {
			
			if( updatePrefs ) {
				FreeGuide.prefs.removeChoice( programme );
			}
			
			if( reminderTimer != null ) {
				
				reminderTimer.cancel();
				
			}
			
			if( programme.getIsMovie() ) {
				setBorder ( movieBorder );
				setBackground( movieColour );
			} else {
				setBorder ( nonTickedBorder );
				setBackground( nonTickedColour );
			}
		}
		
		repaint();
		
	}
	
	/**
	 * The programme that is displayed in this JLabel
	 */
	public Programme programme;
	
	/**
	 * Is this programme selected (ticked)?
	 */
	public boolean isSelected;
	
	/**
	 * Is this programme a favourite?
	 */
	public boolean isFavourite;
	
	private final static Shape heartShape;

	static {
		GeneralPath path = new GeneralPath();
		path.moveTo(300, 200);
		path.curveTo(100, 0, 0, 400, 300, 580);
		path.moveTo(300, 580);
		path.curveTo(600, 400, 500, 0, 300, 200);
		heartShape = path;
	}

	private ViewerFrame viewerFrame;
	private MessageDialogTimer reminderTimer;

	private static Color nonTickedColour;
	private static Color tickedColour;
	private static Color movieColour;
	private static Color heartColour;
	private static Border nonTickedBorder;
	private static Border tickedBorder;
	private static Border movieBorder;

	public static void setNonTickedColour(Color nonTickedColour) {
		ProgrammeJLabel.nonTickedColour = nonTickedColour;
		nonTickedBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK),
			BorderFactory.createLineBorder(nonTickedColour, 2));
	}

	public static void setTickedColour(Color tickedColour) {
		ProgrammeJLabel.tickedColour = tickedColour;
		tickedBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK),
			BorderFactory.createLineBorder(tickedColour, 2));
	}

	public static void setMovieColour(Color movieColour) {
		ProgrammeJLabel.movieColour = movieColour;
		movieBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.BLACK),
			BorderFactory.createLineBorder(movieColour, 2));
	}
	
	public static void setHeartColour(Color heartColour) {
		ProgrammeJLabel.heartColour = heartColour;
	}

	/**
	 * Timer to determine when to scroll the HTML Guide if the user
         * hovers over a programme.
	 */
        private javax.swing.Timer scrollHTMLTimer;
	
        class ScrollHTMLAction implements ActionListener {
          public void actionPerformed(ActionEvent e) {
            viewerFrame.scrollToReference(
                                  HTMLGuideListener.createLinkReference(
                                             ProgrammeJLabel.this.programme));
          }
        }

	public String getToolTipText() {
		String tooltip = super.getToolTipText();
		if (tooltip == null) {
			boolean drawTime = FreeGuide.prefs.screen.getBoolean(
					"display_programme_time", true);
			boolean draw24time = FreeGuide.prefs.screen.getBoolean(
					"display_24hour_time", true);
			SimpleDateFormat timeFormat = ( draw24time ?
						ViewerFrame.timeFormat24Hour :
						ViewerFrame.timeFormat12Hour );
			ProgrammeFormat pf;
			if( drawTime ) {
				pf = new ProgrammeFormat(
						ProgrammeFormat.HTML_FORMAT,
					 	timeFormat);
			} else {
				pf = new ProgrammeFormat(
						ProgrammeFormat.HTML_FORMAT);
			}
			pf.setWrap(true);
			pf.setOnScreen(false);
			tooltip = pf.longFormat(programme).toString();
			// so we don't have to create it next time
			// we can't call setToolTipText(...) because it calls
			// getToolTipText()
			putClientProperty(TOOL_TIP_TEXT_KEY, tooltip);
		}
		return tooltip;
	}
}


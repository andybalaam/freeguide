/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.text.SimpleDateFormat;

/**
 * A JLabel that displays a TV programme
 *
 *@author     Andy Balaam
 *@created    3 July 2003
 *@version    3
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
		
		Calendar programmeStart = programme.getStart();
        Calendar programmeEnd = programme.getEnd();

        String programmeDescription = programme.getLongDesc();
        String programmeTitle = programme.getTitle();
		String programmeSubTitle = programme.getSubTitle();
		String programmeStarString =  programme.getStarString();

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
		
        StringBuffer labelText = new StringBuffer();
		StringBuffer tooltip = new StringBuffer();
		
        if( drawTime ) {
            labelText
				.append( viewerFrame.timeFormat.format(
					programmeStart.getTime() ) )
				.append( " " );
        }

        labelText.append( programmeTitle );
		
		tooltip.append ( timeFormat.format( programmeStart.getTime() ) )
				.append( " " )
				.append( programmeTitle );
		
		if( programmeSubTitle != null ) {
			
			labelText.append( ": " )
				.append( programmeSubTitle );
			
			tooltip.append( ": " )
				.append( programmeSubTitle );
			
		}
		
		if (programmeDescription != null) {
			
				tooltip.append( " - " )
				.append( programme.getShortDesc() );
		}
		
        if ( programme.getIsMovie() && programmeStarString!=null ) {
			
            labelText.append(" ")
				.append( programmeStarString );
			
			tooltip.append( " " )
				.append( programmeStarString );
			
        }
			
        if ( programme.getPreviouslyShown() ) {
            labelText.append(" (R)" );
			tooltip.append( " (R)" );
        }

		setFont( font );
		
        setBorder( new javax.swing.border.LineBorder(Color.black) );
		
        setOpaque(true);

        setBounds(left, top, width, bottom - top);

		// FIXME - put this somewhere where there only needs to be one of it.
		int noPoints = 8;
			
		int[] x = new int[noPoints];
		int[] y = new int[noPoints];
			
		x[0] = width - 8;		y[0] = 14;
		
		x[1] = width -13;		y[1] =  6;
		x[2] = width -13;		y[2] =  4;
		x[3] = width -10;		y[3] =  2;
			
		x[4] = width - 8;		y[4] =  6;
			
		x[5] = width - 6;		y[5] =  2;
		x[6] = width - 3;		y[6] =  4;
		x[7] = width - 3;		y[7] =  6;
		
		heartShape = new Polygon( x, y, noPoints );
		
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

                public void mouseEntered(java.awt.event.MouseEvent evt) { }
                public void mouseExited(java.awt.event.MouseEvent evt) { }

                private void maybeShowPopup( java.awt.event.MouseEvent evt ) {
					
                    if( evt.isPopupTrigger() ) {
						
                        viewerFrame.rightClickedProg
							= (ProgrammeJLabel)evt.getSource();

							
                        viewerFrame.popMenuProgramme.show(
							evt.getComponent(), evt.getX(), evt.getY() );
                    }
                }
            });
		
		this.setText( labelText.toString() );
		setToolTipText( tooltip.toString() );
		
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
		
		Favourite[] favourites =
            FreeGuide.prefs.getFavourites();
			
        if( favourites != null ) {
    
			for( int i = 0; i < favourites.length; i++ ) {

				if( favourites[i].matches( programme ) ) {

                    isFavourite = true;
                    break;
                }
            }
                    
        }
		
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
        g2.setColor( viewerFrame.heartColour );
        
        // switch on anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillPolygon( heartShape );
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
			
			setBackground( viewerFrame.tickedColour );
			
		} else {
			
			if( updatePrefs ) {
				FreeGuide.prefs.removeChoice( programme );
			}
			
			if( reminderTimer != null ) {
				
				reminderTimer.cancel();
				
			}
			
			if( programme.getIsMovie() ) {
				setBackground( viewerFrame.movieColour );
			} else {
				setBackground( viewerFrame.nonTickedColour );
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
	
	private Polygon heartShape;
	private ViewerFrame viewerFrame;
	private MessageDialogTimer reminderTimer;
	
}


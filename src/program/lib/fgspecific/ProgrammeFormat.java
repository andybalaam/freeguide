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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats Programme information.
 */
public class ProgrammeFormat {

	/**
	 * Plain text format
	 */
	public final static int TEXT_FORMAT = 0;
	/**
	 * HTML format including the "<code>&lt;html&gt;&lt;body&gt; ...
	 * &lt;/body&gt;&lt;/html&gt;</code>" tags
	 */
	public final static int HTML_FORMAT = 1;
	/**
	 * HTML format without the <code>&lt;html&gt;&lt;body&gt; ...
	 * &lt;/body&gt;&lt;/html&gt;</code> tags (for use in building
	 * a page from many fragments)
	 */
	public final static int HTML_FRAGMENT_FORMAT = 2;

	public final static String LINE_FEED =
		                 System.getProperty("line.separator", "\r\n");

	private final static int MARGIN = 78;

	private String newline = LINE_FEED;

	private final Pattern defaultWrapPattern = Pattern.compile("(.{1," +
						MARGIN + "})(?:\\s|$)");

	private int outputFormat = TEXT_FORMAT;
	private boolean printTimeDelta = false;
	private DateFormat dateFormat = null;
	private boolean wrap = false;
	private boolean onScreen = true;

	/**
	 * Creates an object to format Programme information.  If the
	 * <code>dateFormat</code> argument is null, no date information will
	 * be output.
	 *
	 * @param outputFormat desired output format
	 * @param dateFormat DateFormat for formatting start and end times
	 * @param printDelta append the delta from now in the summary
	 */
	public ProgrammeFormat(int outputFormat, DateFormat dateFormat, boolean printDelta) {
		setFormat(outputFormat);
		setDateFormat(dateFormat);
		setPrintTimeDelta(printDelta);
	}

	/**
	 * Creates an object to format Programme information without date
	 * information.
	 *
	 * @param outputFormat desired output format
	 */
	public ProgrammeFormat(int outputFormat) {
		this(outputFormat, null, false);
	}

	public ProgrammeFormat() {
	}

	/**
	 * Appends a formatted "short" representation of the Programme to the
	 * supplied StringBuffer.  The following information is returned as
	 * shown:
	 *
	 * <pre>
	 * startTime title: subtitle (starString) (R)
	 * </pre>
	 *
	 * where starString is only shown if the programme is a movie and
	 * (R) is only displayed if the programme has been previously shown.
	 *
	 * @param programme the programme to format
	 * @param toAppendTo where the new programme text should be appended
	 * @return a formatted representation of the Programme appended to
	 *         the StringBuffer
	 * @exception NullPointerException if the given Programme or
	 *                                 StringBuffer is null
	 */
	public StringBuffer shortFormat(Programme programme,
			                StringBuffer toAppendTo) {
		Calendar programmeStart = programme.getStart();
		String programmeTitle = programme.getTitle();
		String programmeSubTitle = programme.getSubTitle();
		String programmeStarString =  programme.getStarString();
		if( dateFormat != null ) {
	            toAppendTo
				.append( dateFormat.format(
					programmeStart.getTime() ) )
				.append( " " );
		}

		toAppendTo.append( programmeTitle );

		if( programmeSubTitle != null ) {

			toAppendTo.append( ": " )
				.append( programmeSubTitle );

		}

		if ( programme.getIsMovie() && programmeStarString!=null ) {

			toAppendTo.append(" ")
				.append( programmeStarString );

		}

		if ( programme.getPreviouslyShown() ) {
			toAppendTo.append(" (R)" );
		}
        
		
		if (printTimeDelta) {
			toAppendTo.append("(");
			calcTimeDelta(programme.getStart(), toAppendTo);
			toAppendTo.append(")");
		}

		return toAppendTo;
	}

	public String shortFormat(Programme programme) {
		return shortFormat(programme, new StringBuffer(75)).toString();
	}

	/**
	 * Returns a String representation of the Programme.
	 * Appends a formatted "long" representation of the Programme to the
	 * supplied StringBuffer.  The following information is returned as
	 * shown:
	 *
	 * <pre>
	 * startTime - title: subtitle
	 * channelName, ends endTime
	 * longDesc (Repeat) starString
	 * </pre>
	 *
	 * where starString is only shown if the programme is a movie and
	 * (Repeat) is only displayed if the programme has been previously
	 * shown.
	 *
	 * @param programme the programme to format
	 * @param toAppendTo where the new programme text should be appended
	 * @return a formatted representation of the Programme appended to
	 *         the StringBuffer
	 * @exception NullPointerException if the given Programme or
	 *                                 StringBuffer is null
	 */
	public StringBuffer longFormat(Programme programme,
			               StringBuffer toAppendTo) {
		Calendar programmeStart = programme.getStart();
		String programmeDescription = programme.getLongDesc();
		if ((programmeDescription != null) && (wrap)) {
			programmeDescription = wrap(programmeDescription,
					            MARGIN).toString();
		}
		String programmeTitle = programme.getTitle();
		String programmeSubTitle = programme.getSubTitle();
		String programmeStarString =  programme.getStarString();


		if (HTML_FRAGMENT_FORMAT == outputFormat) {
			toAppendTo.append( "<p><b>" );
		} else {
			toAppendTo.append( "<html><body><p><b>" );
		}
		if (dateFormat != null) {
			toAppendTo.append(dateFormat.format(
				programme.getStart().getTime()));
			toAppendTo.append( " - " );
		}
		if (onScreen) {
			String ref = HTMLGuideListener.createLinkReference(programme);
			toAppendTo.append( "<a href=\"#" + ref +
					    "\" name=\"" +ref + "\">" );
		}
		toAppendTo.append( programme.getTitle() );

		if( programmeSubTitle != null ) {

			toAppendTo.append( ": " + programmeSubTitle );

		}
		if (onScreen) {
			toAppendTo.append( "</a>" );
		}

		toAppendTo.append( "</b><br>" )
			.append( programme.getChannelName() );
			
		if (dateFormat != null) {
			toAppendTo.append( ", ends " )
                .append( dateFormat.format(
				    programme.getEnd().getTime() ) );
		}
		
		if (printTimeDelta) {
			toAppendTo.append(" <i>(");
			calcTimeDelta(programme.getStart(), toAppendTo);
			toAppendTo.append(")</i>");
		}

		if ( programmeDescription != null) {

			toAppendTo.append( "<br>" )
				.append( LINE_FEED );
			toAppendTo.append( programmeDescription );

		}

		if (programme.getPreviouslyShown()) {
			toAppendTo.append( " (Repeat)" );
		}
		if ( programme.getIsMovie() && programme.getStarRating() != null ) {
			toAppendTo.append( " Rating: ")
			.append( programme.getStarRating() );
		}
		if (HTML_FRAGMENT_FORMAT == outputFormat) {
			toAppendTo.append( "</p>" ).append( LINE_FEED );
		} else {
			toAppendTo.append( "</p></body></html>" )
				  .append( LINE_FEED );
		}

		return toAppendTo;
	}

	public String longFormat(Programme programme) {
		return longFormat(programme, new StringBuffer(200)).toString();
	}


	public void setFormat(int outputFormat) {
		this.outputFormat = outputFormat;
		if (HTML_FORMAT == outputFormat) {
			newline = "<br>" + LINE_FEED;
		} else if (TEXT_FORMAT == outputFormat) {
			newline = LINE_FEED;
		}
	}

	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public void setOnScreen(boolean onScreen) {
		this.onScreen = onScreen;
	}

	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}

	private StringBuffer wrap(CharSequence input, int preferredMargin) {
		int newlineLength = newline.length();
		Pattern wrapPattern = defaultWrapPattern;
		if (preferredMargin != MARGIN) {
			wrapPattern = Pattern.compile("(.{1," +
						MARGIN + "})(?:\\s|$)");
		}
		Matcher m = wrapPattern.matcher(input);
		StringBuffer value = new StringBuffer(300);
		while (m.find()) {
			m.appendReplacement(value, "$0" + newline );
		}
		// Shouldn't ever happen, but...
		m.appendTail(value);
		// Strip the trailing newline
		int length = value.length();
		int possibleStart = length - newlineLength;
		if (value.lastIndexOf(newline) == possibleStart) {
			value.delete(possibleStart, length);
		}
		return value;
	}
	/**
	 * @param printTimeDelta Sets wether to print the time delta from now.
	 */
	public void setPrintTimeDelta(boolean printTimeDelta) {
		this.printTimeDelta = printTimeDelta;
	}
	
	/**
	 * Function that returns the time difference from now in a format like "2 hours and 1 minute"
	 * @param startTime starting time of the program
	 * @param toAppend StringBuffer the resulting string gets added to
	 */
	private void calcTimeDelta(Calendar startTime, StringBuffer toAppend) {
		// Get the current time and calculates the difference in minutes from the starting time
		// >0 in future
		GregorianCalendar now = new GregorianCalendar();
		long delta = startTime.getTimeInMillis() - now.getTimeInMillis();
		delta /= 60000;

		// If delta = 0 then it starts now and we leave as there's nothing else to do
		if (delta == 0) {
			toAppend.append("starts now");
			return;
		}
		
		// Split delta in meaningful fields
		int days = (int)(delta / (24*60));
		int hours = (int)((delta / 60) % 60);
		int minutes = (int)(delta %60);
		
		if (delta>0)
			toAppend.append("starts in ");
		if (delta<0)
			toAppend.append("started ");
		
		switch (days) {
			case 0: break;
			case 1:
			case -1:
				toAppend.append("1 day");
				break;
			default:
				toAppend.append(Math.abs(days)).append(" days");
		}
		if (days != 0 && hours != 0)
			toAppend.append(" ");
		switch (hours) {
			case 0: break;
			case 1:
			case -1:
				toAppend.append("1 hour");
				break;
			default:
				toAppend.append(Math.abs(hours)).append(" hours");
		}
		if ((days != 0 || hours != 0) && minutes != 0)
			toAppend.append(" ");
		switch (minutes) {
			case 0: break;
			case 1:
			case -1:
				toAppend.append("1 minute");
				break;
			default:
				toAppend.append(Math.abs(minutes)).append(" minutes");
		}
		if (delta<0)
			toAppend.append(" ago");
	}
}

/* Old ToolTip format:
 * StartTime Title: Subtitle - ShortDescription (StarString) (PreviouslyShown)
 */

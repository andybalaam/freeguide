package freeguide.lib.fgspecific;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ILocalizer;
import freeguide.plugins.IModuleReminder;

import java.io.IOException;
import java.io.Writer;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Personalized HTML guide creator.
 *
 * @author Andy Balaam
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PersonalizedHTMLGuide
{

    /**
     * Makes a TV Guide in HTML format and write to Writer.
     *
     * @param out
     * @param localizer
     * @param theDate
     * @param data
     * @param dateFormat
     * @param timeFormat
     * @param onScreen
     *
     * @throws IOException
     */
    public void createHTML( 
        final Writer out, final ILocalizer localizer, final Date theDate,
        final TVData data, final SimpleDateFormat dateFormat,
        final SimpleDateFormat timeFormat, final boolean onScreen )
        throws IOException
    {

        final List tickedProgrammes = new ArrayList(  );
        final IModuleReminder[] reminders =
            Application.getInstance(  ).getReminders(  );

        data.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }

                protected void onProgramme( TVProgramme programme )
                {

                    for( int i = 0; i < reminders.length; i++ )
                    {

                        if( reminders[i].isSelected( programme ) )
                        {
                            tickedProgrammes.add( programme );

                        }
                    }
                }
            } );

        // Set up some constants
        String lineBreak = System.getProperty( "line.separator" );

        out.write( "<html>" );
        out.write( lineBreak );

        out.write( "<head>" );
        out.write( lineBreak );

        if( !onScreen )
        {
            out.write( 
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>" );
            out.write( lineBreak );
        }

        out.write( "  <title>" );

        Object[] messageArguments = { dateFormat.format( theDate ) };

        out.write( 
            localizer.getLocalizedMessage( 
                "tv_guide_for_template", messageArguments ) );

        out.write( "</title>" );
        out.write( lineBreak );

        out.write( "  <style type='text/css'>" );
        out.write( lineBreak );

        out.write( "    h1 {" );
        out.write( lineBreak );

        out.write( "        font-family: helvetica, helv, arial;" );
        out.write( lineBreak );

        out.write( "        font-weight: bold;" );
        out.write( lineBreak );

        out.write( "        font-size: x-large;" );
        out.write( lineBreak );

        out.write( "    }" );
        out.write( lineBreak );

        out.write( "    h2 {" );
        out.write( lineBreak );

        out.write( "        font-family: helvetica, helv, arial;" );
        out.write( lineBreak );

        out.write( "        font-weight: bold;" );
        out.write( lineBreak );

        out.write( "        font-size: large;" );
        out.write( lineBreak );

        out.write( "    }" );
        out.write( lineBreak );

        out.write( "    h3 {" );
        out.write( lineBreak );

        out.write( "        font-family: helvetica, helv, arial;" );
        out.write( lineBreak );

        out.write( "        font-weight: bold;" );
        out.write( lineBreak );

        out.write( "        font-size: medium;" );
        out.write( lineBreak );

        out.write( "    }" );
        out.write( lineBreak );

        out.write( "    h4 {" );
        out.write( lineBreak );

        out.write( "        font-family: helvetica, helv, arial;" );
        out.write( lineBreak );

        out.write( "        font-weight: bold;" );
        out.write( lineBreak );

        out.write( "        font-size: small;" );
        out.write( lineBreak );

        out.write( "    }" );
        out.write( lineBreak );

        out.write( "    body {" );
        out.write( lineBreak );

        out.write( "        font-family: helvetica, helv, arial;" );
        out.write( lineBreak );

        out.write( "        font-size: small;" );
        out.write( lineBreak );

        out.write( "    }" );
        out.write( lineBreak );

        out.write( "    address {" );
        out.write( lineBreak );

        out.write( "        font-family: helvetica, helv, arial;" );
        out.write( lineBreak );

        out.write( "        font-size: xx-small;" );
        out.write( lineBreak );

        out.write( "    }" );
        out.write( lineBreak );

        out.write( "  </style>" );
        out.write( lineBreak );

        out.write( "</head>" );
        out.write( lineBreak );

        out.write( "<body>" );
        out.write( lineBreak );

        out.write( "  <h1>" );

        if( onScreen )
        {
            out.write( 
                "<font face='helvetica, helv, arial, sans serif' size='4'>" );

            Object[] messageArguments2 = { dateFormat.format( theDate ) };

            out.write( 
                localizer.getLocalizedMessage( 
                    "your_personalised_tv_guide_for_template",
                    messageArguments2 ) );

            out.write( "</font>" );

        }

        else
        {

            Object[] messageArguments2 = { dateFormat.format( theDate ) };

            out.write( 
                localizer.getLocalizedMessage( 
                    "tv_guide_for_template", messageArguments2 ) );

        }

        out.write( "</h1>" );
        out.write( lineBreak );

        if( onScreen )
        {
            out.write( 
                "<font face='helvetica, helv, arial, sans serif' size=3>" );

            out.write( "<p>" );

            out.write( 
                localizer.getLocalizedMessage( 
                    "select_programmes_by_clicking_on_them" ) );

            out.write( "</p>" );

            out.write( "</font>" );

        }

        // Sort the programmes
        Collections.sort( tickedProgrammes );

        // Add them to the HTML list
        // ----------------------------
        if( onScreen )
        {
            out.write( 
                "<font face='helvetica, helv, arial, sans serif' size=3>" );

        }

        ProgrammeFormat pf =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FRAGMENT_FORMAT, timeFormat, false );

        pf.setOnScreen( onScreen );

        Iterator i = tickedProgrammes.iterator(  );

        while( i.hasNext(  ) )
        {

            TVProgramme prog = (TVProgramme)( i.next(  ) );

            out.write( pf.formatLong( prog ) );

        }

        if( onScreen )
        {
            out.write( "</font>" );

        }

        if( !onScreen )
        {
            out.write( "<hr />" + lineBreak );

            out.write( "<address>" );

            out.write( "http://freeguide-tv.sourceforge.net" );

            out.write( "</address>" );
            out.write( lineBreak );

        }

        out.write( "</body>" );
        out.write( lineBreak );

        out.write( "</html>" );
        out.write( lineBreak );

        out.flush(  );
    }
}

package freeguide.plugins.grabber.zap2it;

import com.tms.webservices.applications.datatypes.StarRating;
import com.tms.webservices.applications.xtvd.Genre;
import com.tms.webservices.applications.xtvd.Program;
import com.tms.webservices.applications.xtvd.ProgramGenre;
import com.tms.webservices.applications.xtvd.SOAPRequest;
import com.tms.webservices.applications.xtvd.Schedule;
import com.tms.webservices.applications.xtvd.Station;
import com.tms.webservices.applications.xtvd.Xtvd;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.common.plugininterfaces.IStoragePipe;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JDialog;

/**
 * Grabber for load data from zap2it.com. See http://labs.zap2it.com for
 * details.
 *
 * @author Alex Buloichik
 */
public class GrabberZap2It extends BaseModule implements IModuleGrabber
{
    protected Zap2ItConfig config = new Zap2ItConfig(  );

    /**
     * Start grabber.
     */
    public void start(  )
    {
    }

    /**
     * Stop grabber.
     */
    public void stop(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return new Zap2ItUIController( this );
    }

    /**
     * Grab data.
     *
     * @param progress progress indicator
     * @param logger logging
     * @param storage storage for grabbed data
     *
     * @throws Exception exception
     */
    public void grabData( 
        IProgress progress, ILogger logger, IStoragePipe storage )
        throws Exception
    {
        final SOAPRequest soapRequest =
            new SOAPRequest( config.username, config.password );
        Calendar start = Calendar.getInstance(  );
        Calendar end = Calendar.getInstance(  );
        end.add( Calendar.DAY_OF_YEAR, config.days );

        Xtvd xtvd = new Xtvd(  );
        soapRequest.getData( start, end, xtvd );

        for( 
            Iterator it = xtvd.getStations(  ).values(  ).iterator(  );
                it.hasNext(  ); )
        {
            final Station station = (Station)it.next(  );
            storage.addChannel( 
                new TVChannel( 
                    "zap2it/" + station.getCallSign(  ), station.getName(  ) ) );
        }

        for( Iterator it = xtvd.getSchedules(  ).iterator(  ); it.hasNext(  ); )
        {
            final Schedule schedule = (Schedule)it.next(  );
            final Station station =
                (Station)xtvd.getStations(  )
                             .get( new Integer( schedule.getStation(  ) ) );
            final String channelID = "zap2it/" + station.getCallSign(  );
            final Program program =
                (Program)xtvd.getPrograms(  ).get( schedule.getProgram(  ) );
            final TVProgramme prog = new TVProgramme(  );
            prog.setTitle( program.getTitle(  ) );
            prog.setSubTitle( program.getSubtitle(  ) );
            prog.setDescription( program.getDescription(  ) );

            final StarRating starRating = program.getStarRating(  );

            if( starRating != null )
            {
                prog.setStarRating( starRating.toString(  ) );
            }

            final ProgramGenre programGenre =
                (ProgramGenre)xtvd.getGenres(  ).get( program.getId(  ) );

            if( programGenre != null )
            {
                for( Genre g : (Collection<Genre>)programGenre.getGenre(  ) )
                {
                    prog.addCategory( g.getClassValue(  ) );
                }
            }

            final long startTime =
                schedule.getTime(  ).getDate(  ).getTime(  );
            final long length =
                ( Integer.parseInt( schedule.getDuration(  ).getHours(  ) ) * 60L * 60L * 1000L )
                + ( Integer.parseInt( schedule.getDuration(  ).getMinutes(  ) ) * 60L * 1000L );
            prog.setStart( startTime );
            prog.setEnd( startTime + length );
            storage.addProgramme( channelID, prog );
        }

        storage.finishBlock(  );
    }

    /**
     * Get config for store.
     *
     * @return config object
     */
    public Object getConfig(  )
    {
        return config;
    }

    /**
     * Data for zap2it.com.
     */
    public static class Zap2ItConfig
    {
        /** User name. */
        public String username;

        /** Password. */
        public String password;

        /** Days to load. */
        public int days = 7;
    }
}

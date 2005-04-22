package freeguide.plugins.storage.inmem;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IStorage;

import freeguide.plugins.storage.StorageHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Storage for store all data in memory, and in one serialized file.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class StorageAllInMemory extends BaseModule implements IStorage
{

    protected static String ID = "inmem";
    protected TVData data;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return ID;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public synchronized Info getInfo(  )
    {

        try
        {
            checkData(  );

        }

        catch( Exception ex )
        {
        }

        final Info result = new Info(  );
        StorageHelper.performInInfo( result, data );

        return result;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channels DOCUMENT_ME!
     * @param minDate DOCUMENT_ME!
     * @param maxDate DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData get( 
        final TVChannelsSet channels, final long minDate, final long maxDate )
        throws Exception
    {

        final TVData result = new TVData(  );

        checkData(  );

        data.iterate( 
            new TVIteratorProgrammes(  )
            {

                protected TVChannel resChannel;

                protected void onChannel( TVChannel channel )
                {

                    if( channels.contains( channel.getID(  ) ) )
                    {
                        resChannel = result.get( channel.getID(  ) );

                        resChannel.loadHeadersFrom( channel );

                    }

                    else
                    {
                        resChannel = null;

                        stopIterateChanel(  );

                    }
                }

                public void onProgramme( TVProgramme programme )
                {

                    if( 
                        ( programme.getStart(  ) >= minDate )
                            && ( programme.getStart(  ) < maxDate ) )
                    {
                        resChannel.put( programme );

                    }
                }
            } );

        return result;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param minDate DOCUMENT_ME!
     * @param check DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVProgramme findEarliest( 
        long minDate, final EarliestCheckAllow check )
        throws Exception
    {
        checkData(  );

        return StorageHelper.findEarliest( data, minDate, check );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param newData DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void add( final TVData newData ) throws Exception
    {
        checkData(  );

        data.mergeFrom( newData );

        ObjectOutputStream out =
            new ObjectOutputStream( 
                new BufferedOutputStream( 
                    new FileOutputStream( 
                        FreeGuide.config.workingDirectory + "/data.ser" ) ) );

        out.writeObject( data );

        out.flush(  );

        out.close(  );

    }

    protected void checkData(  ) throws Exception
    {

        if( data == null )
        {

            try
            {

                ObjectInputStream in =
                    new ObjectInputStream( 
                        new BufferedInputStream( 
                            new FileInputStream( 
                                FreeGuide.config.workingDirectory
                                + "/data.ser" ) ) );

                data = (TVData)in.readObject(  );

                in.close(  );

            }

            catch( FileNotFoundException ex )
            {
                data = new TVData(  );

            }
        }
    }
}

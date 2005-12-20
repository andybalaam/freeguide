package freeguide.lib.impexp;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.IStoragePipe;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Import data from xmltv file.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class XMLTVImport
{

    protected SAXParserFactory factory;
    protected SAXParser saxParser;

    /**
     * Creates a new XMLTVImport object.
     *
     * @throws ParserConfigurationException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     */
    public XMLTVImport(  ) throws ParserConfigurationException, SAXException
    {
        factory = SAXParserFactory.newInstance(  );

        saxParser = factory.newSAXParser(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param file DOCUMENT_ME!
     * @param storage DOCUMENT_ME!
     * @param filter DOCUMENT_ME!
     * @param channelPrefix DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT_ME!
     * @throws IOException DOCUMENT_ME!
     */
    public void process( 
        File file, final IStoragePipe storage, Filter filter,
        final String channelPrefix ) throws SAXException, IOException
    {

        XMLTVImportHandler handler =
            new XMLTVImportHandler( storage, filter, channelPrefix );
        InputSource ins =
            new InputSource( 
                new BufferedInputStream( new FileInputStream( file ) ) );
        ins.setSystemId( file.toURL(  ).toString(  ) );
        saxParser.parse( ins, handler );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param in DOCUMENT_ME!
     * @param filter DOCUMENT_ME!
     * @param filter DOCUMENT_ME!
     * @param channelPrefix DOCUMENT ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void process( 
        InputStream in, final IStoragePipe storage, Filter filter,
        final String channelPrefix ) throws Exception
    {

        XMLTVImportHandler handler =
            new XMLTVImportHandler( storage, filter, channelPrefix );
        InputSource ins = new InputSource( in );
        ins.setSystemId( "memory://data" );
        saxParser.parse( ins, handler );
        storage.finishBlock(  );
    }

    /**
     * Base filter for load. You can redefine some methods for filter data on
     * load.
     *
     * @author Alex Buloichik (mailto: alex73 at zaval.org)
     */
    public static class Filter
    {

        /**
         * Check chanels on start channel tag.
         *
         * @param channelID Channel ID
         *
         * @return True if allow to load
         */
        public boolean checkChannelStart( final String channelID )
        {

            return true;

        }

        /**
         * Call on end channel tag.
         *
         * @param currentChannel channel data
         */
        public void performChannelEnd( final TVChannel currentChannel )
        {
        }

        /**
         * Check programme on start programme tag. Only start time and length
         * defined.
         *
         * @param programme programme data
         *
         * @return True if allow to load
         */
        public boolean checkProgrammeStart( final TVProgramme programme )
        {

            return true;

        }
    }
}

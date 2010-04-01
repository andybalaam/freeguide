package freeguide.common.lib.importexport;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.BadUTF8FilterInputStream;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IStoragePipe;

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
    protected static final String SYSTEM_ID = "memory://data";
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
     * @param countCallback DOCUMENT ME!
     * @param filter DOCUMENT_ME!
     * @return the number of programmes processed
     *
     * @throws SAXException DOCUMENT_ME!
     * @throws IOException DOCUMENT_ME!
     */
    public int process(
        File file, final IStoragePipe storage,
        final ProgrammesCountCallback countCallback, Filter filter,
        ILogger logger ) throws SAXException, IOException
    {
        XMLTVImportHandler handler =
            new XMLTVImportHandler(
                storage, countCallback, filter );
        InputSource ins =
            new InputSource(
                new BadUTF8FilterInputStream(
                new BufferedInputStream( new FileInputStream( file ) ), logger ) );
        ins.setSystemId( file.toURI(  ).toURL(  ).toString(  ) );
        saxParser.parse( ins, handler );

        return handler.programmesCount;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param in DOCUMENT_ME!
     * @param storage DOCUMENT ME!
     * @param filter DOCUMENT_ME!
     * @param filter DOCUMENT_ME!
     * @throws SAXException DOCUMENT_ME!
     * @throws IOException DOCUMENT_ME!
     * @throws ParserConfigurationException DOCUMENT_ME!
     */
    public int process(
        InputStream in, final IStoragePipe storage,
        final ProgrammesCountCallback countCallback, Filter filter,
        ILogger logger )
        throws SAXException, IOException, ParserConfigurationException
    {
        XMLTVImportHandler handler =
            new XMLTVImportHandler(
                storage, countCallback, filter );
        InputSource ins = new InputSource( new BadUTF8FilterInputStream( in,
            logger ) );
        ins.setSystemId( SYSTEM_ID );
        saxParser.parse( ins, handler );
        storage.finishBlock(  );

        return handler.programmesCount;
    }

    /**
     * Base filter for load. You can redefine some methods for filter
     * data on load.
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
         * Check programme on start programme tag. Only start time
         * and length defined.
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

    /**
     * Callback for calculate parsed programmes count.
     *
     * @author $author$
     * @version $Revision$
     */
    public abstract static class ProgrammesCountCallback
    {
        /**
         * On new programme.
         *
         * @param count programme number
         */
        public abstract void onProgramme( final int count );
    }
}

package freeguide.plugins.program.freeguide.lib.fgspecific;

import freeguide.plugins.program.freeguide.FreeGuide;

import freeguide.common.lib.general.Version;

import freeguide.common.plugininterfaces.IModule;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 * Information about loaded plugin.
 *
 * @author Alex Buloichik (alex73 at zaval.rog)
 */
public class PluginInfo extends DefaultHandler
{

    protected String id;
    protected String className;
    protected Version version;
    protected IModule instance;
    protected Map names = new TreeMap(  );
    protected Map descriptions = new TreeMap(  );
    protected List files = new ArrayList(  );
    protected String currentLocaleName;
    protected StringBuffer currentText = new StringBuffer(  );

    /**
     * Get plugin ID.
     *
     * @return plugin ID
     */
    public String getID(  )
    {

        return id;
    }

    /**
     * Get plugin version.
     *
     * @return version
     */
    public Version getVersion(  )
    {

        return version;
    }

    /**
     * Get plugin files.
     *
     * @return plugin files
     */
    public List getFiles(  )
    {

        return Collections.unmodifiableList( files );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Map getNames(  )
    {

        return Collections.unmodifiableMap( names );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Map getDescriptions(  )
    {

        return Collections.unmodifiableMap( descriptions );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param files DOCUMENT_ME!
     */
    public void setFiles( final List files )
    {
        this.files = files;
    }

    /**
     * Get plugin name.
     *
     * @param loc locale
     *
     * @return plugin name
     */
    public String getName( final Locale loc )
    {

        String result = (String)names.get( loc.toString(  ) );

        if( result == null )
        {
            result = (String)names.get( "en" );
        }

        if( result == null )
        {
            result = "Unknown name of module '" + id + "'";
        }

        return result;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getClassName(  )
    {
        return className;
    }

    /**
     * Get plugin description.
     *
     * @param loc locale
     *
     * @return plugin description
     */
    public String getDescription( final Locale loc )
    {

        String result = (String)descriptions.get( loc.toString(  ) );

        if( result == null )
        {
            result = (String)descriptions.get( "en" );
        }

        return result;
    }

    /**
     * Get plugin instance.
     *
     * @return instance
     */
    public IModule getInstance(  )
    {

        if( instance == null )
        {
            instantiate(  );
        }

        return instance;
    }

    protected synchronized void instantiate(  )
    {
        if( instance == null )
        {
            if( className != null )
            {
                try
                {
                    Class moduleClass =
                        getClass(  ).getClassLoader(  ).loadClass( className );
                    instance = (IModule)moduleClass.newInstance(  );

                }
                catch( Exception ex )
                {
                    FreeGuide.log.log( 
                        Level.SEVERE, "Cannot create instance for "
                        + className, ex );
                }

                FreeGuide.log.finest( "Class '" + className + "' loaded" );
            }
        }
    }

    protected void setClassName( final String className )
    {
        this.className = className;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param uri DOCUMENT_ME!
     * @param localName DOCUMENT_ME!
     * @param qName DOCUMENT_ME!
     * @param attributes DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void startElement( 
        String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {

        if( "plugin".equals( qName ) )
        {
            id = attributes.getValue( "id" );
            version = new Version( attributes.getValue( "version" ) );
            setClassName( attributes.getValue( "class" ) );
        }
        else if( "name".equals( qName ) || "description".equals( qName ) )
        {
            currentLocaleName = attributes.getValue( "lang" );
            currentText.setLength( 0 );
        }
        else if( "file".equals( qName ) )
        {
            files.add( attributes.getValue( "path" ) );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param ch DOCUMENT_ME!
     * @param start DOCUMENT_ME!
     * @param length DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        currentText.append( ch, start, length );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param uri DOCUMENT_ME!
     * @param localName DOCUMENT_ME!
     * @param qName DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {

        if( "name".equals( qName ) )
        {
            names.put( currentLocaleName, currentText.toString(  ) );
            currentLocaleName = null;
        }
        else if( "description".equals( qName ) )
        {
            descriptions.put( currentLocaleName, currentText.toString(  ) );
            currentLocaleName = null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void endDocument(  ) throws SAXException
    {
        currentLocaleName = null;
        currentText = null;
    }
}

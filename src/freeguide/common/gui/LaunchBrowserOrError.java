package freeguide.common.gui;

import java.awt.Component;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.DisplayDocs;
import freeguide.common.lib.general.FileHelper;
import freeguide.common.plugininterfaces.IApplication;

/**
 * A class to launch wrap DisplayDocs and FileHelper's browsing methods and
 * display a friendly error message when they fail.
 */
public class LaunchBrowserOrError
{
    public static void browseLocalFileOrError( final File file )
    {
        // TODO:
        Component parentComponent = null;

        try
        {
            new FileHelper().browseLocalFile( file );
        }
        catch( FileHelper.UnableToFindFileToBrowseException e )
        {
            e.printStackTrace();

            Object[] params = { file.toString() };
            JOptionPane.showMessageDialog( parentComponent, Application
                .getInstance().getLocalizedMessage(
                    "unable_to_find_the_tv_guide_file", params ),
                Application.getInstance().getLocalizedMessage(
                    "unable_to_find_tv_guide" ), JOptionPane.ERROR_MESSAGE );
        }
        catch( Exception e )
        {
            displayBrowserErrorDialog( parentComponent, e );
        }
    }

    public static void displayDocsOrError()
    {
        // TODO:
        Component parentComponent = null;

        try
        {
            DisplayDocs.displayDocs();
        }
        catch( FileHelper.UnableToFindFileToBrowseException e )
        {
            e.printStackTrace();

            Object[] params = { e.filePath };
            JOptionPane
                .showMessageDialog( parentComponent, Application.getInstance()
                    .getLocalizedMessage(
                        "unable_to_find_the_documentation_files", params ),
                    Application.getInstance().getLocalizedMessage(
                        "unable_to_find_documentation" ),
                    JOptionPane.ERROR_MESSAGE );
        }
        catch( Exception e )
        {
            displayBrowserErrorDialog( parentComponent, e );
        }
    }

    /**
     * @param parentComponent
     * @param e
     */
    private static void displayBrowserErrorDialog( Component parentComponent,
        Exception e )
    {
        IApplication app = Application.getInstance();
        Object[] params = { e.toString() };
        displayErrorDialog( parentComponent, e, app.getLocalizedMessage(
            "unable_to_launch_browser_to_display_documentation", params ), app
            .getLocalizedMessage( "unable_to_launch_browser" ) );
    }

    /**
     * @param parentComponent
     * @param e
     */
    private static void displayURLErrorDialog( Component parentComponent,
        Exception e )
    {
        IApplication app = Application.getInstance();
        Object[] params = { e.toString() };
        displayErrorDialog( parentComponent, e, app.getLocalizedMessage(
            "unable_to_launch_url", params ), app
            .getLocalizedMessage( "unable_to_launch_browser" ) );
    }

    /**
     * @param parentComponent
     * @param e
     */
    private static void displayErrorDialog( Component parentComponent,
        Exception e, String msg, String title )
    {
        e.printStackTrace();

        JOptionPane.showMessageDialog( parentComponent, msg, title,
            JOptionPane.ERROR_MESSAGE );
    }

    public static void launchBrowserOrError( URL url )
    {
        // TODO:
        Component parentComponent = null;

        if( url == null )
        {
            displayURLErrorDialog( parentComponent, new Exception(
                "Invalid URL" ) );
            return;
        }

        try
        {
            new FileHelper().browseURL( url );
        }
        catch( Exception e )
        {
            displayBrowserErrorDialog( parentComponent, e );
        }
    }

    public static void launchBrowserOrError( String urlString )
    {
        // TODO:
        Component parentComponent = null;

        try
        {
            launchBrowserOrError( new URL( urlString ) );
        }
        catch( MalformedURLException e )
        {
            displayURLErrorDialog( parentComponent, e );
        }
    }
}

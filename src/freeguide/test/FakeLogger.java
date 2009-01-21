/**
 *
 */
package freeguide.test;

import freeguide.common.plugininterfaces.ILogger;

public class FakeLogger implements ILogger
{

    public void error( String message )
    {
    }

    public void error( String message, Exception ex )
    {
    }

    public void info( String message )
    {
    }

    public void warning( String message )
    {
    }

}

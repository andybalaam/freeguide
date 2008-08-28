package freeguide.common.plugininterfaces;

import java.util.logging.Logger;

public class FGLogger implements ILogger
{
    private Logger impl;

    public FGLogger( Logger impl )
    {
        this.impl = impl;
    }

    public void error( String message )
    {
        impl.warning( message );
    }

    public void error( String message, Exception ex )
    {
        impl.warning( message + ex.getMessage() );
    }

    public void info( String message )
    {
        impl.info(  message );
    }

    public void warning( String message )
    {
        impl.warning(  message );
    }

}

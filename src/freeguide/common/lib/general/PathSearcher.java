package freeguide.common.lib.general;

import java.io.File;

public class PathSearcher implements IPathSearcher
{
    public String[] getPathDirs()
    {
        String pathEnvVar = System.getenv( "PATH" );

        // If there is no "PATH", return the empty array
        if( pathEnvVar == null )
        {
            return new String[0];
        }
        else
        {
            return pathEnvVar.split( System.getProperty( "path.separator" ) );
        }
    }

    public boolean existsAndIsExecutable( final File fullPath )
    {
        return fullPath.canExecute();
    }

    public String findInPath( String[] listOfExes, String fallbackExe )
    {
        String[] pathDirs = getPathDirs();

        for( int di = 0; di < pathDirs.length; di++ )
        {
            String dir = pathDirs[di];
            for( int ei = 0; ei < listOfExes.length; ei++ )
            {
                String exe = listOfExes[ei];
                if( existsAndIsExecutable( new File( dir, exe ) ) )
                {
                    return exe;
                }
            }
        }
        return fallbackExe;
    }
}


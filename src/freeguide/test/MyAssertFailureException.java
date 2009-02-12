/**
 *
 */
package freeguide.test;

public class MyAssertFailureException extends Exception
{
    static final long serialVersionUID = 1;

    public MyAssertFailureException( String message )
    {
        super( message );
    }
}

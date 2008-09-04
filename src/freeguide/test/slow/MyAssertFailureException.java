/**
 *
 */
package freeguide.test.slow;

public class MyAssertFailureException extends Exception
{
    static final long serialVersionUID = 1;

    public MyAssertFailureException( String message )
    {
        super( message );
    }
}

package freeguide.common.lib.general;

import freeguide.common.lib.fgspecific.Application;

import java.io.IOException;
import java.io.Writer;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class for parse templates and produce results.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class TemplateParser
{
    protected static final int NEXT_ITERATOR_START = 1;
    protected static final int NEXT_ITERATOR_END = 2;
    protected static final int NEXT_VALUE = 3;
    protected static final int NEXT_IF_START = 4;
    protected static final int NEXT_IF_END = 5;
    protected static final int NEXT_END = 0;
    protected static final String PREFIX_VALUE = "<VALUE:";
    protected static final String PREFIX_ITERATOR_START = "<ITERATOR:";
    protected static final String PREFIX_ITERATOR_END = "</ITERATOR";
    protected static final String PREFIX_IF_START = "<IF";
    protected static final String PREFIX_IF_END = "</IF";

    /** Template to parse. */
    protected final String template;

    /** Current template position. */
    protected int currentPos;

    /** Root object for call methods which starts from '.'. */
    protected Object rootObject;

    /** Output stream. */
    protected Writer out;

/**
     * Creates a new TemplateParser.
     *
     * @param templateClassPath classpathpath to template
     *
     * @throws IOException
     */
    public TemplateParser( final String templateClassPath )
        throws IOException
    {
        template = LanguageHelper.loadResourceAsString( templateClassPath );
    }

    /**
     * Process template on object and write data to output.
     *
     * @param data objet for processing
     * @param out output stream
     *
     * @throws Exception
     */
    public synchronized void process( final Object data, final Writer out )
        throws Exception
    {
        currentPos = 0;
        rootObject = data;
        this.out = out;
        internalProcess( data );
    }

    /**
     * Internal processing handler. It calls on every part of
     * template.
     *
     * @param currentObject object for processing. Can be null for empty part.
     *
     * @throws Exception
     */
    protected void internalProcess( final Object currentObject )
        throws Exception
    {
        while( true )
        {
            final int posIteratorStart = getNextPos( PREFIX_ITERATOR_START );
            final int posIteratorEnd = getNextPos( PREFIX_ITERATOR_END );
            final int posValue = getNextPos( PREFIX_VALUE );
            final int posIfStart = getNextPos( PREFIX_IF_START );
            final int posIfEnd = getNextPos( PREFIX_IF_END );

            int nextType =
                getNextPosIndex( 
                    new int[]
                    {
                        template.length(  ), posIteratorStart, posIteratorEnd,
                        posValue, posIfStart, posIfEnd
                    } );

            switch( nextType )
            {
            case NEXT_ITERATOR_START:

                if( currentObject != null )
                {
                    out.write( 
                        template, currentPos, posIteratorStart - currentPos );
                }

                currentPos = posIteratorStart;
                currentPos = getNextPos( ">" ) + 1;
                iterate( 
                    template.substring( posIteratorStart, currentPos - 1 ),
                    currentObject );

                break;

            case NEXT_ITERATOR_END:

                if( currentObject != null )
                {
                    out.write( 
                        template, currentPos, posIteratorEnd - currentPos );
                }

                currentPos = posIteratorEnd;
                currentPos = getNextPos( ">" ) + 1;

                return;

            case NEXT_VALUE:

                if( currentObject != null )
                {
                    out.write( template, currentPos, posValue - currentPos );
                }

                currentPos = posValue;
                currentPos = getNextPos( ">" ) + 1;

                if( currentObject != null )
                {
                    out.write( 
                        getStringValue( 
                            template.substring( posValue, currentPos - 1 ),
                            currentObject ) );
                }

                break;

            case NEXT_END:

                if( currentObject != null )
                {
                    out.write( 
                        template, currentPos, template.length(  ) - currentPos );
                }

                currentPos = template.length(  );

                return;

            case NEXT_IF_START:

                if( currentObject != null )
                {
                    out.write( template, currentPos, posIfStart - currentPos );
                }

                currentPos = posIfStart;
                currentPos = getNextPos( ">" ) + 1;

                if( currentObject != null )
                {
                    Object value =
                        calculate( 
                            template.substring( posIfStart, currentPos - 1 ),
                            currentObject );

                    if( ( value == null ) || !( value instanceof Boolean ) )
                    {
                        throw new Exception( 
                            "Invalid type of value: "
                            + template.substring( posValue, currentPos - 1 ) );
                    }

                    if( Boolean.TRUE.equals( value ) )
                    {
                        // write to out
                        internalProcess( currentObject );
                    }
                    else
                    {
                        internalProcess( null );
                    }
                }
                else
                {
                    internalProcess( null );
                }

                break;

            case NEXT_IF_END:

                if( currentObject != null )
                {
                    out.write( template, currentPos, posIfEnd - currentPos );
                }

                currentPos = posIfEnd;
                currentPos = getNextPos( ">" ) + 1;

                return;
            }
        }
    }

    /**
     * Find next substring in template from current position.
     *
     * @param subString substring to find
     *
     * @return
     */
    protected int getNextPos( final String subString )
    {
        final int pos = template.indexOf( subString, currentPos );

        return ( pos < 0 ) ? template.length(  ) : pos;
    }

    /**
     * Find which substring found first.
     *
     * @param pos array of substring positions
     *
     * @return index of minimum position
     */
    protected static int getNextPosIndex( final int[] pos )
    {
        int newPos = pos[0];
        int resultIndex = 0;

        for( int i = 1; i < pos.length; i++ )
        {
            if( pos[i] < newPos )
            {
                resultIndex = i;
                newPos = pos[i];
            }
        }

        return resultIndex;
    }

    /**
     * Get string for  VALUE tag.
     *
     * @param str tag expression
     * @param currentObject current processing object
     *
     * @return calculated value
     *
     * @throws Exception
     */
    protected String getStringValue( 
        final String str, final Object currentObject )
        throws Exception
    {
        Object value = calculate( str, currentObject );

        return ( value != null ) ? value.toString(  ) : "";
    }

    /**
     * Process ITERATOR tag.
     *
     * @param str iterator expression
     * @param currentObject current processing object
     *
     * @throws Exception
     */
    protected void iterate( final String str, final Object currentObject )
        throws Exception
    {
        boolean processed = false;

        if( currentObject != null )
        {
            int pos = currentPos;
            Object value = calculate( str, currentObject );

            if( value instanceof Collection )
            {
                for( 
                    Iterator it = ( (Collection)value ).iterator(  );
                        it.hasNext(  ); )
                {
                    currentPos = pos;
                    internalProcess( it.next(  ) );
                    processed = true;
                }
            }
        }

        if( !processed )
        {
            internalProcess( null );
        }
    }

    /**
     * Calculate any expression.
     *
     * @param expr expression to calculate
     * @param currentObject current processign object
     *
     * @return expression value. Can be null.
     *
     * @throws Exception
     */
    protected Object calculate( String expr, final Object currentObject )
        throws Exception
    {
        int pos = expr.indexOf( ':' );

        if( pos >= 0 )
        {
            expr = expr.substring( pos + 1 );
        }

        if( expr.endsWith( "/" ) )
        {
            expr = expr.substring( 0, expr.length(  ) - 1 );
        }

        expr = expr.trim(  );

        return new Calculator( expr ).calculate( rootObject, currentObject );
    }

    /**
     * Object for calculate one expression.
     */
    protected static class Calculator
    {
        protected static final int EXPR_OPEN = 1;
        protected static final int EXPR_CLOSE = 2;
        protected static final int EXPR_NEXT = 3;
        protected static final int EXPR_END = 0;
        protected final String expr;
        protected int currentPos;

/**
         * Creates a new Calculator object.
         *
         * @param expr expression to calculate
         */
        public Calculator( final String expr )
        {
            this.expr = expr;
            currentPos = 0;
        }

        /**
         * Calculate processor.
         *
         * @param rootObject root object
         * @param currentObject current object
         *
         * @return calculated value
         *
         * @throws Exception
         * @throws IOException DOCUMENT ME!
         */
        public Object calculate( 
            final Object rootObject, final Object currentObject )
            throws IOException
        {
            final int posOpen = getNextPos( '(' );
            final int posClose = getNextPos( ')' );
            final int posNext = getNextPos( ',' );

            int nextType =
                getNextPosIndex( 
                    new int[] { expr.length(  ), posOpen, posClose, posNext } );

            String methodName = null;
            List params = new ArrayList(  );
            Object value;

            switch( nextType )
            {
            case EXPR_OPEN:
                methodName = expr.substring( currentPos, posOpen );
                currentPos = posOpen + 1;

                while( true )
                {
                    Object param = calculate( rootObject, currentObject );

                    if( param != null )
                    {
                        params.add( param );
                    }

                    if( expr.charAt( currentPos - 1 ) == ')' )
                    {
                        break;
                    }
                }

                final Object calledObject;

                if( methodName.charAt( 0 ) == '.' )
                {
                    calledObject = rootObject;
                    methodName = methodName.substring( 1 );
                }
                else
                {
                    calledObject = currentObject;
                }

                Class[] parameterTypes = new Class[params.size(  )];

                for( int i = 0; i < params.size(  ); i++ )
                {
                    parameterTypes[i] = params.get( i ).getClass(  );
                }

                if( calledObject != null )
                {
                    try
                    {
                        final Method method =
                            calledObject.getClass(  )
                                        .getMethod( methodName, parameterTypes );
    
                        // invoke methods for Map.Entry by hand, because it can't be invoked through reflection - cannot access to protected class HashMap$Entry
                        if( 
                            method.getName(  ).equals( "getKey" )
                                && ( method.getParameterTypes(  ).length == 0 )
                                && calledObject instanceof Map.Entry )
                        {
                            final Map.Entry entry = (Map.Entry)calledObject;
                            value = entry.getKey(  );
                        }
                        else if( 
                            method.getName(  ).equals( "getValue" )
                                && ( method.getParameterTypes(  ).length == 0 )
                                && calledObject instanceof Map.Entry )
                        {
                            final Map.Entry entry = (Map.Entry)calledObject;
                            value = entry.getValue(  );
                        }
                        else
                        {
                            try
                            {
                                value = method.invoke( 
                                    calledObject, params.toArray(  ) );
                            }
                            catch( IllegalAccessException e )
                            {
                                e.printStackTrace(  );
                                value = null;
                            }
                            catch( InvocationTargetException e )
                            {
                                Application.getInstance(  )
                                    .getLogger(  ).warning(
                                    "Error running method '" + methodName
                                    + "' on object class '" 
                                    + calledObject.getClass(  ).toString(  )
                                    + "'." );
                                
                                e.printStackTrace(  );
                                value = null;
                            }
                        }
                    }
                    catch( NoSuchMethodException e )
                    {
                        e.printStackTrace(  );
                        value = null;
                    }
                }
                else
                {
                    value = null;
                }

                return value;

            case EXPR_CLOSE:
                value = getField( 
                        expr.substring( currentPos, posClose ), rootObject,
                        currentObject );
                currentPos = posClose + 1;

                return value;

            case EXPR_NEXT:
                value = getField( 
                        expr.substring( currentPos, posNext ), rootObject,
                        currentObject );
                currentPos = posNext + 1;

                return value;
            }

            throw new IOException( "Invalid expression format" );
        }

        protected Object getField( 
            final String name, final Object rootObject,
            final Object currentObject )
        {
            if( "this".equals( name ) )
            {
                return currentObject;
            }
            else
            {
                return null;
            }
        }

        protected int getNextPos( final char subString )
        {
            final int pos = expr.indexOf( subString, currentPos );

            return ( pos < 0 ) ? expr.length(  ) : pos;
        }
    }
}

package freeguide.build.docxslt;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class DocXSLT
{
    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {
        final TransformerFactory factory = TransformerFactory
            .newInstance(  );

        final InputStream xslt =
            new FileInputStream( "doc/docbook-xsl-1.69.1/xhtml/chunk.xsl" );
        final String systemId =
            new File( "doc/docbook-xsl-1.69.1/xhtml/" ).toURI(  ).toURL(  )
                                                       .toExternalForm(  );
        final Transformer trans =
            factory.newTransformer( new StreamSource( xslt, systemId ) );

        trans.transform( 
            new StreamSource( new FileInputStream( "doc/manual.xml" ) ), null );
    }
}

package freeguide.plugins.importexport.palmatv;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import java.awt.Component;

import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.swing.DefaultComboBoxModel;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class PalmUIController implements IModuleConfigurationUI
{
    protected PalmUIPanel panel;
    protected final ExportPalmAtv parent;

/**
     * Creates a new PalmUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public PalmUIController( final ExportPalmAtv parent )
    {
        this.parent = parent;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {
        if( panel == null )
        {
            panel = new PalmUIPanel( parent.getLocalizer(  ) );
            panel.getCbCharset(  )
                 .setModel( new DefaultComboBoxModel( getCharsets(  ) ) );
            panel.getCbCharset(  ).setSelectedItem( parent.config.charset );
        }

        return panel;
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        parent.config.charset = (String)panel.getCbCharset(  ).getSelectedItem(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getCharsets(  )
    {
        final List result = new ArrayList(  );
        SortedMap list = Charset.availableCharsets(  );

        for( Iterator it = list.entrySet(  ).iterator(  ); it.hasNext(  ); )
        {
            Map.Entry entry = (Map.Entry)it.next(  );
            Charset ch = (Charset)entry.getValue(  );
            result.add( entry.getKey(  ) );
            result.addAll( ch.aliases(  ) );
        }

        Collections.sort( result );

        return (String[])result.toArray( new String[result.size(  )] );
    }
}

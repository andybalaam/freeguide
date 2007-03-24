package freeguide.plugins.grabber.hallmark;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTree;
import javax.swing.tree.MutableTreeNode;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HallmarkConfigurationUIController
    implements IModuleConfigurationUI
{
    protected final GrabberHallmark parent;
    protected HallmarkConfigurationUIPanel panel;

/**
     * Creates a new HallmarkConfigurationUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public HallmarkConfigurationUIController( final GrabberHallmark parent )
    {
        this.parent = parent;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param leafName DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param tree DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel( 
        String leafName, MutableTreeNode node, JTree tree )
    {
        if( panel == null )
        {
            panel = new HallmarkConfigurationUIPanel( parent.getLocalizer(  ) );
            panel.getCbCountry(  )
                 .setModel( 
                new DefaultComboBoxModel( HallmarkInfo.getCountriesList(  ) ) );
            panel.getCbCountry(  ).addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        HallmarkInfo.Country country =
                            (HallmarkInfo.Country)panel.getCbCountry(  )
                                                       .getSelectedItem(  );

                        if( country != null )
                        {
                            panel.getCbLanguage(  )
                                 .setModel( 
                                new DefaultComboBoxModel( country.languages ) );
                        }
                    }
                } );

            HallmarkInfo.Country[] countries =
                HallmarkInfo.getCountriesList(  );

            for( int i = 0; i < countries.length; i++ )
            {
                if( countries[i].id.equals( parent.config.countryId ) )
                {
                    panel.getCbCountry(  ).setSelectedItem( countries[i] );

                    for( int j = 0; j < countries[i].languages.length; j++ )
                    {
                        if( 
                            countries[i].languages[j].name.equals( 
                                    parent.config.languageName ) )
                        {
                            panel.getCbLanguage(  )
                                 .setSelectedItem( countries[i].languages[j] );
                        }
                    }
                }
            }

            panel.getTextWeeks(  )
                 .setText( Integer.toString( parent.config.weeksNumber ) );
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
        if( panel == null )
        {
            return;
        }

        HallmarkInfo.Country country =
            (HallmarkInfo.Country)panel.getCbCountry(  ).getSelectedItem(  );

        if( country != null )
        {
            parent.config.countryId = country.id;
        }
        else
        {
            parent.config.countryId = null;
        }

        HallmarkInfo.Language lang =
            (HallmarkInfo.Language)panel.getCbLanguage(  ).getSelectedItem(  );

        if( lang != null )
        {
            parent.config.languageName = lang.name;
        }
        else
        {
            parent.config.languageName = null;
        }

        parent.config.weeksNumber = Integer.parseInt( 
                panel.getTextWeeks(  ).getText(  ) );
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
    public String[] getTreeNodes(  )
    {
        return null;
    }
}

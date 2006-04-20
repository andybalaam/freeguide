package freeguide.plugins.grabber.hallmark;

import freeguide.common.plugins.IModuleConfigurationUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

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
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {

        if( panel == null )
        {
            panel = new HallmarkConfigurationUIPanel(  );
            panel.getCbCountry(  ).setModel( 
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
                            panel.getCbLanguage(  ).setModel( 
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
                            panel.getCbLanguage(  ).setSelectedItem( 
                                countries[i].languages[j] );
                        }
                    }
                }
            }

            panel.getTextWeeks(  ).setText( 
                Integer.toString( parent.config.weeksNumber ) );
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

        parent.config.weeksNumber =
            Integer.parseInt( panel.getTextWeeks(  ).getText(  ) );
    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }
}

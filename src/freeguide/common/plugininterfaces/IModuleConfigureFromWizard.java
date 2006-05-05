package freeguide.common.plugininterfaces;

/**
 * Module CAN implement this interface if it can be configured from wizard.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleConfigureFromWizard
{
    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public CountryInfo[] getSupportedCountries(  );

    /**
     * Calls when wizard select region.
     *
     * @param countryName region description
     * @param runSelectChannels true if we need to select channels, false if
     *        we just need to set region
     */
    void configureFromWizard( String countryName, boolean runSelectChannels );

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class CountryInfo
    {
        // ISO Country Code. See http://www.iso.ch/iso/en/prods-services/iso3166ma/02iso-3166-code-lists/list-en1.html.
        final protected String country;
        final protected int priority;
        final protected boolean supportSelectChannel;

/**
         * Creates a new CountryInfo object.
         *
         * @param country DOCUMENT ME!
         * @param priority DOCUMENT ME!
         * @param supportSelectChannels DOCUMENT ME!
         */
        public CountryInfo( 
            final String country, final int priority,
            final boolean supportSelectChannels )
        {
            this.country = country;
            this.priority = priority;
            this.supportSelectChannel = supportSelectChannels;
        }

        /**
         * DOCUMENT ME!
         *
         * @return Returns the country.
         */
        public String getCountry(  )
        {
            return country;
        }

        /**
         * DOCUMENT ME!
         *
         * @return Returns the priority.
         */
        public int getPriority(  )
        {
            return priority;
        }

        /**
         * DOCUMENT ME!
         *
         * @return Returns the supportSelectChannel.
         */
        public boolean isSupportSelectChannel(  )
        {
            return supportSelectChannel;
        }
    }
}

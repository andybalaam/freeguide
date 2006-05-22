/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.common.gui;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.lib.general.Utils;

import freeguide.common.plugininterfaces.IModuleStorage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import java.lang.IllegalAccessException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides search options and displays results.
 * Results are displayed in a scrolling list of 
 * {@link TVProgramme}.  Views (or any other class
 * for that matter) can register for mouse events
 * on this list by specifying a {@link MouseListener} 
 * in the constructor.  An example of how to use 
 * this is the class {@link ProgrammeSelectedHandler}
 *  
 * @author Graham Benton
 * @version 2
 *
 */
public class SearchDialog extends JDialog
{

    private JTextField searchForText;
    private JButton searchButton;
    private JButton closeButton;
    private JList resultList;
    private JPanel jPanelQuestion;
    private Box jPanelInput;
    private JPanel jPanelOptions;
    private Box jPanelButtons;
    private Box jPanelResult;
    private Box backgroundBox;
    private JCheckBox caseSensitive;
    private JCheckBox includeFinished;
    private JCheckBox searchDescription;

    // The programmes found from the last search
    private DefaultListModel foundModel = new DefaultListModel(  );
    private JScrollPane listScroller;

    /**
     * Class Constructor from owner.
     *
     * @param owner the <code>JFrame</code> from which the dialog is
     *        displayed
     */
    public SearchDialog( JFrame owner )
    {
        super( 
            owner, Application.getInstance(  ).getLocalizedMessage( "search" ) );

        initComponents(  );

        Utils.centreDialog( owner, this );

        setVisible( true );
    }

    /**
     * Class Constructor from owner and  a Mouse listener.
     *
     * @param owner the <code>JFrame</code> from which the dialog is
     *        displayed
     * @param m the MouseListener to handle MouseEvents from Result List. 
     */
    public SearchDialog( JFrame owner, java.awt.event.MouseListener m )
    {
        super( 
            owner, Application.getInstance(  ).getLocalizedMessage( "search" ) );

        initComponents(  );
        addMouseListener( m );
        Utils.centreDialog( owner, this );

        setVisible( true );
    }

    /**
     * Adds l to MouseListener list of the programme list.
     *
     * @param l the MouseListener to handle MouseEvents from the Result List. 
     */
    public void addMouseListener(MouseListener l)
    {
        resultList.addMouseListener( l );
    }
 
    /**
     * Class for displaying programme information in a JList.
     *
     * Displays Channel, Date, Time and Title.
     */
    class FoundProgrammeCellRenderer extends JLabel implements ListCellRenderer
    {
        public Component getListCellRendererComponent( 
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus)
        {
            TVProgramme programme = ( TVProgramme )value;

            String progInfo =
                programme.getChannel(  ).getDisplayName(  ) + " "
                + DateFormat.getInstance(  )
                            .format( 
                    new Date( programme.getStart(  ) ) ) + " "
                + programme.getTitle(  );
            setText( progInfo );

            if (isSelected) 
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else 
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;

        }
    }

    /**
     * Sets up the dialog layout.
     */
    private void initComponents(  )
    {
        jPanelQuestion = new JPanel( new FlowLayout(  ) );

        // Set up the question part of the gui
        searchForText = new JTextField( 20 );
        jPanelQuestion.add( searchForText );
        

        // Add buttons to do the work or close
        jPanelButtons = new Box( BoxLayout.X_AXIS ); 
        searchButton = new JButton( 
                Application.getInstance(  ).getLocalizedMessage( "search" ) );
        searchButton.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent evt )
                {
                    searchButtonActionPerformed( evt );
                }
            } );
        jPanelButtons.add( searchButton );

        closeButton = new JButton( 
                Application.getInstance(  ).getLocalizedMessage( "close" ) );
        closeButton.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent evt )
                {
                    setVisible( false );
                    dispose(  );
                }
            } );

        jPanelButtons.add( closeButton );

        // Show the options to the question
        jPanelOptions = new JPanel( new GridLayout( 2,2 ) );
        caseSensitive = new JCheckBox(Application.getInstance(
            ).getLocalizedMessage( "case_sensitive" ) );
        jPanelOptions.add( caseSensitive );
        includeFinished = new JCheckBox(Application.getInstance(
            ).getLocalizedMessage( "include_finished" ) );
        jPanelOptions.add( includeFinished );
        searchDescription = new JCheckBox(Application.getInstance(
            ).getLocalizedMessage( "search_descriptions" ) );
        jPanelOptions.add( searchDescription );

        // Put all the inputs into a panel
        jPanelInput = new Box( BoxLayout.Y_AXIS );
        jPanelInput.add ( jPanelQuestion );
        jPanelInput.add ( jPanelButtons );
        jPanelInput.add( jPanelOptions );


        // Add a panel to display the result
        jPanelResult = new Box( BoxLayout.Y_AXIS );
        jPanelResult.add( new JLabel( "Results" ) );
        jPanelResult.setBorder( BorderFactory.createEtchedBorder(  ) );

        resultList = new JList( foundModel );
        resultList.setCellRenderer(new FoundProgrammeCellRenderer(  ) );
        //  Example way of getting input to user
        //resultList.addMouseListener(new ProgrammeSelectedHandler (  ) );

        JScrollPane listScroller =
            new JScrollPane( resultList );
        listScroller.setAlignmentX( JScrollPane.LEFT_ALIGNMENT );
        listScroller.setPreferredSize( new Dimension( 300, 250 ) );
        jPanelResult.add( listScroller );

        getContentPane(  ).add( jPanelInput, BorderLayout.NORTH );
        getContentPane(  ).add( jPanelResult, BorderLayout.CENTER );

        getRootPane(  ).setDefaultButton( searchButton );
        pack(  );
    }

    /**
     * Performs the actual search and places result in list.
     *
     * @param evt Event that occurred
     */
    private void searchButtonActionPerformed( ActionEvent evt )
    {
        // Clear the previous search results
        foundModel.clear(  );

        IModuleStorage.Info info =
            Application.getInstance(  ).getDataStorage(  ).getInfo(  )
                       .cloneInfo(  );

        TVData data;

        try
        {
            info.channelsList = null;

            data = Application.getInstance(  ).getDataStorage(  ).get( info );
        }
        catch( Exception ex )
        {
//             FreeGuide.log.log( Level.WARNING, "Error Searching data", ex );
           JOptionPane.showMessageDialog( this, ex.toString(  ),
                  "Exception while loading Channel data",
                  JOptionPane.ERROR_MESSAGE); 

            return;
        }

        data.iterate( 
            new TVIteratorProgrammes(  )
            {
                /**
                 * Called for every channel iterated through.
                 * If we need some progress for the user, this
                 * would be a good place to add it.
                 */
                protected void onChannel( TVChannel channel )
                {
                }

                /**
                 * Checks to see if the given string matches according to 
                 * the options set.
                 *
                 * @param toMatch - toMatch the string to check 
                 */
                protected boolean matchText ( String toMatch )
                {
                    if (toMatch == null || toMatch == "")
                    {
                        return false;
                    }

                    String myMatch = new String (toMatch );
                    String lookingFor = new String( searchForText.getText(  ) );

                    if ( !caseSensitive.isSelected(  ) )
                    {
                        myMatch = myMatch.toLowerCase(  );
                        lookingFor = lookingFor.toLowerCase(  );
                    }

                    if ( myMatch.indexOf( lookingFor ) == -1 )
                    {
                        return false;
                    } 
                    else
                    {
                        return true;
                    }
                }

                /**
                 * Called for every Programme.
                 * Check that the programme matches the parameters,
                 * and if so add it to the list.
                 */
                protected void onProgramme( TVProgramme programme )
                {

                    // If Title does not contain looked for text
                    if ( matchText( programme.getTitle(  ) ) == false )
                    {
                        // Check other fields if selected
                        if ( searchDescription.isSelected(  ) )
                        {
                            if ( matchText ( programme.getSubTitle(  ) ) == false  
                                    &&
                                    matchText ( programme.getDescription(  ) ) == false  )
                            {
                                // Can't find text in other fields either
                                return;
                            }

                        }
                        else
                        {
                            return;
                        }
                    }

                    // Here we have a record with matching text                    
                    // Check finishing time is after now, if required
                    if ( !includeFinished.isSelected(  ) )
                    {
                        if (programme.getEnd(  ) < System.currentTimeMillis(  ) )
                        {
                            return;
                        }
                    }

                    // Programme matches all criteria, so add it to the list
                    foundModel.addElement( programme );
                }
            } );
    }

    /**
     * Example MouseAdaptor that constructing classes can use to get selection.
     */
    class ProgrammeSelectedHandler extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if (e.getClickCount( ) == 2)
            {
                JList programmeList = ( JList ) e.getSource(  );

                // Print out the value of the program selected
                System.out.println(programmeList.getSelectedValue(  ).toString(  ) );
            }
        }
    }
}

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.lang.IllegalAccessException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
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
 * Provides search options and displays results
 *
 * @author Graham Benton
 * @version 2
 */
public class SearchDialog extends JDialog
{
    private javax.swing.JTextField searchForText;
    private javax.swing.JButton searchButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JList resultList;
    private javax.swing.JPanel jPanelQuestion;
    private javax.swing.Box jPanelResult;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.Box backgroundBox;
    private java.awt.event.MouseListener programmeMouseListener;
    
    // The programmes found from the last search
    private DefaultListModel foundModel = new DefaultListModel(  );
    private javax.swing.JScrollPane listScroller;
    
    /**
     *
     * @param owner - the <code>JFrame</code> from which the dialog is
     *        displayed
     * @param m     - MouseListener to handle MouseEvents from Result List. 
     */
    public SearchDialog( JFrame owner, java.awt.event.MouseListener m )
    {
        super( 
            owner, Application.getInstance(  )
            .getLocalizedMessage( "search" ) );
        
        programmeMouseListener = m;
        initComponents(  );
        Utils.centreDialog( owner, this );
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
        jPanelQuestion = new javax.swing.JPanel( new java.awt.FlowLayout(  ) );
        
        searchForText = new javax.swing.JTextField( 20 );
        jPanelQuestion.add( searchForText );
        
        searchButton = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "search" ) );
        searchButton.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    searchButtonActionPerformed( evt );
                }
            } );
        jPanelQuestion.add( searchButton );
        
        closeButton = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "close" ) );
        closeButton.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    setVisible( false );
                    dispose(  );
                }
            } );
        
        //closeButton.setMnemonic( KeyEvent.VK_C );
        jPanelQuestion.add( closeButton );
        
        jPanelResult = new javax.swing.Box( BoxLayout.Y_AXIS );
        jPanelResult.add( new javax.swing.JLabel(
            Application.getInstance(  ).getLocalizedMessage( "results" ) ) );
        jPanelResult.setBorder( BorderFactory.createEtchedBorder(  ) );
        
        resultList = new javax.swing.JList( foundModel );
        resultList.setCellRenderer(new FoundProgrammeCellRenderer(  ) );
        resultList.addMouseListener(programmeMouseListener);
        //resultList.addMouseListener(new ProgrammeSelectedHandler (  ) );
        
        javax.swing.JScrollPane listScroller =
            new javax.swing.JScrollPane( resultList );
        listScroller.setAlignmentX( javax.swing.JScrollPane.LEFT_ALIGNMENT );
        listScroller.setPreferredSize( new Dimension( 300, 250 ) );
        jPanelResult.add( listScroller );
        
        getContentPane(  ).add( jPanelQuestion, BorderLayout.NORTH );
        getContentPane(  ).add( jPanelResult, BorderLayout.CENTER );
        
        getRootPane(  ).setDefaultButton( searchButton );
        pack(  );
        
        setVisible( true );
    }
    
    /**
     * performs the actual search and places result in list
     *
     * @param evt Event that occurred
     */
    private void searchButtonActionPerformed( java.awt.event.ActionEvent evt )
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
            Application.getInstance(  ).getLogger(  ).log(
                Level.WARNING, "Error Searching data.", ex );
            
            return;
        }
        
        data.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }
                
                protected void onProgramme( TVProgramme programme )
                {
                    // If title contains looked for text (lower case)
                    if( 
                        programme.getTitle(  ).toLowerCase(  ).indexOf(
                            searchForText.getText(  ).toLowerCase(  ) ) != -1 )
                    {
                        foundModel.addElement( programme );
                    }
                }
            } );
    }
    
    /**
     * Example MouseAdaptor that constructing classes can use to get selection.
     */
    /*class ProgrammeSelectedHandler extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            if (e.getClickCount( ) == 2)
            {
                JList programmeList = ( JList ) e.getSource(  );
                
                // Print out the value of the program selected
                System.out.println(programmeList.getSelectedValue(  )
                    .toString(  ) );
            }
        }
    }*/
}

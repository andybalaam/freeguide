/*
 * FreeGuide J2
 *
 * Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Graphics;

/**
 * A little frame that shows a string, used to show the output of a command
 * execution in FreeGuide (from FreeGuideExecutorDialog).
 *
 * @author Andy Balaam
 * @version 2
 */
public class StringViewer extends javax.swing.JFrame {
	
	public StringViewer(String outputText, String errorText) {
		initComponents();
		bufOut.append(outputText);
		bufErr.append(errorText);
		setVisible(false);
	}
	
    private void initComponents() {
        splitpane = new javax.swing.JSplitPane();
        scrOutput = new javax.swing.JScrollPane();
        txaOutput = new javax.swing.JTextArea();
        scrError = new javax.swing.JScrollPane();
        txaError = new javax.swing.JTextArea();

        getContentPane().setLayout(new java.awt.GridLayout());

        setTitle("View Command Output");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        splitpane.setDividerLocation(100);
        splitpane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        txaOutput.setEditable(false);
        scrOutput.setViewportView(txaOutput);

        splitpane.setTopComponent(scrOutput);

        txaError.setEditable(false);
        scrError.setViewportView(txaError);

        splitpane.setBottomComponent(scrError);

        getContentPane().add(splitpane);

        pack();
		
        java.awt.Dimension screenSize =
			java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		
        setSize(new java.awt.Dimension(450, 400));
        setLocation((screenSize.width-450)/2,(screenSize.height-400)/2);
    }

	public void paint(Graphics g) {
		txaOutput.setText(bufOut.toString());
		txaError.setText(bufErr.toString());
		super.paint(g);
	}

	private void exitForm(java.awt.event.WindowEvent evt) {
		dispose();
	}

	public StringBuffer getOutput() {
		return(bufOut);
	}
	public StringBuffer getError() {
		return(bufErr);
	}

    private javax.swing.JTextArea txaOutput;
    private javax.swing.JTextArea txaError;
    private javax.swing.JScrollPane scrError;
    private javax.swing.JScrollPane scrOutput;
    private javax.swing.JSplitPane splitpane;
	private StringBuffer bufOut = new StringBuffer();
	private StringBuffer bufErr = new StringBuffer();
}

package org.jhotdraw.samples.uml.figures;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.DrawingView;


/**
 * @author C.F.Morrison
 * <p>
 * July 1, 2009
 * <p>
 * <i> Code line length 120 </i>
 * <p>
 */
public class UMLToolTipWindow extends JWindow {

    private static final long serialVersionUID = -732109158436904930L;

    static private UMLToolTipWindow  singleton;

    /**
    *

    *
    */
   public synchronized static UMLToolTipWindow getInstance(DrawingView view) {
       if (singleton == null) {
           try {
                final DefaultDrawingView currentView = (DefaultDrawingView)view;
                final JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(currentView);
                singleton = new UMLToolTipWindow(frame);
           }
           catch (final Exception ex) {
               ex.printStackTrace();
                System.exit(1);
           }
       }
       return singleton;
   }
    private final JLabel header;


    private final JTextArea textArea;




    /**
     * @param owner
     */
    private UMLToolTipWindow(JFrame owner) {
        super(owner);

        UIManager.getDefaults();



        header = new JLabel();
        textArea = new JTextArea();
        final JPanel panel = new JPanel();
        panel.setBorder(UIManager.getBorder("ToolTip.border"));
        panel.setLayout(new BorderLayout());

        panel.setBackground(UIManager.getColor("ToolTip.background"));
        panel.setForeground(UIManager.getColor("ToolTip.foreground"));


        textArea.setBackground(UIManager.getColor("ToolTip.background"));
        textArea.setForeground(UIManager.getColor("ToolTip.foreground"));
        panel.add(header,BorderLayout.NORTH);
        //panel.add(textArea,BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.CENTER);

        Font f = UIManager.getFont("ToolTip.font");
        f = f.deriveFont(10F);
        final Font f2 = f.deriveFont(Font.BOLD);
        header.setFont(f2);
        textArea.setFont(f);
        setVisible(false);
    }

    public void setHeader(String text) {
        header.setText(text);
    }

    public void setText(String text) {
        textArea.setText(text);
    }

}

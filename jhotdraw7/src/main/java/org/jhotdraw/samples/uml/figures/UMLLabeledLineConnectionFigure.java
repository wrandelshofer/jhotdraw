package org.jhotdraw.samples.uml.figures;

import java.awt.Font;
import java.io.IOException;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.LabeledLineConnectionFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.locator.BezierLabelLocator;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

/**
 * The XML for the super class LabeledLineConnectionFigure is far too verbose
 *
 * @author C.F.Morrison
 *         <p>
 *         July 1, 2009
 *         <p>
 *         <i> Code line length 120 </i>
 *         <p>
 */
public class UMLLabeledLineConnectionFigure extends LabeledLineConnectionFigure {

    private static final long serialVersionUID = 1L;

    public UMLLabeledLineConnectionFigure() {
    }

    /**
     * @param text
     * @param f
     * @param start
     */
    public void createLabel(String text, Font f, boolean start)  {
        final TextFigure label = new TextFigure();
        label.set(AttributeKeys.FONT_FACE, f);
        label.set(AttributeKeys.FONT_SIZE, (double)f.getSize2D());
        if (start)
            label.set(AttributeKeys.LABEL_LOCATOR, new BezierLabelLocator(0, -Math.PI / 4, 8));
        else
            label.set(AttributeKeys.LABEL_LOCATOR, new BezierLabelLocator(1, Math.PI + Math.PI / 4, 8));
        label.setAttributeEnabled(AttributeKeys.START_DECORATION, false);
        label.setAttributeEnabled(AttributeKeys.END_DECORATION, false);
        label.setEditable(true);
        label.setText(text);
        label.setVisible(true);
        add(label);
    }


    /* (non-Javadoc)
     * @see org.jhotdraw.draw.LabeledLineConnectionFigure#read(org.jhotdraw.xml.DOMInput)
     */
    @Override
    public void read(DOMInput in) throws IOException {
        readAttributes(in);
        readLiner(in);
        // Note: Points must be read after Liner, because Liner influences
        // the location of the points.
        readPoints(in);

        in.openElement("startLabel");
        final String startLabelText = (String) in.readObject();
        in.closeElement();

        in.openElement("endLabel");
        final String endLabelText = (String) in.readObject();
        in.closeElement();

        Font f = get(AttributeKeys.FONT_FACE);
        f = f.deriveFont((float)get(AttributeKeys.FONT_SIZE).doubleValue());
        willChange();
        createLabel(startLabelText, f, true);
        createLabel(endLabelText, f, false);
        changed();
    }

    /* (non-Javadoc)
     * @see org.jhotdraw.draw.LabeledLineConnectionFigure#write(org.jhotdraw.xml.DOMOutput)
     */
    @Override
    public void write(DOMOutput out) throws IOException {
        writePoints(out);
        writeAttributes(out);
        writeLiner(out);

        final TextFigure label0 = (TextFigure)getChild(0);
        out.openElement("startLabel");
        out.writeObject(label0.getText());
        out.closeElement();
        final TextFigure label1 = (TextFigure)getChild(1);
        out.openElement("endLabel");
        out.writeObject(label1.getText());
        out.closeElement();
    }
}

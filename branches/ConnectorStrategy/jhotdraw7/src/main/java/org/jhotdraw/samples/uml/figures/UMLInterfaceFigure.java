package org.jhotdraw.samples.uml.figures;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.jhotdraw.draw.RoundRectangleFigure;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;


/**
 * @author C.F.Morrison
 * <p>
 * July 1, 2009
 * <p>
 * <i> Code line length 120 </i>
 * <p>
 */
public class UMLInterfaceFigure extends UMLFigure {


    private static final long serialVersionUID = 1L;

    public UMLInterfaceFigure() {
        super();
        setPresentationFigure(new RoundRectangleFigure());
        setInterfaceLabel("<<interface>>");
        setNameCompartment("InterfaceName");
        setAbstractLabel("{abstract}");
        set(FILL_COLOR,Color.white);
        setAttributeEnabled(FILL_COLOR, false);
    }

    @Override
    public void read(DOMInput in) throws IOException {
        willChange();
        final double x = in.getAttribute("x", 0d);
        final double y = in.getAttribute("y", 0d);
        final double w = in.getAttribute("w", 0d);
        final double h = in.getAttribute("h", 0d);


        final String name = in.getAttribute("name", "???");
        setNameCompartment(name);
        readAttributes(in);

        // this MUST be the final statement in read!
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        changed();
    }

    @Override
    public void write(DOMOutput out) throws IOException {
        final Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        out.addAttribute("w", r.width);
        out.addAttribute("h", r.height);
        out.addAttribute("name", getNameCompartment().getText());
        writeAttributes(out);
    }

}
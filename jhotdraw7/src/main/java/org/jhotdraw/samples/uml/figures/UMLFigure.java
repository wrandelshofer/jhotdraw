package org.jhotdraw.samples.uml.figures;


import static org.jhotdraw.draw.AttributeKeys.FONT_BOLD;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.ResizeHandleKit;
import org.jhotdraw.geom.Insets2D;



/**
 * <p>
 *
 * @author C.F.Morrison
 * <p>
 *         July 1, 2009
 * <p>
 *
 */
public abstract class UMLFigure extends GraphicalCompositeFigure {

    private static final long serialVersionUID = 1L;
    static int INTERFACE_INDEX = 0;
    static int NAME_INDEX = 1;
    static int ABSTRACT_INDEX = 2;
    static int SEPARATOR_INDEX = 3;
    static int ATTRIBUTE_INDEX = 4;



    public UMLFigure() {
        super(new RectangleFigure());
        setLayouter(new UMLLayouter());

        TextFigure interfaceLabel = new TextFigure("");
        TextFigure  nameCompartment = new TextFigure("");
        TextFigure abstractLabel = new TextFigure("");
        UMLSeparatorLineFigure separator1 = new UMLSeparatorLineFigure();
        TextFigure attributeCompartment = new TextFigure("");


        FONT_BOLD.set(nameCompartment, true);
        nameCompartment.setAttributeEnabled(STROKE_COLOR, false);
        nameCompartment.setAttributeEnabled(FONT_BOLD, false);

        separator1.setAttributeEnabled(AttributeKeys.START_DECORATION, false);
        separator1.setAttributeEnabled(AttributeKeys.END_DECORATION, false);
        setAttributeEnabled(AttributeKeys.START_DECORATION, false);
        setAttributeEnabled(AttributeKeys.END_DECORATION, false);

        add(INTERFACE_INDEX, interfaceLabel);
        add(NAME_INDEX, nameCompartment);
        add(ABSTRACT_INDEX , abstractLabel);
        add(SEPARATOR_INDEX, separator1);
        add(ATTRIBUTE_INDEX, attributeCompartment);

        Insets2D.Double insets = new Insets2D.Double(4, 8, 4, 8);
        LAYOUT_INSETS.set(nameCompartment, insets);
        LAYOUT_INSETS.set(attributeCompartment, insets);

//        ResourceBundleUtil labels =
//                ResourceBundleUtil.getBundle("org.jhotdraw.samples.uml.Labels");
    }


    /* (non-Javadoc)
     * @see org.jhotdraw.draw.GraphicalCompositeFigure#createHandles(int)
     */
    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case -1:
                handles.add(new BoundsOutlineHandle(this,false,true));
                break;
            case 0:
                ResizeHandleKit.addResizeHandles(this, handles);
                break;
        }
        return handles;
    }

    /**
     * we set the presentation figure's bounds based on
     * {@link UMLLayouter#calculateMinimum} before we layout
     */
    @Override
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        UMLLayouter umlLayouter = (UMLLayouter)getLayouter();
        Rectangle2D.Double minBounds = umlLayouter.calculateMinimum(this, anchor);

        double x  = Math.min(anchor.x, lead.x);
        double y  = Math.min(anchor.y , lead.y);
        double w  = Math.max(minBounds.width, Math.abs(lead.x - anchor.x));
        double h  = Math.max(minBounds.height, Math.abs(lead.y - anchor.y));

        Point2D.Double a = new Point2D.Double(x, y);
        Point2D.Double l = new Point2D.Double(x+w, y+h);

        basicSetPresentationFigureBounds(a, l);
        getLayouter().layout(this, a, l);
        invalidate();
    }

    @Override
    public String getToolTipText(Point2D.Double p) {

        return toString();
    }

    @Override
    public UMLFigure clone() {
        UMLFigure that = (UMLFigure) super.clone();
        return that;
    }
    @Override
    public int getLayer() {
        return 0;
    }
    @Override
    public String toString() {
        return getNameCompartment().getText();
    }

    /**
     * @return the interfaceLabel
     */
    public TextFigure getInterfaceLabel() {
        return (TextFigure)getChild(INTERFACE_INDEX);
    }

    public void setInterfaceLabel(String s) {
        TextFigure interfaceLabel = (TextFigure)getChild(INTERFACE_INDEX);
        interfaceLabel.setText(s);
    }
    /**
     * @return the nameCompartment
     */
    public TextFigure getNameCompartment() {
        return (TextFigure)getChild(NAME_INDEX);
    }

    public void setNameCompartment(String s) {
        TextFigure nameCompartment = (TextFigure)getChild(NAME_INDEX);
        nameCompartment.setText(s);
    }

    /**
     * @return the abstractLabel
     */
    public TextFigure getAbstractLabel() {
        return (TextFigure)getChild(ABSTRACT_INDEX);
    }


    /**
     * @param s
     */
    public void setAbstractLabel(String s) {
        TextFigure abstractLabel = (TextFigure)getChild(ABSTRACT_INDEX);
        abstractLabel.setText(s);
    }


    /**
     * @return the separator1
     */
    public UMLSeparatorLineFigure getSeparator1() {
        return (UMLSeparatorLineFigure) getChild(SEPARATOR_INDEX);
    }


    /**
     * @return the attributeCompartment
     */
    public TextFigure getAttributeCompartment() {
        return (TextFigure)getChild(ATTRIBUTE_INDEX);
    }

}
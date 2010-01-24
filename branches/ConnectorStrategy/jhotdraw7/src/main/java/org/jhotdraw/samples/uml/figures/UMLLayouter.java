package org.jhotdraw.samples.uml.figures;

import static org.jhotdraw.draw.AttributeKeys.LAYOUT_INSETS;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.layouter.AbstractLayouter;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Insets2D;


/**
 * @author C.F.Morrison
 * <p>
 * July 1, 2009
 * <p>
 * <i> Code line length 120 </i>
 * <p>
 */
public class UMLLayouter extends AbstractLayouter {

    /**
     * we set the presentation figure's bounds based on {@code calculateMinimum}
     * before we layout
     */
    public Rectangle2D.Double calculateLayout(CompositeFigure layoutable,
            Point2D.Double anchor, Point2D.Double lead) {
        // return the presentation figure's bounds
        return layoutable.getBounds();
    }

    /**
     * calculates the minimum size for the composite figure
     *
     * @param layoutable
     * @param anchor
     * @return minimum bounds
     */
    public Rectangle2D.Double calculateMinimum(CompositeFigure layoutable, Point2D.Double anchor) {
        Insets2D.Double layoutInsets = LAYOUT_INSETS.get(layoutable);
        if (layoutInsets == null) {
            layoutInsets = new Insets2D.Double(0, 0, 0, 0);
        }
        final Rectangle2D.Double minimumBounds = new Rectangle2D.Double(anchor.x, anchor.y, 0, 0);
        for (final Figure child : layoutable.getChildren()) {
            if (child.isVisible()) {
                final Dimension2DDouble preferredSize = child.getPreferredSize();
                final Insets2D.Double ins = getInsets(child);
                minimumBounds.width = Math.max(minimumBounds.width, preferredSize.width + ins.left + ins.right);
                minimumBounds.height += preferredSize.height + ins.top + ins.bottom;
            }
        }
        minimumBounds.width += layoutInsets.left + layoutInsets.right;
        minimumBounds.height += layoutInsets.top + layoutInsets.bottom;
        return minimumBounds;
    }



    /*
     * (non-Javadoc)
     *
     * @see org.jhotdraw.draw.Layouter#layout(org.jhotdraw.draw.CompositeFigure,
     * java.awt.geom.Point2D.Double, java.awt.geom.Point2D.Double)
     */
    public Rectangle2D.Double layout(CompositeFigure layoutable, Point2D.Double anchor,
            Point2D.Double lead) {
        Insets2D.Double layoutInsets = LAYOUT_INSETS.get(layoutable);
        if (layoutInsets == null) {
            layoutInsets = new Insets2D.Double();
        }
        final UMLFigure umlFigure = (UMLFigure)layoutable;
        final Rectangle2D.Double layoutBounds = calculateLayout(layoutable, anchor, lead);


        double y = layoutBounds.y + layoutInsets.top;
        Insets2D.Double insets = null;
        double height = 0;
        double width = 0;

        final TextFigure interfaceLabel = umlFigure.getInterfaceLabel();
        insets = getInsets(interfaceLabel);
        height = interfaceLabel.getBounds().height;
        width = interfaceLabel.getBounds().width;
        interfaceLabel.setBounds(
                new Point2D.Double(
                        layoutBounds.x + (layoutBounds.width - width) / 2d,
                        y + insets.top),
                new Point2D.Double(
                        layoutBounds.x + (layoutBounds.width + width) / 2d,
                        y + insets.top + height));
        y += height + insets.top + insets.bottom;



        final TextFigure nameCompartment = umlFigure.getNameCompartment();
        insets = getInsets(nameCompartment);
        height = nameCompartment.getBounds().height;
        width = nameCompartment.getBounds().width;
        nameCompartment.setBounds(
                new Point2D.Double(
                        layoutBounds.x + (layoutBounds.width - width) / 2d,
                        y + insets.top),
                new Point2D.Double(
                        layoutBounds.x + (layoutBounds.width + width) / 2d,
                        y + insets.top + height));
        y += height + insets.top + insets.bottom;

        final TextFigure abstractLabel = umlFigure.getAbstractLabel();
        insets = getInsets(abstractLabel);
        height = abstractLabel.getBounds().height;
        width = abstractLabel.getBounds().width;
        abstractLabel.setBounds(
                new Point2D.Double(
                    layoutBounds.x + layoutBounds.width - width - layoutInsets.right - insets.right,
                    y + insets.top),
                new Point2D.Double(
                    layoutBounds.x + layoutBounds.width - layoutInsets.right - insets.right,
                    y + insets.top + height));

        y += height + insets.top + insets.bottom;

        final UMLSeparatorLineFigure separator1 = umlFigure.getSeparator1();
        insets = getInsets(separator1);
        height = separator1.getBounds().height;
        width = separator1.getBounds().width;
        separator1.setBounds(
               new Point2D.Double(
                       layoutBounds.x + layoutInsets.left + insets.left,
                       y + insets.top),
               new Point2D.Double(
                       layoutBounds.x + layoutBounds.width - layoutInsets.right - insets.right,
                       y + insets.top + height));
        y += height + insets.top + insets.bottom;

        final TextFigure attributeCompartment = umlFigure.getAttributeCompartment();
        insets = getInsets(attributeCompartment);
        height = attributeCompartment.getBounds().height;
        width = attributeCompartment.getBounds().width;
        attributeCompartment.setBounds(
               new Point2D.Double(
                       layoutBounds.x + layoutInsets.left + insets.left,
                       y + insets.top),
               new Point2D.Double(
                       layoutBounds.x + layoutBounds.width - layoutInsets.right - insets.right,
                       y + insets.top + height));
        y += height + insets.top + insets.bottom;

        return layoutBounds;
    }
}

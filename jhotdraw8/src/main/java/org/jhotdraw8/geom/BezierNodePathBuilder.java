/* @(#)BezierNodePathBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import org.jhotdraw8.collection.ImmutableObservableList;

/**
 * BezierNodePathBuilder.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class BezierNodePathBuilder extends AbstractPathBuilder {

    private List<BezierNode> nodes = new ArrayList<BezierNode>();

    private void add(BezierNode newValue) {
        nodes.add(newValue);
    }

    @Override
    protected void doClosePath() {
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        BezierNode last = getLastNode();

        last=new BezierNode(last.getMask() | BezierNode.C2_MASK, last.isEquidistant(), last.isColinear(), last.getX0(), last.getY0(), last.getX1(), last.getY1(), x1, y1);
        if (last.computeIsColinear()) last=last.setColinear(true);
        setLast(last);
        add(new BezierNode(BezierNode.C0C1_MASK, false, false, x, y, x2, y2, x - x2 + x, y - y2 + y));
    }

    @Override
    protected void doLineTo(double x, double y) {
        add(new BezierNode(BezierNode.C0_MASK, false, false, x, y, x, y, x, y));
    }

    @Override
    protected void doMoveTo(double x, double y) {
        add(new BezierNode(BezierNode.C0_MASK|BezierNode.MOVE_MASK, false, false, x, y, x, y, x, y));
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x, double y) {
        add(new BezierNode(BezierNode.C0C1_MASK, false, false, x, y, x1, y1, x1, y1));
    }

    @Override
    protected void doSmoothCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        BezierNode last = getLastNode();
        setLast(new BezierNode(last.getMask() | BezierNode.C2_MASK, true, true, last.getX0(), last.getY0(), last.getX1(), last.getY1(), x1, y1));
        add(new BezierNode(BezierNode.C0C1_MASK, false, false, x, y, x1,y1,x2,y2));
    }

    @Override
    protected void doSmoothQuadTo(double x1, double y1, double x, double y) {
        BezierNode last = getLastNode();
        setLast(new BezierNode(last.getMask(), true, true, last.getX0(), last.getY0(), last.getX1(), last.getY1(), last.getX2(), last.getY2()));
        add(new BezierNode(BezierNode.C0C1_MASK, false, false, x, y, x1, y1, x1 ,y1));
    }

    private BezierNode getLastNode() {
        return nodes.get(nodes.size() - 1);
    }

    private void setLast(BezierNode newValue) {
        nodes.set(nodes.size() - 1, newValue);
    }

    public ImmutableObservableList<BezierNode> getNodes() {
        return new ImmutableObservableList<>(nodes);
    }
    @Override
    protected void doFinish() {
      // 
    }


}

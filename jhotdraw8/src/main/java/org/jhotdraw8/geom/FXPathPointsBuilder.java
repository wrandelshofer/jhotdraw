/* @(#)FXPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javax.annotation.Nonnull;

/**
 * Creates a square at each move-to and at the end of the specified path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FXPathPointsBuilder extends AbstractPathBuilder {

    private boolean needsSquareAtLastPoint;
    private double squareSize = 5;

    public FXPathPointsBuilder() {
        this(5, new ArrayList<>());
    }

    public FXPathPointsBuilder(List<PathElement> elements) {
        this(5, elements);
    }

    public FXPathPointsBuilder(int squareSize, List<PathElement> elements) {
        this.elements = elements;
        this.squareSize = squareSize;
    }

    @Override
    protected void doArcTo(double rx, double ry, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
        needsSquareAtLastPoint = true;
    }

    private List<PathElement> elements;

    @Override
    protected void doClosePath() {
        if (needsSquareAtLastPoint) {
            addSquare(getLastX(), getLastY());
            needsSquareAtLastPoint = false;
        }
    }

    @Override
    protected void doCurveTo(double x, double y, double x0, double y0, double x1, double y1) {
        needsSquareAtLastPoint = true;
    }

    @Override
    protected void doLineTo(double x, double y) {
        needsSquareAtLastPoint = true;
    }

    @Override
    protected void doMoveTo(double x, double y) {
        if (needsSquareAtLastPoint) {
            addSquare(getLastX(), getLastY());
            needsSquareAtLastPoint = false;
        }
        addSquare(x, y);
    }

    @Override
    protected void doQuadTo(double x, double y, double x0, double y0) {
        needsSquareAtLastPoint = true;
    }

    @Nonnull
    public Path build() {
        if (needsSquareAtLastPoint) {
            addSquare(getLastX(), getLastY());
            needsSquareAtLastPoint = false;
        }
        return new Path(elements);
    }

    public List<PathElement> getElements() {
        if (needsSquareAtLastPoint) {
            addSquare(getLastX(), getLastY());
            needsSquareAtLastPoint = false;
        }
        return elements;
    }

    private void addSquare(double x, double y) {
        double halfSize = squareSize * 0.5;
        elements.add(new MoveTo(x - halfSize, y - halfSize));
        elements.add(new LineTo(x + halfSize, y - halfSize));
        elements.add(new LineTo(x + halfSize, y + halfSize));
        elements.add(new LineTo(x - halfSize, y + halfSize));
        elements.add(new ClosePath());
    }

    @Override
    protected void doPathDone() {
// empty
    }

}

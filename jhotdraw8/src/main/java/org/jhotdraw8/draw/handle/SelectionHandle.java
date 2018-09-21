/* @(#)ConnectionFigureConnectionHandle.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.geom.Transforms;

/**
 * Handle for showing that a figure is selected.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectionHandle extends LocatorHandle {

    private Point2D pickLocation;
    @Nonnull
    private final Region node;
    private final String styleclass;
    private static final Rectangle REGION_SHAPE = new Rectangle(5, 5);
    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));



    public SelectionHandle(Figure figure, Locator locator) {
        this(figure, locator, STYLECLASS_HANDLE_MOVE);
    }

    public SelectionHandle(Figure figure, Locator locator, String styleclass) {
        super(figure, locator);
        this.styleclass = styleclass;
        node = new Region();
        node.setShape(REGION_SHAPE);
        node.setManaged(false);
        node.setScaleShape(false);
        node.setCenterShape(true);
        node.resize(11, 11);

        node.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public Cursor getCursor() {
        return Cursor.DEFAULT;
    }

    @Nonnull
    @Override
    public Region getNode() {
        return node;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = owner;
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = f.getBoundsInLocal();
        Point2D p = getLocation();
        //Point2D p = unconstrainedPoint!=null?unconstrainedPoint:f.get(pointKey);
        pickLocation= p = t == null ? p : t.transform(p);

        // The node is centered around the location. 
        // (The value 5.5 is half of the node size, which is 11,11.
        // 0.5 is subtracted from 5.5 so that the node snaps between pixels
        // so that we get sharp lines. 
        node.relocate(p.getX() - 5, p.getY() - 5);

        // rotates the node:
        node.setRotate(f.getStyled(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));
    }

    @Override
    public void handleMousePressed(MouseEvent event, DrawingView view) {
       
    }

    @Override
    public void handleMouseDragged(MouseEvent event, DrawingView view) {
        
    }

  
  

    @Override
    public void handleMouseReleased(MouseEvent event, DrawingView dv) {
      
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    public Point2D getLocationInView() {
        return pickLocation;
    }
}

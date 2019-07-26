/*
 * @(#)LineConnectorHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.ConnectingFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Transforms;

import java.util.function.Function;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;

/**
 * Handle for the start or end point of a connection figure.
 * <p>
 * Pressing the alt or the control key while dragging the handle prevents
 * connecting the point.
 * <p>
 * This handle is drawn using a {@code Region}, which can be styled using
 * {@code styleclassDisconnected} and {@code styleclassConnected} given in the
 * constructor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LineConnectorHandle extends AbstractConnectorHandle {
    public static final BorderStrokeStyle INSIDE_STROKE = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1.0, 0, null);

    @Nullable
    private Background REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private Background REGION_BACKGROUND_DISCONNECTED = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nullable
    private static final Function<Color, Border> REGION_BORDER = color -> new Border(
            new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(2)),
            new BorderStroke(color, BorderStrokeStyle.SOLID, null, null)
    );
    private static final Circle REGION_SHAPE = new Circle(4);

    @Nonnull
    private final Region targetNode;

    public LineConnectorHandle(@Nonnull ConnectingFigure figure, @Nonnull NonnullMapAccessor<CssPoint2D> pointKey,
                               @Nonnull MapAccessor<Connector> connectorKey, @Nonnull MapAccessor<Figure> targetKey) {
        this(figure, STYLECLASS_HANDLE_CONNECTION_POINT_DISCONNECTED, STYLECLASS_HANDLE_CONNECTION_POINT_CONNECTED, pointKey,
                connectorKey, targetKey);
    }

    public LineConnectorHandle(@Nonnull ConnectingFigure figure, @Nonnull String styleclassDisconnected, @Nonnull String styleclassConnected, @Nonnull NonnullMapAccessor<CssPoint2D> pointKey,
                               @Nonnull MapAccessor<Connector> connectorKey, @Nonnull MapAccessor<Figure> targetKey) {
        super(figure, styleclassDisconnected, styleclassConnected, pointKey,
                connectorKey, targetKey);
        targetNode = new Region();
        targetNode.setShape(REGION_SHAPE);
        targetNode.setManaged(false);
        targetNode.setScaleShape(true);
        targetNode.setCenterShape(true);
        targetNode.resize(10, 10);
    }


    @Nonnull
    @Override
    public Region getNode(DrawingView view) {
        double size = view.getEditor().getHandleSize();
        if (targetNode.getWidth() != size) {
            targetNode.resize(size, size);
        }
        CssColor color = view.getEditor().getHandleColor();
        Color color1 = (Color) Paintable.getPaint(color);
        targetNode.setBorder(REGION_BORDER.apply(color.getColor()));
        REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(color1, null, null));
        return targetNode;
    }


    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Point2D p = f.getNonnull(pointKey).getConvertedValue();
        pickLocation = p = t.transform(p);
        Connector connector = f.get(connectorKey);
        Figure target = f.get(targetKey);
        boolean isConnected = connector != null && target != null;
        targetNode.setBackground(isConnected ? REGION_BACKGROUND_CONNECTED : REGION_BACKGROUND_DISCONNECTED);
//        targetNode.getStyleClass().set(0, isConnected ? styleclassConnected : styleclassDisconnected);
        double size = targetNode.getWidth();
        targetNode.relocate(p.getX() - size * 0.5, p.getY() - size * 0.5);
        // rotates the node:
        targetNode.setRotate(f.getStyledNonnull(ROTATE));
        targetNode.setRotationAxis(f.getStyledNonnull(ROTATION_AXIS));

        if (connector != null && target != null) {
            connectorLocation = view.worldToView(connector.getPositionInWorld(owner, target));
            targetNode.relocate(connectorLocation.getX() - size * 0.5, connectorLocation.getY() - size * 0.5);
        } else {
            connectorLocation = null;
        }
    }

}

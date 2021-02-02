/*
 * @(#)LabelConnectorHandle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.ConnectingFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Geom;

import java.util.function.Function;

import static java.lang.Math.PI;

/**
 * Handle for the start or end point of a connection figure.
 * <p>
 * Pressing the alt or the control key while dragging the handle prevents
 * connecting the point.
 *
 * @author Werner Randelshofer
 */
public class LabelConnectorHandle extends AbstractConnectorHandle {
    @Nullable
    public static final BorderStrokeStyle INSIDE_STROKE = new BorderStrokeStyle(StrokeType.INSIDE, StrokeLineJoin.MITER, StrokeLineCap.BUTT, 1.0, 0, null);

    @NonNull
    private Background REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private Background REGION_BACKGROUND_DISCONNECTED = new Background(new BackgroundFill(Color.WHITE, null, null));
    private static final Function<Color, Border> REGION_BORDER = color -> new Border(
            new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(2)),
            new BorderStroke(color, BorderStrokeStyle.SOLID, null, null)
    );
    protected static final Circle REGION_SHAPE = new Circle(4);

    @NonNull
    protected final Group groupNode;
    @NonNull
    protected final Region targetNode;
    @NonNull
    protected final Line lineNode;

    @NonNull
    protected final NonNullMapAccessor<CssPoint2D> originKey;

    @Nullable
    protected Point2D connectorTangent;


    public LabelConnectorHandle(@NonNull ConnectingFigure figure,
                                NonNullMapAccessor<CssPoint2D> originKey, NonNullMapAccessor<CssPoint2D> pointKey,
                                MapAccessor<Connector> connectorKey, MapAccessor<Figure> targetKey) {
        super(figure, pointKey,
                connectorKey, targetKey);

        this.originKey = originKey;
        lineNode = new Line();
        lineNode.setMouseTransparent(true);
        lineNode.setManaged(false);
        targetNode = new Region();
        targetNode.setShape(REGION_SHAPE);
        targetNode.setMouseTransparent(true);
        targetNode.setManaged(false);
        targetNode.setScaleShape(true);
        targetNode.setCenterShape(true);
        targetNode.resize(10, 10);
        targetNode.setManaged(false);
        groupNode = new Group();
        groupNode.setManaged(false);
        groupNode.getChildren().addAll(lineNode, targetNode);
    }


    @NonNull
    @Override
    public Group getNode(@NonNull DrawingView view) {
        double size = view.getEditor().getHandleSize();
        if (targetNode.getWidth() != size) {
            targetNode.resize(size, size);
        }
        CssColor color = view.getEditor().getHandleColor();
        Color color1 = (Color) Paintable.getPaint(color);
        targetNode.setBorder(REGION_BORDER.apply(color.getColor()));
        REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(color1, null, null));
        lineNode.setStroke(color.getColor());
        updateNode(view);
        return groupNode;
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        Figure f = getOwner();
        Transform t = FXTransforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Point2D p = f.getNonNull(pointKey).getConvertedValue();
        pickLocation = p = t.transform(p);
        final Connector connector = f.get(connectorKey);
        final Figure target = f.get(targetKey);
        boolean isConnected = connector != null && target != null;
        targetNode.setBackground(isConnected ? REGION_BACKGROUND_CONNECTED : REGION_BACKGROUND_DISCONNECTED);
        double size = targetNode.getWidth();
        // rotates the node:
        final double a = connectorTangent == null ? 0 : Geom.atan2(connectorTangent.getY(), connectorTangent.getX());
        targetNode.setRotate(a * 180 / PI);

        Point2D origin = t.transform(f.getNonNull(originKey).getConvertedValue());
        lineNode.setStartX(origin.getX());
        lineNode.setStartY(origin.getY());
        if (isConnected) {
            connectorLocation = view.worldToView(connector.getPositionInWorld(owner, target));
            connectorTangent = view.getWorldToView().deltaTransform(connector.getTangentInWorld(owner, target));
            if (connectorLocation != null) {
                targetNode.relocate(connectorLocation.getX() - size * 0.5, connectorLocation.getY() - size * 0.5);
                lineNode.setEndX(connectorLocation.getX());
                lineNode.setEndY(connectorLocation.getY());
            }
        } else {
            targetNode.relocate(p.getX() - size * 0.5, p.getY() - size * 0.5);
            lineNode.setEndX(p.getX());
            lineNode.setEndY(p.getY());
        }
    }

}

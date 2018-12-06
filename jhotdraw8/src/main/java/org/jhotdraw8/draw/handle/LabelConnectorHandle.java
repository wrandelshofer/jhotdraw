/* @(#)LineConnectorHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.ConnectingFigure;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import org.jhotdraw8.geom.Transforms;

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
public class LabelConnectorHandle extends AbstractConnectorHandle {

    @Nullable
    private static final Background REGION_BACKGROUND_CONNECTED = new Background(new BackgroundFill(Color.BLUE, null, null));
    @Nullable
    private static final Background REGION_BACKGROUND_DISCONNECTED = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Circle REGION_SHAPE = new Circle(4);

    @Nonnull
    private final Group groupNode;
    @Nonnull
    private final Region targetNode;
    @Nonnull
    private final Line lineNode;
    protected final MapAccessor<CssPoint2D> originKey;
    public LabelConnectorHandle(ConnectingFigure figure,  MapAccessor<CssPoint2D> originKey,MapAccessor<CssPoint2D> pointKey,
            MapAccessor<Connector> connectorKey, MapAccessor<Figure> targetKey) {
        this(figure, STYLECLASS_HANDLE_CONNECTION_POINT_DISCONNECTED, STYLECLASS_HANDLE_CONNECTION_POINT_CONNECTED,originKey, pointKey,
                connectorKey, targetKey);
    }

    public LabelConnectorHandle(ConnectingFigure figure, String styleclassDisconnected, String styleclassConnected,MapAccessor<CssPoint2D> originKey, MapAccessor<CssPoint2D> pointKey,
            MapAccessor<Connector> connectorKey, MapAccessor<Figure> targetKey) {
        super(figure, styleclassDisconnected, styleclassConnected, pointKey,
                connectorKey, targetKey);
        
        this.originKey=originKey;
        lineNode=new Line();
        targetNode = new Region();
        targetNode.setShape(REGION_SHAPE);
        targetNode.setManaged(false);
        targetNode.setScaleShape(false);
        targetNode.setCenterShape(true);
        targetNode.resize(10, 10);
        targetNode.getStyleClass().setAll(styleclassDisconnected, STYLECLASS_HANDLE);
        targetNode.setBorder(REGION_BORDER);
        
        lineNode.getStyleClass().add(styleclassConnected);
        groupNode=new Group();
        groupNode.getChildren().addAll(lineNode,targetNode);
    }

   

    @Nonnull
    @Override
    public Group getNode(DrawingView view) {
        return groupNode;
    }

   
    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Point2D p = f.getNonnull(pointKey).getConvertedValue();
        pickLocation = p = t == null ? p : t.transform(p);
        boolean isConnected = f.get(connectorKey) != null && f.get(targetKey) != null;
        targetNode.setBackground(isConnected ? REGION_BACKGROUND_CONNECTED : REGION_BACKGROUND_DISCONNECTED);
        targetNode.getStyleClass().set(0, isConnected ? styleclassConnected : styleclassDisconnected);
        targetNode.relocate(p.getX() - 5, p.getY() - 5);
        // rotates the node:
        targetNode.setRotate(f.getStyled(ROTATE));
        targetNode.setRotationAxis(f.getStyled(ROTATION_AXIS));

        if (isConnected) {
            connectorLocation = view.worldToView(f.get(connectorKey).getPositionInWorld(owner, f.get(targetKey)));
            targetNode.relocate(connectorLocation.getX() - 5, connectorLocation.getY() - 5);
            Point2D origin=t.transform(f.getNonnull(originKey).getConvertedValue());
            lineNode.setStartX(origin.getX());
            lineNode.setStartY(origin.getY());
            lineNode.setEndX(connectorLocation.getX());
            lineNode.setEndY(connectorLocation.getY());
            lineNode.setVisible(true);
        } else {
            connectorLocation = null;
            lineNode.setVisible(false);
        }
    }

}

/*
 * @(#)BezierNodeEditHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATE;
import static org.jhotdraw8.draw.figure.TransformableFigure.ROTATION_AXIS;
import static org.jhotdraw8.geom.BezierNode.C0C1C2_MASK;
import static org.jhotdraw8.geom.BezierNode.C0C1_MASK;
import static org.jhotdraw8.geom.BezierNode.C0C2_MASK;
import static org.jhotdraw8.geom.BezierNode.C0_MASK;

/**
 * Handle for the point ofCollection a figure.
 *
 * @author Werner Randelshofer
 */
public class BezierNodeEditHandle extends AbstractHandle {

    @Nullable
    private static final Background REGION_BACKGROUND = new Background(new BackgroundFill(Color.WHITE, null, null));
    @Nullable
    private static final Border REGION_BORDER = new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, null));
    private static final Circle REGION_SHAPE_CUBIC = new Circle(0, 0, 4);
    private static final Rectangle REGION_SHAPE_LINEAR = new Rectangle(7, 7);
    private static final Path REGION_SHAPE_QUADRATIC = new Path();


    static {
        final ObservableList<PathElement> elements = REGION_SHAPE_QUADRATIC.getElements();
        elements.add(new MoveTo(2, 0));
        elements.add(new LineTo(4, 0));
        elements.add(new LineTo(6, 2));
        elements.add(new LineTo(6, 4));
        elements.add(new LineTo(4, 6));
        elements.add(new LineTo(2, 6));
        elements.add(new LineTo(0, 4));
        elements.add(new LineTo(0, 2));
        elements.add(new ClosePath());
        elements.add(new MoveTo(3, 0));
        elements.add(new LineTo(3, 6));
    }

    @NonNull
    private final Region node;
    private Point2D pickLocation;
    private final int pointIndex;
    private final MapAccessor<ImmutableList<BezierNode>> pointKey;


    public BezierNodeEditHandle(Figure figure, MapAccessor<ImmutableList<BezierNode>> pointKey, int pointIndex) {
        super(figure);
        this.pointKey = pointKey;
        this.pointIndex = pointIndex;
        node = new Region();
        node.setShape(REGION_SHAPE_LINEAR);
        node.setManaged(false);
        node.setScaleShape(true);
        node.setCenterShape(true);
        node.resize(11, 11);
        node.setBorder(REGION_BORDER);
        node.setBackground(REGION_BACKGROUND);
    }

    @Override
    public boolean contains(DrawingView drawingView, double x, double y, double tolerance) {
        Point2D p = getLocationInView();
        return Geom.lengthSquared(x, y, p.getX(), p.getY()) <= tolerance;
    }

    private BezierNode getBezierNode() {
        ImmutableList<BezierNode> list = owner.get(pointKey);
        return list.get(pointIndex);

    }

    @Override
    public Cursor getCursor() {
        return Cursor.CROSSHAIR;
    }

    @NonNull
    private Point2D getLocation() {
        return getBezierNode().getC0();

    }

    public Point2D getLocationInView() {
        return pickLocation;
    }

    @NonNull
    @Override
    public Region getNode(@NonNull DrawingView view) {
        double size = view.getEditor().getHandleSize();
        if (node.getWidth() != size) {
            node.resize(size, size);
        }
        CssColor color = view.getEditor().getHandleColor();
        BorderStroke borderStroke = node.getBorder().getStrokes().get(0);
        if (!borderStroke.getTopStroke().equals(color.getColor())) {
            node.setBorder(new Border(
                    new BorderStroke(color.getColor(), BorderStrokeStyle.SOLID, null, null)
            ));
        }

        return node;
    }

    @Override
    public void onMouseClicked(@NonNull MouseEvent event, @NonNull DrawingView dv) {
        if (pointKey != null) {
            if (event.getClickCount() == 1) {
                if (event.isControlDown() || event.isAltDown()) {
                    BezierNodePath path = new BezierNodePath(owner.get(pointKey));
                    BezierNode node = path.getNodes().get(pointIndex);
                    switch (node.getMask()) {
                    case C0_MASK:
                        node = node.setMask(C0C1_MASK);
                        break;
                    case C0C1_MASK:
                            node = node.setMask(C0C2_MASK);
                            break;
                        case C0C2_MASK:
                            node = node.setMask(C0C1C2_MASK);
                            break;
                        case C0C1C2_MASK:
                        default:
                            node = node.setMask(C0_MASK);
                            break;
                        case BezierNode.MOVE_MASK:
                            break;
                    }
                    path.getNodes().set(pointIndex, node);
                    dv.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
                    dv.recreateHandles();
                }
            } else if (event.getClickCount() == 2) {
                if (!event.isControlDown() && !event.isMetaDown() && !event.isAltDown()) {
                    removePoint(dv);
                }
            }
        }
    }

    private void removePoint(@NonNull DrawingView dv) {
        if (owner.get(pointKey).size() > 2) {
            BezierNodePath path = new BezierNodePath(owner.get(pointKey));
            path.join(pointIndex, 1.0);
            dv.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
            dv.recreateHandles();
        }
    }

    @Override
    public void onMouseDragged(@NonNull MouseEvent event, @NonNull DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control switches the constrainer off
            newPoint = view.getConstrainer().constrainPoint(getOwner(), new CssPoint2D(newPoint)).getConvertedValue();
        }

        ImmutableList<BezierNode> list = owner.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode p = list.get(pointIndex);
        view.getModel().set(getOwner(), pointKey,
                ImmutableLists.set(list, pointIndex, p.setC0AndTranslateC1C2(getOwner().worldToLocal(newPoint))));
    }

    @Override
    public void onMousePressed(@NonNull MouseEvent event, @NonNull DrawingView view) {
        if (event.isPopupTrigger()) {
            onPopupTriggered(event, view);
        }
    }

    private void onPopupTriggered(@NonNull MouseEvent event, @NonNull DrawingView view) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addPoint = new MenuItem(DrawLabels.getResources().getString("handle.removePoint.text"));
        addPoint.setOnAction(actionEvent -> removePoint(view));
        contextMenu.getItems().add(addPoint);

        Menu tangents = new Menu(DrawLabels.getResources().getString("handle.bezierNode.tangents.text"));
        RadioMenuItem noneRadio = new RadioMenuItem(DrawLabels.getResources().getString("handle.bezierNode.noTangents.text"));
        RadioMenuItem inRadio = new RadioMenuItem(DrawLabels.getResources().getString("handle.bezierNode.incomingTangent.text"));
        RadioMenuItem outRadio = new RadioMenuItem(DrawLabels.getResources().getString("handle.bezierNode.outgoingTangent.text"));
        RadioMenuItem bothRadio = new RadioMenuItem(DrawLabels.getResources().getString("handle.bezierNode.incomingAndOutgoingTangent.text"));

        BezierNodePath path = new BezierNodePath(owner.get(pointKey));
        BezierNode bnode = path.getNodes().get(pointIndex);
        switch (bnode.getMask()) {
            case C0_MASK:
            default:
                noneRadio.setSelected(true);
                break;
            case C0C1_MASK:
                inRadio.setSelected(true);
                break;
            case C0C2_MASK:
                outRadio.setSelected(true);
                break;
            case C0C1C2_MASK:
                bothRadio.setSelected(true);
                break;
        }
        noneRadio.setOnAction(actionEvent -> {
            BezierNode changedNode = bnode.setMask(C0_MASK);
            path.getNodes().set(pointIndex, changedNode);
            view.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
            view.recreateHandles();
        });
        inRadio.setOnAction(actionEvent -> {
            BezierNode changedNode = bnode.setMask(C0C1_MASK);
            path.getNodes().set(pointIndex, changedNode);
            view.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
            view.recreateHandles();
        });
        outRadio.setOnAction(actionEvent -> {
            BezierNode changedNode = bnode.setMask(C0C2_MASK);
            path.getNodes().set(pointIndex, changedNode);
            view.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
            view.recreateHandles();
        });
        bothRadio.setOnAction(actionEvent -> {
            BezierNode changedNode = bnode.setMask(C0C1C2_MASK);
            path.getNodes().set(pointIndex, changedNode);
            view.getModel().set(owner, pointKey, ImmutableLists.ofCollection(path.getNodes()));
            view.recreateHandles();
        });

        tangents.getItems().addAll(noneRadio, inRadio, outRadio, bothRadio);
        contextMenu.getItems().add(tangents);
        contextMenu.show(node, event.getScreenX(), event.getScreenY());
        event.consume();
    }

    @Override
    public void onMouseReleased(MouseEvent event, DrawingView view) {
        if (event.isPopupTrigger()) {
            onPopupTriggered(event, view);
        }
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        ImmutableList<BezierNode> list = f.get(pointKey);
        if (pointIndex >= list.size()) {
            return;
        }
        BezierNode p = getBezierNode();
        Point2D c0 = getLocation();
        pickLocation = c0 = t == null ? c0 : t.transform(c0);
        double size = node.getWidth();
        node.relocate(c0.getX() - size * 0.5, c0.getY() - size * 0.5);
        // rotates the node:
        node.setRotate(f.getStyledNonNull(ROTATE));
        node.setRotationAxis(f.getStyled(ROTATION_AXIS));

        BezierNode bn = getBezierNode();
        if (bn.isC1() && bn.isC2()) {
            node.setShape(REGION_SHAPE_CUBIC);// FIXME this is not correct
        } else if (bn.isC1() || bn.isC2()) {
            node.setShape(REGION_SHAPE_QUADRATIC);// FIXME this is not correct
        } else {
            node.setShape(REGION_SHAPE_LINEAR);
        }
    }

}

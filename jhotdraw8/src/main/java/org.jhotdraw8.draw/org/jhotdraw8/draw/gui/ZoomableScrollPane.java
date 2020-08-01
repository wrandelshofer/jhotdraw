
package org.jhotdraw8.draw.gui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.geom.Geom;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * A ScrollPane that also supports zooming.
 * <p>
 * The ZoomScrollPane can zoom and scroll its content.
 * <p>
 * It also supports a background and a foreground that
 * scroll with the content, but that do not zoom on their own.
 * <p>
 * You can not set the background, foreground and content objects,
 * you can only access their children list.
 * <p>
 * The ZoomScrollPane has the following internal structure:
 * <pre>
 *     ScrollableSubScenePanel
 *     .verticalScrollBar
 *     .horizontalScrollBar
 *     .viewport
 *     ..background
 *     ..subScene
 *     ......content
 *     ..foreground
 * </pre>
 */
@NonNull
public class ZoomableScrollPane {
    private final DoubleProperty scaleFactor = new SimpleDoubleProperty(this, "scaleFactor", 1.0);
    private final DoubleProperty viewRectX = new SimpleDoubleProperty(this, "viewX", 0);
    private final DoubleProperty viewRectY = new SimpleDoubleProperty(this, "viewY", 0);

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="horizontalScrollBar"
    private ScrollBar horizontalScrollBar; // Value injected by FXMLLoader

    @FXML // fx:id="verticalScrollBar"
    private ScrollBar verticalScrollBar; // Value injected by FXMLLoader

    @FXML // fx:id="backgroundPane"
    private StackPane background; // Value injected by FXMLLoader

    @FXML // fx:id="subScene"
    private SubScene subScene; // Value injected by FXMLLoader


    private StackPane content;
    @FXML // fx:id="foregroundPane"
    private StackPane foreground; // Value injected by FXMLLoader

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert horizontalScrollBar != null : "fx:id=\"horizontalScrollBar\" was not injected: check your FXML file 'ZoomableScrollPane.fxml'.";
        assert verticalScrollBar != null : "fx:id=\"verticalScrollBar\" was not injected: check your FXML file 'ZoomableScrollPane.fxml'.";
        assert background != null : "fx:id=\"backgroundPane\" was not injected: check your FXML file 'ZoomableScrollPane.fxml'.";
        assert subScene != null : "fx:id=\"subScene\" was not injected: check your FXML file 'ZoomableScrollPane.fxml'.";
        assert foreground != null : "fx:id=\"foregroundPane\" was not injected: check your FXML file 'ZoomableScrollPane.fxml'.";
        assert viewportPane != null : "fx:id=\"viewportPane\" was not injected: check your FXML file 'ZoomableScrollPane.fxml'.";

        // Initialize the layout
        // ---------------------
        initLayout();

        // Initialize the bindings
        // -----------------------
        initBindings();

        // Initialize the behavior
        // ------------------------
        initBehavior();
    }

    private void initBehavior() {
        // - Scroll in nize chunks
        horizontalScrollBar.setUnitIncrement(20);
        verticalScrollBar.setUnitIncrement(20);

        // - Try to keep the center of the viewRect fixed when zooming.
        scaleFactor.addListener((o, oldv, newv) -> {
            double oldvv = oldv.doubleValue(),
                    newvv = newv.doubleValue(),
                    sf = oldvv / newvv,
                    hmin = horizontalScrollBar.getMin(),
                    hmax = horizontalScrollBar.getMax(),
                    hvalue = horizontalScrollBar.getValue(),
                    hvisible = horizontalScrollBar.getVisibleAmount(),
                    holdmax = hmax * sf,
                    holdmin = hmin * sf,
                    vmin = verticalScrollBar.getMin(),
                    vmax = verticalScrollBar.getMax(),
                    vvalue = verticalScrollBar.getValue(),
                    vvisible = verticalScrollBar.getVisibleAmount(),
                    voldmax = vmax * sf,
                    voldmin = vmin * sf;

            double osf = 1 / oldvv;
            double oldx = ((holdmax - hvisible) * (hvalue - holdmin) / (holdmax - holdmin)) * osf;
            double oldy = ((voldmax - vvisible) * (vvalue - voldmin) / (voldmax - voldmin)) * osf;
            double oldw = hvisible * osf;
            double oldh = vvisible * osf;
            scrollContentRectToVisible(oldx, oldy, oldw, oldh);
        });

        // - Scroll on scroll event.
        viewportPane.addEventHandler(ScrollEvent.SCROLL, event -> {
            onScrollEvent(event, horizontalScrollBar, event.getDeltaX(), viewportPane.getWidth());
            onScrollEvent(event, verticalScrollBar, event.getDeltaY(), viewportPane.getHeight());
        });

        // FIXME Zoom on zoom event.
        viewportPane.addEventHandler(ZoomEvent.ZOOM, event -> {
            // System.out.println("zoomEvent detected yay");
        });

    }

    private void initBindings() {
        // - Translate the viewRect and the background + foreground panes when
        //   the scrollbars are moved.
        DoubleBinding translateXBinding = createTranslateBinding(horizontalScrollBar);
        DoubleBinding translateYBinding = createTranslateBinding(verticalScrollBar);
        viewRectX.bind(translateXBinding.negate());
        viewRectY.bind(translateYBinding.negate());
        background.translateXProperty().bind(translateXBinding);
        background.translateYProperty().bind(translateYBinding);
        foreground.translateXProperty().bind(translateXBinding);
        foreground.translateYProperty().bind(translateYBinding);

        // - Adjust the size of the sub-scene when the viewport is resized.
        subScene.widthProperty().bind(viewportWidthProperty());
        subScene.heightProperty().bind(viewportHeightProperty());

        // - Translate the subScenePane when the scrollbars are moved,
        //   and scale the subScenePane when the zoomFactor is changed.
        Scale scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        scaleFactor.addListener((o, oldv, newv) -> {
            scale.setX(newv.doubleValue());
            scale.setY(newv.doubleValue());
        });
        Translate translate = new Translate();
        translateXBinding.addListener((o, oldv, newv) -> {
            translate.setX(newv.doubleValue());
        });
        translateYBinding.addListener((o, oldv, newv) -> {
            translate.setY(newv.doubleValue());
        });
        content.getTransforms().addAll(translate, scale);

        // - Adjust the scrollbar max, when the subScene is resized.
        horizontalScrollBar.maxProperty().bind(contentWidthProperty().multiply(scaleFactor));
        verticalScrollBar.maxProperty().bind(contentHeightProperty().multiply(scaleFactor));

        // - Adjust the scrollbar visibleAmount whe the viewport is resized.
        horizontalScrollBar.visibleAmountProperty().bind(viewportWidthProperty());
        verticalScrollBar.visibleAmountProperty().bind(viewportHeightProperty());
        horizontalScrollBar.blockIncrementProperty().bind(viewportWidthProperty());
        verticalScrollBar.blockIncrementProperty().bind(viewportHeightProperty());
    }

    private void initLayout() {
        // - Create the sub-scene pane.
        content = new StackPane();
        content.setManaged(false);
        subScene.setRoot(content);
        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(viewportWidthProperty());
        clipRect.heightProperty().bind(viewportHeightProperty());
        viewportPane.setClip(clipRect);

        // - Make all panes transparent.
        viewportPane.setBackground(null);
        background.setBackground(null);
        foreground.setBackground(null);
        content.setBackground(null);
    }

    private void onScrollEvent(ScrollEvent event, ScrollBar scrollBar, double viewDelta, double viewExtent) {
        final double min = scrollBar.getMin();
        final double max = scrollBar.getMax();
        final double value = scrollBar.getValue();
        final double visible = scrollBar.getVisibleAmount();

        final double extent = max - min - visible;
        final double delta = viewDelta * extent / viewExtent / getScaleFactor();
        scrollBar.setValue(Geom.clamp(value - delta, min, max));
        event.consume();
    }

    @NonNull
    private static DoubleBinding createTranslateBinding(ScrollBar scrollBar) {
        return CustomBinding.computeDouble(
                () -> {
                    final double
                            min = scrollBar.getMin(),
                            max = scrollBar.getMax(),
                            value = scrollBar.getValue(),
                            visible = scrollBar.getVisibleAmount();
                    if (visible > max) {
                        return Math.round((visible - max) * 0.5);
                    }
                    return -Geom.clamp(Math.round((max - visible) * (value - min) / (max - min)), min, max);
                },
                scrollBar.valueProperty(),
                scrollBar.minProperty(),
                scrollBar.maxProperty(),
                scrollBar.visibleAmountProperty());
    }

    @NonNull
    public static URL getFxmlResource() {
        return ZoomableScrollPane.class.getResource("/org/jhotdraw8/draw/gui/ZoomableScrollPane.fxml");
    }

    @NonNull
    public final ReadOnlyDoubleProperty viewportWidthProperty() {
        return viewportPane.widthProperty();
    }

    @NonNull
    public final ReadOnlyDoubleProperty viewportHeightProperty() {
        return viewportPane.heightProperty();
    }


    @NonNull
    public final DoubleProperty scaleFactorProperty() {
        return scaleFactor;
    }

    @NonNull
    public double getScaleFactor() {
        return scaleFactor.get();
    }


    @NonNull
    public final ReadOnlyDoubleProperty viewWidthProperty() {
        return horizontalScrollBar.maxProperty();
    }

    @NonNull
    public final ReadOnlyDoubleProperty viewHeightProperty() {
        return verticalScrollBar.maxProperty();
    }

    @NonNull
    public final ReadOnlyDoubleProperty viewRectXProperty() {
        return viewRectX;
    }

    @NonNull
    public final ReadOnlyDoubleProperty viewRectYProperty() {
        return viewRectY;
    }

    @NonNull
    public double getViewportWidth() {
        return viewportWidthProperty().getValue();
    }

    @NonNull
    public double getViewportHeight() {
        return viewportHeightProperty().getValue();
    }

    @NonNull
    public double getViewRectWidth() {
        return horizontalScrollBar.visibleAmountProperty().getValue();
    }

    @NonNull
    public double getViewRectHeight() {
        return verticalScrollBar.visibleAmountProperty().getValue();
    }

    @NonNull
    public ObservableList<Node> getContentChildren() {
        return content.getChildren();
    }

    @NonNull
    public ObservableList<Node> getBackgroundChildren() {
        return background.getChildren();
    }

    @NonNull
    public ObservableList<Node> getForegroundChildren() {
        return foreground.getChildren();
    }

    @NonNull
    public Bounds getViewRect() {
        return new BoundingBox(getViewRectX(), getViewRectY(), getViewRectWidth(), getViewRectHeight());
    }

    @NonNull
    public Bounds getVisibleContentRect() {
        double sf = 1 / getScaleFactor();
        return new BoundingBox(getViewRectX() * sf, getViewRectY() * sf, getViewRectWidth() * sf, getViewRectHeight() * sf);
    }

    @NonNull
    public double getViewRectX() {
        return viewRectX.get();
    }

    private double getViewRectY() {
        return viewRectY.get();
    }


    @FXML // fx:id="viewportPane"
    private StackPane viewportPane; // Value injected by FXMLLoader

    public void setScaleFactor(double newValue) {
        scaleFactor.set(newValue);
    }

    @NonNull
    public final Bounds getViewportBounds() {
        return viewportPane.getLayoutBounds();
    }


    public void scrollViewRectToVisible(@NonNull Bounds b) {
        scrollViewRectToVisible(b.getMinX(), b.getMinY(),
                b.getWidth(), b.getHeight());
    }

    public void scrollViewRectToVisible(double x, double y, double w, double h) {
        final double
                hmin = horizontalScrollBar.getMin(),
                hmax = horizontalScrollBar.getMax(),
                hvisible = horizontalScrollBar.getVisibleAmount(),
                vmin = verticalScrollBar.getMin(),
                vmax = verticalScrollBar.getMax(),
                vvisible = verticalScrollBar.getVisibleAmount();

        final double
                cx = x + (w - hvisible) * 0.5,
                cy = y + (h - vvisible) * 0.5,
                hvalue = cx * (hmax - hmin) / (hmax - hvisible) + hmin,
                vvalue = cy * (vmax - vmin) / (vmax - vvisible) + vmin;

        horizontalScrollBar.setValue(hvalue);
        verticalScrollBar.setValue(vvalue);

    }

    public void scrollContentRectToVisible(double x, double y, double w, double h) {
        double sf = getScaleFactor();
        scrollViewRectToVisible(x * sf, y * sf, w * sf, h * sf);
    }


    public void scrollContentRectToVisible(@NonNull Bounds boundsInWorld) {
        scrollContentRectToVisible(boundsInWorld.getMinX(), boundsInWorld.getMinY(),
                boundsInWorld.getWidth(), boundsInWorld.getHeight());
    }

    @NonNull
    public Transform getContentToView() {
        double sf = getScaleFactor();
        return new Affine(sf, 0, 0, 0, sf, 0);
    }

    private Property<Transform> worldToView;

    @NonNull
    public ReadOnlyProperty<Transform> contentToViewProperty() {
        if (worldToView == null) {
            worldToView = new SimpleObjectProperty<>(this, "contentToView");
            worldToView.bind(CustomBinding.compute(this::getContentToView,
                    scaleFactor));
        }
        return worldToView;
    }

    @NonNull
    public DoubleProperty contentWidthProperty() {
        return content.prefWidthProperty();
    }

    @NonNull
    public DoubleProperty contentHeightProperty() {
        return content.prefHeightProperty();
    }

    public void setContentSize(double w, double h) {
        setContentWidth(w);
        setContentHeight(w);
    }

    private void setContentWidth(double w) {
        contentWidthProperty().set(w);
    }

    private void setContentHeight(double w) {
        contentHeightProperty().set(w);
    }

    private double getContentWidth() {
        return contentWidthProperty().get();
    }

    private double getContentHeight() {
        return contentHeightProperty().get();
    }
}


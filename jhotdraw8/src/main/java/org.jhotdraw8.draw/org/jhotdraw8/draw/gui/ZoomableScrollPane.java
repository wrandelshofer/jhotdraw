
package org.jhotdraw8.draw.gui;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Geom;

import java.io.IOException;
import java.io.UncheckedIOException;
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
 *     ...content
 *     ..foreground
 * </pre>
 */
@NonNull
public class ZoomableScrollPane {
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, "scaleFactor", 1.0);
    private final ObjectProperty<Bounds> visibleContentRect = new SimpleObjectProperty<>(this, "contentRect");
    private final ObjectProperty<Insets> insets = new SimpleObjectProperty<>(this, "insets", new Insets(24, 24, 24, 24));

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
    @FXML // fx:id="gridPane"
    private GridPane gridPane; // Value injected by FXMLLoader

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
        assert gridPane != null : "fx:id=\"gridPane\" was not injected: check your FXML file 'ZoomableScrollPane.fxml'.";

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
        zoomFactor.addListener(this::onZoomFactorChanged);

        // - Scroll on scroll event.
        viewportPane.addEventHandler(ScrollEvent.SCROLL, event -> {
            onScrollEvent(event, horizontalScrollBar, event.getDeltaX());
            onScrollEvent(event, verticalScrollBar, event.getDeltaY());
        });

        // FIXME Zoom on zoom event.
        viewportPane.addEventHandler(ZoomEvent.ZOOM, event -> {
            // System.out.println("zoomEvent detected yay");
        });

    }


    private void onZoomFactorChanged(@NonNull Observable o, @NonNull Number oldv, @NonNull Number newv) {
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
    }

    public ReadOnlyObjectProperty<Bounds> viewportRectProperty() {
        return viewportPane.boundsInParentProperty();
    }

    private void initBindings() {
        // - Translate the viewRect and the background + foreground panes when
        //   the scrollbars are moved.
        DoubleBinding contentTranslateXBinding = createContentRectTranslateBinding(horizontalScrollBar);
        DoubleBinding contentTranslateYBinding = createContentRectTranslateBinding(verticalScrollBar);

        visibleContentRect.bind(CustomBinding.compute(() -> {
                    double invf = 1 / zoomFactor.get();
                    return new BoundingBox(
                            contentTranslateXBinding.get() * invf,
                            contentTranslateYBinding.get() * invf,
                            horizontalScrollBar.getVisibleAmount() * invf,
                            verticalScrollBar.getVisibleAmount() * invf
                    );
                },
                contentTranslateXBinding,
                contentTranslateYBinding,
                horizontalScrollBar.visibleAmountProperty(),
                verticalScrollBar.visibleAmountProperty(),
                zoomFactor));

        // - Adjust the size of the sub-scene when the viewport is resized.
        subScene.widthProperty().bind(viewportWidthProperty());
        subScene.heightProperty().bind(viewportHeightProperty());

        // - Translate the subScenePane when the scrollbars are moved,
        //   and scale the subScenePane when the zoomFactor is changed.
        Scale scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        zoomFactor.addListener((o, oldv, newv) -> {
            scale.setX(newv.doubleValue());
            scale.setY(newv.doubleValue());
        });
        Translate translate = new Translate();
        translate.xProperty().bind(contentTranslateXBinding.negate());
        translate.yProperty().bind(contentTranslateYBinding.negate());
        content.getTransforms().addAll(translate, scale);

        // - Adjust the scrollbar max, when the subScene is resized.
        horizontalScrollBar.maxProperty().bind(
                CustomBinding.compute(() -> getContentWidth() * getZoomFactor() + getInsets().getRight(), contentWidthProperty(), zoomFactor, insets));
        horizontalScrollBar.minProperty().bind(
                CustomBinding.compute(() -> -getInsets().getLeft(), insets));
        verticalScrollBar.maxProperty().bind(
                CustomBinding.compute(() -> getContentHeight() * getZoomFactor() + getInsets().getBottom(), contentHeightProperty(), zoomFactor, insets));
        verticalScrollBar.minProperty().bind(
                CustomBinding.compute(() -> -getInsets().getTop(), insets));

        // - Adjust the scrollbar visibleAmount whe the viewport is resized.
        horizontalScrollBar.visibleAmountProperty().bind(viewportWidthProperty());
        verticalScrollBar.visibleAmountProperty().bind(viewportHeightProperty());
        horizontalScrollBar.blockIncrementProperty().bind(viewportWidthProperty());
        verticalScrollBar.blockIncrementProperty().bind(viewportHeightProperty());

        // - Only show the scrollbars if their visible amount is less than their
        //   extent (we can use the max value here, because we let min=0).
        onlyShowScrollBarIfNeeded(horizontalScrollBar, gridPane.getRowConstraints().get(1));
        onlyShowScrollBarIfNeeded(verticalScrollBar, gridPane.getColumnConstraints().get(1));


        contentToView.bind(CustomBinding.compute(() -> {
                    double sf = getZoomFactor();
                    Bounds vcRect = getVisibleContentRect();
                    double x, y;
                    x = vcRect.getMinX();
                    y = vcRect.getMinY();
                    return new Affine(
                            sf, 0, -x * sf,
                            0, sf, -y * sf);
                },
                visibleContentRect
        ));

    }

    private void onlyShowScrollBarIfNeeded(ScrollBar scrollBar, RowConstraints rowConstraints) {
        BooleanBinding visibilityBinding;
        visibilityBinding = scrollBar.visibleAmountProperty().lessThan(scrollBar.maxProperty());
        scrollBar.visibleProperty().bind(visibilityBinding);
        rowConstraints.prefHeightProperty().bind(
                CustomBinding.convert(visibilityBinding, b -> b ? ScrollBar.USE_COMPUTED_SIZE : 0));
    }

    private void onlyShowScrollBarIfNeeded(ScrollBar scrollBar, ColumnConstraints colConstraints) {
        BooleanBinding visibilityBinding;
        visibilityBinding = scrollBar.visibleAmountProperty().lessThan(scrollBar.maxProperty());
        scrollBar.visibleProperty().bind(visibilityBinding);
        colConstraints.prefWidthProperty().bind(
                CustomBinding.convert(visibilityBinding, b -> b ? ScrollBar.USE_COMPUTED_SIZE : 0));
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

    private void onScrollEvent(ScrollEvent event, ScrollBar scrollBar, double delta) {
        double
                min = scrollBar.getMin(),
                max = scrollBar.getMax(),
                value = scrollBar.getValue(),
                visible = scrollBar.getVisibleAmount();

        // we only consume if we can scroll
        if (visible < max - min) {
            scrollBar.setValue(Geom.clamp(value - delta, min, max));
            event.consume();
        }
    }

    @NonNull
    private static DoubleBinding createContentRectTranslateBinding(ScrollBar scrollBar) {
        return CustomBinding.computeDouble(
                () -> getScrollBarPosition(scrollBar),
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
    public final DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public double getZoomFactor() {
        return zoomFactor.get();
    }


    @NonNull
    public final ReadOnlyDoubleProperty viewWidthProperty() {
        return horizontalScrollBar.maxProperty();
    }

    @NonNull
    public final ReadOnlyDoubleProperty viewHeightProperty() {
        return verticalScrollBar.maxProperty();
    }

    public double getViewportWidth() {
        return viewportWidthProperty().getValue();
    }

    public double getViewportHeight() {
        return viewportHeightProperty().getValue();
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


    /**
     * Returns the rectangle of the content which is currently visible in the
     * viewport in content coordinates.
     *
     * @return visible content rectangle in content coordinates
     */
    @NonNull
    public Bounds getVisibleContentRect() {
        return visibleContentRect.get();
    }

    @NonNull
    public Bounds getViewRect() {
        return getContentToView().transform(getVisibleContentRect());
    }

    /**
     * Gets the position of the scrollbar.
     *
     * @param sb a scrollbar
     * @return the position of the scrollbar
     */
    private static double getScrollBarPosition(ScrollBar sb) {
        double value = sb.getValue(),
                min = sb.getMin(),
                max = sb.getMax(),
                visible = sb.getVisibleAmount();

        if (visible > max) {
            return -Math.round((visible - max) * 0.5);
        }
        return Geom.clamp(Math.round((max - min - visible) * (value - min) / (max - min)) + min, min, max);
    }

    @NonNull
    public Bounds getViewportRect() {
        return viewportPane.getBoundsInParent();
    }

    public ReadOnlyObjectProperty<Bounds> visibleContentRectProperty() {
        return visibleContentRect;
    }


    @FXML // fx:id="viewportPane"
    private StackPane viewportPane; // Value injected by FXMLLoader

    public void setZoomFactor(double newValue) {
        zoomFactor.set(newValue);
    }

    @NonNull
    public final Bounds getViewportBounds() {
        return viewportPane.getLayoutBounds();
    }


    public void scrollViewRectToVisible(@NonNull Bounds b) {
        scrollContentRectToVisible(getViewToContent().transform(b));
    }

    public void scrollViewRectToVisible(double x, double y, double w, double h) {
        scrollViewRectToVisible(new BoundingBox(x, y, w, h));
    }

    public void scrollContentRectToVisible(double x, double y, double w, double h) {
        double sf = getZoomFactor();
        final double
                hmin = horizontalScrollBar.getMin(),
                hmax = horizontalScrollBar.getMax(),
                hvisible = horizontalScrollBar.getVisibleAmount(),
                vmin = verticalScrollBar.getMin(),
                vmax = verticalScrollBar.getMax(),
                vvisible = verticalScrollBar.getVisibleAmount();

        final double
                cx = x * sf + (w * sf - hvisible) * 0.5,
                cy = y * sf + (h * sf - vvisible) * 0.5,
                hvalue = cx * (hmax - hmin) / (hmax - hvisible) + hmin,
                vvalue = cy * (vmax - vmin) / (vmax - vvisible) + vmin;

        horizontalScrollBar.setValue(Geom.clamp(hvalue, horizontalScrollBar.getMin(), horizontalScrollBar.getMax()));
        verticalScrollBar.setValue(Geom.clamp(vvalue, verticalScrollBar.getMin(), verticalScrollBar.getMax()));

    }


    public void scrollContentRectToVisible(@NonNull Bounds boundsInWorld) {
        scrollContentRectToVisible(boundsInWorld.getMinX(), boundsInWorld.getMinY(),
                boundsInWorld.getWidth(), boundsInWorld.getHeight());
    }

    @NonNull
    public Transform getContentToView() {
        return contentToViewProperty().getValue();
    }

    @NonNull
    public Transform getViewToContent() {
        try {
            return getContentToView().createInverse();
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return FXTransforms.IDENTITY;
        }
        /*
        double sf = 1 / getZoomFactor();
        Bounds vcRect = getVisibleContentRect();
        double x, y;
        x = vcRect.getMinX();
        y = vcRect.getMinY();
        return new Affine(
                sf, 0, x,
                0, sf, y);

         */
    }

    private final Property<Transform> contentToView = new SimpleObjectProperty<>(this, "contentToView");



    @NonNull
    public ReadOnlyProperty<Transform> contentToViewProperty() {
        return contentToView;
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
        setContentHeight(h);
    }

    public void setContentWidth(double w) {
        contentWidthProperty().set(w);
    }

    public void setContentHeight(double w) {
        contentHeightProperty().set(w);
    }

    public double getContentWidth() {
        return contentWidthProperty().get();
    }

    public double getContentHeight() {
        return contentHeightProperty().get();
    }

    public String getSubSceneUserAgentStylesheet() {
        return subScene.getUserAgentStylesheet();
    }

    public void setSubSceneUserAgentStylesheet(String newValue) {
        subScene.setUserAgentStylesheet(newValue);
    }

    public ObjectProperty<String> subSceneUserAgentStylesheetProperty() {
        return subScene.userAgentStylesheetProperty();
    }

    public Node getNode() {
        return gridPane;
    }

    public static ZoomableScrollPane create() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ZoomableScrollPane.getFxmlResource());
        loader.setResources(null);
        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return loader.getController();
    }

    public ReadOnlyDoubleProperty viewRectWidthProperty() {
        return horizontalScrollBar.visibleAmountProperty();
    }

    public ReadOnlyDoubleProperty viewRectHeightProperty() {
        return verticalScrollBar.visibleAmountProperty();
    }

    public Insets getInsets() {
        return insets.get();
    }

    public ObjectProperty<Insets> insetsProperty() {
        return insets;
    }

    public void setInsets(Insets insets) {
        this.insets.set(insets);
    }
}


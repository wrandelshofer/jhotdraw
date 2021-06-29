
/*
 * @(#)ZoomableScrollPane.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.gui;

import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
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
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Geom;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * The ZoomScrollPane has the following scene structure:
 * <ul>
 *   <li>{@value #ZOOMABLE_SCROLL_PANE_STYLE_CLASS} – {@link GridPane}<ul>
 *     <li>"scroll-bar:vertical" – {@link ScrollBar}</li>
 *     <li>"scroll-bar:horizontal" – {@link ScrollBar}</li>
 *     <li>{@value #ZOOMABLE_SCROLL_PANE_VIEWPORT_STYLE_CLASS} – {@link StackPane}<ul>
 *         <li>{@value #ZOOMABLE_SCROLL_PANE_BACKGROUND_STYLE_CLASS} – {@link StackPane}</li>
 *         <li>{@value #ZOOMABLE_SCROLL_PANE_SUBSCENE_STYLE_CLASS} – {@link SubScene}<ul>
 *            <li>content</li>
 *         </ul></li>
 *         <li>{@value #ZOOMABLE_SCROLL_PANE_FOREGROUND_STYLE_CLASS} – {@link StackPane}</li>
 *     </ul></li>
 *   </ul></li>
 * </ul>
 */
public @NonNull class ZoomableScrollPane extends GridPane {
    /**
     * The style class of the ZoomableScrollPane is {@value #ZOOMABLE_SCROLL_PANE_STYLE_CLASS}.
     */
    public static final String ZOOMABLE_SCROLL_PANE_STYLE_CLASS = "jhotdraw8-zoomable-scroll-pane";
    /**
     * The style class of the ZoomableScrollPane is {@value #ZOOMABLE_SCROLL_PANE_VIEWPORT_STYLE_CLASS}.
     */
    public static final String ZOOMABLE_SCROLL_PANE_VIEWPORT_STYLE_CLASS = "jhotdraw8-zoomable-scroll-pane-viewpprt";
    /**
     * The style class of the ZoomableScrollPane is {@value #ZOOMABLE_SCROLL_PANE_BACKGROUND_STYLE_CLASS}.
     */
    public static final String ZOOMABLE_SCROLL_PANE_BACKGROUND_STYLE_CLASS = "jhotdraw8-zoomable-scroll-pane-background";
    /**
     * The style class of the ZoomableScrollPane is {@value #ZOOMABLE_SCROLL_PANE_SUBSCENE_STYLE_CLASS}.
     */
    public static final String ZOOMABLE_SCROLL_PANE_SUBSCENE_STYLE_CLASS = "jhotdraw8-zoomable-scroll-subscene-background";
    /**
     * The style class of the ZoomableScrollPane is {@value #ZOOMABLE_SCROLL_PANE_FOREGROUND_STYLE_CLASS}.
     */
    public static final String ZOOMABLE_SCROLL_PANE_FOREGROUND_STYLE_CLASS = "jhotdraw8-zoomable-scroll-pane-foreground";
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, "scaleFactor", 1.0);
    private final ObjectProperty<Bounds> visibleContentRect = new SimpleObjectProperty<>(this, "contentRect");

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

        // Initialize style classes
        // ------------------------
        initStyle();
        initLayout();
        initBindings();
        initBehavior();
    }

    private void initStyle() {
        this.getStyleClass().add(ZOOMABLE_SCROLL_PANE_STYLE_CLASS);
        viewportPane.getStyleClass().add(ZOOMABLE_SCROLL_PANE_VIEWPORT_STYLE_CLASS);
        background.getStyleClass().add(ZOOMABLE_SCROLL_PANE_BACKGROUND_STYLE_CLASS);
        foreground.getStyleClass().add(ZOOMABLE_SCROLL_PANE_FOREGROUND_STYLE_CLASS);
        subScene.getStyleClass().add(ZOOMABLE_SCROLL_PANE_SUBSCENE_STYLE_CLASS);
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
                CustomBinding.compute(() -> getContentWidth() * getZoomFactor() + getInsets().getRight(), contentWidthProperty(), zoomFactor, insetsProperty()));
        horizontalScrollBar.minProperty().bind(
                CustomBinding.compute(() -> -getInsets().getLeft(), insetsProperty()));
        verticalScrollBar.maxProperty().bind(
                CustomBinding.compute(() -> getContentHeight() * getZoomFactor() + getInsets().getBottom(), contentHeightProperty(), zoomFactor, insetsProperty()));
        verticalScrollBar.minProperty().bind(
                CustomBinding.compute(() -> -getInsets().getTop(), insetsProperty()));

        // - Adjust the scrollbar visibleAmount whe the viewport is resized.
        horizontalScrollBar.visibleAmountProperty().bind(viewportWidthProperty());
        verticalScrollBar.visibleAmountProperty().bind(viewportHeightProperty());
        horizontalScrollBar.blockIncrementProperty().bind(viewportWidthProperty());
        verticalScrollBar.blockIncrementProperty().bind(viewportHeightProperty());

        // - Only show the scrollbars if their visible amount is less than their
        //   extent (we can use the max value here, because we let min=0).
        onlyShowScrollBarIfNeeded(horizontalScrollBar, this.getRowConstraints().get(1), hbarPolicyProperty());
        onlyShowScrollBarIfNeeded(verticalScrollBar, this.getColumnConstraints().get(1), vbarPolicyProperty());


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

    private static class StyleableProperties {
        @SuppressWarnings("uncheckded")
        private static final CssMetaData<ZoomableScrollPane, ScrollPane.ScrollBarPolicy> HBAR_POLICY =
                new CssMetaData<ZoomableScrollPane, ScrollPane.ScrollBarPolicy>("-fx-hbar-policy",
                        (StyleConverter<?, ScrollPane.ScrollBarPolicy>) StyleConverter.<ScrollPane.ScrollBarPolicy>getEnumConverter(ScrollPane.ScrollBarPolicy.class),
                        ScrollPane.ScrollBarPolicy.AS_NEEDED) {

                    @Override
                    public boolean isSettable(ZoomableScrollPane n) {
                        return n.hbarPolicy == null || !n.hbarPolicy.isBound();
                    }

                    @Override
                    public StyleableProperty<ScrollPane.ScrollBarPolicy> getStyleableProperty(ZoomableScrollPane n) {
                        return (StyleableProperty<ScrollPane.ScrollBarPolicy>) (WritableValue<ScrollPane.ScrollBarPolicy>) n.hbarPolicyProperty();
                    }
                };

        @SuppressWarnings("uncheckded")
        private static final CssMetaData<ZoomableScrollPane, ScrollPane.ScrollBarPolicy> VBAR_POLICY =
                new CssMetaData<ZoomableScrollPane, ScrollPane.ScrollBarPolicy>("-fx-vbar-policy",
                        (StyleConverter<?, ScrollPane.ScrollBarPolicy>) StyleConverter.<ScrollPane.ScrollBarPolicy>getEnumConverter(ScrollPane.ScrollBarPolicy.class),
                        ScrollPane.ScrollBarPolicy.AS_NEEDED) {

                    @Override
                    public boolean isSettable(ZoomableScrollPane n) {
                        return n.vbarPolicy == null || !n.vbarPolicy.isBound();
                    }

                    @Override
                    public StyleableProperty<ScrollPane.ScrollBarPolicy> getStyleableProperty(ZoomableScrollPane n) {
                        return (StyleableProperty<ScrollPane.ScrollBarPolicy>) (WritableValue<ScrollPane.ScrollBarPolicy>) n.vbarPolicyProperty();
                    }
                };

        private static final CssMetaData<ZoomableScrollPane, Boolean> PANNABLE =
                new CssMetaData<ZoomableScrollPane, Boolean>("-fx-pannable",
                        StyleConverter.getBooleanConverter(), Boolean.FALSE) {

                    @Override
                    public boolean isSettable(ZoomableScrollPane n) {
                        return n.pannable == null || !n.pannable.isBound();
                    }

                    @Override
                    public StyleableProperty<Boolean> getStyleableProperty(ZoomableScrollPane n) {
                        return (StyleableProperty<Boolean>) (WritableValue<Boolean>) n.pannableProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
            styleables.add(HBAR_POLICY);
            styleables.add(VBAR_POLICY);
            styleables.add(PANNABLE);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    private void onlyShowScrollBarIfNeeded(@NonNull ScrollBar scrollBar, @NonNull RowConstraints rowConstraints, @NonNull ObjectProperty<ScrollPane.ScrollBarPolicy> scrollBarPolicy) {
        BooleanBinding visibilityBinding;
        visibilityBinding =
                scrollBarPolicy.isEqualTo(ScrollPane.ScrollBarPolicy.NEVER).not()
                        .and(scrollBarPolicy.isEqualTo(ScrollPane.ScrollBarPolicy.ALWAYS)
                                .or(scrollBar.visibleAmountProperty().lessThan(scrollBar.maxProperty())));
        scrollBar.visibleProperty().bind(visibilityBinding);
        rowConstraints.prefHeightProperty().bind(
                CustomBinding.convert(visibilityBinding, b -> b ? ScrollBar.USE_COMPUTED_SIZE : 0));
    }

    private void onlyShowScrollBarIfNeeded(@NonNull ScrollBar scrollBar, @NonNull ColumnConstraints colConstraints, @NonNull ObjectProperty<ScrollPane.ScrollBarPolicy> scrollBarPolicy) {
        BooleanBinding visibilityBinding;
        visibilityBinding =
                scrollBarPolicy.isEqualTo(ScrollPane.ScrollBarPolicy.NEVER).not()
                        .and(scrollBarPolicy.isEqualTo(ScrollPane.ScrollBarPolicy.ALWAYS)
                                .or(scrollBar.visibleAmountProperty().lessThan(scrollBar.maxProperty())));
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

    private static @NonNull DoubleBinding createContentRectTranslateBinding(ScrollBar scrollBar) {
        return CustomBinding.computeDouble(
                () -> getScrollBarPosition(scrollBar),
                scrollBar.valueProperty(),
                scrollBar.minProperty(),
                scrollBar.maxProperty(),
                scrollBar.visibleAmountProperty());
    }

    public static @Nullable URL getFxmlResource() {
        return ZoomableScrollPane.class.getResource("/org/jhotdraw8/draw/gui/ZoomableScrollPane.fxml");
    }

    public final @NonNull ReadOnlyDoubleProperty viewportWidthProperty() {
        return viewportPane.widthProperty();
    }

    public final @NonNull ReadOnlyDoubleProperty viewportHeightProperty() {
        return viewportPane.heightProperty();
    }


    public final @NonNull DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public double getZoomFactor() {
        return zoomFactor.get();
    }


    public final @NonNull ReadOnlyDoubleProperty viewWidthProperty() {
        return horizontalScrollBar.maxProperty();
    }

    public final @NonNull ReadOnlyDoubleProperty viewHeightProperty() {
        return verticalScrollBar.maxProperty();
    }

    public double getViewportWidth() {
        return viewportWidthProperty().getValue();
    }

    public double getViewportHeight() {
        return viewportHeightProperty().getValue();
    }

    public @NonNull ObservableList<Node> getContentChildren() {
        return content.getChildren();
    }

    public @NonNull ObservableList<Node> getBackgroundChildren() {
        return background.getChildren();
    }

    public @NonNull ObservableList<Node> getForegroundChildren() {
        return foreground.getChildren();
    }


    /**
     * Returns the rectangle of the content which is currently visible in the
     * viewport in content coordinates.
     *
     * @return visible content rectangle in content coordinates
     */
    public @NonNull Bounds getVisibleContentRect() {
        return visibleContentRect.get();
    }

    public @NonNull Bounds getViewRect() {
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

    public @NonNull Bounds getViewportRect() {
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

    public final @NonNull Bounds getViewportBounds() {
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

    public @NonNull Transform getContentToView() {
        return contentToViewProperty().getValue();
    }

    public @NonNull Transform getViewToContent() {
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


    public @NonNull ReadOnlyProperty<Transform> contentToViewProperty() {
        return contentToView;
    }

    public @NonNull DoubleProperty contentWidthProperty() {
        return content.prefWidthProperty();
    }

    public @NonNull DoubleProperty contentHeightProperty() {
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
        return this;
    }

    public static ZoomableScrollPane create() {
        FXMLLoader loader = new FXMLLoader();
        final ZoomableScrollPane controller = new ZoomableScrollPane();
        loader.setLocation(ZoomableScrollPane.getFxmlResource());
        loader.setResources(null);
        loader.setController(controller);
        loader.setRoot(controller);
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

    private ObjectProperty<ScrollPane.ScrollBarPolicy> hbarPolicy;

    public final void setHbarPolicy(ScrollPane.ScrollBarPolicy value) {
        hbarPolicyProperty().set(value);
    }

    public final ScrollPane.ScrollBarPolicy getHbarPolicy() {
        return hbarPolicy == null ? ScrollPane.ScrollBarPolicy.AS_NEEDED : hbarPolicy.get();
    }

    public final ObjectProperty<ScrollPane.ScrollBarPolicy> hbarPolicyProperty() {
        if (hbarPolicy == null) {
            hbarPolicy = new StyleableObjectProperty<ScrollPane.ScrollBarPolicy>(ScrollPane.ScrollBarPolicy.AS_NEEDED) {

                @Override
                public CssMetaData<ZoomableScrollPane, ScrollPane.ScrollBarPolicy> getCssMetaData() {
                    return ZoomableScrollPane.StyleableProperties.HBAR_POLICY;
                }

                @Override
                public Object getBean() {
                    return ZoomableScrollPane.this;
                }

                @Override
                public String getName() {
                    return "hbarPolicy";
                }
            };
        }
        return hbarPolicy;
    }

    /**
     * Specifies the policy for showing the vertical scroll bar.
     */
    private ObjectProperty<ScrollPane.ScrollBarPolicy> vbarPolicy;

    public final void setVbarPolicy(ScrollPane.ScrollBarPolicy value) {
        vbarPolicyProperty().set(value);
    }

    public final ScrollPane.ScrollBarPolicy getVbarPolicy() {
        return vbarPolicy == null ? ScrollPane.ScrollBarPolicy.AS_NEEDED : vbarPolicy.get();
    }

    public final ObjectProperty<ScrollPane.ScrollBarPolicy> vbarPolicyProperty() {
        if (vbarPolicy == null) {
            vbarPolicy = new StyleableObjectProperty<ScrollPane.ScrollBarPolicy>(ScrollPane.ScrollBarPolicy.AS_NEEDED) {

                @Override
                public CssMetaData<ZoomableScrollPane, ScrollPane.ScrollBarPolicy> getCssMetaData() {
                    return ZoomableScrollPane.StyleableProperties.VBAR_POLICY;
                }

                @Override
                public Object getBean() {
                    return ZoomableScrollPane.this;
                }

                @Override
                public String getName() {
                    return "vbarPolicy";
                }
            };
        }
        return vbarPolicy;
    }

    /**
     * Specifies whether the user should be able to pan the viewport by using
     * the mouse. If mouse events reach the ZoomableScrollPane (that is, if mouse
     * events are not blocked by the contained node or one of its children)
     * then {@link #pannableProperty pannable} is consulted to determine if the events should be
     * used for panning.
     */
    private BooleanProperty pannable;

    public final void setPannable(boolean value) {
        pannableProperty().set(value);
    }

    public final boolean isPannable() {
        return pannable == null ? false : pannable.get();
    }

    public final BooleanProperty pannableProperty() {
        if (pannable == null) {
            pannable = new StyleableBooleanProperty(false) {
                @Override
                public void invalidated() {
                    pseudoClassStateChanged(PANNABLE_PSEUDOCLASS_STATE, get());
                }

                @Override
                public CssMetaData<ZoomableScrollPane, Boolean> getCssMetaData() {
                    return ZoomableScrollPane.StyleableProperties.PANNABLE;
                }

                @Override
                public Object getBean() {
                    return ZoomableScrollPane.this;
                }

                @Override
                public String getName() {
                    return "pannable";
                }
            };
        }
        return pannable;
    }

    private static final PseudoClass PANNABLE_PSEUDOCLASS_STATE =
            PseudoClass.getPseudoClass("pannable");
}


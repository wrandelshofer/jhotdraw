
package org.jhotdraw8.draw.gui;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.SubScene;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.binding.CustomBinding;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * ScrollableSubscenePanel provides a scroll pane with a sub-scene,
 * and three stack panes (background, subScene, foreground) that all scroll
 * together. The sub-scene can be zoomed by setting a zoom factor.
 * <p>
 * TODO support scroll wheel and scroll gestures
 * TODO keep camera position when zooming
 */

public class ScrollableSubscenePanel {
    private final DoubleProperty worldScaleFactor = new SimpleDoubleProperty(this, "scaleFactor", 1.0);
    private final DoubleProperty worldWidth = new SimpleDoubleProperty(this, "worldWidth", 300);
    private final DoubleProperty worldHeight = new SimpleDoubleProperty(this, "worldHeight", 300);
    private final DoubleProperty viewX = new SimpleDoubleProperty(this, "viewX", 0);
    private final DoubleProperty viewY = new SimpleDoubleProperty(this, "viewY", 0);

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="horizontalScrollBar"
    private ScrollBar horizontalScrollBar; // Value injected by FXMLLoader

    @FXML // fx:id="verticalScrollBar"
    private ScrollBar verticalScrollBar; // Value injected by FXMLLoader

    @FXML // fx:id="backgroundPane"
    private StackPane backgroundPane; // Value injected by FXMLLoader

    @FXML // fx:id="subScene"
    private SubScene subScene; // Value injected by FXMLLoader


    private StackPane worldPane;
    @FXML // fx:id="foregroundPane"
    private StackPane foregroundPane; // Value injected by FXMLLoader

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert horizontalScrollBar != null : "fx:id=\"horizontalScrollBar\" was not injected: check your FXML file 'ScrollableSubscenePanel.fxml'.";
        assert verticalScrollBar != null : "fx:id=\"verticalScrollBar\" was not injected: check your FXML file 'ScrollableSubscenePanel.fxml'.";
        assert backgroundPane != null : "fx:id=\"backgroundPane\" was not injected: check your FXML file 'ScrollableSubscenePanel.fxml'.";
        assert subScene != null : "fx:id=\"subScene\" was not injected: check your FXML file 'ScrollableSubscenePanel.fxml'.";
        assert foregroundPane != null : "fx:id=\"foregroundPane\" was not injected: check your FXML file 'ScrollableSubscenePanel.fxml'.";
        assert viewportPane != null : "fx:id=\"viewportPane\" was not injected: check your FXML file 'ScrollableSubscenePanel.fxml'.";

        worldPane = new StackPane();
        worldPane.setManaged(false);
        subScene.setRoot(worldPane);


        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(viewportWidthProperty());
        clipRect.heightProperty().bind(viewportHeightProperty());

        viewportPane.setClip(clipRect);


        DoubleBinding translateXBinding = createTranslateBinding(horizontalScrollBar);
        DoubleBinding translateYBinding = createTranslateBinding(verticalScrollBar);


        viewX.bind(translateXBinding.negate());
        viewY.bind(translateYBinding.negate());

        backgroundPane.translateXProperty().bind(translateXBinding);
        backgroundPane.translateYProperty().bind(translateYBinding);
        foregroundPane.translateXProperty().bind(translateXBinding);
        foregroundPane.translateYProperty().bind(translateYBinding);

        subScene.widthProperty().bind(viewportWidthProperty());
        subScene.heightProperty().bind(viewportHeightProperty());

        Scale scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        worldScaleFactor.addListener((o, oldv, newv) -> {
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
        worldPane.getTransforms().addAll(translate, scale);

        horizontalScrollBar.maxProperty().bind(worldWidthProperty().multiply(worldScaleFactor));
        verticalScrollBar.maxProperty().bind(worldHeightProperty().multiply(worldScaleFactor));

        viewportPane.setBackground(null);
        backgroundPane.setBackground(null);
        foregroundPane.setBackground(null);
        worldPane.setBackground(null);

        horizontalScrollBar.visibleAmountProperty().bind(viewportWidthProperty());
        verticalScrollBar.visibleAmountProperty().bind(viewportHeightProperty());

    }

    @NonNull
    private static DoubleBinding createTranslateBinding(ScrollBar horizontalScrollBar2) {
        return CustomBinding.compute(
                () -> {
                    final double min = horizontalScrollBar2.getMin();
                    final double max = horizontalScrollBar2.getMax();
                    final double value = horizontalScrollBar2.getValue();
                    final double visible = horizontalScrollBar2.getVisibleAmount();
                    if (visible > max) {
                        return (visible - max) * 0.5;
                    }
                    return -(max - visible) * (value - min) / (max - min);

                },
                horizontalScrollBar2.valueProperty(),
                horizontalScrollBar2.minProperty(),
                horizontalScrollBar2.maxProperty(),
                horizontalScrollBar2.visibleAmountProperty());
    }

    public static URL getFxmlResource() {
        return ScrollableSubscenePanel.class.getResource("/org/jhotdraw8/draw/gui/ScrollableSubscenePanel.fxml");
    }

    public final ReadOnlyDoubleProperty viewportWidthProperty() {
        return viewportPane.widthProperty();
    }

    public final ReadOnlyDoubleProperty viewportHeightProperty() {
        return viewportPane.heightProperty();
    }

    public final ReadOnlyDoubleProperty viewportXProperty() {
        return horizontalScrollBar.valueProperty();
    }

    public final ReadOnlyDoubleProperty viewportYProperty() {
        return verticalScrollBar.valueProperty();
    }

    public final DoubleProperty worldWidthProperty() {
        return worldWidth;
    }

    public final DoubleProperty worldScaleFactorProperty() {
        return worldScaleFactor;
    }

    public final DoubleProperty worldHeightProperty() {
        return worldHeight;
    }

    public final ReadOnlyDoubleProperty viewWidthProperty() {
        return horizontalScrollBar.maxProperty();
    }

    public final ReadOnlyDoubleProperty viewHeightProperty() {
        return verticalScrollBar.maxProperty();
    }

    public final ReadOnlyDoubleProperty viewXProperty() {
        return viewX;
    }

    public final ReadOnlyDoubleProperty viewYProperty() {
        return viewY;
    }

    public double getViewportWidth() {
        return viewportWidthProperty().getValue();
    }

    public double getViewportHeight() {
        return viewportHeightProperty().getValue();
    }

    public double getViewWidth() {
        return horizontalScrollBar.maxProperty().getValue();
    }

    public double getViewHeight() {
        return verticalScrollBar.maxProperty().getValue();
    }

    public StackPane getWorldPane() {
        return worldPane;
    }

    public StackPane getBackgroundPane() {
        return backgroundPane;
    }

    public StackPane getForegroundPane() {
        return foregroundPane;
    }

    public Bounds getViewRect() {
        return new BoundingBox(getViewX(), getViewY(), getViewWidth(), getViewHeight());
    }

    private double getViewX() {
        return viewX.get();
    }

    private double getViewY() {
        return viewY.get();
    }


    @FXML // fx:id="viewportPane"
    private StackPane viewportPane; // Value injected by FXMLLoader

    public void setWorldWidth(double newValue) {
        worldWidth.set(newValue);
    }

    public void setWorldHeight(double newValue) {
        worldHeight.set(newValue);
    }

    public void setWorldScaleFactor(double newValue) {
        worldScaleFactor.set(newValue);
    }


}


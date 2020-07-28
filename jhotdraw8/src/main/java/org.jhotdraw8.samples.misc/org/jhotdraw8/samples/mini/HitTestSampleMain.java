/*
 * @(#)CardinalSplineSampleMain.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.intersect.IntersectPathIteratorPoint;
import org.jhotdraw8.geom.intersect.IntersectionResult;

import java.awt.geom.AffineTransform;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * CardinalSplineSampleMain.
 *
 * @author Werner Randelshofer
 */
public class HitTestSampleMain extends Application {
    private DoubleProperty width = new SimpleDoubleProperty(0.5);
    private DoubleProperty miterLimit = new SimpleDoubleProperty(10);
    private ObjectProperty<StrokeLineJoin> lineJoin = new SimpleObjectProperty<>(StrokeLineJoin.MITER);
    private Shape path = null;
    private ImageView iview = new ImageView();
    private StackPane canvas;
    private WritableImage image;
    private Circle mouse = new Circle(0, 0, 4);
    private DoubleProperty epsilon = mouse.radiusProperty();
    private String ch = "H";

    @Override
    public void start(@NonNull Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        HBox hbox = new HBox();
        Slider widthSlider = new Slider();
        widthSlider.valueProperty().bindBidirectional(width);
        widthSlider.setMin(0.0);
        widthSlider.setMax(100.0);
        Slider miterLimitSlider = new Slider();
        miterLimitSlider.valueProperty().bindBidirectional(miterLimit);
        miterLimitSlider.setMin(0.0);
        miterLimitSlider.setMax(20.0);
        Slider epsilonSlider = new Slider();
        epsilonSlider.valueProperty().bindBidirectional(epsilon);
        epsilonSlider.setMin(0.0);
        epsilonSlider.setMax(100.0);
        ComboBox<StrokeLineJoin> comboBox = new ComboBox();
        comboBox.getItems().addAll(StrokeLineJoin.BEVEL, StrokeLineJoin.MITER, StrokeLineJoin.ROUND);
        comboBox.valueProperty().bindBidirectional(lineJoin);
        VBox vbox = new VBox();
        HBox h1 = new HBox();
        h1.getChildren().addAll(new Label("width"), widthSlider);
        HBox h2 = new HBox();
        h2.getChildren().addAll(new Label("miterlimit"), miterLimitSlider);
        HBox h3 = new HBox();
        h3.getChildren().addAll(new Label("epsilon"), epsilonSlider);
        vbox.getChildren().addAll(h1, h2, h3);
        hbox.getChildren().add(vbox);
        hbox.getChildren().add(comboBox);

        borderPane.setTop(hbox);
        canvas = new StackPane();
        borderPane.setCenter(canvas);
        canvas.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, null)));
        iview.setManaged(false);
        canvas.getChildren().add(iview);
        mouse.setManaged(false);
        canvas.getChildren().add(mouse);
        image = new WritableImage(300, 300);
        iview.setImage(image);

        Parent root = borderPane;
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("JHotDraw: HitTest Sample");
        primaryStage.setScene(scene);
        primaryStage.show();

        Platform.runLater(this::updateView);
        width.addListener(this::onPropertyChanged);
        lineJoin.addListener(this::onPropertyChanged);
        miterLimit.addListener(this::onPropertyChanged);
        canvas.widthProperty().addListener(this::onPropertyChanged);
        epsilon.addListener(this::onPropertyChanged);
        canvas.heightProperty().addListener(this::onPropertyChanged);
        canvas.setFocusTraversable(true);
        canvas.addEventHandler(KeyEvent.KEY_TYPED, (KeyEvent event) -> {
            ch = event.getCharacter();
            event.consume();
            updateView();
        });
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
            canvas.requestFocus();
            mouse.setCenterX(event.getX());
            mouse.setCenterY(event.getY());
            updateView();
        });
    }

    private void onPropertyChanged(Observable observable) {
        updateView();
    }

    private void updateView() {
        ThreadLocalRandom prng = ThreadLocalRandom.current();

        Text text = new Text();
        text.setText(ch);
        text.setFont(Font.font(300));
        Bounds b = text.getBoundsInParent();

        path = Shape.intersect(text, new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight()));
        path.setManaged(false);
        if (canvas.getChildren().size() > 2) {
            canvas.getChildren().remove(0);
        }
        path.setStrokeWidth(width.doubleValue());
        canvas.getChildren().add(0, path);


        Bounds cb = canvas.getLayoutBounds();
        Bounds lb = path.getLayoutBounds();
        double tx = -lb.getMinX() + 0.5 * (cb.getWidth() - lb.getWidth());
        path.setTranslateX(tx);
        double ty = -lb.getMinY() + 0.5 * (cb.getHeight() - lb.getHeight());
        path.setTranslateY(ty);
        path.setFill(null);
        path.setStroke(Color.BLACK);
        path.setStrokeMiterLimit(miterLimit.doubleValue());
        path.setStrokeLineJoin(lineJoin.getValue());

        if (image.getWidth() != (int) cb.getWidth() || image.getHeight() != (int) cb.getHeight()) {
            image = new WritableImage(Math.max(1, (int) cb.getWidth()), Math.max(1, (int) cb.getHeight()));
            iview.setImage(image);
        }

        // clear the image
        PixelWriter pw = image.getPixelWriter();
        int w = (int) image.getWidth(), h = (int) image.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                pw.setArgb(x, y, 0);
            }
        }

        java.awt.Shape awtPath = Shapes.awtShapeFromFX(path);
        double tolerance = epsilon.get() + width.get() * 0.5;

        IntStream.range(0, 100_000).parallel().forEach(i -> {
            ThreadLocalRandom tlr = ThreadLocalRandom.current();
            int x = tlr.nextInt(0, w);
            int y = tlr.nextInt(0, h);

            IntersectionResult r = IntersectPathIteratorPoint.intersectPathIteratorPoint(
                    awtPath.getPathIterator(AffineTransform.getTranslateInstance(tx, ty)), x, y, tolerance);
            Color color = getColor(r);
            pw.setColor(x, y, color);
        });


        IntersectionResult r = IntersectPathIteratorPoint.intersectPathIteratorPoint(
                awtPath.getPathIterator(AffineTransform.getTranslateInstance(tx, ty)), mouse.getCenterX(),
                mouse.getCenterY(), tolerance);
        mouse.setFill(getColor(r));
    }

    @NonNull
    private Color getColor(IntersectionResult r) {
        return switch (r.getStatus()) {

            case INTERSECTION -> Color.RED;
            case NO_INTERSECTION -> Color.YELLOW;

            case NO_INTERSECTION_INSIDE -> Color.GREEN;

            case NO_INTERSECTION_OUTSIDE -> Color.BLUE;

            case NO_INTERSECTION_TANGENT -> Color.PURPLE;

            case NO_INTERSECTION_COINCIDENT -> Color.CYAN;

            case NO_INTERSECTION_PARALLEL -> Color.ORANGE;

        };
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

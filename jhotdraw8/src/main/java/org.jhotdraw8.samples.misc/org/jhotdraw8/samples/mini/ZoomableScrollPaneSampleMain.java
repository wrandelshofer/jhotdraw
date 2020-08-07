package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.draw.gui.ZoomableScrollPane;
import org.jhotdraw8.geom.FXGeom;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ZoomableScrollPaneSampleMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ZoomableScrollPane.getFxmlResource());
        loader.setResources(null);
        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Parent scrollableSubScenePanel = loader.getRoot();
        ZoomableScrollPane p = loader.getController();
        BorderPane borderPane = new BorderPane();
        HBox hbox = new HBox();

        Slider s = new Slider();
        s.setMin(-10.0);
        s.setMax(10.0);
        s.setValue(0.0);
        s.setMajorTickUnit(5);
        s.setMinorTickCount(4);
        s.setSnapToTicks(true);
        s.setShowTickMarks(true);
        s.setShowTickLabels(true);
        Rectangle storedBounds = new Rectangle();
        Button b = new Button("storeRect");
        b.setOnAction(evt -> {
            Bounds w = p.getViewRect();
            storedBounds.setX(w.getMinX());
            storedBounds.setY(w.getMinY());
            storedBounds.setWidth(w.getWidth());
            storedBounds.setHeight(w.getHeight());
        });
        Button b2 = new Button("scrollToRect");
        b2.setOnAction(evt -> {
            p.scrollContentRectToVisible(storedBounds.getBoundsInLocal());
        });
        hbox.getChildren().add(s);
        hbox.getChildren().add(b);
        hbox.getChildren().add(b2);
        borderPane.setTop(hbox);
        borderPane.setCenter(scrollableSubScenePanel);
        Scene scene = new Scene(borderPane, 300, 250);
        Label label = new Label("-");
        borderPane.setBottom(label);
        label.textProperty().bind(CustomBinding.convert(p.viewRectProperty(), FXGeom::toString));

        primaryStage.setScene(scene);

        p.setContentSize(80_000, 600);

        Rectangle bg = new Rectangle(10, 10, 780, 580);
        bg.setFill(Color.BLUE);
        bg.setManaged(false);
        p.getBackgroundChildren().add(bg);
        p.contentToViewProperty().addListener((o, oldv, newv) -> {
            Bounds lb = newv.transform(new BoundingBox(10, 10, 780, 580));
            bg.setX(lb.getMinX());
            bg.setY(lb.getMinY());
            bg.setWidth(lb.getWidth());
            bg.setHeight(lb.getHeight());
        });

        Rectangle fg = new Rectangle(10, 10, 780, 580);
        fg.setFill(null);
        fg.setStroke(Color.RED);
        fg.setManaged(false);
        p.contentToViewProperty().addListener((o, oldv, newv) -> {
            Bounds lb = newv.transform(new BoundingBox(10, 10, 780, 580));
            fg.setX(lb.getMinX());
            fg.setY(lb.getMinY());
            fg.setWidth(lb.getWidth());
            fg.setHeight(lb.getHeight());
        });
        p.getForegroundChildren().add(fg);

        Rectangle sr = new Rectangle(50, 50, 700, 500);
        sr.setFill(null);
        sr.setStroke(Color.GREEN);
        sr.setManaged(false);
        Rectangle sr2 = new Rectangle(0, 0, 100, 100);
        sr2.setFill(null);
        sr2.setStroke(Color.LIGHTGREEN);
        sr2.setManaged(false);
        Rectangle sr3 = new Rectangle(700, 500, 100, 100);
        sr3.setFill(null);
        sr3.setStroke(Color.LIGHTGREEN);
        sr3.setManaged(false);
        Line sr4 = new Line(400, 0, 400, 600);
        sr4.setFill(null);
        sr4.setStroke(Color.LIGHTGREEN);
        sr4.setManaged(false);
        Line sr5 = new Line(0, 300, 800, 300);
        sr5.setFill(null);
        sr5.setStroke(Color.LIGHTGREEN);
        sr5.setManaged(false);

        Button button = new Button("Button without stylesheet!");
        button.setManaged(false);
        button.resizeRelocate(20, 20, 200, 40);

        p.setSubSceneUserAgentStylesheet(getClass().getResource("empty.css").toString());

        p.getContentChildren().addAll(sr, sr2, sr3, sr4, sr5, button);

        p.zoomFactorProperty().bind(
                CustomBinding.computeDouble(() -> Math.pow(2, s.getValue()), s.valueProperty())
        );


        //p.worldScaleFactorProperty().bind(s.valueProperty().);


        primaryStage.setTitle("ZoomPane");
        primaryStage.show();

    }
}

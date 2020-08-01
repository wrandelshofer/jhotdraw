package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.jhotdraw8.draw.gui.ScrollableSubscenePanel;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ScrollableSubscenePanelSampleMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ScrollableSubscenePanel.getFxmlResource());
        loader.setResources(null);
        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Parent scrollableSubScenePanel = loader.getRoot();
        BorderPane borderPane = new BorderPane();
        Slider s = new Slider();
        s.setMin(0.0);
        s.setMax(2.0);
        s.setValue(1.0);
        borderPane.setTop(s);
        borderPane.setCenter(scrollableSubScenePanel);
        Scene scene = new Scene(borderPane, 300, 250);

        primaryStage.setScene(scene);
        ScrollableSubscenePanel p = loader.getController();

        p.setWorldWidth(800);
        p.setWorldHeight(600);

        Rectangle bg = new Rectangle(10, 10, 780, 580);
        bg.setFill(Color.BLUE);
        bg.setManaged(false);
        p.getBackgroundPane().getChildren().add(bg);

        Rectangle fg = new Rectangle(10, 10, 780, 580);
        fg.setFill(null);
        fg.setStroke(Color.RED);
        fg.setManaged(false);
        p.getForegroundPane().getChildren().add(fg);

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
        p.getWorldPane().getChildren().addAll(sr, sr2, sr3);

        p.worldScaleFactorProperty().bind(s.valueProperty());


        primaryStage.setTitle("ScrollableSubScenePanelSample");
        primaryStage.show();

    }
}

/* @(#)ConnectingFiguresSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javax.annotation.Nonnull;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.tool.SelectionTool;
import org.jhotdraw8.draw.tool.Tool;

/**
 * ConnectingFiguresSample demonstrates how to connect two figures with a
 * LineConnectionFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectingFiguresSampleMain extends Application {

    @Override
    public void start(@Nonnull Stage primaryStage) throws Exception {

        // Create a drawing view.
        DrawingView drawingView = new SimpleDrawingView();

        // Create the drawing and add it to the DrawingView.
        Drawing drawing = new ConnectingFiguresSample().createDrawing();
        drawingView.setDrawing(drawing);

        // A DrawingView can display a Drawing. But it does not directly support editing.
        // To add support for editing, add it to a DrawingEditor, 
        // and activate a  Tool on the drawing editor.
        DrawingEditor drawingEditor = new SimpleDrawingEditor();
        drawingEditor.drawingViewsProperty().add(drawingView);
        Tool tool = new SelectionTool();
        drawingEditor.setActiveTool(tool);

        // Create a ScrollPane and set the DrawingView as its content.
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(drawingView.getNode());

        // Put the drawing view into the scene and show the stage.
        primaryStage.setScene(new Scene(scrollPane));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Launch JavaFX
        launch(args);
    }
}

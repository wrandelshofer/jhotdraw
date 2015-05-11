/* @(#)ConnectingFiguresSample.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.samples.mini;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleDrawingEditor;
import org.jhotdraw.draw.SimpleDrawingView;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.draw.tool.Tool;

/**
 * ConnectingFiguresSample.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectingFiguresSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Drawing drawing = new SimpleDrawing();
        
        RectangleFigure vertex1 = new RectangleFigure(10,10,30,20);
        RectangleFigure vertex2 = new RectangleFigure(50,40,30,20);
        RectangleFigure vertex3 = new RectangleFigure(90,10,30,20);
        
        LineConnectionFigure edge12 = new LineConnectionFigure();
        LineConnectionFigure edge23 = new LineConnectionFigure();
        
        edge12.set(ConnectionFigure.START_FIGURE,vertex1);
        edge12.set(ConnectionFigure.END_FIGURE,vertex2);
        edge23.set(ConnectionFigure.START_FIGURE,vertex2);
        edge23.set(ConnectionFigure.END_FIGURE,vertex3);

        drawing.add(vertex1);
        drawing.add(vertex2);
        drawing.add(vertex3);
        
        drawing.add(edge12);
        drawing.add(edge23);


        DrawingView drawingView = new SimpleDrawingView();
        
        drawingView.setDrawing(drawing);
        
        
        DrawingEditor drawingEditor = new SimpleDrawingEditor();
        drawingEditor.drawingViewsProperty().add(drawingView);
        
        Tool tool = new SelectionTool();
        drawingEditor.setActiveTool(tool);
        
        ScrollPane root = new ScrollPane();
        root.setContent(drawingView.getNode());
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();    }
 public static void main(String[] args) {
        launch(args);
    }
}

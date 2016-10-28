/* @(#)ConnectingFiguresSample.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.GroupFigure;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleDrawingEditor;
import org.jhotdraw.draw.SimpleDrawingView;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.draw.tool.Tool;

/**
 * ConnectingFiguresSample.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectingFiguresSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Drawing drawing = new SimpleDrawing();

        RectangleFigure vertex1 = new RectangleFigure(10, 10, 30, 20);
        RectangleFigure vertex2 = new RectangleFigure(50, 40, 30, 20);
        RectangleFigure vertex3 = new RectangleFigure(90, 10, 30, 20);

        LineConnectionFigure edge12 = new LineConnectionFigure();
        LineConnectionFigure edge23 = new LineConnectionFigure();
        LineConnectionFigure edge3Null = new LineConnectionFigure();
        LineConnectionFigure edgeNullNull = new LineConnectionFigure();

        edge12.setStartConnection(vertex1,new ChopRectangleConnector());
        edge12.setEndConnection(vertex2,new ChopRectangleConnector());

        edge23.setStartConnection(vertex2,new ChopRectangleConnector());
        edge23.setEndConnection(vertex3,new ChopRectangleConnector());
        edge3Null.setStartConnection(vertex3,new ChopRectangleConnector());
        edge3Null.set(LineConnectionFigure.END, new Point2D(145, 15));
        edgeNullNull.set(LineConnectionFigure.START, new Point2D(65, 90));
        edgeNullNull.set(LineConnectionFigure.END, new Point2D(145, 95));

        RectangleFigure vertex2b = new RectangleFigure(80, 40, 30, 20);
        GroupFigure vertex2Group = new GroupFigure();
        vertex2Group.add(vertex2);
        vertex2Group.add(vertex2b);

        Layer layer = new SimpleLayer();
        drawing.add(layer);

        layer.add(vertex1);
        //drawing.add(vertex2);
        layer.add(vertex2Group);
        layer.add(vertex3);

        layer.add(edge12);
        layer.add(edge23);
        layer.add(edge3Null);
        layer.add(edgeNullNull);
        
        drawing.layout();

        DrawingView drawingView = new SimpleDrawingView();

        drawingView.setDrawing(drawing);

        DrawingEditor drawingEditor = new SimpleDrawingEditor();
        drawingEditor.drawingViewsProperty().add(drawingView);

        Tool tool = new SelectionTool();
        drawingEditor.setActiveTool(tool);

        ScrollPane root = new ScrollPane();
        root.setContent(drawingView.getNode());
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

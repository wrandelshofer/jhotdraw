/* @(#)ConnectingFiguresSample.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleDrawingEditor;
import org.jhotdraw.draw.SimpleDrawingView;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.shape.AbstractShapeFigure;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.draw.shape.TextFigure;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.draw.tool.Tool;

/**
 * ConnectingFiguresSample.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Drawing drawing = new SimpleDrawing();

        RectangleFigure vertex1 = new RectangleFigure(10, 10, 30, 20);
        RectangleFigure vertex2 = new RectangleFigure(50, 40, 30, 20);
        TextFigure vertex3 = new TextFigure(120, 50, "Lorem Ipsum");
        RectangleFigure vertex4 = new RectangleFigure(90, 100, 30, 20);

        LineConnectionFigure edge12 = new LineConnectionFigure();
        LineConnectionFigure edge23 = new LineConnectionFigure();
        LineConnectionFigure edge3Null = new LineConnectionFigure();
        LineConnectionFigure edgeNullNull = new LineConnectionFigure();

        edge12.set(LineConnectionFigure.START_FIGURE, vertex1);
        edge12.set(LineConnectionFigure.END_FIGURE, vertex2);
        edge12.set(LineConnectionFigure.START_CONNECTOR, new ChopRectangleConnector());
        edge12.set(LineConnectionFigure.END_CONNECTOR, new ChopRectangleConnector());

        edge23.set(LineConnectionFigure.START_FIGURE, vertex2);
        edge23.set(LineConnectionFigure.END_FIGURE, vertex3);
        edge23.set(LineConnectionFigure.START_CONNECTOR, new ChopRectangleConnector());
        edge23.set(LineConnectionFigure.END_CONNECTOR, new ChopRectangleConnector());
        edge3Null.set(LineConnectionFigure.START_FIGURE, vertex3);
        edge3Null.set(LineConnectionFigure.START_CONNECTOR, new ChopRectangleConnector());
        edge3Null.set(LineConnectionFigure.END, new Point2D(145, 15));
        edgeNullNull.set(LineConnectionFigure.START, new Point2D(65, 90));
        edgeNullNull.set(LineConnectionFigure.END, new Point2D(145, 95));


        Layer layer = new SimpleLayer();
        drawing.add(layer);

        layer.add(vertex1);
        layer.add(vertex2);
        layer.add(vertex3);
        layer.add(vertex4);

        layer.add(edge12);
        layer.add(edge23);
        layer.add(edge3Null);
        layer.add(edgeNullNull);
        
        vertex1.set(Figure.ID,"vertex1");
        vertex2.set(Figure.ID,"vertex2");
        vertex3.set(Figure.ID,"vertex3");
        vertex4.set(Figure.ID,"vertex4");
        
        drawing.layout();
        drawing.set(Drawing.STYLESHEET, CssSample.class.getResource("CssSample.css"));
        drawing.applyCss();
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

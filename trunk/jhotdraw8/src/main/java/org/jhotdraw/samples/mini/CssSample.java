/* @(#)CssSample.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.samples.mini;

import java.net.URI;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleDrawingEditor;
import org.jhotdraw.draw.SimpleDrawingView;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.figure.StyleableFigure;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.draw.figure.LineFigure;
import org.jhotdraw.draw.figure.RectangleFigure;
import org.jhotdraw.draw.figure.TextFigure;
import org.jhotdraw.draw.tool.SelectionTool;
import org.jhotdraw.draw.tool.Tool;

/**
 * CssSample..
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

        edge12.setStartConnection(vertex1,new ChopRectangleConnector());
        edge12.setEndConnection(vertex2,new ChopRectangleConnector());

        edge23.setStartConnection(vertex2,new ChopRectangleConnector());
        edge23.setEndConnection(vertex3,new ChopRectangleConnector());
        edge3Null.setStartConnection(vertex3,new ChopRectangleConnector());
        edge3Null.set(LineConnectionFigure.END, new Point2D(145, 15));
        edgeNullNull.set(LineConnectionFigure.START, new Point2D(65, 90));
        edgeNullNull.set(LineConnectionFigure.END, new Point2D(145, 95));


        LineFigure line1 = new LineFigure();
        line1.set(LineFigure.START, new Point2D(50,150));
        line1.set(LineFigure.END, new Point2D(100,150));
        
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
        layer.add(line1);
        
        vertex1.set(StyleableFigure.STYLE_ID,"vertex1");
        vertex2.set(StyleableFigure.STYLE_ID,"vertex2");
        vertex3.set(StyleableFigure.STYLE_ID,"vertex3");
        vertex4.set(StyleableFigure.STYLE_ID,"vertex4");
        
        ArrayList<URI> stylesheets=new ArrayList<>();
        stylesheets.add(CssSample.class.getResource("CssSample.css").toURI());
        drawing.set(Drawing.USER_AGENT_STYLESHEETS,stylesheets);
        drawing.updateCss();
        
        drawing.layout();

        DrawingView drawingView = new SimpleDrawingView();

        drawingView.setDrawing(drawing);
        drawingView.setConstrainer(new GridConstrainer(10,10));
        //drawingView.setHandleType(HandleType.RESHAPE);

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

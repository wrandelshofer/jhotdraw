/* @(#)CssSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.DrawingFigure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.draw.figure.LineFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.TextFigure;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.draw.tool.SelectionTool;
import org.jhotdraw8.draw.tool.Tool;

import java.net.URI;
import java.util.ArrayList;

/**
 * CssSample..
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSample extends Application {

    @Override
    public void start(@Nonnull Stage primaryStage) throws Exception {
        Drawing drawing = new DrawingFigure();

        RectangleFigure vertex1 = new RectangleFigure(10, 10, 30, 20);
        RectangleFigure vertex2 = new RectangleFigure(50, 40, 30, 20);
        TextFigure vertex3 = new TextFigure(120, 50, "Lorem Ipsum");
        RectangleFigure vertex4 = new RectangleFigure(90, 100, 30, 20);

        LineConnectionFigure edge12 = new LineConnectionFigure();
        LineConnectionFigure edge23 = new LineConnectionFigure();
        LineConnectionFigure edge3Null = new LineConnectionFigure();
        LineConnectionFigure edgeNullNull = new LineConnectionFigure();

        edge12.setStartConnection(vertex1, new RectangleConnector());
        edge12.setEndConnection(vertex2, new RectangleConnector());

        edge23.setStartConnection(vertex2, new RectangleConnector());
        edge23.setEndConnection(vertex3, new RectangleConnector());
        edge3Null.setStartConnection(vertex3, new RectangleConnector());
        edge3Null.set(LineConnectionFigure.END, new CssPoint2D(145, 15));
        edgeNullNull.set(LineConnectionFigure.START, new CssPoint2D(65, 90));
        edgeNullNull.set(LineConnectionFigure.END, new CssPoint2D(145, 95));

        LineFigure line1 = new LineFigure();
        line1.set(LineFigure.START, new CssPoint2D(50, 150));
        line1.set(LineFigure.END, new CssPoint2D(100, 150));

        Layer layer = new LayerFigure();
        drawing.addChild(layer);

        layer.addChild(vertex1);
        layer.addChild(vertex2);
        layer.addChild(vertex3);
        layer.addChild(vertex4);

        layer.addChild(edge12);
        layer.addChild(edge23);
        layer.addChild(edge3Null);
        layer.addChild(edgeNullNull);
        layer.addChild(line1);

        vertex1.set(StyleableFigure.ID, "vertex1");
        vertex2.set(StyleableFigure.ID, "vertex2");
        vertex3.set(StyleableFigure.ID, "vertex3");
        vertex4.set(StyleableFigure.ID, "vertex4");

        ArrayList<URI> stylesheets = new ArrayList<>();
        stylesheets.add(CssSample.class.getResource("CssSample.css").toURI());
        drawing.set(Drawing.USER_AGENT_STYLESHEETS, ImmutableLists.ofCollection(stylesheets));
        drawing.updateCss();

        SimpleRenderContext ctx = new SimpleRenderContext();
        drawing.layout(ctx);

        DrawingView drawingView = new SimpleDrawingView();

        drawingView.setDrawing(drawing);
        drawingView.setConstrainer(new GridConstrainer(10, 10));
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

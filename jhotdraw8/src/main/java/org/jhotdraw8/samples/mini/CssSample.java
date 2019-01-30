/* @(#)CssSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import java.net.URI;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.Nonnull;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.draw.figure.SimpleLineFigure;
import org.jhotdraw8.draw.figure.SimpleRectangleFigure;
import org.jhotdraw8.draw.figure.SimpleTextFigure;
import org.jhotdraw8.draw.render.SimpleRenderContext;
import org.jhotdraw8.draw.tool.SelectionTool;
import org.jhotdraw8.draw.tool.Tool;

/**
 * CssSample..
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSample extends Application {

    @Override
    public void start(@Nonnull Stage primaryStage) throws Exception {
        Drawing drawing = new SimpleDrawing();

        SimpleRectangleFigure vertex1 = new SimpleRectangleFigure(10, 10, 30, 20);
        SimpleRectangleFigure vertex2 = new SimpleRectangleFigure(50, 40, 30, 20);
        SimpleTextFigure vertex3 = new SimpleTextFigure(120, 50, "Lorem Ipsum");
        SimpleRectangleFigure vertex4 = new SimpleRectangleFigure(90, 100, 30, 20);

        SimpleLineConnectionFigure edge12 = new SimpleLineConnectionFigure();
        SimpleLineConnectionFigure edge23 = new SimpleLineConnectionFigure();
        SimpleLineConnectionFigure edge3Null = new SimpleLineConnectionFigure();
        SimpleLineConnectionFigure edgeNullNull = new SimpleLineConnectionFigure();

        edge12.setStartConnection(vertex1, new RectangleConnector());
        edge12.setEndConnection(vertex2, new RectangleConnector());

        edge23.setStartConnection(vertex2, new RectangleConnector());
        edge23.setEndConnection(vertex3, new RectangleConnector());
        edge3Null.setStartConnection(vertex3, new RectangleConnector());
        edge3Null.set(SimpleLineConnectionFigure.END, new CssPoint2D(145, 15));
        edgeNullNull.set(SimpleLineConnectionFigure.START, new CssPoint2D(65, 90));
        edgeNullNull.set(SimpleLineConnectionFigure.END, new CssPoint2D(145, 95));

        SimpleLineFigure line1 = new SimpleLineFigure();
        line1.set(SimpleLineFigure.START, new CssPoint2D(50, 150));
        line1.set(SimpleLineFigure.END, new CssPoint2D(100, 150));

        Layer layer = new SimpleLayer();
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
        drawing.set(Drawing.USER_AGENT_STYLESHEETS, stylesheets);
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

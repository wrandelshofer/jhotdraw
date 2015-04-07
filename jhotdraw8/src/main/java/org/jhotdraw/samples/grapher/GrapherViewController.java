/* @(#)GrapherViewController.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.grapher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import org.jhotdraw.app.AbstractView;
import org.jhotdraw.concurrent.BackgroundTask;
import org.jhotdraw.concurrent.TaskCompletionEvent;
import org.jhotdraw.draw.SimpleDrawingView;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.FigureKeys;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.io.DefaultFigureFactory;
import org.jhotdraw.draw.io.FigureFactory;
import org.jhotdraw.draw.io.SimpleFigureFactory;
import org.jhotdraw.draw.io.SimpleXmlIO;

/**
 *
 * @author werni
 */
public class GrapherViewController extends AbstractView {

    private Node node;

    @FXML
    private ScrollPane scrollPane;

    private SimpleDrawingView drawingView;

    @Override
    public void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("GrapherView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        Drawing d = new SimpleDrawing();
        d.add(new RectangleFigure(10, 10, 40, 30));
        d.add(new TextFigure(15, 30, "Hello"));

        drawingView = new SimpleDrawingView();
        drawingView.init();
        drawingView.setDrawing(d);

        scrollPane.setContent(drawingView.getNode());
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void clearModified() {
    }

    @Override
    public void read(URI uri, boolean append, EventHandler<TaskCompletionEvent> callback) {
 BackgroundTask<SimpleDrawing> t = new BackgroundTask<SimpleDrawing>() {

            @Override
            protected SimpleDrawing call() throws Exception {
                try {
              FigureFactory factory = new DefaultFigureFactory();
                SimpleXmlIO io = new SimpleXmlIO(factory);
                SimpleDrawing drawing = (SimpleDrawing) io.read(uri,null);
                return drawing;
                } catch (Exception e) {
                    throw e;
                }
            }

     @Override
     protected void succeeded(SimpleDrawing value) {
          drawingView.setDrawing(value);
     }
            
        };
        t.addCompletionHandler(callback);
        getApplication().execute(t);
    }

    @Override
    public void write(URI uri, EventHandler<TaskCompletionEvent> callback) {
        BackgroundTask<Void> t = new BackgroundTask<Void>() {

            @Override
            protected Void call() throws Exception {
              FigureFactory factory = new DefaultFigureFactory();
                SimpleXmlIO io = new SimpleXmlIO(factory);
                io.write(uri,drawingView.getDrawing());
                return null;
            }
        };
        t.addCompletionHandler(callback);
        getApplication().execute(t);
    }

    @Override
    public void clear() {
    }

    @FXML
    public void buttonPerformed(ActionEvent event) {
        Instant now = Instant.now();
        for (Figure f : drawingView.getDrawing().children()) {
            if (f instanceof TextFigure) {
                TextFigure tf = (TextFigure) f;
                tf.set(TextFigure.TEXT, now.toString());
            }
        }
    }
}

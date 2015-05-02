/* @(#)TextAreaViewController.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import org.jhotdraw.concurrent.BackgroundTask;
import org.jhotdraw.concurrent.TaskCompletionEvent;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class TextAreaView extends AbstractView implements Initializable {

    @Override
    public void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("TextAreaView.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea textArea;

    private Node node;

    /**
     * Initializes the controller class.
     */
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        textArea.textProperty().addListener((observable -> modified.set(true)));
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void clear() {
        textArea.setText(null);
    }

    @Override
    public void read(URI uri, boolean append, EventHandler<TaskCompletionEvent> handler) {
        BackgroundTask<String> t = new BackgroundTask<String>() {

            @Override
            protected String call() throws Exception {
                StringBuilder builder = new StringBuilder();
                char[] cbuf = new char[8192];
                try (Reader in = new InputStreamReader(new FileInputStream(new File(uri)), StandardCharsets.UTF_8)) {
                    for (int count = in.read(cbuf, 0, cbuf.length); count != -1; count = in.read(cbuf, 0, cbuf.length)) {
                        builder.append(cbuf, 0, count);
                    }
                }
                return builder.toString();
            }

            @Override
            protected void succeeded(String value) {
                if (append) {
                    textArea.appendText(value);
                } else {
                    textArea.setText(value);
                }
            }
        };
        t.addCompletionHandler(handler);
        getApplication().execute(t);

    }

    @Override
    public void write(URI uri, EventHandler<TaskCompletionEvent> handler) {
        final String text = textArea.getText();
        BackgroundTask<Void> t = new BackgroundTask<Void>() {

            @Override
            protected Void call() throws Exception {
                try (Writer out = new OutputStreamWriter(new FileOutputStream(new File(uri)), StandardCharsets.UTF_8)) {
                    out.write(text);
                }
                return null;
            }
        };
        t.addCompletionHandler(handler);
        getApplication().execute(t);
    }

    @Override
    public void clearModified() {
        modified.set(false);
    }

}

/* @(#)TextAreaViewController.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.samples.teddy;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.app.AbstractDocumentView;
import org.jhotdraw8.app.DocumentView;
import org.jhotdraw8.concurrent.FXWorker;

/**
 * TeddyView.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class TeddyView extends AbstractDocumentView implements DocumentView, Initializable {

    @Override
    public void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            node = loader.load(getClass().getResourceAsStream("TeddyView.fxml"));
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
    public CompletionStage<Void> clear() {
        textArea.setText(null);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> read(URI uri, DataFormat format, boolean append) {
        return FXWorker.supply(() -> {
            StringBuilder builder = new StringBuilder();
            char[] cbuf = new char[8192];
            try (Reader in = new InputStreamReader(new FileInputStream(new File(uri)), StandardCharsets.UTF_8)) {
                for (int count = in.read(cbuf, 0, cbuf.length); count != -1; count = in.read(cbuf, 0, cbuf.length)) {
                    builder.append(cbuf, 0, count);
                }
            }
            return builder.toString();
        }).thenAccept(value -> {
            if (append) {
                textArea.appendText(value);
            } else {
                textArea.setText(value);
            }
        });
    }

    @Override
    public CompletionStage<Void> write(URI uri, DataFormat format) {
        final String text = textArea.getText();
        return FXWorker.run(() -> {
            try (Writer out = new OutputStreamWriter(new FileOutputStream(new File(uri)), StandardCharsets.UTF_8)) {
                out.write(text);
            }
        });
    }

    @Override
    public void clearModified() {
        modified.set(false);
    }

}

/*
 * @(#)FxmlNodeReader.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.app.spi;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FxmlNodeReader implements NodeReader {
    @Override
    public Node read(@NonNull URL url) throws IOException {
        FXMLLoader loader = new FXMLLoader(url);
        loader.load();
        return loader.getRoot();
    }

    @Override
    public Node read(@NonNull InputStream in) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.load(in);
        return loader.getRoot();
    }
}

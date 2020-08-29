/*
 * @(#)FxSvgTinyReaderNew.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.render.SimpleDrawingRenderer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;

public class FXSvgTinyReaderNew {

    public Node read(@NonNull java.nio.file.Path p) throws IOException {
        try (InputStream in = new BufferedInputStream(Files.newInputStream(p))) {
            return read(in, p.getParent().toUri());
        }
    }

    public Node read(@NonNull InputStream in, @NonNull URI documentHome) throws IOException {
        Figure figure = new FigureSvgTinyReaderNew().read(in, documentHome);
        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        return r.render(figure);
    }
}

/*
 * @(#)FXSvgTinyReaderNew.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.render.SimpleDrawingRenderer;

import javax.xml.transform.Source;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;

/**
 * Reads an SVG "Tiny" 1.2 file and creates JavaFX nodes from it.
 */
public class FXSvgTinyReader {



    public Node read(@NonNull Source in) throws IOException {
        Figure figure = new FigureSvgTinyReader().read(in);
        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        Node node = r.render(figure);
        node.setManaged(true);
        return node;
    }
}

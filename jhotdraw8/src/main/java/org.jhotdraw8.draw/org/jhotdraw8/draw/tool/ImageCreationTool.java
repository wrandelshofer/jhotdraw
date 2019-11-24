/*
 * @(#)ImageCreationTool.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.stage.FileChooser;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.util.Resources;

import java.net.URI;
import java.util.function.Supplier;

/**
 * ImageCreationTool.
 *
 * @author Werner Randelshofer
 * @design.pattern CreationTool AbstractFactory, Client.
 */
public class ImageCreationTool extends CreationTool {

    @NonNull
    private MapAccessor<URI> uriKey = ImageFigure.IMAGE_URI;
    private URIChooser uriChooser;
    @Nullable
    private URI uri;

    public ImageCreationTool(String name, Resources rsrc, Supplier<Figure> supplier, Supplier<Layer> layerFactory) {
        super(name, rsrc, supplier, layerFactory);
    }

    @Override
    public void activate(@NonNull DrawingEditor editor) {
        super.activate(editor);
        uri = chooseFile();
    }

    @Override
    protected Figure createFigure() {
        Figure f = super.createFigure();
        f.set(uriKey, uri);
        return f;
    }

    @NonNull
    protected URIChooser createURIChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.bmp", "*.gif", "*.jpg", "*.png"));
        return c;
    }

    @Nullable
    protected URI chooseFile() {
        if (uriChooser == null) {
            uriChooser = createURIChooser();
        }
        URI uri = uriChooser.showDialog(node);
        /*if (uri != null) {
            Drawing drawing = getDrawingView().getDrawing();
            URI documentHome = drawing.get(Drawing.DOCUMENT_HOME);
            if (documentHome != null) {
                uri = documentHome.relativize(uri);
            }
        }*/
        return uri;
    }
}

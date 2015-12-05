/* @(#)ImageCreationTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.net.URI;
import java.util.function.Supplier;
import javafx.stage.FileChooser;
import org.jhotdraw.collection.MapAccessor;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.Layer;
import org.jhotdraw.draw.SimpleDrawingEditor;
import org.jhotdraw.draw.figure.misc.ImageFigure;
import org.jhotdraw.gui.FileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.util.Resources;

/**
 * ImageCreationTool.
 *
 * @author Werner Randelshofer
 */
public class ImageCreationTool extends CreationTool {

    private MapAccessor<URI> uriKey = ImageFigure.IMAGE_URI;
    private URIChooser uriChooser;
    private URI uri;

    public ImageCreationTool(String name, Resources rsrc, Supplier<Figure> supplier, Supplier<Layer> layerFactory) {
        super(name, rsrc, supplier, layerFactory);
    }

    @Override
    public void activate(SimpleDrawingEditor editor) {
        super.activate(editor);

        uri = chooseFile();
    }

    @Override
    protected Figure createFigure() {
        Figure f = super.createFigure();
        f.set(uriKey, uri);
        return f;
    }

    protected URIChooser createURIChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.bmp", "*.gif", "*.jpg", "*.png"));
        return c;
    }

    protected URI chooseFile() {
        if (uriChooser == null) {
            uriChooser = createURIChooser();
        }
        URI uri = uriChooser.showDialog(node);
        if (uri != null) {
            Drawing drawing = getDrawingView().getDrawing();
            URI documentHome = drawing.get(Drawing.DOCUMENT_HOME);
            if (documentHome != null) {
                uri = documentHome.relativize(uri);
            }
        }
        return uri;
    }
}

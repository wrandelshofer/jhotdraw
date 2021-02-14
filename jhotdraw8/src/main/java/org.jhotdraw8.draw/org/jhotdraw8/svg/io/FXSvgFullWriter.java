/*
 * @(#)FXSvgFullWriter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.io;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssDimension2D;
import org.jhotdraw8.draw.figure.StyleableFigure;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Exports a JavaFX scene graph to SVG 1.1 "Full".
 * <p>
 * References:
 * <dl>
 *     <dt>SVG 1.1</dt>
 *     <dd><a href="https://www.w3.org/TR/SVG11/">w3.org</a></dd>
 * </dl>
 *
 *
 * @author Werner Randelshofer
 */
public class FXSvgFullWriter extends AbstractFXSvgWriter {
    private static final String SVG_VERSION = "1.1";
    private static final String SVG_BASE_PROFILE = "full";
    public static final String SVG_MIME_TYPE_WITH_VERSION = SVG_MIME_TYPE + ";version=\"" + SVG_VERSION + "\"";

    /**
     * @param imageUriKey this property is used to retrieve an URL from an
     *                    ImageView
     * @param skipKey     this property is used to retrieve a Boolean from a Node.
     */
    public FXSvgFullWriter(Object imageUriKey, Object skipKey) {
        super(imageUriKey, skipKey);
    }

    protected void writeDocumentElementAttributes(@NonNull XMLStreamWriter
                                                          w, Node drawingNode, @Nullable CssDimension2D size) throws XMLStreamException {
        w.writeAttribute("version", getSvgVersion());
        w.writeAttribute("baseProfile", getSvgBaseProfile());
        if (size != null) {
            w.writeAttribute("width", nb.toString(size.getWidth().getValue()) + size.getWidth().getUnits());
            w.writeAttribute("height", nb.toString(size.getHeight().getValue()) + size.getHeight().getUnits());
        }
    }

    protected String getSvgVersion() {
        return SVG_VERSION;
    }

    protected String getSvgBaseProfile() {
        return SVG_BASE_PROFILE;
    }

    @Override
    protected void writeClipAttributes(@NonNull XMLStreamWriter w, @NonNull Node node) throws XMLStreamException {
        Node clip = node.getClip();
        if (clip == null) {
            return;
        }

        String id = idFactory.getId(clip);
        if (id != null) {
            w.writeAttribute("clip-path", "url(#" + id + ")");
        } else {
            System.err.println("WARNING SvgExporter does not support recursive clips!");
        }
    }

    @Override
    protected void writeClipPathDefs(@NonNull XMLStreamWriter w, @NonNull Node node) throws IOException, XMLStreamException {
        // FIXME clip nodes can in turn have clips - we need to support recursive calls to defsNode!!!
        Node clip = node.getClip();
        if (clip == null) {
            return;
        }
        if (idFactory.getId(clip) == null) {
            String id = idFactory.createId(clip, "clipPath");
            w.writeStartElement("clipPath");
            w.writeAttribute("id", id);
            writeNodeRecursively(w, clip, 2);
            w.writeEndElement();
        }
    }

    protected void writeCompositingAttributes(@NonNull XMLStreamWriter w, @NonNull Node
            node) throws XMLStreamException {
        if (node.getOpacity() != 1.0) {
            w.writeAttribute("opacity", nb.toString(node.getOpacity()));
        }
        /*
        if (node.getBlendMode() != null && node.getBlendMode() != BlendMode.SRC_OVER) {
        switch (node.getBlendMode()) {
        case MULTIPLY:
        case SCREEN:
        case DARKEN:
        case LIGHTEN:
        elem.setAttribute("mode", node.getBlendMode().toString().toLowerCase());
        break;
        default:
        // ignore
        }
        }*/
    }

    @Override
    protected List<String> getAdditionalNodeClasses(@NonNull Node node) {
        String typeSelector = (String) node.getProperties().get(StyleableFigure.TYPE_SELECTOR_NODE_KEY);
        if (typeSelector != null) {
            return Collections.singletonList(typeSelector);
        }
        return Collections.emptyList();
    }

}

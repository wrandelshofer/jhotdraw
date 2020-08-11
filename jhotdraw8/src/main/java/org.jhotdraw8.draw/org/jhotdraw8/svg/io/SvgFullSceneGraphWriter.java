/*
 * @(#)SvgFullSceneGraphExporter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.io;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.figure.StyleableFigure;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Exports a JavaFX scene graph to SVG 1.1 Full.
 * <p>
 * References:<br>
 * <a href="https://www.w3.org/TR/SVG11/">SVG 1.1</a>
 *
 * @author Werner Randelshofer
 */
public class SvgFullSceneGraphWriter extends AbstractSvgSceneGraphWriter {
    private final static String SVG_VERSION = "1.1";
    private final static String SVG_BASE_PROFILE = "full";
    public final static String SVG_MIME_TYPE_WITH_VERSION = SVG_MIME_TYPE + ";version=\"" + SVG_VERSION + "\"";

    /**
     * @param imageUriKey this property is used to retrieve an URL from an
     *                    ImageView
     * @param skipKey     this property is used to retrieve a Boolean from a Node.
     */
    public SvgFullSceneGraphWriter(Object imageUriKey, Object skipKey) {
        super(imageUriKey, skipKey);
    }

    protected void writeDocumentElementAttributes(@NonNull XMLStreamWriter
                                                          w, Node drawingNode) throws XMLStreamException {
        w.writeAttribute("version", getSvgVersion());
        w.writeAttribute("baseProfile", getSvgBaseProfile());
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
            writeNodeRecursively(w, clip, 2);
            w.writeAttribute("id", id);
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

/*
 * @(#)SvgExporter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.io;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;

/**
 * Exports a JavaFX scene graph to SVG 1.1 Full.
 * <p>
 * References:<br>
 * <a href="https://www.w3.org/TR/SVG11/">SVG 1.1</a>
 *
 * @author Werner Randelshofer
 */
public class SvgFullSceneGraphExporter extends AbstractSvgSceneGraphExporter {
    private final static String SVG_VERSION = "1.1";
    private final static String SVG_BASE_PROFILE = "full";
    public final static String SVG_MIME_TYPE_WITH_VERSION = SVG_MIME_TYPE + ";version=\"" + SVG_VERSION + "\"";

    /**
     * @param imageUriKey this property is used to retrieve an URL from an
     *                    ImageView
     * @param skipKey     this property is used to retrieve a Boolean from a Node.
     */
    public SvgFullSceneGraphExporter(Object imageUriKey, Object skipKey) {
        super(imageUriKey, skipKey);
    }

    protected void writeDocumentElementAttributes(@NonNull Element
                                                          docElement, javafx.scene.Node drawingNode) {
        docElement.setAttributeNS(XMLNS_NS, "xmlns:" + XLINK_Q, XLINK_NS);
        docElement.setAttribute("version", getSvgVersion());
        docElement.setAttribute("baseProfile", getSvgBaseProfile());
    }

    protected String getSvgVersion() {
        return SVG_VERSION;
    }

    protected String getSvgBaseProfile() {
        return SVG_BASE_PROFILE;
    }

    @Override
    protected void writeClipAttributes(@NonNull Element elem, @NonNull Node node) {
        Node clip = node.getClip();
        if (clip == null) {
            return;
        }

        String id = idFactory.getId(clip);
        if (id != null) {
            elem.setAttribute("clip-path", "url(#" + id + ")");
        } else {
            System.err.println("WARNING SvgExporter does not supported recursive clips!");
        }
    }

    @Override
    protected void writeClipPathDefs(@NonNull Document doc, @NonNull Element
            defsNode, @NonNull Node node) throws IOException {
        // FIXME clip nodes can in turn have clips - we need to support recursive calls to defsNode!!!
        Node clip = node.getClip();
        if (clip == null) {
            return;
        }
        if (idFactory.getId(clip) == null) {
            String id = idFactory.createId(clip, "clipPath");
            Element elem = doc.createElement("clipPath");
            writeNodeRecursively(doc, elem, clip);
            elem.setAttribute("id", id);
            defsNode.appendChild(elem);
        }
    }

    protected void writeCompositingAttributes(@NonNull Element elem, @NonNull Node
            node) {
        if (node.getOpacity() != 1.0) {
            elem.setAttribute("opacity", nb.toString(node.getOpacity()));
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

}

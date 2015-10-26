/* @(#)SimpleDrawing.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.io.IOException;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.css.StyleableStyleManager;
import org.jhotdraw.css.CssParser;

/**
 * SimpleDrawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawing extends AbstractCompositeFigure implements Drawing {

    /**
     * The style manager is created lazily. If the stylesheet property is
     * changed, the style manager is set to null again.
     */
    private StyleableStyleManager styleManager = null;

    public SimpleDrawing() {
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        Rectangle background = new Rectangle();
        background.setId("background");
        g.getProperties().put("background", background);
        return g;
    }

    @Override
    public void updateNode(RenderContext v, Node n) {
        Group g = (Group) n;
        ObservableList<Node> children = ((Group) n).getChildren();
        children.clear();
        Bounds bounds = getBoundsInLocal();
        Rectangle page = (Rectangle) g.getProperties().get("background");
        page.setX(bounds.getMinX());
        page.setY(bounds.getMinY());
        page.setWidth(bounds.getWidth());
        page.setHeight(bounds.getHeight());
        page.setFill(getStyled(BACKGROUND));
        children.add(page);

        for (Figure child : childrenProperty()) {
            children.add(v.getNode(child));
        }
    }

    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(0.0, 0.0, get(WIDTH), get(HEIGHT));

    }

    @Override
    public void reshape(Transform transform) {
        for (Figure child : childrenProperty()) {
            child.reshape(transform);
        }
    }

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public StyleableStyleManager getStyleManager() {
        if (styleManager == null) {
            styleManager = new StyleableStyleManager();
            try {
                styleManager.updateStylesheets(StyleOrigin.USER_AGENT, get(DOCUMENT_HOME),get(USER_AGENT_STYLESHEETS));
                styleManager.updateStylesheets(StyleOrigin.AUTHOR, get(DOCUMENT_HOME),get(AUTHOR_STYLESHEETS));
            } catch (IOException ex) {
                System.err.println("Warning could not load user agent stylesheets.");
                ex.printStackTrace();
            }
        }
        return styleManager;
    }

    @Override
    public void stylesheetNotify() {
        if (styleManager != null) {
            try {
                styleManager.updateStylesheets(StyleOrigin.USER_AGENT, get(DOCUMENT_HOME),get(USER_AGENT_STYLESHEETS));
                styleManager.updateStylesheets(StyleOrigin.AUTHOR, get(DOCUMENT_HOME),get(AUTHOR_STYLESHEETS));
            } catch (IOException ex) {
                System.err.println("Warning could not load user agent stylesheets.");
                ex.printStackTrace();
            }
        }
        super.stylesheetNotify();
    }
}

/* @(#)DocumentStyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.css.StyleOrigin;
import org.jhotdraw.css.ast.Declaration;
import org.jhotdraw.css.ast.StyleRule;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.draw.figure.Figure;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * DocumentStyleManager applies styling rules to a {@code Document} or to an
 * individual {@code Element} of a document.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DocumentStyleManager extends AbstractStyleManager<Element> {

    private final DocumentSelectorModel selectorModel = new DocumentSelectorModel();

    private final CssParser parser = new CssParser();

    public DocumentStyleManager() {
    }

    public void applyStylesRecursively(Element elem) {
        NodeList list = elem.getElementsByTagName("*");
        for (int i = 0, n = list.getLength(); i < n; i++) {
            applyStylesTo((Element) list.item(i));
        }
    }

    @Override
    public void applyStylesTo(Element elem, Map<String, Set<Element>> pseudoClassStates) {
        selectorModel.additionalPseudoClassStatesProperty().putAll(pseudoClassStates);
        HashMap<String, String> applicableDeclarations = new HashMap<>();

        // user agent stylesheets can not override element attributes
        for (MyEntry e : getUserAgentStylesheets()) {
            Stylesheet s = e.getStylesheet();
            if (s == null) {
                continue;
            }
            for (StyleRule r : s.getStyleRules()) {
                if (r.getSelectorGroup().matches(selectorModel, elem)) {
                    for (Declaration d : r.getDeclarations()) {
                        // Declarations without terms are ignored
                        if (d.getTerms().isEmpty()) {
                            continue;
                        }

                        if (!elem.hasAttribute(d.getProperty())) {
                            applicableDeclarations.put(d.getProperty(), d.getTermsAsString());
                        }
                    }
                }
            }
        }

        // author stylesheets override user agent stylesheet and element attributes
        for (MyEntry e : getAuthorStylesheets()) {
            Stylesheet s = e.getStylesheet();
            if (s == null) {
                continue;
            }
            for (StyleRule r : s.getStyleRules()) {
                if (r.getSelectorGroup().matches(selectorModel, elem)) {
                    for (Declaration d : r.getDeclarations()) {
                        // Declarations without terms are ignored
                        if (d.getTerms().isEmpty()) {
                            continue;
                        }

                        applicableDeclarations.put(d.getProperty(), d.getTermsAsString());
                    }
                }

            }
        }
        // inline stylesheets override user agent stylesheet, element attributes and author stylesheets
        for (MyEntry e : getInlineStylesheets()) {
            Stylesheet s = e.getStylesheet();
            if (s == null) {
                continue;
            }
            for (StyleRule r : s.getStyleRules()) {
                if (r.getSelectorGroup().matches(selectorModel, elem)) {
                    for (Declaration d : r.getDeclarations()) {
                        // Declarations without terms are ignored
                        if (d.getTerms().isEmpty()) {
                            continue;
                        }

                        applicableDeclarations.put(d.getProperty(), d.getTermsAsString());
                    }
                }

            }
        }

        // inline styles can override all other values
        if (elem.hasAttribute("style")) {
            try {
                for (Declaration d : parser.parseDeclarationList(elem.getAttribute("style"))) {
                    // Declarations without terms are ignored
                    if (d.getTerms().isEmpty()) {
                        continue;
                    }

                    applicableDeclarations.put(d.getProperty(), d.getTermsAsString());
                }
            } catch (IOException ex) {
                System.err.println("DOMStyleManager: Invalid style attribute on element. style=" + elem.getAttribute("style"));
                ex.printStackTrace();
            }
        }

        for (Map.Entry<String, String> entry : applicableDeclarations.entrySet()) {
            elem.setAttribute(entry.getKey(), entry.getValue());
        }
        applicableDeclarations.clear();

        selectorModel.additionalPseudoClassStatesProperty().clear();
    }

    @Override
    public DocumentSelectorModel getSelectorModel() {
        return selectorModel;
    }

    @Override
    public void applyStylesheetTo(StyleOrigin styleOrigin, Stylesheet s, Figure f, HashMap<String, Set<Figure>> pseudoStyles) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

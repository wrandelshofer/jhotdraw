/* @(#)SimpleStylesheetsManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import javafx.css.StyleOrigin;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StyleableMapAccessor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * SimpleStylesheetsManager.
 *
 * @author Werner Randelshofer
 * @version $Id: SimpleStylesheetsManager.java 1120 2016-01-15 17:37:49Z
 rawcoder $
 * @param <E> the element type that can be styled by this style manager
 */
public class SimpleStylesheetsManager<E> implements StylesheetsManager<E> {

    private final CssParser parser = new CssParser();
    private SelectorModel<E> selectorModel;

    public SimpleStylesheetsManager(SelectorModel<E> selectorModel) {
        this.selectorModel = selectorModel;
    }
    
    public void getSelectorModel(SelectorModel<E> newValue) {
        selectorModel = newValue;
    }
    @Override
    public SelectorModel<E> getSelectorModel() {
        return selectorModel;
    }

    protected class ParsedStylesheetEntry {

        private StyleOrigin origin;
        private FutureTask<Stylesheet> future;

        public ParsedStylesheetEntry(StyleOrigin origin, URI uri) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                return p.parseStylesheet(uri);
            });
            executor.execute(future);
        }

        public ParsedStylesheetEntry(StyleOrigin origin, String str) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                return p.parseStylesheet(str);
            });
            executor.execute(future);
        }

        public StyleOrigin getOrigin() {
            return origin;
        }

        public Stylesheet getStylesheet() {
            try {
                return future.get();
            } catch (InterruptedException ex) {
                return null;
            } catch (ExecutionException ex) {
                return null;
            }
        }

    }

    /**
     * Cache for parsed user agent stylesheets.
     * <p>
     * The key is either an URI or a literal CSS String for which we cache the
     * data. The value contains the parsed stylesheet entry.
     */
    private LinkedHashMap<Object, ParsedStylesheetEntry> userAgentList = new LinkedHashMap<>();
    /**
     * @see #userAgentList
     */
    private LinkedHashMap<Object, ParsedStylesheetEntry> authorList = new LinkedHashMap<>();
    /**
     * @see #userAgentList
     */
    private LinkedHashMap<Object, ParsedStylesheetEntry> inlineList = new LinkedHashMap<>();

    private Executor executor = Executors.newCachedThreadPool();

    @Override
    public void addStylesheet(StyleOrigin origin, URI documentHome, URI uri) {
        URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
        getMap(origin).put(resolvedUri, new ParsedStylesheetEntry(origin, resolvedUri));
    }

    @Override
    public void addStylesheet(StyleOrigin origin, String str) {
        getMap(origin).put(str, new ParsedStylesheetEntry(origin, str));
    }

    @Override
    public void clearStylesheets(StyleOrigin origin) {
        if (origin == null) {
            authorList.clear();
            userAgentList.clear();
            inlineList.clear();
        } else {
            getMap(origin).clear();
        }
    }

    private LinkedHashMap<Object, ParsedStylesheetEntry> getMap(StyleOrigin origin) {
        switch (origin) {
            case AUTHOR:
                return authorList;
            case USER_AGENT:
                return userAgentList;
            case INLINE:
                return inlineList;
            default:
                throw new IllegalArgumentException("illegal origin:" + origin);
        }
    }

    private void setMap(StyleOrigin origin, LinkedHashMap<Object, ParsedStylesheetEntry> newValue) {
        switch (origin) {
            case AUTHOR:
                authorList = newValue;
                break;
            case USER_AGENT:
                userAgentList = newValue;
                break;
            case INLINE:
                inlineList = newValue;
                break;
            default:
                throw new IllegalArgumentException("illegal origin:" + origin);
        }
    }

    @Override
    public <T> void setStylesheets(StyleOrigin origin, URI documentHome, List<T> stylesheets) {
        LinkedHashMap<Object, ParsedStylesheetEntry> oldMap = getMap(origin);
        if (stylesheets == null) {
            oldMap.clear();
            return;
        }
        LinkedHashMap<Object, ParsedStylesheetEntry> newMap = new LinkedHashMap<>();
        for (T t : stylesheets) {
            if (t instanceof URI) {
                URI uri = (URI) t;
                URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
                ParsedStylesheetEntry old = oldMap.get(resolvedUri);
                if (false && old != null) { // XXX we always need to reload the file!
                    newMap.put(resolvedUri, old);
                } else {
                    newMap.put(resolvedUri, new ParsedStylesheetEntry(origin, resolvedUri));
                }
            } else if (t instanceof String) {
                ParsedStylesheetEntry old = oldMap.get(t);
                if (old != null) {
                    newMap.put(t, old);
                } else {
                    newMap.put(t, new ParsedStylesheetEntry(origin, (String) t));
                }
            } else {
                throw new IllegalArgumentException("illegal item " + t);
            }
        }
        setMap(origin, newMap);
    }

    protected Collection<ParsedStylesheetEntry> getAuthorStylesheets() {
        return authorList.values();
    }

    protected Collection<ParsedStylesheetEntry> getUserAgentStylesheets() {
        return userAgentList.values();
    }

    protected Collection<ParsedStylesheetEntry> getInlineStylesheets() {
        return inlineList.values();
    }

    @Override
    public void applyStylesheetsTo(E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();
        
        // The stylesheet is a user-agent stylesheet
        for (Declaration d : collectApplicableDeclarations(elem, getUserAgentStylesheets())) {
            selectorModel.setAttribute(elem, StyleOrigin.USER_AGENT, d.getProperty(),d.getTermsAsString());
        }

        // The value of a property was set by the user through a call to a set method
        // StyleOrigin.USER
        
        // The stylesheet is an external file
        for (Declaration d : collectApplicableDeclarations(elem, getAuthorStylesheets())) {
            selectorModel.setAttribute(elem, StyleOrigin.AUTHOR, d.getProperty(),d.getTermsAsString());
        }

        // The stylesheet is an internal file
        for (Declaration d : collectApplicableDeclarations(elem, getInlineStylesheets())) {
            selectorModel.setAttribute(elem, StyleOrigin.INLINE, d.getProperty(),d.getTermsAsString());
        }

        // 'inline style attributes' can override all other values
        HashMap<String, String> applicableDeclarations = new HashMap<>();
        if (selectorModel.hasAttribute(elem, "style")) {
            String styleValue = selectorModel.getAttribute(elem, "style");
            try {
                for (Declaration d : parser.parseDeclarationList(styleValue)) {
                    // Declarations without terms are ignored
                    if (d.getTerms().isEmpty()) {
                        continue;
                    }

                    applicableDeclarations.put(d.getProperty(), d.getTermsAsString());
                }
            } catch (IOException ex) {
                System.err.println("DOMStyleManager: Invalid style attribute on element. style=" + styleValue);
                ex.printStackTrace();
            }
        }
        for (Map.Entry<String, String> entry : applicableDeclarations.entrySet()) {
            selectorModel.setAttribute(elem, StyleOrigin.INLINE, entry.getKey(), entry.getValue());
        }
        applicableDeclarations.clear();
    }

    /**
     * Collects all declarations in all specified stylesheets which are applicable to the specified element.
     * 
     * @param elem an element
     * @param stylesheets the stylesheets
     * @return list of applicable declarations
     */
    private List<Declaration> collectApplicableDeclarations(E elem, Collection<ParsedStylesheetEntry> stylesheets) {
List<Map.Entry<Integer, Declaration>> applicableDeclarations = new LinkedList<>();
        for (ParsedStylesheetEntry e : stylesheets) {
            Stylesheet s = e.getStylesheet();
            if (s == null) {
                continue;
            }
            collectApplicableDeclarations(elem, s, applicableDeclarations);
        }
        
        return applicableDeclarations.stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    private  List<Map.Entry<Integer, Declaration>> collectApplicableDeclarations(E elem, Stylesheet s,  List<Map.Entry<Integer, Declaration>>  applicableDeclarations) {
        SelectorModel<E> selectorModel = getSelectorModel();
        for (StyleRule r : s.getStyleRules()) {
          Selector selector;
            if (null!=(selector=r.getSelectorGroup().match(selectorModel, elem))) {
                for (Declaration d : r.getDeclarations()) {
                    // Declarations without terms are ignored
                    if (d.getTerms().isEmpty()) {
                        continue;
                    }

                    applicableDeclarations.add(new AbstractMap.SimpleEntry<>(selector.getSpecificity(),d));
                }
            }
        }
        return applicableDeclarations;
    }

    @Override
    public void applyStylesheetTo(StyleOrigin styleOrigin, Stylesheet s, E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();
        for (Map.Entry<Integer, Declaration> entry : collectApplicableDeclarations(elem, s,
                new LinkedList<Map.Entry<Integer, Declaration>>())) {
          Declaration d = entry.getValue();
            selectorModel.setAttribute(elem, styleOrigin, d.getProperty(),d.getTermsAsString());
        }
    }
}

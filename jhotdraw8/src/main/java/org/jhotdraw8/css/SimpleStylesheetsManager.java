/* @(#)SimpleStylesheetsManager.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.io.IOException;
import java.net.URI;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import javafx.css.StyleOrigin;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;

/**
 * SimpleStylesheetsManager.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <E> the element type that can be styled by this style manager
 */
public class SimpleStylesheetsManager<E> implements StylesheetsManager<E> {

    private final CssParser parser = new CssParser();
    private SelectorModel<E> selectorModel;

    public SimpleStylesheetsManager(SelectorModel<E> selectorModel) {
        this.selectorModel = selectorModel;
    }

    private void doSetAttribute(SelectorModel<E> selectorModel1, E elem, StyleOrigin styleOrigin, String key, String value) {
        selectorModel1.setAttribute(elem, styleOrigin, key, value);
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
        @Nullable
        private FutureTask<Stylesheet> future;
        @Nullable
        private Stylesheet stylesheet;

        public ParsedStylesheetEntry(StyleOrigin origin, @Nonnull URI uri) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                return p.parseStylesheet(uri);
            });
            executor.execute(future);
        }

        public ParsedStylesheetEntry(StyleOrigin origin, @Nonnull String str) {
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

        @Nullable
        public Stylesheet getStylesheet() {
            if (future != null) {
                try {
                    stylesheet = future.get();
                    future = null;
                } catch (InterruptedException ex) {
                    // retry later
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                    stylesheet=null;
                    future = null;
                }
            }
            return stylesheet;
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

    @Nonnull
    private Executor executor = Executors.newCachedThreadPool();

    @Override
    public void addStylesheet(@Nonnull StyleOrigin origin, @Nullable URI documentHome, @Nonnull URI uri) {
        URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
        getMap(origin).put(resolvedUri, new ParsedStylesheetEntry(origin, resolvedUri));
    }

    @Override
    public void addStylesheet(@Nonnull StyleOrigin origin, @Nonnull String str) {
        getMap(origin).put(str, new ParsedStylesheetEntry(origin, str));
    }

    @Override
    public void clearStylesheets(@Nullable StyleOrigin origin) {
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
    public <T> void setStylesheets(@Nonnull StyleOrigin origin, @Nullable URI documentHome, @Nullable List<T> stylesheets) {
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

    @Nonnull
    protected Collection<ParsedStylesheetEntry> getAuthorStylesheets() {
        return authorList.values();
    }

    @Nonnull
    protected Collection<ParsedStylesheetEntry> getUserAgentStylesheets() {
        return userAgentList.values();
    }

    @Nonnull
    protected Collection<ParsedStylesheetEntry> getInlineStylesheets() {
        return inlineList.values();
    }

    @Override
    public void applyStylesheetsTo(E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();

        // The stylesheet is a user-agent stylesheet
        for (Declaration d : collectApplicableDeclarations(elem, getUserAgentStylesheets())) {
            doSetAttribute(selectorModel, elem, StyleOrigin.USER_AGENT, d.getProperty(), d.getTermsAsString());
        }

        // The value of a property was set by the user through a call to a set method with StyleOrigin.USER
        // ... nothing to do!
        // The stylesheet is an external file
        for (Declaration d : collectApplicableDeclarations(elem, getAuthorStylesheets())) {
            doSetAttribute(selectorModel, elem, StyleOrigin.AUTHOR, d.getProperty(), d.getTermsAsString());
        }

        // The stylesheet is an internal file
        for (Declaration d : collectApplicableDeclarations(elem, getInlineStylesheets())) {
            doSetAttribute(selectorModel, elem, StyleOrigin.INLINE, d.getProperty(), d.getTermsAsString());
        }

        // 'inline style attributes' can override all other values
        if (selectorModel.hasAttribute(elem, "style")) {
            Map<String, String> inlineDeclarations = new HashMap<>();
            String styleValue = selectorModel.getAttribute(elem, "style");
            try {
                for (Declaration d : parser.parseDeclarationList(styleValue)) {
                    // Declarations without terms are ignored
                    if (d.getTerms().isEmpty()) {
                        continue;
                    }

                    inlineDeclarations.put(d.getProperty(), d.getTermsAsString());
                }
            } catch (IOException ex) {
                System.err.println("DOMStyleManager: Invalid style attribute on element. style=" + styleValue);
                ex.printStackTrace();
            }
            for (Map.Entry<String, String> entry : inlineDeclarations.entrySet()) {
                doSetAttribute(selectorModel, elem, StyleOrigin.INLINE, entry.getKey(), entry.getValue());
            }
            inlineDeclarations.clear();
        }
    }

    /**
     * Collects all declarations in all specified stylesheets which are
     * applicable to the specified element.
     *
     * @param elem an element
     * @param stylesheets the stylesheets
     * @return list of applicable declarations
     */
    private List<Declaration> collectApplicableDeclarations(E elem, Collection<ParsedStylesheetEntry> stylesheets) {
        List<Map.Entry<Integer, Declaration>> applicableDeclarations = new ArrayList<>();
        for (ParsedStylesheetEntry e : stylesheets) {
            Stylesheet s = e.getStylesheet();
            if (s == null) {
                continue;
            }
            collectApplicableDeclarations(elem, s, applicableDeclarations);
        }

        return applicableDeclarations.stream().sorted(Map.Entry.comparingByKey()).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    @Nonnull
    private List<Map.Entry<Integer, Declaration>> collectApplicableDeclarations(E elem, Stylesheet s, @Nonnull List<Map.Entry<Integer, Declaration>> applicableDeclarations) {
        SelectorModel<E> selectorModel = getSelectorModel();
        for (StyleRule r : s.getStyleRules()) {
            Selector selector;
            if (null != (selector = r.getSelectorGroup().match(selectorModel, elem))) {
                for (Declaration d : r.getDeclarations()) {
                    // Declarations without terms are ignored
                    if (d.getTerms().isEmpty()) {
                        continue;
                    }

                    applicableDeclarations.add(new AbstractMap.SimpleEntry<>(selector.getSpecificity(), d));
                }
            }
        }
        return applicableDeclarations;
    }

    @Override
    public boolean applyStylesheetTo(StyleOrigin styleOrigin, @Nonnull Stylesheet s, E elem) {
        SelectorModel<E> selectorModel = getSelectorModel();
        final List<Map.Entry<Integer, Declaration>> applicableDeclarations = collectApplicableDeclarations(elem, s,
                new ArrayList<Map.Entry<Integer, Declaration>>());
        if (applicableDeclarations.isEmpty()) {
            return false;
        }
        for (Map.Entry<Integer, Declaration> entry : applicableDeclarations) {
            Declaration d = entry.getValue();
            selectorModel.setAttribute(elem, styleOrigin, d.getProperty(), d.getTermsAsString());
        }
        return true;
    }
}

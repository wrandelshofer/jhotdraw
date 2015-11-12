/*
 * @(#)AbstractStyleManager.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.css;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javafx.css.StyleOrigin;
import org.jhotdraw.css.ast.Stylesheet;

/**
 * AbstractStyleManager.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public abstract class AbstractStyleManager<E> implements StyleManager<E> {

    protected class Entry {

        private StyleOrigin origin;
        private FutureTask<Stylesheet> future;

        public Entry(StyleOrigin origin, URI uri) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                return p.parseStylesheet(uri);
            });
            executor.execute(future);
        }

        public Entry(StyleOrigin origin, String str) {
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
    private LinkedHashMap<Object, Entry> userAgentList = new LinkedHashMap<>();
    private LinkedHashMap<Object, Entry> authorList = new LinkedHashMap<>();
    private LinkedHashMap<Object, Entry> inlineList = new LinkedHashMap<>();

    private Executor executor = Executors.newCachedThreadPool();

    @Override
    public void addStylesheet(StyleOrigin origin, URI documentHome, URI uri) {
        URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
        getMap(origin).put(resolvedUri, new Entry(origin, resolvedUri));
    }

    @Override
    public void addStylesheet(StyleOrigin origin, String str) {
        getMap(origin).put(str, new Entry(origin, str));
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

    private LinkedHashMap<Object, Entry> getMap(StyleOrigin origin) {
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

    private void setMap(StyleOrigin origin, LinkedHashMap<Object, Entry> newValue) {
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
        LinkedHashMap<Object, Entry> oldMap = getMap(origin);
        if (stylesheets == null) {
            oldMap.clear();
            return;
        }
        LinkedHashMap<Object, Entry> newMap = new LinkedHashMap<>();
        for (T t : stylesheets) {
            if (t instanceof URI) {
                URI uri = (URI) t;
                URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
                Entry old = oldMap.get(resolvedUri);
                if (old != null) {
                    newMap.put(resolvedUri, old);
                } else {
                    newMap.put(resolvedUri, new Entry(origin, resolvedUri));
                }
            } else if (t instanceof String) {
                Entry old = oldMap.get(t);
                if (old != null) {
                    newMap.put(t, old);
                } else {
                    newMap.put(t, new Entry(origin, (String) t));
                }
            } else {
                throw new IllegalArgumentException("illegal item " + t);
            }
        }
        setMap(origin, newMap);
    }

    protected Collection<Entry> getAuthorStylesheets() {
        return authorList.values();
    }

    protected Collection<Entry> getUserAgentStylesheets() {
        return userAgentList.values();
    }

    protected Collection<Entry> getInlineStylesheets() {
        return inlineList.values();
    }
}

/* @(#)AbstractStyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
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
 * @version $Id$
 */
public abstract class AbstractStyleManager<E> implements StyleManager<E> {

    protected class MyEntry {

        private StyleOrigin origin;
        private FutureTask<Stylesheet> future;

        public MyEntry(StyleOrigin origin, URI uri) {
            this.origin = origin;
            this.future = new FutureTask<>(() -> {
                CssParser p = new CssParser();
                return p.parseStylesheet(uri);
            });
            executor.execute(future);
        }

        public MyEntry(StyleOrigin origin, String str) {
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
    private LinkedHashMap<Object, MyEntry> userAgentList = new LinkedHashMap<>();
    private LinkedHashMap<Object, MyEntry> authorList = new LinkedHashMap<>();
    private LinkedHashMap<Object, MyEntry> inlineList = new LinkedHashMap<>();

    private Executor executor = Executors.newCachedThreadPool();

    @Override
    public void addStylesheet(StyleOrigin origin, URI documentHome, URI uri) {
        URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
        getMap(origin).put(resolvedUri, new MyEntry(origin, resolvedUri));
    }

    @Override
    public void addStylesheet(StyleOrigin origin, String str) {
        getMap(origin).put(str, new MyEntry(origin, str));
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

    private LinkedHashMap<Object, MyEntry> getMap(StyleOrigin origin) {
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

    private void setMap(StyleOrigin origin, LinkedHashMap<Object, MyEntry> newValue) {
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
        LinkedHashMap<Object, MyEntry> oldMap = getMap(origin);
        if (stylesheets == null) {
            oldMap.clear();
            return;
        }
        LinkedHashMap<Object, MyEntry> newMap = new LinkedHashMap<>();
        for (T t : stylesheets) {
            if (t instanceof URI) {
                URI uri = (URI) t;
                URI resolvedUri = documentHome == null ? uri : documentHome.resolve(uri);
                MyEntry old = oldMap.get(resolvedUri);
                if (false && old != null) { // XXX we always need to reload the file!
                    newMap.put(resolvedUri, old);
                } else {
                    newMap.put(resolvedUri, new MyEntry(origin, resolvedUri));
                }
            } else if (t instanceof String) {
                MyEntry old = oldMap.get(t);
                if (old != null) {
                    newMap.put(t, old);
                } else {
                    newMap.put(t, new MyEntry(origin, (String) t));
                }
            } else {
                throw new IllegalArgumentException("illegal item " + t);
            }
        }
        setMap(origin, newMap);
    }

    protected Collection<MyEntry> getAuthorStylesheets() {
        return authorList.values();
    }

    protected Collection<MyEntry> getUserAgentStylesheets() {
        return userAgentList.values();
    }

    protected Collection<MyEntry> getInlineStylesheets() {
        return inlineList.values();
    }
}

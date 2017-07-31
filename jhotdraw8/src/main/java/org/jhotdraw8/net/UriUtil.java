/*
 * @(#)UriUtil.java
 * 
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 * 
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.net;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UriUtil.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class UriUtil {

    /**
     * Prevent instance creation.
     */
    private void URIUtil() {
    }

    /**
     * Returns the name of an URI for display in the title bar of a window.
     *
     * @param uri the uri
     * @return the name
     */
    public static String getName(URI uri) {
        if (uri.getScheme() != null && "file".equals(uri.getScheme())) {
            File file = new File(clearQuery(uri));
            return file.getName() + " [" + file.getPath() + "]";
        }
        return uri.toString();
    }

    /**
     * Adds a query. If a query is already present, adds it after a {@literal '&'}
     * character. Both, the key, and the value may not include the characters
     * {@literal '&'} and '='.
     *
     * @param uri an uri
     * @param key the key
     * @param value the value
     * @return the updated query
     */
    public static URI addQuery(URI uri, String key, String value) {
        if (key == null || value == null) {
            return uri;
        }
        if (key.indexOf('=') != -1) {
            throw new IllegalArgumentException("key:" + key);
        }
        if (value.indexOf('=') != -1) {
            throw new IllegalArgumentException("value:" + value);
        }

        return addQuery(uri, key + '=' + value);
    }

    /**
     * Adds a query. If a query is already present, adds it after a {@literal '&'}
     * character. The query may not include the character {@literal '&'}.
     *
     * @param uri an uri
     * @param query the query
     * @return the updated query
     */
    public static URI addQuery(URI uri, String query) {
        if (query == null) {
            return uri;
        }
        if (query.indexOf('&') != -1) {
            throw new IllegalArgumentException("query:" + query);
        }

        String oldQuery = uri.getQuery();
        String newQuery = oldQuery == null ? query : oldQuery + "&" + query;

        return setQuery(uri, newQuery);
    }

    /**
     * Sets the query on the specified URI. If a query is already present, it is
     * removed.
     *
     * @param uri an uri
     * @param query the query
     * @return the update uri
     */
    public static URI setQuery(URI uri, String query) {
        URI u = uri;
        try {
            u = new URI(u.getScheme(),
                    u.getUserInfo(), u.getHost(), u.getPort(),
                    u.getPath(), query,
                    u.getFragment());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return u;
    }

    public static URI clearQuery(URI uri) {

        return setQuery(uri, null);
    }

    /**
     * Parses the query of the URI. Assumes that the query consists of
     * {@literal '&'}-separated, key '=' value pairs.
     *
     * @param uri an URI
     * @return a map
     */
    public static Map<String, String> parseQuery(URI uri) {
        String query = uri.getQuery();
        Map<String, String> map = new LinkedHashMap<>();
        if (query != null) {
            for (String pair : query.split("&")) {
                int p = pair.indexOf('=');
                String key = pair.substring(0, p);
                String value = pair.substring(p + 1);
                map.put(key, value);
            }
        }
        return map;
    }
}

/*
 * @(#)SimpleIdFactory.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * SimpleIdFactory.
 *
 * @author Werner Randelshofer
 */
public class SimpleIdFactory implements IdFactory {
    private final @NonNull Map<String, Long> prefixToNextId = new HashMap<>(128,0.4f);
    private final @NonNull Map<String, Object> idToObject = new HashMap<>(128,0.4f);
    private final @NonNull Map<Object, String> objectToId = new HashMap<>(128,0.4f);

    @Override
    public void reset() {
        prefixToNextId.clear();
        idToObject.clear();
        objectToId.clear();
    }

    @Override
    public @Nullable String createId(Object object) {
        return createId(object, "");
    }

    @Override
    public String getId(Object object) {
        return objectToId.get(object);
    }

    @Nullable
    private URI documentHome;

    public void setDocumentHome(@Nullable URI documentHome) {
        this.documentHome=documentHome;
    }

    @Override
    public @NonNull URI relativize(@NonNull URI uri) {
        return documentHome==null?uri:UriResolver.relativize(documentHome,uri);
    }

    @Override
    public Object getObject(String id) {
        return idToObject.get(id);
    }

    @Override
    public @NonNull URI absolutize(@NonNull URI uri) {
        return documentHome==null?uri:UriResolver.absolutize(documentHome,uri);
    }

    public Object putIdAndObject(String id, Object object) {
        String oldId = objectToId.put(object, id);
        if (oldId != null) {
            idToObject.remove(oldId);
        }
        Object oldObject = idToObject.put(id, object);
        if (oldObject != null) {
            objectToId.remove(oldObject);
        }
        return oldObject;
    }

    public Object putIdToObject(String id, Object object) {
        Object oldObject = idToObject.put(id, object);
        if (oldObject != null) {
            objectToId.remove(oldObject);
        }
        return oldObject;
    }

    public String createId(Object object, @Nullable String prefix) {
        String id = objectToId.get(object);
        if (id == null) {
            long pNextId = prefixToNextId.getOrDefault(prefix, 1L);

            do { // XXX linear search
                id = (prefix == null ? "" : prefix) + pNextId++;
            } while (idToObject.containsKey(id));
            objectToId.put(object, id);
            idToObject.put(id, object);
            prefixToNextId.put(prefix, pNextId);
        }
        return id;
    }

    public @Nullable String createId(Object object, @Nullable String prefix, @Nullable String suggestedId) {
        String existingId = objectToId.get(object);
        if (existingId == null) {
            if (suggestedId != null && !idToObject.containsKey(suggestedId)) {
                existingId = suggestedId;
            } else {
                long pNextId = prefixToNextId.getOrDefault(prefix, 1L);

                do { // XXX linear search
                    existingId = (prefix == null ? "" : prefix) + pNextId++;
                } while (idToObject.containsKey(existingId));
                prefixToNextId.put(prefix, pNextId);
            }
            objectToId.put(object, existingId);
            idToObject.put(existingId, object);
        } else {

        }
        return existingId;
    }
}

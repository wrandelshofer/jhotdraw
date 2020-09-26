/*
 * @(#)SimpleIdFactory.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * SimpleIdFactory.
 *
 * @author Werner Randelshofer
 */
public class SimpleIdFactory implements IdFactory {
    // inv:
    // idToObject.size() == objectToId.size();

    private final @NonNull Map<String, Long> prefixToNextId = new HashMap<>();
    private final @NonNull Map<String, Object> idToObject = new HashMap<>();
    private final @NonNull Map<Object, String> objectToId = new HashMap<>();

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

    @Override
    public Object getObject(String id) {
        return idToObject.get(id);
    }

    public Object putId(String id, Object object) {
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

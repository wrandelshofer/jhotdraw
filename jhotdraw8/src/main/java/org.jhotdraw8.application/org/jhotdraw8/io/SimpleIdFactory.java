/*
 * @(#)SimpleIdFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * SimpleIdFactory.
 *
 * @author Werner Randelshofer
 */
public class SimpleIdFactory implements IdFactory {

    @Nonnull
    private Map<String, Long> prefixToNextId = new HashMap<>();
    @Nonnull
    private Map<String, Object> idToObject = new HashMap<>();
    @Nonnull
    private Map<Object, String> objectToId = new HashMap<>();

    @Override
    public void reset() {
        prefixToNextId.clear();
        idToObject.clear();
        objectToId.clear();
    }

    @Override
    public String createId(Object object) {
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

    public void putId(String id, Object object) {
        String oldId = objectToId.put(object, id);
        if (oldId != null) {
            idToObject.remove(oldId);
        }
        Object oldObject = idToObject.put(id, object);
        if (oldObject != null) {
            objectToId.remove(oldObject);
        }
    }

    public String createId(Object object, @Nullable String prefix) {
        String id = objectToId.get(object);
        if (id == null) {
            long pNextId = prefixToNextId.getOrDefault(prefix, 1L);

            do { // XXX linear search
                id = (prefix == null ? "" : prefix) + Long.toString(pNextId++);
            } while (idToObject.containsKey(id));
            objectToId.put(object, id);
            idToObject.put(id, object);
            prefixToNextId.put(prefix, pNextId);
        }
        return id;
    }

    @Nullable
    public String createId(Object object, @Nullable String prefix, @Nullable String idx) {
        String existingId = objectToId.get(object);
        if (existingId == null) {
            if (idx != null && !idToObject.containsKey(idx)) {
                existingId = idx;
            } else {
                long pNextId = prefixToNextId.getOrDefault(prefix, 1L);

                do { // XXX linear search
                    existingId = (prefix == null ? "" : prefix) + Long.toString(pNextId++);
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

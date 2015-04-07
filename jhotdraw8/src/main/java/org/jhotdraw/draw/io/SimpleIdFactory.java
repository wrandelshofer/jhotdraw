/* @(#)SimpleIdFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.io.IOException;
import java.util.HashMap;
import org.jhotdraw.draw.Figure;

/**
 * SimpleIdFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleIdFactory implements IdFactory {

    private long nextId;

    private HashMap<String, Object> idToObject = new HashMap<>();
    private HashMap<Object, String> objectToId = new HashMap<>();

    @Override
    public void reset() {
        nextId = 0;
        idToObject.clear();
        objectToId.clear();
    }

    @Override
    public String createId(Object object) {
        String id = objectToId.get(object);
        if (id == null) {
            id = Long.toString(nextId++);
            objectToId.put(object, id);
            idToObject.put(id, object);
        }
        return id;
    }

    @Override
    public String getId(Object object) {
        return objectToId.get(object);
    }

    @Override
    public Object getObject(String id) {
        return idToObject.get(id);
    }

    public void putId(Object object, String id) {
        String oldId = objectToId.put(object, id);
        if (oldId != null) {
            idToObject.remove(oldId);
        }
        Object oldObject = idToObject.put(id, object);
        if (oldObject != null) {
            objectToId.remove(oldObject);
        }
    }

}

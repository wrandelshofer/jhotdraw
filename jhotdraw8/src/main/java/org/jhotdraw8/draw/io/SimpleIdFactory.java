/* @(#)SimpleIdFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.util.HashMap;
import java.util.Map;

/**
 * SimpleIdFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleIdFactory implements IdFactory {

    private Map<String, Long> prefixToNextId = new HashMap<>();
    private Map<String, Object> idToObject = new HashMap<>();
    private Map<Object, String> objectToId = new HashMap<>();

    @Override
    public void reset() {
        prefixToNextId.clear();
        idToObject.clear();
        objectToId.clear();
    }

    @Override
    public String createId(Object object) {
      return createId(object,"");
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

  String createId(Object object, String prefix) {
        String id = objectToId.get(object);
        if (id == null) {
            long pNextId=prefixToNextId.getOrDefault(prefix, 1L);
            
            do { // XXX linear search
                id = prefix+Long.toString(pNextId++);
            } while (idToObject.containsKey(id));
            objectToId.put(object, id);
            idToObject.put(id, object);
            prefixToNextId.put(prefix, pNextId);
        }
        return id;
  }

}

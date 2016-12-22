/* @(#)IdFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.io;

import java.util.Objects;

/**
 * IdFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface IdFactory extends UnitConverter {


    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @return the id
     */
    default String createId(Object object) {
        return createId(object, "");
    }

    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @param prefix the desired prefix for the id
     * @return the id
     */
    public String createId(Object object, String prefix);


     /**
     * Gets an id for the specified object. Returns null if the object has no
     * id.
     *
     * @param object the object
     * @return the id
     */
    public String getId(Object object);
    /**
     * Gets the object for the specified id. Returns null if the id has no
     * object.
     *
     * @param id the id
     * @return the object
     */
    public Object getObject(String id);
    /**
     * Puts an id for the specified object. If the object already has an id, the
     * old id is replaced.
     *
     * @param object the object
     * @param id the id
     */
    public void putId(Object object, String id);
    /**
     * Clears all ids.
     */
    public void reset();
}

/* @(#)IdFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.io;

/**
 * IdFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface IdFactory {

    /** Clears all ids. */
    public void reset();

    /** Creates an id for the specified object. 
     * If the object already has an id, then that id is returned.
     */
    public String createId(Object object);

    /** Gets an id for the specified object. 
     * Returns null if the object has no id.
     */
    public String getId(Object object);

    /** Gets the object for the specified id.
     * Returns null if the id has no object.
     */
    public Object getObject(String id);

    /** Puts an id for the specified object.
     * If the object already has an id, the old id is replaced.
     */
    public void putId(Object object, String id);

}

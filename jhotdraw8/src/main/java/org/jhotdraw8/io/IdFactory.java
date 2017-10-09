/* @(#)IdFactory.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

/**
 * IdFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface IdFactory {

    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @return the id
     */
    @Nonnull
    default String createId(@Nonnull Object object) {
        String id = createId(object, "");
        return id;
    }

    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @param prefix the desired prefix for the id
     * @return the id
     */
    public String createId(@Nonnull Object object, @Nullable String prefix);

    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @param prefix the prefix used to create a new id, if the desired id is
     * taken
     * @param id the desired id
     * @return the id
     */
    public String createId(@Nonnull Object object, @Nullable String prefix, @Nonnull String id);

    /**
     * Gets an id for the specified object. Returns null if the object has no
     * id.
     *
     * @param object the object
     * @return the id
     */
    @Nullable
    public String getId(@Nonnull Object object);

    /**
     * Gets the object for the specified id. Returns null if the id has no
     * object.
     *
     * @param id the id
     * @return the object
     */
    @Nullable
    public Object getObject(@Nonnull String id);

    /**
     * Puts an id for the specified object. If the object already has an id, the
     * old id is replaced.
     *
     * @param id the id
     * @param object the object
     */
    public void putId(@Nonnull String id, @Nullable Object object);

    /**
     * Clears all ids.
     */
    public void reset();
}

/*
 * @(#)IdFactory.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.net.URI;

/**
 * IdFactory.
 *
 * @author Werner Randelshofer
 */
public interface IdFactory extends IdResolver, IdSupplier {
void setDocumentHome(@Nullable URI documentHome);
    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned.
     *
     * @param object the object
     * @return the id
     */
    @Nullable
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
    String createId(Object object, @Nullable String prefix);

    /**
     * Creates an id for the specified object. If the object already has an id,
     * then that id is returned. If the object has no id and the suggestedId
     * has not been assigned to an object yet, then the suggestedId is
     * used.
     *
     * @param object      the object
     * @param prefix      the prefix used to create a new id, if the suggested id is
     *                    taken
     * @param suggestedId the suggested id
     * @return the id
     */
    @Nullable String createId(@NonNull Object object, @Nullable String prefix, String suggestedId);

    /**
     * Puts the id and the object for mapping between them.
     * <p>
     * If the object already has an id, the old id is replaced.
     *
     * @param id     the id
     * @param object the object
     * @return the object that previously was assigned to this id
     */
    @Nullable
    Object putIdAndObject(@NonNull String id, @NonNull Object object);

    /**
     * Puts the id for mapping to the specified object.
     *
     * @param id     the id
     * @param object the object
     * @return the object that previously was assigned to this id
     */
    @Nullable
    Object putIdToObject(@NonNull String id, @NonNull Object object);

    /**
     * Clears all ids.
     */
    void reset();
}

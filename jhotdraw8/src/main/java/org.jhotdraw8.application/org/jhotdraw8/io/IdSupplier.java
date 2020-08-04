package org.jhotdraw8.io;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

public interface IdSupplier {
    /**
     * Gets an id for the specified object. Returns null if the object has no
     * id.
     *
     * @param object the object
     * @return the id
     */
    @Nullable String getId(Object object);

    /**
     * Gets an id for the specified object. Throws an exception if the object has no
     * id.
     *
     * @param object the object
     * @return the id
     */
    default @NonNull String getIdNonNull(Object object) {
        return Objects.requireNonNull(getId(object));
    }
}

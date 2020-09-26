package org.jhotdraw8.io;

import org.jhotdraw8.annotation.Nullable;

public interface IdSupplier {
    /**
     * Gets an id for the specified object. Returns null if the object has no
     * id.
     *
     * @param object the object
     * @return the id
     */
    @Nullable String getId(Object object);

}

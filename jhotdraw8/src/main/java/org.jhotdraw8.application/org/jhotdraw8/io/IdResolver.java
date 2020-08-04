package org.jhotdraw8.io;

import org.jhotdraw8.annotation.Nullable;

public interface IdResolver {
    /**
     * Gets the object for the specified id. Returns null if the id has no
     * object.
     *
     * @param id the id
     * @return the object
     */
    @Nullable Object getObject(String id);


}

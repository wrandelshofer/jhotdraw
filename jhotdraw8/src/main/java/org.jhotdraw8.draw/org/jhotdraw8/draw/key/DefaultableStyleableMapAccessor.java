/*
 * @(#)DefaultableStyleableMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.key;

import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.css.CssDefaultableValue;

public interface DefaultableStyleableMapAccessor<T> extends NonNullMapAccessor<CssDefaultableValue<T>> {
    T getInitialValue();
}

/*
 * @(#)Picker.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;

import java.util.function.BiConsumer;

public interface Picker<T> {
    /**
     * @param anchor       anchor node will be blocked by the picker
     * @param screenX      desired screen coordinate
     * @param screenY      desired screen coordinate
     * @param initialValue initial value
     * @param callback     callback when a value was selected (true), or
     *                     reset to the initial value (false).
     */
    void show(Node anchor, double screenX, double screenY,
              T initialValue,
              BiConsumer<Boolean, T> callback);
}

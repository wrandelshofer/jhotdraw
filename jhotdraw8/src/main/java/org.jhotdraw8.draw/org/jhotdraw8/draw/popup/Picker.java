package org.jhotdraw8.draw.popup;

import javafx.scene.Node;

import java.util.function.BiConsumer;

public interface Picker<T> {
    /**
     * @param anchor       anchore node will be blocked by the picker
     * @param screenX      desired screen coordinate
     * @param screenY      desired screen cordinate
     * @param initialValue initial value
     * @param callback     selected value, the boolean indicates if
     *                     the selected value should be set (true) or
     *                     of the "initial" value should be set (false)
     */
    void show(Node anchor, double screenX, double screenY,
              T initialValue,
              BiConsumer<Boolean, T> callback);
}

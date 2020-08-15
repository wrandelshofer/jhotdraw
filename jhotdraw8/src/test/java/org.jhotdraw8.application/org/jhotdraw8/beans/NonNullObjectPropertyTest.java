/* @(#)NonNullPropertyTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.jupiter.api.Test;

import java.util.Objects;

/**
 * @author wr
 */
public class NonNullObjectPropertyTest {

    @Test
    public void testBind() {
        double minv = 0;
        double maxv = 1;

        NonNullObjectProperty<String> p1 = new NonNullObjectProperty<>(null, null, "hello");
        ObjectProperty<String> p2 = new SimpleObjectProperty<>(null);
        p1.addListener((o, oldv, newv) -> {
            Objects.requireNonNull(newv);
        });

        // must not set a null value
        p1.set(null);
        String s = p1.get();
        assert s != null;

    }
}

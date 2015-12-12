/* @(#)CustomBinding.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.binding;

import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;

/**
 * CustomBinding.
 * <p>
 * Slightly adapted code from example shown at stackoverflow.com.
 * <p>
 * Reference:
 * <a href="http://stackoverflow.com/questions/27052927/custom-bidirectional-bindings-in-javafx">
 * stackoverflow.com: Custom Bidirectional Bindings in JavaFX</a>
 *
 * @author BlackLabrador (as stated in the reference)
 * @version $$Id$$
 */
public class CustomBinding {

    public static <A, B> void bindBidirectional(Property<A> propertyA, Property<B> propertyB, Function<A, B> updateB, Function<B, A> updateA) {
        boolean[] alreadyCalled = new boolean[1];
        addFlaggedChangeListener(propertyA, propertyB, updateB,alreadyCalled);
        addFlaggedChangeListener(propertyB, propertyA, updateA,alreadyCalled);
    }

    public static <A, B> void bind(Property<A> propertyA, Property<B> propertyB, Function<A, B> updateB) {
        boolean[] alreadyCalled = new boolean[1];
        addFlaggedChangeListener(propertyA, propertyB, updateB,alreadyCalled);
    }

    private static <X, Y> void addFlaggedChangeListener(ObservableValue<X> propertyX, WritableValue<Y> propertyY, Function<X, Y> updateY,
            boolean[] alreadyCalled) {
        propertyX.addListener(new ChangeListener<X>() {
            @Override
            public void changed(ObservableValue<? extends X> observable, X oldValue, X newValue) {
                if (alreadyCalled[0]) {
                    return;
                }
                try {
                    alreadyCalled[0] = true;
                    propertyY.setValue(updateY.apply(newValue));
                } finally {
                    alreadyCalled[0] = false;
                }
            }
        });
    }
}

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
 * Provides bindings with conversion functions.
 * <p>
 * Slightly adapted code from example shown at stackoverflow.com.
 * <p>
 * Reference:
 * <a href="http://stackoverflow.com/questions/27052927/custom-bidirectional-bindings-in-javafx">
 * stackoverflow.com: Custom Bidirectional Bindings in JavaFX</a>
 *
 * @author BlackLabrador (as stated in the reference)
 * @version $Id$
 */
public class CustomBinding {

  /**
   * Creates a bidirectional binding for properties A and B using the conversion
   * functions updateB and updateA.
   *
   * @param <A> the type of property A
   * @param <B> the type of poperty B
   * @param propertyA property A
   * @param propertyB property B
   * @param updateB converts a value from A to B
   * @param updateA converts a value from B to A
   */
  public static <A, B, PROPERTY_A extends WritableValue<A> & ObservableValue<A>,
        PROPERTY_B extends WritableValue<B> & ObservableValue<B>> void bindBidirectional(PROPERTY_A propertyA, PROPERTY_B propertyB, Function<A, B> updateB, Function<B, A> updateA) {
    boolean[] alreadyCalled = new boolean[1];
    addFlaggedChangeListener(propertyA, propertyB, updateB, alreadyCalled);
    addFlaggedChangeListener(propertyB, propertyA, updateA, alreadyCalled);
  }

  /**
   * Binds writable value B to observable value A using the conversion function updateB.
   *
   * @param <A> the type of observable value A
   * @param <B> the type of observable value B
   * @param propertyA property A
   * @param propertyB property B
   * @param updateB converts a value from A to B
   */
  public static <A, B> void bind(ObservableValue<A> propertyA, WritableValue<B> propertyB, Function<A, B> updateB) {
    boolean[] alreadyCalled = new boolean[1];
    addFlaggedChangeListener(propertyA, propertyB, updateB, alreadyCalled);
  }

  private static <X, Y> void addFlaggedChangeListener(ObservableValue<X> propertyX, WritableValue<Y> propertyY, Function<X, Y> updateY,
          boolean[] alreadyCalled) {
    propertyX.addListener((observable, oldValue, newValue) -> {
      if (!alreadyCalled[0]) {
        try {
          alreadyCalled[0] = true;
          propertyY.setValue(updateY.apply(newValue));
        } finally {
          alreadyCalled[0] = false;
        }
      }
    }
    );
  }
}

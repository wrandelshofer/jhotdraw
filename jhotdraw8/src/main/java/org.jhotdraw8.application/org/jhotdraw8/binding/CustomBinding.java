/*
 * @(#)CustomBinding.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.binding;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.util.StringConverter;
import org.jhotdraw8.annotation.Nonnull;

import java.util.function.Function;

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
     * Binds property 'a' to property 'b'. Property b is provided by 'mediator'.
     *
     * @param <T>       the type of properties 'a' and 'b'
     * @param <M>       the type of the mediator property
     * @param propertyA property 'a'
     * @param mediator  the mediator property
     * @param propertyB property 'b'
     */
    public static <T, M> void bindBidirectional(
            @Nonnull Property<T> propertyA, @Nonnull Property<M> mediator, @Nonnull Function<M, Property<T>> propertyB) {

        final ChangeListener<M> changeListener = (o, oldv, newv) -> {
            if (oldv != null) {
                propertyA.unbindBidirectional(propertyB.apply(oldv));
            }
            if (newv != null) {
                propertyA.bindBidirectional(propertyB.apply(newv));
            }
        };
        changeListener.changed(mediator, null, null);
        mediator.addListener(changeListener);
    }

    /**
     * Binds property 'a' to property 'b'. Property b is provided by 'mediator'.
     *
     * @param <T>       the type of properties 'a' and 'b'
     * @param <M>       the type of the mediator property
     * @param propertyA property 'a'
     * @param mediator  the mediator property
     * @param propertyB property 'b'
     */
    public static <T, M> void bind(
            @Nonnull Property<T> propertyA, @Nonnull Property<M> mediator, @Nonnull Function<M, ObservableValue<T>> propertyB) {

        final ChangeListener<M> changeListener = (o, oldv, newv) -> {
            if (oldv != null) {
                propertyA.unbind();
            }
            if (newv != null) {
                propertyA.bind(propertyB.apply(newv));
            }
        };
        changeListener.changed(mediator, null, null);
        mediator.addListener(changeListener);
    }

    /**
     * Binds property 'a' to property 'b'. Property b is provided by 'mediator'.
     *
     * @param <T>             the value type of property 'b'
     * @param <S>             the value type of the mediator property
     * @param propertyA       property 'a'
     * @param mediator        the mediator property
     * @param propertyB       property 'b'
     * @param stringConverter the converter
     */
    public static <T, S> void bindBidirectional(@Nonnull StringProperty propertyA, @Nonnull Property<S> mediator, @Nonnull Function<S, Property<T>> propertyB,
                                                @Nonnull StringConverter<T> stringConverter) {
        final ChangeListener<S> changeListener = (o, oldv, newv) -> {
            if (oldv != null) {
                propertyA.unbindBidirectional(propertyB.apply(oldv));
            }
            if (newv != null) {
                propertyA.bindBidirectional(propertyB.apply(newv), stringConverter);
            }
        };
        changeListener.changed(mediator, null, null);
        mediator.addListener(changeListener);
    }


    /**
     * Creates a bidirectional binding for properties A and B using the
     * provided conversion functions.
     *
     * @param <A>          the type of value A
     * @param <B>          the type of value B
     * @param <PROPERTY_A> the type of property A
     * @param <PROPERTY_B> the type of property B
     * @param propertyA    property A
     * @param propertyB    property B
     * @param convertAtoB  converts a value from A to B
     * @param convertBtoA  converts a value from B to A
     */
    public static <A, B, PROPERTY_A extends WritableValue<A> & ObservableValue<A>, PROPERTY_B extends WritableValue<B> & ObservableValue<B>>
    void bindBidirectionalAndConvert(@Nonnull PROPERTY_A propertyA, @Nonnull PROPERTY_B propertyB, @Nonnull Function<A, B> convertAtoB, @Nonnull Function<B, A> convertBtoA) {
        boolean[] alreadyCalled = new boolean[1];
        addFlaggedChangeListener(propertyB, propertyA, convertAtoB, alreadyCalled);
        addFlaggedChangeListener(propertyA, propertyB, convertBtoA, alreadyCalled);
    }

    /**
     * Binds writable value A to observable value B using the conversion
     * function updateA.
     *
     * @param <B>       the type of observable value A
     * @param <A>       the type of observable value B
     * @param propertyB property A
     * @param propertyA property B
     * @param updateA   converts a value from B to A
     */
    public static <A, B> void bindAndConvert(@Nonnull WritableValue<A> propertyA, @Nonnull ObservableValue<B> propertyB, @Nonnull Function<B, A> updateA) {
        boolean[] alreadyCalled = new boolean[1];
        addFlaggedChangeListener(propertyA, propertyB, updateA, alreadyCalled);
    }

    private static <Y, X> void addFlaggedChangeListener(@Nonnull WritableValue<X> propertyX, ObservableValue<Y> propertyY, @Nonnull Function<Y, X> updateX,
                                                        boolean[] alreadyCalled) {
        propertyY.addListener((observable, oldValue, newValue) -> {
                    if (!alreadyCalled[0]) {
                        try {
                            alreadyCalled[0] = true;
                            propertyX.setValue(updateX.apply(newValue));
                        } finally {
                            alreadyCalled[0] = false;
                        }
                    }
                }
        );
    }

    /**
     * Returns a string expression which uses {@code java.test.MessageFormat} to
     * format the text. See {@link MessageStringFormatter} for special treatment
     * of boolean values.
     *
     * @param format The format string.
     * @param args   The arguments.
     * @return The string expression
     */
    public static StringExpression formatted(String format, Object... args) {
        return MessageStringFormatter.format(format, args);
    }
}

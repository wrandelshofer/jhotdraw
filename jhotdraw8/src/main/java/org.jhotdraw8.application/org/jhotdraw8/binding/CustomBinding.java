/*
 * @(#)CustomBinding.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.binding;

import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.util.StringConverter;
import org.jhotdraw8.annotation.NonNull;

import java.util.function.BiConsumer;
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
            @NonNull Property<T> propertyA, @NonNull Property<M> mediator, @NonNull Function<M, Property<T>> propertyB) {

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
            @NonNull Property<T> propertyA, @NonNull Property<M> mediator, @NonNull Function<M, ObservableValue<T>> propertyB) {

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
    public static <T, S> void bindBidirectional(@NonNull StringProperty propertyA, @NonNull Property<S> mediator, @NonNull Function<S, Property<T>> propertyB,
                                                @NonNull StringConverter<T> stringConverter) {
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
    void bindBidirectionalAndConvert(@NonNull PROPERTY_A propertyA, @NonNull PROPERTY_B propertyB, @NonNull Function<A, B> convertAtoB, @NonNull Function<B, A> convertBtoA) {
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
    public static <A, B> void bindAndConvert(@NonNull WritableValue<A> propertyA, @NonNull ObservableValue<B> propertyB, @NonNull Function<B, A> updateA) {
        boolean[] alreadyCalled = new boolean[1];
        addFlaggedChangeListener(propertyA, propertyB, updateA, alreadyCalled);
    }

    private static <Y, X> void addFlaggedChangeListener(@NonNull WritableValue<X> propertyX, @NonNull ObservableValue<Y> propertyY, @NonNull Function<Y, X> updateX,
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
    @NonNull
    public static StringExpression formatted(String format, Object... args) {
        return MessageStringFormatter.format(format, args);
    }

    /**
     * Binds list dest to list source.
     * The binding can be removed by calling {@link #unbindContent};
     *
     * @param dest   list dest
     * @param src    list source
     * @param toDest mapping function to dest
     * @param <D>    the type of list dest
     * @param <S>    the type of list source
     */
    public static <D, S> void bindContent(ObservableList<D> dest, ObservableList<S> src, Function<S, D> toDest) {
        ListTransformContentBinding<D, S> binding = new ListTransformContentBinding<>(dest, src, toDest);
        src.addListener(binding);
    }

    /**
     * Binds list dest to set source.
     * The binding can be removed by calling {@link #unbindContent};
     *
     * @param dest   list dest
     * @param src    list source
     * @param toDest mapping function to dest
     * @param <D>    the type of list dest
     * @param <S>    the type of list source
     */
    public static <D, S> void bindListContentToSet(ObservableList<D> dest, ObservableSet<S> src, Function<S, D> toDest) {
        ListToSetTransformContentBinding<D, S> binding = new ListToSetTransformContentBinding<>(dest, src, toDest);
        src.addListener(binding);
    }

    /**
     * Unbinds the specified content binding.
     *
     * @param dest list dest
     * @param src  list source
     * @param <D>  the type of list dest
     * @param <S>  the type of list source
     */
    public static <D, S> void unbindContent(ObservableList<D> dest, ObservableList<S> src, Function<S, D> toDest) {
        ListTransformContentBinding<D, S> binding = new ListTransformContentBinding<>(dest, src, (a) -> null);
        src.removeListener(binding);
    }

    /**
     * Binds the specified property of all list elements to the given property.
     * <p>
     * If an element is added, its property is bound.
     * <p>
     * If an element is removed, its property is unbound and the property value
     * is set to null.
     *
     * @param list     the list
     * @param getter   the getter for the element property
     * @param property the property to which the element properties shall be bound
     * @param <E>      the element type
     * @param <T>      the property type
     */
    public static <E, T> void bindElements(ObservableList<E> list, Function<E, Property<T>> getter, Property<T> property) {
        for (E elem : list) {
            Property<T> p = getter.apply(elem);
            p.unbind();
            p.bind(property);
        }
        list.addListener((ListChangeListener.Change<? extends E> change) -> {
            while (change.next()) {
                for (E removed : change.getRemoved()) {
                    Property<T> p = getter.apply(removed);
                    p.unbind();
                    p.setValue(null);
                }
                for (E added : change.getAddedSubList()) {
                    Property<T> p = getter.apply(added);
                    p.unbind();
                    p.bind(property);
                }
            }
        });
    }

    /**
     * Sets the specified value to all elements of the list.
     * <p>
     * If an element is added, the specified value is set.
     * <p>
     * If an element is removed, the null value is set.
     *
     * @param list   the list
     * @param setter the setter for the value on the element
     * @param value  the value
     * @param <E>    the element type
     * @param <T>    the value type
     */
    public static <E, T> void bindElements(ObservableList<E> list, BiConsumer<E, T> setter, T value) {
        for (E elem : list) {
            setter.accept(elem, value);
        }
        list.addListener((ListChangeListener.Change<? extends E> change) -> {
            while (change.next()) {
                for (E removed : change.getRemoved()) {
                    setter.accept(removed, null);
                }
                for (E added : change.getAddedSubList()) {
                    setter.accept(added, value);
                }
            }
        });
    }
}

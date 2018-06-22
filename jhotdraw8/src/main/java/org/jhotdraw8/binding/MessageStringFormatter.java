/* @(#)MessageStringFormatter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.binding;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * MessageStringFormatter.
 * <p>
 * Boolean values are converted to 0 and 1. This allows to format the boolean
 * value using a choice: {@code {0,choice,0#false|1#true} }
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class MessageStringFormatter extends StringBinding {

    private static Object extractValue(Object obj) {
        Object value = obj instanceof ObservableValue ? ((ObservableValue<?>) obj).getValue() : obj;
        // since message format can not handle booleans, we convert them to 1 and 0
        if (Boolean.TRUE.equals(value)) {
            return 1;
        }
        if (Boolean.FALSE.equals(value)) {
            return 0;
        }
        return value;
    }

    @NonNull
    private static Object[] extractValues(Object[] objs) {
        final int n = objs.length;
        final Object[] values = new Object[n];
        for (int i = 0; i < n; i++) {
            values[i] = extractValue(objs[i]);
        }
        return values;
    }

    private static ObservableValue<?>[] extractDependencies(Object... args) {
        final List<ObservableValue<?>> dependencies = new ArrayList<ObservableValue<?>>();
        for (final Object obj : args) {
            if (obj instanceof ObservableValue) {
                dependencies.add((ObservableValue<?>) obj);
            }
        }
        return dependencies.toArray(new ObservableValue<?>[dependencies.size()]);
    }

    @NonNull
    public static StringExpression format(@Nullable final String format, @NonNull final Object... args) {
        if (format == null) {
            throw new NullPointerException("Format cannot be null.");
        }
        if (extractDependencies(args).length == 0) {
            return ConstantStringExpression.of(String.format(format, args));
        }
        final MessageStringFormatter formatter = new MessageStringFormatter() {
            {
                super.bind(extractDependencies(args));
            }

            @Override
            public void dispose() {
                super.unbind(extractDependencies(args));
            }

            @Override
            protected String computeValue() {
                final Object[] values = extractValues(args);
                return MessageFormat.format(format, values);
            }

            @Override
            public ObservableList<ObservableValue<?>> getDependencies() {
                return FXCollections.unmodifiableObservableList(FXCollections
                        .observableArrayList(extractDependencies(args)));
            }
        };
        // Force calculation to check format
        formatter.get();
        return formatter;
    }
}

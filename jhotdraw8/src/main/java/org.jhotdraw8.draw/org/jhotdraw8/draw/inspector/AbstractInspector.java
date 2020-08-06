/*
 * @(#)AbstractInspector.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.jhotdraw8.annotation.NonNull;

/**
 * AbstractInspector.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractInspector<S> implements Inspector<S> {

    @NonNull
    protected final ObjectProperty<S> subject = new SimpleObjectProperty<>(this, SUBJECT_PROPERTY);
    @NonNull
    protected final BooleanProperty showing = new SimpleBooleanProperty(this, SHOWING_PROPERTY, true);

    @NonNull
    public ObjectProperty<S> subjectProperty() {
        return subject;
    }

    @NonNull
    public BooleanProperty showingProperty() {
        return showing;
    }
}

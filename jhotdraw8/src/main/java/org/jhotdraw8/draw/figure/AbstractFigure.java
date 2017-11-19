/* @(#)AbstractFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.FigureKey;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.styleable.AbstractStyleablePropertyBean;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * AbstractFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractFigure extends AbstractStyleablePropertyBean implements Figure, CacheableFigure {

    private final Map<? super Key<?>, Object> cachedValues = new HashMap<>();

    private ObservableSet<Figure> dependentFigures;
    private final ObjectProperty<Figure> parent = new ObjectPropertyBase<Figure>() {

        @Override
        protected void fireValueChangedEvent() {
            if (get() != null && !isSuitableParent(get())) {
                throw new IllegalArgumentException(get() + " is not a suitable parent for this figure.");
            }
            super.fireValueChangedEvent();
        }

        @Override
        public Object getBean() {
            return AbstractFigure.this;
        }

        @Override
        public String getName() {
            return PARENT_PROPERTY;
        }

    };
    private CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> propertyChangeListeners;

    /**
     * This method calls {@link #doAddNotify}.
     */
    @Override
    final public void addNotify(Drawing drawing) {
        this.drawing = drawing;
        doAddNotify(drawing);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void callObservers(StyleOrigin origin, boolean willChange, MapChangeListener.Change<Key<?>, Object> change) {
        if (origin == StyleOrigin.USER && !Objects.equals(change.getValueRemoved(), change.getValueAdded())) {
            if (willChange) {
                if (change.getKey() instanceof FigureKey) {
                    if (((FigureKey<?>) change.getKey()).getDirtyMask().containsOneOf(DirtyBits.LAYOUT_SUBJECT)) {
                        firePropertyChangeEvent(this, FigurePropertyChangeEvent.EventType.WILL_CHANGE, (Key<Object>) change.getKey(), change.getValueRemoved(), change.getValueAdded());
                    }
                }
            } else {
                firePropertyChangeEvent(this, FigurePropertyChangeEvent.EventType.CHANGED, (Key<Object>) change.getKey(), change.getValueRemoved(), change.getValueAdded());
            }
        }
    }

    @Override
    public <T> T getCachedValue(Key<T> key) {
        return key.get(cachedValues);
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        List<CssMetaData<? extends Styleable, ?>> list = new ArrayList<>();
        for (MapAccessor<?> key : getSupportedKeys()) {
            if (key instanceof WriteableStyleableMapAccessor<?>) {
                WriteableStyleableMapAccessor<?> sk = (WriteableStyleableMapAccessor<?>) key;

                CssMetaData<? extends Styleable, ?> md = sk.getCssMetaData();
                list.add(md);
            }
        }
        return list;
    }

    @Override
    final public Drawing getDrawing() {
        return drawing;
    }

    /**
     * This method is called by {@link #addNotify}. The implementation of this
     * class is empty.
     *
     * @param drawing the drawing
     */
    protected void doAddNotify(@Nonnull Drawing drawing) {

    }

    /**
     * This method is called by {@link #removeNotify}. The implementation of
     * this class is empty.
     *
     * @param drawing the drawing
     */
    protected void doRemoveNotify(@Nonnull Drawing drawing) {

    }

    @Override
    public final ObservableSet<Figure> getLayoutObservers() {
        if (dependentFigures == null) {
            dependentFigures = FXCollections.observableSet(Collections.newSetFromMap(new IdentityHashMap<Figure, Boolean>()));
        }
        return dependentFigures;
    }

    @Override
    public CopyOnWriteArrayList<Listener<FigurePropertyChangeEvent>> getPropertyChangeListeners() {
        if (propertyChangeListeners == null) {
            propertyChangeListeners = new CopyOnWriteArrayList<>();
        }
        return propertyChangeListeners;
    }

    /**
     * Returns a new map instance with all properties of this figure.
     * <p>
     * This method is used for XML serialization using the Java XMLEncoder and
     * XMLDecoder classes.
     *
     * @return a new list instance
     */
    public HashMap<String, Object> getPropertyMap() {
        HashMap<String, Object> result = new HashMap<>();
        for (Map.Entry<Key<?>, Object> e : getProperties().entrySet()) {
            Key<?> k = e.getKey();
            if (!Objects.equals(e.getValue(), k.getDefaultValue())) {
                result.put(k.getName(), e.getValue());
            }
        }
        return result;
    }

    /**
     * Replaces the properties map of this figure with the contents of the
     * specified map.
     * <p>
     * This method is used for XML serialization using the Java XMLEncoder and
     * XMLDecoder classes.
     *
     * @param newMap the new properties
     */
    public void setPropertyMap(HashMap<String, Object> newMap) {
        HashMap<String, Key<?>> keyst = new HashMap<>();
        Map<Key<?>, Object> m = getProperties();
        for (MapAccessor<?> ma : Figure.getDeclaredAndInheritedMapAccessors(getClass())) {
            if (ma instanceof Key<?>) {
                keyst.put(ma.getName(), (Key<?>) ma);
            }
        }
        for (Map.Entry<String, Object> e : newMap.entrySet()) {
            String name = e.getKey();
            Key<?> key = keyst.get(name);
            if (key != null) {
                m.put(key, e.getValue());
            }
        }
    }

    @Override
    public boolean hasPropertyChangeListeners() {
        return propertyChangeListeners != null && !propertyChangeListeners.isEmpty();
    }

    /**
     * This method whether the provided figure is a suitable parent for this
     * figure.
     * <p>
     * This implementation returns false if {@code newParent} is a
     * {@link Drawing}. Because only {@link org.jhotdraw8.draw.figure.Layer}s
     * may have {@code org.jhotdraw8.draw.Drawing} as a parent.
     *
     * @param newParent The new parent figure.
     * @return true if {@code newParent} is an acceptable parent
     */
    @Override
    public boolean isSuitableParent(Figure newParent) {
        return newParent != null && !(newParent instanceof Drawing);
    }

    @Override
    public ObjectProperty<Figure> parentProperty() {
        return parent;
    }

    /**
     * This implementation is empty.
     */
    @Override
    public void removeAllLayoutSubjects() {
        // empty
    }

    /**
     * This implementation is empty.
     *
     * @param connectedFigure the connected figure
     */
    @Override
    public void removeLayoutSubject(Figure connectedFigure) {
        // empty
    }

    /**
     * This method calls {@link #doAddNotify}.
     */
    @Override
    final public void removeNotify(Drawing drawing) {
        this.drawing = null;
        doRemoveNotify(drawing);
    }

    @Nullable
    private Drawing drawing;

    @Override
    public <T> T setCachedValue(Key<T> key, T value) {
        return key.put(cachedValues, value);
    }

    @Override
    public void updateCss() {
        getStyleableMap().clearAuthorAndInlineValues();
        Drawing d = getDrawing();
        if (d != null) {
            StylesheetsManager<Figure> styleManager = d.getStyleManager();
            styleManager.applyStylesheetsTo(this);
            /*
            for (Figure child : getChildren()) {
                child.updateCss();// should not recurse, because style manager knows better if it is worthwile?
            }*/
        }
        invalidateTransforms();
    }

}

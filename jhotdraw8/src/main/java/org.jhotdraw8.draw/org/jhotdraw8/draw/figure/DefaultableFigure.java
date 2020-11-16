/*
 * @(#)DefaultableFigure.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import javafx.css.StyleOrigin;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssDefaultableValue;
import org.jhotdraw8.css.CssDefaulting;
import org.jhotdraw8.draw.key.DefaultableStyleableMapAccessor;

import java.util.Objects;

public interface DefaultableFigure extends Figure{
    /**
     * Returns the styled value.
     *
     * @param <T> The value type
     * @param key The property key
     * @return The styled value.
     */
    default @Nullable <T> T getDefaultableStyled(@NonNull DefaultableStyleableMapAccessor<T> key) {
        return getDefaultableStyled(StyleOrigin.INLINE,key);
    }
    default @Nullable <T> T getDefaultableStyled(@NonNull StyleOrigin origin, @NonNull DefaultableStyleableMapAccessor<T> key) {
        // FIXME REVERT does not work this way, must use getStyled(origin,key) for _starting a search at the specified origin_ value
        CssDefaultableValue<T> dv = Objects.requireNonNull(getStyled(origin==StyleOrigin.INLINE?null:origin,key));
        if (dv.getDefaulting()==null) {
            return dv.getValue();
        }
        switch (dv.getDefaulting()) {
            case INITIAL:
                return key.getInitialValue();
            case INHERIT:
                if (getParent() instanceof DefaultableFigure) {
                    return ((DefaultableFigure)getParent()).getDefaultableStyled(key);
                } else {
                    return key.getInitialValue();
                }
            case UNSET:
                CssDefaultableValue<T> defaultValue = key.getDefaultValue();
                if (defaultValue.getDefaulting()==CssDefaulting.INHERIT) {
                    if (getParent() instanceof DefaultableFigure) {
                        return ((DefaultableFigure)getParent()).getDefaultableStyled(key);
                    } else {
                        return key.getInitialValue();
                    }
                }else {
                    return key.getInitialValue();
                }
            case REVERT:
                switch (origin) {
                    case USER_AGENT:
                        return key.getInitialValue();
                    case USER:
                        return getDefaultableStyled(StyleOrigin.USER_AGENT,key);
                    case AUTHOR:
                        return getDefaultableStyled(StyleOrigin.USER,key);
                    case INLINE:
                        return getDefaultableStyled(StyleOrigin.AUTHOR,key);
                    default:
                        throw new IllegalStateException("Unexpected value: " + origin);
                }
            default:
                throw new UnsupportedOperationException("unsupported defaulting: "+dv.getDefaulting());
        }
    }
    /**
     * Returns the styled value.
     *
     * @param <T> The value type
     * @param key The property key
     * @return The styled value.
     */
    default @NonNull <T> T getDefaultableStyledNonNull(@NonNull DefaultableStyleableMapAccessor<T> key) {
        return Objects.requireNonNull(getDefaultableStyled(key));
    }
}

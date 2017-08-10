/* @(#)OptionalProperty.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import java.util.Optional;

/**
 * OptionalProperty.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OptionalProperty<T> extends NonnullProperty<Optional<T>> {

    public OptionalProperty(Object bean, String name) {
        this(bean, name, null);
    }

    public OptionalProperty(Object bean, String name, T initialValue) {
        super(bean, name, Optional.ofNullable(initialValue));
    }

}

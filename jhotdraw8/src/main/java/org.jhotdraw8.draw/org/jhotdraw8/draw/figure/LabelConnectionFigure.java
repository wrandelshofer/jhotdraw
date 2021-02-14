/*
 * @(#)LabelConnectionFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.SimpleNullableKey;
import org.jhotdraw8.draw.connector.Connector;

public interface LabelConnectionFigure extends Figure {
    /**
     * The label target.
     */
    public static final @NonNull SimpleNullableKey<Figure> LABEL_TARGET = new SimpleNullableKey<>("labelTarget", Figure.class, null);
    /**
     * The connector.
     */
    public static final @NonNull SimpleNullableKey<Connector> LABEL_CONNECTOR = new SimpleNullableKey<>("labelConnector", Connector.class, null);

}

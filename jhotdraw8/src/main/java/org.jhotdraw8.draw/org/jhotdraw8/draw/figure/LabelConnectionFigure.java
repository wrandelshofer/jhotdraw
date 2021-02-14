/*
 * @(#)LabelConnectionFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.NullableObjectKey;

public interface LabelConnectionFigure extends Figure {
    /**
     * The label target.
     */
    public static final @NonNull NullableObjectKey<Figure> LABEL_TARGET = new NullableObjectKey<>("labelTarget", Figure.class, null);
    /**
     * The connector.
     */
    public static final @NonNull NullableObjectKey<Connector> LABEL_CONNECTOR = new NullableObjectKey<>("labelConnector", Connector.class, null);

}

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.NullableObjectKey;

public interface LabelConnectionFigure extends Figure {
    /**
     * The label target.
     */
    @NonNull
    public final static NullableObjectKey<Figure> LABEL_TARGET = new NullableObjectKey<>("labelTarget", Figure.class, null);
    /**
     * The connector.
     */
    @NonNull
    public final static NullableObjectKey<Connector> LABEL_CONNECTOR = new NullableObjectKey<>("labelConnector", Connector.class, null);

}

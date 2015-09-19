/* @(#)AbstractConnectableLeafFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw.shape;

import org.jhotdraw.draw.*;
import java.util.ArrayList;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;

/**
 * AbstractConnectableLeafFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractConnectableShapeFigure extends AbstractShapeFigure implements ConnectableFigure {
       private final ReadOnlyListProperty<ConnectionFigure> connections = new ReadOnlyListWrapper<>(this, CONNECTIONS_PROPERTY, FXCollections.observableList(new ArrayList<ConnectionFigure>())).getReadOnlyProperty();
 
    @Override
    public ReadOnlyListProperty<ConnectionFigure> connectionsProperty() {
        return connections;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhotdraw.draw;

import java.util.ArrayList;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;

/**
 * AbstractConnectableLeafFigure.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractConnectableLeafFigure extends AbstractLeafFigure implements ConnectableFigure {
       private final ReadOnlyListProperty<ConnectionFigure> connections = new ReadOnlyListWrapper<>(this, CONNECTIONS_PROPERTY, FXCollections.observableList(new ArrayList<ConnectionFigure>())).getReadOnlyProperty();
 
    @Override
    public ReadOnlyListProperty<ConnectionFigure> connectionsProperty() {
        return connections;
    }
}

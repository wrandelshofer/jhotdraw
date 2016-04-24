/* @(#)Disableable.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.app;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SetProperty;

/**
 * A disableable object is disabled when it has one or more disablers.
 * <p>
 * A disabled object is not allowed to process user input. If the disabled
 * object is a user interface component, then it must ignore all mouse events,
 * key events, and any other user input events.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Disableable {
    // Disabled state

    /** Indicates whether or not this object is disabled. 
     * This is true when the disabler set is not empty.
     *
     * @return the disabled property.
     */
    ReadOnlyBooleanProperty disabledProperty();
    
    /** The set of disablers.
     * @return The disablers.
     */
    SetProperty<Object> disablersProperty();

    // Convenience method
    default boolean isDisabled() {
        return disabledProperty().get();
    }

    /** Adds a disabler
     * @param disabler a new disabler */
    default void addDisabler(Object disabler) {
        disablersProperty().add(disabler);
    }

    /** Removes a disabler
     * @param disabler an object which does not disable anymore */
    default void removeDisabler(Object disabler) {
        disablersProperty().remove(disabler);
    }

}

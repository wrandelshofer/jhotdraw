/*
 * @(#)InspectorStyleClasses.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.inspector;

/**
 * This interface defines constants for style classes that GUI components
 * of the drawing editor use.
 */
public interface InspectorStyleClasses {
    /**
     * This style class is used for checkboxes that change the visible state
     * of an object.
     * <p>
     * An object is visible, if the checkbox is selected.
     * <p>
     * The value is: <tt>{@value #VISIBLE_CHECK_BOX}</tt>.
     */
    String VISIBLE_CHECK_BOX = "visible-check-box";

    /**
     * This style class is used for checkboxes that change the locked state
     * of an object.
     * <p>
     * An object is locked, if the checkbox is selected.
     * <p>
     * The value is: <tt>{@value #LOCKED_CHECK_BOX}</tt>.
     */
    String LOCKED_CHECK_BOX = "locked-check-box";

    /**
     * This style class is used for buttons that add an object to a collection.
     * <p>
     * The value is: <tt>{@value #ADD_BUTTON}</tt>.
     */
    String ADD_BUTTON = "add-button";
    /**
     * This style class is used for buttons that remove an object from a
     * collection.
     * <p>
     * The value is: <tt>{@value #REMOVE_BUTTON}</tt>.
     */
    String REMOVE_BUTTON = "remove-button";
    /**
     * This style class is used by inspector controls.
     * <p>
     * The value is: <tt>{@value #INSPECTOR}</tt>.
     */
    String INSPECTOR = "inspector";
    /**
     * This style class is used by controls that are placed without white
     * space at their top, left and right bounds.
     * <p>
     * The value is: <tt>{@value #flushTopRightLeft}</tt>.
     */
    String FLUSH_TOP_RIGHT_LEFT = "flushTopRightLeft";
}

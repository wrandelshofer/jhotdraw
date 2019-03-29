package org.jhotdraw8.samples.modeler.model;

public enum MLModifier {
    /**
     * An operation with this modifier is static and concrete.
     */
    STATIC,
    /**
     * An operation with this modifier is concrete.
     */
    DEFAULT,
    /**
     * An operation with this modifier is concrete unless it has
     * one of the following modifier: STATIC, DEFAULT.
     */
    ABSTRACT
}

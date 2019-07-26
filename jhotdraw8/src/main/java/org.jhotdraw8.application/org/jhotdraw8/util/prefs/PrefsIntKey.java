/*
 * @(#)PrefsIntKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.util.prefs;

import org.jhotdraw8.annotation.Nonnull;

import java.util.prefs.Preferences;

/**
 * PrefsIntKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrefsIntKey {

    private final String key;
    private final int defaultValue;
    private final int clampMin;
    private final int clampMax;

    public PrefsIntKey(String key, int defaultValue, int clampMin, int clampMax) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.clampMin = clampMin;
        this.clampMax = clampMax;
    }

    public int get(@Nonnull Preferences prefs) {
        return clamp(prefs.getInt(key, defaultValue), clampMin, clampMax);
    }

    public void put(@Nonnull Preferences prefs, int newValue) {
        prefs.putInt(key, newValue);
    }

    /**
     * Clamps a value to the given range.
     *
     * @param value the value
     * @param min   the lower bound of the range
     * @param max   the upper bound of the range
     * @return the constrained value
     */
    private int clamp(int value, int min, int max) {
        return min <= value ? (value <= max ? value : max) : min;
    }
}

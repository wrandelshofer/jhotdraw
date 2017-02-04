/* @(#)PrefsIntKey.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.util.prefs;

import java.util.prefs.Preferences;
import org.jhotdraw8.geom.Geom;

/**
 * PrefsIntKey.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
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

    public int get(Preferences prefs) {
        return Geom.clamp(prefs.getInt(key, defaultValue), clampMin, clampMax);
    }

    public void put(Preferences prefs, int newValue) {
        prefs.putInt(key, newValue);
    }
}

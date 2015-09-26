/* @(#)StyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.xml.css;

import java.util.Map;

/**
 *
 * @author werni
 */
public interface StyleManager {

    void addRule(String selector, Map<String, String> properties);

}

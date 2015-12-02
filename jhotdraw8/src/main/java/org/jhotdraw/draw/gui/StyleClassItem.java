/* @(#)StyleClassItem.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.gui;

/**
 * StyleClassItem.
 * @author Werner Randelshofer
 */
public class StyleClassItem {
    /** The text of the tag. */
    private final String text;
    /** Whether the tag is present in all elements. */
    private final boolean inAllElements;

    public StyleClassItem(String text, boolean isInAllElements) {
        this.text = text;
        this.inAllElements = isInAllElements;
    }

    public String getText() {
        return text;
    }
    public boolean isInAllElements() {
        return inAllElements;
    }
    
    
}

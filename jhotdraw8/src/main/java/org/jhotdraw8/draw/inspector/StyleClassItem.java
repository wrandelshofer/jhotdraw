/* @(#)StyleClassItem.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

/**
 * StyleClassItem.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StyleClassItem {

    /**
     * The text of the tag.
     */
    private final String text;
    /**
     * Whether the tag is present in all elements.
     */
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

/* @(#)Reference.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.sysdoc;

/**
 * Identifies a reference in a text fragment.
 *
 * @author Werner Randelshofer
*/
public class Reference {

    private final Fragment fragment;
    private final String name;

    public Reference(Fragment fragment, String name) {
        this.fragment = fragment;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Reference{" + "file=" + fragment.getFileName() + ", name=" + name + '}';
    }

}

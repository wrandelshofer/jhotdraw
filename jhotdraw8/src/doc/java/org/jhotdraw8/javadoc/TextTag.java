/* @(#)TextTag.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.javadoc;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

/**
 * TextTag.
 *
 * @author Werner Randelshofer
*/
public class TextTag implements Tag {

    private final Doc holder;
    private final String text;
    private final SourcePosition position;

    public TextTag(Doc holder, String text, SourcePosition position) {
        this.holder = holder;
        this.text = text;
        this.position = position;
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public Doc holder() {
        return holder;
    }

    @Override
    public String kind() {
       return name();
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public Tag[] inlineTags() {
        return new Tag[] {this};
    }

    @Override
    public Tag[] firstSentenceTags() {
        return new Tag[] {this};
    }

    @Override
    public SourcePosition position() {
        return position;
    }

}

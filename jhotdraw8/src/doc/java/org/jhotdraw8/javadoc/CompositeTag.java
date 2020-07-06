/* @(#)CompositeTag.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.javadoc;

import com.sun.javadoc.Doc;
import com.sun.javadoc.SourcePosition;
import com.sun.javadoc.Tag;

/**
 * CompositeTag.
 *
 * @author Werner Randelshofer
*/
public class CompositeTag implements Tag {

    private final Doc holder;
    private final Tag[] inlineTags;
    private final SourcePosition position;

    public CompositeTag(Doc holder, Tag[] inlineTags, SourcePosition position) {
        this.holder = holder;
        this.inlineTags = inlineTags;
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
        StringBuilder buf=new StringBuilder();
        for (Tag t:inlineTags) {
            buf.append(t.text());
        }
        return buf.toString();
    }

    @Override
    public Tag[] inlineTags() {
        return inlineTags;
    }

    @Override
    public Tag[] firstSentenceTags() {
        return inlineTags;
    }

    @Override
    public SourcePosition position() {
        return position;
    }

}


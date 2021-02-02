/*
 * @(#)AtRule.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.CssToken;

import java.util.List;

/**
 * A "at-rule" consists of an "at-keyword", a list of header tokens and a
 * list of body tokens.
 *
 * @author Werner Randelshofer
 */
public class AtRule extends Rule {
    private final @NonNull String atKeyword;
    private final @NonNull ImmutableList<CssToken> header;
    private final @NonNull ImmutableList<CssToken> body;

    public AtRule(@NonNull String atKeyword,
                  @NonNull List<? extends CssToken> header, @NonNull List<? extends CssToken> body) {
        this.atKeyword = atKeyword;
        this.header = ImmutableLists.ofCollection(header);
        this.body = ImmutableLists.ofCollection(body);
    }

    @Override
    public @NonNull String toString() {
        StringBuilder buf = new StringBuilder("AtRule: ");
        buf.append(atKeyword);
        if (!header.isEmpty()) {
            buf.append(" ");
            for (CssToken t : header) {
                buf.append(t.fromToken());
            }
        }
        if (!header.isEmpty() && !body.isEmpty()) {
            buf.append(" ");
        }
        if (!body.isEmpty()) {
            buf.append("{");
            for (CssToken t : body) {
                buf.append(t.fromToken());
            }
            buf.append("}");
        }
        return buf.toString();
    }

    public @NonNull String getAtKeyword() {
        return atKeyword;
    }

    public @NonNull ReadOnlyList<CssToken> getHeader() {
        return header;
    }

    public @NonNull ReadOnlyList<CssToken> getBody() {
        return body;
    }

}

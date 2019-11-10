/*
 * @(#)AtRule.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.CssToken;

import java.util.List;

/**
 * A "at-rule" consists of an "at-keyword", a list of header tokens and a list of body tokens.
 *
 * @author Werner Randelshofer
 */
public class AtRule extends Rule {
    @NonNull
    private final String atKeyword;
    @NonNull
    private final ImmutableList<CssToken> header;
    @NonNull
    private final ImmutableList<CssToken> body;

    public AtRule(@NonNull String atKeyword,
                  @NonNull List<? extends CssToken> header, @NonNull List<? extends CssToken> body) {
        this.atKeyword = atKeyword;
        this.header = ImmutableLists.ofCollection(header);
        this.body = ImmutableLists.ofCollection(body);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("AtRule: ");
        buf.append(atKeyword);
        if (!header.isEmpty()) {
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

    @NonNull
    public String getAtKeyword() {
        return atKeyword;
    }

    @NonNull
    public ReadOnlyList<CssToken> getHeader() {
        return header;
    }

    @NonNull
    public ReadOnlyList<CssToken> getBody() {
        return body;
    }

}

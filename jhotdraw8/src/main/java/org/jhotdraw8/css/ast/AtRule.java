/* @(#)QualifiedRule.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ReadOnlyList;
import org.jhotdraw8.css.CssToken;

import org.jhotdraw8.annotation.Nonnull;

import java.util.List;

/**
 * A "at-rule" consists of an "at-keyword", a list of header tokens and a list of body tokens.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AtRule extends Rule {
    @Nonnull
    private final String atKeyword;
    @Nonnull
    private final ImmutableList<CssToken> header;
    @Nonnull
    private final ImmutableList<CssToken> body;

    public AtRule(@Nonnull String atKeyword,
                  @Nonnull List<? extends CssToken> header, @Nonnull List<? extends CssToken> body) {
        this.atKeyword = atKeyword;
        this.header = ImmutableList.ofCollection(header);
        this.body = ImmutableList.ofCollection(body);
    }

    @Nonnull
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
    @Nonnull
    public String getAtKeyword() {
        return atKeyword;
    }

    @Nonnull
    public ReadOnlyList<CssToken> getHeader() {
        return header;
    }

    @Nonnull
    public ReadOnlyList<CssToken> getBody() {
        return body;
    }

}

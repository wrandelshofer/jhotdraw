/*
 * @(#)ListCssTokenizer.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

import java.util.List;

import static org.jhotdraw8.css.CssTokenType.TT_BAD_COMMENT;
import static org.jhotdraw8.css.CssTokenType.TT_CDC;
import static org.jhotdraw8.css.CssTokenType.TT_CDO;
import static org.jhotdraw8.css.CssTokenType.TT_COMMENT;
import static org.jhotdraw8.css.CssTokenType.TT_S;

public class ListCssTokenizer implements CssTokenizer {
    private final @NonNull ImmutableList<CssToken> in;
    private int index = 0;
    private boolean pushBack = true;
    private CssToken current;
    private static final CssToken EOF = new CssToken(CssTokenType.TT_EOF);

    public ListCssTokenizer(@NonNull List<CssToken> in) {
        this(ImmutableLists.ofCollection(in));
    }

    public ListCssTokenizer(@NonNull ReadOnlyList<CssToken> in) {
        this.in = ImmutableLists.ofCollection(in);
        current = in.isEmpty() ? EOF : in.get(0);
    }

    @Override
    public @Nullable Number currentNumber() {
        return current.getNumericValue();
    }

    @Override
    public @Nullable String currentString() {
        return current.getStringValue();
    }

    @Override
    public int current() {
        return current.getType();
    }

    @Override
    public int getLineNumber() {
        return current.getLineNumber();
    }

    @Override
    public int getStartPosition() {
        return current.getStartPos();
    }

    @Override
    public int getEndPosition() {
        return current.getEndPos();
    }

    @Override
    public int getNextPosition() {
        if (pushBack) {
            return current.getStartPos();
        } else {
            return current.getEndPos();
        }
    }

    @Override
    public int next() {
        skipWhitespace();
        while (skipComment()) {
            skipWhitespace();
        }
        return nextNoSkip();
    }

    @Override
    public int nextNoSkip() {
        if (pushBack) {
            pushBack = false;
        } else {
            index++;
            if (index < in.size()) {
                current = in.get(index);
            } else {
                current = EOF;
            }
        }
        return current.getType();
    }


    private void skipWhitespace() {
        while (nextNoSkip() == TT_S//
                || current.getType() == TT_CDC//
                || current.getType() == TT_CDO) {
        }
        pushBack();
    }

    private boolean skipComment() {
        boolean didSkip = false;
        while (nextNoSkip() == TT_COMMENT//
                || current.getType() == TT_BAD_COMMENT) {
            didSkip = true;
        }
        pushBack();
        return didSkip;
    }

    @Override
    public void pushBack() {
        pushBack = true;
    }


    @Override
    public CssToken getToken() {
        return current;
    }
}

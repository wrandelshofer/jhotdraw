/*
 * @(#)ListCssTokenizer.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.ReadOnlyList;

import java.io.IOException;
import java.util.List;

import static org.jhotdraw8.css.CssTokenType.TT_BAD_COMMENT;
import static org.jhotdraw8.css.CssTokenType.TT_CDC;
import static org.jhotdraw8.css.CssTokenType.TT_CDO;
import static org.jhotdraw8.css.CssTokenType.TT_COMMENT;
import static org.jhotdraw8.css.CssTokenType.TT_S;

public class ListCssTokenizer implements CssTokenizer {
    private final ImmutableList<CssToken> in;
    private int index = 0;
    private boolean pushBack = true;
    private CssToken current;

    public ListCssTokenizer(List<CssToken> in) {
        this(ImmutableLists.ofCollection(in));
    }

    public ListCssTokenizer(ReadOnlyList<CssToken> in) {
        this.in = ImmutableLists.ofCollection(in);
        current = in.isEmpty() ? new CssToken(CssTokenType.TT_EOF) : in.get(0);
    }

    @Nullable
    @Override
    public Number currentNumber() {
        return current.getNumericValue();
    }

    @Nullable
    @Override
    public String currentString() {
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
    public int next() throws IOException {
        skipWhitespace();
        while (skipComment()) {
            skipWhitespace();
        }
        return nextNoSkip();
    }

    @Override
    public int nextNoSkip() throws IOException {
        if (pushBack) {
            pushBack = false;
        } else {
            index++;
            if (index < in.size()) {
                current = in.get(index);
            } else {
                if (current == null) {
                    current = new CssToken(CssTokenType.TT_EOF);
                } else {
                    current = new CssToken(CssTokenType.TT_EOF, null, null
                            , current.getLineNumber(),
                            current.getEndPos(), current
                            .getEndPos());
                }
            }
        }
        return current.getType();
    }


    private void skipWhitespace() throws IOException {
        while (nextNoSkip() == TT_S//
                || current.getType() == TT_CDC//
                || current.getType() == TT_CDO) {
        }
        pushBack();
    }

    private boolean skipComment() throws IOException {
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

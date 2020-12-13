/*
 * @(#)Declaration.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssToken;

import java.util.Arrays;
import java.util.List;

/**
 * A "declaration" associates a "propertyName" with a list of preserved tokens. If
 * the list of preserved tokens is empty, the declaration must be ignored.
 *
 * @author Werner Randelshofer
 */
public class Declaration extends AST {
    @Nullable
    private final String namespace;
    @NonNull
    private final String propertyName;
    @NonNull
    private final ImmutableList<CssToken> terms;
    private int startPos = -1;
    private int endPos = -1;

    public Declaration(@Nullable String namespace, @NonNull String propertyName, @NonNull List<CssToken> terms, int startPos, int endPos) {
        this.namespace = namespace;
        this.propertyName = propertyName;
        this.terms = ImmutableLists.ofCollection(terms);
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Nullable
    public String getNamespace() {
        return namespace;
    }

    @NonNull
    public String getPropertyName() {
        return propertyName;
    }

    @NonNull
    public ImmutableList<CssToken> getTerms() {
        return terms;
    }

    @NonNull
    public String getTermsAsString() {
        StringBuilder buf = new StringBuilder();

        for (CssToken t : terms) {
            buf.append(t.toString());
        }
        return buf.toString();
    }

    @NonNull
    @Override
    public String toString() {

        return propertyName + ":" + getTermsAsString();
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

}

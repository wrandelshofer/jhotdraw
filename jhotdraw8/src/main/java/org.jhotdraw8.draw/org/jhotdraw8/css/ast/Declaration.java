/* @(#)Declaration.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.annotation.Nonnull;
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
 * @version $Id$
 */
public class Declaration extends AST {
    @Nullable
    private final String propertyNamespace;
    @Nonnull
    private final String propertyName;
    @Nonnull
    private final ImmutableList<CssToken> terms;
    private int startPos = -1;
    private int endPos = -1;

    public Declaration(@Nullable String propertyNamespace, @Nonnull String propertyName, CssToken term) {
        this(propertyNamespace, propertyName, Arrays.asList(new CssToken[]{term}));
    }

    public Declaration(@Nullable String propertyNamespace, @Nonnull String propertyName, @Nonnull List<CssToken> terms) {
        this(propertyNamespace, propertyName, terms, -1, -1);
    }

    public Declaration(@Nullable String propertyNamespace, @Nonnull String propertyName, @Nonnull List<CssToken> terms, int startPos, int endPos) {
        this.propertyNamespace = propertyNamespace;
        this.propertyName = propertyName;
        this.terms = ImmutableLists.ofCollection(terms);
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Nullable
    public String getPropertyNamespace() {
        return propertyNamespace;
    }

    @Nonnull
    public String getPropertyName() {
        return propertyName;
    }

    @Nonnull
    public ImmutableList<CssToken> getTerms() {
        return terms;
    }

    @Nonnull
    public String getTermsAsString() {
        StringBuilder buf = new StringBuilder();

        for (CssToken t : terms) {
            buf.append(t.toString());
        }
        return buf.toString();
    }

    @Nonnull
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

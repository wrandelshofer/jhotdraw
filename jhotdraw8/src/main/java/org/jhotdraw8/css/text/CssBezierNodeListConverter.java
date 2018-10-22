/* @(#)BezierNodeList.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
import org.jhotdraw8.geom.BezierNodePathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts an BezierNodeList path to a CSS String.
 * <p>
 * The null value will be converted to the CSS identifier "none".
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssBezierNodeListConverter extends AbstractCssConverter<ImmutableList<BezierNode>> {

    public CssBezierNodeListConverter(boolean nullable) {
        super(nullable);
    }


    @NotNull
    @Override
    public ImmutableList<BezierNode> parseNonnull(@Nonnull CssTokenizer tt, @Nullable IdFactory idFactory) throws ParseException, IOException {
        if (tt.next() != CssTokenType.TT_STRING) {
            throw new ParseException("⟨BezierNodePath⟩ String expected.", tt.getStartPosition());
        }
        BezierNodePathBuilder builder = new BezierNodePathBuilder();
        Shapes.buildFromSvgString(builder, tt.currentStringNonnull());
        return builder.build();
    }

    @Override
    protected <TT extends ImmutableList<BezierNode>> void produceTokensNonnull(@Nonnull TT value, @Nullable IdFactory idFactory, @Nonnull Consumer<CssToken> out) {
        if (value.isEmpty()) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else {
            out.accept(new CssToken(CssTokenType.TT_STRING, Shapes.doubleSvgStringFromAWT(new BezierNodePath(value).getPathIterator(null))));
        }
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨BezierNodePath⟩: \"⟨SvgPath⟩\"";
    }
}

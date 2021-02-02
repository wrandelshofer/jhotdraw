/*
 * @(#)CssPoint3DConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssPoint3D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Converts a {@code javafx.geometry.CssPoint3D} into a {@code String} and vice
 * versa.
 *
 * @author Werner Randelshofer
 */
public class CssPoint3DConverter extends AbstractCssConverter<CssPoint3D> {
    private final boolean withSpace;
    private final boolean withComma;

    public CssPoint3DConverter(boolean nullable) {
        this(nullable, true, false);
    }

    public CssPoint3DConverter(boolean nullable, boolean withSpace, boolean withComma) {
        super(nullable);
        this.withSpace = withSpace || !withComma;
        this.withComma = withComma;
    }

    @NonNull
    @Override
    public CssPoint3D parseNonNull(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        final CssSize x, y, z;
        x = parseDimension(tt, "x");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        y = parseDimension(tt, "y");
        tt.skipIfPresent(CssTokenType.TT_COMMA);
        z = parseDimension(tt, "z");

        return new CssPoint3D(x, y, z);
    }

    @NonNull
    private CssSize parseDimension(@NonNull CssTokenizer tt, String variable) throws ParseException, IOException {
        switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                return new CssSize(tt.currentNumberNonNull().doubleValue());
            case CssTokenType.TT_DIMENSION:
                String s = tt.currentStringNonNull();
                return new CssSize(tt.currentNumberNonNull().doubleValue(), s == null ? UnitConverter.DEFAULT : s);
            default:
                throw new ParseException(" ⟨CssPoint3D⟩: ⟨" + variable + "⟩ expected.", tt.getStartPosition());
        }
    }

    @Override
    protected <TT extends CssPoint3D> void produceTokensNonNull(@NonNull TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        CssSize x = value.getX();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, x.getValue(), x.getUnits()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssSize y = value.getY();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, y.getValue(), y.getUnits()));
        if (withComma) {
            out.accept(new CssToken(CssTokenType.TT_COMMA));
        }
        if (withSpace) {
            out.accept(new CssToken(CssTokenType.TT_S, " "));
        }
        CssSize z = value.getZ();
        out.accept(new CssToken(CssTokenType.TT_DIMENSION, z.getValue(), z.getUnits()));
    }

    @Override
    public String getHelpText() {
        return "Format of ⟨CssPoint3D⟩: ⟨x⟩ ⟨y⟩";
    }
}

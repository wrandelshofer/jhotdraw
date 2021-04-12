/*
 * @(#)CssSizeConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
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
 * CssDoubleConverter.
 * <p>
 * Parses the following EBNF from the
 * <a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">JavaFX
 * CSS Reference Guide</a>.
 * </p>
 * <pre>
 * Size := Double, [Unit] ;
 * Unit := ("px"|"mm"|"cm"|in"|"pt"|"pc"]"em"|"ex") ;
 * </pre>
 *
 * @author Werner Randelshofer
 */
public class CssSizeConverter implements CssConverter<CssSize> {

    private final boolean nullable;

    public CssSizeConverter(boolean nullable) {
        this.nullable = nullable;
    }


    @Override
    public @Nullable CssSize parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (nullable) {
            if (tt.next() == CssTokenType.TT_IDENT && CssTokenType.IDENT_NONE.equals(tt.currentString())) {
                return null;
            } else {
                tt.pushBack();
            }
        }
        return parseSizeOrPercentage(tt, "size");
    }

    public static CssSize parseSize(@NonNull CssTokenizer tt, String variable) throws ParseException, IOException {
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            return new CssSize(tt.currentNumberNonNull().doubleValue());
        case CssTokenType.TT_DIMENSION:
            return new CssSize(tt.currentNumberNonNull().doubleValue(), tt.currentString());
        case CssTokenType.TT_IDENT:
            switch (tt.currentStringNonNull()) {
            case "NaN":
                return new CssSize(Double.NaN);
            case "INF":
                return new CssSize(Double.POSITIVE_INFINITY);
            case "-INF":
                return new CssSize(Double.NEGATIVE_INFINITY);
            default:
                throw new ParseException(" ⟨CssPoint2D⟩: ⟨" + variable + "⟩ expected.", tt.getStartPosition());
            }
        default:
            throw new ParseException(" ⟨CssPoint2D⟩: ⟨" + variable + "⟩ expected.", tt.getStartPosition());
        }
    }

    public static CssSize parseSizeOrPercentage(@NonNull CssTokenizer tt, String variable) throws ParseException, IOException {
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            return new CssSize(tt.currentNumberNonNull().doubleValue());
        case CssTokenType.TT_DIMENSION:
            return new CssSize(tt.currentNumberNonNull().doubleValue(), tt.currentString());
        case CssTokenType.TT_PERCENTAGE:
            return new CssSize(tt.currentNumberNonNull().doubleValue(), "%");
        case CssTokenType.TT_IDENT:
            switch (tt.currentStringNonNull()) {
            case "NaN":
                return new CssSize(Double.NaN);
            case "INF":
                return new CssSize(Double.POSITIVE_INFINITY);
            case "-INF":
                return new CssSize(Double.NEGATIVE_INFINITY);
            default:
                throw new ParseException(" ⟨CssPoint2D⟩: ⟨" + variable + "⟩ expected.", tt.getStartPosition());
            }
        default:
            throw new ParseException(" ⟨CssPoint2D⟩: ⟨" + variable + "⟩ expected.", tt.getStartPosition());
        }
    }


    @Override
    public <TT extends CssSize> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        if (value == null) {
            out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
        } else if (UnitConverter.DEFAULT.equals(value.getUnits())) {
            out.accept(new CssToken(CssTokenType.TT_NUMBER, value.getValue(), ""));
        } else {
            switch (value.getUnits()) {
            case UnitConverter.PERCENTAGE:
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, value.getValue(), "%"));
                break;
            default:
                out.accept(new CssToken(CssTokenType.TT_DIMENSION, value.getValue(), value.getUnits()));
                break;
            }
        }
    }

    @Override
    public @Nullable CssSize getDefaultValue() {
        return null;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public @NonNull String getHelpText() {
        return "Format of ⟨Size⟩: ⟨size⟩ | ⟨percentage⟩% | ⟨size⟩⟨Units⟩"
                + "\nFormat of ⟨Units⟩: mm | cm | em | ex | in | pc | px | pt";
    }

}

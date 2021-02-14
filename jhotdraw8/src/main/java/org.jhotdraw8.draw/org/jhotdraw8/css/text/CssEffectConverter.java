/*
 * @(#)CssEffectConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

/**
 * CssEffectConverter.
 * <p>
 * Parses the following EBNF:
 * </p>
 * <pre>
 * Effect = "none" | ( Blend | Bloom | BoxBlur | ColorAdjust | DropShadow | GaussianBlur | Glow | InnerShadow | Shadow ) , { Effect };
 * Blend = "blend(" , [
 *                 blendType
 *              ] , ")";
 * Bloom = "bloom(" , [
 *                 threshold
 *              ] , ")";
 * BoxBlur = "box-blur(" , [
 *                 width, Sep, height, Sep, iterations
 *              ] , ")";
 * ColorAdjust = "color-adjust(" , [
 *                 "hue", S, hue, Sep,
 *                 "saturation" , S , saturation, Sep,
 *                 "brightness", S, brightness, Sep,
 *                 "contrast", S, contrast
 *              ] , ")";
 * DropShadow = "drop-shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius , Sep ,  spread , Sep ,  xOffset , Sep ,  yOffset
 *              ] , ")";
 * GaussianBlur = "gaussian-blur(" , [
 *                 radius
 *              ] , ")";
 * Glow = "glow(" , [
 *                 level
 *              ] , ")";
 * InnerShadow = "inner-shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius , Sep, choke , Sep ,  xOffset , Sep ,  yOffset
 *               ] , ")";
 * Shadow = "shadow(" , [
 *                 blurType , Sep , color , Sep ,
 *                 radius
 *               ] , ")";
 *
 * Sep         = ( S , { S } | { S } , "," , { S } ) ;
 * S           = (* white space character *)
 * </pre>
 * <p>
 * References:
 * <dl>
 *     <dt>JavaFX CSS Reference Guide</dt>
 *     <dd><a href="https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html">oracle.com</a></dd>
 * </dl>
 *
 * @author Werner Randelshofer
 */
public class CssEffectConverter implements CssConverter<Effect> {

    private static final String BLEND = "blend";
    private static final String BLOOM = "bloom";
    private static final String BOX_BLUR = "box-blur";
    private static final String COLOR_ADJUST = "color-adjust";
    private static final String DROP_SHADOW = "drop-shadow";
    private static final String GAUSSIAN_BLUR = "gaussian-blur";
    private static final String GLOW = "glow";
    private static final String INNER_SHADOW = "inner-shadow";
    private static final String SHADOW = "shadow";

    private @NonNull CssEnumConverter<BlurType> blurTypeConverter = new CssEnumConverter<>(BlurType.class, false);
    private @NonNull CssEnumConverter<BlendMode> blendModeConverter = new CssEnumConverter<>(BlendMode.class, false);
    private @NonNull CssColorConverter colorConverter = new CssColorConverter(false);

    @Override
    public <TT extends Effect> void produceTokens(@Nullable TT value, @Nullable IdSupplier idSupplier, @NonNull Consumer<CssToken> out) {
        Deque<Effect> effects = new ArrayDeque<Effect>();
        for (Effect chainedEffect = value; chainedEffect != null; ) {
            effects.add(chainedEffect);
            try {
                Object inputEffect = chainedEffect.getClass().getDeclaredMethod("getInput", Effect.class).invoke(chainedEffect);
                if (inputEffect instanceof Effect) {
                    chainedEffect = (Effect) inputEffect;
                    effects.addFirst(chainedEffect);
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                chainedEffect = null;
            }
        }

        boolean first = true;
        for (Effect eff : effects) {
            if (first) {
                first = false;
            } else {
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_S, " "));
            }

            if (eff instanceof Blend) {
                Blend fx = (Blend) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, BLEND));
                blendModeConverter.produceTokens(fx.getMode(), idSupplier, out);
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                //FIXME
            /* if (fx.getInput() != null) {
                out.accept(new CssToken(CssTokenType.,", ");
                toString(out, idFactory, fx.getInput());
            }*/
            } else if (eff instanceof Bloom) {
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, BLOOM));
                Bloom fx = (Bloom) eff;
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getThreshold() * 100));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else if (eff instanceof BoxBlur) {
                BoxBlur fx = (BoxBlur) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, BOX_BLUR));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getWidth()));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getHeight()));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getIterations()));
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else if (eff instanceof ColorAdjust) {
                ColorAdjust fx = (ColorAdjust) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, COLOR_ADJUST));
                boolean needComma = false;
                final double hue = fx.getHue();
                final double saturation = fx.getSaturation();
                final double brightness = fx.getBrightness();
                final double contrast = fx.getContrast();
                boolean all = hue == 0 && saturation == 0 && brightness == 0 && contrast == 0;
                if (hue != 0 || all) {
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "hue"));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    if (hue > 0) {
                        out.accept(new CssToken(CssTokenType.TT_PLUS));
                    }
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, hue * 100));
                    needComma = true;
                }
                if (saturation != 0 || all) {
                    if (needComma) {
                        out.accept(new CssToken(CssTokenType.TT_COMMA));
                    }
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "saturation"));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    if (saturation > 0) {
                        out.accept(new CssToken(CssTokenType.TT_PLUS));
                    }
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, saturation * 100));
                    needComma = true;
                }
                if (brightness != 0 || all) {
                    if (needComma) {
                        out.accept(new CssToken(CssTokenType.TT_COMMA));
                    }
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "brightness"));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    if (brightness > 0) {
                        out.accept(new CssToken(CssTokenType.TT_PLUS));
                    }
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, brightness * 100));
                    needComma = true;
                }
                if (contrast != 0 || all) {
                    if (needComma) {
                        out.accept(new CssToken(CssTokenType.TT_COMMA));
                    }
                    out.accept(new CssToken(CssTokenType.TT_IDENT, "contrast"));
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    if (contrast > 0) {
                        out.accept(new CssToken(CssTokenType.TT_PLUS));
                    }
                    out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, contrast * 100));
                }
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else if (eff instanceof DropShadow) {
                DropShadow fx = (DropShadow) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, DROP_SHADOW));
                blurTypeConverter.produceTokens(fx.getBlurType(), idSupplier, out);
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                colorConverter.produceTokens(new CssColor(fx.getColor()), idSupplier, out);
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getSpread() * 100));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetX()));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetY()));
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else if (eff instanceof GaussianBlur) {
                GaussianBlur fx = (GaussianBlur) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, GAUSSIAN_BLUR));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_COMMA));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else if (eff instanceof Glow) {
                Glow fx = (Glow) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, GLOW));
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getLevel() * 100));
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else if (eff instanceof InnerShadow) {
                InnerShadow fx = (InnerShadow) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, INNER_SHADOW));
                blurTypeConverter.produceTokens(fx.getBlurType(), idSupplier, out);
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                colorConverter.produceTokens(new CssColor(fx.getColor()), idSupplier, out);
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_PERCENTAGE, fx.getChoke() * 100));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetX()));
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getOffsetY()));
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else if (eff instanceof Shadow) {
                Shadow fx = (Shadow) eff;
                out.accept(new CssToken(CssTokenType.TT_FUNCTION, SHADOW));
                blurTypeConverter.produceTokens(fx.getBlurType(), idSupplier, out);
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                colorConverter.produceTokens(new CssColor(fx.getColor()), idSupplier, out);
                out.accept(new CssToken(CssTokenType.TT_COMMA));
                out.accept(new CssToken(CssTokenType.TT_NUMBER, fx.getRadius()));
                out.accept(new CssToken(CssTokenType.TT_RIGHT_BRACKET));
                if (fx.getInput() != null) {
                    out.accept(new CssToken(CssTokenType.TT_S, " "));
                    produceTokens(fx.getInput(), idSupplier, out);
                }
            } else {
                out.accept(new CssToken(CssTokenType.TT_IDENT, CssTokenType.IDENT_NONE));
            }
        }
    }


    @Override
    public @Nullable Effect parse(@NonNull CssTokenizer tt, @Nullable IdResolver idResolver) throws ParseException, IOException {
        if (tt.nextIsIdentNone()) {
            return null;
        }
        tt.pushBack();
        return parseEffect(tt);
    }

    private @Nullable Effect parseEffect(@NonNull CssTokenizer tt) throws ParseException, IOException {
        Effect first = null;
        Effect previous = null;
        while (tt.next() == CssTokenType.TT_FUNCTION) {

            Effect current;
            switch (tt.currentStringNonNull()) {
            case BLEND:
                current = parseBlend(tt);
                break;
            case BLOOM:
                current = parseBloom(tt);
                break;
            case BOX_BLUR:
                current = parseBoxBlur(tt);
                break;
            case COLOR_ADJUST:
                current = parseColorAdjust(tt);
                break;
            case DROP_SHADOW:
                current = parseDropShadow(tt);
                break;
            case GAUSSIAN_BLUR:
                current = parseGaussianBlur(tt);
                break;
            case GLOW:
                current = parseGlow(tt);
                break;
            case INNER_SHADOW:
                current = parseInnerShadow(tt);
                break;
            case SHADOW:
                current = parseShadow(tt);
                break;
            default:
                throw tt.createParseException("CSS Effect: \"" + BLEND + ", " + DROP_SHADOW + "(\" or \"" + INNER_SHADOW + "(\"  expected.");
            }
            if (first == null) {
                first = previous = current;
            } else {
                try {
                    previous.getClass().getDeclaredMethod("setInput", Effect.class).invoke(previous, current);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    ParseException pe = tt.createParseException("CSS Effect: can not combine effects.");
                    pe.initCause(ex);
                    throw pe;
                }
                previous = current;
            }

            if (tt.next() != ',') {
                tt.pushBack();
            }
        }
        return first;
    }

    private @NonNull Effect parseBlend(@NonNull CssTokenizer tt) throws ParseException, IOException {
        BlendMode mode = BlendMode.SRC_OVER;
        if (tt.next() == CssTokenType.TT_IDENT) {
            tt.pushBack();
            mode = blendModeConverter.parse(tt, null);
        }
        if (tt.next() != ')') {
            throw tt.createParseException("CSS Effect: ')' expected.");
        }
        return new Blend(mode);
    }

    private @NonNull Effect parseBloom(@NonNull CssTokenizer tt) throws ParseException, IOException {
        double threshold = 0.3;
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            threshold = tt.currentNumberNonNull().doubleValue();
            break;
        case CssTokenType.TT_PERCENTAGE:
            threshold = tt.currentNumberNonNull().doubleValue() / 100;
            break;
        default:
            tt.pushBack();
        }
        if (tt.next() != ')') {
            throw tt.createParseException("CSS Effect: ')' expected.");
        }
        return new Bloom(Geom.clamp(threshold, 0, 1));
    }

    private @NonNull Effect parseBoxBlur(@NonNull CssTokenizer tt) throws ParseException, IOException {
        double width = 5;
        double height = 5;
        int iterations = 1;
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            width = Geom.clamp(tt.currentNumberNonNull().doubleValue(), 0, 255);
            break;
        default:
            tt.pushBack();
        }
        if (tt.next() != ',') {
            tt.pushBack();
        }
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            height = Geom.clamp(tt.currentNumberNonNull().doubleValue(), 0, 255);
            break;
        default:
            tt.pushBack();
        }
        if (tt.next() != ',') {
            tt.pushBack();
        }
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            iterations = Geom.clamp(tt.currentNumberNonNull().intValue(), 0, 3);
            break;
        default:
            tt.pushBack();
        }
        if (tt.next() != ')') {
            throw tt.createParseException("CSS Effect: ')' expected.");
        }
        return new BoxBlur(width, height, iterations);
    }

    private @NonNull Effect parseColorAdjust(@NonNull CssTokenizer tt) throws ParseException, IOException {
        double hue = 0.0;
        double saturation = 0.0;
        double brightness = 0.0;
        double contrast = 0.0;
        while (tt.next() == CssTokenType.TT_IDENT) {
            String ident = tt.currentStringNonNull();
            double adjust = 0.0;
            switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                adjust = tt.currentNumberNonNull().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                adjust = tt.currentNumberNonNull().doubleValue() / 100;
                break;
            default:
                tt.pushBack();
            }
            adjust = Geom.clamp(adjust, 0, 1);
            switch (ident) {
            case "hue":
                hue = adjust;
                break;
            case "saturation":
                saturation = adjust;
                break;
            case "brightness":
                brightness = adjust;
                break;
            case "contrast":
                contrast = adjust;
                break;
            default:
                throw tt.createParseException("CSS \"hue\", \"saturation\", \"brightness\", or \"contrast\" expected.");
            }
            if (tt.next() != ',') {
                tt.pushBack();
            }
        }
        if (tt.current() != ')') {
            throw tt.createParseException("CSS Effect: ')' expected.");
        }
        return new ColorAdjust(hue, saturation, brightness, contrast);
    }

    private @NonNull Effect parseDropShadow(@NonNull CssTokenizer tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, true);
    }

    private @NonNull Effect parseGaussianBlur(@NonNull CssTokenizer tt) throws ParseException, IOException {
        double radius = 5;
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            radius = Geom.clamp(tt.currentNumberNonNull().doubleValue(), 0, 63);
            break;
        default:
            tt.pushBack();
        }
        if (tt.next() != ')') {
            throw tt.createParseException("CSS Effect: ')' expected.");
        }
        return new GaussianBlur(radius);
    }

    private @NonNull Effect parseInnerShadow(@NonNull CssTokenizer tt) throws ParseException, IOException {
        return parseDropShadowOrInnerShadow(tt, false);
    }

    private @NonNull Effect parseGlow(@NonNull CssTokenizer tt) throws ParseException, IOException {
        double level = 0.3;
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            level = tt.currentNumberNonNull().doubleValue();
            break;
        case CssTokenType.TT_PERCENTAGE:
            level = tt.currentNumberNonNull().doubleValue() / 100;
            break;
        default:
            tt.pushBack();
        }
        if (tt.next() != ')') {
            throw tt.createParseException("CSS Effect: ')' expected.");
        }
        return new Glow(Geom.clamp(level, 0, 1));
    }

    private @NonNull Effect parseDropShadowOrInnerShadow(@NonNull CssTokenizer tt, boolean isDropShadow) throws ParseException, IOException {
        String func = isDropShadow ? DROP_SHADOW : INNER_SHADOW;
        BlurType blurType = BlurType.GAUSSIAN;
        Color color = new Color(0, 0, 0, 0.25);
        double radius = 10.0;
        double spreadOrChocke = 0.0;
        double offsetX = 0.0;
        double offsetY = 4.0;
        Effect input = null;

        if (tt.next() != ')') {
            if (tt.current() != CssTokenType.TT_IDENT) {
                throw tt.createParseException("CSS Effect: " + func + "(<blur-type>,color,radius,spread,offset-x,offset-y) expected.");
            }
            tt.pushBack();
            blurType = blurTypeConverter.parseNonNull(tt, null);

            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() == CssTokenType.TT_HASH) {
                color = Color.web('#' + tt.currentString());
            } else if (tt.current() == CssTokenType.TT_IDENT) {
                color = Color.web(tt.currentStringNonNull());
            } else if (tt.current() == CssTokenType.TT_FUNCTION) {
                tt.pushBack();
                CssColor colorOrNull = colorConverter.parse(tt, null);
                color = colorOrNull == null ? Color.BLACK : colorOrNull.getColor();
            } else {
                throw tt.createParseException("CSS Effect: " + func + "(" + blurType.toString().toLowerCase().replace('_', '-') + ",  <color> expected.");
            }
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw tt.createParseException("CSS Effect: radius number expected.");
            }
            radius = tt.currentNumberNonNull().doubleValue();

            if (tt.next() != ',') {
                tt.pushBack();
            }
            switch (tt.next()) {
            case CssTokenType.TT_NUMBER:
                spreadOrChocke = tt.currentNumberNonNull().doubleValue();
                break;
            case CssTokenType.TT_PERCENTAGE:
                spreadOrChocke = tt.currentNumberNonNull().doubleValue() / 100.0;
                break;
            default:
                throw tt.createParseException("CSS Shadow-Effect: spread or chocke number expected.");
            }
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw tt.createParseException("CSS Shadow-Effect: offset-x number expected.");
            }
            offsetX = tt.currentNumberNonNull().doubleValue();
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw tt.createParseException("CSS Shadow-Effect: offset-y number expected.");
            }
            offsetY = tt.currentNumberNonNull().doubleValue();
            if (tt.next() != ',') {
                tt.pushBack();
            } else {
                input = parseEffect(tt);
            }
            if (tt.next() != ')') {
                throw tt.createParseException("CSS Shadow-Effect: ')' expected.");
            }
        }

        final Effect effect;
        if (isDropShadow) {
            DropShadow dropShadow = new DropShadow(blurType, color, Geom.clamp(radius, 0, 127), spreadOrChocke, offsetX, offsetY);
            if (input != null) {
                dropShadow.setInput(input);
            }
            effect = dropShadow;
        } else {
            InnerShadow innerhShadow = new InnerShadow(blurType, color, Geom.clamp(radius, 0, 127), spreadOrChocke, offsetX, offsetY);
            if (input != null) {
                innerhShadow.setInput(input);
            }
            effect = innerhShadow;
        }
        return effect;
    }

    private @NonNull Effect parseShadow(@NonNull CssTokenizer tt) throws ParseException, IOException {
        String func = SHADOW;
        BlurType blurType = BlurType.GAUSSIAN;
        Color color = new Color(0, 0, 0, 0.75);
        double radius = 10.0;

        if (tt.next() != ')') {
            if (tt.current() != CssTokenType.TT_IDENT) {
                throw tt.createParseException("CSS Effect: " + func + "(<blur-type>,color,radius,spread,offset-x,offset-y) expected.");
            }
            tt.pushBack();
            blurType = blurTypeConverter.parseNonNull(tt, null);

            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() == CssTokenType.TT_HASH) {
                color = Color.web('#' + tt.currentStringNonNull());
            } else if (tt.current() == CssTokenType.TT_IDENT) {
                color = Color.web(tt.currentStringNonNull());
            } else if (tt.current() == CssTokenType.TT_FUNCTION) {
                tt.pushBack();
                CssColor colorOrNull = colorConverter.parse(tt, null);
                color = colorOrNull == null ? Color.BLACK : colorOrNull.getColor();
            } else {
                throw tt.createParseException("CSS Effect: " + func + "(" + blurType.toString().toLowerCase().replace('_', '-') + ",  <color> expected.");
            }
            if (tt.next() != ',') {
                tt.pushBack();
            }
            if (tt.next() != CssTokenType.TT_NUMBER) {
                throw tt.createParseException("CSS Effect: radius number expected.");
            }
            radius = tt.currentNumberNonNull().doubleValue();

            if (tt.next() != ')') {
                throw tt.createParseException("CSS Effect: ')' expected.");
            }
        }
        return new Shadow(blurType, color, Geom.clamp(radius, 0, 127));
    }


    @Override
    public @Nullable Effect getDefaultValue() {
        return null;
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public @NonNull String getHelpText() {
        return "Format of ⟨Effect⟩: none｜（⟨Blend⟩｜⟨Bloom⟩｜⟨BoxBlur⟩｜⟨ColorAdjust⟩｜⟨DropShadow⟩｜⟨GaussianBlur⟩｜ ⟨InnerShadow⟩）｛, ⟨Effect⟩｝"
                + "\nFormat of ⟨Blend⟩: blend(⟨BlendMode⟩)"
                + "\nFormat of ⟨Bloom⟩: bloom(⟨luminosity⟩%)"
                + "\nFormat of ⟨BoxBlur⟩: box-blur(⟨width⟩,⟨height⟩,⟨iterations⟩)"
                + "\nFormat of ⟨ColorAdjust⟩: color-adjust(hue ±⟨h⟩%, saturation ±⟨s⟩%, brightness ±⟨b⟩%, contrast ±⟨c⟩%)"
                + "\nFormat of ⟨DropShadow⟩: drop-shadow(⟨BlurType⟩,⟨Color⟩,⟨radius⟩,⟨spread⟩,⟨xoffset⟩,⟨yoffset⟩)"
                + "\nFormat of ⟨GaussianBlur⟩: gaussian-blur(⟨radius⟩)"
                + "\nFormat of ⟨InnerShadow⟩: inner-shadow(⟨BlurType⟩,⟨Color⟩,⟨radius⟩,⟨choke⟩,⟨xoffset⟩,⟨yoffset⟩)"
                + "\nFormat of ⟨Shadow⟩: shadow(⟨BlurType⟩,⟨Color⟩,⟨radius⟩)"
                + "\n" + blendModeConverter.getHelpText()
                + "\n" + blurTypeConverter.getHelpText()
                + "\n" + colorConverter.getHelpText();
    }
}

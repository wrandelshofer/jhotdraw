/*
 * @(#)LadderCssFunction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.function;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssFunctionProcessor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssToken;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.DefaultUnitConverter;
import org.jhotdraw8.css.SelectorModel;
import org.jhotdraw8.css.UnitConverter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Processes the ladder() function.
 * <pre>
 *     ladder = "ladder(" , s* , color , "," , color-stop , { "," , color-stop } , ")" ;
 *     color = (* a css color *);
 *     color-stop = color , number-or-percentage ;
 *     number-or-percentage = number | percentage ;
 * </pre>
 * The ladder function interpolates between colors.
 * The effect is as if a gradient is created using the stops provided, and then
 * the brightness of the provided <color> is used to index a color value within
 * that gradient. At 0% brightness, the color at the 0.0 end of the gradient is
 * used; at 100% brightness, the color at the 1.0 end of the gradient is used;
 * and at 50% brightness, the color at 0.5, the midway point of the gradient,
 * is used. Note that no gradient is actually rendered. This is merely an
 * interpolation function that results in a single color.
 * <p>
 * References:
 * <ul>
 * <li>JavaFX CSS Reference Guide.
 * <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html">oracle.com</a>
 * </li>
 * </ul>
 *
 * @param <T>
 */
public class LadderCssFunction<T> extends AbstractColorCssFunction<T> {
    public final static String NAME = "ladder";

    public LadderCssFunction() {
        this(NAME);
    }

    public LadderCssFunction(String name) {
        super(name);
    }

    @Override
    public void process(@NonNull T element, @NonNull CssTokenizer tt, @NonNull SelectorModel<T> model, @NonNull CssFunctionProcessor<T> functionProcessor, @NonNull Consumer<CssToken> out, int recursionDepth) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "〈" + getName() + "〉: function " + getName() + "() expected.");
        if (!getName().equals(tt.currentString())) {
            throw tt.createParseException("〈" + getName() + "〉: function " + getName() + "() expected.");
        }
        final CssColor primaryColor = parseColorValue(element, tt, functionProcessor);

        final TreeMap<Double, List<CssColor>> ladder = new TreeMap<>();
        while (tt.next() == CssTokenType.TT_COMMA) {
            final CssColor ladderColor = parseColorValue(element, tt, functionProcessor);
            final CssSize ladderPercentage = parsePercentageValue(element, tt, functionProcessor);
            final double percentageValue = DefaultUnitConverter.getInstance().convert(ladderPercentage, UnitConverter.DEFAULT);
            ladder.computeIfAbsent(percentageValue, k -> new ArrayList<>()).add(ladderColor);
        }
        tt.pushBack();
        tt.requireNextToken(CssTokenType.TT_RIGHT_BRACKET, "〈" + getName() + "〉: ')' expected.");

        double brightness = primaryColor == null || primaryColor.getColor() == null ? 0.0
                : primaryColor.getColor().getBrightness();
        CssColor computedColor = interpolate(brightness, ladder);
        converter.produceTokens(computedColor, null, out);
    }

    /**
     * Picks a color from the ladder.
     *
     * @param brightness the brightness is used to find the most appropriate color
     * @param ladder     the ladder
     * @return a color from the ladder
     */
    private CssColor interpolate(double brightness, TreeMap<Double, List<CssColor>> ladder) {
        Map.Entry<Double, List<CssColor>> entry = ladder.floorEntry(brightness);
        if (entry == null) {
            entry = ladder.firstEntry();
        }
        if (entry == null) {
            return null;
        }

        final List<CssColor> list = entry.getValue();
        return brightness <= entry.getKey()
                ? list.get(0)
                : list.get(list.size() - 1);
    }

    @Nullable
    protected CssSize parsePercentageValue(@NonNull T element, @NonNull CssTokenizer tt, CssFunctionProcessor<T> functionProcessor) throws IOException, ParseException {
        CssSize size = null;
        switch (tt.next()) {
        case CssTokenType.TT_NUMBER:
            size = new CssSize(tt.currentNumberNonNull().doubleValue());
            break;
        case CssTokenType.TT_PERCENTAGE:
            size = new CssSize(tt.currentNumberNonNull().doubleValue(), UnitConverter.PERCENTAGE);
            break;
        default:
            throw tt.createParseException("〈percentage-value〉: percentage value expected.");
        }
        return size;

    }


    @Override
    public String getHelpText() {
        return getName() + "(⟨color⟩, ⟨color⟩ ⟨percentage⟩, ⟨color⟩ ⟨percentage⟩ ... )"
                + "\n    The ladder function interpolates between colors. " +
                "The effect is as if a gradient is created using the stops provided, " +
                "and then the brightness of the provided <color> is used to index a " +
                "color value within that gradient. At 0% brightness, the color" +
                " at the 0.0 end of the gradient is used; at 100% brightness, " +
                "the color at the 1.0 end of the gradient is used; and at 50% brightness, " +
                "the color at 0.5, the midway point of the gradient, is used." +
                "";
    }
}

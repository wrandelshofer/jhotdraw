/*
 * @(#)NamedCssColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableMap;
import org.jhotdraw8.collection.ImmutableMaps;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a named color in a cascading stylesheet.
 * <p>
 * References:
 * <dl>
 *     <dt>CSS Color Module Level 4, Named Colors</dt>
 *     <dd><a href="https://www.w3.org/TR/css-color-4/#named-colors">w3.org/<a></a></a></dd>
 * </dl>
 */

public class NamedCssColor extends CssColor {
    /**
     * Creates a new named color with the specified name.
     *
     * @param name  the name
     * @param color the color
     */
    public NamedCssColor(@NonNull String name, @NonNull Color color) {
        super(name, color);
    }

    /**
     * Creates a new named color with the specified name.
     *
     * @param name the name
     * @param rgb  the color
     */
    public NamedCssColor(@NonNull String name, int rgb) {
        super(name,
                Color.rgb((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff));
    }

    public static final @NonNull NamedCssColor TRANSPARENT = new NamedCssColor(NamedColorName.TRANSPARENT, Color.TRANSPARENT);


    public static final @NonNull NamedCssColor ALICEBLUE = new NamedCssColor(NamedColorName.ALICEBLUE, 0xF0F8FF);
    public static final @NonNull NamedCssColor ANTIQUEWHITE = new NamedCssColor(NamedColorName.ANTIQUEWHITE, 0xFAEBD7);
    public static final @NonNull NamedCssColor AQUA = new NamedCssColor(NamedColorName.AQUA, 0x00FFFF);
    public static final @NonNull NamedCssColor AQUAMARINE = new NamedCssColor(NamedColorName.AQUAMARINE, 0x7FFFD4);
    public static final @NonNull NamedCssColor AZURE = new NamedCssColor(NamedColorName.AZURE, 0xF0FFFF);
    public static final @NonNull NamedCssColor BEIGE = new NamedCssColor(NamedColorName.BEIGE, 0xF5F5DC);
    public static final @NonNull NamedCssColor BISQUE = new NamedCssColor(NamedColorName.BISQUE, 0xFFE4C4);
    public static final @NonNull NamedCssColor BLACK = new NamedCssColor(NamedColorName.BLACK, 0x000000);
    public static final @NonNull NamedCssColor BLANCHEDALMOND = new NamedCssColor(NamedColorName.BLANCHEDALMOND, 0xFFEBCD);
    public static final @NonNull NamedCssColor BLUE = new NamedCssColor(NamedColorName.BLUE, 0x0000FF);
    public static final @NonNull NamedCssColor BLUEVIOLET = new NamedCssColor(NamedColorName.BLUEVIOLET, 0x8A2BE2);
    public static final @NonNull NamedCssColor BROWN = new NamedCssColor(NamedColorName.BROWN, 0xA52A2A);
    public static final @NonNull NamedCssColor BURLYWOOD = new NamedCssColor(NamedColorName.BURLYWOOD, 0xDEB887);
    public static final @NonNull NamedCssColor CADETBLUE = new NamedCssColor(NamedColorName.CADETBLUE, 0x5F9EA0);
    public static final @NonNull NamedCssColor CHARTREUSE = new NamedCssColor(NamedColorName.CHARTREUSE, 0x7FFF00);
    public static final @NonNull NamedCssColor CHOCOLATE = new NamedCssColor(NamedColorName.CHOCOLATE, 0xD2691E);
    public static final @NonNull NamedCssColor CORAL = new NamedCssColor(NamedColorName.CORAL, 0xFF7F50);
    public static final @NonNull NamedCssColor CORNFLOWERBLUE = new NamedCssColor(NamedColorName.CORNFLOWERBLUE, 0x6495ED);
    public static final @NonNull NamedCssColor CORNSILK = new NamedCssColor(NamedColorName.CORNSILK, 0xFFF8DC);
    public static final @NonNull NamedCssColor CRIMSON = new NamedCssColor(NamedColorName.CRIMSON, 0xDC143C);
    public static final @NonNull NamedCssColor CYAN = new NamedCssColor(NamedColorName.CYAN, 0x00FFFF);
    public static final @NonNull NamedCssColor DARKBLUE = new NamedCssColor(NamedColorName.DARKBLUE, 0x00008B);
    public static final @NonNull NamedCssColor DARKCYAN = new NamedCssColor(NamedColorName.DARKCYAN, 0x008B8B);
    public static final @NonNull NamedCssColor DARKGOLDENROD = new NamedCssColor(NamedColorName.DARKGOLDENROD, 0xB8860B);
    public static final @NonNull NamedCssColor DARKGRAY = new NamedCssColor(NamedColorName.DARKGRAY, 0xA9A9A9);
    public static final @NonNull NamedCssColor DARKGREEN = new NamedCssColor(NamedColorName.DARKGREEN, 0x006400);
    public static final @NonNull NamedCssColor DARKGREY = new NamedCssColor(NamedColorName.DARKGREY, 0xA9A9A9);
    public static final @NonNull NamedCssColor DARKKHAKI = new NamedCssColor(NamedColorName.DARKKHAKI, 0xBDB76B);
    public static final @NonNull NamedCssColor DARKMAGENTA = new NamedCssColor(NamedColorName.DARKMAGENTA, 0x8B008B);
    public static final @NonNull NamedCssColor DARKOLIVEGREEN = new NamedCssColor(NamedColorName.DARKOLIVEGREEN, 0x556B2F);
    public static final @NonNull NamedCssColor DARKORANGE = new NamedCssColor(NamedColorName.DARKORANGE, 0xFF8C00);
    public static final @NonNull NamedCssColor DARKORCHID = new NamedCssColor(NamedColorName.DARKORCHID, 0x9932CC);
    public static final @NonNull NamedCssColor DARKRED = new NamedCssColor(NamedColorName.DARKRED, 0x8B0000);
    public static final @NonNull NamedCssColor DARKSALMON = new NamedCssColor(NamedColorName.DARKSALMON, 0xE9967A);
    public static final @NonNull NamedCssColor DARKSEAGREEN = new NamedCssColor(NamedColorName.DARKSEAGREEN, 0x8FBC8F);
    public static final @NonNull NamedCssColor DARKSLATEBLUE = new NamedCssColor(NamedColorName.DARKSLATEBLUE, 0x483D8B);
    public static final @NonNull NamedCssColor DARKSLATEGRAY = new NamedCssColor(NamedColorName.DARKSLATEGRAY, 0x2F4F4F);
    public static final @NonNull NamedCssColor DARKSLATEGREY = new NamedCssColor(NamedColorName.DARKSLATEGREY, 0x2F4F4F);
    public static final @NonNull NamedCssColor DARKTURQUOISE = new NamedCssColor(NamedColorName.DARKTURQUOISE, 0x00CED1);
    public static final @NonNull NamedCssColor DARKVIOLET = new NamedCssColor(NamedColorName.DARKVIOLET, 0x9400D3);
    public static final @NonNull NamedCssColor DEEPPINK = new NamedCssColor(NamedColorName.DEEPPINK, 0xFF1493);
    public static final @NonNull NamedCssColor DEEPSKYBLUE = new NamedCssColor(NamedColorName.DEEPSKYBLUE, 0x00BFFF);
    public static final @NonNull NamedCssColor DIMGRAY = new NamedCssColor(NamedColorName.DIMGRAY, 0x696969);
    public static final @NonNull NamedCssColor DIMGREY = new NamedCssColor(NamedColorName.DIMGREY, 0x696969);
    public static final @NonNull NamedCssColor DODGERBLUE = new NamedCssColor(NamedColorName.DODGERBLUE, 0x1E90FF);
    public static final @NonNull NamedCssColor FIREBRICK = new NamedCssColor(NamedColorName.FIREBRICK, 0xB22222);
    public static final @NonNull NamedCssColor FLORALWHITE = new NamedCssColor(NamedColorName.FLORALWHITE, 0xFFFAF0);
    public static final @NonNull NamedCssColor FORESTGREEN = new NamedCssColor(NamedColorName.FORESTGREEN, 0x228B22);
    public static final @NonNull NamedCssColor FUCHSIA = new NamedCssColor(NamedColorName.FUCHSIA, 0xFF00FF);
    public static final @NonNull NamedCssColor GAINSBORO = new NamedCssColor(NamedColorName.GAINSBORO, 0xDCDCDC);
    public static final @NonNull NamedCssColor GHOSTWHITE = new NamedCssColor(NamedColorName.GHOSTWHITE, 0xF8F8FF);
    public static final @NonNull NamedCssColor GOLD = new NamedCssColor(NamedColorName.GOLD, 0xFFD700);
    public static final @NonNull NamedCssColor GOLDENROD = new NamedCssColor(NamedColorName.GOLDENROD, 0xDAA520);
    public static final @NonNull NamedCssColor GRAY = new NamedCssColor(NamedColorName.GRAY, 0x808080);
    public static final @NonNull NamedCssColor GREEN = new NamedCssColor(NamedColorName.GREEN, 0x008000);
    public static final @NonNull NamedCssColor GREENYELLOW = new NamedCssColor(NamedColorName.GREENYELLOW, 0xADFF2F);
    public static final @NonNull NamedCssColor GREY = new NamedCssColor(NamedColorName.GREY, 0x808080);
    public static final @NonNull NamedCssColor HONEYDEW = new NamedCssColor(NamedColorName.HONEYDEW, 0xF0FFF0);
    public static final @NonNull NamedCssColor HOTPINK = new NamedCssColor(NamedColorName.HOTPINK, 0xFF69B4);
    public static final @NonNull NamedCssColor INDIANRED = new NamedCssColor(NamedColorName.INDIANRED, 0xCD5C5C);
    public static final @NonNull NamedCssColor INDIGO = new NamedCssColor(NamedColorName.INDIGO, 0x4B0082);
    public static final @NonNull NamedCssColor IVORY = new NamedCssColor(NamedColorName.IVORY, 0xFFFFF0);
    public static final @NonNull NamedCssColor KHAKI = new NamedCssColor(NamedColorName.KHAKI, 0xF0E68C);
    public static final @NonNull NamedCssColor LAVENDER = new NamedCssColor(NamedColorName.LAVENDER, 0xE6E6FA);
    public static final @NonNull NamedCssColor LAVENDERBLUSH = new NamedCssColor(NamedColorName.LAVENDERBLUSH, 0xFFF0F5);
    public static final @NonNull NamedCssColor LAWNGREEN = new NamedCssColor(NamedColorName.LAWNGREEN, 0x7CFC00);
    public static final @NonNull NamedCssColor LEMONCHIFFON = new NamedCssColor(NamedColorName.LEMONCHIFFON, 0xFFFACD);
    public static final @NonNull NamedCssColor LIGHTBLUE = new NamedCssColor(NamedColorName.LIGHTBLUE, 0xADD8E6);
    public static final @NonNull NamedCssColor LIGHTCORAL = new NamedCssColor(NamedColorName.LIGHTCORAL, 0xF08080);
    public static final @NonNull NamedCssColor LIGHTCYAN = new NamedCssColor(NamedColorName.LIGHTCYAN, 0xE0FFFF);
    public static final @NonNull NamedCssColor LIGHTGOLDENRODYELLOW = new NamedCssColor(NamedColorName.LIGHTGOLDENRODYELLOW, 0xFAFAD2);
    public static final @NonNull NamedCssColor LIGHTGRAY = new NamedCssColor(NamedColorName.LIGHTGRAY, 0xD3D3D3);
    public static final @NonNull NamedCssColor LIGHTGREEN = new NamedCssColor(NamedColorName.LIGHTGREEN, 0x90EE90);
    public static final @NonNull NamedCssColor LIGHTGREY = new NamedCssColor(NamedColorName.LIGHTGREY, 0xD3D3D3);
    public static final @NonNull NamedCssColor LIGHTPINK = new NamedCssColor(NamedColorName.LIGHTPINK, 0xFFB6C1);
    public static final @NonNull NamedCssColor LIGHTSALMON = new NamedCssColor(NamedColorName.LIGHTSALMON, 0xFFA07A);
    public static final @NonNull NamedCssColor LIGHTSEAGREEN = new NamedCssColor(NamedColorName.LIGHTSEAGREEN, 0x20B2AA);
    public static final @NonNull NamedCssColor LIGHTSKYBLUE = new NamedCssColor(NamedColorName.LIGHTSKYBLUE, 0x87CEFA);
    public static final @NonNull NamedCssColor LIGHTSLATEGRAY = new NamedCssColor(NamedColorName.LIGHTSLATEGRAY, 0x778899);
    public static final @NonNull NamedCssColor LIGHTSLATEGREY = new NamedCssColor(NamedColorName.LIGHTSLATEGREY, 0x778899);
    public static final @NonNull NamedCssColor LIGHTSTEELBLUE = new NamedCssColor(NamedColorName.LIGHTSTEELBLUE, 0xB0C4DE);
    public static final @NonNull NamedCssColor LIGHTYELLOW = new NamedCssColor(NamedColorName.LIGHTYELLOW, 0xFFFFE0);
    public static final @NonNull NamedCssColor LIME = new NamedCssColor(NamedColorName.LIME, 0x00FF00);
    public static final @NonNull NamedCssColor LIMEGREEN = new NamedCssColor(NamedColorName.LIMEGREEN, 0x32CD32);
    public static final @NonNull NamedCssColor LINEN = new NamedCssColor(NamedColorName.LINEN, 0xFAF0E6);
    public static final @NonNull NamedCssColor MAGENTA = new NamedCssColor(NamedColorName.MAGENTA, 0xFF00FF);
    public static final @NonNull NamedCssColor MAROON = new NamedCssColor(NamedColorName.MAROON, 0x800000);
    public static final @NonNull NamedCssColor MEDIUMAQUAMARINE = new NamedCssColor(NamedColorName.MEDIUMAQUAMARINE, 0x66CDAA);
    public static final @NonNull NamedCssColor MEDIUMBLUE = new NamedCssColor(NamedColorName.MEDIUMBLUE, 0x0000CD);
    public static final @NonNull NamedCssColor MEDIUMORCHID = new NamedCssColor(NamedColorName.MEDIUMORCHID, 0xBA55D3);
    public static final @NonNull NamedCssColor MEDIUMPURPLE = new NamedCssColor(NamedColorName.MEDIUMPURPLE, 0x9370DB);
    public static final @NonNull NamedCssColor MEDIUMSEAGREEN = new NamedCssColor(NamedColorName.MEDIUMSEAGREEN, 0x3CB371);
    public static final @NonNull NamedCssColor MEDIUMSLATEBLUE = new NamedCssColor(NamedColorName.MEDIUMSLATEBLUE, 0x7B68EE);
    public static final @NonNull NamedCssColor MEDIUMSPRINGGREEN = new NamedCssColor(NamedColorName.MEDIUMSPRINGGREEN, 0x00FA9A);
    public static final @NonNull NamedCssColor MEDIUMTURQUOISE = new NamedCssColor(NamedColorName.MEDIUMTURQUOISE, 0x48D1CC);
    public static final @NonNull NamedCssColor MEDIUMVIOLETRED = new NamedCssColor(NamedColorName.MEDIUMVIOLETRED, 0xC71585);
    public static final @NonNull NamedCssColor MIDNIGHTBLUE = new NamedCssColor(NamedColorName.MIDNIGHTBLUE, 0x191970);
    public static final @NonNull NamedCssColor MINTCREAM = new NamedCssColor(NamedColorName.MINTCREAM, 0xF5FFFA);
    public static final @NonNull NamedCssColor MISTYROSE = new NamedCssColor(NamedColorName.MISTYROSE, 0xFFE4E1);
    public static final @NonNull NamedCssColor MOCCASIN = new NamedCssColor(NamedColorName.MOCCASIN, 0xFFE4B5);
    public static final @NonNull NamedCssColor NAVAJOWHITE = new NamedCssColor(NamedColorName.NAVAJOWHITE, 0xFFDEAD);
    public static final @NonNull NamedCssColor NAVY = new NamedCssColor(NamedColorName.NAVY, 0x000080);
    public static final @NonNull NamedCssColor OLDLACE = new NamedCssColor(NamedColorName.OLDLACE, 0xFDF5E6);
    public static final @NonNull NamedCssColor OLIVE = new NamedCssColor(NamedColorName.OLIVE, 0x808000);
    public static final @NonNull NamedCssColor OLIVEDRAB = new NamedCssColor(NamedColorName.OLIVEDRAB, 0x6B8E23);
    public static final @NonNull NamedCssColor ORANGE = new NamedCssColor(NamedColorName.ORANGE, 0xFFA500);
    public static final @NonNull NamedCssColor ORANGERED = new NamedCssColor(NamedColorName.ORANGERED, 0xFF4500);
    public static final @NonNull NamedCssColor ORCHID = new NamedCssColor(NamedColorName.ORCHID, 0xDA70D6);
    public static final @NonNull NamedCssColor PALEGOLDENROD = new NamedCssColor(NamedColorName.PALEGOLDENROD, 0xEEE8AA);
    public static final @NonNull NamedCssColor PALEGREEN = new NamedCssColor(NamedColorName.PALEGREEN, 0x98FB98);
    public static final @NonNull NamedCssColor PALETURQUOISE = new NamedCssColor(NamedColorName.PALETURQUOISE, 0xAFEEEE);
    public static final @NonNull NamedCssColor PALEVIOLETRED = new NamedCssColor(NamedColorName.PALEVIOLETRED, 0xDB7093);
    public static final @NonNull NamedCssColor PAPAYAWHIP = new NamedCssColor(NamedColorName.PAPAYAWHIP, 0xFFEFD5);
    public static final @NonNull NamedCssColor PEACHPUFF = new NamedCssColor(NamedColorName.PEACHPUFF, 0xFFDAB9);
    public static final @NonNull NamedCssColor PERU = new NamedCssColor(NamedColorName.PERU, 0xCD853F);
    public static final @NonNull NamedCssColor PINK = new NamedCssColor(NamedColorName.PINK, 0xFFC0CB);
    public static final @NonNull NamedCssColor PLUM = new NamedCssColor(NamedColorName.PLUM, 0xDDA0DD);
    public static final @NonNull NamedCssColor POWDERBLUE = new NamedCssColor(NamedColorName.POWDERBLUE, 0xB0E0E6);
    public static final @NonNull NamedCssColor PURPLE = new NamedCssColor(NamedColorName.PURPLE, 0x800080);
    public static final @NonNull NamedCssColor REBECCAPURPLE = new NamedCssColor(NamedColorName.REBECCAPURPLE, 0x663399);
    public static final @NonNull NamedCssColor RED = new NamedCssColor(NamedColorName.RED, 0xFF0000);
    public static final @NonNull NamedCssColor ROSYBROWN = new NamedCssColor(NamedColorName.ROSYBROWN, 0xBC8F8F);
    public static final @NonNull NamedCssColor ROYALBLUE = new NamedCssColor(NamedColorName.ROYALBLUE, 0x4169E1);
    public static final @NonNull NamedCssColor SADDLEBROWN = new NamedCssColor(NamedColorName.SADDLEBROWN, 0x8B4513);
    public static final @NonNull NamedCssColor SALMON = new NamedCssColor(NamedColorName.SALMON, 0xFA8072);
    public static final @NonNull NamedCssColor SANDYBROWN = new NamedCssColor(NamedColorName.SANDYBROWN, 0xF4A460);
    public static final @NonNull NamedCssColor SEAGREEN = new NamedCssColor(NamedColorName.SEAGREEN, 0x2E8B57);
    public static final @NonNull NamedCssColor SEASHELL = new NamedCssColor(NamedColorName.SEASHELL, 0xFFF5EE);
    public static final @NonNull NamedCssColor SIENNA = new NamedCssColor(NamedColorName.SIENNA, 0xA0522D);
    public static final @NonNull NamedCssColor SILVER = new NamedCssColor(NamedColorName.SILVER, 0xC0C0C0);
    public static final @NonNull NamedCssColor SKYBLUE = new NamedCssColor(NamedColorName.SKYBLUE, 0x87CEEB);
    public static final @NonNull NamedCssColor SLATEBLUE = new NamedCssColor(NamedColorName.SLATEBLUE, 0x6A5ACD);
    public static final @NonNull NamedCssColor SLATEGRAY = new NamedCssColor(NamedColorName.SLATEGRAY, 0x708090);
    public static final @NonNull NamedCssColor SLATEGREY = new NamedCssColor(NamedColorName.SLATEGREY, 0x708090);
    public static final @NonNull NamedCssColor SNOW = new NamedCssColor(NamedColorName.SNOW, 0xFFFAFA);
    public static final @NonNull NamedCssColor SPRINGGREEN = new NamedCssColor(NamedColorName.SPRINGGREEN, 0x00FF7F);
    public static final @NonNull NamedCssColor STEELBLUE = new NamedCssColor(NamedColorName.STEELBLUE, 0x4682B4);
    public static final @NonNull NamedCssColor TAN = new NamedCssColor(NamedColorName.TAN, 0xD2B48C);
    public static final @NonNull NamedCssColor TEAL = new NamedCssColor(NamedColorName.TEAL, 0x008080);
    public static final @NonNull NamedCssColor THISTLE = new NamedCssColor(NamedColorName.THISTLE, 0xD8BFD8);
    public static final @NonNull NamedCssColor TOMATO = new NamedCssColor(NamedColorName.TOMATO, 0xFF6347);
    public static final @NonNull NamedCssColor TURQUOISE = new NamedCssColor(NamedColorName.TURQUOISE, 0x40E0D0);
    public static final @NonNull NamedCssColor VIOLET = new NamedCssColor(NamedColorName.VIOLET, 0xEE82EE);
    public static final @NonNull NamedCssColor WHEAT = new NamedCssColor(NamedColorName.WHEAT, 0xF5DEB3);
    public static final @NonNull NamedCssColor WHITE = new NamedCssColor(NamedColorName.WHITE, 0xFFFFFF);
    public static final @NonNull NamedCssColor WHITESMOKE = new NamedCssColor(NamedColorName.WHITESMOKE, 0xF5F5F5);
    public static final @NonNull NamedCssColor YELLOW = new NamedCssColor(NamedColorName.YELLOW, 0xFFFF00);
    public static final @NonNull NamedCssColor YELLOWGREEN = new NamedCssColor(NamedColorName.YELLOWGREEN, 0x9ACD32);

    private static final @NonNull ImmutableMap<String, NamedCssColor> NAMED_COLORS;

    static {
        // Workaround for Java SE 8: javac hangs if ImmutableMap.ofEntries() has many entries.
        Map<String, NamedCssColor> m = new LinkedHashMap<>();

        m.put(TRANSPARENT.getName(), TRANSPARENT);
        m.put(ALICEBLUE.getName(), ALICEBLUE);
        m.put(ANTIQUEWHITE.getName(), ANTIQUEWHITE);
        m.put(AQUA.getName(), AQUA);
        m.put(AQUAMARINE.getName(), AQUAMARINE);
        m.put(AZURE.getName(), AZURE);
        m.put(BEIGE.getName(), BEIGE);
        m.put(BISQUE.getName(), BISQUE);
        m.put(BLACK.getName(), BLACK);
        m.put(BLANCHEDALMOND.getName(), BLANCHEDALMOND);
        m.put(BLUE.getName(), BLUE);
        m.put(BLUEVIOLET.getName(), BLUEVIOLET);
        m.put(BROWN.getName(), BROWN);
        m.put(BURLYWOOD.getName(), BURLYWOOD);
        m.put(CADETBLUE.getName(), CADETBLUE);
        m.put(CHARTREUSE.getName(), CHARTREUSE);
        m.put(CHOCOLATE.getName(), CHOCOLATE);
        m.put(CORAL.getName(), CORAL);
        m.put(CORNFLOWERBLUE.getName(), CORNFLOWERBLUE);
        m.put(CORNSILK.getName(), CORNSILK);
        m.put(CRIMSON.getName(), CRIMSON);
        m.put(CYAN.getName(), CYAN);
        m.put(DARKBLUE.getName(), DARKBLUE);
        m.put(DARKCYAN.getName(), DARKCYAN);
        m.put(DARKGOLDENROD.getName(), DARKGOLDENROD);
        m.put(DARKGRAY.getName(), DARKGRAY);
        m.put(DARKGREEN.getName(), DARKGREEN);
        m.put(DARKGREY.getName(), DARKGREY);
        m.put(DARKKHAKI.getName(), DARKKHAKI);
        m.put(DARKMAGENTA.getName(), DARKMAGENTA);
        m.put(DARKOLIVEGREEN.getName(), DARKOLIVEGREEN);
        m.put(DARKORANGE.getName(), DARKORANGE);
        m.put(DARKORCHID.getName(), DARKORCHID);
        m.put(DARKRED.getName(), DARKRED);
        m.put(DARKSALMON.getName(), DARKSALMON);
        m.put(DARKSEAGREEN.getName(), DARKSEAGREEN);
        m.put(DARKSLATEBLUE.getName(), DARKSLATEBLUE);
        m.put(DARKSLATEGRAY.getName(), DARKSLATEGRAY);
        m.put(DARKSLATEGREY.getName(), DARKSLATEGREY);
        m.put(DARKTURQUOISE.getName(), DARKTURQUOISE);
        m.put(DARKVIOLET.getName(), DARKVIOLET);
        m.put(DEEPPINK.getName(), DEEPPINK);
        m.put(DEEPSKYBLUE.getName(), DEEPSKYBLUE);
        m.put(DIMGRAY.getName(), DIMGRAY);
        m.put(DIMGREY.getName(), DIMGREY);
        m.put(DODGERBLUE.getName(), DODGERBLUE);
        m.put(FIREBRICK.getName(), FIREBRICK);
        m.put(FLORALWHITE.getName(), FLORALWHITE);
        m.put(FORESTGREEN.getName(), FORESTGREEN);
        m.put(FUCHSIA.getName(), FUCHSIA);
        m.put(GAINSBORO.getName(), GAINSBORO);
        m.put(GHOSTWHITE.getName(), GHOSTWHITE);
        m.put(GOLD.getName(), GOLD);
        m.put(GOLDENROD.getName(), GOLDENROD);
        m.put(GRAY.getName(), GRAY);
        m.put(GREEN.getName(), GREEN);
        m.put(GREENYELLOW.getName(), GREENYELLOW);
        m.put(GREY.getName(), GREY);
        m.put(HONEYDEW.getName(), HONEYDEW);
        m.put(HOTPINK.getName(), HOTPINK);
        m.put(INDIANRED.getName(), INDIANRED);
        m.put(INDIGO.getName(), INDIGO);
        m.put(IVORY.getName(), IVORY);
        m.put(KHAKI.getName(), KHAKI);
        m.put(LAVENDER.getName(), LAVENDER);
        m.put(LAVENDERBLUSH.getName(), LAVENDERBLUSH);
        m.put(LAWNGREEN.getName(), LAWNGREEN);
        m.put(LEMONCHIFFON.getName(), LEMONCHIFFON);
        m.put(LIGHTBLUE.getName(), LIGHTBLUE);
        m.put(LIGHTCORAL.getName(), LIGHTCORAL);
        m.put(LIGHTCYAN.getName(), LIGHTCYAN);
        m.put(LIGHTGOLDENRODYELLOW.getName(), LIGHTGOLDENRODYELLOW);
        m.put(LIGHTGRAY.getName(), LIGHTGRAY);
        m.put(LIGHTGREEN.getName(), LIGHTGREEN);
        m.put(LIGHTGREY.getName(), LIGHTGREY);
        m.put(LIGHTPINK.getName(), LIGHTPINK);
        m.put(LIGHTSALMON.getName(), LIGHTSALMON);
        m.put(LIGHTSEAGREEN.getName(), LIGHTSEAGREEN);
        m.put(LIGHTSKYBLUE.getName(), LIGHTSKYBLUE);
        m.put(LIGHTSLATEGRAY.getName(), LIGHTSLATEGRAY);
        m.put(LIGHTSLATEGREY.getName(), LIGHTSLATEGREY);
        m.put(LIGHTSTEELBLUE.getName(), LIGHTSTEELBLUE);
        m.put(LIGHTYELLOW.getName(), LIGHTYELLOW);
        m.put(LIME.getName(), LIME);
        m.put(LIMEGREEN.getName(), LIMEGREEN);
        m.put(LINEN.getName(), LINEN);
        m.put(MAGENTA.getName(), MAGENTA);
        m.put(MAROON.getName(), MAROON);
        m.put(MEDIUMAQUAMARINE.getName(), MEDIUMAQUAMARINE);
        m.put(MEDIUMBLUE.getName(), MEDIUMBLUE);
        m.put(MEDIUMORCHID.getName(), MEDIUMORCHID);
        m.put(MEDIUMPURPLE.getName(), MEDIUMPURPLE);
        m.put(MEDIUMSEAGREEN.getName(), MEDIUMSEAGREEN);
        m.put(MEDIUMSLATEBLUE.getName(), MEDIUMSLATEBLUE);
        m.put(MEDIUMSPRINGGREEN.getName(), MEDIUMSPRINGGREEN);
        m.put(MEDIUMTURQUOISE.getName(), MEDIUMTURQUOISE);
        m.put(MEDIUMVIOLETRED.getName(), MEDIUMVIOLETRED);
        m.put(MIDNIGHTBLUE.getName(), MIDNIGHTBLUE);
        m.put(MINTCREAM.getName(), MINTCREAM);
        m.put(MISTYROSE.getName(), MISTYROSE);
        m.put(MOCCASIN.getName(), MOCCASIN);
        m.put(NAVAJOWHITE.getName(), NAVAJOWHITE);
        m.put(NAVY.getName(), NAVY);
        m.put(OLDLACE.getName(), OLDLACE);
        m.put(OLIVE.getName(), OLIVE);
        m.put(OLIVEDRAB.getName(), OLIVEDRAB);
        m.put(ORANGE.getName(), ORANGE);
        m.put(ORANGERED.getName(), ORANGERED);
        m.put(ORCHID.getName(), ORCHID);
        m.put(PALEGOLDENROD.getName(), PALEGOLDENROD);
        m.put(PALEGREEN.getName(), PALEGREEN);
        m.put(PALETURQUOISE.getName(), PALETURQUOISE);
        m.put(PALEVIOLETRED.getName(), PALEVIOLETRED);
        m.put(PAPAYAWHIP.getName(), PAPAYAWHIP);
        m.put(PEACHPUFF.getName(), PEACHPUFF);
        m.put(PERU.getName(), PERU);
        m.put(PINK.getName(), PINK);
        m.put(PLUM.getName(), PLUM);
        m.put(POWDERBLUE.getName(), POWDERBLUE);
        m.put(PURPLE.getName(), PURPLE);
        m.put(REBECCAPURPLE.getName(), REBECCAPURPLE);
        m.put(RED.getName(), RED);
        m.put(ROSYBROWN.getName(), ROSYBROWN);
        m.put(ROYALBLUE.getName(), ROYALBLUE);
        m.put(SADDLEBROWN.getName(), SADDLEBROWN);
        m.put(SALMON.getName(), SALMON);
        m.put(SANDYBROWN.getName(), SANDYBROWN);
        m.put(SEAGREEN.getName(), SEAGREEN);
        m.put(SEASHELL.getName(), SEASHELL);
        m.put(SIENNA.getName(), SIENNA);
        m.put(SILVER.getName(), SILVER);
        m.put(SKYBLUE.getName(), SKYBLUE);
        m.put(SLATEBLUE.getName(), SLATEBLUE);
        m.put(SLATEGRAY.getName(), SLATEGRAY);
        m.put(SLATEGREY.getName(), SLATEGREY);
        m.put(SNOW.getName(), SNOW);
        m.put(SPRINGGREEN.getName(), SPRINGGREEN);
        m.put(STEELBLUE.getName(), STEELBLUE);
        m.put(TAN.getName(), TAN);
        m.put(TEAL.getName(), TEAL);
        m.put(THISTLE.getName(), THISTLE);
        m.put(TOMATO.getName(), TOMATO);
        m.put(TURQUOISE.getName(), TURQUOISE);
        m.put(VIOLET.getName(), VIOLET);
        m.put(WHEAT.getName(), WHEAT);
        m.put(WHITE.getName(), WHITE);
        m.put(WHITESMOKE.getName(), WHITESMOKE);
        m.put(YELLOW.getName(), YELLOW);
        m.put(YELLOWGREEN.getName(), YELLOWGREEN);
        NAMED_COLORS = ImmutableMaps.ofMap(m);
    }

    /**
     * Returns a named color for the given name.
     * <p>
     * The name is not case sensitive.
     * <p>
     * If the name is unknown, then an illegal argument exception is thrown.
     *
     * @param name the name of the color
     * @return a named color
     * @throws IllegalArgumentException if the name is unknown
     */
    public static @NonNull NamedCssColor ofNonNull(@NonNull String name) {
        NamedCssColor color = NAMED_COLORS.get(name.toLowerCase());
        if (color == null) {
            throw new IllegalArgumentException("unsupported color name: " + name);
        }
        return color;
    }

    /**
     * Returns a named color for the given name.
     * <p>
     * The name is not case sensitive.
     * <p>
     * If the name is unknown, then null is returned.
     *
     * @param name the name of the color
     * @return a named color or null
     */
    public static @Nullable NamedCssColor of(@NonNull String name) {
        return NAMED_COLORS.get(name.toLowerCase());
    }

    /**
     * Returns true if the given name is a known system color.
     * <p>
     * The name is not case sensitive.
     *
     * @param name a name
     * @return true if known
     */
    public static boolean isNamedColor(@NonNull String name) {
        return NAMED_COLORS.containsKey(name.toLowerCase());
    }

}

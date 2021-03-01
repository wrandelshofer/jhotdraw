/*
 * @(#)NamedCssColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.ImmutableMap;
import org.jhotdraw8.collection.ImmutableMaps;

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
        super(name, Color.rgb((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff));
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

    private static final @NonNull ImmutableMap<String, NamedCssColor> NAMED_COLORS = ImmutableMaps.ofEntries(
            Map.entry(TRANSPARENT.getName(), TRANSPARENT),
            Map.entry(ALICEBLUE.getName(), ALICEBLUE),
            Map.entry(ANTIQUEWHITE.getName(), ANTIQUEWHITE),
            Map.entry(AQUA.getName(), AQUA),
            Map.entry(AQUAMARINE.getName(), AQUAMARINE),
            Map.entry(AZURE.getName(), AZURE),
            Map.entry(BEIGE.getName(), BEIGE),
            Map.entry(BISQUE.getName(), BISQUE),
            Map.entry(BLACK.getName(), BLACK),
            Map.entry(BLANCHEDALMOND.getName(), BLANCHEDALMOND),
            Map.entry(BLUE.getName(), BLUE),
            Map.entry(BLUEVIOLET.getName(), BLUEVIOLET),
            Map.entry(BROWN.getName(), BROWN),
            Map.entry(BURLYWOOD.getName(), BURLYWOOD),
            Map.entry(CADETBLUE.getName(), CADETBLUE),
            Map.entry(CHARTREUSE.getName(), CHARTREUSE),
            Map.entry(CHOCOLATE.getName(), CHOCOLATE),
            Map.entry(CORAL.getName(), CORAL),
            Map.entry(CORNFLOWERBLUE.getName(), CORNFLOWERBLUE),
            Map.entry(CORNSILK.getName(), CORNSILK),
            Map.entry(CRIMSON.getName(), CRIMSON),
            Map.entry(CYAN.getName(), CYAN),
            Map.entry(DARKBLUE.getName(), DARKBLUE),
            Map.entry(DARKCYAN.getName(), DARKCYAN),
            Map.entry(DARKGOLDENROD.getName(), DARKGOLDENROD),
            Map.entry(DARKGRAY.getName(), DARKGRAY),
            Map.entry(DARKGREEN.getName(), DARKGREEN),
            Map.entry(DARKGREY.getName(), DARKGREY),
            Map.entry(DARKKHAKI.getName(), DARKKHAKI),
            Map.entry(DARKMAGENTA.getName(), DARKMAGENTA),
            Map.entry(DARKOLIVEGREEN.getName(), DARKOLIVEGREEN),
            Map.entry(DARKORANGE.getName(), DARKORANGE),
            Map.entry(DARKORCHID.getName(), DARKORCHID),
            Map.entry(DARKRED.getName(), DARKRED),
            Map.entry(DARKSALMON.getName(), DARKSALMON),
            Map.entry(DARKSEAGREEN.getName(), DARKSEAGREEN),
            Map.entry(DARKSLATEBLUE.getName(), DARKSLATEBLUE),
            Map.entry(DARKSLATEGRAY.getName(), DARKSLATEGRAY),
            Map.entry(DARKSLATEGREY.getName(), DARKSLATEGREY),
            Map.entry(DARKTURQUOISE.getName(), DARKTURQUOISE),
            Map.entry(DARKVIOLET.getName(), DARKVIOLET),
            Map.entry(DEEPPINK.getName(), DEEPPINK),
            Map.entry(DEEPSKYBLUE.getName(), DEEPSKYBLUE),
            Map.entry(DIMGRAY.getName(), DIMGRAY),
            Map.entry(DIMGREY.getName(), DIMGREY),
            Map.entry(DODGERBLUE.getName(), DODGERBLUE),
            Map.entry(FIREBRICK.getName(), FIREBRICK),
            Map.entry(FLORALWHITE.getName(), FLORALWHITE),
            Map.entry(FORESTGREEN.getName(), FORESTGREEN),
            Map.entry(FUCHSIA.getName(), FUCHSIA),
            Map.entry(GAINSBORO.getName(), GAINSBORO),
            Map.entry(GHOSTWHITE.getName(), GHOSTWHITE),
            Map.entry(GOLD.getName(), GOLD),
            Map.entry(GOLDENROD.getName(), GOLDENROD),
            Map.entry(GRAY.getName(), GRAY),
            Map.entry(GREEN.getName(), GREEN),
            Map.entry(GREENYELLOW.getName(), GREENYELLOW),
            Map.entry(GREY.getName(), GREY),
            Map.entry(HONEYDEW.getName(), HONEYDEW),
            Map.entry(HOTPINK.getName(), HOTPINK),
            Map.entry(INDIANRED.getName(), INDIANRED),
            Map.entry(INDIGO.getName(), INDIGO),
            Map.entry(IVORY.getName(), IVORY),
            Map.entry(KHAKI.getName(), KHAKI),
            Map.entry(LAVENDER.getName(), LAVENDER),
            Map.entry(LAVENDERBLUSH.getName(), LAVENDERBLUSH),
            Map.entry(LAWNGREEN.getName(), LAWNGREEN),
            Map.entry(LEMONCHIFFON.getName(), LEMONCHIFFON),
            Map.entry(LIGHTBLUE.getName(), LIGHTBLUE),
            Map.entry(LIGHTCORAL.getName(), LIGHTCORAL),
            Map.entry(LIGHTCYAN.getName(), LIGHTCYAN),
            Map.entry(LIGHTGOLDENRODYELLOW.getName(), LIGHTGOLDENRODYELLOW),
            Map.entry(LIGHTGRAY.getName(), LIGHTGRAY),
            Map.entry(LIGHTGREEN.getName(), LIGHTGREEN),
            Map.entry(LIGHTGREY.getName(), LIGHTGREY),
            Map.entry(LIGHTPINK.getName(), LIGHTPINK),
            Map.entry(LIGHTSALMON.getName(), LIGHTSALMON),
            Map.entry(LIGHTSEAGREEN.getName(), LIGHTSEAGREEN),
            Map.entry(LIGHTSKYBLUE.getName(), LIGHTSKYBLUE),
            Map.entry(LIGHTSLATEGRAY.getName(), LIGHTSLATEGRAY),
            Map.entry(LIGHTSLATEGREY.getName(), LIGHTSLATEGREY),
            Map.entry(LIGHTSTEELBLUE.getName(), LIGHTSTEELBLUE),
            Map.entry(LIGHTYELLOW.getName(), LIGHTYELLOW),
            Map.entry(LIME.getName(), LIME),
            Map.entry(LIMEGREEN.getName(), LIMEGREEN),
            Map.entry(LINEN.getName(), LINEN),
            Map.entry(MAGENTA.getName(), MAGENTA),
            Map.entry(MAROON.getName(), MAROON),
            Map.entry(MEDIUMAQUAMARINE.getName(), MEDIUMAQUAMARINE),
            Map.entry(MEDIUMBLUE.getName(), MEDIUMBLUE),
            Map.entry(MEDIUMORCHID.getName(), MEDIUMORCHID),
            Map.entry(MEDIUMPURPLE.getName(), MEDIUMPURPLE),
            Map.entry(MEDIUMSEAGREEN.getName(), MEDIUMSEAGREEN),
            Map.entry(MEDIUMSLATEBLUE.getName(), MEDIUMSLATEBLUE),
            Map.entry(MEDIUMSPRINGGREEN.getName(), MEDIUMSPRINGGREEN),
            Map.entry(MEDIUMTURQUOISE.getName(), MEDIUMTURQUOISE),
            Map.entry(MEDIUMVIOLETRED.getName(), MEDIUMVIOLETRED),
            Map.entry(MIDNIGHTBLUE.getName(), MIDNIGHTBLUE),
            Map.entry(MINTCREAM.getName(), MINTCREAM),
            Map.entry(MISTYROSE.getName(), MISTYROSE),
            Map.entry(MOCCASIN.getName(), MOCCASIN),
            Map.entry(NAVAJOWHITE.getName(), NAVAJOWHITE),
            Map.entry(NAVY.getName(), NAVY),
            Map.entry(OLDLACE.getName(), OLDLACE),
            Map.entry(OLIVE.getName(), OLIVE),
            Map.entry(OLIVEDRAB.getName(), OLIVEDRAB),
            Map.entry(ORANGE.getName(), ORANGE),
            Map.entry(ORANGERED.getName(), ORANGERED),
            Map.entry(ORCHID.getName(), ORCHID),
            Map.entry(PALEGOLDENROD.getName(), PALEGOLDENROD),
            Map.entry(PALEGREEN.getName(), PALEGREEN),
            Map.entry(PALETURQUOISE.getName(), PALETURQUOISE),
            Map.entry(PALEVIOLETRED.getName(), PALEVIOLETRED),
            Map.entry(PAPAYAWHIP.getName(), PAPAYAWHIP),
            Map.entry(PEACHPUFF.getName(), PEACHPUFF),
            Map.entry(PERU.getName(), PERU),
            Map.entry(PINK.getName(), PINK),
            Map.entry(PLUM.getName(), PLUM),
            Map.entry(POWDERBLUE.getName(), POWDERBLUE),
            Map.entry(PURPLE.getName(), PURPLE),
            Map.entry(REBECCAPURPLE.getName(), REBECCAPURPLE),
            Map.entry(RED.getName(), RED),
            Map.entry(ROSYBROWN.getName(), ROSYBROWN),
            Map.entry(ROYALBLUE.getName(), ROYALBLUE),
            Map.entry(SADDLEBROWN.getName(), SADDLEBROWN),
            Map.entry(SALMON.getName(), SALMON),
            Map.entry(SANDYBROWN.getName(), SANDYBROWN),
            Map.entry(SEAGREEN.getName(), SEAGREEN),
            Map.entry(SEASHELL.getName(), SEASHELL),
            Map.entry(SIENNA.getName(), SIENNA),
            Map.entry(SILVER.getName(), SILVER),
            Map.entry(SKYBLUE.getName(), SKYBLUE),
            Map.entry(SLATEBLUE.getName(), SLATEBLUE),
            Map.entry(SLATEGRAY.getName(), SLATEGRAY),
            Map.entry(SLATEGREY.getName(), SLATEGREY),
            Map.entry(SNOW.getName(), SNOW),
            Map.entry(SPRINGGREEN.getName(), SPRINGGREEN),
            Map.entry(STEELBLUE.getName(), STEELBLUE),
            Map.entry(TAN.getName(), TAN),
            Map.entry(TEAL.getName(), TEAL),
            Map.entry(THISTLE.getName(), THISTLE),
            Map.entry(TOMATO.getName(), TOMATO),
            Map.entry(TURQUOISE.getName(), TURQUOISE),
            Map.entry(VIOLET.getName(), VIOLET),
            Map.entry(WHEAT.getName(), WHEAT),
            Map.entry(WHITE.getName(), WHITE),
            Map.entry(WHITESMOKE.getName(), WHITESMOKE),
            Map.entry(YELLOW.getName(), YELLOW),
            Map.entry(YELLOWGREEN.getName(), YELLOWGREEN)
    );

    /**
     * Creates a named color for the given name.
     * <p>
     * If the name is unknown, then a black named color with the given
     * name is created.
     *
     * @param name the name of the system color
     * @return a new instance
     */
    public static @NonNull NamedCssColor of(@NonNull String name) {
        NamedCssColor color = NAMED_COLORS.get(name.toLowerCase());
        return color == null ? new NamedCssColor(name, Color.BLACK) : color;
    }

    /**
     * Returns true if the given name is a known system color.
     *
     * @param name a name
     * @return true if known
     */
    public static boolean isNamedColor(@NonNull String name) {
        return NAMED_COLORS.containsKey(name.toLowerCase());
    }

}

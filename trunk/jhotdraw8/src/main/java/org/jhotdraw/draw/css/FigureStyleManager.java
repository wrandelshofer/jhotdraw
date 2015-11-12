/* @(#)FigureStyleManager.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.css;

import java.util.WeakHashMap;
import org.jhotdraw.css.AbstractStyleManager;
import org.jhotdraw.css.ast.Declaration;
import org.jhotdraw.draw.Figure;

/**
 * FigureStyleManager.
 * @author Werner Randelshofer
 */
public class FigureStyleManager  extends AbstractStyleManager<Figure> {
    private final WeakHashMap<Declaration, Object> convertedValues = new WeakHashMap<>();

    @Override
    public void applyStylesTo(Figure e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

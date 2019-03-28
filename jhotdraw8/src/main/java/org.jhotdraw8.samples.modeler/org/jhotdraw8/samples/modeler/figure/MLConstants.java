/* @(#)MLConstants.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import org.jhotdraw8.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.StringStyleableFigureKey;

public class MLConstants {
    public static final String ML_NAMESPACE_PREFIX = "ml";
    public final static StringStyleableFigureKey KEYWORD = new StringStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "keyword", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);
    public final static StringStyleableFigureKey PACKAGE = new StringStyleableFigureKey(MLConstants.ML_NAMESPACE_PREFIX, "package", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);
    public final static BooleanStyleableFigureKey KEYWORD_LABEL_VISIBLE = new BooleanStyleableFigureKey("keywordLabelVisible", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false);

}
/* @(#)MLConstants.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import org.jhotdraw8.draw.key.BooleanStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.StringStyleableKey;

public class MLConstants {
    public static final String ML_NAMESPACE_PREFIX = "ml";
    public final static StringStyleableKey KEYWORD = new StringStyleableKey(MLConstants.ML_NAMESPACE_PREFIX, "keyword", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);
    public final static StringStyleableKey PACKAGE = new StringStyleableKey(MLConstants.ML_NAMESPACE_PREFIX, "package", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), true, null, null);
    public final static BooleanStyleableKey KEYWORD_LABEL_VISIBLE = new BooleanStyleableKey("keywordLabelVisible", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), false);

}
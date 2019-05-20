/* @(#)MLConstants.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.modeler.figure;

import org.jhotdraw8.draw.key.BooleanStyleableKey;
import org.jhotdraw8.draw.key.NullableStringStyleableKey;

public class MLConstants {
    public static final String ML_NAMESPACE_PREFIX = "ml";
    public final static NullableStringStyleableKey KEYWORD = new NullableStringStyleableKey(MLConstants.ML_NAMESPACE_PREFIX, "keyword");
    public final static NullableStringStyleableKey PACKAGE = new NullableStringStyleableKey(MLConstants.ML_NAMESPACE_PREFIX, "package");
    public final static BooleanStyleableKey KEYWORD_LABEL_VISIBLE = new BooleanStyleableKey("keywordLabelVisible", false);

}
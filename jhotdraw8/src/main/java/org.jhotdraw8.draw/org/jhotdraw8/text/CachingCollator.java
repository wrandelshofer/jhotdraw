/* @(#)CachingCollator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.Nullable;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * CachingCollator.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CachingCollator implements Comparator<String> {

    private final Collator collator;
    private final Map<String, CollationKey> keyMap = new HashMap<>();

    public CachingCollator(Collator collator) {
        this.collator = collator;
    }

    @Override
    public int compare(@Nullable String o1, @Nullable String o2) {
        CollationKey k1 = keyMap.computeIfAbsent(o1 == null ? "" : o1, collator::getCollationKey);
        CollationKey k2 = keyMap.computeIfAbsent(o2 == null ? "" : o2, collator::getCollationKey);
        return k1.compareTo(k2);
    }

    public void clearCache() {
        keyMap.clear();
    }
}

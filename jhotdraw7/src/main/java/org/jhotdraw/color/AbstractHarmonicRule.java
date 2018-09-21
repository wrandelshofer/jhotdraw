/* @(#)AbstractHarmonicRule.java
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */

package org.jhotdraw.color;

import javax.annotation.Nonnull;

/**
 * AbstractHarmonicRule.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractHarmonicRule implements HarmonicRule {
    protected int baseIndex;
    protected int[] derivedIndices;
    
    
    @Override
    public void setBaseIndex() {
       // this.baseIndex = baseIndex;
    }

    @Override
    public int getBaseIndex() {
        return baseIndex;
    }

    @Override
    public void setDerivedIndices(int... indices) {
        this.derivedIndices = indices;
    }

    @Override
    public int[] getDerivedIndices() {
        return derivedIndices;
    }

}

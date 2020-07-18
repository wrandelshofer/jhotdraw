/* @(#)AbstractHarmonicRule.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.color;

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

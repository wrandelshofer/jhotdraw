/* @(#)HarmonicRule.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.color;

import java.awt.Color;

/**
 * HarmonicRule.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface HarmonicRule {
    
    public void setBaseIndex();
    
    public int getBaseIndex();
    
    public void setDerivedIndices(int... indices);
    
    public int[] getDerivedIndices();
    
    public void apply(HarmonicColorModel model);
    
    public void colorChanged(HarmonicColorModel model, int index, Color oldValue, Color newValue);
}

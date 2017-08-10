/* @(#)AbstractNamedColorSpace.java
 * 
 * Copyright © 1996-2017 The authors and contributors of JHotDraw.
 * MIT License, CC-by License, or LGPL License.
 */
package org.jhotdraw.color;

import java.awt.color.ColorSpace;

/**
 * {@code AbstractNamedColorSpace}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractNamedColorSpace extends ColorSpace implements NamedColorSpace {
    private static final long serialVersionUID = 1L;

    public AbstractNamedColorSpace(int type, int numcomponents) {
        super(type, numcomponents);
    }

    @Override
    public float[] fromCIEXYZ(float[] colorvalue) {
        return fromCIEXYZ(colorvalue, new float[getNumComponents()]);
    }

    @Override
    public final float[] toRGB(float[] colorvalue) {
        return toRGB(colorvalue, new float[3]);
    }

    @Override
    public float[] fromRGB(float[] rgb) {
        float[] tmp = new float[getNumComponents()];
        return fromRGB(rgb, new float[getNumComponents()]);
    }

    @Override
    public final float[] toCIEXYZ(float[] colorvalue) {
        return toCIEXYZ(colorvalue, new float[3]);
    }
    
    @Override
    public float[] toCIEXYZ(float[] colorvalue, float[] xyz) {
       return ColorUtil.RGBtoCIEXYZ(toRGB(colorvalue,xyz),xyz);
    }

    @Override
    public float[] fromCIEXYZ(float[] xyz, float[] colorvalue) {
       return fromRGB(ColorUtil.CIEXYZtoRGB(xyz,colorvalue),colorvalue);
    }

}

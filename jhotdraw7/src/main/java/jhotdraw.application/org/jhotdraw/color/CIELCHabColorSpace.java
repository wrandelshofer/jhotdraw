/* @(#)CIELCHabColorSpace.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.color;

import static java.lang.Math.PI;

/**
 * The 1976 CIE L*CHa*b* color space (CIELCH).
 * <p>
 * The L* coordinate of an object is the lightness intensity as measured on a
 * scale from 0 to 100, where 0 represents black and 100 represents white.
 * <p>
 * The C and H coordinates are projections of the a* and b* colors of the CIE
 * L*a*b* color space into polar coordinates.
 * <pre>
 * a = C * cos(H)
 * b = C * sin(H)
 * </pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CIELCHabColorSpace extends CIELABColorSpace {

    private static final long serialVersionUID = 1L;

    public CIELCHabColorSpace() {
    }

    /**
     * LCH to XYZ.
     *
     * @param colorvalue LCH color value.
     * @return CIEXYZ color value.
     */
    @Override
    public float[] toCIEXYZ(float[] colorvalue, float[] xyz) {
        double L = colorvalue[0];
        double C = colorvalue[1];
        double H = colorvalue[2] / 180 * PI;

        double a = C * Math.cos(H);
        double b = C * Math.sin(H);

        return toCIEXYZ(L, a, b, xyz);
    }

    /**
     * XYZ to LCH.
     *
     * @param colorvalue CIEXYZ color value.
     * @return LCH color value.
     */
    @Override
    public float[] fromCIEXYZ(float[] xyz, float[] colorvalue) {
        colorvalue = super.fromCIEXYZ(xyz, colorvalue);
        double L = colorvalue[0];
        double a = colorvalue[1];
        double b = colorvalue[2];

        double C = Math.sqrt(a * a + b * b);
        double H = Math.atan2(b, a);

        colorvalue[0] = (float) L;
        colorvalue[1] = (float) C;
        colorvalue[2] = (float) (H * 180 / PI);
        return colorvalue;
    }

    @Override
    public String getName() {
        return "CIE 1976 L*CHa*b*";
    }

    @Override
    public float getMinValue(int component) {
        switch (component) {
            case 0:
                return 0f;
            case 1:
                return 0f;
            case 2:
                return 0f;
        }
        throw new IllegalArgumentException("Illegal component:" + component);
    }

    @Override
    public float getMaxValue(int component) {
        switch (component) {
            case 0:
                return 100f;
            case 1:
                return 127f;
            case 2:
                return 360f;
        }
        throw new IllegalArgumentException("Illegal component:" + component);
    }

    @Override
    public String getName(int component) {
        switch (component) {
            case 0:
                return "L*";
            case 1:
                return "c*";
            case 2:
                return "h*";
        }
        throw new IllegalArgumentException("Illegal component:" + component);
    }
}

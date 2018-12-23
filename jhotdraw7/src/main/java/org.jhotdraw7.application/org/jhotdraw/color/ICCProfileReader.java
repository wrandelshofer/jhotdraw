/* @(#)ICCProfileReader.java
 * Copyright © by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.color;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * ICCProfileReader decodes ICC profile values.
 * <p>
 * References:
 * <p>
 * <a href="http://www.color.org/icc32.pdf">ICC Profile Format Specification.
 * Version 3.2 November 20, 1995. International Color Consortium.</a>.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class ICCProfileReader {

    private DataInputStream in;

    public ICCProfileReader(DataInputStream in) {
        this.in = in;
    }

    public ICCProfileReader(byte[] data) {
        this.in = new DataInputStream(new ByteArrayInputStream(data));
    }

    /**
     * Reads an XYZType.
     * <p>
     * The XYZType contains an array of three encoded values for the XYZ
     * tristimulus values. The number of sets of values is determined from the
     * size of the tag. The byte stream is given below. Tristimulus values must
     * be non-negative, the signed encoding allows for implementation
     * optimizations by minimizing the number of fixed formats.
     * <table>
     * <tr><th>Byte Offset</th><th>Content</th><th>Encoding</th></tr>
     * <tr><td>0-3</td><td>‘XYZ ‘(58595A20h) type descriptor</td><td></td></tr>
     * <tr><td>4-7</td><td>reserved, must be set to 0</td><td></td></tr>
     * <tr><td>8-n</td><td>an array of XYZ numbers</td><td>XYZNumber</td></tr>
     * </table>
     */
    public double[] readXYZType() throws IOException {
        int typeDescriptor = in.readInt();
        if (typeDescriptor != 0x58595A20) {
            throw new IOException("illegal type descriptor: 0x" + Integer.toHexString(typeDescriptor));
        }
        int reserved = in.readInt();
        if (reserved != 0x0) {
            throw new IOException("illegal reserved: 0x" + Integer.toHexString(reserved));
        }
        return readXYZNumber();
    }

    /**
     * Reads an XYZNumber.
     * <p>
     * This type represents a set of three fixed signed 4 byte/32 bit quantities
     * used to encode CIEXYZ tristimulus values where byte usage is assigned as
     * follows:
     * <table>
     * <tr><th>Byte Offset</th><th>Content</th><th>Encoding</th></tr>
     * <tr><td>0-3</td><td>CIE X</td><td></td>s15Fixed16Number</td></tr>
     * <tr><td>4-7</td><td>CIE Y</td><td></td>s15Fixed16Number</td></tr>
     * <tr><td>8-11</td><td>CIE Z</td><td>s15Fixed16Number</td></tr>
     * </table>
     */
    public double[] readXYZNumber() throws IOException {
        double[] xyz = new double[3];
        xyz[0] = readS15Fixed16Number();
        xyz[1] = readS15Fixed16Number();
        xyz[2] = readS15Fixed16Number();
        return xyz;
    }

    /**
     * Reads an s15Fixed16Number.
     * <p>
     * This type represents a fixed signed 4 byte/32 bit quantity which has 16
     * fractional bits. An example of this encoding is:
     * <table>
     * <tr><td>-32768.0</td><td>80000000h</td></tr>
     * <tr><td>0</td><td>0000000h</td></tr>
     * <tr><td>1.0</td><td>00010000h</td></tr>
     * <tr><td>32767 + (65535/65536)</td><td>7FFFFFFFh</td></tr>
     * </table>
     */
    public double readS15Fixed16Number() throws IOException {
        int s15 = in.readShort();
        int p15 = in.readUnsignedShort();
        return (double) s15 + (double) p15 / 65536.0;
    }
}

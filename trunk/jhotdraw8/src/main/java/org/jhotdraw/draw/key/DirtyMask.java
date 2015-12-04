/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw.draw.key;

/**
 * Represents a bitmask of {@code DirtyBits}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DirtyMask {

    /** The bit set is coalesced into a bitmask. */
    private final int bitmask;

    public final static DirtyMask EMPTY = new DirtyMask(0);
    public final static DirtyMask ALL = new DirtyMask(~0);

    /** Prevent instantiation. */
    private DirtyMask(int bitmask) {
        this.bitmask = bitmask;
    }

    public static DirtyMask of(DirtyBits... bits) {
        int mask = 0;
        for (DirtyBits bit : bits) {
            mask |= bit.getMask();
        }
        return new DirtyMask(mask);
    }

    /** API for DirtyBits. */
    final int getMask() {
        return bitmask;
    }

    public boolean containsOneOf(DirtyBits... bits) {
        for (DirtyBits bit : bits) {
            if ((bitmask & bit.getMask()) == bit.getMask()) {
                return true;
            }
        }
        return false;
    }

    /** Adds all bits of the specified dirty mask to this mask.
     * 
     * @param that that mask
     * @return a new mask
     */
    public DirtyMask add(DirtyMask that) {
        return new DirtyMask(this.bitmask|that.bitmask);
    }
}

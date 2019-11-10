/*
 * @(#)DirtyMask.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;

/**
 * Represents a bitmask of {@code DirtyBits}.
 *
 * @author Werner Randelshofer
 */
public class DirtyMask {

    /**
     * The bit set is coalesced into a bitmask.
     */
    private final int bitmask;

    public final static DirtyMask EMPTY = new DirtyMask(0);
    public final static DirtyMask ALL = new DirtyMask(~0);

    /**
     * Prevent instantiation.
     */
    private DirtyMask(int bitmask) {
        this.bitmask = bitmask;
    }

    @NonNull
    public static DirtyMask of(@NonNull DirtyBits... bits) {
        int mask = 0;
        for (DirtyBits bit : bits) {
            mask |= bit.getMask();
        }
        return new DirtyMask(mask);
    }

    /**
     * API for DirtyBits.
     */
    final int getMask() {
        return bitmask;
    }

    public boolean containsOneOf(@NonNull DirtyBits... bits) {
        for (DirtyBits bit : bits) {
            if ((bitmask & bit.getMask()) == bit.getMask()) {
                return true;
            }
        }
        return false;
    }

    public boolean intersects(DirtyBits... bits) {
        return intersects(of(bits));
    }

    public boolean intersects(@NonNull DirtyMask that) {
        return (this.bitmask & that.bitmask) != 0;
    }

    public boolean isEmpty() {
        return bitmask == 0;
    }

    /**
     * Adds all bits of the specified dirty mask to this mask.
     *
     * @param that that mask
     * @return a new mask
     */
    @NonNull
    public DirtyMask add(@NonNull DirtyMask that) {
        return new DirtyMask(this.bitmask | that.bitmask);
    }

    @NonNull
    public DirtyMask add(@NonNull DirtyBits bits) {
        return new DirtyMask(this.bitmask | bits.getMask());
    }

    @NonNull
    @Override
    public String toString() {
        return "DirtyMask{" + "bitmask=" + Integer.toBinaryString(bitmask) + '}';
    }

}

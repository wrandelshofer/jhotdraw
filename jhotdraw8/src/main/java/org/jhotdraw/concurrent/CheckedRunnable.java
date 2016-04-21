/* @(#)CheckedRunnable.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.concurrent;

/**
 * A Runnable which can throw a checked exception.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface CheckedRunnable {
    void run() throws Exception;
}

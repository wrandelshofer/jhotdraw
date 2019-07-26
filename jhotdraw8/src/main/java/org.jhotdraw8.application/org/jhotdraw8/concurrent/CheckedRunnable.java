/*
 * @(#)CheckedRunnable.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.concurrent;

/**
 * A Runnable which can throw a checked exception.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CheckedRunnable {

    void run() throws Exception;
}

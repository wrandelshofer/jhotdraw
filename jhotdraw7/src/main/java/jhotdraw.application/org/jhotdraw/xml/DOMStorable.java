/* @(#)DOMStorable.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.xml;

import java.io.IOException;
/**
 * Interface for objects that can be made persistent using 
 * <code>DOMOutput.writeObject</code> and <code>DOMInput.readObject</code>.
 * <p>
 * By convention every object implementing the DOMStorable interface MUST
 * provide a public parameterless constructor.
 * <p>
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * {@code DOMFactory} is used by {@code DOMInput} and {@code DOMOutput} for
 * reading and writing objects.
 * Client: {@link DOMInput}, {@link DOMOutput}.<br>
 * Strategy: {@link DOMFactory}.<br>
 *
 * <p><em>Chain of Responsibility</em><br>
 * {@code DOMFactory} can delegate reading and writing to objects which implement
 * the {@code DOMStorable} interface.
 * Client: {@link DOMFactory}.<br>
 * Strategy: {@link DOMStorable}.<br>
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public interface DOMStorable {
    public void write(DOMOutput out) throws IOException;
    public void read(DOMInput in) throws IOException;
}

/* @(#)SimpleFigureIdFactory.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.io;

import javafx.css.Styleable;
import org.jhotdraw8.io.SimpleIdFactory;

/**
 * SimpleFigureIdFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleFigureIdFactory extends SimpleIdFactory {
    @Override
    public String createId(Object object) {
        String id = getId(object);

        if (id == null) {
            if (object instanceof Styleable) {
                Styleable f = (Styleable) object;
                id = f.getId();
                if (id != null && getObject(id) == null) {
                    putId( id, object);
                } else {
                    id = super.createId(object, f.getTypeSelector().toLowerCase());
                }
            } else {
                id = super.createId(object);
            }
        }
        return id;
    }
    
        public String putId(Object object) {
        String id = getId(object);

        if (id == null) {
            if (object instanceof Styleable) {
                Styleable f = (Styleable) object;
                id = f.getId();
                if (id != null) {
                    putId(id, object);
                } else {
                    id = super.createId(object, f.getTypeSelector());
                }
            } else {
                id = super.createId(object);
            }
        }
        return id;
    }
}

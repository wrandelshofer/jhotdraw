/*
 * @(#)CustomSkin.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import org.jhotdraw8.annotation.Nonnull;

/**
 * A custom skin without behavior.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CustomSkin<C extends Control> extends SkinBase<C> {

    public CustomSkin(@Nonnull C control) {
        super(control);
    }

}

/*
 * @(#)CustomSkin.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import org.jhotdraw8.annotation.NonNull;

/**
 * A custom skin without behavior.
 *
 * @author Werner Randelshofer
 */
public class CustomSkin<C extends Control> extends SkinBase<C> {

    public CustomSkin(@NonNull C control) {
        super(control);
    }

}

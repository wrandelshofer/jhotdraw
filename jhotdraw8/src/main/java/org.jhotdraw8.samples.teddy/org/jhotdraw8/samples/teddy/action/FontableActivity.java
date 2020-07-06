/*
 * @(#)FontableActivity.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.teddy.action;

import javafx.beans.property.ObjectProperty;
import javafx.scene.text.Font;
import org.jhotdraw8.app.Activity;

public interface FontableActivity extends Activity {
    ObjectProperty<Font> fontProperty();

    default void setFont(Font font) {
        fontProperty().set(font);
    }

    default Font getFont() {
        return fontProperty().get();
    }
}

/*
 * @(#)FigurePersistenceDelegate.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Key;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * FigurePersistenceDelegate.
 *
 * @author Werner Randelshofer
 */
public class FigurePersistenceDelegate extends DefaultPersistenceDelegate {


    protected void initialixze(Class<?> type, Object oldInstance, Object newInstance, @NonNull Encoder out) {
        if (true) {
            return;
        }
        Figure f = (Figure) oldInstance;
        HashMap<String, Object> result = new HashMap<>();
        for (Map.Entry<Key<?>, Object> e : f.getProperties().entrySet()) {
            Key<?> k = e.getKey();
            if (!Objects.equals(e.getValue(), k.getDefaultValue())) {
                result.put(k.getName(), e.getValue());
            }
        }
        out.writeStatement(new Statement("properties", "set", new Object[]{oldInstance, result}));
        out.writeStatement(new Statement("children", "set", new Object[]{oldInstance, f.getChildren()}));
    }


}

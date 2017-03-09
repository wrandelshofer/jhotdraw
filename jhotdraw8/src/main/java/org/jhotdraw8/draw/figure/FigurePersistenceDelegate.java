/* @(#)FigurePersistenceDelegate.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.figure;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jhotdraw8.collection.Key;

/**
 * FigurePersistenceDelegate.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FigurePersistenceDelegate extends DefaultPersistenceDelegate {


    protected void initialixze(Class<?> type, Object oldInstance, Object newInstance, Encoder out) {
        if (true) return;
        Figure f =(Figure)oldInstance;
      HashMap<String,Object> result=new HashMap<>();
        for (Map.Entry<Key<?>,Object> e:f.getProperties().entrySet()) {
            Key<?> k=e.getKey();
            if (!Objects.equals(e.getValue(),k.getDefaultValue())) {
            result.put(k.getName(),e.getValue());
            }
        }
       out.writeStatement(new Statement("properties", "set", new Object[] { oldInstance, result }));
       out.writeStatement(new Statement("children", "set", new Object[] { oldInstance, f.getChildren() }));
    }

    

}

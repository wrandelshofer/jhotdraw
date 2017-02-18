/* @(#)AbstractStyleablePropertyBeanNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.styleable;

import java.util.List;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.FillableFigure;
import org.jhotdraw8.draw.key.PaintableStyleableFigureKey;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

/**
 * AbstractStyleablePropertyBeanNGTest.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AbstractStyleablePropertyBeanNGTest {
    
    public AbstractStyleablePropertyBeanNGTest() {
    }

    @Test
    public void testNullValueIsNotSameAsDefaultPropertyValue() {
        System.out.println("testNullValueIsNotSameAsDefaultPropertyValue");
        AbstractStyleablePropertyBean instance = new AbstractStyleablePropertyBeanImpl();
        final PaintableStyleableFigureKey key = FillableFigure.FILL;
        
        
       assertNotNull(key.getDefaultValue(),"need a key with a non-null default value for this test");
       assertFalse(instance.getProperties().containsKey(key),"value has not been set, map must not contain key");
        assertEquals(instance.get(key),key.getDefaultValue(),"value has not been set, must deliver default value");
        
        instance.set(key, null);
        
       assertNull(instance.get(key),"value has been explicitly set to null");
       assertTrue(instance.getProperties().containsKey(key),"map must contain key after explicit set");
       
       instance.remove(key);
       
       assertEquals(instance.get(key),key.getDefaultValue(),"key has been removed, value must be default value");
       assertFalse(instance.getProperties().containsKey(key),"key has been removed, map must not contain key");
       
    }
    
    public class AbstractStyleablePropertyBeanImpl extends AbstractStyleablePropertyBean {

        @Override
        public String getTypeSelector() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getId() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ObservableList<String> getStyleClass() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getStyle() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Styleable getStyleableParent() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public ObservableSet<PseudoClass> getPseudoClassStates() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
}

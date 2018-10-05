/* @(#)KeyTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import javafx.beans.binding.Bindings;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.text.CssPaintConverter;
import org.jhotdraw8.text.StringConverterAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author werni
 */
public class KeyTest {

    public KeyTest() {
    }

    /**
     * Test of propertyAt method, of class Key.
     */
    @Test
    public void testPropertyAt1() {
        // setup
        ObservableMap<Key<?>, Object> om = FXCollections.observableHashMap();
        MapProperty<Key<?>, Object> mp = new SimpleMapProperty<>(om);
        ObjectKey<String> sk = new ObjectKey<>("s", String.class);
        Property<String> ps = sk.propertyAt(mp);
        
        // test 1
        om.put(sk, "1");
        assertEquals(ps.getValue(),"1");
    }   
    /**
     * Test of propertyAt method, of class Key.
     */
    @Test
    public void testPropertyAt2() {
        // setup
        ObservableMap<Key<?>, Object> om = FXCollections.observableHashMap();
        MapProperty<Key<?>, Object> mp = new SimpleMapProperty<>(om);
        ObjectKey<String> sk = new ObjectKey<>("s", String.class);
        Property<String> ps = sk.propertyAt(mp);
        
        // test 2
        ps.setValue("2");
        assertEquals(om.get(sk),"2");
    }
    /**
     * Test of propertyAt method, of class Key.
     */
    @Test
    public void testPropertyAt3() {
        // setup
        ObservableMap<Key<?>, Object> om = FXCollections.observableHashMap();
        MapProperty<Key<?>, Object> mp = new SimpleMapProperty<>(om);
        ObjectKey<String> sk = new ObjectKey<>("s", String.class);
        Property<String> ps = sk.propertyAt(mp);
        
        // test 3
        Property<String> ps2 = new SimpleStringProperty("ps2");
        ps2.bindBidirectional(ps);
        ps2.setValue("3");
        assertEquals(om.get(sk),"3");
    }
    /**
     * Test of propertyAt method, of class Key.
     */
    @Test
    public void testPropertyAt4() {
        // setup
        ObservableMap<Key<?>, Object> om = FXCollections.observableHashMap();
        MapProperty<Key<?>, Object> mp = new SimpleMapProperty<>(om);
        ObjectKey<String> sk = new ObjectKey<>("s", String.class);
        Property<String> ps = sk.propertyAt(mp);
        
        // test 4
        Property<String> ps2 = new SimpleStringProperty("ps2");
        ps2.bindBidirectional(ps);
        om.put(sk, "4");
        assertEquals(ps2.getValue(),"4");
    }
    /**
     * Test of propertyAt method, of class Key.
     */
    @Test
    public void testPropertyAt5() {
        // setup
        ObservableMap<Key<?>, Object> om = FXCollections.observableHashMap();
        MapProperty<Key<?>, Object> mp = new SimpleMapProperty<>(om);
        ObjectKey<String> sk = new ObjectKey<>("s", String.class);
        Property<String> ps = sk.propertyAt(mp);
        
        // test 5
        Property<String> ps2 = new SimpleStringProperty("ps2");
        ps2.bindBidirectional(ps);
        Property<String> ps3 = new SimpleStringProperty("ps3");
        ps3.bindBidirectional(ps);
        ps2.setValue("5");
        assertEquals(ps3.getValue(),"5");
    }
    /**
     * Test of propertyAt method, of class Key.
     */
    @Test
    public void testPropertyAt6() {
        // setup
        ObservableMap<Key<?>, Object> om = FXCollections.observableHashMap();
        MapProperty<Key<?>, Object> mp = new SimpleMapProperty<>(om);
        ObjectKey<String> sk = new ObjectKey<>("s", String.class);
        Property<String> ps = sk.propertyAt(mp);
        
        // test 6
        Property<String> ps2 = new SimpleStringProperty("ps2");
        ps2.bindBidirectional(ps);
        Property<String> ps3 = new SimpleStringProperty("ps3");
        ps3.bindBidirectional(ps);
        String[] newValueReceived=new String[1];
        ps3.addListener((o,oldValue,newValue)->newValueReceived[0]=newValue);
        ps2.setValue("6");
        assertEquals(ps3.getValue(),"6");
        assertEquals(newValueReceived[0],"6");
    }
    @Test
    public void testPropertyAt7() {
        // setup
        ObservableMap<Key<?>, Object> om = FXCollections.observableHashMap();
        MapProperty<Key<?>, Object> mp = new SimpleMapProperty<>(om);
        ObjectKey<Paint> sk = new ObjectKey<>("c", Paint.class);
        Property<Paint> ps = sk.propertyAt(mp);
        
        // test 6
        Property<String> ps2 = new SimpleStringProperty("ps2");
        Bindings.bindBidirectional(ps2,ps,new StringConverterAdapter<Paint>(new CssPaintConverter()));
        Property<String> ps3 = new SimpleStringProperty("ps3");
        Bindings.bindBidirectional(ps3,ps,new StringConverterAdapter<Paint>(new CssPaintConverter()));
        String[] newValueReceived=new String[1];
        ps3.addListener((o,oldValue,newValue)->newValueReceived[0]=newValue);
        ps2.setValue("#660033");
        assertEquals(ps.getValue(),Color.valueOf("#660033"),"value from ps2 to ps");
        assertEquals(ps3.getValue(),"#660033","value from ps2 to ps to ps3");
        assertEquals(newValueReceived[0],"#660033", "value from ps2 to listener on ps3");
    }
    @Test
    public void testPropertyAt8() {
        // setup
        ObjectProperty<Paint> ps = new SimpleObjectProperty<>();
        
        // test 6
        Property<String> ps2 = new SimpleStringProperty("ps2");
        Bindings.bindBidirectional(ps2,ps,new StringConverterAdapter<Paint>(new CssPaintConverter()));
        Property<String> ps3 = new SimpleStringProperty("ps3");
        Bindings.bindBidirectional(ps3,ps,new StringConverterAdapter<Paint>(new CssPaintConverter()));
        String[] newValueReceived=new String[1];
        ps3.addListener((o,oldValue,newValue)->newValueReceived[0]=newValue);
        ps2.setValue("#660033");
        assertEquals(ps.getValue(),Color.valueOf("#660033"),"value from ps2 to ps");
        assertEquals(ps3.getValue(),"#660033","value from ps2 to ps to ps3");
        assertEquals(newValueReceived[0],"#660033", "value from ps2 to listener on ps3");
    }
}

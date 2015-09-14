/*
 * @(#)ListenerSupport.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */
package org.jhotdraw.beans;

import java.util.function.Consumer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * ListenerSupport.
 *
 * @version $Id$
 */
public class ListenerSupport<T> {

    public void addListener(T listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void removeListener(T listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void fire(Consumer<T> event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

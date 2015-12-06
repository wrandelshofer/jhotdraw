/*
 * @(#)Regex.java
 * Copyright (c) 2014 Supercomputing Systems AG, Schweiz.
 * Alle Rechte vorbehalten. 
 */

package org.jhotdraw.text;

/**
 * Regex.
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Regex {
    private String find;
    private String replace;

    public Regex() {
        this.find=".*";
        this.replace="$1";
    }
    public Regex(String find, String replace) {
        this.find=find;
        this.replace=replace;
    }
    
    public String getFind() {
        return find;
    }
    
    public String getReplace() {
        return replace;
    }

    @Override
    public String toString() {
        return "/" + escape( find) + "/" + escape( find) + "/";
    }
    
    private String escape(String str) {
        return find.replace("/","\\/");
    }
    
    
}

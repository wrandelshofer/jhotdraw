/* @(#)DefaultUnitConverter.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.io;

/**
 * DefaultUnitConverter.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class DefaultUnitConverter implements UnitConverter {

  final static DefaultUnitConverter instance = new DefaultUnitConverter();

  public static DefaultUnitConverter getInstance() {
    return instance;
  }
}

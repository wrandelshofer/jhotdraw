/* @(#)FXExecutor.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.concurrent;

import java.util.concurrent.Executor;
import javafx.application.Platform;

/**
 * An Executor which executes the provided runnable on the Java FX Application thread.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class FXExecutor implements Executor {

  private final static FXExecutor instance = new FXExecutor();
  
  private FXExecutor() {
    
  }
  
  @Override
  public void execute(Runnable command) {
    Platform.runLater(command);
  }

  
  public static FXExecutor getInstance() {
    return instance;
  }
}

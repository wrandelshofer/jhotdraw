/* @(#)AbstractDrawingView.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.jhotdraw.beans.SimplePropertyBean;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.input.ClipboardInputFormat;
import org.jhotdraw.draw.input.ClipboardOutputFormat;

/**
 * AbstractDrawingView.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public abstract class AbstractDrawingView extends SimplePropertyBean implements DrawingView {
  private ObjectProperty<ClipboardOutputFormat> clipboardOutputFormat = new SimpleObjectProperty<>(this,CLIPBOARD_OUTPUT_FORMAT_PROPERTY);
  private ObjectProperty<ClipboardInputFormat> clipboardInputFormat = new SimpleObjectProperty<>(this,CLIPBOARD_INPUT_FORMAT_PROPERTY);

  @Override
  public ObjectProperty<ClipboardInputFormat> clipboardInputFormatProperty() {
    return clipboardInputFormat;
  }

  @Override
  public ObjectProperty<ClipboardOutputFormat> clipboardOutputFormatProperty() {
    return clipboardOutputFormat;
  }
  
  public void cut() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  public void copy() {
    ClipboardOutputFormat out = getClipboardOutputFormat();
    if (out == null) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } else {
      Map<DataFormat, Object> content=new LinkedHashMap<>();
      try { 
        final ObservableSet<Figure> selectedFigures = getSelectedFigures();
        
        out.write(content, getDrawing(), selectedFigures.isEmpty()?FXCollections.singletonObservableList(getDrawing()):selectedFigures);
         Clipboard.getSystemClipboard().setContent(content);
         
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  public void paste() {
    Clipboard cb = Clipboard.getSystemClipboard();
    
    for (DataFormat df:cb.getContentTypes()) {
          
    System.out.println(df);
    System.out.println(cb.getContent(df));

    }
    
    ClipboardInputFormat in = getClipboardInputFormat();
    if (in == null) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  }

  

}

/* @(#)CssPoint2DConverter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;

/**
 * Converts a {@code CssSize2D} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssPaperSizeConverter implements Converter<CssSize2D> {

  private final CssSizeConverter sizeConverter = new CssSizeConverter(false);
  private final static Map<String, CssSize2D> paperSizes;
  private final static Map<CssSize2D,String> sizePapers;

  static {
    Map<String, CssSize2D> m = new LinkedHashMap<>();
    m.put("A0", new CssSize2D(new CssSize(841, "mm"), new CssSize(1189, "mm")));
    m.put("A1", new CssSize2D(new CssSize(594, "mm"), new CssSize(841, "mm")));
    m.put("A2", new CssSize2D(new CssSize(420, "mm"), new CssSize(594, "mm")));
    m.put("A3", new CssSize2D(new CssSize(297, "mm"), new CssSize(420, "mm")));
    m.put("A4", new CssSize2D(new CssSize(210, "mm"), new CssSize(297, "mm")));
    m.put("A5", new CssSize2D(new CssSize(148, "mm"), new CssSize(210, "mm")));
    m.put("A6", new CssSize2D(new CssSize(105, "mm"), new CssSize(148, "mm")));
    m.put("DesignatedLong", new CssSize2D(new CssSize(110, "mm"), new CssSize(220, "mm")));
    m.put("Letter", new CssSize2D(new CssSize(8.5, "in"), new CssSize(11, "in")));
    m.put("Legal", new CssSize2D(new CssSize(8.4, "in"), new CssSize(14, "in")));
    m.put("Tabloid", new CssSize2D(new CssSize(11.0, "in"), new CssSize(17.0, "in")));
    m.put("Executive", new CssSize2D(new CssSize(7.25, "in"), new CssSize(10.5, "in")));
    m.put("x8x10", new CssSize2D(new CssSize(8, "in"), new CssSize(10, "in")));
    m.put("MonarchEnvelope", new CssSize2D(new CssSize(3.87, "in"), new CssSize(7.5, "in")));
    m.put("Number10Envelope", new CssSize2D(new CssSize(4.125, "in"), new CssSize(9.5, "in")));
    m.put("C", new CssSize2D(new CssSize(17.0, "in"), new CssSize(22.0, "in")));
    m.put("B4", new CssSize2D(new CssSize(257, "mm"), new CssSize(364, "mm")));
    m.put("B5", new CssSize2D(new CssSize(182, "mm"), new CssSize(257, "mm")));
    m.put("B6", new CssSize2D(new CssSize(128, "mm"), new CssSize(182, "mm")));
    m.put("JapanesePostcard", new CssSize2D(new CssSize(100, "mm"), new CssSize(148, "mm")));
    paperSizes = m;
    
     Map<CssSize2D,String> x = new LinkedHashMap<>();
     for (Map.Entry<String,CssSize2D> e:m.entrySet()){
      final CssSize2D v = e.getValue();
       x.put(v,e.getKey()+" portrait");
       x.put(new CssSize2D(v.getY(),v.getX()),e.getKey()+" landscape");
     }
     sizePapers=x;
  }

  private final static String LANDSCAPE = "landscape";
  private final static String PORTRAIT = "portrait";

  private CssSize2D parsePageSize(CssTokenizerInterface tt) throws ParseException, IOException {
    tt.setSkipWhitespaces(true);
    if (tt.nextToken() == CssTokenizerInterface.TT_IDENT) {
      CssSize2D paperSize = paperSizes.get(tt.currentStringValue());
      if (paperSize == null) {
        throw new ParseException("Illegal paper format:" + tt.currentStringValue(), tt.getStartPosition());
      }
      if (tt.nextToken() == CssTokenizerInterface.TT_IDENT) {
        switch (tt.currentStringValue()) {
          case LANDSCAPE:
            paperSize = new CssSize2D(paperSize.getY(), paperSize.getX());
            break;
          case PORTRAIT:
            break;
          default:
            tt.pushBack();
        }
      } else {
        tt.pushBack();
      }
        return paperSize;
    } else {
      tt.pushBack();
      CssSize x = sizeConverter.parseSize(tt);
      CssSize y = sizeConverter.parseSize(tt);
      return new CssSize2D(x, y);
    }
  }

  @Override
  public void toString(Appendable out, IdFactory idFactory, CssSize2D value) throws IOException {
String paper=sizePapers.get(value);
if (paper != null) out.append(paper);
else{
  sizeConverter.toString(value.getX());
  out.append(' ');
  sizeConverter.toString(value.getY());
}
  }

  @Override
  public CssSize2D fromString(CharBuffer buf, IdFactory idFactory) throws ParseException, IOException {
    CssTokenizerInterface tt = new CssTokenizer(buf);
    return parsePageSize(tt);

  }

  @Override
  public CssSize2D getDefaultValue() {
    return new CssSize2D(new CssSize(0, null), new CssSize(0, null));
  }

  @Override
  public String getHelpText() {
    StringBuilder buf = new StringBuilder();
    for (String s : paperSizes.keySet()) {
      if (buf.length() != 0) {
        buf.append('｜');
      }
      buf.append(s);
    }
    return "Format of ⟨PageSize⟩: " + "⟨width⟩mm ⟨height⟩mm｜⟨PaperFormat⟩ landscape｜⟨PaperFormat⟩ portrait"
            + "\nFormat of ⟨PaperFormat⟩: " + buf.toString();
  }

}

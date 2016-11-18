/* @(#)CssTokenizerInterface.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.css;

import java.io.IOException;

/**
 * CssTokenizerInterface.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface CssTokenizerInterface {

  int TT_AT_KEYWORD = -3;
  int TT_BAD_COMMENT = -7;
  int TT_BAD_STRING = -5;
  int TT_BAD_URI = -6;
  int TT_CDC = -15;
  int TT_CDO = -14;
  int TT_COLUMN = -24;
  int TT_COMMENT = -17;
  int TT_DASH_MATCH = -20;
  int TT_DIMENSION = -11;
  int TT_EOF = -1;
  int TT_FUNCTION = -18;
  int TT_HASH = -8;
  /**
   * Token types. DELIM token are given as UTF-16 characters.
   */
  int TT_IDENT = -2;
  int TT_INCLUDE_MATCH = -19;
  int TT_NUMBER = -9;
  int TT_PERCENTAGE = -10;
  int TT_PREFIX_MATCH = -21;
  int TT_S = -16;
  int TT_STRING = -4;
  int TT_SUBSTRING_MATCH = -23;
  int TT_SUFFIX_MATCH = -22;
  int TT_UNICODE_RANGE = -13;
  int TT_URI = -12;

  Number currentNumericValue();

  String currentStringValue();

  int currentToken();

  int getLineNumber();

  int getPosition();

  int nextToken() throws IOException;

  /**
   * Pushes the current token back.
   */
  void pushBack();

  /** Consumes tokens until a non-whitespace token arrives.
   * That token is then pushed back.
   *
   * @throws IOException on IO failure
   */
  void skipWhitespace() throws IOException;

}

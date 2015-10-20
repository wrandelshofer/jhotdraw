/* @(#)Term.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.css.ast;

import org.jhotdraw.css.CssTokenizer;

/**
 * Term.
 * @author Werner Randelshofer
 */
public class Term extends AST {
    /** The token type. */
    private final int ttype;
    /** The string value. */
    private final String stringValue;
    /** The numeric value. */
    private final double numericValue;

    public Term(int ttype, String stringValue, double numericValue) {
        this.ttype = ttype;
        this.stringValue = stringValue;
        this.numericValue = numericValue;
    }

    @Override
    public String toString() {
        if (ttype>=0) {
            return stringValue;
        }
        switch (ttype) {
    case CssTokenizer.TT_IDENT : return fromIDENT(stringValue) ;
    case CssTokenizer.TT_AT_KEYWORD : return "@"+fromIDENT(stringValue) ;
    case CssTokenizer.TT_STRING : return fromSTRING(stringValue) ;
    //case CssTokenizer.TT_BAD_STRING : return fromBAD_STRING(stringValue) ;
    //case CssTokenizer.TT_BAD_URI : return fromBAD_URI(stringValue) ;
    //case CssTokenizer.TT_BAD_COMMENT : return fromBAD_COMMENT(stringValue) ;
    case CssTokenizer.TT_HASH : return "#"+fromIDENT(stringValue) ;
    case CssTokenizer.TT_NUMBER : return fromNUMBER(numericValue) ;
    case CssTokenizer.TT_PERCENTAGE : return fromNUMBER(numericValue)+"%" ;
    case CssTokenizer.TT_DIMENSION : return fromNUMBER(numericValue)+fromIDENT(stringValue) ;
    case CssTokenizer.TT_URI : return fromURI(stringValue) ;
    case CssTokenizer.TT_UNICODE_RANGE : return fromUNICODE_RANGE(stringValue) ;
    case CssTokenizer.TT_CDO : return fromCDO(stringValue) ;
    case CssTokenizer.TT_CDC : return fromCDC(stringValue) ;
    case CssTokenizer.TT_S : return fromS(stringValue) ;
    //case CssTokenizer.TT_COMMENT : return fromCOMMENT(stringValue) ;
    case CssTokenizer.TT_FUNCTION : return fromFUNCTION(stringValue) ;
    case CssTokenizer.TT_INCLUDE_MATCH : return fromINCLUDE_MATCH(stringValue) ;
    case CssTokenizer.TT_DASH_MATCH : return fromDASH_MATCH(stringValue) ;
    case CssTokenizer.TT_PREFIX_MATCH : return fromPREFIX_MATCH(stringValue) ;
    case CssTokenizer.TT_SUFFIX_MATCH : return fromSUFFIX_MATCH(stringValue) ;
    case CssTokenizer.TT_SUBSTRING_MATCH : return fromSUBSTRING_MATCH(stringValue) ;
    case CssTokenizer.TT_COLUMN : return fromCOLUMN(stringValue) ;

        }
        throw new InternalError("Unsupported TTYPE:"+ttype);
    }

    private String fromIDENT(String stringValue) {
       return stringValue;
    }

    private String fromSTRING(String stringValue) {
       return stringValue;
    }

    private String fromBAD_STRING(String stringValue) {
       return stringValue;
    }

    private String fromBAD_URI(String stringValue) {
       return stringValue;
    }

    private String fromBAD_COMMENT(String stringValue) {
       return stringValue;
    }

    private String fromNUMBER(double doubleValue) {
       return Double.toString(doubleValue);
    }

    private String fromPERCENTAGE(String stringValue) {
       return stringValue;
    }

    private String fromDIMENSION(String stringValue) {
       return stringValue;
    }

    private String fromURI(String stringValue) {
       return stringValue;
    }

    private String fromUNICODE_RANGE(String stringValue) {
       return stringValue;
    }

    private String fromCDO(String stringValue) {
       return stringValue;
    }

    private String fromCDC(String stringValue) {
       return stringValue;
    }

    private String fromS(String stringValue) {
       return stringValue;
    }

    private String fromCOMMENT(String stringValue) {
       return stringValue;
    }

    private String fromFUNCTION(String stringValue) {
       return stringValue;
    }

    private String fromINCLUDE_MATCH(String stringValue) {
       return stringValue;
    }

    private String fromDASH_MATCH(String stringValue) {
       return stringValue;
    }

    private String fromPREFIX_MATCH(String stringValue) {
       return stringValue;
    }

    private String fromSUFFIX_MATCH(String stringValue) {
       return stringValue;
    }

    private String fromSUBSTRING_MATCH(String stringValue) {
       return stringValue;
    }

    private String fromCOLUMN(String stringValue) {
       return stringValue;
    }    
}

/* @(#)Term.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.css.ast;

import org.jhotdraw.css.CssTokenizer;
import org.jhotdraw.text.XmlDoubleConverter;

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
    private final Number numericValue;
    
    
    private final static XmlDoubleConverter DOUBLE_CONVERTER = new XmlDoubleConverter();

    public Term(int ttype, String stringValue, Number numericValue) {
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
    case CssTokenizer.TT_IDENT : return fromIDENT() ;
    case CssTokenizer.TT_AT_KEYWORD : return "@"+fromIDENT() ;
    case CssTokenizer.TT_STRING : return fromSTRING() ;
    //case CssTokenizer.TT_BAD_STRING : return fromBAD_STRING(stringValue) ;
    //case CssTokenizer.TT_BAD_URI : return fromBAD_URI(stringValue) ;
    //case CssTokenizer.TT_BAD_COMMENT : return fromBAD_COMMENT(stringValue) ;
    case CssTokenizer.TT_HASH : return "#"+fromIDENT() ;
    case CssTokenizer.TT_NUMBER : return fromNUMBER() ;
    case CssTokenizer.TT_PERCENTAGE : return fromNUMBER()+"%" ;
    case CssTokenizer.TT_DIMENSION : return fromNUMBER()+fromIDENT() ;
    case CssTokenizer.TT_URI : return fromURI() ;
    case CssTokenizer.TT_UNICODE_RANGE : return fromUNICODE_RANGE() ;
    //case CssTokenizer.TT_CDO : return fromCDO() ;
    //case CssTokenizer.TT_CDC : return fromCDC() ;
    case CssTokenizer.TT_S : return fromS() ;
    //case CssTokenizer.TT_COMMENT : return fromCOMMENT() ;
    case CssTokenizer.TT_FUNCTION : return fromIDENT()+"(" ;
    case CssTokenizer.TT_INCLUDE_MATCH : return fromINCLUDE_MATCH() ;
    case CssTokenizer.TT_DASH_MATCH : return fromDASH_MATCH() ;
    case CssTokenizer.TT_PREFIX_MATCH : return fromPREFIX_MATCH() ;
    case CssTokenizer.TT_SUFFIX_MATCH : return fromSUFFIX_MATCH() ;
    case CssTokenizer.TT_SUBSTRING_MATCH : return fromSUBSTRING_MATCH() ;
    case CssTokenizer.TT_COLUMN : return fromCOLUMN() ;

        }
        throw new InternalError("Unsupported TTYPE:"+ttype);
    }

    private String fromIDENT() {
       return stringValue;
    }

    private String fromSTRING() {
        // FIXME implement proper escaping
        if (stringValue.contains("'")) {
         return '"'+stringValue+'"';   
        }else{
       return '\''+stringValue+'\'';
        }
    }


    private String fromNUMBER() {
        if (numericValue instanceof Double) {
       return DOUBLE_CONVERTER.toString((Double)numericValue);
        }else{
            return numericValue.toString();
        }
    }

    private String fromPERCENTAGE() {
       return stringValue;
    }

    private String fromDIMENSION() {
       return stringValue;
    }

    private String fromURI() {
        // FIXME escape string value if necessary
       return stringValue;
    }

    private String fromUNICODE_RANGE() {
       return stringValue;
    }

    private String fromS() {
       return " ";
    }

    private String fromCOMMENT() {
       return stringValue;
    }

    private String fromINCLUDE_MATCH() {
       return stringValue;
    }

    private String fromDASH_MATCH() {
       return stringValue;
    }

    private String fromPREFIX_MATCH() {
       return stringValue;
    }

    private String fromSUFFIX_MATCH() {
       return stringValue;
    }

    private String fromSUBSTRING_MATCH() {
       return stringValue;
    }

    private String fromCOLUMN() {
       return stringValue;
    }    
}

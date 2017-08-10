/* @(#)PreservedToken.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.text.CssStringConverter;
import org.jhotdraw8.text.XmlNumberConverter;

/**
 * PreservedToken.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PreservedToken extends AST {

    /**
     * The token type.
     */
    private final int ttype;
    /**
     * The string value.
     */
    private final String stringValue;
    /**
     * The numeric value.
     */
    private final Number numericValue;

    private int startPos = -1;
    private int endPos = -1;

    private final static XmlNumberConverter DOUBLE_CONVERTER = new XmlNumberConverter();

    public PreservedToken(int ttype, String stringValue, Number numericValue, int startPos, int endPos) {
        this.ttype = ttype;
        this.stringValue = stringValue;
        this.numericValue = numericValue;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    @Override
    public String toString() {
        if (ttype >= 0) {
            return stringValue;
        }
        switch (ttype) {
            case CssTokenizer.TT_IDENT:
                return fromIDENT();
            case CssTokenizer.TT_AT_KEYWORD:
                return "@" + fromIDENT();
            case CssTokenizer.TT_STRING:
                return fromSTRING();
            //case CssTokenizer.TT_BAD_STRING : return fromBAD_STRING(stringValue) ;
            //case CssTokenizer.TT_BAD_URI : return fromBAD_URI(stringValue) ;
            //case CssTokenizer.TT_BAD_COMMENT : return fromBAD_COMMENT(stringValue) ;
            case CssTokenizer.TT_HASH:
                return "#" + fromIDENT();
            case CssTokenizer.TT_NUMBER:
                return fromNUMBER();
            case CssTokenizer.TT_PERCENTAGE:
                return fromNUMBER() + "%";
            case CssTokenizer.TT_DIMENSION:
                return fromNUMBER() + fromIDENT();
            case CssTokenizer.TT_URI:
                return fromURI();
            case CssTokenizer.TT_UNICODE_RANGE:
                return fromUNICODE_RANGE();
            //case CssTokenizer.TT_CDO : return fromCDO() ;
            //case CssTokenizer.TT_CDC : return fromCDC() ;
            case CssTokenizer.TT_S:
                return fromS();
            //case CssTokenizer.TT_COMMENT : return fromCOMMENT() ;
            case CssTokenizer.TT_FUNCTION:
                return fromIDENT() + "(";
            case CssTokenizer.TT_INCLUDE_MATCH:
                return fromINCLUDE_MATCH();
            case CssTokenizer.TT_DASH_MATCH:
                return fromDASH_MATCH();
            case CssTokenizer.TT_PREFIX_MATCH:
                return fromPREFIX_MATCH();
            case CssTokenizer.TT_SUFFIX_MATCH:
                return fromSUFFIX_MATCH();
            case CssTokenizer.TT_SUBSTRING_MATCH:
                return fromSUBSTRING_MATCH();
            case CssTokenizer.TT_COLUMN:
                return fromCOLUMN();

        }
        throw new InternalError("Unsupported TTYPE:" + ttype);
    }

    private String fromIDENT() {
        return stringValue;
    }

    private final static CssStringConverter cssStringConverter = new CssStringConverter();

    private String fromSTRING() {
        return cssStringConverter.toString(stringValue);
        /*
        // FIXME implement proper escaping
        if (stringValue.contains("'")) {
         return '"'+stringValue.replaceAll("\n", "\\\n")+'"';   
        }else{
       return '\''+stringValue.replaceAll("\n", "\\\n")+'\'';
        }*/
    }

    private String fromNUMBER() {
        if (numericValue instanceof Double) {
            return DOUBLE_CONVERTER.toString((Double) numericValue);
        } else {
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
        return "url(" + stringValue + ")";
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

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

}

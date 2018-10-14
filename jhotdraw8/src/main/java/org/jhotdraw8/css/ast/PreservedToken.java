/* @(#)PreservedToken.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.ast;

import org.jhotdraw8.css.CssToken;
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
            case CssToken.TT_IDENT:
                return fromIDENT();
            case CssToken.TT_AT_KEYWORD:
                return "@" + fromIDENT();
            case CssToken.TT_STRING:
                return fromSTRING();
            //case CssToken.TT_BAD_STRING : return fromBAD_STRING(stringValue) ;
            //case CssToken.TT_BAD_URI : return fromBAD_URI(stringValue) ;
            //case CssToken.TT_BAD_COMMENT : return fromBAD_COMMENT(stringValue) ;
            case CssToken.TT_HASH:
                return "#" + fromIDENT();
            case CssToken.TT_NUMBER:
                return fromNUMBER();
            case CssToken.TT_PERCENTAGE:
                return fromNUMBER() + "%";
            case CssToken.TT_DIMENSION:
                return fromNUMBER() + fromIDENT();
            case CssToken.TT_URL:
                return fromURI();
            case CssToken.TT_UNICODE_RANGE:
                return fromUNICODE_RANGE();
            //case CssToken.TT_CDO : return fromCDO() ;
            //case CssToken.TT_CDC : return fromCDC() ;
            case CssToken.TT_S:
                return fromS();
            //case CssToken.TT_COMMENT : return fromCOMMENT() ;
            case CssToken.TT_FUNCTION:
                return fromIDENT() + "(";
            case CssToken.TT_INCLUDE_MATCH:
                return fromINCLUDE_MATCH();
            case CssToken.TT_DASH_MATCH:
                return fromDASH_MATCH();
            case CssToken.TT_PREFIX_MATCH:
                return fromPREFIX_MATCH();
            case CssToken.TT_SUFFIX_MATCH:
                return fromSUFFIX_MATCH();
            case CssToken.TT_SUBSTRING_MATCH:
                return fromSUBSTRING_MATCH();
            case CssToken.TT_COLUMN:
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

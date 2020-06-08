/*
 * @(#)CssTokenType.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

/**
 * Defines CSS 3 token types.
 * <p>
 * References:
 * <ul>
 * <li><a href="https://www.w3.org/TR/css-syntax-3/#tokenization">
 * CSS Syntax Module Level 3, Tokenization</a></li>
 * </ul>
 */
public class CssTokenType {

    public final static String IDENT_NONE = "none";

    /**
     * The 'initial' keyword is used to reset a property.
     *
     * <a href="https://www.w3.org/TR/css3-cascade/#initial">Resetting a
     * Property: the 'initial' keyword.</a>
     */
    public final static String IDENT_INITIAL = "initial";

    /**
     * Defines an at-keyword-token.
     * <pre>
     *     at-keyword-token = '@', ident-token ;
     * </pre>
     */
    public final static int TT_AT_KEYWORD = -3;

    /**
     * Defines a bad-comment-token.
     */
    public final static int TT_BAD_COMMENT = -7;

    /**
     * Defines a bad-string-token.
     */
    public final static int TT_BAD_STRING = -5;

    /**
     * Defines a bad-uri-token.
     */
    public final static int TT_BAD_URI = -6;

    /**
     * Defines a CDC-token.
     * <pre>
     *  CDC-token ={@literal "-->"};
     * </pre>
     */
    public final static int TT_CDC = -15;

    /**
     * Defines a CDO-token.
     * <pre>
     *  CDO-token ={@literal "<!--"};
     * </pre>
     */
    public final static int TT_CDO = -14;

    /**
     * Defines a column-token.
     * <pre>
     *  column-token = "||";
     * </pre>
     */
    public final static int TT_COLUMN = -24;

    /**
     * Defines a comment-token.
     * <pre>
     *     comment-token = "/','*',comment-body,'*','/' ;
     *     comment-body = (* anything but '*' followed by '/' *);
     * </pre>
     */
    public final static int TT_COMMENT = -17;

    /**
     * Defines a dash-match-token.
     * <pre>
     *  dash-match-token = "|=";
     * </pre>
     */
    public final static int TT_DASH_MATCH = -20;

    /**
     * Defines a dimension-token.
     * <pre>
     *     dimension-token = number-token , ident-token ;
     * </pre>
     */
    public final static int TT_DIMENSION = -11;

    /**
     * Defines an EOF-token.
     */
    public final static int TT_EOF = -1;

    /**
     * Defines a function-token.
     * <pre>
     *     function-token = ident-token , '(' ;
     * </pre>
     */
    public final static int TT_FUNCTION = -18;

    /**
     * Defines a hash-token.
     * <pre>
     *     hash-token = '#' , ident-char , { ident-char } ;
     *
     *     ident-char = ( letter | '_' | digit )
     *                | non-ASCII
     *                | escape ;
     *
     *     letter = (* a letter from 'a' to 'z' and 'A' to 'Z' *) ;
     *
     *     digit = "0" | "1"  | "2"  | "3"  | "4"  | "5"  | "6"  | "7"  | "8"  | "9" ;
     *
     *     escape = '\' , char - (newline | hex-digit)
     *            | '\' , 6 * hex-digit
     *            | '\', hex-digit , 4 * {hex-digit}, whitespace ;
     *
     *     non-ASCII = (* a unicode code-point between U+80 and U+10FFFF )
     *
     * </pre>
     */
    public final static int TT_HASH = -8;

    /**
     * Defines an ident-token.
     * <pre>
     *     ident-token = { '-' } , first-ident-char , { ident-char } ;
     *
     *     first-ident-char = ('a'-'z'|'A'-'Z'|'_')
     *                      | non-ASCII
     *                      | escape ;
     *
     *     ident-char = ( 'a'-'z' | 'A'-'Z' | '_' | '-' | '0'-'9' )
     *                | non-ASCII
     *                | escape ;
     *
     *     escape = '\' , (* not newline or hex-digit *)
     *            | '\' , 6 * hex-digit
     *            | '\', hex-digit , 4 * {hex-digit}, whitespace ;
     *
     *     hex-digit = digit
     *               | 'a' | 'b' | 'c' | 'd' | 'e' | 'f'
     *               | 'A' | 'B' | 'C' | 'D' | 'E' | 'F'
     * </pre>
     */
    public final static int TT_IDENT = -2;

    /**
     * Defines an include-match-token.
     * <pre>
     *  include-match-token = "~=";
     * </pre>
     */
    public final static int TT_INCLUDE_MATCH = -19;

    /**
     * Defines a number-token.
     * <pre>
     * number-token      ::= decimal-number | scientific-number ;
     * integer           ::= [ "+" | "-" ] , digit, {digit} ;
     * decimal-number    ::= integer
     *                     | [ "+" | "-" ] , {digit} , "." , digit, {digit} ;
     * scientific-number ::= decimal-number , ( "E" | "e" ) , integer ;
     * </pre>
     */
    public final static int TT_NUMBER = -9;

    /**
     * Defines a percentage-token.
     * <pre>
     *     percentage-token = number-token , '%' ;
     * </pre>
     */
    public final static int TT_PERCENTAGE = -10;

    /**
     * Defines a prefix-match-token.
     * <pre>
     *  prefix-match-token = "^=";
     * </pre>
     */
    public final static int TT_PREFIX_MATCH = -21;

    /**
     * Defines a ws*-token.
     * <pre>
     *     ws* = { whitespace-token } ;
     *
     *     whitespace-token = { whitespace } ;
     *
     *     whitespace = ' ' | '\' | newline ;
     *
     *     newline = '\n' | "\r\n" | '\r' | '\f' ;
     * </pre>
     */
    public final static int TT_S = -16;

    /**
     * Defines a string-token.
     * <pre>
     *     string-token = quote-string
     *                  | apostrophe-string ;
     *
     *     quote-string = '"', {quote-string-body}, '"' ;
     *
     *     apostrophe-string = "'", {apostrophe-string-body}, "'" ;
     *
     *     quote-string-body = (* not " \ or newline | escape | '\' newline *);
     *
     *     apostrophe-string-body = (* not ' \ or newline | escape | '\' newline *);
     *
     *     escape = '\' , char - (newline | hex-digit)
     *            | '\' , 6 * hex-digit
     *            | '\', hex-digit , 4 * {hex-digit}, whitespace ;
     *
     *     newline = '\n' | "\r\n" | '\r' | '\f' ;
     * </pre>
     */
    public final static int TT_STRING = -4;

    /**
     * Defines a substring-match-token.
     * <pre>
     *  substring-match-token = "*=";
     * </pre>
     */
    public final static int TT_SUBSTRING_MATCH = -23;

    /**
     * Defines a suffix-match-token.
     * <pre>
     *  suffix-match-token = "$=";
     * </pre>
     */
    public final static int TT_SUFFIX_MATCH = -22;

    /**
     * Defines a unicode-range-token.
     * <pre>
     *     unicode-range = ('U'|'u'),'+',( mask-range, from-to-range );
     *     mask-range = 1 * hex-digit , 5 * { '?' }
     *                | 2 * hex-digit , 4 * { '?' }
     *                | 3 * hex-digit , 3 * { '?' }
     *                | 4 * hex-digit , 2 * { '?' }
     *                | 5 * hex-digit , 1 * { '?' }
     *                | 6 * hex-digit
     *     from-to-range = hex-digit, 5 * { hex-digit } , '-' , hex-digit, 5 * { hex-digit } ;
     * </pre>
     */
    public final static int TT_UNICODE_RANGE = -13;

    /**
     * Defines a url-token.
     * <pre>
     *     url-token = "url(" , ws* , url-unquoted | string-token , ws*, ')' ;
     *     url-unquoted = ur-unquoted-char , {url-unquoted-char} ;
     *     url-unquoted-char = not " ' ( ) \ whitespace or non-printable | escape ;
     * </pre>
     */
    public final static int TT_URL = -12;

    /**
     * Defines the comma "," delim-token.
     */
    public final static int TT_COMMA = ',';

    /**
     * Defines the semicolon ";" delim-token.
     */
    public final static int TT_SEMICOLON = ';';

    /**
     * Defines the point "." delim-token.
     */
    public final static int TT_POINT = '.';

    /**
     * Defines the colon ":" delim-token.
     */
    public final static int TT_COLON = ':';

    /**
     * Defines the asterisk "*" delim-token.
     */
    public final static int TT_ASTERISK = '*';

    /**
     * Defines the left bracket ")" delim-token.
     */
    public final static int TT_LEFT_BRACKET = '(';
    /**
     * Defines the right round bracket ")" delim-token.
     */
    public final static int TT_RIGHT_BRACKET = ')';
    /**
     * Defines the right curly bracket "}" delim-token.
     */
    public final static int TT_RIGHT_CURLY_BRACKET = '}';
    /**
     * Defines the left curly bracket "{" delim-token.
     */
    public final static int TT_LEFT_CURLY_BRACKET = '{';
    /**
     * Defines the right square bracket "]" delim-token.
     */
    public final static int TT_RIGHT_SQUARE_BRACKET = ']';
    /**
     * Defines the left square bracket "[" delim-token.
     */
    public final static int TT_LEFT_SQUARE_BRACKET = '[';
    /**
     * Defines the equals "=" delim-token.
     */
    public final static int TT_EQUALS = '=';
    /**
     * Defines the slash "/" delim-token.
     */
    public final static int TT_SLASH = '/';

    /**
     * Defines the plus "+" delim-token.
     */
    public final static int TT_PLUS = '+';
    /**
     * Defines the percent "%" delim-token.
     */
    public final static int TT_PERCENT_DELIM = '%';

    /**
     * Defines the vertical line "|" delim-token.
     */
    public final static int TT_VERTICAL_LINE = '|';

    /**
     * Defines the greater than ">" delim-token.
     */
    public static final int TT_GREATER_THAN = '>';
    public static final int TT_TILDE = '~';
}

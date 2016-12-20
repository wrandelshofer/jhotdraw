/* @(#)CssParser.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.jhotdraw8.css.ast.AbstractAttributeSelector;
import org.jhotdraw8.css.ast.AdjacentSiblingCombinator;
import org.jhotdraw8.css.ast.AndCombinator;
import org.jhotdraw8.css.ast.AtRule;
import org.jhotdraw8.css.ast.ExistsMatchSelector;
import org.jhotdraw8.css.ast.EqualsMatchSelector;
import org.jhotdraw8.css.ast.ChildCombinator;
import org.jhotdraw8.css.ast.ClassSelector;
import org.jhotdraw8.css.ast.DashMatchSelector;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.DescendantCombinator;
import org.jhotdraw8.css.ast.FunctionPseudoClassSelector;
import org.jhotdraw8.css.ast.GeneralSiblingCombinator;
import org.jhotdraw8.css.ast.IdSelector;
import org.jhotdraw8.css.ast.IncludeMatchSelector;
import org.jhotdraw8.css.ast.PrefixMatchSelector;
import org.jhotdraw8.css.ast.PseudoClassSelector;
import org.jhotdraw8.css.ast.SelectNothingSelector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.SelectorGroup;
import org.jhotdraw8.css.ast.SimplePseudoClassSelector;
import org.jhotdraw8.css.ast.SimpleSelector;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.css.ast.SubstringMatchSelector;
import org.jhotdraw8.css.ast.SuffixMatchSelector;
import org.jhotdraw8.css.ast.PreservedToken;
import org.jhotdraw8.css.ast.TypeSelector;
import org.jhotdraw8.css.ast.UniversalSelector;

/**
 * The {@code CssParser} processes a stream of characters into a
 * {@code Stylesheet} object.
 * <p>
 * The CSS Syntax Module Level 3 defines a grammar which is equivalent to the
 * following EBNF ISO/IEC 14977 productions:
 * <pre>
 * stylesheet_core = { S | CDO | CDC | qualified_rule | at_rule} ;
 *
 * rule_list    = { S | qualified_rule | at_rule} ;
 *
 * at_rule      = AT_KEYWORD , { component_value } , ( curly_block | ';' ) ;
 *
 * qualified_rule
 *              = { component_value } , curly_block ;
 *
 * declaration_list_core
 *              = { S } , ( [ declaration_core ] , [ ';' , declaration_list_core ]
 *                        | at_rule , declaration_list_core ,
 *                        ) ;
 *
 * declaration_core  = IDENT , { S } ,  ":", { component_value } , [ !important ] ;
 *
 * !important   = '!' , { S } , "important" , { S } ;
 *
 * component_value
 *              = ( preserved_token | curly_block | round_block | square_block
 *                | function_block ) ;
 *
 * curly_block  = '{' , { component_value } , '}' ;
 * round_block  = '(' , { component_value } , ')' ;
 * square_block = '[' , { component_value } , ']' ;
 * function_block
 *              = FUNCTION , { component_value } , ')' ;
 *
 * </pre> This parser parses the following syntax:
 * <pre>
 * stylesheet   = { S | CDO | CDC | qualified_rule | style_rule} ;
 *
 * operator     = ( '/' | ',' ) , { S } ;
 *
 * combinator   = ( '+' | '&gt;' | '~' ) , { S } ;
 *
 * unary_operator
 *              = ( '-' | '+' ) ;
 *
 * property     = IDENT , { S } ;
 *
 * style_rule   = [ selector_group ] , "{" , declaration_list , "}" ;
 *
 * selector_group
 *              = selector , { "," , { S }, selector } ;
 *
 * selector     = simple_selector ,
 *                { ( combinator , selector
 *                  | { S }, [ [ combinator ] , selector ]
 *                  )
 *                } ;
 *
 * simple_selector
 *              = universal_selector | type_selector | id_selector
 *                | class_selector | pseudoclass_selector | attribute_selector ;
 * universal_selector   = '*' ;
 * type_selector        = IDENT ;
 * id_selector          = HASH ;
 * class_selector       = "." , IDENT ;
 * pseudoclass_selector = ":" , IDENT ;
 * attribute_selector   = "[" , IDENT
 *                            , [ ( "=" | "~=" | "|=" ) , ( IDENT | STRING ) ],
 *                        "]" ;
 *
 * declaration_list
 *              = { S } , [ declaration ] , [ ';' , declaration_list ] ;
 *
 * declaration  = IDENT , { S } ,  ":", { preserved_token } ;
 *
 * term         = [ unary_operator] ,
 *                ( NUMBER , { S } | PERCENTAGE , { S }  | LENGTH , { S }
 *                | EMS , { S } | EXS , { S } | ANGLE , { S } | TIME , { S }
 *                | FREQ , { S } | STRING , { S } | IDENT , { S } | URI , { S }
 *                | hexcolor | function
 *                ) ;
 *
 * function     = FUNCTION , { S } , expr , ')' , { S } ;
 * expr         = term , { [ operator ] , term } ;
 *
 * hexcolor     = HASH , { S } ;
 *                (* There is a constraint on the color that it must
 *                   have either 3 or 6 hex-digits (i.e., [0-9a-fA-F])
 *                   after the "#"; e.g., "#000" is OK, but "#abcd" is not. *)
 *
 * </pre>
 * <p>
 * References:
 * <ul>
 * <li><a href="http://www.w3.org/TR/2014/CR-css-syntax-3/#parsing">
 * CSS Syntax Module Level 3, Chapter 5. Parsing</a></li>
 * <li><a href="https://www.w3.org/TR/CSS2/grammar.html#q25.0">
 * W3C CSS2, Appendix G.1 Grammar of CSS 2.1</a></li>
 * </ul>
 *
 * @author Werner Randelshofer
 */
public class CssParser {

    private List<ParseException> exceptions;

    public Stylesheet parseStylesheet(URL css) throws IOException {
        try (Reader in = new BufferedReader(new InputStreamReader(css.openConnection().getInputStream()))) {
            return parseStylesheet(in);
        }
    }

    public Stylesheet parseStylesheet(URI css) throws IOException {
        return parseStylesheet(css.toURL());
    }

    public Stylesheet parseStylesheet(String css) throws IOException {
        return parseStylesheet(new StringReader(css));
    }

    public Stylesheet parseStylesheet(Reader css) throws IOException {
        exceptions = new ArrayList<>();
        CssTokenizerInterface tt = new CssTokenizer(css);
        return parseStylesheet(tt);
    }

    /**
     * Parses a declaration list.
     *
     * @param css A stylesheet
     * @return the declaration list
     * @throws IOException if parsing fails
     */
    public List<Declaration> parseDeclarationList(String css) throws IOException {
        return CssParser.this.parseDeclarationList(new StringReader(css));
    }

    /**
     * Parses a declaration list.
     *
     * @param css A stylesheet
     * @return the declaration list
     * @throws IOException if parsing fails
     */
    public List<Declaration> parseDeclarationList(Reader css) throws IOException {
        exceptions = new ArrayList<>();
        CssTokenizerInterface tt = new CssTokenizer(css);
        try {
            return parseDeclarationList(tt);
        } catch (ParseException ex) {
            exceptions.add(ex);
        }
        return new ArrayList<>();
    }

    private Stylesheet parseStylesheet(CssTokenizerInterface tt) throws IOException {
        List<StyleRule> styleRules = new ArrayList<>();
        while (tt.nextToken() != CssTokenizerInterface.TT_EOF) {
            try {
                switch (tt.currentToken()) {
                    case CssTokenizerInterface.TT_S:
                    case CssTokenizerInterface.TT_CDC:
                    case CssTokenizerInterface.TT_CDO:
                        break;
                    case CssTokenizerInterface.TT_AT_KEYWORD: {
                        tt.pushBack();
                        AtRule r = parseAtRule(tt);
                        if (r != null) {
                            // FIXME don't throw at-rules away!
                            //   rulesets.add(r);
                        }
                        break;
                    }
                    default: {
                        tt.pushBack();
                        // FIXME parse qualified rules instead of style rule
                        StyleRule r = parseStyleRule(tt);
                        if (r != null) {
                            styleRules.add(r);
                        }
                        break;
                    }
                }
            } catch (ParseException e) {
                exceptions.add(e);
            }
        }
        return new Stylesheet(styleRules);
    }

    public List<ParseException> getParseExceptions() {
        return exceptions;
    }

    private void skipWhitespace(CssTokenizerInterface tt) throws IOException, ParseException {
        while (tt.currentToken() == CssTokenizerInterface.TT_S//
                || tt.currentToken() == CssTokenizerInterface.TT_CDC//
                || tt.currentToken() == CssTokenizerInterface.TT_CDO) {
            tt.nextToken();
        }
    }

    private AtRule parseAtRule(CssTokenizerInterface tt) throws IOException, ParseException {
        // FIXME implement this properly
        if (tt.nextToken() != CssTokenizerInterface.TT_AT_KEYWORD) {
            throw new ParseException("AtRule: At-Keyword expected.", tt.getLineNumber());
        }
        String atKeyword = tt.currentStringValue();
        tt.nextToken();
        skipWhitespace(tt);
        while (tt.currentToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != '{'//
                && tt.currentToken() != ';') {
            tt.pushBack();
            parseComponentValue(tt);
            tt.nextToken();
        }
        if (tt.currentToken() == ';') {
            return new AtRule(atKeyword, null, null);
        } else {
            tt.pushBack();
            parseCurlyBlock(tt);
            return new AtRule(atKeyword, null, null);
        }
    }

    private Object parseComponentValue(CssTokenizerInterface tt) throws IOException, ParseException {
        switch (tt.nextToken()) {
            case '{':
                tt.pushBack();
                parseCurlyBlock(tt);
                break;
            case '(':
                tt.pushBack();
                parseRoundBlock(tt);
                break;
            case '[':
                tt.pushBack();
                parseSquareBlock(tt);
                break;
            case CssTokenizerInterface.TT_FUNCTION:
                tt.pushBack();
                parseFunctionBlock(tt);
                break;
            default:
                tt.pushBack();
                parsePreservedToken(tt);
                break;
        }
        return null;
    }

    private Object parseCurlyBlock(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() != '{') {
            throw new ParseException("CurlyBlock: '{' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != '}') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt);
        }
        if (tt.currentToken() != '}') {
            throw new ParseException("CurlyBlock: '}' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseRoundBlock(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() != '(') {
            throw new ParseException("RoundBlock: '(' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != ')') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt);
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("RoundBlock: ')' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseSquareBlock(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() != '[') {
            throw new ParseException("SquareBlock: '[' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != ']') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt);
        }
        if (tt.nextToken() != ']') {
            throw new ParseException("SquareBlock: ']' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseFunctionBlock(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() != CssTokenizerInterface.TT_FUNCTION) {
            throw new ParseException("FunctionBlock: function expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        if (tt.nextToken() != ')') {
            throw new ParseException("FunctionBlock: ')' expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parsePreservedToken(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() == CssTokenizerInterface.TT_EOF) {
            throw new ParseException("PreservedToken: token expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        return null;
    }

    private Object parseQualifiedRule(CssTokenizerInterface tt) throws IOException, ParseException {
        // Fixme don't throw away a qualified rule
        tt.nextToken();
        skipWhitespace(tt);
        while (tt.currentToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != '{'//
                && tt.currentToken() != ';') {
            tt.pushBack();
            parseComponentValue(tt);
            tt.nextToken();
        }
        tt.pushBack();
        parseCurlyBlock(tt);
        return null;
    }

    private StyleRule parseStyleRule(CssTokenizerInterface tt) throws IOException, ParseException {
        SelectorGroup selectorGroup;
        tt.nextToken();
        skipWhitespace(tt);
        if (tt.currentToken() == '{') {
            tt.pushBack();
            selectorGroup = new SelectorGroup(new UniversalSelector());
        } else {
            tt.pushBack();
            selectorGroup = parseSelectorGroup(tt);
        }
        skipWhitespace(tt);
        if (tt.nextToken() != '{') {
            throw new ParseException("StyleRule: '{' expected.", tt.getLineNumber());
        }
        List<Declaration> declarations = parseDeclarationList(tt);
        tt.nextToken();
        skipWhitespace(tt);
        if (tt.currentToken() != '}') {
            throw new ParseException("StyleRule: '}' expected.", tt.getLineNumber());
        }
        return new StyleRule(selectorGroup, declarations);
    }

    private SelectorGroup parseSelectorGroup(CssTokenizerInterface tt) throws IOException, ParseException {
        List<Selector> selectors = new ArrayList<>();
        selectors.add(parseSelector(tt));
        while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != '{') {
            skipWhitespace(tt);
            if (tt.currentToken() != ',') {
                throw new ParseException("SelectorGroup: ',' expected.", tt.getLineNumber());
            }
            tt.nextToken();
            skipWhitespace(tt);
            tt.pushBack();
            selectors.add(parseSelector(tt));
        }
        tt.pushBack();
        return new SelectorGroup(selectors);
    }

    private Selector parseSelector(CssTokenizerInterface tt) throws IOException, ParseException {
        SimpleSelector simpleSelector = parseSimpleSelector(tt);
        Selector selector = simpleSelector;
        while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != '{' && tt.currentToken() != ',') {

            boolean potentialDescendantCombinator = false;
            if (tt.currentToken() == CssTokenizerInterface.TT_S) {
                potentialDescendantCombinator = true;
                skipWhitespace(tt);
            }
            if (tt.currentToken() == CssTokenizerInterface.TT_EOF
                    || tt.currentToken() == '{' || tt.currentToken() == ',') {
                break;
            }
            switch (tt.currentToken()) {
                case '>':
                    selector = new ChildCombinator(simpleSelector, parseSelector(tt));
                    break;
                case '+':
                    selector = new AdjacentSiblingCombinator(simpleSelector, parseSelector(tt));
                    break;
                case '~':
                    selector = new GeneralSiblingCombinator(simpleSelector, parseSelector(tt));
                    break;
                default:
                    tt.pushBack();
                    if (potentialDescendantCombinator) {
                        selector = new DescendantCombinator(simpleSelector, parseSelector(tt));
                    } else {
                        selector = new AndCombinator(simpleSelector, parseSelector(tt));
                    }
                    break;
            }
        }
        tt.pushBack();
        return selector;
    }

    private SimpleSelector parseSimpleSelector(CssTokenizerInterface tt) throws IOException, ParseException {
        tt.nextToken();
        skipWhitespace(tt);
        try {
            switch (tt.currentToken()) {
                case '*':
                    return new UniversalSelector();
                case CssTokenizerInterface.TT_IDENT:
                    return new TypeSelector(tt.currentStringValue());
                case CssTokenizerInterface.TT_HASH:
                    return new IdSelector(tt.currentStringValue());
                case '.':
                    if (tt.nextToken() != CssTokenizerInterface.TT_IDENT) {
                        throw new ParseException("SimpleSelector: identifier expected.", tt.getLineNumber());
                    }
                    return new ClassSelector(tt.currentStringValue());
                case ':':
                    tt.pushBack();
                    return parsePseudoClassSelector(tt);
                case '[':
                    tt.pushBack();
                    return parseAttributeSelector(tt);
                default:
                    throw new ParseException("SimpleSelector: SimpleSelector expected instead of \"" + tt.currentStringValue() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
            }
        } catch (ParseException e) {
            exceptions.add(e);
            return new SelectNothingSelector();
        }
    }

    private PseudoClassSelector parsePseudoClassSelector(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() != ':') {
            throw new ParseException("Pseudo Class Selector: ':' expected of \"" + tt.currentStringValue() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        if (tt.nextToken() != CssTokenizerInterface.TT_IDENT
                && tt.currentToken() != CssTokenizerInterface.TT_FUNCTION) {
            throw new ParseException("Pseudo Class Selector: identifier or function expected instead of \"" + tt.currentStringValue() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }

        if (tt.currentToken() == CssTokenizerInterface.TT_FUNCTION) {
            String ident = tt.currentStringValue();
            List<PreservedToken> terms = new ArrayList<>();
            while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                    && tt.currentToken() != ')') {
                terms.add(new PreservedToken(tt.currentToken(), tt.currentStringValue(), tt.currentNumericValue(), tt.getStartPosition(), tt.getEndPosition()));
            }
            return new FunctionPseudoClassSelector(ident, terms);
        } else {

            return new SimplePseudoClassSelector(tt.currentStringValue());
        }
    }

    private AbstractAttributeSelector parseAttributeSelector(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() != '[') {
            throw new ParseException("AttributeSelector: '[' expected.", tt.getLineNumber());
        }
        if (tt.nextToken() != CssTokenizerInterface.TT_IDENT) {
            throw new ParseException("AttributeSelector: Identifier expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        String attributeName = tt.currentStringValue();
        AbstractAttributeSelector selector;
        switch (tt.nextToken()) {
            case '=':
                if (tt.nextToken() != CssTokenizerInterface.TT_IDENT && tt.currentToken() != CssTokenizerInterface.TT_STRING) {
                    throw new ParseException("AttributeSelector: identifier or string expected.", tt.getLineNumber());
                }
                selector = new EqualsMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizerInterface.TT_INCLUDE_MATCH:
                if (tt.nextToken() != CssTokenizerInterface.TT_IDENT && tt.currentToken() != CssTokenizerInterface.TT_STRING) {
                    throw new ParseException("AttributeSelector: identifier or string expected.", tt.getLineNumber());
                }
                selector = new IncludeMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizerInterface.TT_DASH_MATCH:
                if (tt.nextToken() != CssTokenizerInterface.TT_IDENT && tt.currentToken() != CssTokenizerInterface.TT_STRING) {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());
                }
                selector = new DashMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizerInterface.TT_PREFIX_MATCH:
                if (tt.nextToken() != CssTokenizerInterface.TT_IDENT && tt.currentToken() != CssTokenizerInterface.TT_STRING) {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());
                }
                selector = new PrefixMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizerInterface.TT_SUFFIX_MATCH:
                if (tt.nextToken() != CssTokenizerInterface.TT_IDENT && tt.currentToken() != CssTokenizerInterface.TT_STRING) {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());
                }
                selector = new SuffixMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizerInterface.TT_SUBSTRING_MATCH:
                if (tt.nextToken() != CssTokenizerInterface.TT_IDENT && tt.currentToken() != CssTokenizerInterface.TT_STRING) {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());
                }
                selector = new SubstringMatchSelector(attributeName, tt.currentStringValue());
                break;
            case ']':
                selector = new ExistsMatchSelector(attributeName);
                tt.pushBack();
                break;
            default:
                throw new ParseException("AttributeSelector: operator expected. Line " + tt.getLineNumber() + ".", tt.getStartPosition());

        }
        tt.skipWhitespace();
        if (tt.nextToken() != ']') {
            throw new ParseException("AttributeSelector: ']' expected.", tt.getLineNumber());
        }
        return selector;
    }

    private List<Declaration> parseDeclarationList(CssTokenizerInterface tt) throws IOException, ParseException {
        List<Declaration> declarations = new ArrayList<>();

        while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                && tt.currentToken() != '}') {
            switch (tt.currentToken()) {
                case CssTokenizerInterface.TT_IDENT:
                    tt.pushBack();
                    try {
                        declarations.add(parseDeclaration(tt));
                    } catch (ParseException e) {
                        // We could not parse the current declaration.
                        // However we will try to parse the next declarations.
                        System.out.println("CssParser skipped " + e);
                        exceptions.add(e);
                    }
                    break;
                case ';':
                case CssTokenizerInterface.TT_S:
                    break;
                default:
                    throw new ParseException(//
                            "Declaration List: declaration or at-rule expected. Line"//
                            + tt.getLineNumber() + ".", //
                            tt.getStartPosition());

            }
        }

        tt.pushBack();
        return declarations;

    }

    private Declaration parseDeclaration(CssTokenizerInterface tt) throws IOException, ParseException {
        if (tt.nextToken() != CssTokenizerInterface.TT_IDENT) {
            throw new ParseException(//
                    "Declaration: property name expected. Line "//
                    + tt.getLineNumber() + ".",//
                    tt.getStartPosition());
        }
        String property = tt.currentStringValue();
        int startPos = tt.getStartPosition();
        tt.nextToken();
        skipWhitespace(tt);
        if (tt.currentToken() != ':') {
            throw new ParseException("Declaration: ':' expected instead of \"" + tt.currentStringValue() + "\". Line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }
        List<PreservedToken> terms = parseTerms(tt);
        int endPos = terms.isEmpty() ? tt.getStartPosition() : terms.get(terms.size() - 1).getEndPos();

        return new Declaration(property, terms, startPos, endPos);

    }

    private List<PreservedToken> parseTerms(CssTokenizerInterface tt) throws IOException, ParseException {
        List<PreservedToken> terms = new ArrayList<>();
        tt.nextToken();
        skipWhitespace(tt);
        tt.pushBack();
        while (tt.nextToken() != CssTokenizerInterface.TT_EOF
                && //
                tt.currentToken() != '}' && tt.currentToken() != ';') {
            switch (tt.currentToken()) {
                case CssTokenizerInterface.TT_CDC:
                case CssTokenizerInterface.TT_CDO:
                    break;
                case CssTokenizerInterface.TT_BAD_URI:
                    throw new ParseException("Terms: Bad URI in line " + tt.getLineNumber() + ".", tt.getStartPosition());
                case CssTokenizerInterface.TT_BAD_STRING:
                    throw new ParseException("Terms: Bad String in line " + tt.getLineNumber() + ".", tt.getStartPosition());
                default:
                    terms.add(new PreservedToken(tt.currentToken(), tt.currentStringValue(), tt.currentNumericValue(), tt.getStartPosition(), tt.getEndPosition()));
                    break;
            }
        }
        tt.pushBack();

        /*if (terms.isEmpty()) {
            throw new ParseException("Terms: Terms expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
        }*/
        return terms;
    }

    private PreservedToken parseTerm(CssTokenizerInterface tt) throws IOException, ParseException {
        switch (tt.nextToken()) {
            case CssTokenizerInterface.TT_EOF:
                throw new ParseException("Term: Term expected in line " + tt.getLineNumber() + ".", tt.getStartPosition());
            case CssTokenizerInterface.TT_BAD_URI:
                throw new ParseException("Term: Bad URI in line " + tt.getLineNumber() + ".", tt.getStartPosition());
            case CssTokenizerInterface.TT_BAD_STRING:
                throw new ParseException("Term: Bad String in line " + tt.getLineNumber() + ".", tt.getStartPosition());
            default:
                return new PreservedToken(tt.currentToken(), tt.currentStringValue(), tt.currentNumericValue(), tt.getStartPosition(), tt.getEndPosition());
        }
    }
}

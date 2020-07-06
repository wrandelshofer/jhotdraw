/*
 * @(#)CssParser.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.ast.AbstractAttributeSelector;
import org.jhotdraw8.css.ast.AdjacentSiblingCombinator;
import org.jhotdraw8.css.ast.AndCombinator;
import org.jhotdraw8.css.ast.AtRule;
import org.jhotdraw8.css.ast.ChildCombinator;
import org.jhotdraw8.css.ast.ClassSelector;
import org.jhotdraw8.css.ast.DashMatchSelector;
import org.jhotdraw8.css.ast.Declaration;
import org.jhotdraw8.css.ast.DescendantCombinator;
import org.jhotdraw8.css.ast.EqualsMatchSelector;
import org.jhotdraw8.css.ast.ExistsMatchSelector;
import org.jhotdraw8.css.ast.FunctionPseudoClassSelector;
import org.jhotdraw8.css.ast.GeneralSiblingCombinator;
import org.jhotdraw8.css.ast.IdSelector;
import org.jhotdraw8.css.ast.IncludeMatchSelector;
import org.jhotdraw8.css.ast.NegationPseudoClassSelector;
import org.jhotdraw8.css.ast.PrefixMatchSelector;
import org.jhotdraw8.css.ast.PseudoClassSelector;
import org.jhotdraw8.css.ast.Rule;
import org.jhotdraw8.css.ast.SelectNothingSelector;
import org.jhotdraw8.css.ast.Selector;
import org.jhotdraw8.css.ast.SelectorGroup;
import org.jhotdraw8.css.ast.SimplePseudoClassSelector;
import org.jhotdraw8.css.ast.SimpleSelector;
import org.jhotdraw8.css.ast.StyleRule;
import org.jhotdraw8.css.ast.Stylesheet;
import org.jhotdraw8.css.ast.SubstringMatchSelector;
import org.jhotdraw8.css.ast.SuffixMatchSelector;
import org.jhotdraw8.css.ast.TypeSelector;
import org.jhotdraw8.css.ast.UniversalSelector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code CssParser} processes a stream of characters into a
 * {@code Stylesheet} object.
 * <p>
 * The CSS Syntax Module Level 3 defines a grammar which is equivalent to the
 * following EBNF ISO/IEC 14977 productions:
 * <pre>
 * stylesheet_core = { S | CDO | CDC | qualified_rule | at_rule } ;
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
 *              = ROUND_BLOCK , { component_value } , ')' ;
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
 * type_selector        = ns_aware_ident ;
 * id_selector          = HASH ;
 * class_selector       = "." , IDENT ;
 * pseudoclass_selector = ":" , IDENT ;
 * attribute_selector   = "[" , ns_aware_ident
 *                            , [ ( "=" | "~=" | "|=" ) , ( IDENT | STRING ) ],
 *                        "]" ;
 * ns_aware_ident      = IDENT
 *                      | '*' , '|', IDENT
 *                      | IDENT , '|', IDENT
 *                      ;
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
 *                | hexcolor
 *                | function
 *                | bracketedTerms
 *                ) ;
 *
 * bracketedTerms = "{", {term} "}"
 *                | "[", {term} "]";
 *
 *
 * function     = ROUND_BLOCK , { S } , expr , ')' , { S } ;
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

    public final static String ALL_NAMESPACES_PREFIX = "*";
    public final static String DEFAULT_NAMESPACE_PREFIX = "|";
    private final Map<String, String> prefixToNamespaceMap = new LinkedHashMap<>();
    @NonNull
    private List<ParseException> exceptions = new ArrayList<>();

    @NonNull
    private FunctionPseudoClassSelector createFunctionPseudoClassSelector(@NonNull CssTokenizer tt) throws IOException, ParseException {
        tt.requireNextToken(CssTokenType.TT_FUNCTION, "FunctionPseudoClassSelector: Function expected");
        @NonNull final String ident = tt.currentStringNonNull();
        switch (ident) {
            case "not":
                final SimpleSelector simpleSelector = parseSimpleSelector(tt);
                tt.requireNextToken(')', ":not() Selector: ')' expected.");
                return new NegationPseudoClassSelector(ident, simpleSelector);
            default:
                Loop:
                while (tt.next() != CssTokenType.TT_EOF) {
                    switch (tt.current()) {
                        case ')':
                            tt.pushBack();
                            break Loop;
                        case '{':
                        case '}':
                            final ParseException ex = tt.createParseException(":" + ident + "() Selector ')' expected.");
                            tt.pushBack(); // so that we can resume parsing robustly
                            throw ex;
                        default:
                            break;
                    }
                }
                tt.requireNextToken(')', ":" + ident + "() Selector ')' expected.");
                return new FunctionPseudoClassSelector(ident);
        }
    }

    @NonNull
    public List<ParseException> getParseExceptions() {
        return exceptions;
    }

    /**
     * Some special at-rules contain information for the parser.
     */
    private void interpretAtRule(AtRule atRule) {
        if ("namespace".equals(atRule.getAtKeyword())) {
            ListCssTokenizer tt = new ListCssTokenizer(atRule.getHeader());
            final String prefix;
            if (tt.next() == CssTokenType.TT_IDENT) {
                prefix = tt.currentStringNonNull();
            } else {
                prefix = DEFAULT_NAMESPACE_PREFIX;
                tt.pushBack();
            }
            if (tt.next() == CssTokenType.TT_URL || tt.current() == CssTokenType.TT_STRING) {
                String namespace = tt.currentStringNonNull();
                prefixToNamespaceMap.put(prefix, namespace);
            }
        }
    }

    @NonNull
    private AtRule parseAtRule(@NonNull CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != CssTokenType.TT_AT_KEYWORD) {
            throw tt.createParseException("AtRule: At-Keyword expected.");
        }
        String atKeyword = tt.currentStringNonNull();
        tt.next();
        List<CssToken> header = new ArrayList<>();
        while (tt.current() != CssTokenType.TT_EOF
                && tt.current() != '{'//
                && tt.current() != ';') {
            tt.pushBack();
            parseComponentValue(tt, header);
            tt.nextNoSkip();
        }
        List<CssToken> body = new ArrayList<>();
        if (tt.current() == ';') {
            return new AtRule(atKeyword, header, body);
        } else {
            tt.pushBack();
            parseCurlyBlock(tt, body);
            body.remove(0);
            body.remove(body.size() - 1);
            return new AtRule(atKeyword, header, body);
        }
    }

    @NonNull
    private AbstractAttributeSelector parseAttributeSelector(@NonNull CssTokenizer tt) throws IOException, ParseException {
        tt.requireNextNoSkip('[', "AttributeSelector: '[' expected.");
        tt.requireNextNoSkip(CssTokenType.TT_IDENT, "AttributeSelector: Identifier expected.");

        String attributeNameOrPrefix = tt.currentStringNonNull();
        String attributeName;
        String namespacePrefix;
        if (tt.nextNoSkip() == CssTokenType.TT_VERTICAL_LINE) {
            namespacePrefix = attributeNameOrPrefix;
            tt.requireNextNoSkip(CssTokenType.TT_IDENT, "AttributeSelector: Identifier after " + namespacePrefix + "| expected.");
            attributeName = tt.currentStringNonNull();
        } else {
            tt.pushBack();
            namespacePrefix = null;
            attributeName = attributeNameOrPrefix;
        }
        String namespace = resolveNamespacePrefix(namespacePrefix);
        AbstractAttributeSelector selector;
        switch (tt.nextNoSkip()) {
            case '=':
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw tt.createParseException("AttributeSelector: identifier, string or number expected. Line:" + tt.getLineNumber() + ".");
                }
                selector = new EqualsMatchSelector(namespace, attributeName, tt.currentStringNonNull());
                break;
            case CssTokenType.TT_INCLUDE_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw tt.createParseException("AttributeSelector: identifier, string or number expected. Line:" + tt.getLineNumber() + ".");
                }
                selector = new IncludeMatchSelector(namespace, attributeName, tt.currentStringNonNull());
                break;
            case CssTokenType.TT_DASH_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw tt.createParseException("AttributeSelector: identifier, string or number expected. Line:" + tt.getLineNumber() + ".");
                }
                selector = new DashMatchSelector(namespace, attributeName, tt.currentStringNonNull());
                break;
            case CssTokenType.TT_PREFIX_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw tt.createParseException("AttributeSelector: identifier, string or number expected. Line:" + tt.getLineNumber() + ".");
                }
                selector = new PrefixMatchSelector(namespace, attributeName, tt.currentStringNonNull());
                break;
            case CssTokenType.TT_SUFFIX_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw tt.createParseException("AttributeSelector: identifier, string or number expected. Line:" + tt.getLineNumber() + ".");
                }
                selector = new SuffixMatchSelector(namespace, attributeName, tt.currentStringNonNull());
                break;
            case CssTokenType.TT_SUBSTRING_MATCH:
                if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                        && tt.current() != CssTokenType.TT_STRING
                        && tt.current() != CssTokenType.TT_NUMBER) {
                    throw tt.createParseException("AttributeSelector: identifier, string or number expected. Line:" + tt.getLineNumber() + ".");
                }
                selector = new SubstringMatchSelector(namespace, attributeName, tt.currentStringNonNull());
                break;
            case ']':
                selector = new ExistsMatchSelector(namespace, attributeName);
                tt.pushBack();
                break;
            default:
                throw tt.createParseException("AttributeSelector: operator expected. Line " + tt.getLineNumber() + ".");

        }
        if (tt.nextNoSkip() != ']') {
            throw tt.createParseException("AttributeSelector: ']' expected.");
        }
        return selector;
    }

    private void parseBracketedTerms(@NonNull CssTokenizer tt, @NonNull List<CssToken> terms, int endBracket) throws IOException, ParseException {
        terms.add(new CssToken(tt.current(), tt.currentString(), tt.currentNumber(),
                tt.getLineNumber(), tt.getStartPosition(), tt.getEndPosition()));
        tt.nextNoSkip();
        skipWhitespaceAndComments(tt);
        tt.pushBack();
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != endBracket) {
            switch (tt.current()) {
                case CssTokenType.TT_CDC:
                case CssTokenType.TT_CDO:
                    break;
                case CssTokenType.TT_BAD_URI:
                    throw tt.createParseException("BracketedTerms: Bad URI.");
                case CssTokenType.TT_BAD_STRING:
                    throw tt.createParseException("BracketedTerms: Bad String.");
                default:
                    terms.add(new CssToken(tt.current(), tt.currentString(), tt.currentNumber(),
                            tt.getLineNumber(), tt.getStartPosition(), tt.getEndPosition()));
                    break;
            }
        }
        terms.add(new CssToken(tt.current(), tt.currentString(), tt.currentNumber(),
                tt.getLineNumber(), tt.getStartPosition(), tt.getEndPosition()));
    }

    private void parseComponentValue(@NonNull CssTokenizer tt, @NonNull List<CssToken> preservedTokens) throws IOException, ParseException {
        switch (tt.nextNoSkip()) {
        case '{':
            tt.pushBack();
            parseCurlyBlock(tt, preservedTokens);
            break;
        case '(':
            tt.pushBack();
            parseRoundBlock(tt, preservedTokens);
            break;
        case '[':
                tt.pushBack();
                parseSquareBlock(tt, preservedTokens);
                break;
            case CssTokenType.TT_FUNCTION:
                tt.pushBack();
                parseFunctionBlock(tt, preservedTokens);
                break;
            default:
                tt.pushBack();
                parsePreservedToken(tt, preservedTokens);
                break;
        }
    }

    private void parseCurlyBlock(@NonNull CssTokenizer tt, @NonNull List<CssToken> preservedTokens) throws IOException, ParseException {
        if (tt.nextNoSkip() != '{') {
            throw tt.createParseException("CurlyBlock: '{' expected.");
        }
        preservedTokens.add(tt.getToken());
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != '}') {
            tt.pushBack();
            parseComponentValue(tt, preservedTokens);
        }
        if (tt.current() != '}') {
            throw tt.createParseException("CurlyBlock: '}' expected.");
        }
        preservedTokens.add(tt.getToken());
    }

    @NonNull
    private Declaration parseDeclaration(@NonNull CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != CssTokenType.TT_IDENT) {
            throw tt.createParseException("Declaration: property name expected.");
        }
        String property = tt.currentStringNonNull();
        int startPos = tt.getStartPosition();
        tt.nextNoSkip();
        skipWhitespaceAndComments(tt);
        if (tt.current() != ':') {
            throw tt.createParseException("Declaration: ':' expected.");
        }
        List<CssToken> terms = parseTerms(tt);
        int endPos = terms.isEmpty() ? tt.getStartPosition() : terms.get(terms.size() - 1).getEndPos();

        String namespacePrefix = null;
        return new Declaration(namespacePrefix, property, terms, startPos, endPos);

    }

    /**
     * Parses a declaration list.
     *
     * @param css A stylesheet
     * @return the declaration list
     * @throws IOException if parsing fails
     */
    @NonNull
    public List<Declaration> parseDeclarationList(@NonNull String css) throws IOException {
        return CssParser.this.parseDeclarationList(new StringReader(css));
    }

    /**
     * Parses a declaration list.
     *
     * @param css A stylesheet
     * @return the declaration list
     * @throws IOException if parsing fails
     */
    @NonNull
    public List<Declaration> parseDeclarationList(Reader css) throws IOException {
        exceptions = new ArrayList<>();
        CssTokenizer tt = new StreamCssTokenizer(css);
        try {
            return parseDeclarationList(tt);
        } catch (ParseException ex) {
            exceptions.add(ex);
        }
        return new ArrayList<>();
    }

    @NonNull
    private List<Declaration> parseDeclarationList(@NonNull CssTokenizer tt) throws IOException, ParseException {
        List<Declaration> declarations = new ArrayList<>();

        while (tt.next() != CssTokenType.TT_EOF
                && tt.current() != '}') {
            switch (tt.current()) {
                case CssTokenType.TT_IDENT:
                    tt.pushBack();
                    try {
                        declarations.add(parseDeclaration(tt));
                    } catch (ParseException e) {
                        // We could not parse the current declaration.
                        // However we will try to parse the next declarations.
                        exceptions.add(e);
                    }
                    break;
                case ';':
                    break;
                default:
                    throw tt.createParseException("Declaration List: declaration or at-rule expected.");

            }
        }

        tt.pushBack();
        return declarations;

    }

    private void parseFunctionBlock(@NonNull CssTokenizer tt, @NonNull List<CssToken> preservedTokens) throws IOException, ParseException {
        if (tt.nextNoSkip() != CssTokenType.TT_FUNCTION) {
            throw tt.createParseException("FunctionBlock: function expected.");
        }
        preservedTokens.add(tt.getToken());
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != ')') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt, preservedTokens);
        }
        if (tt.current() != ')') {
            throw tt.createParseException("FunctionBlock: ')' expected.");
        }
        preservedTokens.add(tt.getToken());
    }

    private void parsePreservedToken(@NonNull CssTokenizer tt, @NonNull List<CssToken> preservedTokens) throws IOException, ParseException {
        if (tt.nextNoSkip() == CssTokenType.TT_EOF) {
            throw tt.createParseException("CssToken: token expected.");
        }
        preservedTokens.add(tt.getToken());
    }

    @NonNull
    private PseudoClassSelector parsePseudoClassSelector(@NonNull CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextNoSkip() != ':') {
            throw tt.createParseException("Pseudo Class Selector: ':' expected of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".");
        }
        if (tt.nextNoSkip() != CssTokenType.TT_IDENT
                && tt.current() != CssTokenType.TT_FUNCTION) {
            throw tt.createParseException("Pseudo Class Selector: identifier or function expected instead of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".");
        }

        if (tt.current() == CssTokenType.TT_FUNCTION) {
            tt.pushBack();
            return createFunctionPseudoClassSelector(tt);
        } else {

            return new SimplePseudoClassSelector(tt.currentString());
        }
    }

    private void parseRoundBlock(@NonNull CssTokenizer tt, @NonNull List<CssToken> preservedTokens) throws IOException, ParseException {
        if (tt.nextNoSkip() != '(') {
            throw tt.createParseException("RoundBlock: '(' expected.");
        }
        preservedTokens.add(tt.getToken());
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != ')') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt, preservedTokens);
        }
        if (tt.current() != ')') {
            throw tt.createParseException("RoundBlock: ')' expected.");
        }
        preservedTokens.add(tt.getToken());
    }

    @NonNull
    private Selector parseSelector(@NonNull CssTokenizer tt) throws IOException, ParseException {
        SimpleSelector simpleSelector = parseSimpleSelector(tt);
        Selector selector = simpleSelector;
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != '{' && tt.current() != ',') {

            boolean potentialDescendantCombinator = false;
            if (tt.current() == CssTokenType.TT_S) {
                potentialDescendantCombinator = true;
                skipWhitespaceAndComments(tt);
            }
            if (tt.current() == CssTokenType.TT_EOF
                    || tt.current() == '{' || tt.current() == ',') {
                break;
            }
            switch (tt.current()) {
                case CssTokenType.TT_GREATER_THAN:
                    selector = new ChildCombinator(simpleSelector, parseSelector(tt));
                    break;
                case CssTokenType.TT_PLUS:
                    selector = new AdjacentSiblingCombinator(simpleSelector, parseSelector(tt));
                    break;
                case CssTokenType.TT_TILDE:
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

    @NonNull
    public SelectorGroup parseSelectorGroup(@NonNull CssTokenizer tt) throws IOException, ParseException {
        List<Selector> selectors = new ArrayList<>();
        selectors.add(parseSelector(tt));
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != '{') {
            skipWhitespaceAndComments(tt);
            if (tt.current() != ',') {
                throw tt.createParseException("SelectorGroup: ',' expected.");
            }
            tt.nextNoSkip();
            skipWhitespaceAndComments(tt);
            tt.pushBack();
            selectors.add(parseSelector(tt));
        }
        tt.pushBack();
        return new SelectorGroup(selectors);
    }

    @NonNull
    private SimpleSelector parseSimpleSelector(@NonNull CssTokenizer tt) throws IOException, ParseException {
        tt.nextNoSkip();
        skipWhitespaceAndComments(tt);

        try {
            switch (tt.current()) {
                case '*':
                    if (tt.nextNoSkip() == '|') {
                        tt.requireNextNoSkip(CssTokenType.TT_IDENT, "element name expected after *|");
                        return new TypeSelector(resolveNamespacePrefix(ALL_NAMESPACES_PREFIX), tt.currentStringNonNull());
                    } else {
                        tt.pushBack();
                        return new UniversalSelector();
                    }
                case CssTokenType.TT_IDENT:
                    String typeOrPrefix = tt.currentStringNonNull();
                    if (tt.nextNoSkip() == '|') {
                        tt.requireNextNoSkip(CssTokenType.TT_IDENT, "element name expected after " + typeOrPrefix + "|");
                        return new TypeSelector(resolveNamespacePrefix(typeOrPrefix), tt.currentStringNonNull());
                    } else {
                        tt.pushBack();
                        return new TypeSelector(resolveNamespacePrefix(null), typeOrPrefix);
                    }
                case CssTokenType.TT_HASH:
                    return new IdSelector(tt.currentString());
                case '.':
                    if (tt.nextNoSkip() != CssTokenType.TT_IDENT) {
                        throw tt.createParseException("SimpleSelector: identifier expected.");
                    }
                    return new ClassSelector(tt.currentString());
                case ':':
                    tt.pushBack();
                    return parsePseudoClassSelector(tt);
                case '[':
                    tt.pushBack();
                    return parseAttributeSelector(tt);
                case '{':
                    tt.pushBack();
                    throw tt.createParseException("SimpleSelector: SimpleSelector expected instead of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".");
                default:
                    // don't push back!
                    throw tt.createParseException("SimpleSelector: SimpleSelector expected instead of \"" + tt.currentString() + "\". Line " + tt.getLineNumber() + ".");
            }
        } catch (ParseException e) {
            exceptions.add(e);
            return new SelectNothingSelector();
        }
    }

    private void parseSquareBlock(@NonNull CssTokenizer tt, @NonNull List<CssToken> preservedTokens) throws IOException, ParseException {
        if (tt.nextNoSkip() != '[') {
            throw tt.createParseException("SquareBlock: '[' expected.");
        }
        preservedTokens.add(tt.getToken());
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && tt.current() != ']') {
            tt.pushBack();
            // FIXME do something with component value
            parseComponentValue(tt, preservedTokens);
        }
        if (tt.current() != ']') {
            throw tt.createParseException("SquareBlock: ']' expected.");
        }
        preservedTokens.add(tt.getToken());
    }

    @NonNull
    private StyleRule parseStyleRule(@NonNull CssTokenizer tt) throws IOException, ParseException {
        SelectorGroup selectorGroup;
        tt.nextNoSkip();
        skipWhitespaceAndComments(tt);
        if (tt.current() == '{') {
            tt.pushBack();
            selectorGroup = new SelectorGroup(new UniversalSelector());
        } else {
            tt.pushBack();
            selectorGroup = parseSelectorGroup(tt);
        }
        skipWhitespaceAndComments(tt);
        if (tt.nextNoSkip() != '{') {
            throw tt.createParseException("StyleRule: '{' expected.");
        }
        List<Declaration> declarations = parseDeclarationList(tt);
        tt.nextNoSkip();
        skipWhitespaceAndComments(tt);
        if (tt.current() != '}') {
            throw tt.createParseException("StyleRule: '}' expected.");
        }
        return new StyleRule(selectorGroup, declarations);
    }

    @NonNull
    public Stylesheet parseStylesheet(@NonNull URL css) throws IOException {
        try (Reader in = new BufferedReader(new InputStreamReader(css.openConnection().getInputStream(), StandardCharsets.UTF_8))) {
            return parseStylesheet(in);
        }
    }

    @NonNull
    public Stylesheet parseStylesheet(@NonNull URI css) throws IOException {
        return parseStylesheet(css.toURL());
    }

    @NonNull
    public Stylesheet parseStylesheet(@NonNull String css) throws IOException {
        return parseStylesheet(new StringReader(css));
    }

    @NonNull
    public Stylesheet parseStylesheet(Reader css) throws IOException {
        exceptions = new ArrayList<>();
        CssTokenizer tt = new StreamCssTokenizer(css);
        return parseStylesheet(tt);
    }

    @NonNull
    public Stylesheet parseStylesheet(@NonNull CssTokenizer tt) throws IOException {
        List<Rule> rules = new ArrayList<>();
        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            try {
                switch (tt.current()) {
                    case CssTokenType.TT_S:
                    case CssTokenType.TT_CDC:
                    case CssTokenType.TT_CDO:
                    case CssTokenType.TT_COMMENT:
                        break;
                    case CssTokenType.TT_AT_KEYWORD: {
                        tt.pushBack();
                        AtRule r = parseAtRule(tt);
                            interpretAtRule(r);
                            rules.add(r);
                        break;
                    }
                    default: {
                        tt.pushBack();
                        // FIXME parse qualified rules instead of style rule
                        StyleRule r = parseStyleRule(tt);
                            rules.add(r);
                        break;
                    }
                }
            } catch (ParseException e) {
                exceptions.add(e);
            }
        }
        return new Stylesheet(rules);
    }

    @NonNull
    private List<CssToken> parseTerms(@NonNull CssTokenizer tt) throws IOException, ParseException {
        List<CssToken> terms = new ArrayList<>();
        tt.nextNoSkip();
        skipWhitespaceAndComments(tt);
        tt.pushBack();
        while (tt.nextNoSkip() != CssTokenType.TT_EOF
                && //
                tt.current() != CssTokenType.TT_RIGHT_CURLY_BRACKET && tt.current() != CssTokenType.TT_SEMICOLON) {
            switch (tt.current()) {
                case CssTokenType.TT_CDC:
                case CssTokenType.TT_CDO:
                    break;
                case CssTokenType.TT_BAD_URI:
                    throw tt.createParseException("Terms: Bad URI.");
                case CssTokenType.TT_BAD_STRING:
                    throw tt.createParseException("Terms: Bad String.");
                case CssTokenType.TT_LEFT_CURLY_BRACKET:
                    parseBracketedTerms(tt, terms, CssTokenType.TT_RIGHT_CURLY_BRACKET);
                    break;
                case CssTokenType.TT_LEFT_SQUARE_BRACKET:
                    parseBracketedTerms(tt, terms, CssTokenType.TT_RIGHT_SQUARE_BRACKET);
                    break;
                default:
                    terms.add(new CssToken(tt.current(), tt.currentString(), tt.currentNumber(),
                            tt.getLineNumber(), tt.getStartPosition(), tt.getEndPosition()));
                    break;
            }
        }
        tt.pushBack();
        return terms;
    }

    /**
     * Resolves the namespace prefix.
     *
     * @param namespacePrefix a namespace prefix, null is treated as the {@link #DEFAULT_NAMESPACE_PREFIX}.
     * @return a namespace URL or the special value '*' or the prefix, the namespace can be null
     */
    @Nullable
    private String resolveNamespacePrefix(@Nullable String namespacePrefix) {
        if (namespacePrefix == null) {
            return prefixToNamespaceMap.get(DEFAULT_NAMESPACE_PREFIX);
        } else if (ALL_NAMESPACES_PREFIX.equals(namespacePrefix)) {
            return null;
        } else {
            return prefixToNamespaceMap.get(namespacePrefix);
        }
    }

    private void skipWhitespaceAndComments(@NonNull CssTokenizer tt) throws IOException {
        while (tt.current() == CssTokenType.TT_S//
                || tt.current() == CssTokenType.TT_CDC//
                || tt.current() == CssTokenType.TT_CDO
                || tt.current() == CssTokenType.TT_COMMENT
                || tt.current() == CssTokenType.TT_BAD_COMMENT) {
            tt.nextNoSkip();
        }
    }
}

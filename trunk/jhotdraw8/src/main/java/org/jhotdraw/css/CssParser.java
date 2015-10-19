/* @(#)CssParser.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.jhotdraw.css.ast.AbstractAttributeSelector;
import org.jhotdraw.css.ast.AdjacentSiblingCombinator;
import org.jhotdraw.css.ast.AndCombinator;
import org.jhotdraw.css.ast.ExistsMatchSelector;
import org.jhotdraw.css.ast.EqualsMatchSelector;
import org.jhotdraw.css.ast.ChildCombinator;
import org.jhotdraw.css.ast.ClassSelector;
import org.jhotdraw.css.ast.DashMatchSelector;
import org.jhotdraw.css.ast.Declaration;
import org.jhotdraw.css.ast.DescendantCombinator;
import org.jhotdraw.css.ast.GeneralSiblingCombinator;
import org.jhotdraw.css.ast.IdSelector;
import org.jhotdraw.css.ast.IncludeMatchSelector;
import org.jhotdraw.css.ast.PrefixMatchSelector;
import org.jhotdraw.css.ast.PseudoClassSelector;
import org.jhotdraw.css.ast.Ruleset;
import org.jhotdraw.css.ast.Selector;
import org.jhotdraw.css.ast.SelectorGroup;
import org.jhotdraw.css.ast.SimpleSelector;
import org.jhotdraw.css.ast.Stylesheet;
import org.jhotdraw.css.ast.SubstringMatchSelector;
import org.jhotdraw.css.ast.SuffixMatchSelector;
import org.jhotdraw.css.ast.TypeSelector;
import org.jhotdraw.css.ast.UniversalSelector;

/**
 * The {@code CssParser} processes a stream of characters into a
 * {@code Stylesheet} object.
 * <p>
 * The parser processes the following EBNF ISO/IEC 14977 grammar:
 * <pre>
 * stylesheet   = { S | CDO | CDC } ,
 *                { import , { CDO , { S } | CDC , { S } } } ,
 *                { { ruleset | media | page } , { CDO , { S } | CDC , { S } } }
 *                ;
 *
 * import       = IMPORT_SYM , { S } ,
 *                ( STRING | URI ) , { S } , [ media_list ] , ';' , { S } ;
 *
 * media        = MEDIA_SYM , { S } , media_list ,
 *                '{' , { S } , { ruleset } , '}' , { S } ;
 *
 * media_list   = medium , { COMMA , { S } , medium } ;
 *
 * medium       = IDENT , { S } ;
 *
 * page         = PAGE_SYM , { S } , { pseudo_page } ,
 *                '{' , { S } , declarations, '}',  { S } ;
 *
 * declarations = [ declaration ] , { ';' , { S }  [ declaration ] } ;
 *
 * pseudo_page  = ':' , IDENT , { S } ;
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
 * ruleset      = [ selector_group ] , "{" , declarations , "}" ;
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
 * declaration  = property, ":", { S } , expr , [ prio ] ;
 * expr         = term , { [ operator ] , term } ;
 * term         = [ unary_operator] ,
 *                ( NUMBER , { S } | PERCENTAGE , { S }  | LENGTH , { S }
 *                | EMS , { S } | EXS , { S } | ANGLE , { S } | TIME , { S }
 *                | FREQ , { S } | STRING , { S } | IDENT , { S } | URI , { S }
 *                | hexcolor | function
 *                ) ;
 *
 * function     = FUNCTION , { S } , expr , ')' , { S } ;
 *
 * hexcolor     = HASH , { S } ;
 *                (* There is a constraint on the color that it must
 *                   have either 3 or 6 hex-digits (i.e., [0-9a-fA-F])
 *                   after the "#"; e.g., "#000" is OK, but "#abcd" is not. *)
 * </pre> References:
 * <ul>
 * <li><a href="http://www.w3.org/TR/2014/CR-css-syntax-3-20140220/">CSS Syntax
 * Module Level 3, Chapter 5. Parsing</a></li>
 * <li><a href="http://www.w3.org/TR/2011/REC-CSS2-20110607">W3C CSS2, Appendix
 * G.1 Grammar of CSS 2.1</a></li>
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

    public Stylesheet parseStylesheet(String css) throws IOException {
        return parseStylesheet(new StringReader(css));
    }

    public Stylesheet parseStylesheet(Reader css) throws IOException {
        CssTokenizer tt = new CssTokenizer(css);
        return parseStylesheet(tt);
    }
    public List<Declaration> parseDeclarations(String css) throws IOException {
        return parseDeclarations(new StringReader(css));
    }

    public List<Declaration> parseDeclarations(Reader css) throws IOException {
        exceptions = new ArrayList<>();
        CssTokenizer tt = new CssTokenizer(css);
        try {
            return parseDeclarations(tt);
        } catch (ParseException ex) {
             exceptions.add(ex);
        }
        return new ArrayList<>();
    }


    private Stylesheet parseStylesheet(CssTokenizer tt) throws IOException {
        exceptions = new ArrayList<>();
        List<Ruleset> rulesets = new ArrayList<>();
        while (tt.nextToken() != CssTokenizer.TT_EOF) {
            try {
                switch (tt.currentToken()) {
                    case CssTokenizer.TT_S:
                    case CssTokenizer.TT_CDC:
                    case CssTokenizer.TT_CDO:
                        break;
                    default:
                        tt.pushBack();
                        Ruleset r = parseRuleset(tt);
                        if (r != null) {
                            rulesets.add(r);
                        }
                        break;
                }
            } catch (ParseException e) {
                exceptions.add(e);
            }
        }
        return new Stylesheet(rulesets);
    }

    public List<ParseException> getParseExceptions() {
        return exceptions;
    }

    private void skipWhitespace(CssTokenizer tt) throws IOException, ParseException {
        while (tt.currentToken() == CssTokenizer.TT_S//
                || tt.currentToken() == CssTokenizer.TT_CDC//
                || tt.currentToken() == CssTokenizer.TT_CDO) {
            tt.nextToken();
        }
    }

    private Ruleset parseRuleset(CssTokenizer tt) throws IOException, ParseException {
        SelectorGroup selectorGroup;
        skipWhitespace(tt);
        if (tt.nextToken() == '{') {
            tt.pushBack();
            selectorGroup = new SelectorGroup(new UniversalSelector());
        } else {
            tt.pushBack();
            selectorGroup = parseSelectorGroup(tt);
        }
        skipWhitespace(tt);
        if (tt.nextToken() != '{') {
            throw new ParseException("Ruleset: '{' expected.", tt.getLineNumber());
        }
        List<Declaration> declarations = parseDeclarations(tt);
        if (tt.nextToken() != '}') {
            throw new ParseException("Ruleset: '}' expected.", tt.getLineNumber());
        }
        return new Ruleset(selectorGroup, declarations);
    }

    private SelectorGroup parseSelectorGroup(CssTokenizer tt) throws IOException, ParseException {
        List<Selector> selectors = new ArrayList<>();
        selectors.add(parseSelector(tt));
        while (tt.nextToken() != CssTokenizer.TT_EOF
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

    private Selector parseSelector(CssTokenizer tt) throws IOException, ParseException {
        SimpleSelector simpleSelector = parseSimpleSelector(tt);
        Selector selector = simpleSelector;
        while (tt.nextToken() != CssTokenizer.TT_EOF
                && tt.currentToken() != '{' && tt.currentToken() != ',') {

            boolean potentialDescendantCombinator = false;
            if (tt.currentToken() == CssTokenizer.TT_S) {
                potentialDescendantCombinator = true;
                skipWhitespace(tt);
            }
            if (tt.currentToken() == CssTokenizer.TT_EOF
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

    private SimpleSelector parseSimpleSelector(CssTokenizer tt) throws IOException,ParseException {
        switch (tt.nextToken()) {
            case '*':
                return new UniversalSelector();
            case CssTokenizer.TT_IDENT:
                return new TypeSelector(tt.currentStringValue());
            case CssTokenizer.TT_HASH:
                return new IdSelector(tt.currentStringValue());
            case '.':
                if (tt.nextToken() != CssTokenizer.TT_IDENT) {
                    throw new ParseException("SimpleSelector: identifier expected." ,tt.getLineNumber());
                }
                return new ClassSelector(tt.currentStringValue());
            case ':':
                if (tt.nextToken() != CssTokenizer.TT_IDENT) {
                    throw new ParseException("SimpleSelector: identifier expected. Line "+tt.getLineNumber()+".",tt.getPosition());
                }
                return new PseudoClassSelector(tt.currentStringValue());
            case '[':
                tt.pushBack();
                return parseAttributeSelector(tt);
            default:
                throw new ParseException("SimpleSelector: SimpleSelector expected instead of \""+tt.currentStringValue()+"\". Line "+tt.getLineNumber()+".",tt.getPosition());
        }
    }

    private AbstractAttributeSelector parseAttributeSelector(CssTokenizer tt) throws IOException,ParseException {
        if (tt.nextToken() != '[') {
            throw new ParseException("AttributeSelector: '[' expected.", tt.getLineNumber());
        }
        if (tt.nextToken() != CssTokenizer.TT_IDENT) {
            throw new ParseException("AttributeSelector: Identifier expected. Line "+tt.getLineNumber()+".",tt.getPosition());
        }
        String attributeName = tt.currentStringValue();
        AbstractAttributeSelector selector;
        switch (tt.nextToken()) {
            case '=':
                if (tt.nextToken() != CssTokenizer.TT_IDENT && tt.currentToken()!= CssTokenizer.TT_STRING) {
                    throw new ParseException("AttributeSelector: identifier or string expected."  , tt.getLineNumber());
                }
                selector = new EqualsMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizer.TT_INCLUDE_MATCH:
                if (tt.nextToken() != CssTokenizer.TT_IDENT && tt.currentToken() != '\'' && tt.currentToken() != '"') {
                    throw new ParseException("AttributeSelector: identifier or string expected."  , tt.getLineNumber());
                }
                selector = new IncludeMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizer.TT_DASH_MATCH:
                if (tt.nextToken() != CssTokenizer.TT_IDENT && tt.currentToken() != '\'' && tt.currentToken() != '"') {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line "+tt.getLineNumber()+".",tt.getPosition());
                }
                selector = new DashMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizer.TT_PREFIX_MATCH:
                if (tt.nextToken() != CssTokenizer.TT_IDENT && tt.currentToken() != '\'' && tt.currentToken() != '"') {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line "+tt.getLineNumber()+".",tt.getPosition());
                }
                selector = new PrefixMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizer.TT_SUFFIX_MATCH:
                if (tt.nextToken() != CssTokenizer.TT_IDENT && tt.currentToken() != '\'' && tt.currentToken() != '"') {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line "+tt.getLineNumber()+".",tt.getPosition());
                }
                selector = new SuffixMatchSelector(attributeName, tt.currentStringValue());
                break;
            case CssTokenizer.TT_SUBSTRING_MATCH:
                if (tt.nextToken() != CssTokenizer.TT_IDENT && tt.currentToken() != '\'' && tt.currentToken() != '"') {
                    throw new ParseException("AttributeSelector: identifier or string expected. Line "+tt.getLineNumber()+".",tt.getPosition());
                }
                selector = new SubstringMatchSelector(attributeName, tt.currentStringValue());
                break;
            case ']':
                selector = new ExistsMatchSelector(attributeName);
                tt.pushBack();
                break;
            default:
                throw new ParseException("AttributeSelector: operator expected. Line "+tt.getLineNumber()+".",tt.getPosition());

        }
        if (tt.nextToken() != ']') {
            throw new ParseException("AttributeSelector: ']' expected.", tt.getLineNumber());
        }
        return selector;
    }

    private List<Declaration> parseDeclarations(CssTokenizer tt) throws IOException, ParseException {
        List<Declaration> declarations = new ArrayList<>();

        while (tt.nextToken() != CssTokenizer.TT_EOF
                && tt.currentToken() != '}') {
            if (tt.currentToken() != ';') {
                if (tt.currentToken() == CssTokenizer.TT_IDENT) {
                    tt.pushBack();
                    declarations.add(parseDeclaration(tt));
                }
            }
        }

        tt.pushBack();
        return declarations;

    }

    private Declaration parseDeclaration(CssTokenizer tt) throws IOException, ParseException {
        if (tt.nextToken() != CssTokenizer.TT_IDENT) {
            throw new ParseException("Declaration: property name expected. Line "+tt.getLineNumber()+".",tt.getPosition());
        }
        String property = tt.currentStringValue();
        tt.nextToken();
        skipWhitespace(tt);
        if (tt.currentToken() != ':') {
            throw new ParseException("Declaration: ':' expected instead of \""+tt.currentStringValue()+"\". Line "+tt.getLineNumber()+".",tt.getPosition());
        }

        List<String> terms = parseTerms(tt);

        return new Declaration(property, terms);

    }

    private List<String> parseTerms(CssTokenizer tt) throws IOException, ParseException {
        // FIXME we do not properly parse the terms yet
        List<String> terms = new ArrayList<>();
        skipWhitespace(tt);
        while (tt.nextToken() != CssTokenizer.TT_EOF && tt.currentToken() != '}' && tt.currentToken() != ';') {
            switch (tt.currentToken()) {
                case CssTokenizer.TT_S:
                    break;
                default:
                        terms.add(tt.currentStringValue());
                    break;
            }
        }
        tt.pushBack();
        return terms;
    }

}

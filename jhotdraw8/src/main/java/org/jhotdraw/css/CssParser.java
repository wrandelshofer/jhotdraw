/* @(#)CssParser.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.css;

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
 * operator     = ( '/' , { S } | ',' { S } ) ;
 * 
 * combinator   = ( '+' , { S } | '&gt;' { S } ) ;
 * 
 * unary_operator
 *              = ( '-' | '+' ) ;
 * 
 * property     = IDENT , { S } ;
 *
 * ruleset      = selector_group , "{" , declarations , "}" ;
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
 * </pre>
 * References:
 * <ul>
 * <li><a href="http://www.w3.org/TR/2014/CR-css-syntax-3-20140220/">CSS Syntax
 * Module Level 3, Chapter 5. Parsing</a></li>
 * <li><a href="http://www.w3.org/TR/2011/REC-CSS2-20110607">W3C CSS2, 
 * Appendix G.1 Grammar of CSS 2.1</a></li>
 * </ul>
 * 
 * @author Werner Randelshofer
 */
public class CssParser {

}

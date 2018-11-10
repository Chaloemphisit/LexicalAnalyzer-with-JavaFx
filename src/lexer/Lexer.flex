package lexer;
import static lexer.Token.*;
%%
%public %class Lexer
%type Token

%{
public String lexeme;
StringBuffer string = new StringBuffer();
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent   = ( [^*] | \*+ [^/*] )*

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING

%%

/* Keyword */
<YYINITIAL> ( "if" | "then" | "else" | "endif" | "while" | "do" | "endwhile" | "print" | "newline" | "read" )    {lexeme = yytext(); return KEYWORD;}

<YYINITIAL>{

        /* Operator */
        ( "+" | "-" | "*" | "/" | "=" | ">" | ">=" | "<" | "<=" | "==" | "++" | "--" )    {lexeme = yytext(); return OPERATOR;}

        /* Punctuation */
        (";" | "(" | ")")  {lexeme = yytext(); return PUNCTUATION;}

        /* identifiers */ 
        {Identifier}                   { lexeme=yytext(); return IDENTIFIER; }

        /* literals */
        {DecIntegerLiteral}            { lexeme=yytext(); return INTEGER; }
        \" { string.setLength(0); yybegin(STRING); }

        /* comments */
        {Comment}                      { /* ignore */ }
     
        /* whitespace */
        {WhiteSpace}                   { /* ignore */ }
}

    <STRING> {
      \"                             { yybegin(YYINITIAL);  
                                       lexeme=string.toString();
                                       return STRINGS; }
      [^\n\r\"\\]+                   { string.append( yytext() ); }
      \\t                            { string.append('\t'); }
      \\n                            { string.append('\n'); }

      \\r                            { string.append('\r'); }
      \\\"                           { string.append('\"'); }
      \\                             { string.append('\\'); }
    }
 
. {return ERROR;}
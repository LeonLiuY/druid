grammar Druid;

druid: statement*;

statement: define | assign | extend | function | returnst | functionCallSt;

define : 'var' names+=ID (',' names+=ID)* ';';

assign : ID '=' expr ';';

extend: ID '<-' expr ';';

expr : (nil='nil' | ID | INT | STRING | array | hash | signal) #valueExpr | arr=expr '[' index=expr ']' #arrayCallExpr | functionCall #functionCallExpr | '-' expr #negExpr| '(' expr ')' #parenExpr| left=expr (op='*' | op='/') right=expr #opExpr| left=expr (op='+' | op='-') right=expr #opExpr;

functionCallSt: call=functionCall ';';
functionCall: ID '(' (args+=expr (',' args+=expr)*)? ')';
function : 'def' name=ID '(' (params+=ID (',' params+=ID)*)? ')' '{' statement* '}' ;
returnst : 'return' expr ';';

array : '[' (elements+=expr (',' elements+=expr)*)? ']';

hash : '{' (entries+=entry (',' entries+=entry)*)? '}';

signal : SID '(' (args+=expr (',' args+=expr)*)? ')';
entry: key=expr '->' value=expr;

SID : '@'ID;
ID: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

INT : ('0'..'9')+ ;

STRING : '"' ( ~( '"' | '\\' ) | '\\' . )* '"'  |  '\'' ( ~( '\'' | '\\' ) | '\\' . )* '\'';

WS : [ \t\r\n]+ -> skip ;

grammar Druid;

druid: statement*;

statement: define | assign | extend;

define : 'var' names+=ID (',' names+=ID)* ';';

assign : ID '=' expr ';';

extend: ID '<-' expr ';';

expr : '-' expr #negExpr| '(' expr ')' #parenExpr| left=expr (op='*' | op='/') right=expr #opExpr| left=expr (op='+' | op='-') right=expr #opExpr | (ID | INT) #valueExpr;


ID: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

INT : ('0'..'9')+ ;
WS : [ \t\r\n]+ -> skip ;

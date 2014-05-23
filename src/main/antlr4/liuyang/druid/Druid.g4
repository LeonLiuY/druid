grammar Druid;

druid: statement*;

statement: define | assign | extend;

define : 'var' ID ';';

assign : ID '=' value ';';

extend: ID '<-' ID ';';

value: ID | INT;

           
           
           
ID: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'_'|'0'..'9')*;

INT : ('0'..'9')+ ;
WS : [ \t\r\n]+ -> skip ;
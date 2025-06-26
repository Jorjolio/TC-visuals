grammar compilador;

// Regla principal del programa
program : declaration* main_function EOF ;

// Declaraciones de funciones (sin prototipos, definición completa)
declaration
    : function_declaration
    ;

function_declaration
    : type_specifier IDENTIFIER LPAREN parameter_list? RPAREN compound_statement
    ;

parameter_list
    : parameter (COMMA parameter)*
    ;

parameter
    : type_specifier IDENTIFIER
    ;

// Función main
main_function
    : type_specifier MAIN LPAREN RPAREN compound_statement
    ;

// Tipos de datos
type_specifier
    : INT
    | FLOAT
    | DOUBLE
    | CHAR
    | BOOL
    | VOID
    ;

// Declaración compuesta (bloque de código)
compound_statement
    : LBRACE statement_list? RBRACE
    ;

statement_list
    : statement+
    ;

// Declaraciones/Sentencias
statement
    : variable_declaration SEMICOLON                      # VariableDeclStmt
    | assignment_statement SEMICOLON                      # AssignmentStmt
    | if_statement                                        # IfStmt
    | for_statement                                       # ForStmt
    | while_statement                                     # WhileStmt
    | function_call SEMICOLON                            # FunctionCallStmt
    | return_statement SEMICOLON                         # ReturnStmt
    | compound_statement                                 # CompoundStmt
    | expression SEMICOLON                               # ExpressionStmt
    ;

// Declaración de variables
variable_declaration
    : type_specifier IDENTIFIER (ASSIGN expression)?
    ;

// Asignación
assignment_statement
    : IDENTIFIER ASSIGN expression
    ;

// Sentencia if
if_statement
    : IF LPAREN condition RPAREN statement (ELSE statement)?
    ;

// Bucle for
for_statement
    : FOR LPAREN for_init? SEMICOLON condition? SEMICOLON for_update? RPAREN statement
    ;

for_init
    : variable_declaration
    | assignment_statement
    ;

for_update
    : assignment_statement
    | IDENTIFIER INCREMENT
    | IDENTIFIER DECREMENT
    | INCREMENT IDENTIFIER
    | DECREMENT IDENTIFIER
    ;

// Bucle while
while_statement
    : WHILE LPAREN condition RPAREN statement
    ;

// Llamada a función
function_call
    : IDENTIFIER LPAREN argument_list? RPAREN
    ;

argument_list
    : expression (COMMA expression)*
    ;

// Sentencia return
return_statement
    : RETURN expression?
    ;

// Condiciones
condition
    : expression relational_operator expression           # RelationalCondition
    | expression logical_operator expression              # LogicalCondition
    | LPAREN condition RPAREN                            # ParenCondition
    | TRUE                                               # TrueCondition
    | FALSE                                              # FalseCondition
    | expression                                         # ExpressionCondition
    ;

// Operadores relacionales
relational_operator
    : EQUAL                                              # EqualOp
    | NOT_EQUAL                                          # NotEqualOp
    | LESS_THAN                                          # LessOp
    | LESS_EQUAL                                         # LessEqualOp
    | GREATER_THAN                                       # GreaterOp
    | GREATER_EQUAL                                      # GreaterEqualOp
    ;

// Operadores lógicos
logical_operator
    : AND                                                # AndOp
    | OR                                                 # OrOp
    ;

// Expresiones
expression
    : expression PLUS term                               # AddExpression
    | expression MINUS term                              # SubExpression
    | term                                               # TermExpression
    ;

term
    : term MULTIPLY factor                               # MultTerm
    | term DIVIDE factor                                 # DivTerm
    | term MODULO factor                                 # ModTerm
    | factor                                             # FactorTerm
    ;

factor
    : IDENTIFIER                                         # IdFactor
    | NUMBER                                             # NumFactor
    | FLOAT_LITERAL                                      # FloatFactor
    | CHAR_LITERAL                                       # CharFactor
    | STRING_LITERAL                                     # StringFactor
    | function_call                                      # FunctionCallFactor
    | LPAREN expression RPAREN                           # ParenFactor
    | PLUS factor                                        # UnaryPlusFactor
    | MINUS factor                                       # UnaryMinusFactor
    | NOT factor                                         # NotFactor
    ;

// TOKENS (Léxico)

// Palabras reservadas
INT         : 'int' ;
FLOAT       : 'float' ;
DOUBLE      : 'double' ;
CHAR        : 'char' ;
BOOL        : 'bool' ;
VOID        : 'void' ;
MAIN        : 'main' ;
IF          : 'if' ;
ELSE        : 'else' ;
FOR         : 'for' ;
WHILE       : 'while' ;
RETURN      : 'return' ;
TRUE        : 'true' ;
FALSE       : 'false' ;

// Operadores
ASSIGN      : '=' ;
PLUS        : '+' ;
MINUS       : '-' ;
MULTIPLY    : '*' ;
DIVIDE      : '/' ;
MODULO      : '%' ;
INCREMENT   : '++' ;
DECREMENT   : '--' ;

// Operadores relacionales
EQUAL       : '==' ;
NOT_EQUAL   : '!=' ;
LESS_THAN   : '<' ;
LESS_EQUAL  : '<=' ;
GREATER_THAN: '>' ;
GREATER_EQUAL: '>=' ;

// Operadores lógicos
AND         : '&&' ;
OR          : '||' ;
NOT         : '!' ;

// Delimitadores
LPAREN      : '(' ;
RPAREN      : ')' ;
LBRACE      : '{' ;
RBRACE      : '}' ;
SEMICOLON   : ';' ;
COMMA       : ',' ;

// Literales
IDENTIFIER  : [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER      : [0-9]+ ;
FLOAT_LITERAL: [0-9]+ '.' [0-9]+ ;
CHAR_LITERAL: '\'' . '\'' ;
STRING_LITERAL: '"' (~["\r\n])* '"' ;

// Espacios en blanco
WS          : [ \t\r\n]+ -> skip ;

// Comentarios
LINE_COMMENT: '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
package prieba;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import prieba.SymbolTable.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Analizador Semántico que implementa el patrón Visitor
 * Realiza verificación de tipos, ámbitos y coherencia semántica
 */
public class SemanticAnalyzer extends compiladorBaseVisitor<DataType> {
    
    private SymbolTable symbolTable;
    private DataType currentFunctionReturnType;
    private boolean hasReturnStatement;
    private String sourceCode;
    private String[] sourceLines;
    
    public SemanticAnalyzer(String sourceCode) {
        this.symbolTable = new SymbolTable();
        this.currentFunctionReturnType = null;
        this.hasReturnStatement = false;
        this.sourceCode = sourceCode;
        this.sourceLines = sourceCode.split("\n");
    }
    
    // ========== PROGRAMA PRINCIPAL ==========
    
    @Override
    public DataType visitProgram(compiladorParser.ProgramContext ctx) {
        System.out.println(Colors.BLUE + "\n=== INICIANDO ANÁLISIS SEMÁNTICO ===" + Colors.RESET);
        
        // Visitar todas las declaraciones de funciones
        for (compiladorParser.DeclarationContext declCtx : ctx.declaration()) {
            visit(declCtx);
        }
        
        // Visitar función main
        visit(ctx.main_function());
        
        // Verificar que main existe
        FunctionSymbol mainFunction = symbolTable.lookupFunction("main");
        if (mainFunction == null) {
            addError("No se encontró la función 'main'", 1, 1, "main", SemanticError.ErrorType.UNDEFINED_FUNCTION);
        }
        
        // Verificar funciones declaradas pero no definidas
        checkUndefinedFunctions();
        
        return DataType.VOID;
    }
    
    // ========== DECLARACIONES ==========
    
    @Override
    public DataType visitFunction_declaration(compiladorParser.Function_declarationContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();
        DataType returnType = getDataType(ctx.type_specifier());
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        // Obtener parámetros
        List<String> paramNames = new ArrayList<>();
        List<DataType> paramTypes = new ArrayList<>();
        
        if (ctx.parameter_list() != null) {
            for (compiladorParser.ParameterContext paramCtx : ctx.parameter_list().parameter()) {
                String paramName = paramCtx.IDENTIFIER().getText();
                DataType paramType = getDataType(paramCtx.type_specifier());
                paramNames.add(paramName);
                paramTypes.add(paramType);
            }
        }
        
        // Declarar función en la tabla de símbolos
        boolean success = symbolTable.declareFunction(functionName, returnType, paramNames, paramTypes, line, column);
        
        if (success) {
            // Entrar al scope de la función
            symbolTable.enterScope("FUNCTION_" + functionName);
            currentFunctionReturnType = returnType;
            hasReturnStatement = false;
            
            // Declarar parámetros como variables locales
            for (int i = 0; i < paramNames.size(); i++) {
                symbolTable.declareVariable(paramNames.get(i), paramTypes.get(i), line, column, true);
            }
            
            // Visitar el cuerpo de la función
            visit(ctx.compound_statement());
            
            // Verificar return statement para funciones no-void
            if (returnType != DataType.VOID && !hasReturnStatement) {
                addWarning(String.format("Función '%s' puede no retornar un valor en todos los caminos", functionName),
                          line, column, functionName, SemanticWarning.WarningType.MISSING_RETURN);
            }
            
            // Salir del scope de la función
            symbolTable.exitScope();
            currentFunctionReturnType = null;
        }
        
        return returnType;
    }
    
    @Override
    public DataType visitMain_function(compiladorParser.Main_functionContext ctx) {
        String functionName = "main";
        DataType returnType = getDataType(ctx.type_specifier());
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        // Declarar función main
        List<String> paramNames = new ArrayList<>();
        List<DataType> paramTypes = new ArrayList<>();
        boolean success = symbolTable.declareFunction(functionName, returnType, paramNames, paramTypes, line, column);
        
        if (success) {
            // Entrar al scope de main
            symbolTable.enterScope("FUNCTION_main");
            currentFunctionReturnType = returnType;
            hasReturnStatement = false;
            
            // Visitar el cuerpo
            visit(ctx.compound_statement());
            
            // Main puede no tener return explícito
            if (returnType != DataType.VOID && !hasReturnStatement) {
                addWarning("Función 'main' puede no retornar un valor explícitamente",
                          line, column, functionName, SemanticWarning.WarningType.MISSING_RETURN);
            }
            
            // Salir del scope
            symbolTable.exitScope();
            currentFunctionReturnType = null;
        }
        
        return returnType;
    }
    
    @Override
    public DataType visitVariable_declaration(compiladorParser.Variable_declarationContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        DataType varType = getDataType(ctx.type_specifier());
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        // Declarar variable
        boolean success = symbolTable.declareVariable(varName, varType, line, column, false);
        
        if (success) {
            VariableSymbol variable = symbolTable.lookupVariable(varName);
            
            // Si hay inicialización, visitarla
            if (ctx.expression() != null) {
                DataType initType = visit(ctx.expression());
                
                // Verificar compatibilidad de tipos
                if (!varType.isCompatibleWith(initType)) {
                    if (initType.isCompatibleWith(varType)) {
                        // Conversión implícita
                        addWarning(String.format("Conversión implícita de %s a %s en inicialización de '%s'",
                                  initType, varType, varName),
                                  line, column, varName, SemanticWarning.WarningType.IMPLICIT_CONVERSION);
                    } else {
                        addError(String.format("Tipo incompatible en inicialización de '%s': esperado %s, encontrado %s",
                                varName, varType, initType),
                                line, column, varName, SemanticError.ErrorType.TYPE_MISMATCH);
                    }
                }
                
                // Marcar como inicializada
                if (variable != null) {
                    variable.setInitialized(true);
                }
            }
        }
        
        return varType;
    }
    
    // ========== SENTENCIAS ==========
    
    @Override
    public DataType visitAssignment_statement(compiladorParser.Assignment_statementContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        // Buscar la variable
        VariableSymbol variable = symbolTable.lookupVariable(varName);
        if (variable == null) {
            addError(String.format("Variable '%s' no está definida", varName),
                    line, column, varName, SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return DataType.ERROR;
        }
        
        // Marcar como usada e inicializada
        variable.setUsed(true);
        variable.setInitialized(true);
        
        // Evaluar expresión del lado derecho
        DataType exprType = visit(ctx.expression());
        DataType varType = variable.getType();
        
        // Verificar compatibilidad de tipos
        if (!varType.isCompatibleWith(exprType)) {
            if (exprType.isCompatibleWith(varType)) {
                // Conversión implícita
                addWarning(String.format("Conversión implícita de %s a %s en asignación a '%s'",
                          exprType, varType, varName),
                          line, column, varName, SemanticWarning.WarningType.IMPLICIT_CONVERSION);
            } else {
                addError(String.format("Tipo incompatible en asignación a '%s': esperado %s, encontrado %s",
                        varName, varType, exprType),
                        line, column, varName, SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        return varType;
    }
    
    @Override
    public DataType visitIf_statement(compiladorParser.If_statementContext ctx) {
        // Evaluar condición
        DataType conditionType = visit(ctx.condition());
        
        // Verificar que la condición sea lógica
        if (!conditionType.isLogical()) {
            addError("La condición del 'if' debe ser de tipo lógico",
                    ctx.condition().getStart().getLine(),
                    ctx.condition().getStart().getCharPositionInLine(),
                    "if condition", SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Visitar sentencia then
        visit(ctx.statement(0));
        
        // Visitar sentencia else si existe
        if (ctx.statement().size() > 1) {
            visit(ctx.statement(1));
        }
        
        return DataType.VOID;
    }
    
    @Override
    public DataType visitFor_statement(compiladorParser.For_statementContext ctx) {
        // Entrar a nuevo scope para variables del for
        symbolTable.enterScope("FOR_LOOP");
        
        // Visitar inicialización
        if (ctx.for_init() != null) {
            visit(ctx.for_init());
        }
        
        // Evaluar condición
        if (ctx.condition() != null) {
            DataType conditionType = visit(ctx.condition());
            if (!conditionType.isLogical()) {
                addError("La condición del 'for' debe ser de tipo lógico",
                        ctx.condition().getStart().getLine(),
                        ctx.condition().getStart().getCharPositionInLine(),
                        "for condition", SemanticError.ErrorType.TYPE_MISMATCH);
            }
        }
        
        // Visitar actualización
        if (ctx.for_update() != null) {
            visit(ctx.for_update());
        }
        
        // Visitar cuerpo del bucle
        visit(ctx.statement());
        
        // Salir del scope
        symbolTable.exitScope();
        
        return DataType.VOID;
    }
    
    @Override
    public DataType visitWhile_statement(compiladorParser.While_statementContext ctx) {
        // Evaluar condición
        DataType conditionType = visit(ctx.condition());
        
        // Verificar que la condición sea lógica
        if (!conditionType.isLogical()) {
            addError("La condición del 'while' debe ser de tipo lógico",
                    ctx.condition().getStart().getLine(),
                    ctx.condition().getStart().getCharPositionInLine(),
                    "while condition", SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        // Visitar cuerpo del bucle
        visit(ctx.statement());
        
        return DataType.VOID;
    }
    
    @Override
    public DataType visitReturn_statement(compiladorParser.Return_statementContext ctx) {
        hasReturnStatement = true;
        
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        // Verificar que estamos dentro de una función
        if (currentFunctionReturnType == null) {
            addError("Sentencia 'return' fuera de función",
                    line, column, "return", SemanticError.ErrorType.INVALID_OPERATION);
            return DataType.ERROR;
        }
        
        DataType returnType = DataType.VOID;
        
        // Si hay expresión de retorno
        if (ctx.expression() != null) {
            returnType = visit(ctx.expression());
        }
        
        // Verificar compatibilidad con el tipo de retorno de la función
        if (!currentFunctionReturnType.isCompatibleWith(returnType)) {
            if (returnType.isCompatibleWith(currentFunctionReturnType)) {
                // Conversión implícita
                addWarning(String.format("Conversión implícita de %s a %s en valor de retorno",
                          returnType, currentFunctionReturnType),
                          line, column, "return", SemanticWarning.WarningType.IMPLICIT_CONVERSION);
            } else {
                addError(String.format("Tipo de retorno incompatible: esperado %s, encontrado %s",
                        currentFunctionReturnType, returnType),
                        line, column, "return", SemanticError.ErrorType.RETURN_TYPE_MISMATCH);
            }
        }
        
        return returnType;
    }
    
    @Override
    public DataType visitFunction_call(compiladorParser.Function_callContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        // Obtener tipos de argumentos
        List<DataType> argTypes = new ArrayList<>();
        if (ctx.argument_list() != null) {
            for (compiladorParser.ExpressionContext exprCtx : ctx.argument_list().expression()) {
                DataType argType = visit(exprCtx);
                argTypes.add(argType);
            }
        }
        
        // Verificar la llamada
        return symbolTable.callFunction(functionName, argTypes, line, column);
    }
    
    @Override
    public DataType visitCompound_statement(compiladorParser.Compound_statementContext ctx) {
        // Entrar a nuevo scope para el bloque
        symbolTable.enterScope("BLOCK");
        
        // Visitar todas las sentencias del bloque
        if (ctx.statement_list() != null) {
            visit(ctx.statement_list());
        }
        
        // Salir del scope
        symbolTable.exitScope();
        
        return DataType.VOID;
    }
    
    // ========== EXPRESIONES ==========
    
    @Override
    public DataType visitAddExpression(compiladorParser.AddExpressionContext ctx) {
        DataType leftType = visit(ctx.expression());
        DataType rightType = visit(ctx.term());
        
        return checkArithmeticOperation(leftType, rightType, "+", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
    
    @Override
    public DataType visitSubExpression(compiladorParser.SubExpressionContext ctx) {
        DataType leftType = visit(ctx.expression());
        DataType rightType = visit(ctx.term());
        
        return checkArithmeticOperation(leftType, rightType, "-", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
    
    @Override
    public DataType visitMultTerm(compiladorParser.MultTermContext ctx) {
        DataType leftType = visit(ctx.term());
        DataType rightType = visit(ctx.factor());
        
        return checkArithmeticOperation(leftType, rightType, "*", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
    
    @Override
    public DataType visitDivTerm(compiladorParser.DivTermContext ctx) {
        DataType leftType = visit(ctx.term());
        DataType rightType = visit(ctx.factor());
        
        return checkArithmeticOperation(leftType, rightType, "/", ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }
    
    @Override
    public DataType visitModTerm(compiladorParser.ModTermContext ctx) {
        DataType leftType = visit(ctx.term());
        DataType rightType = visit(ctx.factor());
        
        // El módulo requiere tipos enteros
        if (leftType != DataType.INT || rightType != DataType.INT) {
            addError("El operador '%' requiere operandos de tipo entero",
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "modulo", SemanticError.ErrorType.TYPE_MISMATCH);
            return DataType.ERROR;
        }
        
        return DataType.INT;
    }
    
    @Override
    public DataType visitIdFactor(compiladorParser.IdFactorContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        
        return symbolTable.useVariable(varName, line, column);
    }
    
    @Override
    public DataType visitNumFactor(compiladorParser.NumFactorContext ctx) {
        return DataType.INT;
    }
    
    @Override
    public DataType visitFloatFactor(compiladorParser.FloatFactorContext ctx) {
        return DataType.FLOAT;
    }
    
    @Override
    public DataType visitCharFactor(compiladorParser.CharFactorContext ctx) {
        return DataType.CHAR;
    }
    
    @Override
    public DataType visitStringFactor(compiladorParser.StringFactorContext ctx) {
        return DataType.STRING;
    }
    
    @Override
    public DataType visitUnaryPlusFactor(compiladorParser.UnaryPlusFactorContext ctx) {
        DataType operandType = visit(ctx.factor());
        
        if (!operandType.isNumeric()) {
            addError("El operador unario '+' requiere un operando numérico",
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "unary+", SemanticError.ErrorType.TYPE_MISMATCH);
            return DataType.ERROR;
        }
        
        return operandType;
    }
    
    @Override
    public DataType visitUnaryMinusFactor(compiladorParser.UnaryMinusFactorContext ctx) {
        DataType operandType = visit(ctx.factor());
        
        if (!operandType.isNumeric()) {
            addError("El operador unario '-' requiere un operando numérico",
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "unary-", SemanticError.ErrorType.TYPE_MISMATCH);
            return DataType.ERROR;
        }
        
        return operandType;
    }
    
    @Override
    public DataType visitNotFactor(compiladorParser.NotFactorContext ctx) {
        DataType operandType = visit(ctx.factor());
        
        if (!operandType.isLogical()) {
            addError("El operador '!' requiere un operando lógico",
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "not", SemanticError.ErrorType.TYPE_MISMATCH);
            return DataType.ERROR;
        }
        
        return DataType.BOOL;
    }
    
    @Override
    public DataType visitParenFactor(compiladorParser.ParenFactorContext ctx) {
        return visit(ctx.expression());
    }
    
    @Override
    public DataType visitFunctionCallFactor(compiladorParser.FunctionCallFactorContext ctx) {
        return visit(ctx.function_call());
    }
    
    // ========== CONDICIONES ==========
    
    @Override
    public DataType visitRelationalCondition(compiladorParser.RelationalConditionContext ctx) {
        DataType leftType = visit(ctx.expression(0));
        DataType rightType = visit(ctx.expression(1));
        
        // Verificar que los tipos sean comparables
        if (!areComparable(leftType, rightType)) {
            addError(String.format("Tipos incomparables en operación relacional: %s y %s",
                    leftType, rightType),
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "relational", SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return DataType.BOOL;
    }
    
    @Override
    public DataType visitLogicalCondition(compiladorParser.LogicalConditionContext ctx) {
        DataType leftType = visit(ctx.expression(0));
        DataType rightType = visit(ctx.expression(1));
        
        // Verificar que ambos operandos sean lógicos
        if (!leftType.isLogical() || !rightType.isLogical()) {
            addError("Los operadores lógicos requieren operandos de tipo lógico",
                    ctx.getStart().getLine(),
                    ctx.getStart().getCharPositionInLine(),
                    "logical", SemanticError.ErrorType.TYPE_MISMATCH);
        }
        
        return DataType.BOOL;
    }
    
    @Override
    public DataType visitTrueCondition(compiladorParser.TrueConditionContext ctx) {
        return DataType.BOOL;
    }
    
    @Override
    public DataType visitFalseCondition(compiladorParser.FalseConditionContext ctx) {
        return DataType.BOOL;
    }
    
    @Override
    public DataType visitParenCondition(compiladorParser.ParenConditionContext ctx) {
        return visit(ctx.condition());
    }
    
    @Override
    public DataType visitExpressionCondition(compiladorParser.ExpressionConditionContext ctx) {
        DataType exprType = visit(ctx.expression());
        
        // En C, cualquier expresión puede ser usada como condición
        // 0 es falso, cualquier otro valor es verdadero
        if (!exprType.isLogical()) {
            addWarning("Usando expresión no-booleana como condición",
                      ctx.getStart().getLine(),
                      ctx.getStart().getCharPositionInLine(),
                      "condition", SemanticWarning.WarningType.IMPLICIT_CONVERSION);
        }
        
        return DataType.BOOL;
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private DataType getDataType(compiladorParser.Type_specifierContext ctx) {
        if (ctx.INT() != null) return DataType.INT;
        if (ctx.FLOAT() != null) return DataType.FLOAT;
        if (ctx.DOUBLE() != null) return DataType.DOUBLE;
        if (ctx.CHAR() != null) return DataType.CHAR;
        if (ctx.BOOL() != null) return DataType.BOOL;
        if (ctx.VOID() != null) return DataType.VOID;
        return DataType.UNKNOWN;
    }
    
    private DataType checkArithmeticOperation(DataType leftType, DataType rightType, String operator, int line, int column) {
        // Verificar que ambos operandos sean numéricos
        if (!leftType.isNumeric() || !rightType.isNumeric()) {
            addError(String.format("El operador '%s' requiere operandos numéricos: encontrado %s y %s",
                    operator, leftType, rightType),
                    line, column, operator, SemanticError.ErrorType.TYPE_MISMATCH);
            return DataType.ERROR;
        }
        
        return DataType.getArithmeticResultType(leftType, rightType);
    }
    
    private boolean areComparable(DataType type1, DataType type2) {
        // Los tipos numéricos son comparables entre sí
        if (type1.isNumeric() && type2.isNumeric()) return true;
        
        // Los tipos booleanos son comparables entre sí
        if (type1 == DataType.BOOL && type2 == DataType.BOOL) return true;
        
        // Los tipos iguales son comparables
        return type1 == type2;
    }
    
    private void checkUndefinedFunctions() {
        // Esta verificación se puede expandir más tarde para prototipos de funciones
        // Por ahora, todas las funciones deben estar definidas donde se declaran
    }
    
    private void addError(String message, int line, int column, String context, SemanticError.ErrorType type) {
        symbolTable.getErrors().add(new SemanticError(message, line, column, context, type));
    }
    
    private void addWarning(String message, int line, int column, String context, SemanticWarning.WarningType type) {
        symbolTable.getWarnings().add(new SemanticWarning(message, line, column, context, type));
    }
    
    // ========== GETTERS ==========
    
    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
    
    public List<SemanticError> getErrors() {
        return symbolTable.getErrors();
    }
    
    public List<SemanticWarning> getWarnings() {
        return symbolTable.getWarnings();
    }
    
    public boolean hasErrors() {
        return symbolTable.hasErrors();
    }
    
    public boolean hasWarnings() {
        return symbolTable.hasWarnings();
    }
}
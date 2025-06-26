package prieba;

import org.antlr.v4.runtime.tree.ParseTree;
import prieba.SymbolTable.*;
import java.util.*;
import java.io.*;

/**
 * Generador de código intermedio de tres direcciones
 * Implementa el patrón Visitor para recorrer el AST y generar código intermedio
 */
public class IntermediateCodeGenerator extends compiladorBaseVisitor<String> {
    
    private List<ThreeAddressCode> instructions;
    private SymbolTable symbolTable;
    private int tempCounter;
    private int labelCounter;
    private Stack<String> breakLabels;
    private Stack<String> continueLabels;
    private String currentFunction;
    private String sourceCode;
    
    public IntermediateCodeGenerator(SymbolTable symbolTable, String sourceCode) {
        this.instructions = new ArrayList<>();
        this.symbolTable = symbolTable;
        this.tempCounter = 0;
        this.labelCounter = 0;
        this.breakLabels = new Stack<>();
        this.continueLabels = new Stack<>();
        this.currentFunction = null;
        this.sourceCode = sourceCode;
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private String newTemp() {
        return "t" + (tempCounter++);
    }
    
    private String newLabel() {
        return "L" + (labelCounter++);
    }
    
    private void emit(String op, String arg1, String arg2, String result) {
        instructions.add(new ThreeAddressCode(op, arg1, arg2, result));
    }
    
    private void emit(String op, String arg1, String result) {
        emit(op, arg1, null, result);
    }
    
    private void emit(String op, String result) {
        emit(op, null, null, result);
    }
    
    // ========== PROGRAMA PRINCIPAL ==========
    
    @Override
    public String visitProgram(compiladorParser.ProgramContext ctx) {
        System.out.println(Colors.BLUE + "\n=== FASE 4: GENERACIÓN DE CÓDIGO INTERMEDIO ===" + Colors.RESET);
        System.out.println(Colors.BLUE + "=== INICIANDO GENERACIÓN DE CÓDIGO DE TRES DIRECCIONES ===" + Colors.RESET);
        
        // Generar código para todas las declaraciones de funciones
        for (compiladorParser.DeclarationContext declCtx : ctx.declaration()) {
            visit(declCtx);
        }
        
        // Generar código para la función main
        visit(ctx.main_function());
        
        // Mostrar el código generado
        printIntermediateCode();
        
        // Guardar en archivo
        saveIntermediateCodeToFile();
        
        System.out.println(Colors.GREEN + "\n✅ Generación de código intermedio completada exitosamente." + Colors.RESET);
        
        return null;
    }
    
    // ========== DECLARACIONES DE FUNCIONES ==========
    
    @Override
    public String visitFunction_declaration(compiladorParser.Function_declarationContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();
        currentFunction = functionName;
        
        // Etiqueta de inicio de función
        emit("FUNCTION", functionName);
        
        // Generar código para el cuerpo de la función
        visit(ctx.compound_statement());
        
        // Etiqueta de fin de función
        emit("END_FUNCTION", functionName);
        
        currentFunction = null;
        return functionName;
    }
    
    @Override
    public String visitMain_function(compiladorParser.Main_functionContext ctx) {
        currentFunction = "main";
        
        // Etiqueta de inicio de main
        emit("FUNCTION", "main");
        
        // Generar código para el cuerpo de main
        visit(ctx.compound_statement());
        
        // Etiqueta de fin de main
        emit("END_FUNCTION", "main");
        
        currentFunction = null;
        return "main";
    }
    
    // ========== SENTENCIAS ==========
    
    @Override
    public String visitVariableDeclStmt(compiladorParser.VariableDeclStmtContext ctx) {
        return visit(ctx.variable_declaration());
    }
    
    @Override
    public String visitVariable_declaration(compiladorParser.Variable_declarationContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        
        if (ctx.expression() != null) {
            // Declaración con inicialización
            String value = visit(ctx.expression());
            emit("=", value, varName);
        } else {
            // Declaración sin inicialización
            emit("DECLARE", varName);
        }
        
        return varName;
    }
    
    @Override
    public String visitAssignmentStmt(compiladorParser.AssignmentStmtContext ctx) {
        return visit(ctx.assignment_statement());
    }
    
    @Override
    public String visitAssignment_statement(compiladorParser.Assignment_statementContext ctx) {
        String varName = ctx.IDENTIFIER().getText();
        String value = visit(ctx.expression());
        emit("=", value, varName);
        return varName;
    }
    
    @Override
    public String visitReturnStmt(compiladorParser.ReturnStmtContext ctx) {
        return visit(ctx.return_statement());
    }
    
    @Override
    public String visitReturn_statement(compiladorParser.Return_statementContext ctx) {
        if (ctx.expression() != null) {
            String value = visit(ctx.expression());
            emit("RETURN", value);
        } else {
            emit("RETURN", (String) null);
        }
        return null;
    }
    
    @Override
    public String visitExpressionStmt(compiladorParser.ExpressionStmtContext ctx) {
        return visit(ctx.expression());
    }
    
    // ========== ESTRUCTURAS DE CONTROL ==========
    
    @Override
    public String visitIfStmt(compiladorParser.IfStmtContext ctx) {
        return visit(ctx.if_statement());
    }
    
    @Override
    public String visitIf_statement(compiladorParser.If_statementContext ctx) {
        String condition = visit(ctx.condition());
        String falseLabel = newLabel();
        String endLabel = newLabel();
        
        // Si la condición es falsa, saltar a falseLabel
        emit("IF_FALSE", condition, falseLabel);
        
        // Código del bloque then
        visit(ctx.statement(0));
        
        if (ctx.statement(1) != null) {
            // Hay bloque else
            emit("GOTO", endLabel);
            emit("LABEL", falseLabel);
            visit(ctx.statement(1));
            emit("LABEL", endLabel);
        } else {
            // No hay bloque else
            emit("LABEL", falseLabel);
        }
        
        return null;
    }
    
    @Override
    public String visitForStmt(compiladorParser.ForStmtContext ctx) {
        return visit(ctx.for_statement());
    }
    
    @Override
    public String visitFor_statement(compiladorParser.For_statementContext ctx) {
        String startLabel = newLabel();
        String continueLabel = newLabel();
        String endLabel = newLabel();
        
        // Apilar etiquetas para break y continue
        breakLabels.push(endLabel);
        continueLabels.push(continueLabel);
        
        // Inicialización
        if (ctx.for_init() != null) {
            visit(ctx.for_init());
        }
        
        // Etiqueta de inicio del bucle
        emit("LABEL", startLabel);
        
        // Condición
        if (ctx.condition() != null) {
            String condition = visit(ctx.condition());
            emit("IF_FALSE", condition, endLabel);
        }
        
        // Cuerpo del bucle
        visit(ctx.statement());
        
        // Etiqueta de continue
        emit("LABEL", continueLabel);
        
        // Actualización
        if (ctx.for_update() != null) {
            visit(ctx.for_update());
        }
        
        // Salto al inicio
        emit("GOTO", startLabel);
        
        // Etiqueta de fin
        emit("LABEL", endLabel);
        
        // Desapilar etiquetas
        breakLabels.pop();
        continueLabels.pop();
        
        return null;
    }
    
    @Override
    public String visitWhileStmt(compiladorParser.WhileStmtContext ctx) {
        return visit(ctx.while_statement());
    }
    
    @Override
    public String visitWhile_statement(compiladorParser.While_statementContext ctx) {
        String startLabel = newLabel();
        String endLabel = newLabel();
        
        // Apilar etiquetas para break y continue
        breakLabels.push(endLabel);
        continueLabels.push(startLabel);
        
        // Etiqueta de inicio del bucle
        emit("LABEL", startLabel);
        
        // Condición
        String condition = visit(ctx.condition());
        emit("IF_FALSE", condition, endLabel);
        
        // Cuerpo del bucle
        visit(ctx.statement());
        
        // Salto al inicio
        emit("GOTO", startLabel);
        
        // Etiqueta de fin
        emit("LABEL", endLabel);
        
        // Desapilar etiquetas
        breakLabels.pop();
        continueLabels.pop();
        
        return null;
    }
    
    // ========== MANEJO DE FOR_INIT Y FOR_UPDATE ==========
    
    @Override
    public String visitFor_init(compiladorParser.For_initContext ctx) {
        if (ctx.variable_declaration() != null) {
            return visit(ctx.variable_declaration());
        } else if (ctx.assignment_statement() != null) {
            return visit(ctx.assignment_statement());
        }
        return null;
    }
    
    @Override
    public String visitFor_update(compiladorParser.For_updateContext ctx) {
        if (ctx.assignment_statement() != null) {
            return visit(ctx.assignment_statement());
        } else if (ctx.IDENTIFIER() != null) {
            String varName = ctx.IDENTIFIER().getText();
            String temp = newTemp();
            
            if (ctx.INCREMENT() != null) {
                // Pre o post incremento
                emit("+", varName, "1", temp);
                emit("=", temp, varName);
            } else if (ctx.DECREMENT() != null) {
                // Pre o post decremento
                emit("-", varName, "1", temp);
                emit("=", temp, varName);
            }
            
            return varName;
        }
        return null;
    }
    
    // ========== MANEJO DE COMPOUND STATEMENTS ==========
    
    @Override
    public String visitCompoundStmt(compiladorParser.CompoundStmtContext ctx) {
        return visit(ctx.compound_statement());
    }
    
    @Override
    public String visitCompound_statement(compiladorParser.Compound_statementContext ctx) {
        if (ctx.statement_list() != null) {
            visit(ctx.statement_list());
        }
        return null;
    }
    
    @Override
    public String visitStatement_list(compiladorParser.Statement_listContext ctx) {
        for (compiladorParser.StatementContext stmtCtx : ctx.statement()) {
            visit(stmtCtx);
        }
        return null;
    }
    
    // ========== EXPRESIONES ==========
    
    @Override
    public String visitAddExpression(compiladorParser.AddExpressionContext ctx) {
        String left = visit(ctx.expression());
        String right = visit(ctx.term());
        String temp = newTemp();
        
        emit("+", left, right, temp);
        return temp;
    }
    
    @Override
    public String visitSubExpression(compiladorParser.SubExpressionContext ctx) {
        String left = visit(ctx.expression());
        String right = visit(ctx.term());
        String temp = newTemp();
        
        emit("-", left, right, temp);
        return temp;
    }
    
    @Override
    public String visitTermExpression(compiladorParser.TermExpressionContext ctx) {
        return visit(ctx.term());
    }
    
    @Override
    public String visitMultTerm(compiladorParser.MultTermContext ctx) {
        String left = visit(ctx.term());
        String right = visit(ctx.factor());
        String temp = newTemp();
        
        emit("*", left, right, temp);
        return temp;
    }
    
    @Override
    public String visitDivTerm(compiladorParser.DivTermContext ctx) {
        String left = visit(ctx.term());
        String right = visit(ctx.factor());
        String temp = newTemp();
        
        emit("/", left, right, temp);
        return temp;
    }
    
    @Override
    public String visitModTerm(compiladorParser.ModTermContext ctx) {
        String left = visit(ctx.term());
        String right = visit(ctx.factor());
        String temp = newTemp();
        
        emit("%", left, right, temp);
        return temp;
    }
    
    @Override
    public String visitFactorTerm(compiladorParser.FactorTermContext ctx) {
        return visit(ctx.factor());
    }
    
    // ========== FACTORES (CORREGIDOS SEGÚN LA GRAMÁTICA REAL) ==========
    
    @Override
    public String visitIdFactor(compiladorParser.IdFactorContext ctx) {
        return ctx.IDENTIFIER().getText();
    }
    
    @Override
    public String visitNumFactor(compiladorParser.NumFactorContext ctx) {
        return ctx.NUMBER().getText();
    }
    
    @Override
    public String visitFloatFactor(compiladorParser.FloatFactorContext ctx) {
        return ctx.FLOAT_LITERAL().getText();
    }
    
    @Override
    public String visitCharFactor(compiladorParser.CharFactorContext ctx) {
        return ctx.CHAR_LITERAL().getText();
    }
    
    @Override
    public String visitStringFactor(compiladorParser.StringFactorContext ctx) {
        return ctx.STRING_LITERAL().getText();
    }
    
    @Override
    public String visitFunctionCallFactor(compiladorParser.FunctionCallFactorContext ctx) {
        return visit(ctx.function_call());
    }
    
    @Override
    public String visitParenFactor(compiladorParser.ParenFactorContext ctx) {
        return visit(ctx.expression());
    }
    
    @Override
    public String visitUnaryPlusFactor(compiladorParser.UnaryPlusFactorContext ctx) {
        String operand = visit(ctx.factor());
        String temp = newTemp();
        emit("UNARY+", operand, temp);
        return temp;
    }
    
    @Override
    public String visitUnaryMinusFactor(compiladorParser.UnaryMinusFactorContext ctx) {
        String operand = visit(ctx.factor());
        String temp = newTemp();
        emit("UNARY-", operand, temp);
        return temp;
    }
    
    @Override
    public String visitNotFactor(compiladorParser.NotFactorContext ctx) {
        String operand = visit(ctx.factor());
        String temp = newTemp();
        emit("!", operand, temp);
        return temp;
    }
    
    // ========== CONDICIONES ==========
    
    @Override
    public String visitRelationalCondition(compiladorParser.RelationalConditionContext ctx) {
        String left = visit(ctx.expression(0));
        String right = visit(ctx.expression(1));
        String temp = newTemp();
        String operator = getRelationalOperator(ctx.relational_operator());
        
        emit(operator, left, right, temp);
        return temp;
    }
    
    @Override
    public String visitLogicalCondition(compiladorParser.LogicalConditionContext ctx) {
        String left = visit(ctx.expression(0));
        String right = visit(ctx.expression(1));
        String temp = newTemp();
        String operator = getLogicalOperator(ctx.logical_operator());
        
        emit(operator, left, right, temp);
        return temp;
    }
    
    @Override
    public String visitParenCondition(compiladorParser.ParenConditionContext ctx) {
        return visit(ctx.condition());
    }
    
    @Override
    public String visitTrueCondition(compiladorParser.TrueConditionContext ctx) {
        return "true";
    }
    
    @Override
    public String visitFalseCondition(compiladorParser.FalseConditionContext ctx) {
        return "false";
    }
    
    @Override
    public String visitExpressionCondition(compiladorParser.ExpressionConditionContext ctx) {
        return visit(ctx.expression());
    }
    
    // ========== OPERADORES ==========
    
    private String getRelationalOperator(compiladorParser.Relational_operatorContext ctx) {
        if (ctx.getToken(compiladorParser.EQUAL, 0) != null) return "==";
        if (ctx.getToken(compiladorParser.NOT_EQUAL, 0) != null) return "!=";
        if (ctx.getToken(compiladorParser.LESS_THAN, 0) != null) return "<";
        if (ctx.getToken(compiladorParser.LESS_EQUAL, 0) != null) return "<=";
        if (ctx.getToken(compiladorParser.GREATER_THAN, 0) != null) return ">";
        if (ctx.getToken(compiladorParser.GREATER_EQUAL, 0) != null) return ">=";
        return "==";
    }
    
    private String getLogicalOperator(compiladorParser.Logical_operatorContext ctx) {
        if (ctx.getToken(compiladorParser.AND, 0) != null) return "&&";
        if (ctx.getToken(compiladorParser.OR, 0) != null) return "||";
        return "&&";
    }
    
    // ========== LLAMADAS A FUNCIONES ==========
    
    @Override
    public String visitFunctionCallStmt(compiladorParser.FunctionCallStmtContext ctx) {
        return visit(ctx.function_call());
    }
    
    @Override
    public String visitFunction_call(compiladorParser.Function_callContext ctx) {
        String functionName = ctx.IDENTIFIER().getText();
        List<String> args = new ArrayList<>();
        
        // Evaluar argumentos
        if (ctx.argument_list() != null) {
            for (compiladorParser.ExpressionContext exprCtx : ctx.argument_list().expression()) {
                String arg = visit(exprCtx);
                args.add(arg);
                emit("PARAM", arg);
            }
        }
        
        // Llamada a función
        String temp = newTemp();
        emit("CALL", functionName, String.valueOf(args.size()), temp);
        
        return temp;
    }
    
    // ========== MÉTODOS DE SALIDA ==========
    
    /**
     * Imprime el código intermedio generado
     */
    public void printIntermediateCode() {
        System.out.println(Colors.BLUE + "\n📋 CÓDIGO INTERMEDIO DE TRES DIRECCIONES:" + Colors.RESET);
        System.out.println("─".repeat(60));
        
        for (int i = 0; i < instructions.size(); i++) {
            ThreeAddressCode instruction = instructions.get(i);
            System.out.println(String.format("%3d: %s", i + 1, instruction.toString()));
        }
        
        System.out.println("─".repeat(60));
        System.out.println(Colors.CYAN + "Total de instrucciones: " + instructions.size() + Colors.RESET);
    }
    
    /**
     * Guarda el código intermedio en un archivo
     */
    public void saveIntermediateCodeToFile() {
        try {
            String filename = "intermediate_code.txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println("=== CÓDIGO INTERMEDIO DE TRES DIRECCIONES ===");
                writer.println("Generado por: Compilador ANTLR4");
                writer.println("Fecha: " + new Date());
                writer.println();
                
                for (int i = 0; i < instructions.size(); i++) {
                    ThreeAddressCode instruction = instructions.get(i);
                    writer.println(String.format("%3d: %s", i + 1, instruction.toString()));
                }
                
                writer.println();
                writer.println("Total de instrucciones: " + instructions.size());
                writer.println("Variables temporales utilizadas: " + tempCounter);
                writer.println("Etiquetas generadas: " + labelCounter);
            }
            
            System.out.println(Colors.GREEN + "Código intermedio guardado en: " + filename + Colors.RESET);
            
        } catch (IOException e) {
            System.err.println(Colors.YELLOW + "Advertencia: No se pudo guardar el código intermedio: " + 
                e.getMessage() + Colors.RESET);
        }
    }
    
    // ========== GETTERS ==========
    
    public List<ThreeAddressCode> getInstructions() {
        return new ArrayList<>(instructions);
    }
    
    public int getInstructionCount() {
        return instructions.size();
    }
    
    public int getTempCount() {
        return tempCounter;
    }
    
    public int getLabelCount() {
        return labelCounter;
    }
}

/**
 * Clase para representar instrucciones de código de tres direcciones
 */
class ThreeAddressCode {
    private String operator;
    private String arg1;
    private String arg2;
    private String result;
    
    public ThreeAddressCode(String operator, String arg1, String arg2, String result) {
        this.operator = operator;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }
    
    @Override
    public String toString() {
        switch (operator) {
            case "=":
                return String.format("%s = %s", result, arg1);
            case "DECLARE":
                return String.format("DECLARE %s", arg1);
            case "FUNCTION":
                return String.format("FUNCTION %s:", arg1);
            case "END_FUNCTION":
                return String.format("END_FUNCTION %s", arg1);
            case "LABEL":
                return String.format("%s:", arg1);
            case "GOTO":
                return String.format("GOTO %s", arg1);
            case "IF_FALSE":
                return String.format("IF_FALSE %s GOTO %s", arg1, arg2);
            case "RETURN":
                return arg1 != null ? String.format("RETURN %s", arg1) : "RETURN";
            case "PARAM":
                return String.format("PARAM %s", arg1);
            case "CALL":
                return String.format("%s = CALL %s, %s", result, arg1, arg2);
            case "+":
            case "-":
            case "*":
            case "/":
            case "%":
            case "==":
            case "!=":
            case "<":
            case "<=":
            case ">":
            case ">=":
            case "&&":
            case "||":
                return String.format("%s = %s %s %s", result, arg1, operator, arg2);
            case "!":
            case "UNARY+":
            case "UNARY-":
                String op = operator.equals("UNARY+") ? "+" : (operator.equals("UNARY-") ? "-" : operator);
                return String.format("%s = %s%s", result, op, arg1);
            default:
                return String.format("%s %s %s %s", operator, 
                    arg1 != null ? arg1 : "", 
                    arg2 != null ? arg2 : "", 
                    result != null ? result : "").trim();
        }
    }
    
    // Getters
    public String getOperator() { return operator; }
    public String getArg1() { return arg1; }
    public String getArg2() { return arg2; }
    public String getResult() { return result; }
}
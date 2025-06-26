package prieba;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Clase principal del compilador que integra análisis léxico y sintáctico
 * con detección de errores
 */
public class CompilerMain {
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Uso: java CompilerMain <archivo_fuente>");
            System.exit(1);
        }
        
        String inputFile = args[0];
        CompilerMain compiler = new CompilerMain();
        compiler.compile(inputFile);
    }
    
    public void compile(String inputFile) {
        try {
            // Leer el archivo fuente
            String sourceCode = new String(Files.readAllBytes(Paths.get(inputFile)));
            System.out.println(Colors.BLUE + "=== INICIANDO COMPILACIÓN ===" + Colors.RESET);
            System.out.println("Archivo: " + inputFile);
            System.out.println("\nCódigo fuente:");
            System.out.println(Colors.CYAN + sourceCode + Colors.RESET);
            
            // Crear el flujo de entrada
            ANTLRInputStream input = new ANTLRInputStream(sourceCode);
            
            // 1. ANÁLISIS LÉXICO
            System.out.println("\n" + Colors.BLUE + "=== FASE 1: ANÁLISIS LÉXICO ===" + Colors.RESET);
            
            // Crear el lexer con manejo de errores personalizado
            compiladorLexer lexer = new compiladorLexer(input);
            CustomLexicalErrorListener lexicalErrorListener = new CustomLexicalErrorListener();
            lexer.removeErrorListeners(); // Remover listeners por defecto
            lexer.addErrorListener(lexicalErrorListener);
            
            // Generar tokens
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill(); // Forzar la tokenización completa
            
            // Mostrar tabla de tokens
            generateTokenTable(tokens);
            
            // Verificar si hubo errores léxicos
            if (lexicalErrorListener.hasErrors()) {
                System.out.println(Colors.RED + "\n❌ ERRORES LÉXICOS ENCONTRADOS:" + Colors.RESET);
                lexicalErrorListener.printErrors();
                System.out.println(Colors.RED + "\nCompilación terminada debido a errores léxicos." + Colors.RESET);
                return;
            } else {
                System.out.println(Colors.GREEN + "\n✅ Análisis léxico completado sin errores." + Colors.RESET);
            }
            
            // 2. ANÁLISIS SINTÁCTICO
            System.out.println("\n" + Colors.BLUE + "=== FASE 2: ANÁLISIS SINTÁCTICO ===" + Colors.RESET);
            
            // Resetear el stream de tokens para el parser
            tokens.seek(0);
            
            // Crear el parser con manejo de errores personalizado
            compiladorParser parser = new compiladorParser(tokens);
            CustomSyntacticErrorListener syntacticErrorListener = new CustomSyntacticErrorListener(sourceCode);
            parser.removeErrorListeners(); // Remover listeners por defecto
            parser.addErrorListener(syntacticErrorListener);
            
            // Parsear el programa
            ParseTree tree = parser.program();
            
            // Verificar si hubo errores sintácticos
            if (syntacticErrorListener.hasErrors()) {
                System.out.println(Colors.RED + "\n❌ ERRORES SINTÁCTICOS ENCONTRADOS:" + Colors.RESET);
                syntacticErrorListener.printErrors();
                System.out.println(Colors.RED + "\nCompilación terminada debido a errores sintácticos." + Colors.RESET);
                return;
            } else {
                System.out.println(Colors.GREEN + "\n✅ Análisis sintáctico completado sin errores." + Colors.RESET);
            }
            
            // Mostrar el árbol sintáctico
            System.out.println("\n" + Colors.BLUE + "=== ÁRBOL SINTÁCTICO ===" + Colors.RESET);
            showParseTree(tree, parser);
            
            // Generar archivo con el árbol sintáctico
            saveParseTreeToFile(tree, parser, inputFile);
            
            // 3. ANÁLISIS SEMÁNTICO
            System.out.println("\n" + Colors.BLUE + "=== FASE 3: ANÁLISIS SEMÁNTICO ===" + Colors.RESET);
            
            // Crear el analizador semántico
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(sourceCode);
            
            // Realizar análisis semántico
            semanticAnalyzer.visit(tree);
            
            // Crear manejador de errores semánticos
            SemanticErrorHandler errorHandler = new SemanticErrorHandler(sourceCode);
            errorHandler.processResults(semanticAnalyzer);
            
            // Mostrar tabla de símbolos
            System.out.println("\n" + Colors.BLUE + "=== TABLA DE SÍMBOLOS ===" + Colors.RESET);
            semanticAnalyzer.getSymbolTable().printSymbolTable();
            
            // Verificar si hubo errores semánticos
            if (errorHandler.hasErrors()) {
                System.out.println(Colors.RED + "\nCompilación terminada debido a errores semánticos." + Colors.RESET);
                return;
            } else {
                System.out.println(Colors.GREEN + "\n✅ Análisis semántico completado exitosamente." + Colors.RESET);
                if (errorHandler.hasWarnings()) {
                    System.out.println(Colors.YELLOW + "⚠️ Se encontraron " + errorHandler.getWarningCount() + " warning(s)." + Colors.RESET);
                }
            }
            
            System.out.println(Colors.GREEN + "\n🎉 ¡ANÁLISIS COMPLETO EXITOSO!" + Colors.RESET);
            System.out.println(Colors.BLUE + "Resumen: " + errorHandler.getSummary() + Colors.RESET);
            
        } catch (IOException e) {
            System.err.println(Colors.RED + "Error leyendo el archivo: " + e.getMessage() + Colors.RESET);
        } catch (Exception e) {
            System.err.println(Colors.RED + "Error durante la compilación: " + e.getMessage() + Colors.RESET);
            e.printStackTrace();
        }
    }
    
    /**
     * Genera y muestra la tabla de tokens
     */
    private void generateTokenTable(CommonTokenStream tokens) {
        List<Token> tokenList = tokens.getTokens();
        
        System.out.println("\n📋 TABLA DE TOKENS:");
        System.out.println(String.format("%-5s %-15s %-20s %-10s %-10s", 
            "Pos", "Tipo", "Texto", "Línea", "Columna"));
        System.out.println("─".repeat(65));
        
        compiladorLexer lexer = new compiladorLexer(null);
        String[] tokenNames = lexer.getTokenNames();
        
        for (int i = 0; i < tokenList.size(); i++) {
            Token token = tokenList.get(i);
            if (token.getType() == Token.EOF) break;
            
            String tokenType = token.getType() < tokenNames.length ? 
                tokenNames[token.getType()] : "UNKNOWN";
            String tokenText = token.getText();
            
            // Colores según el tipo de token
            String color = getTokenColor(tokenType);
            
            System.out.println(String.format("%-5d %-15s %s%-20s%s %-10d %-10d", 
                i, tokenType, color, tokenText, Colors.RESET, 
                token.getLine(), token.getCharPositionInLine()));
        }
    }
    
    /**
     * Obtiene el color apropiado para cada tipo de token
     */
    private String getTokenColor(String tokenType) {
        if (tokenType.contains("IDENTIFIER")) return Colors.CYAN;
        if (tokenType.contains("NUMBER") || tokenType.contains("FLOAT") || tokenType.contains("CHAR")) 
            return Colors.YELLOW;
        if (tokenType.contains("STRING")) return Colors.GREEN;
        if (tokenType.matches("'(int|float|double|char|bool|void|main|if|else|for|while|return|true|false)'")) 
            return Colors.PURPLE;
        return Colors.WHITE;
    }
    
    /**
     * Muestra el árbol sintáctico de forma indentada
     */
    private void showParseTree(ParseTree tree, Parser parser) {
        System.out.println(toStringTree(tree, parser, 0));
    }
    
    /**
     * Convierte el árbol sintáctico a string con indentación
     */
    private String toStringTree(ParseTree tree, Parser parser, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "  ".repeat(indent);
        
        if (tree instanceof TerminalNode) {
            TerminalNode terminal = (TerminalNode) tree;
            String text = terminal.getText();
            sb.append(indentStr).append(Colors.GREEN).append("'").append(text).append("'").append(Colors.RESET);
        } else {
            RuleContext ruleContext = (RuleContext) tree;
            String ruleName = parser.getRuleNames()[ruleContext.getRuleIndex()];
            sb.append(indentStr).append(Colors.BLUE).append(ruleName).append(Colors.RESET);
        }
        
        if (tree.getChildCount() > 0) {
            for (int i = 0; i < tree.getChildCount(); i++) {
                sb.append("\n").append(toStringTree(tree.getChild(i), parser, indent + 1));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Guarda el árbol sintáctico en un archivo
     */
    private void saveParseTreeToFile(ParseTree tree, Parser parser, String inputFile) {
        try {
            String outputFile = inputFile.replace(".txt", "_parse_tree.txt");
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                writer.println("=== ÁRBOL SINTÁCTICO ===");
                writer.println("Archivo fuente: " + inputFile);
                writer.println("Generado por: Compilador ANTLR4\n");
                writer.println(toStringTreePlain(tree, parser, 0));
            }
            System.out.println(Colors.GREEN + "Árbol sintáctico guardado en: " + outputFile + Colors.RESET);
        } catch (IOException e) {
            System.err.println(Colors.YELLOW + "Advertencia: No se pudo guardar el árbol sintáctico: " + 
                e.getMessage() + Colors.RESET);
        }
    }
    
    /**
     * Versión sin colores para archivo
     */
    private String toStringTreePlain(ParseTree tree, Parser parser, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "  ".repeat(indent);
        
        if (tree instanceof TerminalNode) {
            TerminalNode terminal = (TerminalNode) tree;
            String text = terminal.getText();
            sb.append(indentStr).append("'").append(text).append("'");
        } else {
            RuleContext ruleContext = (RuleContext) tree;
            String ruleName = parser.getRuleNames()[ruleContext.getRuleIndex()];
            sb.append(indentStr).append(ruleName);
        }
        
        if (tree.getChildCount() > 0) {
            for (int i = 0; i < tree.getChildCount(); i++) {
                sb.append("\n").append(toStringTreePlain(tree.getChild(i), parser, indent + 1));
            }
        }
        
        return sb.toString();
    }
}

/**
 * Clase para colores en la consola
 */
class Colors {
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String PURPLE = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";
}
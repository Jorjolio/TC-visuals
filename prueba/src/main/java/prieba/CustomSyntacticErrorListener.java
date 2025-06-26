package prieba;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * Listener personalizado para manejar errores sintácticos
 * Detecta y reporta errores de sintaxis con mensajes mejorados y contexto
 */
public class CustomSyntacticErrorListener extends BaseErrorListener {
    
    private List<SyntacticError> errors = new ArrayList<>();
    private String sourceCode;
    private String[] sourceLines;
    
    /**
     * Constructor que recibe el código fuente para mostrar contexto
     */
    public CustomSyntacticErrorListener(String sourceCode) {
        this.sourceCode = sourceCode;
        this.sourceLines = sourceCode.split("\n");
    }
    
    /**
     * Clase interna para representar un error sintáctico
     */
    public static class SyntacticError {
        private final int line;
        private final int charPositionInLine;
        private final String msg;
        private final RecognitionException exception;
        private final String context;
        private final String suggestion;
        
        public SyntacticError(int line, int charPositionInLine, String msg, 
                            RecognitionException exception, String context, String suggestion) {
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.msg = msg;
            this.exception = exception;
            this.context = context;
            this.suggestion = suggestion;
        }
        
        // Getters
        public int getLine() { return line; }
        public int getCharPositionInLine() { return charPositionInLine; }
        public String getMessage() { return msg; }
        public RecognitionException getException() { return exception; }
        public String getContext() { return context; }
        public String getSuggestion() { return suggestion; }
    }
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                           int line, int charPositionInLine, String msg, RecognitionException e) {
        
        String context = getLineContext(line);
        String enhancedMessage = enhanceErrorMessage(offendingSymbol, msg, e, recognizer);
        String suggestion = generateSuggestion(offendingSymbol, msg, e, recognizer);
        
        errors.add(new SyntacticError(line, charPositionInLine, enhancedMessage, e, context, suggestion));
    }
    
    /**
     * Mejora el mensaje de error sintáctico
     */
    private String enhanceErrorMessage(Object offendingSymbol, String originalMsg, 
                                     RecognitionException e, Recognizer<?, ?> recognizer) {
        
        String tokenText = offendingSymbol != null ? offendingSymbol.toString() : "EOF";
        
        // Casos específicos de errores sintácticos comunes
        
        // Punto y coma faltante
        if (originalMsg.contains("expecting ';'")) {
            return String.format("Falta punto y coma (;) después de '%s'", tokenText);
        }
        
        // Llave de apertura faltante
        if (originalMsg.contains("expecting '{'")) {
            return String.format("Se esperaba llave de apertura '{' después de '%s'", tokenText);
        }
        
        // Llave de cierre faltante
        if (originalMsg.contains("expecting '}'")) {
            return "Falta llave de cierre '}' para cerrar el bloque";
        }
        
        // Paréntesis faltantes
        if (originalMsg.contains("expecting '('")) {
            return String.format("Se esperaba paréntesis de apertura '(' después de '%s'", tokenText);
        }
        
        if (originalMsg.contains("expecting ')'")) {
            return "Falta paréntesis de cierre ')' para cerrar la expresión";
        }
        
        // Token inesperado
        if (originalMsg.contains("extraneous input")) {
            return String.format("Token inesperado: '%s'. Verifique la sintaxis en esta posición", tokenText);
        }
        
        // Entrada no válida
        if (originalMsg.contains("no viable alternative")) {
            return String.format("Sintaxis no válida cerca de '%s'. No se puede continuar el análisis", tokenText);
        }
        
        // Identificador esperado
        if (originalMsg.contains("expecting IDENTIFIER")) {
            return String.format("Se esperaba un identificador válido, pero se encontró '%s'", tokenText);
        }
        
        // Tipo de dato esperado
        if (originalMsg.contains("expecting") && 
            (originalMsg.contains("INT") || originalMsg.contains("FLOAT") || 
             originalMsg.contains("DOUBLE") || originalMsg.contains("CHAR") || 
             originalMsg.contains("VOID"))) {
            return String.format("Se esperaba un tipo de dato válido (int, float, double, char, void), pero se encontró '%s'", tokenText);
        }
        
        // Error de fin de archivo inesperado
        if (tokenText.equals("<EOF>") || tokenText.equals("EOF")) {
            return "Fin de archivo inesperado. Verifique que el código esté completo y todas las estructuras estén cerradas";
        }
        
        // Casos de palabras reservadas mal ubicadas
        if (isReservedKeyword(tokenText)) {
            return String.format("Palabra reservada '%s' en contexto incorrecto", tokenText);
        }
        
        // Mensaje por defecto mejorado
        return String.format("Error de sintaxis: %s en '%s'", originalMsg, tokenText);
    }
    
    /**
     * Genera sugerencias para corregir el error
     */
    private String generateSuggestion(Object offendingSymbol, String originalMsg, 
                                    RecognitionException e, Recognizer<?, ?> recognizer) {
        
        String tokenText = offendingSymbol != null ? offendingSymbol.toString() : "EOF";
        
        // Sugerencias específicas según el tipo de error
        if (originalMsg.contains("expecting ';'")) {
            return "Agregue un punto y coma (;) al final de la declaración o expresión";
        }
        
        if (originalMsg.contains("expecting '{'")) {
            return "Agregue una llave de apertura '{' para iniciar el bloque de código";
        }
        
        if (originalMsg.contains("expecting '}'")) {
            return "Agregue una llave de cierre '}' para terminar el bloque de código";
        }
        
        if (originalMsg.contains("expecting '('")) {
            return "Agregue un paréntesis de apertura '(' después de la función o estructura de control";
        }
        
        if (originalMsg.contains("expecting ')'")) {
            return "Agregue un paréntesis de cierre ')' para cerrar la expresión";
        }
        
        if (originalMsg.contains("expecting IDENTIFIER")) {
            return "Proporcione un nombre de variable o función válido (debe comenzar con letra o underscore)";
        }
        
        if (originalMsg.contains("extraneous input")) {
            return String.format("Elimine '%s' o verifique si falta algún operador o delimitador", tokenText);
        }
        
        if (originalMsg.contains("no viable alternative")) {
            return "Verifique la sintaxis del lenguaje. Posibles problemas: operadores incorrectos, palabras reservadas mal ubicadas, o estructura incompleta";
        }
        
        if (tokenText.equals("<EOF>") || tokenText.equals("EOF")) {
            return "Complete el código. Verifique que todas las funciones, bloques y expresiones estén terminadas correctamente";
        }
        
        return "Revise la gramática del lenguaje y corrija la sintaxis en esta posición";
    }
    
    /**
     * Obtiene el contexto de la línea donde ocurrió el error
     */
    private String getLineContext(int line) {
        if (line > 0 && line <= sourceLines.length) {
            return sourceLines[line - 1].trim();
        }
        return "";
    }
    
    /**
     * Verifica si un token es una palabra reservada
     */
    private boolean isReservedKeyword(String token) {
        Set<String> keywords = new HashSet<>();
        keywords.add("int");
        keywords.add("float");
        keywords.add("double");
        keywords.add("char");
        keywords.add("bool");
        keywords.add("void");
        keywords.add("main");
        keywords.add("if");
        keywords.add("else");
        keywords.add("for");
        keywords.add("while");
        keywords.add("return");
        keywords.add("true");
        keywords.add("false");
        
        return keywords.contains(token.toLowerCase());
    }
    
    /**
     * Verifica si hay errores sintácticos
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Obtiene la lista de errores
     */
    public List<SyntacticError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Imprime todos los errores sintácticos con formato y contexto
     */
    public void printErrors() {
        for (int i = 0; i < errors.size(); i++) {
            SyntacticError error = errors.get(i);
            
            System.out.println(String.format(
                "%s[Error Sintáctico %d]%s Línea %d, Columna %d:",
                Colors.RED,
                i + 1,
                Colors.RESET,
                error.getLine(),
                error.getCharPositionInLine()
            ));
            
            // Mensaje del error
            System.out.println(String.format(
                "  %s%s%s",
                Colors.YELLOW,
                error.getMessage(),
                Colors.RESET
            ));
            
            // Contexto de la línea
            if (!error.getContext().isEmpty()) {
                System.out.println(String.format(
                    "  %sContexto:%s %s",
                    Colors.CYAN,
                    Colors.RESET,
                    error.getContext()
                ));
                
                // Mostrar indicador de posición
                String pointer = " ".repeat(error.getCharPositionInLine()) + "^";
                System.out.println(String.format(
                    "  %s%s%s",
                    Colors.RED,
                    pointer,
                    Colors.RESET
                ));
            }
            
            // Sugerencia
            System.out.println(String.format(
                "  %s💡 Sugerencia:%s %s",
                Colors.GREEN,
                Colors.RESET,
                error.getSuggestion()
            ));
            
            System.out.println(); // Línea en blanco entre errores
        }
        
        // Resumen de errores
                System.out.println(String.format(
                    "%sTotal de errores sintácticos: %d%s",
                    Colors.RED,
                    errors.size(),
                    Colors.RESET
                ));
            }
        }
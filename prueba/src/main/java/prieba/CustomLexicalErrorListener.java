package prieba;

import org.antlr.v4.runtime.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Listener personalizado para manejar errores léxicos
 * Detecta y reporta caracteres no reconocidos y otros errores léxicos
 */
public class CustomLexicalErrorListener extends BaseErrorListener {
    
    private List<LexicalError> errors = new ArrayList<>();
    
    /**
     * Clase interna para representar un error léxico
     */
    public static class LexicalError {
        private final int line;
        private final int charPositionInLine;
        private final String msg;
        private final RecognitionException exception;
        
        public LexicalError(int line, int charPositionInLine, String msg, RecognitionException exception) {
            this.line = line;
            this.charPositionInLine = charPositionInLine;
            this.msg = msg;
            this.exception = exception;
        }
        
        public int getLine() { return line; }
        public int getCharPositionInLine() { return charPositionInLine; }
        public String getMessage() { return msg; }
        public RecognitionException getException() { return exception; }
    }
    
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                           int line, int charPositionInLine, String msg, RecognitionException e) {
        
        // Crear un error léxico más descriptivo
        String enhancedMessage = enhanceErrorMessage(offendingSymbol, msg, e);
        errors.add(new LexicalError(line, charPositionInLine, enhancedMessage, e));
    }
    
    /**
     * Mejora el mensaje de error para hacerlo más comprensible
     */
    private String enhanceErrorMessage(Object offendingSymbol, String originalMsg, RecognitionException e) {
        if (offendingSymbol instanceof String) {
            String symbol = (String) offendingSymbol;
            
            // Caracter no válido
            if (originalMsg.contains("token recognition error")) {
                return String.format("Caracter no válido: '%s'. No es reconocido por el lenguaje.", symbol);
            }
            
            // Casos específicos de errores comunes
            if (symbol.matches("[0-9]+[a-zA-Z]+")) {
                return String.format("Identificador inválido: '%s'. Los identificadores no pueden comenzar con números.", symbol);
            }
            
            if (symbol.contains("@") || symbol.contains("#") || symbol.contains("$")) {
                return String.format("Caracter especial no permitido: '%s'. El lenguaje no soporta este caracter.", symbol);
            }
            
            if (symbol.matches(".*[áéíóúñüÁÉÍÓÚÑÜ].*")) {
                return String.format("Caracter con acento encontrado: '%s'. Use caracteres ASCII estándar.", symbol);
            }
            
            // String no cerrado
            if (symbol.startsWith("\"") && !symbol.endsWith("\"") && symbol.length() > 1) {
                return String.format("String literal no cerrado: '%s'. Falta comilla de cierre.", symbol);
            }
            
            // Caracter literal no cerrado
            if (symbol.startsWith("'") && !symbol.endsWith("'") && symbol.length() > 1) {
                return String.format("Caracter literal no cerrado: '%s'. Falta comilla de cierre.", symbol);
            }
        }
        
        // Si no podemos mejorar el mensaje, devolver el original con formato
        return String.format("Error léxico: %s", originalMsg);
    }
    
    /**
     * Verifica si hay errores léxicos
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Obtiene la lista de errores
     */
    public List<LexicalError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Imprime todos los errores léxicos con formato
     */
    public void printErrors() {
        for (int i = 0; i < errors.size(); i++) {
            LexicalError error = errors.get(i);
            System.out.println(String.format(
                "%s[Error Léxico %d]%s Línea %d, Columna %d: %s%s%s",
                Colors.RED,
                i + 1,
                Colors.RESET,
                error.getLine(),
                error.getCharPositionInLine(),
                Colors.YELLOW,
                error.getMessage(),
                Colors.RESET
            ));
        }
        
        // Resumen de errores
        System.out.println(String.format(
            "\n%sTotal de errores léxicos: %d%s",
            Colors.RED,
            errors.size(),
            Colors.RESET
        ));
        
        // Sugerencias generales
        if (hasErrors()) {
            System.out.println(Colors.CYAN + "\n💡 Sugerencias:" + Colors.RESET);
            System.out.println("  • Verifique que todos los caracteres pertenezcan al alfabeto del lenguaje");
            System.out.println("  • Asegúrese de que las cadenas y caracteres literales estén correctamente cerrados");
            System.out.println("  • Los identificadores deben comenzar con letra o underscore");
            System.out.println("  • Use solo caracteres ASCII estándar");
        }
    }
    
    /**
     * Obtiene estadísticas de los errores
     */
    public void printStatistics() {
        if (errors.isEmpty()) {
            System.out.println(Colors.GREEN + "✅ No se encontraron errores léxicos" + Colors.RESET);
            return;
        }
        
        System.out.println(Colors.BLUE + "\n📊 ESTADÍSTICAS DE ERRORES LÉXICOS:" + Colors.RESET);
        
        // Contar tipos de errores
        int invalidCharacters = 0;
        int unrecognizedTokens = 0;
        int unclosedStrings = 0;
        int invalidIdentifiers = 0;
        int other = 0;
        
        for (LexicalError error : errors) {
            String msg = error.getMessage().toLowerCase();
            if (msg.contains("caracter no válido") || msg.contains("caracter especial")) {
                invalidCharacters++;
            } else if (msg.contains("no cerrado")) {
                unclosedStrings++;
            } else if (msg.contains("identificador inválido")) {
                invalidIdentifiers++;
            } else if (msg.contains("token recognition")) {
                unrecognizedTokens++;
            } else {
                other++;
            }
        }
        
        System.out.println("  • Caracteres inválidos: " + invalidCharacters);
        System.out.println("  • Strings/chars no cerrados: " + unclosedStrings);
        System.out.println("  • Identificadores inválidos: " + invalidIdentifiers);
        System.out.println("  • Tokens no reconocidos: " + unrecognizedTokens);
        System.out.println("  • Otros errores: " + other);
        System.out.println("  • Total: " + errors.size());
    }
    
    /**
     * Limpia la lista de errores
     */
    public void clearErrors() {
        errors.clear();
    }
}
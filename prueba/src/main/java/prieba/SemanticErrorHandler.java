package prieba;

import prieba.SymbolTable.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manejador especializado para errores y warnings semánticos
 * Proporciona reportes detallados y sugerencias de corrección
 */
public class SemanticErrorHandler {
    
    private String sourceCode;
    private String[] sourceLines;
    private List<SemanticError> errors;
    private List<SemanticWarning> warnings;
    
    public SemanticErrorHandler(String sourceCode) {
        this.sourceCode = sourceCode;
        this.sourceLines = sourceCode.split("\n");
    }
    
    /**
     * Procesa y reporta todos los errores y warnings semánticos
     */
    public void processResults(SemanticAnalyzer analyzer) {
        this.errors = analyzer.getErrors();
        this.warnings = analyzer.getWarnings();
        
        if (hasErrors()) {
            System.out.println(Colors.RED + "\n❌ ERRORES SEMÁNTICOS ENCONTRADOS:" + Colors.RESET);
            printErrors();
        } else {
            System.out.println(Colors.GREEN + "\n✅ Análisis semántico completado sin errores." + Colors.RESET);
        }
        
        if (hasWarnings()) {
            System.out.println(Colors.YELLOW + "\n⚠️ WARNINGS SEMÁNTICOS:" + Colors.RESET);
            printWarnings();
        }
        
        // Mostrar estadísticas
        printStatistics();
        
        // Generar reporte detallado
        generateDetailedReport(analyzer);
    }
    
    /**
     * Imprime todos los errores semánticos con formato detallado
     */
    private void printErrors() {
        for (int i = 0; i < errors.size(); i++) {
            SemanticError error = errors.get(i);
            
            System.out.println(String.format(
                "%s[Error Semántico %d]%s Línea %d, Columna %d:",
                Colors.RED,
                i + 1,
                Colors.RESET,
                error.getLine(),
                error.getColumn()
            ));
            
            // Mensaje del error
            System.out.println(String.format(
                "  %s%s%s",
                Colors.YELLOW,
                error.getMessage(),
                Colors.RESET
            ));
            
            // Contexto de la línea
            String context = getLineContext(error.getLine());
            if (!context.isEmpty()) {
                System.out.println(String.format(
                    "  %sContexto:%s %s",
                    Colors.CYAN,
                    Colors.RESET,
                    context
                ));
                
                // Mostrar indicador de posición
                String pointer = " ".repeat(Math.max(0, error.getColumn())) + "^";
                System.out.println(String.format(
                    "  %s%s%s",
                    Colors.RED,
                    pointer,
                    Colors.RESET
                ));
            }
            
            // Sugerencia específica según el tipo de error
            String suggestion = generateErrorSuggestion(error);
            System.out.println(String.format(
                "  %s💡 Sugerencia:%s %s",
                Colors.GREEN,
                Colors.RESET,
                suggestion
            ));
            
            System.out.println(); // Línea en blanco entre errores
        }
        
        // Resumen de errores
        System.out.println(String.format(
            "%sTotal de errores semánticos: %d%s",
            Colors.RED,
            errors.size(),
            Colors.RESET
        ));
    }
    
    /**
     * Imprime todos los warnings semánticos
     */
    private void printWarnings() {
        for (int i = 0; i < warnings.size(); i++) {
            SemanticWarning warning = warnings.get(i);
            
            System.out.println(String.format(
                "%s[Warning %d]%s Línea %d, Columna %d: %s%s%s",
                Colors.YELLOW,
                i + 1,
                Colors.RESET,
                warning.getLine(),
                warning.getColumn(),
                Colors.CYAN,
                warning.getMessage(),
                Colors.RESET
            ));
            
            // Contexto si está disponible
            String context = getLineContext(warning.getLine());
            if (!context.isEmpty()) {
                System.out.println(String.format(
                    "    %sContexto:%s %s",
                    Colors.BLUE,
                    Colors.RESET,
                    context
                ));
            }
            
            // Sugerencia para el warning
            String suggestion = generateWarningSuggestion(warning);
            if (!suggestion.isEmpty()) {
                System.out.println(String.format(
                    "    %s💡 Recomendación:%s %s",
                    Colors.GREEN,
                    Colors.RESET,
                    suggestion
                ));
            }
        }
        
        System.out.println(String.format(
            "%sTotal de warnings: %d%s",
            Colors.YELLOW,
            warnings.size(),
            Colors.RESET
        ));
    }
    
    /**
     * Genera sugerencias específicas para cada tipo de error
     */
    private String generateErrorSuggestion(SemanticError error) {
        switch (error.getType()) {
            case UNDEFINED_VARIABLE:
                return String.format("Verifique que la variable '%s' esté declarada antes de usarla. " +
                      "Posibles causas: error de tipeo, variable declarada en otro ámbito, o falta la declaración.",
                      error.getContext());
                      
            case UNDEFINED_FUNCTION:
                return String.format("Verifique que la función '%s' esté declarada antes de llamarla. " +
                      "Asegúrese de que el nombre y la firma sean correctos.",
                      error.getContext());
                      
            case REDEFINITION:
                return String.format("El identificador '%s' ya está definido. " +
                      "Use un nombre diferente o verifique si realmente necesita redefinirlo.",
                      error.getContext());
                      
            case TYPE_MISMATCH:
                return "Verifique que los tipos de datos sean compatibles. " +
                      "Puede necesitar una conversión explícita (cast) o cambiar el tipo de alguna variable.";
                      
            case INVALID_OPERATION:
                return "Esta operación no está permitida en este contexto. " +
                      "Verifique la sintaxis y semántica del lenguaje.";
                      
            case FUNCTION_CALL_ERROR:
                return String.format("Verifique que la llamada a '%s' tenga el número correcto de argumentos " +
                      "y que los tipos coincidan con la declaración de la función.",
                      error.getContext());
                      
            case RETURN_TYPE_MISMATCH:
                return "El tipo del valor de retorno no coincide con el tipo declarado de la función. " +
                      "Verifique el tipo de retorno o modifique la expresión de retorno.";
                      
            case UNINITIALIZED_VARIABLE:
                return String.format("Inicialice la variable '%s' antes de usarla, " +
                      "o verifique que todas las rutas de ejecución la inicialicen.",
                      error.getContext());
                      
            default:
                return "Revise la documentación del lenguaje para más información sobre este error.";
        }
    }
    
    /**
     * Genera sugerencias para warnings
     */
    private String generateWarningSuggestion(SemanticWarning warning) {
        switch (warning.getType()) {
            case UNUSED_VARIABLE:
                return String.format("La variable '%s' no se usa. Considere eliminarla si no es necesaria.",
                      warning.getContext());
                      
            case UNUSED_FUNCTION:
                return String.format("La función '%s' no se llama. Considere eliminarla si no es necesaria.",
                      warning.getContext());
                      
            case UNUSED_PARAMETER:
                return String.format("El parámetro '%s' no se usa en la función. " +
                      "Considere eliminarlo o usar un nombre que indique que no se usa (ej: '_unused').",
                      warning.getContext());
                      
            case IMPLICIT_CONVERSION:
                return "Se está realizando una conversión implícita de tipos. " +
                      "Considere hacer la conversión explícita para claridad.";
                      
            case UNREACHABLE_CODE:
                return "Este código nunca se ejecutará. Verifique la lógica de control de flujo.";
                      
            case MISSING_RETURN:
                return "La función puede no retornar un valor en todos los caminos. " +
                      "Agregue sentencias 'return' donde sea necesario.";
                      
            default:
                return "";
        }
    }
    
    /**
     * Imprime estadísticas detalladas de errores y warnings
     */
    private void printStatistics() {
        System.out.println(Colors.BLUE + "\n📊 ESTADÍSTICAS DEL ANÁLISIS SEMÁNTICO:" + Colors.RESET);
        
        if (errors.isEmpty() && warnings.isEmpty()) {
            System.out.println(Colors.GREEN + "  ✅ Código semánticamente correcto" + Colors.RESET);
            return;
        }
        
        // Estadísticas de errores por tipo
        if (!errors.isEmpty()) {
            System.out.println(Colors.RED + "\n  Errores por tipo:" + Colors.RESET);
            Map<SemanticError.ErrorType, Integer> errorCounts = new HashMap<>();
            
            for (SemanticError error : errors) {
                errorCounts.put(error.getType(), errorCounts.getOrDefault(error.getType(), 0) + 1);
            }
            
            for (Map.Entry<SemanticError.ErrorType, Integer> entry : errorCounts.entrySet()) {
                System.out.println(String.format("    • %s: %d", 
                    getErrorTypeDescription(entry.getKey()), entry.getValue()));
            }
        }
        
        // Estadísticas de warnings por tipo
        if (!warnings.isEmpty()) {
            System.out.println(Colors.YELLOW + "\n  Warnings por tipo:" + Colors.RESET);
            Map<SemanticWarning.WarningType, Integer> warningCounts = new HashMap<>();
            
            for (SemanticWarning warning : warnings) {
                warningCounts.put(warning.getType(), warningCounts.getOrDefault(warning.getType(), 0) + 1);
            }
            
            for (Map.Entry<SemanticWarning.WarningType, Integer> entry : warningCounts.entrySet()) {
                System.out.println(String.format("    • %s: %d", 
                    getWarningTypeDescription(entry.getKey()), entry.getValue()));
            }
        }
        
        // Resumen general
        System.out.println(String.format("\n  %sResumen:%s %d errores, %d warnings",
            Colors.BLUE, Colors.RESET, errors.size(), warnings.size()));
    }
    
    /**
     * Genera un reporte detallado en archivo
     */
    private void generateDetailedReport(SemanticAnalyzer analyzer) {
        try {
            String reportFile = "semantic_analysis_report.txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
                writer.println("=== REPORTE DE ANÁLISIS SEMÁNTICO ===");
                writer.println("Generado por: Compilador ANTLR4");
                writer.println("Fecha: " + java.time.LocalDateTime.now());
                writer.println();
                
                // Resumen ejecutivo
                writer.println("RESUMEN EJECUTIVO:");
                writer.println("- Errores semánticos: " + errors.size());
                writer.println("- Warnings: " + warnings.size());
                writer.println("- Estado: " + (errors.isEmpty() ? "EXITOSO" : "CON ERRORES"));
                writer.println();
                
                // Tabla de símbolos
                writer.println("TABLA DE SÍMBOLOS:");
                writer.println("==================");
                generateSymbolTableReport(writer, analyzer.getSymbolTable());
                writer.println();
                
                // Errores detallados
                if (!errors.isEmpty()) {
                    writer.println("ERRORES SEMÁNTICOS:");
                    writer.println("===================");
                    for (int i = 0; i < errors.size(); i++) {
                        SemanticError error = errors.get(i);
                        writer.println(String.format("[Error %d] Línea %d, Columna %d", 
                            i + 1, error.getLine(), error.getColumn()));
                        writer.println("Tipo: " + getErrorTypeDescription(error.getType()));
                        writer.println("Mensaje: " + error.getMessage());
                        writer.println("Contexto: " + getLineContext(error.getLine()));
                        writer.println("Sugerencia: " + generateErrorSuggestion(error));
                        writer.println();
                    }
                }
                
                // Warnings detallados
                if (!warnings.isEmpty()) {
                    writer.println("WARNINGS:");
                    writer.println("=========");
                    for (int i = 0; i < warnings.size(); i++) {
                        SemanticWarning warning = warnings.get(i);
                        writer.println(String.format("[Warning %d] Línea %d, Columna %d", 
                            i + 1, warning.getLine(), warning.getColumn()));
                        writer.println("Tipo: " + getWarningTypeDescription(warning.getType()));
                        writer.println("Mensaje: " + warning.getMessage());
                        writer.println("Contexto: " + getLineContext(warning.getLine()));
                        writer.println("Recomendación: " + generateWarningSuggestion(warning));
                        writer.println();
                    }
                }
                
                // Recomendaciones generales
                writer.println("RECOMENDACIONES GENERALES:");
                writer.println("==========================");
                generateGeneralRecommendations(writer);
            }
            
            System.out.println(Colors.GREEN + "Reporte detallado guardado en: " + reportFile + Colors.RESET);
            
        } catch (IOException e) {
            System.err.println(Colors.YELLOW + "Advertencia: No se pudo generar el reporte detallado: " + 
                e.getMessage() + Colors.RESET);
        }
    }
    
    /**
     * Genera reporte de la tabla de símbolos
     */
    private void generateSymbolTableReport(PrintWriter writer, SymbolTable symbolTable) {
        // Obtener el scope global para mostrar todas las funciones y variables globales
        SymbolTable.Scope globalScope = symbolTable.getCurrentScope();
        while (globalScope != null && globalScope.getParent() != null) {
            globalScope = globalScope.getParent();
        }
        
        if (globalScope != null) {
            writer.println("Símbolos Globales:");
            for (SymbolTable.Symbol symbol : globalScope.getAllSymbols()) {
                if (symbol instanceof SymbolTable.FunctionSymbol) {
                    SymbolTable.FunctionSymbol func = (SymbolTable.FunctionSymbol) symbol;
                    writer.println(String.format("  FUNCIÓN: %s", func.getSignature()));
                    writer.println(String.format("    - Definida: %s", func.isDefined() ? "Sí" : "No"));
                    writer.println(String.format("    - Llamada: %s", func.isCalled() ? "Sí" : "No"));
                    writer.println(String.format("    - Línea: %d", func.getLine()));
                } else if (symbol instanceof SymbolTable.VariableSymbol) {
                    SymbolTable.VariableSymbol var = (SymbolTable.VariableSymbol) symbol;
                    writer.println(String.format("  VARIABLE: %s %s", var.getType(), var.getName()));
                    writer.println(String.format("    - Inicializada: %s", var.isInitialized() ? "Sí" : "No"));
                    writer.println(String.format("    - Usada: %s", var.isUsed() ? "Sí" : "No"));
                    writer.println(String.format("    - Línea: %d", var.getLine()));
                }
            }
        }
    }
    
    /**
     * Genera recomendaciones generales
     */
    private void generateGeneralRecommendations(PrintWriter writer) {
        writer.println("1. Declaraciones:");
        writer.println("   - Declare todas las variables antes de usarlas");
        writer.println("   - Use nombres descriptivos para variables y funciones");
        writer.println("   - Inicialice las variables en su declaración cuando sea posible");
        writer.println();
        
        writer.println("2. Tipos de datos:");
        writer.println("   - Verifique la compatibilidad de tipos en asignaciones y operaciones");
        writer.println("   - Use conversiones explícitas cuando sea necesario");
        writer.println("   - Considere el rango y precisión de los tipos numéricos");
        writer.println();
        
        writer.println("3. Funciones:");
        writer.println("   - Verifique que todas las funciones retornen un valor del tipo declarado");
        writer.println("   - Use parámetros const cuando no se modifiquen");
        writer.println("   - Considere eliminar funciones no utilizadas");
        writer.println();
        
        writer.println("4. Buenas prácticas:");
        writer.println("   - Elimine variables y parámetros no utilizados");
        writer.println("   - Use nombres que indiquen claramente el propósito");
        writer.println("   - Mantenga las funciones pequeñas y con propósito único");
    }
    
    /**
     * Obtiene el contexto de una línea específica
     */
    private String getLineContext(int line) {
        if (line > 0 && line <= sourceLines.length) {
            return sourceLines[line - 1].trim();
        }
        return "";
    }
    
    /**
     * Obtiene descripción legible del tipo de error
     */
    private String getErrorTypeDescription(SemanticError.ErrorType type) {
        switch (type) {
            case UNDEFINED_VARIABLE: return "Variable no definida";
            case UNDEFINED_FUNCTION: return "Función no definida";
            case REDEFINITION: return "Redefinición";
            case TYPE_MISMATCH: return "Incompatibilidad de tipos";
            case INVALID_OPERATION: return "Operación inválida";
            case FUNCTION_CALL_ERROR: return "Error en llamada a función";
            case RETURN_TYPE_MISMATCH: return "Tipo de retorno incorrecto";
            case UNINITIALIZED_VARIABLE: return "Variable no inicializada";
            default: return type.toString();
        }
    }
    
    /**
     * Obtiene descripción legible del tipo de warning
     */
    private String getWarningTypeDescription(SemanticWarning.WarningType type) {
        switch (type) {
            case UNUSED_VARIABLE: return "Variable no usada";
            case UNUSED_FUNCTION: return "Función no usada";
            case UNUSED_PARAMETER: return "Parámetro no usado";
            case IMPLICIT_CONVERSION: return "Conversión implícita";
            case UNREACHABLE_CODE: return "Código inalcanzable";
            case MISSING_RETURN: return "Falta sentencia return";
            default: return type.toString();
        }
    }
    
    /**
     * Verifica si hay errores
     */
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    /**
     * Verifica si hay warnings
     */
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    /**
     * Obtiene el número de errores
     */
    public int getErrorCount() {
        return errors != null ? errors.size() : 0;
    }
    
    /**
     * Obtiene el número de warnings
     */
    public int getWarningCount() {
        return warnings != null ? warnings.size() : 0;
    }
    
    /**
     * Verifica si el análisis fue exitoso (sin errores)
     */
    public boolean isSuccessful() {
        return !hasErrors();
    }
    
    /**
     * Genera un resumen breve del análisis
     */
    public String getSummary() {
        if (!hasErrors() && !hasWarnings()) {
            return "✅ Análisis semántico exitoso";
        } else if (!hasErrors()) {
            return String.format("⚠️ Análisis exitoso con %d warning(s)", getWarningCount());
        } else {
            return String.format("❌ Análisis fallido: %d error(es), %d warning(s)", 
                getErrorCount(), getWarningCount());
        }
    }
}
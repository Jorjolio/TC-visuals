package prieba;

import java.util.*;

/**
 * Tabla de Símbolos para el análisis semántico
 * Maneja variables, funciones y sus ámbitos (scopes)
 */
public class SymbolTable {
    
    // Stack de scopes para manejar ámbitos anidados
    private Stack<Scope> scopeStack;
    private int currentScopeLevel;
    private List<SemanticError> errors;
    private List<SemanticWarning> warnings;
    
    public SymbolTable() {
        this.scopeStack = new Stack<>();
        this.currentScopeLevel = 0;
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        
        // Crear scope global
        enterScope("GLOBAL");
    }
    
    /**
     * Clase para representar un ámbito (scope)
     */
    public static class Scope {
        private String name;
        private int level;
        private Map<String, Symbol> symbols;
        private Scope parent;
        
        public Scope(String name, int level, Scope parent) {
            this.name = name;
            this.level = level;
            this.symbols = new HashMap<>();
            this.parent = parent;
        }
        
        public void addSymbol(Symbol symbol) {
            symbols.put(symbol.getName(), symbol);
        }
        
        public Symbol getSymbol(String name) {
            return symbols.get(name);
        }
        
        public boolean containsSymbol(String name) {
            return symbols.containsKey(name);
        }
        
        public Collection<Symbol> getAllSymbols() {
            return symbols.values();
        }
        
        // Getters
        public String getName() { return name; }
        public int getLevel() { return level; }
        public Scope getParent() { return parent; }
    }
    
    /**
     * Clase base para símbolos
     */
    public static abstract class Symbol {
        protected String name;
        protected DataType type;
        protected int line;
        protected int column;
        protected boolean initialized;
        protected boolean used;
        
        public Symbol(String name, DataType type, int line, int column) {
            this.name = name;
            this.type = type;
            this.line = line;
            this.column = column;
            this.initialized = false;
            this.used = false;
        }
        
        // Getters y setters
        public String getName() { return name; }
        public DataType getType() { return type; }
        public int getLine() { return line; }
        public int getColumn() { return column; }
        public boolean isInitialized() { return initialized; }
        public void setInitialized(boolean initialized) { this.initialized = initialized; }
        public boolean isUsed() { return used; }
        public void setUsed(boolean used) { this.used = used; }
        
        public abstract String getSymbolType();
    }
    
    /**
     * Símbolo para variables
     */
    public static class VariableSymbol extends Symbol {
        private boolean isParameter;
        private Object value; // Para propagación de constantes
        
        public VariableSymbol(String name, DataType type, int line, int column) {
            super(name, type, line, column);
            this.isParameter = false;
        }
        
        public VariableSymbol(String name, DataType type, int line, int column, boolean isParameter) {
            super(name, type, line, column);
            this.isParameter = isParameter;
            if (isParameter) {
                this.initialized = true; // Los parámetros se consideran inicializados
            }
        }
        
        @Override
        public String getSymbolType() {
            return isParameter ? "PARAMETER" : "VARIABLE";
        }
        
        public boolean isParameter() { return isParameter; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
    }
    
    /**
     * Símbolo para funciones
     */
    public static class FunctionSymbol extends Symbol {
        private List<DataType> parameterTypes;
        private List<String> parameterNames;
        private boolean defined; // true si tiene implementación, false si solo declaración
        private boolean called;
        
        public FunctionSymbol(String name, DataType returnType, int line, int column) {
            super(name, returnType, line, column);
            this.parameterTypes = new ArrayList<>();
            this.parameterNames = new ArrayList<>();
            this.defined = false;
            this.called = false;
            this.initialized = true; // Las funciones no necesitan inicialización
        }
        
        public void addParameter(String paramName, DataType paramType) {
            parameterNames.add(paramName);
            parameterTypes.add(paramType);
        }
        
        @Override
        public String getSymbolType() {
            return "FUNCTION";
        }
        
        // Getters y setters
        public List<DataType> getParameterTypes() { return new ArrayList<>(parameterTypes); }
        public List<String> getParameterNames() { return new ArrayList<>(parameterNames); }
        public boolean isDefined() { return defined; }
        public void setDefined(boolean defined) { this.defined = defined; }
        public boolean isCalled() { return called; }
        public void setCalled(boolean called) { this.called = called; }
        
        public String getSignature() {
            StringBuilder sb = new StringBuilder();
            sb.append(type.toString()).append(" ").append(name).append("(");
            for (int i = 0; i < parameterTypes.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(parameterTypes.get(i));
            }
            sb.append(")");
            return sb.toString();
        }
    }
    
    /**
     * Enumeración para tipos de datos
     */
    public enum DataType {
        INT("int"),
        FLOAT("float"), 
        DOUBLE("double"),
        CHAR("char"),
        BOOL("bool"),
        VOID("void"),
        STRING("string"), // Para literales de cadena
        UNKNOWN("unknown"),
        ERROR("error");
        
        private final String name;
        
        DataType(String name) {
            this.name = name;
        }
        
        @Override
        public String toString() {
            return name;
        }
        
        public static DataType fromString(String str) {
            for (DataType type : DataType.values()) {
                if (type.name.equalsIgnoreCase(str)) {
                    return type;
                }
            }
            return UNKNOWN;
        }
        
        /**
         * Verifica si dos tipos son compatibles para asignación
         */
        public boolean isCompatibleWith(DataType other) {
            if (this == other) return true;
            if (this == ERROR || other == ERROR) return false;
            
            // Conversiones implícitas permitidas
            switch (this) {
                case DOUBLE:
                    return other == FLOAT || other == INT;
                case FLOAT:
                    return other == INT;
                case INT:
                    return other == CHAR; // char se puede asignar a int
                default:
                    return false;
            }
        }
        
        /**
         * Obtiene el tipo resultante de una operación aritmética
         */
        public static DataType getArithmeticResultType(DataType left, DataType right) {
            if (left == ERROR || right == ERROR) return ERROR;
            
            // Jerarquía: double > float > int > char
            if (left == DOUBLE || right == DOUBLE) return DOUBLE;
            if (left == FLOAT || right == FLOAT) return FLOAT;
            if (left == INT || right == INT) return INT;
            if (left == CHAR && right == CHAR) return INT; // char + char = int
            
            return ERROR;
        }
        
        /**
         * Verifica si el tipo soporta operaciones aritméticas
         */
        public boolean isNumeric() {
            return this == INT || this == FLOAT || this == DOUBLE || this == CHAR;
        }
        
        /**
         * Verifica si el tipo soporta operaciones lógicas
         */
        public boolean isLogical() {
            return this == BOOL || this.isNumeric(); // En C, cualquier número puede ser lógico
        }
    }
    
    /**
     * Clase para errores semánticos
     */
    public static class SemanticError {
        private final String message;
        private final int line;
        private final int column;
        private final String context;
        private final ErrorType type;
        
        public enum ErrorType {
            UNDEFINED_VARIABLE,
            UNDEFINED_FUNCTION,
            REDEFINITION,
            TYPE_MISMATCH,
            INVALID_OPERATION,
            FUNCTION_CALL_ERROR,
            RETURN_TYPE_MISMATCH,
            UNINITIALIZED_VARIABLE
        }
        
        public SemanticError(String message, int line, int column, String context, ErrorType type) {
            this.message = message;
            this.line = line;
            this.column = column;
            this.context = context;
            this.type = type;
        }
        
        // Getters
        public String getMessage() { return message; }
        public int getLine() { return line; }
        public int getColumn() { return column; }
        public String getContext() { return context; }
        public ErrorType getType() { return type; }
    }
    
    /**
     * Clase para warnings semánticos
     */
    public static class SemanticWarning {
        private final String message;
        private final int line;
        private final int column;
        private final String context;
        private final WarningType type;
        
        public enum WarningType {
            UNUSED_VARIABLE,
            UNUSED_FUNCTION,
            UNUSED_PARAMETER,
            IMPLICIT_CONVERSION,
            UNREACHABLE_CODE,
            MISSING_RETURN,
            UNINITIALIZED_VARIABLE
        }
        
        public SemanticWarning(String message, int line, int column, String context, WarningType type) {
            this.message = message;
            this.line = line;
            this.column = column;
            this.context = context;
            this.type = type;
        }
        
        // Getters
        public String getMessage() { return message; }
        public int getLine() { return line; }
        public int getColumn() { return column; }
        public String getContext() { return context; }
        public WarningType getType() { return type; }
    }
    
    // ========== MÉTODOS PRINCIPALES ==========
    
    /**
     * Entra a un nuevo ámbito
     */
    public void enterScope(String scopeName) {
        Scope currentScope = scopeStack.isEmpty() ? null : scopeStack.peek();
        Scope newScope = new Scope(scopeName, currentScopeLevel++, currentScope);
        scopeStack.push(newScope);
    }
    
    /**
     * Sale del ámbito actual
     */
    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            Scope exitingScope = scopeStack.pop();
            currentScopeLevel--;
            
            // Verificar variables no utilizadas
            checkUnusedSymbols(exitingScope);
        }
    }
    
    /**
     * Declara una variable
     */
    public boolean declareVariable(String name, DataType type, int line, int column, boolean isParameter) {
        if (scopeStack.isEmpty()) {
            addError("Error interno: No hay scope activo", line, column, name, SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return false;
        }
        
        Scope currentScope = scopeStack.peek();
        
        // Verificar redefinición en el scope actual
        if (currentScope.containsSymbol(name)) {
            addError(String.format("Variable '%s' ya está definida en este ámbito", name), 
                    line, column, name, SemanticError.ErrorType.REDEFINITION);
            return false;
        }
        
        // Agregar variable al scope actual
        VariableSymbol variable = new VariableSymbol(name, type, line, column, isParameter);
        currentScope.addSymbol(variable);
        return true;
    }
    
    /**
     * Declara una función
     */
    public boolean declareFunction(String name, DataType returnType, List<String> paramNames, 
                                  List<DataType> paramTypes, int line, int column) {
        if (scopeStack.isEmpty()) {
            addError("Error interno: No hay scope activo", line, column, name, SemanticError.ErrorType.UNDEFINED_FUNCTION);
            return false;
        }
        
        // Las funciones se declaran en el scope global
        Scope globalScope = getGlobalScope();
        
        // Verificar si ya existe
        if (globalScope.containsSymbol(name)) {
            Symbol existing = globalScope.getSymbol(name);
            if (existing instanceof FunctionSymbol) {
                FunctionSymbol existingFunc = (FunctionSymbol) existing;
                
                // Verificar si las firmas coinciden
                if (!functionsSignatureMatch(existingFunc, returnType, paramTypes)) {
                    addError(String.format("Función '%s' redefinida con firma diferente", name), 
                            line, column, name, SemanticError.ErrorType.REDEFINITION);
                    return false;
                }
                
                // Si ya estaba definida, es un error de redefinición
                if (existingFunc.isDefined()) {
                    addError(String.format("Función '%s' ya está definida", name), 
                            line, column, name, SemanticError.ErrorType.REDEFINITION);
                    return false;
                }
                
                // Marcar como definida
                existingFunc.setDefined(true);
                return true;
            } else {
                addError(String.format("'%s' ya está definido como variable", name), 
                        line, column, name, SemanticError.ErrorType.REDEFINITION);
                return false;
            }
        }
        
        // Crear nueva función
        FunctionSymbol function = new FunctionSymbol(name, returnType, line, column);
        for (int i = 0; i < paramNames.size(); i++) {
            function.addParameter(paramNames.get(i), paramTypes.get(i));
        }
        function.setDefined(true);
        
        globalScope.addSymbol(function);
        return true;
    }
    
    /**
     * Busca una variable en todos los scopes
     */
    public VariableSymbol lookupVariable(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Scope scope = scopeStack.get(i);
            Symbol symbol = scope.getSymbol(name);
            if (symbol instanceof VariableSymbol) {
                return (VariableSymbol) symbol;
            }
        }
        return null;
    }
    
    /**
     * Busca una función
     */
    public FunctionSymbol lookupFunction(String name) {
        Scope globalScope = getGlobalScope();
        Symbol symbol = globalScope.getSymbol(name);
        if (symbol instanceof FunctionSymbol) {
            return (FunctionSymbol) symbol;
        }
        return null;
    }
    
    /**
     * Verifica el uso de una variable
     */
    public DataType useVariable(String name, int line, int column) {
        VariableSymbol variable = lookupVariable(name);
        if (variable == null) {
            addError(String.format("Variable '%s' no está definida", name), 
                    line, column, name, SemanticError.ErrorType.UNDEFINED_VARIABLE);
            return DataType.ERROR;
        }
        
        // Marcar como usada
        variable.setUsed(true);
        
        // Verificar si está inicializada (solo para variables, no parámetros)
        if (!variable.isParameter() && !variable.isInitialized()) {
            addWarning(String.format("Variable '%s' usada sin inicializar", name), 
                      line, column, name, SemanticWarning.WarningType.UNINITIALIZED_VARIABLE);
        }
        
        return variable.getType();
    }
    
    /**
     * Verifica una llamada a función
     */
    public DataType callFunction(String name, List<DataType> argTypes, int line, int column) {
        FunctionSymbol function = lookupFunction(name);
        if (function == null) {
            addError(String.format("Función '%s' no está definida", name), 
                    line, column, name, SemanticError.ErrorType.UNDEFINED_FUNCTION);
            return DataType.ERROR;
        }
        
        // Marcar como llamada
        function.setCalled(true);
        
        // Verificar número de argumentos
        List<DataType> paramTypes = function.getParameterTypes();
        if (argTypes.size() != paramTypes.size()) {
            addError(String.format("Función '%s' espera %d argumentos, pero se pasaron %d", 
                    name, paramTypes.size(), argTypes.size()), 
                    line, column, name, SemanticError.ErrorType.FUNCTION_CALL_ERROR);
            return function.getType();
        }
        
        // Verificar tipos de argumentos
        for (int i = 0; i < argTypes.size(); i++) {
            DataType argType = argTypes.get(i);
            DataType paramType = paramTypes.get(i);
            
            if (!paramType.isCompatibleWith(argType)) {
                if (argType.isCompatibleWith(paramType)) {
                    // Conversión implícita posible
                    addWarning(String.format("Conversión implícita de %s a %s en argumento %d de función '%s'", 
                              argType, paramType, i + 1, name), 
                              line, column, name, SemanticWarning.WarningType.IMPLICIT_CONVERSION);
                } else {
                    addError(String.format("Tipo incompatible en argumento %d de función '%s': esperado %s, encontrado %s", 
                            i + 1, name, paramType, argType), 
                            line, column, name, SemanticError.ErrorType.TYPE_MISMATCH);
                }
            }
        }
        
        return function.getType();
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private Scope getGlobalScope() {
        return scopeStack.isEmpty() ? null : scopeStack.get(0);
    }
    
    private boolean functionsSignatureMatch(FunctionSymbol existing, DataType returnType, List<DataType> paramTypes) {
        if (!existing.getType().equals(returnType)) return false;
        
        List<DataType> existingParams = existing.getParameterTypes();
        if (existingParams.size() != paramTypes.size()) return false;
        
        for (int i = 0; i < paramTypes.size(); i++) {
            if (!existingParams.get(i).equals(paramTypes.get(i))) return false;
        }
        
        return true;
    }
    
    private void checkUnusedSymbols(Scope scope) {
        for (Symbol symbol : scope.getAllSymbols()) {
            if (!symbol.isUsed()) {
                if (symbol instanceof VariableSymbol) {
                    VariableSymbol var = (VariableSymbol) symbol;
                    SemanticWarning.WarningType warningType = var.isParameter() ? 
                        SemanticWarning.WarningType.UNUSED_PARAMETER : 
                        SemanticWarning.WarningType.UNUSED_VARIABLE;
                    
                    addWarning(String.format("%s '%s' declarado pero no usado", 
                              var.getSymbolType().toLowerCase(), var.getName()), 
                              var.getLine(), var.getColumn(), var.getName(), warningType);
                } else if (symbol instanceof FunctionSymbol) {
                    FunctionSymbol func = (FunctionSymbol) symbol;
                    if (!func.isCalled() && !func.getName().equals("main")) {
                        addWarning(String.format("Función '%s' definida pero no llamada", func.getName()), 
                                  func.getLine(), func.getColumn(), func.getName(), 
                                  SemanticWarning.WarningType.UNUSED_FUNCTION);
                    }
                }
            }
        }
    }
    
    private void addError(String message, int line, int column, String context, SemanticError.ErrorType type) {
        errors.add(new SemanticError(message, line, column, context, type));
    }
    
    private void addWarning(String message, int line, int column, String context, SemanticWarning.WarningType type) {
        warnings.add(new SemanticWarning(message, line, column, context, type));
    }
    
    // ========== GETTERS Y UTILIDADES ==========
    
    public List<SemanticError> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public List<SemanticWarning> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public void clearErrors() {
        errors.clear();
    }
    
    public void clearWarnings() {
        warnings.clear();
    }
    
    public Scope getCurrentScope() {
        return scopeStack.isEmpty() ? null : scopeStack.peek();
    }
    
    public int getCurrentScopeLevel() {
        return currentScopeLevel;
    }
    
    /**
     * Imprime la tabla de símbolos para debugging
     */
    public void printSymbolTable() {
        System.out.println(Colors.BLUE + "\n=== TABLA DE SÍMBOLOS ===" + Colors.RESET);
        
        for (int i = 0; i < scopeStack.size(); i++) {
            Scope scope = scopeStack.get(i);
            String indent = "  ".repeat(i);
            
            System.out.println(String.format("%s%sScope: %s (Nivel %d)%s", 
                    indent, Colors.CYAN, scope.getName(), scope.getLevel(), Colors.RESET));
            
            for (Symbol symbol : scope.getAllSymbols()) {
                String color = symbol instanceof FunctionSymbol ? Colors.GREEN : Colors.YELLOW;
                String status = symbol.isUsed() ? "✓" : "✗";
                
                System.out.println(String.format("%s  %s%s %s%s %s(%s) - Línea %d %s", 
                        indent, color, symbol.getSymbolType(), symbol.getName(), Colors.RESET,
                        symbol.getType(), status, symbol.getLine(),
                        symbol.isInitialized() ? "[INIT]" : "[NO-INIT]"));
            }
        }
    }
}
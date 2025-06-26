package prieba;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link compiladorParser}.
 */
public interface compiladorListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link compiladorParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(compiladorParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(compiladorParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(compiladorParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(compiladorParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#function_declaration}.
	 * @param ctx the parse tree
	 */
	void enterFunction_declaration(compiladorParser.Function_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#function_declaration}.
	 * @param ctx the parse tree
	 */
	void exitFunction_declaration(compiladorParser.Function_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#parameter_list}.
	 * @param ctx the parse tree
	 */
	void enterParameter_list(compiladorParser.Parameter_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#parameter_list}.
	 * @param ctx the parse tree
	 */
	void exitParameter_list(compiladorParser.Parameter_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(compiladorParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(compiladorParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#main_function}.
	 * @param ctx the parse tree
	 */
	void enterMain_function(compiladorParser.Main_functionContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#main_function}.
	 * @param ctx the parse tree
	 */
	void exitMain_function(compiladorParser.Main_functionContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#type_specifier}.
	 * @param ctx the parse tree
	 */
	void enterType_specifier(compiladorParser.Type_specifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#type_specifier}.
	 * @param ctx the parse tree
	 */
	void exitType_specifier(compiladorParser.Type_specifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#compound_statement}.
	 * @param ctx the parse tree
	 */
	void enterCompound_statement(compiladorParser.Compound_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#compound_statement}.
	 * @param ctx the parse tree
	 */
	void exitCompound_statement(compiladorParser.Compound_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#statement_list}.
	 * @param ctx the parse tree
	 */
	void enterStatement_list(compiladorParser.Statement_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#statement_list}.
	 * @param ctx the parse tree
	 */
	void exitStatement_list(compiladorParser.Statement_listContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VariableDeclStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclStmt(compiladorParser.VariableDeclStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VariableDeclStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclStmt(compiladorParser.VariableDeclStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentStmt(compiladorParser.AssignmentStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentStmt(compiladorParser.AssignmentStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(compiladorParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(compiladorParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(compiladorParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(compiladorParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(compiladorParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(compiladorParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCallStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallStmt(compiladorParser.FunctionCallStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCallStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallStmt(compiladorParser.FunctionCallStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(compiladorParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(compiladorParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CompoundStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStmt(compiladorParser.CompoundStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CompoundStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStmt(compiladorParser.CompoundStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStmt(compiladorParser.ExpressionStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStmt(compiladorParser.ExpressionStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#variable_declaration}.
	 * @param ctx the parse tree
	 */
	void enterVariable_declaration(compiladorParser.Variable_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#variable_declaration}.
	 * @param ctx the parse tree
	 */
	void exitVariable_declaration(compiladorParser.Variable_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#assignment_statement}.
	 * @param ctx the parse tree
	 */
	void enterAssignment_statement(compiladorParser.Assignment_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#assignment_statement}.
	 * @param ctx the parse tree
	 */
	void exitAssignment_statement(compiladorParser.Assignment_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(compiladorParser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(compiladorParser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#for_statement}.
	 * @param ctx the parse tree
	 */
	void enterFor_statement(compiladorParser.For_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#for_statement}.
	 * @param ctx the parse tree
	 */
	void exitFor_statement(compiladorParser.For_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#for_init}.
	 * @param ctx the parse tree
	 */
	void enterFor_init(compiladorParser.For_initContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#for_init}.
	 * @param ctx the parse tree
	 */
	void exitFor_init(compiladorParser.For_initContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#for_update}.
	 * @param ctx the parse tree
	 */
	void enterFor_update(compiladorParser.For_updateContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#for_update}.
	 * @param ctx the parse tree
	 */
	void exitFor_update(compiladorParser.For_updateContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void enterWhile_statement(compiladorParser.While_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#while_statement}.
	 * @param ctx the parse tree
	 */
	void exitWhile_statement(compiladorParser.While_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#function_call}.
	 * @param ctx the parse tree
	 */
	void enterFunction_call(compiladorParser.Function_callContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#function_call}.
	 * @param ctx the parse tree
	 */
	void exitFunction_call(compiladorParser.Function_callContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#argument_list}.
	 * @param ctx the parse tree
	 */
	void enterArgument_list(compiladorParser.Argument_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#argument_list}.
	 * @param ctx the parse tree
	 */
	void exitArgument_list(compiladorParser.Argument_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link compiladorParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void enterReturn_statement(compiladorParser.Return_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link compiladorParser#return_statement}.
	 * @param ctx the parse tree
	 */
	void exitReturn_statement(compiladorParser.Return_statementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RelationalCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterRelationalCondition(compiladorParser.RelationalConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RelationalCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitRelationalCondition(compiladorParser.RelationalConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LogicalCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterLogicalCondition(compiladorParser.LogicalConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LogicalCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitLogicalCondition(compiladorParser.LogicalConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterParenCondition(compiladorParser.ParenConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitParenCondition(compiladorParser.ParenConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TrueCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterTrueCondition(compiladorParser.TrueConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TrueCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitTrueCondition(compiladorParser.TrueConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FalseCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterFalseCondition(compiladorParser.FalseConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FalseCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitFalseCondition(compiladorParser.FalseConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExpressionCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterExpressionCondition(compiladorParser.ExpressionConditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExpressionCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitExpressionCondition(compiladorParser.ExpressionConditionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void enterEqualOp(compiladorParser.EqualOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void exitEqualOp(compiladorParser.EqualOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void enterNotEqualOp(compiladorParser.NotEqualOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void exitNotEqualOp(compiladorParser.NotEqualOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LessOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void enterLessOp(compiladorParser.LessOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LessOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void exitLessOp(compiladorParser.LessOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LessEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void enterLessEqualOp(compiladorParser.LessEqualOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LessEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void exitLessEqualOp(compiladorParser.LessEqualOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GreaterOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void enterGreaterOp(compiladorParser.GreaterOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GreaterOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void exitGreaterOp(compiladorParser.GreaterOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GreaterEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void enterGreaterEqualOp(compiladorParser.GreaterEqualOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GreaterEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 */
	void exitGreaterEqualOp(compiladorParser.GreaterEqualOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AndOp}
	 * labeled alternative in {@link compiladorParser#logical_operator}.
	 * @param ctx the parse tree
	 */
	void enterAndOp(compiladorParser.AndOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AndOp}
	 * labeled alternative in {@link compiladorParser#logical_operator}.
	 * @param ctx the parse tree
	 */
	void exitAndOp(compiladorParser.AndOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OrOp}
	 * labeled alternative in {@link compiladorParser#logical_operator}.
	 * @param ctx the parse tree
	 */
	void enterOrOp(compiladorParser.OrOpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OrOp}
	 * labeled alternative in {@link compiladorParser#logical_operator}.
	 * @param ctx the parse tree
	 */
	void exitOrOp(compiladorParser.OrOpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TermExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTermExpression(compiladorParser.TermExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TermExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTermExpression(compiladorParser.TermExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAddExpression(compiladorParser.AddExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAddExpression(compiladorParser.AddExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SubExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSubExpression(compiladorParser.SubExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SubExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSubExpression(compiladorParser.SubExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void enterMultTerm(compiladorParser.MultTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void exitMultTerm(compiladorParser.MultTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DivTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void enterDivTerm(compiladorParser.DivTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DivTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void exitDivTerm(compiladorParser.DivTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FactorTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void enterFactorTerm(compiladorParser.FactorTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FactorTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void exitFactorTerm(compiladorParser.FactorTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ModTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void enterModTerm(compiladorParser.ModTermContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ModTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 */
	void exitModTerm(compiladorParser.ModTermContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterIdFactor(compiladorParser.IdFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitIdFactor(compiladorParser.IdFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterNumFactor(compiladorParser.NumFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitNumFactor(compiladorParser.NumFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FloatFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFloatFactor(compiladorParser.FloatFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FloatFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFloatFactor(compiladorParser.FloatFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CharFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterCharFactor(compiladorParser.CharFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CharFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitCharFactor(compiladorParser.CharFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterStringFactor(compiladorParser.StringFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitStringFactor(compiladorParser.StringFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCallFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallFactor(compiladorParser.FunctionCallFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCallFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallFactor(compiladorParser.FunctionCallFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterParenFactor(compiladorParser.ParenFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitParenFactor(compiladorParser.ParenFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryPlusFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterUnaryPlusFactor(compiladorParser.UnaryPlusFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryPlusFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitUnaryPlusFactor(compiladorParser.UnaryPlusFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryMinusFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterUnaryMinusFactor(compiladorParser.UnaryMinusFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryMinusFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitUnaryMinusFactor(compiladorParser.UnaryMinusFactorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NotFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void enterNotFactor(compiladorParser.NotFactorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NotFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 */
	void exitNotFactor(compiladorParser.NotFactorContext ctx);
}
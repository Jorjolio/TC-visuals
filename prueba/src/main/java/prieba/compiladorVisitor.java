package prieba;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link compiladorParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface compiladorVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link compiladorParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(compiladorParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(compiladorParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#function_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_declaration(compiladorParser.Function_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#parameter_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter_list(compiladorParser.Parameter_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(compiladorParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#main_function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMain_function(compiladorParser.Main_functionContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#type_specifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_specifier(compiladorParser.Type_specifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#compound_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompound_statement(compiladorParser.Compound_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#statement_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_list(compiladorParser.Statement_listContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VariableDeclStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclStmt(compiladorParser.VariableDeclStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignmentStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStmt(compiladorParser.AssignmentStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(compiladorParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(compiladorParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(compiladorParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCallStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallStmt(compiladorParser.FunctionCallStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(compiladorParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CompoundStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompoundStmt(compiladorParser.CompoundStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionStmt}
	 * labeled alternative in {@link compiladorParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionStmt(compiladorParser.ExpressionStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#variable_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_declaration(compiladorParser.Variable_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#assignment_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment_statement(compiladorParser.Assignment_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(compiladorParser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#for_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_statement(compiladorParser.For_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#for_init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_init(compiladorParser.For_initContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#for_update}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_update(compiladorParser.For_updateContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#while_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile_statement(compiladorParser.While_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#function_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_call(compiladorParser.Function_callContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#argument_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument_list(compiladorParser.Argument_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link compiladorParser#return_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_statement(compiladorParser.Return_statementContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelationalCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalCondition(compiladorParser.RelationalConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalCondition(compiladorParser.LogicalConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenCondition(compiladorParser.ParenConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueCondition(compiladorParser.TrueConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseCondition(compiladorParser.FalseConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExpressionCondition}
	 * labeled alternative in {@link compiladorParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionCondition(compiladorParser.ExpressionConditionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualOp(compiladorParser.EqualOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotEqualOp(compiladorParser.NotEqualOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LessOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessOp(compiladorParser.LessOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LessEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLessEqualOp(compiladorParser.LessEqualOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GreaterOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGreaterOp(compiladorParser.GreaterOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code GreaterEqualOp}
	 * labeled alternative in {@link compiladorParser#relational_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGreaterEqualOp(compiladorParser.GreaterEqualOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AndOp}
	 * labeled alternative in {@link compiladorParser#logical_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndOp(compiladorParser.AndOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OrOp}
	 * labeled alternative in {@link compiladorParser#logical_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrOp(compiladorParser.OrOpContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TermExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTermExpression(compiladorParser.TermExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpression(compiladorParser.AddExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SubExpression}
	 * labeled alternative in {@link compiladorParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubExpression(compiladorParser.SubExpressionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MultTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultTerm(compiladorParser.MultTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code DivTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDivTerm(compiladorParser.DivTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FactorTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactorTerm(compiladorParser.FactorTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ModTerm}
	 * labeled alternative in {@link compiladorParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModTerm(compiladorParser.ModTermContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IdFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdFactor(compiladorParser.IdFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NumFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumFactor(compiladorParser.NumFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FloatFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatFactor(compiladorParser.FloatFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CharFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCharFactor(compiladorParser.CharFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringFactor(compiladorParser.StringFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FunctionCallFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCallFactor(compiladorParser.FunctionCallFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenFactor(compiladorParser.ParenFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryPlusFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryPlusFactor(compiladorParser.UnaryPlusFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryMinusFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryMinusFactor(compiladorParser.UnaryMinusFactorContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NotFactor}
	 * labeled alternative in {@link compiladorParser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotFactor(compiladorParser.NotFactorContext ctx);
}
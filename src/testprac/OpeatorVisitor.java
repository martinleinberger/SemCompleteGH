package testprac;

import java.util.List;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class OpeatorVisitor extends VoidVisitorAdapter {
	
	@Override
	public void visit(BinaryExpr expr, Object arg)
	{
		Expression leftExpr=expr.getLeft();
		Expression rightExpr=expr.getRight();
		Operator operExpr=expr.getOperator();
		System.out.println("Binary: "+leftExpr+" "+operExpr+" "+rightExpr);
		handle_expression(leftExpr);
		handle_expression(rightExpr);
	}
	
	public void visit(AssignExpr expr, Object arg)
	{
		Expression target=expr.getTarget();
		Expression value=expr.getValue();
		System.out.println("Assignment: "+target+ "="+ value);
		if(value instanceof BinaryExpr)
		{
			handle_expression(value);
		}
		if(target instanceof BinaryExpr)
		{
			handle_expression(target);
			//visit((BinaryExpr)target, null);
		}	
	}
	public void visit(EnclosedExpr expr, Object arg)
	{
		Expression inner=expr.getInner();
		System.out.println(inner);
		handle_expression(inner);
	}
	
	
	protected void handle_expression(Expression expr)
	{
		
		if(expr instanceof BinaryExpr)
		{
			visit((BinaryExpr)expr,null);
		}
		if(expr instanceof AssignExpr)
		{
			visit((AssignExpr)expr,null);
		}
		if(expr instanceof EnclosedExpr)
		{
			visit((EnclosedExpr)expr,null);
		}
	}
	
	
	public void visit(MethodDeclaration n, Object arg)
	{
		BlockStmt block=n.getBody();
		List<Statement> stmts=block.getStmts();
		for(Statement stmt:stmts)
		{
			if(stmt instanceof ExpressionStmt)
			{
				
				Expression expr=((ExpressionStmt) stmt).getExpression();
				handle_expression(expr);
			}
			
		}
		//System.out.println(block);
	}

}

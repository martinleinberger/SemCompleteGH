package stats;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.visitor.VoidVisitor;
import japa.parser.ast.visitor.VoidVisitorAdapter;


public class MyDemoVisitor extends  VoidVisitorAdapter {

	public int method_count=0;
	public  int constructor_count=0;
	public int line_count=0;
	
	@Override
	public void visit(MethodDeclaration m, Object arg)
	{
		method_count++;
		count_the_line(m.getBody());
		
	}
	@Override
	public void visit(ConstructorDeclaration c,Object arg)
	{
		constructor_count++;
		count_the_line(c.getBlock());
	}
	
	protected void count_the_line(BlockStmt block)
	{
		try
		{
		String[] lines=block.toString().split("\n");
		line_count+=lines.length;
		line_count+=2;
		}catch(Exception exc){}
		
		
	}
	
	
	
	
	
}

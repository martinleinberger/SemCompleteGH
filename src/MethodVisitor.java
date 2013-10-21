import java.util.ArrayList;
import java.util.List;

//java parse name
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

//jena name 
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;
import java.io.*;

public class MethodVisitor extends VoidVisitorAdapter {
	
	
	Model model=null;
	Resource resource=null;
	RDFNode rdfNode=null;
	String ns="http://www.semcomplete.com#";
	
	public MethodVisitor()
	{
		//creating the model
		model=ModelFactory.createDefaultModel();	
	}
	
	
	@Override
	public void visit(MethodDeclaration n, Object arg) {
		// here you can access the attributes of the method.
		// this method will be called for all methods in this
		// CompilationUnit, including inner class methods
		try
		{
		System.out.println("Exploring Method: "+n.getName());
		
		//creating a resource
		resource=model.createResource(ns+n.getName());

		BlockStmt block = n.getBody();
		System.out.println("Showing method: " + n.getName()
				+ "\n----------------------\n");
		handle_the_block(block);
		System.out.println("Finishing method: " + n.getName()
				+ "\n----------------------\n");
		
		//now show the resource diagram
		//model.write(System.out);
		//writing to the file
		File file=new File("D:/My MSc/MyDataset/SemDataset/RDFs/first.owl");
		FileOutputStream fs=new FileOutputStream(file);
		model.write(fs);
		}catch(Exception exc){
			
		}
	}

	
	
	
	
	public void handle_the_block(BlockStmt block) {
		// code for handling the block
		try {
			// System.out.println(block);
			List<Statement> mylist = block.getStmts();
			// now go through the statements
			for (Statement stmt : mylist) {

				// now lets go through all types of statements
				// =============================================

				// at first check some block level statement
				
				//try-catch
				if (stmt instanceof TryStmt) {
					// now go deeper
					BlockStmt b = ((TryStmt) stmt).getTryBlock();
					explore_the_block(b);
				}
				
				//looping
				if (stmt instanceof ForeachStmt) {
					// go deeper
					BlockStmt b = (BlockStmt) ((ForeachStmt) stmt).getBody();
					explore_the_block(b);
				}
				if (stmt instanceof ForStmt) {
					// go deeper
					BlockStmt b = (BlockStmt) ((ForStmt) stmt).getBody();
					explore_the_block(b);
				}
				if (stmt instanceof WhileStmt) {
					// go deeper
					BlockStmt b = (BlockStmt) ((WhileStmt) stmt).getBody();
					explore_the_block(b);
				}
				if (stmt instanceof DoStmt) {
					// go deeper
					BlockStmt b = (BlockStmt) ((DoStmt) stmt).getBody();
					explore_the_block(b);
				}
				
				//control statement
				if(stmt instanceof IfStmt)
				{
					//go deeper
					BlockStmt b1=(BlockStmt)((IfStmt)stmt).getThenStmt();
					explore_the_block(b1);
					try
					{
						BlockStmt b2=(BlockStmt)((IfStmt)stmt).getElseStmt();
						explore_the_block(b2);
					}
					catch (Exception e) {
					}
				}
				//single level statement
				//expression statement
				if(stmt instanceof ExpressionStmt)
				{
					//go deeper
					//handle the expression
					explore_the_expression_stmt((ExpressionStmt)stmt);
				}
			}
		} catch (Exception exc) {

		}
	}
	
	
	public void explore_non_expression_statement(Statement stmt)
	{
		//explore other type of statement
		// asset statement
		if (stmt instanceof AssertStmt) {
			// print assert statement
			System.out.println(stmt);
		} else if (stmt instanceof BreakStmt) {
			// print break statement
			System.out.println(stmt);
		} else if (stmt instanceof ContinueStmt) {
			// print continue statement
			System.out.println(stmt);
		} else if (stmt instanceof ReturnStmt) {
			System.out.println(stmt);
		} else if (stmt instanceof LabeledStmt) {
			System.out.println(stmt);
		} else {
			// checking if its a block or not
		}
	}
	
	
	public void explore_the_expression_stmt(ExpressionStmt stmt)
	{
		//handle the expression statement
		try
		{
				if (stmt instanceof ExpressionStmt) {
				
				Expression expr=((ExpressionStmt) stmt).getExpression();
				
				//now discover different types of expressions
				
				if(expr instanceof VariableDeclarationExpr)
				{
					visit((VariableDeclarationExpr)expr,null);
					
				}else if(expr instanceof MethodCallExpr)
				{
					visit((MethodCallExpr)expr,null);
				}
			} 
		}catch(Exception exc){
			
		}
		
	}
	
	

	public void explore_the_block(BlockStmt stmt_b) {
		// code for exploring the block
		try {
			List<Statement> stmts = stmt_b.getStmts();
			
			for (Statement stmt : stmts) {
				// now go through the loop
				    if(stmt instanceof ExpressionStmt)
				    {
				    	explore_the_expression_stmt((ExpressionStmt)stmt);
				    }else
				    {
				    	explore_non_expression_statement(stmt);
				    }
				}

		} catch (Exception exc) {
			System.err.println("Cant explore the block properly."
					+ exc.getMessage());
		}
	}

	
	
	
	
	
	
	public void create_the_graph(String subject, String predicate,String object)
	{
		//code for creating the RDF
		try
		{
			
			
			
			
		}catch(Exception exc){
			System.out.println("Failed to create the RDF node");
			
		}
		
	}
	
	
	
	@Override
	public void visit(VariableDeclarationExpr vDecExpr, Object arg) {
		// code for accessing method call information
		try {
			
			//getting type of variables
			Type myType=vDecExpr.getType();
			System.out.println("Type:"+myType.toString());
			
			//declaration
			List<VariableDeclarator> vars=vDecExpr.getVars();
			VariableDeclarator dec=vars.get(0);
			//variable
			String varPart=dec.toString().split("=")[0];
			//initialization
			String InitPart=dec.toString().split("=")[1];
			
//			for(VariableDeclarator dec:vars)
//			{
//				System.out.print("VD:"+dec.toString());
//			}
			System.out.println();
			
			//do you have the tuple?
			//myType
			
			
			Resource dec_resource=model.createResource(ns+varPart);
			Property type_property=model.createProperty(ns+"Class");
			Property init_property=model.createProperty(ns+"InitiatedBy");
			dec_resource.addProperty(type_property, myType.toString());
			dec_resource.addProperty(init_property,InitPart);
			//now add to the parent
			Property declartion=model.createProperty(ns+"declaration");
			resource.addProperty(declartion, dec_resource);
			
			//now update new resource
			//resource=dec_resource;
			
			
		} catch (Exception exc) {
		}

	}
	
	@Override
	public void visit(MethodCallExpr mCallExpr, Object arg) {
		// code for accessing method call information
		try {
			
			//scope
			Expression expr=mCallExpr.getScope();
			System.out.print("Scope:"+expr.toString()+ " ");
			System.out.print("MC: "+mCallExpr.getName()+ " ");
			//parameters
			List<Expression> exprs= mCallExpr.getArgs();
			System.out.print("Parameters:");
			for(Expression exp1:exprs)
			{
				System.out.print(exp1+" ");
				
			}
			System.out.println();
			
			//do you have the tuples
			//scope: resource
			//predicate : hasMethod
			//object: MC
			

		} catch (Exception exc) {
		}

	}

}

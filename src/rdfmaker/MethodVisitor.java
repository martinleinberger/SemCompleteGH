package rdfmaker;

import java.util.List;

import japa.parser.ast.Comment;
import japa.parser.ast.LineComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import com.hp.hpl.jena.graph.query.Expression.Variable;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.*;

//code for visiting method
public class MethodVisitor extends VoidVisitorAdapter {

	// file counter
	public int project_file_counter = 1;

	// rdf related properties
	Model model = null;
	Resource tempResource = null; // temporary resource pointer
	Resource root_resource = null;
	Resource present_root = null;
	RDFNode rdfNode = null;
	String ns = "http://www.semcomplete.com#";

	// the relationships considered
	// property of a Class
	String hasMethod = "hasMethod"; // Method
	String hasConstructor = "hasConstructor"; // Method
	String hasAttribute = "hasAttribute"; // Attribute
	String initiatedBy = "varInitiatedBy"; // variable initiated by literal
	// String rdfType; //RDF node
	// String hasName; //String literal

	// property of an attribute
	String isAttributeOf = "isAttributeOf"; // Class Object
	// String hasName; //String literal
	// String rdfType; //RDF node

	// property of a method
	String hasReturnType = "hasReturnType"; // Class Object or built-in
	String isMethodOf = "isMethodOf"; // Class Object
	String hasParameter = "hasParameter"; // Parameter
	// String hasName //String literal
	// String rdfType; //RDF node

	// property of a parameter
	String isParamterOf = "isParameterOf"; // Method
	// String isInstanceOf; //Class: not sure to use yet
	// String rdfType; //RDF node

	// common property
	String hasName = "hasName";
	String classType = "classType";
	String startedBy = "startedby";
	String nodeType = "nodeType";

	public MethodVisitor() {
		// creating the model
		// model=ModelFactory.createDefaultModel();
	}

	protected void print_the_comment(String commentText) {
		// code for creating comments
		LineComment comment = new LineComment(commentText);
		System.out.print(comment);
	}
	
	protected int get_line_count(BlockStmt block)
	{
		//code for getting line numbers
		int line_count=0;
		List<Statement> stmts=block.getStmts();
		for(Statement stmt:stmts)
		{
			String[] lines=stmt.toString().split("\n");
			line_count+=lines.length;
		}
		//returning line count
		return line_count;
	}
	

	@Override
	public void visit(MethodDeclaration n, Object arg) {
		try {

			// directory name
			String rdfDir = "D:/My MSc/MyDataset/SemDataset/RDFColl/aoi/rdf";
			String javaDir = "D:/My MSc/MyDataset/SemDataset/RDFColl/aoi/java";

			// method name
			System.out.println("Function:" + n.getName());
			// fileNames
			String rdf_file_name = rdfDir + "/" + project_file_counter + ".rdf";
			String java_file_name = javaDir + "/" + project_file_counter
					+ ".java";
			System.out.println("==============================");

			// create the model
			model = ModelFactory.createDefaultModel();

			// method body
			BlockStmt block = n.getBody();

			// apply thresholding: discard unimportant methods

			// create root resource
			root_resource = model.createResource(ns + "program");
			present_root = root_resource; // representative of root

			// explore it
			explore_the_block(block);

			// writing the model and source to the files
			try {

				int block_line_count=get_line_count(block);
				//apply some thresholds
				if (true/*block_line_count > 5 && block_line_count<20*/) {
					
					File javafile = new File(java_file_name);
					FileWriter writer = new FileWriter(java_file_name);
					writer.write(n.getBody() + "\n");
					// writer.write(block.toString());
					writer.close();

					//File rdffile = new File(rdf_file_name);
					// showing the model
					//model.write(new FileOutputStream(rdf_file_name));

					// destroying the model
					model = null;
					// incrementing counter
					project_file_counter++;
				}

			} catch (Exception exc) {
			}

			System.out.println("==============================");
		} catch (Exception exc) {
		}
	}

	public void explore_the_block(BlockStmt block) {
		// code for handling the block
		try {
			// System.out.println(block);
			List<Statement> mylist = block.getStmts();
			// now go through the statements
			for (Statement stmt : mylist) {
				// try-catch
				if (stmt instanceof TryStmt) {
					print_the_comment("Start: Try Block");
					// now go deeper
					BlockStmt b = ((TryStmt) stmt).getTryBlock();
					explore_the_block(b);
					print_the_comment("End: Try Block");
				}
				// looping
				if (stmt instanceof ForeachStmt) {

					print_the_comment("Start: ForEach Iterable");
					// condition expression
					Expression condition = ((ForeachStmt) stmt).getIterable();
					explore_the_expression_stmt(new ExpressionStmt(condition));
					print_the_comment("End: ForEach Iterable");

					print_the_comment("Start: ForEach Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((ForeachStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("Start: ForEach Body");
				}
				if (stmt instanceof ForStmt) {

					print_the_comment("Start: For Condition");
					// condition
					Expression condition = ((ForStmt) stmt).getCompare();
					explore_the_expression_stmt(new ExpressionStmt(condition));
					print_the_comment("End: For Condition");

					print_the_comment("Start: For Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((ForStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("End: For Body");
				}
				if (stmt instanceof WhileStmt) {

					print_the_comment("Start: While Condition");
					// condition
					Expression condition = ((WhileStmt) stmt).getCondition();
					explore_the_expression_stmt(new ExpressionStmt(condition));
					print_the_comment("End: While Condition");
					print_the_comment("Start: While Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((WhileStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("End: While Body");
				}
				if (stmt instanceof DoStmt) {

					print_the_comment("Start: Do Condition");
					// condition
					Expression condition = ((DoStmt) stmt).getCondition();
					explore_the_expression_stmt(new ExpressionStmt(condition));
					print_the_comment("End: Do Condition");
					print_the_comment("Start: Do Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((DoStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("End: Do Body");
				}

				// control statement
				if (stmt instanceof IfStmt) {
					// access the condition
					print_the_comment("Start: If Condition");
					Expression condition = ((IfStmt) stmt).getCondition();
					explore_the_expression_stmt(new ExpressionStmt(condition));
					print_the_comment("End: If Condition");

					print_the_comment("Start: If-Then Block");
					// go deeper
					BlockStmt b1 = (BlockStmt) ((IfStmt) stmt).getThenStmt();
					explore_the_block(b1);
					print_the_comment("End: If-Then Block");
					try {

						BlockStmt b2 = (BlockStmt) ((IfStmt) stmt)
								.getElseStmt();
						if (b2 != null) {
							print_the_comment("Start: Else Block");
							explore_the_block(b2);
							print_the_comment("End: Else Block");
						}
					} catch (Exception e) {
					}
				}

				// single level statement
				// expression statement
				if (stmt instanceof ExpressionStmt) {
					// explore the expression
					explore_the_expression_stmt((ExpressionStmt) stmt);
				}
			}
		} catch (Exception exc) {
		}
	}

	public void explore_the_expression_stmt(ExpressionStmt stmt) {
		// handle the expression statement
		try {
			if (stmt instanceof ExpressionStmt) {
				Expression expr = ((ExpressionStmt) stmt).getExpression();
				// now discover different types of expressions
				if (expr instanceof ObjectCreationExpr) {
					visit((ObjectCreationExpr) expr, null);
				} else if (expr instanceof VariableDeclarationExpr) {
					visit((VariableDeclarationExpr) expr, null);

				} else if (expr instanceof MethodCallExpr) {
					visit((MethodCallExpr) expr, null);
				} else if (expr instanceof FieldAccessExpr) {
					visit((FieldAccessExpr) expr, null);
				} else if (expr instanceof AssignExpr) {
					visit((AssignExpr) expr, null);
				} else {
					System.out.println(expr);
				}
			}
		} catch (Exception exc) {
		}
	}

	protected Resource find_the_resource_by_constructor(String constructor) {
		// code for finding stand alone constructor resource
		Resource targetResource = null;
		try {
			ResIterator iterator = model.listResourcesWithProperty(model
					.createProperty(ns + nodeType));
			while (iterator.hasNext()) {
				Resource item = iterator.nextResource();
				if (item.hasURI(ns + constructor + "()")) {
					targetResource = item;
					break;
				}
			}
		} catch (Exception exc) {
		}
		// returning resource
		return targetResource;
	}

	protected Resource find_the_new_resource_by_className(String constructor) {
		// code for finding resource containing classType
		Resource targetResource = null;
		try {
			Property classNameProperty = model.createProperty(ns + hasName);
			Property constructorProperty = model.createProperty(ns
					+ hasConstructor);
			Property initiatedProperty = model.createProperty(ns + initiatedBy);
			ResIterator resIterator = model
					.listResourcesWithProperty(classNameProperty);
			while (resIterator.hasNext()) {
				Resource item = resIterator.nextResource();
				if (item.hasLiteral(classNameProperty, constructor)) {
					if (!item.hasProperty(constructorProperty)
							&& !item.hasProperty(initiatedProperty)) {
						targetResource = item;
						break;
					}
				}
			}
		} catch (Exception exc) {
		}
		// returning the resource
		return targetResource;
	}

	protected Resource find_the_resource_by_className(String constructor) {
		// code for finding resource containing classType
		Resource targetResource = null;
		try {
			Property classNameProperty = model.createProperty(ns + hasName);
			ResIterator resIterator = model
					.listResourcesWithProperty(classNameProperty);
			while (resIterator.hasNext()) {
				Resource item = resIterator.nextResource();
				if (item.hasLiteral(classNameProperty, constructor)) {
					targetResource = item;
					break;
				}
			}
		} catch (Exception exc) {
		}
		// returning the resource
		return targetResource;
	}

	protected Resource find_the_resource_by_URI(String targetURI) {
		// code for finding the resource
		Resource targetResource = null;
		try {
			ResIterator resIterator = model.listResourcesWithProperty(model
					.createProperty(ns + classType));
			// System.out.println(resIterator.toList().size());
			while (resIterator.hasNext()) {
				Resource item = resIterator.nextResource();
				// System.out.println(item.getURI());
				if (item.hasURI(targetURI.trim())) {
					targetResource = item;
					// System.out.println("Get URI"+item.getURI());
					break;
				}
			}
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
		}
		// returning the resource
		return targetResource;
	}

	protected void clear_temp_resource() {
		// code for clearing temporary variable
		tempResource = null;
	}

	@Override
	public void visit(ObjectCreationExpr objCreateExpr, Object arg) {
		// code for checking object creation expression
		try {
			String constructor = objCreateExpr.getType().toString();
			System.out.print("Constructor: " + constructor + " ");

			// target object
			Resource objectResource = find_the_new_resource_by_className(constructor);
			// constructor object
			Resource constructorResource = model.createResource(ns
					+ constructor + "()");
			// this is a constructor node
			constructorResource.addProperty(
					model.createProperty(ns + nodeType), "Constructor");
			if (objectResource != null) {
				// now create the link
				objectResource.addProperty(
						model.createProperty(ns + hasConstructor),
						constructorResource);
			} else {
				// now this is a stand alone constructor
			}

			List<Expression> params = objCreateExpr.getArgs();
			System.out.print("Parameters:");
			if (params != null) // if it not empty
				for (Expression expr : params) {
					// check if it is a object creattion expression or not
					if (expr instanceof ObjectCreationExpr) {
						explore_the_expression_stmt(new ExpressionStmt(expr));
						// System.out.print(expr+", ");
						Resource constrct2 = find_the_resource_by_constructor(((ObjectCreationExpr) expr)
								.getType().toString());
						constructorResource.addProperty(
								model.createProperty(ns + hasParameter),
								constrct2);
					} else if (expr instanceof FieldAccessExpr) {
						// create parameter link
						explore_the_expression_stmt(new ExpressionStmt(expr));
						// temp resource has got the pointer now
						constructorResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempResource);
					}else if(expr instanceof AssignExpr)
					{
						//create parameter link
						explore_the_expression_stmt(new ExpressionStmt(expr));
						//temp resource has got the pointer now
						constructorResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempResource);
					}
					
					
					else {
						// create parameter link
						Resource paramResource = find_the_resource_by_URI(ns
								+ expr.toString());
						constructorResource.addProperty(
								model.createProperty(ns + hasParameter),
								paramResource);

					}
				}

			// constructor initiated the parent declared variables.
			tempResource = constructorResource;

		} catch (Exception exc) {

		}
	}

	@Override
	public void visit(FieldAccessExpr fieldAccessExpr, Object arg) {
		// code for checking field access expression
		try {
			String myObject = fieldAccessExpr.getScope().toString();
			System.out.print("Scope:" + myObject + ",");
			Resource scopeResource = find_the_resource_by_URI(ns + myObject);
			String field = fieldAccessExpr.getField();
			System.out.println("Field: " + field + " ");
			Resource fieldResource = model.createResource(ns + field);
			fieldResource.addProperty(model.createProperty(ns + nodeType),
					"Attribute");
			scopeResource.addProperty(model.createProperty(ns + hasAttribute),
					fieldResource);
			// assigning temporary resource
			tempResource = fieldResource;
		} catch (Exception exc) {

		}
	}

	@Override
	public void visit(AssignExpr assignExpr, Object arg) {
		// code for checking assignment expression
		try {
			Expression targetExpr = assignExpr.getTarget();
			explore_the_expression_stmt(new ExpressionStmt(targetExpr));
			Resource scopeResource = null;
			if (tempResource != null) {
				scopeResource = tempResource;
				clear_temp_resource();
			}

			System.out.print(" Assigned To: ");
			Expression valueExpression = assignExpr.getValue();
			explore_the_expression_stmt(new ExpressionStmt(valueExpression));
			Resource scopeValue = null;
			if (tempResource != null) {
				scopeValue = tempResource;
				clear_temp_resource();
			}
			if (scopeResource != null && scopeValue != null) {
				// now do the assignment value to scope
				scopeResource.addProperty(
						model.createProperty(ns + initiatedBy), scopeValue);
			}
			
			//lets take out the scopeResource
			tempResource=scopeResource;

		} catch (Exception exc) {
		}
	}

	@Override
	public void visit(MethodCallExpr mCallExpr, Object arg) {
		// code for accessing method call information
		try {

			// scope
			Expression expr = mCallExpr.getScope();
			System.out.print("Scope:" + expr.toString() + " ,");
			// searching for the scope
			Resource scopeResource = find_the_resource_by_URI(ns + expr);
			Resource methodResource = model.createResource(ns
					+ mCallExpr.getName());
			scopeResource.addProperty(model.createProperty(ns + hasMethod),
					methodResource);
			System.out.print("MC: " + mCallExpr.getName() + " ,");
			// parameters
			try {
				List<Expression> exprs = mCallExpr.getArgs();
				System.out.print("Parameters:");
				for (Expression exp1 : exprs) {
					explore_the_expression_stmt(new ExpressionStmt(exp1));
					// now extract the temp resource
					if (tempResource != null) {
						methodResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempResource);
						clear_temp_resource();
					}

				}
			} catch (Exception exc) {
			}
			System.out.println();

			// assigning method resource
			tempResource = methodResource;

		} catch (Exception exc) {
			System.out.println(exc.getMessage());
		}
	}

	@Override
	public void visit(VariableDeclarationExpr vDecExpr, Object arg) {
		// code for accessing method call information
		try {

			// getting type of variables
			Type myType = vDecExpr.getType();
			System.out.print("Type:" + myType.toString() + ",");

			// declaration
			List<VariableDeclarator> vars = vDecExpr.getVars();
			for (VariableDeclarator myDec : vars) {
				String varPart = myDec.toString().split("=")[0].trim();
				System.out.print("Variable: " + varPart + ",");
				Resource varResource = model.createResource(ns + varPart);
				// varResource.addProperty(RDF.type, "Class");
				varResource.addProperty(model.createProperty(ns + hasName),
						myType.toString());
				varResource.addProperty(model.createProperty(ns + classType),
						model.createResource(ns + myType.toString().trim()));
				varResource.addProperty(model.createProperty(ns + nodeType),
						"Class"); // these are class nodes

				try {
					// initialization
					// String InitPart=myDec.toString().split("=")[1].trim();
					Expression InitExpr = myDec.getInit();
					System.out.print("Initiated by:");
					// if it access field or use constructor
					if (InitExpr instanceof ObjectCreationExpr) {
						explore_the_expression_stmt(new ExpressionStmt(InitExpr));
						// Resource
						// constResource=find_the_resource_by_constructor(myType.toString());
						// this is a constructor node
						varResource.addProperty(
								model.createProperty(ns + initiatedBy),
								tempResource);
						clear_temp_resource();
					} else if (InitExpr instanceof FieldAccessExpr) {
						explore_the_expression_stmt(new ExpressionStmt(InitExpr));
						// we got the temporary resource
						varResource.addProperty(
								model.createProperty(ns + initiatedBy),
								tempResource);
						clear_temp_resource();
					} else if (InitExpr instanceof MethodCallExpr) {
						explore_the_expression_stmt(new ExpressionStmt(InitExpr));
						// we got the temporary resource
						varResource.addProperty(
								model.createProperty(ns + initiatedBy),
								tempResource);
						clear_temp_resource();

					} else // or just any literal
					{
						varResource.addProperty(
								model.createProperty(ns + initiatedBy),
								InitExpr.toString());
					}
				} catch (Exception exc) {
				}
			}

			System.out.println();

		} catch (Exception exc) {
		}

	}

}

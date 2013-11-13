package rdfmaker;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import japa.parser.ast.Comment;
import japa.parser.ast.LineComment;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BinaryExpr.Operator;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
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
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import java.io.*;

//code for visiting method
public class MethodVisitor2 extends VoidVisitorAdapter {
	// file counter
	public int apis_occurred = 0;
	public int api_considered = 0;
	public int project_file_counter = 1;
	public int node_id = 0;
	public int currFileNumber = 0;

	// RDF related properties
	Model model = null;
	Resource tempResource = null; // temporary resource pointer
	Resource tempCResource = null;
	Resource tempCstResource = null;
	Resource tempMResource = null;
	Resource tempFResource = null;
	Resource tempAACResource = null; // array access and array creation
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
	String hasInstance = "hasInstance"; // variable initiated by literal
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

	// control block parameters
	String inScopeOf = "inScopeOf"; // Control node

	// common property
	String hasName = "hasName";
	String classType = "classType";
	String startedBy = "startedby";
	String assignedTo = "assignedTo";
	String nodeType = "nodeType";
	String nodeID = "nodeID";
	String hasCondition = "hasCondition";

	// triple serial
	int triple_serial = 0;
	String sequence_info = new String();

	String[] utilClasses = null;
	HashSet<String> apiSet = null;

	HashSet<String> dataTypeSet = null;

	// project file
	String projectFile = null;

	// control block start end
	Stack<Seq> bEndPointNodes = null;
	boolean is_in_the_block = false;

	public MethodVisitor2(String projectDir, int currFileNumber) {
		// creating the model
		// model=ModelFactory.createDefaultModel();
		utilClasses = new String[250]; // string array created..
		apiSet = new HashSet<String>();
		dataTypeSet = new HashSet<String>();
		this.load_the_classes();
		// initializing the stack
		this.bEndPointNodes = new Stack<Seq>();
		// initializing file counter
		this.apis_occurred = 0;
		// assigning project file
		this.projectFile = projectDir;
		// setting file number
		this.currFileNumber = currFileNumber;
	}

	protected void load_the_classes() {
		String util_class_path = "./data/java.util.awt.io.txt";
		try {
			File file = new File(util_class_path);
			Scanner scanner = new Scanner(file);
			int count = 0;
			while (scanner.hasNext()) {
				// utilClasses[count]=scanner.next().trim();
				apiSet.add(scanner.next());
				count++;
			}

			// do not consider the expression having these data types
			dataTypeSet.add("double");
			dataTypeSet.add("long");
			dataTypeSet.add("short");
			dataTypeSet.add("char");
			dataTypeSet.add("int");
			dataTypeSet.add("float");
			dataTypeSet.add("boolean");
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
			exc.printStackTrace();
		}
	}

	protected void print_the_comment(String commentText) {
		// code for creating comments
		LineComment comment = new LineComment(commentText);
		System.out.print(comment);
	}

	protected int get_line_count(BlockStmt block) {
		// code for getting line numbers
		int line_count = 0;
		List<Statement> stmts = block.getStmts();
		for (Statement stmt : stmts) {
			String[] lines = stmt.toString().split("\n");
			line_count += lines.length;
		}
		// returning line count
		return line_count;
	}

	protected boolean analyze_the_desired_API_usage(String methodBody) {
		// code for checking if any API is used here
		boolean used = false;
		try {
			StringTokenizer tokenizer = new StringTokenizer(methodBody);
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (apiSet.contains(token)) {
					used = true;
					break;
				}
			}
		} catch (Exception exc) {
			System.err.println("Failed to analyze the API usage..");
		}
		return used;
	}

	@Override
	public void visit(MethodDeclaration n, Object arg) {
		try {

			// check if it contains the API methods
			if (analyze_the_desired_API_usage(n.getBody().toString())) {
				apis_occurred++;

				// directory name
				String rdfDir = this.projectFile + "/rdf";
				String javaDir = this.projectFile + "/java";
				String tripleDir = this.projectFile + "/sequence";

				// method name
				// System.out.println("Function Exploring:" + n.getName());
				System.out.println();
				System.out.println("Entering the method: " + n.getName());
				// fileNames
				String rdf_file_name = rdfDir + "/" + this.currFileNumber
						+ ".rdf";
				String java_file_name = javaDir + "/" + this.currFileNumber
						+ ".java";
				String sequence_file_name = tripleDir + "/"
						+ this.currFileNumber + ".nt";
				System.out.println("==============================");

				// create the model
				if (this.model == null)
					model = ModelFactory.createDefaultModel();

				// initializing node id
				node_id = 0;
				// clearing temporary resource
				clear_temp_resource();

				// explore method parameters
				List<Parameter> params = n.getParameters();
				// explore the declared method parameters
				explore_declaration_method_parameters(params);
				// method body
				BlockStmt block = n.getBody();

				// create root resource
				root_resource = model.createResource(ns + "program");
				present_root = root_resource; // representative of root

				// explore it
				explore_the_block(block);

				// writing the model and source to the files
				try {

					int block_line_count = get_line_count(block);
					// apply some thresholds
					if (block_line_count > 3 && block_line_count < 20) {

						// actually considered
						api_considered++;

						File javafile = new File(java_file_name);
						FileWriter writer = new FileWriter(java_file_name);
						writer.write(n + "\n");
						// writer.write(block.toString());
						writer.close();

						File rdffile = new File(rdf_file_name);
						// showing the model
						model.write(new FileOutputStream(rdf_file_name));

						// also showing in console
						System.out.println(n);
						model.write(System.out);

						// destroying the model
						this.model = null;

						// creating the sequence file
						File seqfile = new File(sequence_file_name);
						FileWriter seqWriter = new FileWriter(seqfile);
						seqWriter.write(sequence_info.trim());
						seqWriter.close();
						sequence_info = new String();
						triple_serial = 0;

						System.out.println("==============================");
						System.out.println("Successfully wrote : "
								+ n.getName());

						// updating last file number
						this.currFileNumber++;

						// incrementing counter
						project_file_counter++;
					}

				} catch (Exception exc) {
					System.err.println("Cant write to file: "
							+ exc.getMessage());
				}

			}// end of if
		} catch (Exception exc) {
			System.err.println("Failed to create the model -"
					+ exc.getMessage());
			exc.printStackTrace();
		}
	}

	public void visit(ConstructorDeclaration n, Object args) {
		// code for handling constructor declaration
		// check if it contains the API methods
		if (analyze_the_desired_API_usage(n.getBlock().toString())) {
			apis_occurred++;
			// directory name
			String rdfDir = this.projectFile + "/rdf";
			String javaDir = this.projectFile + "/java";
			String tripleDir = this.projectFile + "/sequence";

			// method name
			// System.out.println("Function Exploring:" + n.getName());
			System.out.println();
			System.out.println("Entering the method: " + n.getName());
			// fileNames
			String rdf_file_name = rdfDir + "/" + this.currFileNumber + ".rdf";
			String java_file_name = javaDir + "/" + this.currFileNumber
					+ ".java";
			String sequence_file_name = tripleDir + "/" + this.currFileNumber
					+ ".nt";
			System.out.println("==============================");

			// create the model
			if (model == null)
				model = ModelFactory.createDefaultModel();

			// initializing node id
			node_id = 0;
			// clearing temporary resource
			clear_temp_resource();

			// explore method parameters
			List<Parameter> params = n.getParameters();
			// explore the declared constructor parameters
			explore_declaration_method_parameters(params);

			// method body
			BlockStmt block = n.getBlock();

			// create root resource
			root_resource = model.createResource(ns + "program");
			present_root = root_resource; // representative of root

			// explore it
			explore_the_block(block);

			// writing the model and source to the files
			try {

				int block_line_count = get_line_count(block);
				// apply some thresholds
				if (block_line_count > 3 && block_line_count < 20) {

					// APIs written
					api_considered++;

					File javafile = new File(java_file_name);
					FileWriter writer = new FileWriter(java_file_name);
					// writer.write(n.getBody() + "\n");
					writer.write(block.toString());
					writer.close();

					File rdffile = new File(rdf_file_name);
					// showing the model
					model.write(new FileOutputStream(rdf_file_name));

					System.out.println(n);
					model.write(System.out);

					// destroying the model
					model.close();
					
					
					// creating the sequence file
					File seqfile = new File(sequence_file_name);
					FileWriter seqWriter = new FileWriter(seqfile);
					seqWriter.write(sequence_info.trim());
					seqWriter.close();
					sequence_info = new String();
					triple_serial = 0;
					
					System.out.println("==============================");
					System.out.println("Successfully wrote: " + n.getName());
					// updating last file number
					this.currFileNumber++;

					// incrementing counter
					project_file_counter++;
				}

			} catch (Exception exc) {
				System.err.println("Cant write to file: " + exc.getMessage());
			}
		}// end of if
	}

	protected void record_triple_serial(String subject, String predicate,
			String object) {
		// code for recording triple serial
		++triple_serial;
		String triple="";
		if(object.startsWith("http://"))
		triple = "<" + subject + "> <" + predicate + "> <" + object
				+ "> .";
		else triple = "<" + subject + "> <" + predicate + "> \"" + object
				+ "\" .";
		// adding the triple
		sequence_info += triple+"\n";

        Resource pitResource = model.createResource(ns + "POINT_IN_TIME_" + triple_serial);

        pitResource.addProperty(model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), ns+"POINT_IN_TIME");
        pitResource.addProperty(model.createProperty(ns+"pit_index"), ""+triple_serial);
        pitResource.addProperty(model.createProperty(ns+"code"), subject);
        pitResource.addProperty(model.createProperty(predicate), object);
	}

	protected void explore_declaration_method_parameters(List<Parameter> params) {
		// code for exploring method parameters

		if (params == null || params.size() == 0)
			return;
		// List<Parameter> params=n.getParameters();
		for (Parameter param : params) {
			Type myType = param.getType();
			VariableDeclaratorId varID = param.getId();
			// creating nodes for the class.
			if (!dataTypeSet.contains(myType.toString())) {
				Resource typeResource = model.createResource(ns
						+ myType.toString());
				typeResource.addProperty(model.createProperty(ns + nodeType),
						"Class");
				// record the triple
				record_triple_serial(typeResource.toString(), ns + nodeType,
						"Class");

				if (!typeResource
						.hasProperty(model.createProperty(ns + nodeID)))
					typeResource.addProperty(model.createProperty(ns + nodeID),
							++node_id + ""); // adding node id
				// now add the instance also
				typeResource.addProperty(
						model.createProperty(ns + hasInstance),
						varID.toString());
				// record the triple
				record_triple_serial(typeResource.toString(), ns + hasInstance,
						varID.toString());
			}
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
					create_block_endpoint_nodes("try", "start");
					// now go deeper
					BlockStmt b = ((TryStmt) stmt).getTryBlock();
					explore_the_block(b);
					print_the_comment("End: Try Block");
					create_block_endpoint_nodes("try", "end");
				}
				// looping
				if (stmt instanceof ForeachStmt) {

					print_the_comment("Start: ForEach Iterable");
					create_block_endpoint_nodes("foreach", "start");
					// condition expression
					Expression condition = ((ForeachStmt) stmt).getIterable();
					System.err.println(condition);
					explore_condition_expression(condition, "foreach");
					print_the_comment("End: ForEach Iterable");

					print_the_comment("Start: ForEach Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((ForeachStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("Start: ForEach Body");
					create_block_endpoint_nodes("foreach", "end");
				}
				if (stmt instanceof ForStmt) {

					print_the_comment("Start: For Condition");
					create_block_endpoint_nodes("for", "start");
					// condition
					Expression condition = ((ForStmt) stmt).getCompare();
					System.err.println(condition);
					explore_condition_expression(condition, "for");
					print_the_comment("End: For Condition");

					print_the_comment("Start: For Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((ForStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("End: For Body");
					create_block_endpoint_nodes("for", "end");
				}
				if (stmt instanceof WhileStmt) {

					print_the_comment("Start: While Condition");
					create_block_endpoint_nodes("while", "start");
					// condition
					Expression condition = ((WhileStmt) stmt).getCondition();
					explore_condition_expression(condition, "while");
					print_the_comment("End: While Condition");
					print_the_comment("Start: While Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((WhileStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("End: While Body");
					create_block_endpoint_nodes("while", "end");
				}
				if (stmt instanceof DoStmt) {

					print_the_comment("Start: Do Body");
					// go deeper
					BlockStmt b = (BlockStmt) ((DoStmt) stmt).getBody();
					explore_the_block(b);
					print_the_comment("End: Do Body");
					create_block_endpoint_nodes("do", "end");

					print_the_comment("Start: Do Condition");
					create_block_endpoint_nodes("do", "start");
					// condition
					Expression condition = ((DoStmt) stmt).getCondition();
					explore_condition_expression(condition, "do");
					print_the_comment("End: Do Condition");
				}

				// control statement
				if (stmt instanceof IfStmt) {
					// access the condition
					print_the_comment("Start: If Condition");
					create_block_endpoint_nodes("if", "start");
					Expression condition = ((IfStmt) stmt).getCondition();
					explore_condition_expression(condition, "if");
					print_the_comment("End: If Condition");

					print_the_comment("Start: If-Then Block");
					// go deeper
					BlockStmt b1 = (BlockStmt) ((IfStmt) stmt).getThenStmt();
					explore_the_block(b1);
					print_the_comment("End: If-Then Block");
					create_block_endpoint_nodes("if", "end");
					try {

						BlockStmt b2 = (BlockStmt) ((IfStmt) stmt)
								.getElseStmt();
						if (b2 != null) {
							print_the_comment("Start: Else Block");
							create_block_endpoint_nodes("else", "start");
							explore_the_block(b2);
							print_the_comment("End: Else Block");
							create_block_endpoint_nodes("else", "end");
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
			System.err.println("Failed to explore the block -"
					+ exc.getMessage());
		}
	}

	protected void explore_condition_expression(Expression expr,
			String controlName) {
		// code for handling branching or controlling expression
		try {
			// initiating the temporary variables
			tempFResource = null;
			tempMResource = null;
			tempCResource = null;
			// check if it is an expression
			explore_the_expression_stmt(new ExpressionStmt(expr));
			// we should get the temporary resource
			if (tempFResource != null || tempMResource != null
					|| tempCResource != null) {
				String controlResURI = ns + controlName;
				Seq controlResource = (Seq) find_the_resource_by_URI(controlResURI);
				if (!controlResource.hasProperty(model.createProperty(ns
						+ hasCondition))) {
					if (tempFResource != null) {
						int index = controlResource.indexOf(tempFResource);
						controlResource.remove(index);
						controlResource.addProperty(
								model.createProperty(ns + hasCondition),
								tempFResource);
						// record the triple
						record_triple_serial(controlResource.toString(), ns
								+ hasCondition, tempFResource.toString());
					}
					if (tempMResource != null) {
						int index = controlResource.indexOf(tempMResource);
						controlResource.remove(index);
						controlResource.addProperty(
								model.createProperty(ns + hasCondition),
								tempMResource);
						// record the triple
						record_triple_serial(controlResource.toString(), ns
								+ hasCondition, tempMResource.toString());
					}
					if (tempCResource != null) {
						int index = controlResource.indexOf(tempCResource);
						controlResource.remove(index);
						controlResource.addProperty(
								model.createProperty(ns + hasCondition),
								tempCResource);
						// record the triple
						record_triple_serial(controlResource.toString(), ns
								+ hasCondition, tempCResource.toString());
					}
				}
			}
		} catch (Exception exc) {
		}
	}

	protected void create_block_endpoint_nodes(String nodeName, String endPoint) {
		// code for writing block end points
		try {
			// creating end point resource
			if (endPoint.startsWith("start")) {
				Seq endpointSeq = model.createSeq(ns + nodeName);
				endpointSeq.addProperty(model.createProperty(ns + nodeType),
						"Control");
				// record the triple
				record_triple_serial(endpointSeq.toString(), ns + nodeType,
						"Control");

				if (!endpointSeq.hasProperty(model.createProperty(ns + nodeID)))
					endpointSeq.addProperty(model.createProperty(ns + nodeID),
							++node_id + "");

				// checking if it is a sub item
				if (!bEndPointNodes.isEmpty()) {
					Seq seqParent = bEndPointNodes.peek();
					if (seqParent != null && this.is_in_the_block) {
						// add this seq as a child
						handle_statement_in_the_block(endpointSeq);
					}
				}
				// a control block is started
				bEndPointNodes.push(endpointSeq);
				this.is_in_the_block = true;
			} else if (endPoint.startsWith("end")) {
				// a control block ended
				bEndPointNodes.pop();
				if (bEndPointNodes.empty())
					this.is_in_the_block = false;
			}
		} catch (Exception exc) {
			System.err.println(exc.getMessage());
		}
	}

	protected void handle_statement_in_the_block(Resource stmtResource) {
		// code handling the statements inside the control loop
		if (this.is_in_the_block) {
			// get the top resource
			Seq controlNode = this.bEndPointNodes.peek();
			// Property scopeProperty=model.createProperty(ns+inScopeOf);
			// stmtResource.addProperty(scopeProperty, controlNode);
			if (!controlNode.contains(stmtResource)) {
				controlNode.add(stmtResource);
			}
			// if(stmtResource!=conditionResource)
			// again push the top element
			// this.bEndPointNodes.push(controlNode);
		}
	}

	public void explore_the_expression_stmt(ExpressionStmt stmt) {
		// handle the expression statement
		try {
			if (stmt instanceof ExpressionStmt) {
				Expression expr = ((ExpressionStmt) stmt).getExpression();

				int already_found = 0;

				// expression related to OOP
				if (expr instanceof ObjectCreationExpr) {
					already_found = 1;
					visit((ObjectCreationExpr) expr, null);
				} else if (expr instanceof VariableDeclarationExpr) {
					already_found = 1;
					visit((VariableDeclarationExpr) expr, null);
				} else if (expr instanceof MethodCallExpr) {
					already_found = 1;
					visit((MethodCallExpr) expr, null);
				} else if (expr instanceof FieldAccessExpr) {
					already_found = 1;
					visit((FieldAccessExpr) expr, null);

				} else {
					already_found = 0;
					clear_temp_resource();
				}

				// expression related to arithmetic
				if (already_found == 0)
					explore_arithmetic_expression(expr);

				// handling if it is inside a block
				try {
					// add all the nodes inside the scope
					if (tempCResource != null)
						handle_statement_in_the_block(tempCResource);
					if (tempFResource != null)
						handle_statement_in_the_block(tempFResource);
					if (tempCstResource != null)
						handle_statement_in_the_block(tempCstResource);
					if (tempMResource != null)
						handle_statement_in_the_block(tempMResource);
					if (tempAACResource != null)
						handle_statement_in_the_block(tempAACResource);
				} catch (Exception exc) {
				}
			}
		} catch (Exception exc) {
		}
	}

	protected void explore_arithmetic_expression(Expression expr) {
		// code for handling arithmetic expression
		try {
			if (expr instanceof AssignExpr) {
				visit((AssignExpr) expr, null);
			} else if (expr instanceof InstanceOfExpr) {
				visit((InstanceOfExpr) expr, null);
			} else if (expr instanceof ArrayCreationExpr) {
				visit((ArrayCreationExpr) expr, null);
			} else if (expr instanceof ArrayAccessExpr) {
				visit((ArrayAccessExpr) expr, null);
			} else if (expr instanceof BinaryExpr) {
				visit((BinaryExpr) expr, null);
			} else if (expr instanceof EnclosedExpr) {
				visit((EnclosedExpr) expr, null);
			} else {
				// System.out.println(expr);
				// if nothing is found...clear previous temp variables.
				clear_temp_resource();
			}
		} catch (Exception exc) {

		}

	}

	protected Resource find_the_resource_by_constructor(String constructor) {
		// code for finding stand alone constructor resource
		Resource targetResource = null;
		try {
			ResIterator iterator = model.listResourcesWithProperty(
					model.createProperty(ns + nodeType), "Constructor");
			while (iterator.hasNext()) {
				Resource item = iterator.nextResource();
				if (item.hasURI(ns + constructor + ".new")) {
					targetResource = item;
					break;
				}
			}
		} catch (Exception exc) {
		}
		// returning resource
		return targetResource;
	}

	protected Resource find_the_new_resource_by_className(String className) {
		// code for finding resource containing classType
		Resource targetResource = null;
		try {

			Property constructorProperty = model.createProperty(ns
					+ hasConstructor);
			Property initiatedProperty = model.createProperty(ns + initiatedBy);
			ResIterator iterator = model.listResourcesWithProperty(
					model.createProperty(ns + nodeType), "Class");
			while (iterator.hasNext()) {
				Resource item = iterator.nextResource();
				if (item.hasURI(ns + className)) {
					if (!item.hasProperty(initiatedProperty)
							&& !item.hasProperty(constructorProperty)) {
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

	protected Resource find_the_resource_by_instance(String instance) {
		// code for getting resources with instance
		Resource targetResource = null;
		try {
			ResIterator iter1 = model.listResourcesWithProperty(
					model.createProperty(ns + hasInstance), instance);
			while (iter1.hasNext()) {
				targetResource = iter1.nextResource();
				break;
			}
		} catch (Exception exc) {
		}
		return targetResource;
	}

	protected Resource find_the_resource_by_URI(String targetURI) {
		// code for finding the resource
		Resource targetResource = null;
		try {
			ResIterator resIterator = model.listResourcesWithProperty(model
					.createProperty(ns + nodeType));
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

	protected Resource find_the_condition_resource(Seq seq) {
		// code for finding the condition resource
		Resource targetResource = null;
		try {
			Property conditionProp = model.createProperty(ns + hasCondition);
			StmtIterator stmts = seq.listProperties(conditionProp);
			while (stmts.hasNext()) {
				targetResource = stmts.next().getResource();
				break;
				// got the resouce containing condition statement
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
		tempCResource = null;
		tempFResource = null;
		tempCstResource = null;
		tempMResource = null;
		tempAACResource = null;
	}

	@Override
	public void visit(ObjectCreationExpr objCreateExpr, Object arg) {
		// code for checking object creation expression
		try {
			String className = objCreateExpr.getType().toString();
			// System.out.print("Constructor: " + className + " ");

			// target object
			Resource objectResource = find_the_new_resource_by_className(className);
			// constructor object
			Resource constructorResource = model.createResource(ns + className
					+ ".new");
			// this is a constructor node
			constructorResource.addProperty(
					model.createProperty(ns + nodeType), "Constructor");
			// recording triple
			record_triple_serial(constructorResource.toString(), ns + nodeType,
					"Constructor");

			// adding node id
			if (!constructorResource.hasProperty(model.createProperty(ns
					+ nodeID)))
				constructorResource.addProperty(
						model.createProperty(ns + nodeID), ++node_id + "");
			// linking with object
			if (objectResource != null) {
				// now create the link
				objectResource.addProperty(
						model.createProperty(ns + hasConstructor),
						constructorResource);
				// recording triple
				record_triple_serial(objectResource.toString(), ns
						+ hasConstructor, constructorResource.toString());

			}

			try {
				// constructor parameters
				List<Expression> params = objCreateExpr.getArgs();
				// System.out.print("Parameters:");
				if (params != null) // if it not empty
					for (Expression expr : params) {

						// check if it is a object creation expression or not
						if (expr instanceof ObjectCreationExpr) {
							explore_the_expression_stmt(new ExpressionStmt(expr));
							// System.out.print(expr+", ");
							Resource constrct2 = find_the_resource_by_constructor(((ObjectCreationExpr) expr)
									.getType().toString());
							constructorResource.addProperty(
									model.createProperty(ns + hasParameter),
									constrct2);
							// recording triple
							record_triple_serial(
									constructorResource.toString(), ns
											+ hasParameter,
									constrct2.toString());

						} else if (expr instanceof FieldAccessExpr) {
							// create parameter link
							explore_the_expression_stmt(new ExpressionStmt(expr));
							// temp resource has got the pointer now
							if (tempFResource != null) {
								constructorResource
										.addProperty(
												model.createProperty(ns
														+ hasParameter),
												tempFResource);
								// recording triple
								record_triple_serial(
										constructorResource.toString(), ns
												+ hasParameter,
										tempFResource.toString());
							}
						} else if (expr instanceof AssignExpr) {
							// create parameter link
							explore_the_expression_stmt(new ExpressionStmt(expr));
							// temp resource has got the pointer now
							constructorResource.addProperty(model
									.createProperty(ns + hasParameter),
									tempFResource != null ? tempFResource
											: tempCResource);
							// recording triple
							record_triple_serial(
									constructorResource.toString(), ns
											+ hasParameter,
									(tempFResource != null ? tempFResource
											: tempCResource).toString());
							// clearing temp resource
							clear_temp_resource();
						} else {
							// create parameter link
							Resource paramResource = find_the_resource_by_instance(expr
									.toString());
							// if(paramResource==null)paramResource=model.createResource(ns+expr);
							constructorResource.addProperty(
									model.createProperty(ns + hasParameter),
									paramResource);
							// recording triple
							record_triple_serial(
									constructorResource.toString(), ns
											+ hasParameter,
									paramResource.toString());
						}
					}
			} catch (Exception exc) {
			}

			// constructor initiated the parent declared variables.
			tempCstResource = constructorResource;

		} catch (Exception exc) {

			System.err.println("Failed to explore Objectcreation Expr");
		}
	}

	@Override
	public void visit(FieldAccessExpr fieldAccessExpr, Object arg) {
		// code for checking field access expression
		try {
			String myObject = fieldAccessExpr.getScope().toString();
			// System.out.print("Scope:" + myObject + ",");
			Resource scopeResource = find_the_resource_by_instance(myObject);

			if (scopeResource == null) {
				// assuming its not a data type
				if (!dataTypeSet.contains(myObject)) {

					// assuming the scope starts with a upper case letter, means
					// a class
					if (!Character.isLowerCase(myObject.charAt(0))) {
						scopeResource = model.createResource(ns + myObject);
						scopeResource.addProperty(
								model.createProperty(ns + nodeType), "Class");
						// recording triple
						record_triple_serial(scopeResource.toString(), ns
								+ nodeType, "Class");
						// adding node id
						if (!scopeResource.hasProperty(model.createProperty(ns
								+ nodeID)))
							scopeResource.addProperty(
									model.createProperty(ns + nodeID),
									++node_id + "");
						// System.out.println("i am here");
					}
				}
			}

			String field = fieldAccessExpr.getField();
			// System.out.println("Field: " + field + " ");
			Resource fieldResource = model.createResource(ns
					+ scopeResource.getLocalName() + "." + field);
			// adding properties
			fieldResource.addProperty(model.createProperty(ns + nodeType),
					"Attribute");
			// recording triple
			record_triple_serial(fieldResource.toString(), ns + nodeType,
					"Attribute");
			// adding node id
			if (!fieldResource.hasProperty(model.createProperty(ns + nodeID)))
				fieldResource.addProperty(model.createProperty(ns + nodeID),
						++node_id + "");
			// hasAttribute
			scopeResource.addProperty(model.createProperty(ns + hasAttribute),
					fieldResource);
			// recording triple
			record_triple_serial(scopeResource.toString(), ns + hasAttribute,
					fieldResource.toString());
			// assigning temporary resource
			tempFResource = fieldResource;
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
			if (tempFResource != null || tempCResource != null) {
				scopeResource = (tempFResource != null ? tempFResource
						: tempCResource);
				clear_temp_resource();
			} else {
				// consider finding by resource search
				scopeResource = find_the_resource_by_instance(targetExpr
						.toString());
			}
			// System.out.print(" Assigned To: ");
			Expression valueExpression = assignExpr.getValue();
			explore_the_expression_stmt(new ExpressionStmt(valueExpression));
			Resource scopeValue = null;
			if (tempFResource != null || tempMResource != null
					|| tempCstResource != null) {
				if (tempFResource != null)
					scopeValue = tempFResource;
				else if (tempMResource != null)
					scopeValue = tempMResource;
				else
					scopeValue = tempCstResource;
				clear_temp_resource();
			} else {
				// finding the resource directly
				scopeValue = find_the_resource_by_instance(valueExpression
						.toString());
			}
			if (scopeResource != null) {
				// now do the assignment value to scope
				scopeResource.addProperty(
						model.createProperty(ns + assignedTo), scopeValue);
				// recording triple
				record_triple_serial(scopeResource.toString(), ns + assignedTo,
						scopeValue.toString());
				// hasProperty
				if (!scopeResource.hasProperty(model
						.createProperty(ns + nodeID)))
					scopeResource.addProperty(
							model.createProperty(ns + nodeID), ++node_id + "");
			}
			// lets take out the scopeResource
			if (scopeResource.hasProperty(model.createProperty(ns + classType),
					"Attribute"))
				tempFResource = scopeResource;
			else
				tempCResource = scopeResource;

		} catch (Exception exc) {
		}
	}

	@Override
	public void visit(EnclosedExpr encExpr, Object arg) {
		// get inner
		Expression inner = encExpr.getInner();
		// now explore the expression
		explore_the_expression_stmt(new ExpressionStmt(inner));
	}

	@Override
	public void visit(BinaryExpr binaryExpr, Object arg) {
		// code for checking binary expression
		try {
			Expression leftExpr = binaryExpr.getLeft();
			explore_the_expression_stmt(new ExpressionStmt(leftExpr));
			Resource scopeResource = null;
			if (tempFResource != null || tempCResource != null) {
				scopeResource = (tempFResource != null ? tempFResource
						: tempCResource);
				clear_temp_resource();
			} else {
				// finding by search
				scopeResource = find_the_resource_by_instance(leftExpr
						.toString());
			}

			// System.out.print(" Assigned To: ");
			Expression rightExpression = binaryExpr.getRight();
			explore_the_expression_stmt(new ExpressionStmt(rightExpression));
			Resource scopeValue = null;
			if (tempFResource != null || tempMResource != null
					|| tempCstResource != null) {
				if (tempFResource != null)
					scopeValue = tempFResource;
				else if (tempMResource != null)
					scopeValue = tempMResource;
				else
					scopeValue = tempCstResource;
				clear_temp_resource();
			} else {
				// finding by search
				scopeValue = find_the_resource_by_instance(rightExpression
						.toString());
			}

			// getting the binary operator
			Operator binOpr = binaryExpr.getOperator();

			if (scopeResource != null && scopeValue != null) {
				// now do the assignment value to scope
				scopeResource.addProperty(model.createProperty(ns + binOpr),
						scopeValue);
				// recording triple
				record_triple_serial(scopeResource.toString(), ns + binOpr,
						scopeValue.toString());
				// hasProperty
				if (!scopeResource.hasProperty(model
						.createProperty(ns + nodeID)))
					scopeResource.addProperty(
							model.createProperty(ns + nodeID), ++node_id + "");
			}

			// lets take out the scopeResource
			if (scopeResource.hasProperty(model.createProperty(ns + classType),
					"Attribute"))
				tempFResource = scopeResource;
			else
				tempCResource = scopeResource;

		} catch (Exception exc) {
		}
	}

	@Override
	public void visit(ArrayCreationExpr arrayCreationExpr, Object arg) {
		// code for overriding array creation
		Type myType = arrayCreationExpr.getType();
		if (!dataTypeSet.contains(myType.toString())) {
			Resource typeResource = find_the_new_resource_by_className(myType
					.toString());
			Resource constructorResource = model.createResource(ns + myType
					+ ".new");
			// this is a constructor node
			constructorResource.addProperty(
					model.createProperty(ns + nodeType), "Constructor");
			// recording triple
			record_triple_serial(constructorResource.toString(), ns + nodeType,
					"Constructor");

			// adding node id
			if (!constructorResource.hasProperty(model.createProperty(ns
					+ nodeID)))
				constructorResource.addProperty(
						model.createProperty(ns + nodeID), ++node_id + "");
			// linking with object
			if (typeResource != null) {
				// now create the link
				typeResource.addProperty(
						model.createProperty(ns + hasConstructor),
						constructorResource);
				// recording triple
				record_triple_serial(typeResource.toString(), ns
						+ hasConstructor, constructorResource.toString());
			}
			// clearing others
			clear_temp_resource();
			tempAACResource = constructorResource;
		}

	}

	@Override
	public void visit(ArrayAccessExpr arrayAccessExpr, Object arg) {
		// getting reference of the element accessing array elements
		Expression arrayName = arrayAccessExpr.getName();
		visit((FieldAccessExpr) arrayName, null);
		Resource myTemp = null;
		if (tempFResource != null) {
			myTemp = tempFResource;
		} else {
			myTemp = find_the_resource_by_instance(arrayName.toString());
		}
		clear_temp_resource();
		tempAACResource = myTemp;
	}

	@Override
	public void visit(InstanceOfExpr instanceOfExpr, Object arg) {
		// code for checking instance of expression
		Type myType = instanceOfExpr.getType();
		Expression expr = instanceOfExpr.getExpr();
		Resource typeResource = find_the_new_resource_by_className(myType
				.toString());
		if (typeResource != null) {
			// adding the instance
			typeResource.addProperty(model.createProperty(ns + hasInstance),
					expr.toString());
			// recording triple
			record_triple_serial(typeResource.toString(), ns + hasInstance,
					expr.toString());

		} else {
			// creating and adding instance
			typeResource = model.createResource(ns + myType);
			// node id
			if (!typeResource.hasProperty(model.createProperty(ns + nodeID)))
				typeResource.addProperty(model.createProperty(ns + nodeID),
						++node_id + "");
			// has Instance
			typeResource.addProperty(model.createProperty(ns + hasInstance),
					expr.toString());
			// recording triple
			record_triple_serial(typeResource.toString(), ns + hasInstance,
					expr.toString());
		}

		// assigning class resource
		tempCResource = typeResource;
	}

	@Override
	public void visit(MethodCallExpr mCallExpr, Object arg) {
		// code for accessing method call information
		try {

			// scope
			Expression expr = mCallExpr.getScope();
			// System.out.println(mCallExpr);
			// searching for the scope: The class node itself
			Resource scopeResource = find_the_resource_by_instance(expr
					.toString().trim());
			// getting class name
			String scopeClassName = scopeResource.getLocalName();
			Resource methodResource = model.createResource(ns + scopeClassName
					+ "." + mCallExpr.getName());
			// node type
			methodResource.addProperty(model.createProperty(ns + nodeType),
					"Method");
			// recording triple
			record_triple_serial(methodResource.toString(), ns + nodeType,
					"Method");
			// node id
			if (!methodResource.hasProperty(model.createProperty(ns + nodeID)))
				methodResource.addProperty(model.createProperty(ns + nodeID),
						++node_id + "");
			// adding method as property
			scopeResource.addProperty(model.createProperty(ns + hasMethod),
					methodResource);
			// recording triple
			record_triple_serial(scopeResource.toString(), ns + hasMethod,
					methodResource.toString());
			// System.out.print("MC: " + mCallExpr.getName() + " ,");
			// parameters
			try {
				List<Expression> exprs = mCallExpr.getArgs();
				// System.out.print("Parameters:");
				for (Expression exp1 : exprs) {
					explore_the_expression_stmt(new ExpressionStmt(exp1));
					// now extract the temporary C resource
					if (tempCResource != null) {
						methodResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempCResource);
						// recording triple
						record_triple_serial(methodResource.toString(), ns
								+ hasParameter, tempCResource.toString());
						clear_temp_resource();
					} else if (tempCstResource != null) {
						methodResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempCstResource);
						// recording triple
						record_triple_serial(methodResource.toString(), ns
								+ hasParameter, tempCstResource.toString());
						clear_temp_resource();
					}
					// now extract the temporary F resource
					else if (tempFResource != null) {
						methodResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempFResource);
						// recording triple
						record_triple_serial(methodResource.toString(), ns
								+ hasParameter, tempFResource.toString());
						clear_temp_resource();
					}
					// now extract the temporary M resources
					else if (tempMResource != null) {
						methodResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempMResource);
						// recording triple
						record_triple_serial(methodResource.toString(), ns
								+ hasParameter, tempMResource.toString());
						clear_temp_resource();
					} else if (tempAACResource != null) {
						methodResource.addProperty(
								model.createProperty(ns + hasParameter),
								tempAACResource);
						// recording triple
						record_triple_serial(methodResource.toString(), ns
								+ hasParameter, tempAACResource.toString());
						clear_temp_resource();
					}
				}
			} catch (Exception exc) {
			}

			// System.out.println();
			// assigning method resource
			tempMResource = methodResource;

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
			Resource typeResource = null;
			if (!dataTypeSet.contains(myType)) {
				// its not a data type node
				// System.out.print("Type:" + myType.toString() + ",");
				typeResource = find_the_new_resource_by_className(myType
						.toString());
				if (typeResource == null) {
					// getting the type
					typeResource = model.createResource(ns + myType.toString());
					// adding class type
					typeResource.addProperty(
							model.createProperty(ns + nodeType), "Class");
					// updating the sequence info
					record_triple_serial(typeResource.toString(),
							ns + nodeType, "Class");
					// adding node id
					if (!typeResource.hasProperty(model.createProperty(ns
							+ nodeID)))
						typeResource.addProperty(
								model.createProperty(ns + nodeID), ++node_id
										+ "");
				}
			}

			// declaration
			List<VariableDeclarator> vars = vDecExpr.getVars();
			for (VariableDeclarator myDec : vars) {

				// adding the instance
				String varPart = myDec.toString().split("=")[0].trim();
				if (varPart != null) {
					typeResource.addProperty(
							model.createProperty(ns + hasInstance), varPart);
					// updating the sequence info
					record_triple_serial(typeResource.toString(), ns
							+ hasInstance, varPart);
				} else {
					typeResource.addProperty(
							model.createProperty(ns + hasInstance),
							myDec.toString());
					// updating the sequence info
					record_triple_serial(typeResource.toString(), ns
							+ hasInstance, varPart);
				}

				// handling initialization
				try {
					// initialization
					// String InitPart=myDec.toString().split("=")[1].trim();
					Expression InitExpr = myDec.getInit();
					// System.out.print("Initiated by:");
					// if it access field or use constructor
					if (InitExpr instanceof ObjectCreationExpr) {
						explore_the_expression_stmt(new ExpressionStmt(InitExpr));
						// Resource
						// constResource=find_the_resource_by_constructor(myType.toString());
						// this is a constructor node
						typeResource.addProperty(
								model.createProperty(ns + initiatedBy),
								tempCstResource);
						// recording triple
						record_triple_serial(typeResource.toString(), ns
								+ initiatedBy, tempCstResource.toString());
						clear_temp_resource();
					} else if (InitExpr instanceof FieldAccessExpr) {
						explore_the_expression_stmt(new ExpressionStmt(InitExpr));
						// we got the temporary resource
						typeResource.addProperty(
								model.createProperty(ns + initiatedBy),
								tempFResource);
						// recording triple
						record_triple_serial(typeResource.toString(), ns
								+ initiatedBy, tempFResource.toString());
						clear_temp_resource();
					} else if (InitExpr instanceof MethodCallExpr) {
						explore_the_expression_stmt(new ExpressionStmt(InitExpr));
						// we got the temporary resource
						typeResource.addProperty(
								model.createProperty(ns + initiatedBy),
								tempMResource);
						// recording triple
						record_triple_serial(typeResource.toString(), ns
								+ initiatedBy, tempMResource.toString());
						clear_temp_resource();
					} else if (InitExpr instanceof ArrayCreationExpr) {
						explore_the_expression_stmt(new ExpressionStmt(InitExpr));
						// array creation statement
						typeResource.addProperty(
								model.createProperty(ns + initiatedBy),
								tempAACResource);
						// recording triple
						record_triple_serial(typeResource.toString(), ns
								+ initiatedBy, tempAACResource.toString());
						clear_temp_resource();
					} else // or just any literal
					{
						typeResource.addProperty(
								model.createProperty(ns + initiatedBy),
								InitExpr.toString());
						// recording triple
						record_triple_serial(typeResource.toString(), ns
								+ initiatedBy, InitExpr.toString());
					}
				} catch (Exception exc) {
				}
			}
			System.out.println();

		} catch (Exception exc) {
		}

	}

}

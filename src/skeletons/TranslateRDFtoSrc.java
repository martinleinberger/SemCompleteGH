
//code for translating a RDF file into java source
//Author: Masudur Rahman

package skeletons;
import java.io.File;
import java.io.FileInputStream;

import com.google.common.base.Predicate;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class TranslateRDFtoSrc {

	/**
	 * @param args
	 */
	
	/**
	 * @param args
	 */
	Model model=null;
	String ns = "http://www.semcomplete.com#";
	int node_traversed=0;
	
	//classNode properties
	String hasMethod = "hasMethod"; // Method
	String hasConstructor = "hasConstructor"; // Method
	String hasAttribute = "hasAttribute"; // Attribute
	String initiatedBy = "varInitiatedBy"; // variable initiated by literal
	String hasIntance = "hasInstance"; // variable initiated by literal
	
	//attribute
	//String isAttributeOf = "isAttributeOf"; // Class Object
	
	//method
	//String hasReturnType = "hasReturnType"; // Class Object or built-in
	//String isMethodOf = "isMethodOf"; // Class Object
	String hasParameter = "hasParameter"; // Parameter
	
	//control
	String hasConidtion="hasCondition"; //hasCondition
	
	//parameter
	//String isParamterOf = "isParameterOf"; // Method
	
	// common properties
	// common property
	//String hasName = "hasName";
	String classType = "classType";
	//String startedBy = "startedby";
	String assignedTo = "assignedTo";
	String nodeType = "nodeType";
	String nodeID = "nodeID";
	
	
	
	public TranslateRDFtoSrc()
	{
		model=ModelFactory.createDefaultModel();
	}
	
	
	protected Resource find_node_with_nodeID(int nodeID)
	{
		Resource targetResource=null;
		try
		{
			ResIterator iter1=model.listResourcesWithProperty(model.createProperty(ns+this.nodeID));
			while(iter1.hasNext())
			{
				Resource temp=iter1.nextResource();
				//System.out.println(temp);
				if(temp.hasProperty(model.createProperty(ns+this.nodeID), nodeID+"")) //finding a node with the ID
				{	
					targetResource=temp;
					//System.out.println("Node found:"+temp);
					break;
				}
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
		return targetResource;
	}
	
	
//	protected Resource find_the_resource_by_URI(String targetURI) {
//		//code for finding the resource with params
//		Resource
//	}catch
//	}
	
	protected String create_demo_instance(String className)
	{
		//code for creating demo instance
		String myInstance=null;
		try
		{
			char initCar=className.charAt(0);
			myInstance=Character.toLowerCase(initCar)+className.substring(1);
		}catch(Exception exc){}
		return myInstance;
	}
	
	
	protected void explore_class_node(Resource node)
	{
		//code for exploring class node
		try
		{
			//this is the className
			String className=node.getLocalName();
			//id assigned?
			int id_assigned=0;
			//constructed
			int obj_constructed=0;
			//statement list
			StmtIterator stmts=node.listProperties();
			while(stmts.hasNext())
			{
				Statement stmt=stmts.next();
				String my_predicate=stmt.getPredicate().getLocalName();
				if(my_predicate.equals(this.nodeType)) //supported in JRE 7
				{
					System.out.print("\n"+node.getLocalName());
				}
				if(my_predicate.equals(this.nodeID) && id_assigned==0)
				{
					//node traversed updated..
					int my_node_id=Integer.parseInt(stmt.getObject().toString());
					node_traversed=my_node_id>=node_traversed?my_node_id:node_traversed;
					//tag the statement as visited
					id_assigned=1;
				}
				if(my_predicate.equals(this.hasIntance)){
					System.out.print(" "+create_demo_instance(className));
				}
				
				if(my_predicate.equals(this.hasConstructor))
				{
					System.out.print("=");
					explore_constructor_node((Resource)stmt.getObject());
					obj_constructed=1;
				}
				if(my_predicate.equals(this.initiatedBy) && obj_constructed==0)
				{
					System.out.print("=");
					try
					{
					explore_the_node((Resource)stmt.getObject());
					}catch(Exception exc){
						System.out.print(stmt.getObject());
						//System.out.println();
					}
				}
				else
				{
					
				}
			}	
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	protected void explore_constructor_node(Resource node)
	{
		//code for exploring constructor node
		try
		{
			StmtIterator stmts=node.listProperties();
			while(stmts.hasNext())
			{
				Statement stmt=stmts.next();
				String my_predicate=stmt.getPredicate().getLocalName();
				if(my_predicate.equals(this.nodeType)) //supported in JRE 7
				{
					System.out.print("new "+node.getLocalName().split("\\.")[0]);
					System.out.print("(");
				}
				if(my_predicate.equals(this.nodeID))
				{
					//node traversed updated
					int my_node_id=Integer.parseInt(stmt.getObject().toString());
					node_traversed=my_node_id>=node_traversed?my_node_id:node_traversed;
				}
				if(my_predicate.equals(this.hasParameter)){
					//System.out.print(stmt.getObject());
					Resource innerNode=(Resource)stmt.getObject();
					explore_the_node(innerNode);
				}
			}
			System.out.print(")");
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	
	protected void explore_method_node(Resource node)
	{
		//code for exploring a method node
		try
		{
			//get the class Name
			String className=node.getLocalName().split("\\.")[0];
			String methodName=node.getLocalName().split("\\.")[1];
			StmtIterator stmts=node.listProperties();
			while(stmts.hasNext())
			{
				Statement stmt=stmts.next();
				String my_predicate=stmt.getPredicate().getLocalName();
				if(my_predicate.equals(this.nodeType)) //supported in JRE 7
				{
					System.out.print("\n"+create_demo_instance(className)+"."+methodName+"(");
				}
				if(my_predicate.equals(this.nodeID))
				{
					//node traversed updated..
					int my_node_id=Integer.parseInt(stmt.getObject().toString());
					node_traversed=my_node_id>=node_traversed?my_node_id:node_traversed;
				}
				if(my_predicate.equals(this.hasParameter)){
					//System.out.print(stmt.getObject());
					Resource innerNode=(Resource)stmt.getObject();
					explore_the_node(innerNode);
				}else
				{
					
				}
			}
			System.out.print(")");
			
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	
	protected void explore_attribute_node(Resource node)
	{
		// code for exploring an attribute node

		try {
			// get the class Name
			String className = node.getLocalName().split("\\.")[0];
			String fieldName = node.getLocalName().split("\\.")[1];
			
			//id assigned?
			int id_assigned=0;
			
			StmtIterator stmts = node.listProperties();
			while (stmts.hasNext()) {
				Statement stmt = stmts.next();
				String my_predicate = stmt.getPredicate().getLocalName();
				if (my_predicate.equals(this.nodeType)) // supported in JRE 7
				{
					System.out.print("\n" + create_demo_instance(className)
							+ "." + fieldName);
				}
				if (my_predicate.equals(this.nodeID) && id_assigned==0) {
					// node traversed updated..
					int my_node_id = Integer.parseInt(stmt.getObject()
							.toString());
					node_traversed=my_node_id>=node_traversed?my_node_id:node_traversed;
					//tag the statement as visited
					id_assigned=1;
					
				}
				if (my_predicate.equals(this.assignedTo)) {
					// System.out.print(stmt.getObject());
					try
					{
					Resource innerNode = (Resource) stmt.getObject();
					System.out.print("=");
					explore_the_node(innerNode);
					}catch(Exception exc){
						System.out.print(stmt.getObject());
					}
				} else {
				}
			}
			//System.out.print("\n");

		} catch (Exception exc) {
			System.err.println(exc.getMessage());
		}
	}
	
	protected void print_control_condition(Resource controlNode)
	{
		//code for printing the control condition
		try
		{
			StmtIterator iter2=controlNode.listProperties();
			while(iter2.hasNext())
			{
				Statement stmt=iter2.next();
				String my_predicate=stmt.getPredicate().toString();
				if(my_predicate.equals(ns+this.hasConidtion))
				{
					Resource my_object=(Resource)stmt.getObject();
					explore_the_node(my_object);
				}
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	protected void explore_block_childs(Resource controlNode)
	{
		//code for exploring the control block
		StmtIterator iter1=controlNode.listProperties();
			while(iter1.hasNext())
			{
					Statement stmt=iter1.nextStatement();
					//System.out.print(stmt);
					try
					{
						Resource res2=(Resource)stmt.getObject();
						explore_the_node(res2);
					}catch(Exception exc2){
						//System.out.println(stmt.getObject());
					}
			}
	}
	
	protected void explore_control_node(Resource node)
	{
		//code for exploring control node
		try
		{
			String nodeName=node.getLocalName();
			switch(nodeName)
			{
			case "if":
				System.out.print("\nif(");
				print_control_condition(node);
				System.out.print("){\n");
				explore_block_childs(node);
				System.out.print("\n}");
				node_traversed++;
				break;
			case "for":
				System.out.print("\nfor(;");
				print_control_condition(node);
				System.out.print(";){\n");
				explore_block_childs(node);
				System.out.print("\n}");
				node_traversed++;
				break;
			case "while":
				System.out.print("\nwhile(");
				print_control_condition(node);
				System.out.print("){\n");
				explore_block_childs(node);
				System.out.print("\n}");
				node_traversed++;
				break;
			case "do":
				System.out.print("\ndo(");
				System.out.print("){\n");
				explore_block_childs(node);
				System.out.print("\n}(");
				print_control_condition(node);
				System.out.println(");");
				node_traversed++;
				break;
				
			case "try":
				System.out.print("\ntry \n{");
				//print_control_condition(node);
				//System.out.print(";){\n");
				explore_block_childs(node);
				System.out.print("\n}\n");
				System.out.print("\ncatch(Exception exc){}");
				node_traversed++;
				break;
			
				
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage()+exc.getStackTrace());
		}
		
	}
	
	
	protected void explore_the_node(Resource node)
	{
		//exploring current node
		try
		{
			Property nodeTypeProperty=model.createProperty(ns+nodeType);
			String nodeTypeVal=node.listProperties(nodeTypeProperty).next().getObject().toString();
			switch(nodeTypeVal)
			{
			case "Class":
				explore_class_node(node);
				break;
			case "Constructor":
				explore_constructor_node(node);
				break;
			case "Method":
				explore_method_node(node);
				break;
			case "Attribute":
				explore_attribute_node(node);
				break;
			case "Control":
				//System.out.println(node);
				explore_control_node(node);
				break;
			default:
				System.out.println(node.toString());
				break;
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			System.err.println(e.getMessage());
		}
	}
	
	protected Resource find_the_resource_by_URI(String targetURI) {
		// code for finding the resource
		Resource targetResource = null;
		try {
			ResIterator resIterator = model.listResourcesWithProperty(model.createProperty(ns + nodeType));
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
	
	
	protected void traverse_the_network(String className)
	{
		//code for traversing the network
		try
		{
			Resource starter=find_the_resource_by_URI(ns+className);
			explore_the_node(starter);
//			Property myProperty=model.createProperty(ns+this.nodeType);
//			ResIterator resColl=model.listResourcesWithProperty(myProperty);
//			while(resColl.hasNext())
//			{
//				Resource tempNode=resColl.next();
//				explore_the_node(tempNode);
//			}
		}catch(Exception exc)
		{
			System.err.println(exc.getMessage());
		}
	}
	
	protected void traverse_the_network()
	{
		//code for traversing the network
		try
		{
			ResIterator resColl=model.listResourcesWithProperty(model.createProperty(ns+this.nodeType)); 
			//model.listResourcesWithProperty(model.createProperty(ns+nodeID));
			int total_nodes=resColl.toList().size();
			System.out.println("Total nodes with nodeID:"+total_nodes);
			int nodeID=1;
			while(nodeID<=total_nodes)
			{
				Resource currResource=find_node_with_nodeID(nodeID);
				//System.out.println(currResource);
				explore_the_node(currResource);
				//updating as already some node traversed...
				nodeID=node_traversed;
				//new node id
				nodeID++;
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	protected void convert_rdf_to_source(String rdf_file_name)
	{
		//code for converting RDF to source code skeletons
		try
		{
		//Model model=ModelFactory.createDefaultModel();
		File file=new File(rdf_file_name);
		FileInputStream in=new FileInputStream(file);
		model.read(in,null);
		System.out.println("Total number of triples:"+model.size());
		model.write(System.out);
		
		//traversing the nodes
		//String className="BufferedWriter";
		traverse_the_network();
		//traverse_the_network();
		}catch(Exception exc)
		{
			System.err.println(exc.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			TranslateRDFtoSrc translater=new TranslateRDFtoSrc();
			String rdf_file_name="D:/My MSc/MyDataset/SemDataset/RDFColl/Ant-Contrib/rdf/3.rdf";
			//String className="Set";
			translater.convert_rdf_to_source(rdf_file_name);
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}

}

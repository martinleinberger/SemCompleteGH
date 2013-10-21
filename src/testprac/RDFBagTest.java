package testprac;

import com.hp.hpl.jena.rdf.model.*;
public class RDFBagTest {

	/**
	 * @param args
	 */
	String ns="http://www.semcomplete.com#";
	
	protected void create_a_bag()
	{
		//code for creating a bag
		try
		{
			Model model=ModelFactory.createDefaultModel();
			
			Bag bag=model.createBag(ns+"MyBag");
			Resource node1=model.createResource(ns+"Nodel");
			node1.addProperty(model.createProperty(ns+"Name"), "Siam");
			Resource node2=model.createResource(ns+"Node2");
			Resource node3=model.createResource(ns+"Node3");
			Resource node4=model.createResource(ns+"Node4");
			bag.addProperty(model.createProperty(ns+"child"), node1);
			bag.addProperty(model.createProperty(ns+"child"), node2);
			bag.addProperty(model.createProperty(ns+"child"), node4);
			bag.addProperty(model.createProperty(ns+"child"), node3);
			
			Resource firstNode=model.createResource(ns+"StandAlone");
			firstNode.addProperty(model.createProperty(ns+"child"), "Max");
			
			//write output
			model.write(System.out);	
		}catch(Exception exc){
			System.err.println(exc.getMessage());
			
		}	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RDFBagTest app=new RDFBagTest();
		app.create_a_bag();

	}

}

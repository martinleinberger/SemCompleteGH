package rdfcompare;

import java.io.File;
import java.io.FileInputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RDFIntersectTest {

	/**
	 * @param args
	 */
	
	
	protected void generate_all_subgraphs(Model mymodel)
	{
		//code for 
	}
	
	protected void intersect_the_rdfs()
	{
		//code for testing the instersection of Rdfs
		try
		{
			String rdf1="D:/My MSc/MyDataset/SemDataset/Patterns/PatternColl/BufferedInputStream/patt/13_14.rdf";
			String rdf2="D:/My MSc/MyDataset/SemDataset/Patterns/PatternColl/ArrayList/rdf/JHotdraw7_213.rdf";
			Model model1=ModelFactory.createDefaultModel();
			Model model2=ModelFactory.createDefaultModel();
			//File file1=new File(rdf1);
			//File file2=new File(rdf2);
			model1.read(new FileInputStream(rdf1),null);
			model2.read(new FileInputStream(rdf2),null);
			//model1.write(System.out);
			Model intersectModel=model1.intersection(model2);
			//intersectModel.write(System.out);
			int model_size=(int)intersectModel.size();
			//boolean isSubgraph=model1.containsAll(intersectModel);
			System.out.println("Size of the subgraph="+model_size);
			model1.write(System.out);
			//now lets browse throguh statements
			System.out.println("--------------------");
			StmtIterator stmts=intersectModel.listStatements();
			while(stmts.hasNext())
			{
				System.out.println(stmts.next());
			}
			
			//System.out.println("They are subgraph: " +isSubgraph);
			
			
			
			
		}catch(Exception exc){
			System.err.println(exc.getMessage());
			exc.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RDFIntersectTest test=new RDFIntersectTest();
		test.intersect_the_rdfs();
		
	}

}

package rdfcompare;
//code for comparing RDF
import java.io.File;
import java.io.FileInputStream;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.*;
public class RDFComparer {

	/**
	 * @param args
	 */
	
	protected void compare_two_rdf_networks(String file1, String file2)
	{
		//code for comparing between two networks
		try
		{
			//creating models
			Model model1 = ModelFactory.createDefaultModel();
			Model model2 = ModelFactory.createDefaultModel();
			//reading the rdfs
			model1.read(new FileInputStream(file1), null);
			model2.read(new FileInputStream(file2), null);
			//reading subjects
			ResIterator subjects1 = model1.listSubjects();
			ResIterator subjects2 = model2.listSubjects();
			
			int matched=0;
			int total=0;
			int explored=0;
			
			while(subjects1.hasNext())
			{
				explored=1;
				String myURI=subjects1.nextResource().getURI();
				ResIterator subs2=model2.listSubjects();
				while(subs2.hasNext())
				{
					Resource item=subs2.nextResource();
					if(item.hasURI(myURI))
					{
					matched++;
					break;
					}
				}
				total++;
			}
			
			if(explored==0)
			while(subjects2.hasNext())
			{
				String myURI=subjects2.nextResource().getURI();
				ResIterator subs1=model1.listSubjects();
				while(subs1.hasNext())
				{
					Resource item=subs1.nextResource();
					if(item.hasURI(myURI))
					{
					matched++;
					break;
					}
				}
				total++;
			}
			System.out.println("Source :"+file1+", Nodes:"+subjects1.toList().size());
			System.out.println("Target :"+file2+", Nodes:"+subjects2.toList().size());
			System.out.println("Node matched:"+matched+", Matching="+(double)subjects1.toList().size()/subjects2.toList().size());
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	protected void manage_rdf_files_comparer(String projDir)
	{
		//code for handling the comparison
		File file=new File(projDir);
		if(file.isDirectory())
		{
			File[] files=file.listFiles();
			for(int i=0;i<files.length;i++)
			{
				for(int j=i+1;j<files.length;j++)
				{
					String rdffile1=files[i].getAbsolutePath();
					String rdffile2=files[j].getAbsolutePath();
					//compare two networks
					compare_two_rdf_networks(rdffile1, rdffile2);
				}
			}
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			RDFComparer comparer=new RDFComparer();
			String projDir="D:/My MSc/MyDataset/SemDataset/Patterns/PatternColl/Iterator/rdf";
			comparer.manage_rdf_files_comparer(projDir);
		} catch (Exception exc) {
			System.err.println(exc.getMessage());

		}
	}
}

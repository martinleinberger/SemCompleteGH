package testprac;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import org.semanticweb.yars.nx.parser.NxParser;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class RDFTestDemo {

	/**
	 * @param args
	 */
	
	protected void load_from_rdf(String rdffile)
	{
		try
		{
		//code for loading from RDF
		File file=new File(rdffile);
		FileInputStream fs=new FileInputStream(file);
		Model model=ModelFactory.createDefaultModel();
		model.read(fs,null);
		model.write(System.out);
		System.out.println("=======================");
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	protected void load_from_ntriple(String triplefile)
	{
		try
		{
		//code for loading from RDF
		File file=new File(triplefile);
		FileInputStream fs=new FileInputStream(file);
		Model model=ModelFactory.createDefaultModel();
		model.read(fs,null,"N-TRIPLE");
		model.write(System.out);
		System.out.println("=======================");
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
		String rdf_file="D:/My MSc/MyDataset/SemDataset/RDFCollNier/AOI/rdf/28.rdf";
		String ttl_file="D:/My MSc/MyDataset/SemDataset/RDFCollNier/AOI/sequence/28.nt";
		RDFTestDemo demo=new RDFTestDemo();
		demo.load_from_rdf(rdf_file);
		demo.load_from_ntriple(ttl_file);
		}catch(Exception exc){
			System.err.println("Failed to load the file");
			exc.printStackTrace();	
		}
	}

}

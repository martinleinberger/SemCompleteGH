package stats;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class CrossValidatePatterns {
	
	//HashSet patterns
	HashSet<String> extractedPatterns;
	String targetPatternsFolder;
	HashSet<Model> usageModelSet;
	int total_pattern=0;
	int pattern_detected=0;
	
	public CrossValidatePatterns(String patternsfolder)
	{
		//code for cross validation constructor
		this.targetPatternsFolder=patternsfolder;
		this.extractedPatterns=new HashSet<String>();
		this.usageModelSet=new HashSet<Model>();
		this.load_the_target_patterns();
		this.load_the_target_projects();
		//this.total_pattern=this.extractedPatterns.size();
		this.pattern_detected=0;
	}
	
	public void load_the_target_patterns()
	{
		//code for loading the target patterns
		try
		{
			File file=new File(this.targetPatternsFolder);
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				for(File folder:files)
				{
					String patt_path=folder.getAbsolutePath()+"/patt";
					//System.out.println(patt_path);
					File pattfolder=new File(patt_path);
					if(pattfolder.isDirectory())
					{
						File[] patterns=pattfolder.listFiles();
						for(File rdf:patterns)
						{
							try
							{
							this.extractedPatterns.add(rdf.getAbsolutePath());
							}catch(Exception exc){
								exc.printStackTrace();
							}
						}
					}
				}
			}
			
			this.total_pattern=extractedPatterns.size();
			
		}catch(Exception exc){
		exc.printStackTrace();	
		}	
	}
	
	protected void load_the_target_projects()
	{
		String projFolder[]={"DNSJava","javaparser","javapeg","jKiwi","jtds","jgeom","JWebUnit","JID2"};
		for(String project:projFolder)
		{
			String projPath="D:/My MSc/MyDataset/SemDataset/TestRDFColl/"+project+"/rdf";
			File usagerdfs=new File(projPath);
			if(usagerdfs.isDirectory())
			{
				File[] rdfs=usagerdfs.listFiles();
				for(File f:rdfs)
				{
					try
					{
					Model model1=ModelFactory.createDefaultModel();
					model1.read(new FileInputStream(f),null);
					this.usageModelSet.add(model1);
					}catch(Exception exc){
						//exc.printStackTrace();
					}
				}
			}
		}
		System.out.println("Total usages found:"+this.usageModelSet.size());
	}
	
	
	protected void detect_the_target_patterns()
	{
		//code for checking the target patterns in other projects
		try
		{
			for(String str:this.extractedPatterns)
			{
				Model model1=ModelFactory.createDefaultModel();
				model1.read(new FileInputStream(str),null);
				for(Model model2:this.usageModelSet)
				{
					if(model2.containsAll(model1))
					{
						this.pattern_detected++;
						//model2.write(System.out);
						//System.out.println("==============");
						//model1.write(System.out);
						break;
					}
				}
			}	
		}catch(Exception exc){
			
		}
	}

	public static void main(String args[])
	{
		try
		{
			String patternsfolder="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl";
			CrossValidatePatterns cross=new CrossValidatePatterns(patternsfolder);
			//cross.load_the_target_patterns();
			System.out.println("Total patterns:"+cross.total_pattern);
			//cross.load_the_target_projects();
			System.out.println("Total usages:"+cross.usageModelSet.size());
			cross.detect_the_target_patterns();
			System.out.println("Pattern detected:"+cross.pattern_detected);
		}catch(Exception exc){
			exc.printStackTrace();
		}
		
	}
	
}

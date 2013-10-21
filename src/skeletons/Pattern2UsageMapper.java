package skeletons;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Pattern2UsageMapper {

	/**
	 * @param args
	 */
	
	String patternFile;
	String usageFolder;
	HashMap<String, Model> modelMap;
	Model selectedModel;
	
	public Pattern2UsageMapper(String patternFile,String usageFolder)
	{
		this.patternFile=patternFile;
		this.usageFolder=usageFolder;
		modelMap=new HashMap<String,Model>();
		//create default model
		selectedModel=ModelFactory.createDefaultModel();
		this.load_selected_model();
		this.load_the_usage_model();
	}
	
	protected void load_selected_model()
	{
		//code for loading the selected model
		try
		{
		File file=new File(this.patternFile);
		this.selectedModel.read(new FileInputStream(file),null);
		}catch(Exception exc){	
		}
	}
	
	protected void load_the_usage_model()
	{
		//code for loading usage models
		File file=new File(this.usageFolder);
		if(file.isDirectory())
		{
			File[] files=file.listFiles();
			for(File file1:files)
			{
				try
				{
				Model model1=ModelFactory.createDefaultModel();
				model1.read(new FileInputStream(file1),null);
				this.modelMap.put(file1.getAbsolutePath(), model1);
				}catch(Exception exc){}	
			}	
		}
	}
	
	protected String map_pattern_to_usage()
	{
		String mapped_usage="";
		//code for mapping pattern to usage
		for(String key:this.modelMap.keySet())
		{
			Model mymodel=this.modelMap.get(key);
			if(mymodel.containsAll(this.selectedModel))
			{
				mapped_usage=key;
				Model intersect=mymodel.intersection(this.selectedModel);
				intersect.write(System.out);
				break;
			}
		}
		return mapped_usage;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			String selected="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl/FileReader/patt/22_20.rdf";
			String usage_folder="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl/FileReader/rdf";
			Pattern2UsageMapper mapper=new Pattern2UsageMapper(selected, usage_folder);
			String usage_file=mapper.map_pattern_to_usage();
			System.out.println("Selected usage:"+usage_file);
		}catch(Exception exc){
		}
	}
}

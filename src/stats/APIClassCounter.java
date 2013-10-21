package stats;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class APIClassCounter {

	/**
	 * @param args
	 */
	HashMap<String,Integer> pMap=new HashMap<String,Integer>();
	HashMap<String,Integer> pMap1=new HashMap<String,Integer>();
	HashMap<String,Integer> cMap=new HashMap<String,Integer>();
	HashMap<String,Integer> appMap=new HashMap<String,Integer>();
	HashMap<String,Integer> appMap1=new HashMap<String,Integer>();
	
	
	protected void project_api_patterns_stats(String pattFolder)
	{
		//code for fidning the patterns in different APIs
		try
		{
			File patt=new File(pattFolder);
			File[] apis=patt.listFiles();
			for(File api:apis)
			{
				String apiclass=api.getAbsolutePath()+"/patt";
				File f2=new File(apiclass);
				for(File rdf:f2.listFiles())
				{
					
					try
					{
					Model model=ModelFactory.createDefaultModel();
					model.read(new FileInputStream(rdf), null);
					
					HashSet<String> distset=new HashSet<String>();
					
					String projoccured=api.getAbsolutePath()+"/rdf";
					File pfiles=new File(projoccured);
					for(File rdf2:pfiles.listFiles())
					{
						try
						{
						Model m2=ModelFactory.createDefaultModel();
						m2.read(new FileInputStream(rdf2),null);
						String projectName=rdf2.getName().split("_")[0];
						if(m2.containsAny(model) || model.containsAny(m2))
						{
							if(appMap.containsKey(projectName))
							{
								int count=appMap.get(projectName).intValue();
								count++;
								appMap.put(projectName, count);
							}else
							{
								appMap.put(projectName, 1);
							}
							
							distset.add(projectName);
							
						}}catch(Exception exc){}	
					}
						//adding hashset info
					//now adding the set to hasset
					for(String proj:distset)
					{
						if(appMap1.containsKey(proj))
						{
							int count=appMap1.get(proj).intValue();
							count++;
							appMap1.put(proj,count);
						}else
						{
							appMap1.put(proj, 1);
						}
					}
					
					
					}catch(Exception exc){}
					
				}
			}
		}catch(Exception exc){
		System.err.println(exc.getMessage());	
		}
		
	}
	
	protected void explore_the_api_classes(String folder)
	{
		try
		{
			File file=new File(folder);
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				for(File f:files)
				{
					String path=f.getAbsolutePath()+"/"+"java";
					File f2=new File(path);
					if(f2.isDirectory())
					{
						int api_c_count=f2.listFiles().length;
						System.out.println(f.getName()+":"+api_c_count);
						
						HashSet<String> distset=new HashSet<String>();
						
						for(File f3:f2.listFiles())
						{
							String fileName=f3.getName();
							String[] parts=fileName.split("_");
							String pName=parts[0];
							//adding to hashset
							distset.add(pName);
							//adding to hashmap
							if(pMap.containsKey(pName))
							{
								int count=pMap.get(pName).intValue();
								count++;
								pMap.put(pName, count);	
							}else
							{
								pMap.put(pName, 1);
							}	
						}
						
						//now adding the set to hashset
						for(String proj:distset)
						{
							if(pMap1.containsKey(proj))
							{
								int count=pMap1.get(proj).intValue();
								count++;
								pMap1.put(proj,count);
							}else
							{
								pMap1.put(proj, 1);
							}
						}
						
					}
					
				}
			}else
			{
				
			}
		}catch(Exception exc)
		{
			
		}
	}
	
	protected void show_the_api_class_frequency()
	{
		for(String key:this.pMap.keySet())
		{
			
			System.out.println("Project: "+key+", Total class:"+pMap.get(key));
			System.out.println("Project: "+key+", Distinct class:"+pMap1.get(key));
			System.out.println("==================");
		}	
	}
	protected void show_project_api_pattern_count()
	{
		int total=0;
		for(String key:this.appMap.keySet())
		{
			System.out.println("Project: "+key+": Total Patterns "+appMap.get(key));
			System.out.println("Project: "+key+": Distinct Patterns "+appMap1.get(key));
			total+=appMap1.get(key).intValue();
			System.out.println("==================");	
		}
		System.out.println("Average patterns "+(total/25));		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		APIClassCounter acounter=new APIClassCounter();
		String folder="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl";
		//acounter.explore_the_api_classes(folder);
		//acounter.show_the_api_class_frequency();
		acounter.project_api_patterns_stats(folder);
		acounter.show_project_api_pattern_count();	
	}
}

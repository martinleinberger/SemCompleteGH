package rdfmaker;
//code for filtration of API methods

import java.io.*;
import java.util.*;

import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
public class APIFiltration {

	/**
	 * @param args
	 */
	
	//class variables
	String[] utilClasses=null;
	Hashtable<String, Integer> htable=null;
	Hashtable<String, String> hftable=null;
	HashSet<String> apiSet=null;
    HashSet<String> dataTypeSet=null;
	
	
    
	public APIFiltration()
	{
		utilClasses=new String[250]; //string array created..
		apiSet=new HashSet<String>();
		dataTypeSet=new HashSet<String>();
		this.load_the_classes();
		htable=new Hashtable<String, Integer>();
		hftable=new Hashtable<String, String>();
	}
	
	protected void load_the_classes()
	{
		String util_class_path="D:/My MSc/MyDataset/SemDataset/JavaAPIs/java.util.awt.io.txt";
		try
		{
			File file=new File(util_class_path);
			Scanner scanner=new Scanner(file);
			int count=0;
			while(scanner.hasNext())
			{
				//utilClasses[count]=scanner.next().trim();
				apiSet.add(scanner.next());
				count++;
			}
			//do not consider the expression having these data types
			dataTypeSet.add("double");
			dataTypeSet.add("long");
			dataTypeSet.add("short");
			dataTypeSet.add("char");
			dataTypeSet.add("int");
			dataTypeSet.add("float");
			dataTypeSet.add("boolean");
		}catch(Exception exc){
			System.err.println(exc.getMessage());
			exc.printStackTrace();
		}
	}
	
	protected HashSet analyze_the_desired_API_usage(String methodBody, HashSet set)
	{
		//code for checking if any API is used here
		boolean used=false;
		try
		{
			StringTokenizer tokenizer=new StringTokenizer(methodBody);
			while(tokenizer.hasMoreTokens())
			{
				String token=tokenizer.nextToken();
				if(apiSet.contains(token))
				{
					//adding to hashset
					if(!set.contains(token))
					set.add(token);	
				}
			}
		}catch(Exception exc){
			System.err.println("Failed to analyze the API usage..");
		}
		//returning Hash set
		return set;
	}
	
	
	protected void calculate_single_file_stats(File srcFile)
	{
		try {
			Scanner scanner = new Scanner(srcFile);
			String temp_class = "";
			HashSet set = new HashSet();
			String methodBody = new String();
			// accumulating file content
			while (scanner.hasNext()) {
				String line = scanner.next();
				methodBody += line + "\n";
			}
			// getting count api count from a file
			set = analyze_the_desired_API_usage(methodBody, set);

			// now add the classes to the hash table
			temp_class = temp_class.trim();
			for (Object myclass : set.toArray()) {
				if (htable.containsKey(myclass)) {
					// updating the integer in htable
					Integer objVal = htable.get(myclass);
					int value = Integer.valueOf(objVal.toString()) + 1;
					htable.put(myclass.toString(), new Integer(value));

					// updating the hftable
					String objValue2=hftable.get(myclass);
					objValue2+=";"+srcFile.getAbsolutePath();
					hftable.put(myclass.toString(), objValue2);
				} else {
					htable.put(myclass.toString(), 1);
					hftable.put(myclass.toString(),srcFile.getAbsolutePath());
				}
			}
		} catch (Exception exc) {
		}
		
	}
	
	protected void calculate_api_frequency(String projDir)
	{
		//code for getting API frequency
		try
		{
			File file=new File(projDir);
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				for(File srcFile:files)
				{
					if(srcFile.isDirectory())
					{
						calculate_api_frequency(srcFile.getAbsolutePath());
					}
					else if(srcFile.getName().endsWith(".java"))
					{
						calculate_single_file_stats(srcFile);
					}
				}
			}else if(file.getName().startsWith(".java"))
			{
				calculate_single_file_stats(file);
			}
		}catch(Exception exc){
		}
		
	}
	
	
	protected void copy_the_clusters(String APIClass)
	{
		//code for creating the clusters
		try
		{
			String pattDir="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl/"+APIClass;
			File myDir=new File(pattDir);
			if(!myDir.exists()){
				myDir.mkdir();
			}
			
			String pattDirJava=pattDir+"/java";
			File javadir=new File(pattDirJava);
			if(!javadir.exists())javadir.mkdir();
			
			String pattDirRDF=pattDir+"/rdf";
			File rdfdir=new File(pattDirRDF);
			if(!rdfdir.exists())rdfdir.mkdir();
			
			String pattDirSeq=pattDir+"/sequence";
			File seqdir=new File(pattDirSeq);
			if(!seqdir.exists())seqdir.mkdir();
			
			//now copy the classes here
			String files=this.hftable.get(APIClass);
			String[] filePaths=files.split(";");
			int copied=0;
			for(String pathString:filePaths)
			{
				//java file
				File file1=new File(pathString);
				//getting project name
				String projectNameFull=file1.getParentFile().getParent();
				int sla_index=projectNameFull.lastIndexOf('\\');
				String projectName=projectNameFull.substring(sla_index+1);
				
				String fileName=file1.getName();
				String namePart=fileName.split("\\.")[0];
				
				String file2Path=pattDirJava+"/"+projectName+"_"+fileName;
				File file2=new File(file2Path);
			    if(file2.exists())
			    {
			    	//file2=new File(pattDirJava+"/"+namePart+"_.java");
			    }
				//copying files
				Files.copy(file1, file2);
				
				//now RDF file
				String rdfDir1=new File(file1.getParent()).getParent()+"/rdf";
				System.out.println(rdfDir1);
				File rdfFile2=new File(pattDirRDF+"/"+projectName+"_"+namePart+".rdf");
				File rdffile1=new File(rdfDir1+"/"+namePart+".rdf");
				if(rdfFile2.exists())
				{
					//rdfFile2=new File(pattDirRDF+"/"+namePart+"_"+".rdf");
				}
				//copying rdf file
				Files.copy(rdffile1, rdfFile2);
				
				//now sequence file
				String seqDir1=new File(file1.getParent()).getParent()+"/sequence";
				System.out.println(seqDir1);
				File seqFile2=new File(pattDirSeq+"/"+projectName+"_"+namePart+".nt");
				File seqfile1=new File(seqDir1+"/"+namePart+".nt");
				if(seqFile2.exists())
				{
					//rdfFile2=new File(pattDirRDF+"/"+namePart+"_"+".rdf");
				}
				//copying rdf file
				Files.copy(seqfile1, seqFile2);
				
				copied++;
			}			
			
			System.out.println("Files copied to "+pattDir+":"+copied);
			
		}catch(Exception exc){
			System.err.println("Failed to create the clusters."+exc.getMessage());
		}
	}
	
	
	
	protected void filter_api_methods(String projDir, String TargetDir)
	{
		//code for filtration of API methods
		try
		{
			File file=new File(projDir+"/rdf");
			if(file.isDirectory())
			{
				String[] files=file.list();
				//now go through the files
				int total_api_found=0;
				int counter=0;
				while(counter<files.length)
				{
					int api_class_found=0;
					String fileName=projDir+"/rdf/"+files[counter];
					//file
					File innerFile=new File(fileName);
					FileInputStream inStream=new FileInputStream(innerFile);
					//model
					Model model=ModelFactory.createDefaultModel();
					//reading file
					model.read(inStream, null);
					ResIterator subjects=model.listSubjects();
					while(subjects.hasNext())
					{
						Resource item=subjects.nextResource();
						String className=item.getLocalName();
						System.out.print(className+" ");
						for(String str:utilClasses)
						{
							if(str.trim().startsWith(className))
							{
								api_class_found=1;
								System.out.println("\nFOUND "+className);
								break;
							}
						}
						if(api_class_found==1)
							{
							//total_api_found++;
							System.out.println(fileName);
							break;
							}
							
					}
					model=null;
					System.out.println();
					
					//System.out.println("Just finished:"+fileName);
					/*File innerFile=new File(projDir+"/java/"+files[counter]);
					Scanner scanner=new Scanner(innerFile);
					int api_class_found=0;
					String replace_file_name=""*/;
					
					/*while(scanner.hasNext())
					{
						String line=scanner.next();
						
						for(String my_util_class:utilClasses)
						{
							line=line.trim();
							String[] lineparts=line.split("\\s+");
							
							//System.out.println(lineparts[0]+"-"+my_util_class);
							if(lineparts[0].startsWith(my_util_class))
							{
								api_class_found=1;
								break;
							}	
						}
						if(api_class_found>0)
						{
							replace_file_name=files[counter];
							break;
						}
						
					}*/
					
					if(api_class_found>0)
					{
						//now extract that file
						String tempFileName=innerFile.getName();
						String fileNumber=tempFileName.split("\\.")[0];
						File sourceFile=new File(projDir+"/java/"+fileNumber+".java");
						File destrdfFile=new File(TargetDir+"/rdf/"+fileNumber+".rdf");
						File destsrcFile=new File(TargetDir+"/java/"+fileNumber+".java");
						
						//now perform the file copy + transfer
						Files.copy(innerFile, destsrcFile);
						Files.copy(sourceFile, destrdfFile);
						
						System.out.println("API occurred :"+tempFileName);
						total_api_found++;
					}
					counter++;
				}
				System.out.println("Total API occurrence found:"+total_api_found);
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
			exc.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
		String projDir="D:/My MSc/MyDataset/SemDataset/RDFColl/TreeView";
		String targetDir="D:/My MSc/MyDataset/SemDataset/RDFColl/TreeView_filtered";
		String wholeDir="D:/My MSc/MyDataset/SemDataset/RDFCollNier";
		APIFiltration api=new APIFiltration();
		api.load_the_classes();
		//api.filter_api_methods(projDir, targetDir);
		api.calculate_api_frequency(wholeDir);
		
		//showing the stat from htable
		int total_found=0;
		int gt_than_5=0;
		for(String key:api.htable.keySet())
		{
			System.out.println(key+":"+api.htable.get(key));
			total_found+=api.htable.get(key).intValue();
			if(api.htable.get(key).intValue()>0)
			{
				api.copy_the_clusters(key);
				gt_than_5++;
				//System.out.println(key+":"+api.hftable.get(key));
			}
		}
		System.out.println("Total API class found:"+total_found);
		System.out.println("Unique API class found:"+api.htable.keySet().size());
		System.out.println("Greater than 5:"+gt_than_5);
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
}

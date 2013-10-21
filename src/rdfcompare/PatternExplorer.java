package rdfcompare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelFactoryBase;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.util.ModelUtils;

public class PatternExplorer {

	/**
	 * @param args
	 */
	HashSet<Model> modelColl=null;
	HashSet<String> modelFileName=null;
	int no_of_models;
	String modelFolder=null;
	//the common model will contain the minimal pattern
	Model commonModel=ModelFactory.createDefaultModel();
	final int model_size_threshold=1;
	int iteration=0;
	double convergence_threshold=4;
	Hashtable<Statement, Integer> StmtTable=null;
	int primary_pattern_threshold=0;
	HashMap<Integer,HashMap<Integer,Model>> modelPattern=null;
	//HashMap<Integer,Integer> modelFrequency=null;
	int threshold_frequnecy=0;
	String patternOuputDir;
	String masterPatternDir;
	
	
	
	
	public PatternExplorer(String modelFolder)
	{
		//initializing the models
		File file=new File(modelFolder+"/rdf");
		if(file.isDirectory())
		{
			File[] files=file.listFiles();
			this.no_of_models=files.length;
			//initiating models
			modelColl=new HashSet<Model>();
			//collecting model rdf name
			modelFileName=new HashSet<String>();
			//initiating model folder
			this.modelFolder=modelFolder+"/rdf";
			//creating statement table
			StmtTable=new Hashtable();
			//creating model pattern
			modelPattern=new HashMap();
			//creating model frequency
			//modelFrequency=new HashMap();
			this.patternOuputDir=modelFolder+"/patt";
			this.masterPatternDir=modelFolder+"/master";
		}
	}
	
	
	protected void load_the_model()
	{
		//loading the models
		File file=new File(this.modelFolder);
		if(file.isDirectory())
		{
			File[] rdfs=file.listFiles();
			for(File rdf:rdfs)
			{
				try
				{
				FileInputStream in=new FileInputStream(rdf);
				Model model=ModelFactory.createDefaultModel();
				model.read(in,null);
				this.modelColl.add(model);
				//adding the model file name
				this.modelFileName.add(rdf.getAbsolutePath());
				}catch(Exception exc){
				}
			}
		}
	}
	
	
	protected void find_common_model(Model model1, Model model2)
	{
		//code for finding common model
		try
		{
			Model common=model1.intersection(model2);
			StmtIterator iter=common.listStatements();
			while(iter.hasNext())
			{
				System.out.println(iter.next());
			}
			System.out.println("=========================");
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	protected double difference(double a, double b)
	{
		//calculating the difference
		if(a>=b)return a-b;
		else return b-a;
	}
	
	
	protected HashSet explore_common_pattern(HashSet<Model> modelColl, double threshold)
	{
		//My HashSet
		HashSet<Model> mySet=new HashSet<Model>();
		//code for exploring common pattern
		int total_model_count=0;
		double avg_intersect_size=0;
		int total_intersect_size=0;
		
		for(int i=0;i<modelColl.size();i++)
		{
			for(int j=i+1;j<modelColl.size();j++)
			{
				Model model1=(Model)modelColl.toArray()[i];
				Model model2=(Model)modelColl.toArray()[j];
				Model intersect=model1.intersection(model2);
				//System.out.println("i="+i+",j="+j+", intersection size="+intersect.size());
				if(intersect.size()>1) //consider from a granular to big
				{
					mySet.add(intersect);
					total_intersect_size+=intersect.size();
					total_model_count++;
				}
			}	
		}
		
		if(total_model_count>400)
		{
			//do some sampling rather than all
			int avg_model_size=(total_intersect_size/total_model_count);
			mySet=filter_the_model(mySet,avg_model_size);
			System.out.println("Just filtered and got:"+mySet.size());
		}
		System.out.println("Current # Model:"+mySet.size());
		
		return mySet;
	}
	
	protected HashSet<Model> filter_the_model(HashSet<Model> mySet,int model_size_threshold)
	{
		//code for filtering the model set based on thresholds
		int total_count=mySet.size();
		HashSet<Model> newSet=new HashSet<Model>();
		
		int allowed_samples=500;
		int min=0;
		int max=total_count;
		
		//sorting a collection
		ArrayList<Model> l = new ArrayList<Model>(mySet);
	       Collections.sort(l, new Comparator<Model>(){
	         public int compare(Model o1, Model o2) {
	            return (int)(o2.size()-o1.size());
	        }});
		
		//getting top models
	    Iterator<Model> iter1=l.iterator();
	    while(iter1.hasNext())
	    {
	    	Model model=iter1.next();
	    	if(newSet.size()<allowed_samples)
				newSet.add(model);
			else break;		
	    }
		return newSet;
	}
	

	protected void count_statement_frequency(HashSet<Model> mySet)
	{
		//code for calculating statement frequency
		try
		{
			HashSet<Statement> temp=new HashSet<Statement>();
			for(Object objModel:mySet.toArray())
			{
				Model model=(Model)objModel;
				StmtIterator iter=model.listStatements();
				while(iter.hasNext())
				{
					temp.add(iter.next());
				}
				
				//now add them to the StmtTable
				for(Object stmtobj:temp.toArray())
				{
					Statement stmt=(Statement)stmtobj;
					if(StmtTable.containsKey(stmt))
					{
						Integer count=StmtTable.get(stmt);
						int intVal=count.intValue();
						intVal++;
						StmtTable.put(stmt, new Integer(intVal));			
					}else
					{
						StmtTable.put(stmt, 1);
					}	
				}
			}		
			
			//now show the frequency
			Model skeleton=ModelFactory.createDefaultModel();
			//sort the hashtable
			sortValue(this.StmtTable);
			
			for(Statement stmt:StmtTable.keySet())
			{
				//System.out.println(stmt+" : "+StmtTable.get(stmt));
				//int frequency=StmtTable.get(stmt);
				skeleton.add(stmt);
			}
			
			//showing the model
			visualize_the_model(skeleton);
			//skeleton.write(System.out);
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	public void sortValue(Hashtable<Statement, Integer> t){
		  //Transfer as List and sort it
	       ArrayList<Map.Entry<Statement, Integer>> l = new ArrayList(t.entrySet());
	       Collections.sort(l, new Comparator<Map.Entry<Statement, Integer>>(){
	         public int compare(Map.Entry<Statement, Integer> o1, Map.Entry<Statement, Integer> o2) {
	            return o2.getValue().compareTo(o1.getValue());
	        }});
	       
	       //temporary Hashtable
	       Hashtable<Statement, Integer> temphtable=new Hashtable();
	       Iterator<Map.Entry<Statement,Integer>> iter1=l.iterator();
	       while(iter1.hasNext())
	       {
	    	   //System.out.println(iter1.next());   
	    	   Map.Entry<Statement, Integer> mape=iter1.next();
	    	   //System.out.println(mape.getKey()+" : frequency="+mape.getValue());
	    	   temphtable.put(mape.getKey(), mape.getValue());
	       }
	       //now refill the hash table
	       this.StmtTable=temphtable;
	    }
	
	
	protected void find_the_most_frequent_item_model(HashSet<Model> mymodels)
	{
		//code for finding the most frequent item model
		try
		{
			Iterator<Model> iter1=mymodels.iterator();
			int model_counter=0;
			while(iter1.hasNext())
			{
				Model model=iter1.next();
				model_counter++;
				//explore the model
				StmtIterator iter2=model.listStatements();
				int score=0;
				int stmt_occurred=0;
				while(iter2.hasNext())
				{
					Statement keystm=iter2.next();
					if(this.StmtTable.containsKey(keystm))
					{
						try
						{
							int count=this.StmtTable.get(keystm).intValue();
							score+=count;
							stmt_occurred++;
						}catch(Exception exc){
						}
					}
				}
				System.out.println(model_counter+" gets "+score+" for "+stmt_occurred+ ">"+this.modelFileName.toArray()[model_counter]);
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}

	protected void visualize_the_model(Model m)
	{
		//code for visualizing the model
		m.write(System.out);
	}
	
	
	protected void find_out_the_master_pattern(HashSet<Model> mymodels)
	{
		//code for getting master patterns
		try
		{
			Model master=ModelFactory.createDefaultModel();
			for(int i=0;i<mymodels.size();i++)
			{
				Model temp1=(Model)mymodels.toArray()[i];
				for(int j=i+1;j<mymodels.size();j++)
				{
					Model temp2=(Model)mymodels.toArray()[j];
					Model temp3=temp1.intersection(temp2);
					master=master.union(temp3);
				}
			}
			//so now we have got a master pattern with node id
			String masterFile=this.masterPatternDir+"/mPattern.rdf";
			master.write(new FileOutputStream(masterFile));
			System.out.println("===Master Pattern written to file===");
			
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	
	protected void manage_duplicate_models(HashMap<Integer,Model> existing, Model target,int score)
	{
		for(Integer key:existing.keySet())
		{	
			Model model=existing.get(key);
			if(model.containsAll(target))
			{
				if(score>key)
				{
					//no need to insert the new model
					//existing.put(key, value)
				}
			}
		}
	}
	
	protected void clusterize_the_model(HashSet<Model> mymodels)
	{
		//code for clustering the models
		//form the threshold frequency
		this.threshold_frequnecy=(int)(this.modelColl.size()*.5);
		//50% usage contains the pattern
		try
		{
			for(int i=0;i<mymodels.size();i++)
			{
				int iso_morph_count=0;
				int model_size=0;
				
				int score1=0;
				int score2=0;
				
				Model model1=(Model)mymodels.toArray()[i];
				model_size=(int) model1.size();
				
				for(int j=0;j<mymodels.size();j++)
				{
					if(i!=j)
					{
						//model_size=(int) model1.size();
						Model model2=(Model)mymodels.toArray()[j];
						//if(model1.containsAll(model2) || model2.containsAll(model1))
						if(model2.containsAll(model1))
						{
							iso_morph_count++;//common occurrence
						}
					}	
				}
				score1=iso_morph_count;
				
				for(int k=0;k<this.modelColl.size();k++)
				{
					Model model3=(Model)this.modelColl.toArray()[k];
					if(model3.containsAll(model1))
					{
						score2++; //single occurrence
					}
				}
				//total score
				int total_score=score1+score2;
				
				//adding to the HashMap
				try
				{
					if(this.modelPattern.containsKey(new Integer(model_size)))
					{
						HashMap existing=(HashMap)this.modelPattern.get(model_size);
						//Model existing=(Model)this.modelPattern.get(model_size);
						if(total_score>this.threshold_frequnecy)
						{
							existing.put(new Integer(total_score), model1);
							this.modelPattern.put(new Integer(model_size),existing);
						}
					}else
					{
						if(total_score>this.threshold_frequnecy) //50% of total occurrences
						{
						HashMap<Integer,Model> currentMap=new HashMap<Integer,Model>();
						currentMap.put(total_score,model1);
						this.modelPattern.put(new Integer(model_size),currentMap);
						//this.modelFrequency.put(new Integer(model_size),new Integer(iso_morph_count));
						}
					}
				}catch(Exception exc){
					System.err.println("Failed to add to hashmap");
					exc.printStackTrace();
				}
			}
			
			//now we got all patterns
			
			int pattern_found=0;
			//now show the pattern
			for(Integer sizeIndex:this.modelPattern.keySet())
			{
				System.out.println("========================");
				System.out.println("Model size:"+sizeIndex);
				int pattern_size_count=0;
				HashMap<Integer,Model> pattMap=new HashMap();
				pattMap=(HashMap)this.modelPattern.get(sizeIndex);
				for(Integer score:pattMap.keySet())
				{
					Model pattModel=(Model)pattMap.get(score);
					String fileName=this.patternOuputDir+"/"+sizeIndex+"_"+score+".rdf";
					pattModel.write(new FileOutputStream(fileName));
					pattern_size_count++;
				}
				System.out.println("Pattern size:"+sizeIndex+", count= "+pattern_size_count);
				System.out.println("========================");
				pattern_found+=pattern_size_count;
			}
			System.out.println("Total unique pattern found:"+pattern_found);
			
			
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	protected void destroy_the_object()
	{
		//code for destroying the object
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			//System.out.println(args[0]);
			String model_home_folder= "D:/My MSc/MyDataset/SemDataset/PatternsNier/test";
			File file=new File(model_home_folder);
			String[] files=file.list();
			int completed=0;
			for(String folderName:files)
			{
			try
			{
			//String pattern_dir="D:/My MSc/MyDataset/SemDataset/Patterns/PatternColl/ArrayList/patt";
			//String model_folder="/home/gyc291/semextract/PatternColl/ArrayList/rdf";//
			PatternExplorer pexplorer=new PatternExplorer(model_home_folder+"/"+folderName);
			pexplorer.load_the_model();
			
			System.out.println(folderName+" Total model loaded:"+pexplorer.modelColl.size());
			if(pexplorer.modelColl.size()<=40)continue;
			//if(pexplorer.modelColl.size()>40)continue;
			HashSet<Model> mymodelSet=pexplorer.explore_common_pattern(pexplorer.modelColl,pexplorer.model_size_threshold);
			System.out.println("I received total: "+mymodelSet.size()+" model occurences");
			//pexplorer.count_statement_frequency(mymodelSet);
			//find out the master pattern co
			pexplorer.find_out_the_master_pattern(mymodelSet);
			//count isomorphic subgraph
			pexplorer.clusterize_the_model(mymodelSet);
			//pexplorer.visualize_the_model(mymodel);
			//pexplorer.find_the_most_frequent_item_model(pexplorer.modelColl);
			completed++;
			System.gc();
			}catch(Exception e){
				
			}
			System.out.println("Just completed:"+folderName);
			}
			System.out.println("Total pattern extraction completed:"+completed);
			
		}catch(Exception exc){
			
		}
	}

}

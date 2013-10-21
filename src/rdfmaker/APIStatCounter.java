package rdfmaker;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;



public class APIStatCounter {

	/**
	 * @param args
	 */
	
	String[] utilClasses=null;
	HashSet<String> apiSet=null;
	
    HashSet<String> dataTypeSet=null;
    
	
	public APIStatCounter()
	{
		
		
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
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

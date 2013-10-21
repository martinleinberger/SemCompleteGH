import java.io.File;
import java.io.FileWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;


public class PackageStats {

	/**
	 * @param args
	 */
	
	
	public void create_package_statistics()
	{
		try
		{
		//code for package statistics management
		String file="D:/My MSc/MyDataset/SemDataset/Code Base/combined_imports.txt";
		String file2="D:/My MSc/MyDataset/SemDataset/Code Base/combined_imports_norm.txt";
		FileWriter writer=new FileWriter(new File(file2));
		Scanner scanner=new Scanner(new File(file));
		final Hashtable<String,Integer> htable=new Hashtable<String,Integer>();
		while(scanner.hasNext())
		{
			String line=scanner.next();
			//now do some checking
			if(line.startsWith("java") ||line.startsWith("javax") || line.startsWith("org.eclipse") || line.startsWith("org.apache")) 
			{
				if(htable.contains(line)){
					Integer count=htable.get(line);
					count=count+1;
					htable.put(line,count);
				}else
				{
					htable.put(line,1);
					
				}	
			}
		}
		//now write to the normalized file
		for(String key:htable.keySet())
		{
			writer.write(key+" "+htable.get(key)+"\n");
			
		}
		writer.close();
		
		System.out.println("Package statistics extracted successfully.");
		
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}	
	}
	
	protected void create_compact_package()
	{
		//code for compacting the package
		try
		{//code for package statistics management
			String file="D:/My MSc/MyDataset/SemDataset/Code Base/combined_imports_norm.txt";
			String file2="D:/My MSc/MyDataset/SemDataset/Code Base/combined_imports_compact.txt";
			FileWriter writer=new FileWriter(new File(file2));
			Scanner scanner=new Scanner(new File(file));
			Hashtable<String,Integer> htable=new Hashtable<String,Integer>();
			while(scanner.hasNext())
			{
				String line=scanner.next();
				//now do some checking
				if(line.startsWith("java") ||line.startsWith("javax")) 
				{
					try
					{
					String[] parts=line.split("\\.");
					String _line=parts[0]+"."+parts[1];
					_line=_line.trim();
					System.out.println(_line);
					
					if(htable.containsKey(_line)){
						Integer count=htable.get(_line);
						int myval=count.intValue()+1;
						htable.put(_line,myval);
					}else
					{
						htable.put(_line,1);
						
					}	
					}catch(Exception exc){}
				}
				if(line.startsWith("org.eclipse") || line.startsWith("org.apache"))
				{
					try
					{
					String[] parts=line.split("\\.");
					String _line=parts[0]+"."+parts[1]+"."+parts[2];
					_line=_line.trim();
					System.out.println(_line);
					
					if(htable.containsKey(_line)){
						Integer count=htable.get(_line);
						int myval=count.intValue()+1;
						htable.put(_line,myval);
					}else
					{
						htable.put(_line,1);
						
					}	
					}catch(Exception exc){}
					
				}
				
			}
			//now write to the normalized file
			for(String key:htable.keySet())
			{
				writer.write(key+" "+htable.get(key)+"\n");
			}
			writer.close();
			System.out.println("Package statistics compacted  successfully.");
		}catch(Exception xc){
				
		}	
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
		PackageStats stat=new PackageStats();
		//stat.create_package_statistics();
		stat.create_compact_package();
		}catch(Exception exc)
		{
			System.err.println(exc.getMessage());
			
		}
	}

}

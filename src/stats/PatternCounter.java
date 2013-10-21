package stats;

import java.io.File;

public class PatternCounter {

	/**
	 * @param args
	 */
	
	
	protected int get_class_wise_patterns(String folder)
	{
		//code for getting the patterns
		int total_count=0;
		try
		{
			File file=new File(folder);
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				total_count=files.length;
				System.out.println("Class: "+folder+", # of patterns:"+total_count);
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
		return total_count;
	}
	
	
	protected void show_the_patterns(String home)
	{
		//code for counting patterns
		File file=new File(home);
		if(file.isAbsolute())
		{
				File[] folders=file.listFiles();
				int pattern_found_for_class=0;
				int pattern_great_5=0;
				int total_patterns=0;
				for(File f:folders)
				{
					String pattPath=f.getAbsolutePath()+"/patt";
					int count=get_class_wise_patterns(pattPath);
					if(count>0)pattern_found_for_class++;
					if(count>=5)pattern_great_5++;
					total_patterns+=count;
				}
				System.out.println("=======================");
				System.out.println("Total patterns found:"+total_patterns+ " for "+pattern_found_for_class+" classes");	
				System.out.println("Class containing mode than 5 patterns "+pattern_great_5);
		}	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PatternCounter pcount=new PatternCounter();
		String folder="D:/My MSc/MyDataset/SemDataset/NPattern";
		pcount.show_the_patterns(folder);

	}

}

package garbagemanager;
import java.io.*;

public class ManageGarbage {

	/**
	 * @param args
	 */
	
	
	protected void delete_all_files(String projDir)
	{
		//code for clearing database
				try
				{
					File file=new File(projDir);
					if(file.isDirectory())
					{
						File[] files=file.listFiles();
						for(File file1:files)
						{
							if(file1.isDirectory())
							{
								delete_all_files(file1.getAbsolutePath());
							}
							else
							{
									file1.delete();
							}
						}
					}else
					{
							file.delete();
					}
				}catch(Exception exc){
				System.err.println(exc.getMessage());
				}
	}
	
	
	protected void clear_target_folders(String projDir)
	{
		//code for clearing database
				try
				{
					File file=new File(projDir);
					if(file.isDirectory())
					{
						File[] files=file.listFiles();
						for(File file1:files)
						{
							if(file1.isDirectory())
							{
								clear_target_folders(file1.getAbsolutePath());
							}
							else
							{
								if(file1.getAbsolutePath().contains("\\patt\\")&& file1.getName().endsWith(".rdf"))
								{
									file1.delete();
								}
							}	
						}
					}else
					{
						if(file.getAbsolutePath().contains("\\patt\\")&& file.getName().endsWith(".rdf"))
						{
							file.delete();
						}
					}
				}catch(Exception exc){
				}
	}
	
	protected void clear_the_garbase(String projDir)
	{
		//code for clearing database
		try
		{
			File file=new File(projDir);
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				for(File file1:files)
				{
					if(file1.isDirectory())
					{
						clear_the_garbase(file1.getAbsolutePath());
					}
					else
					{
						if(file1.getName().endsWith(".java") ||file1.getName().endsWith(".rdf"))
						{
							file1.delete();
						}
					}
					
				}
			}else
			{
				if(file.getName().endsWith(".java") ||file.getName().endsWith(".rdf"))
				{
					file.delete();
				}
				
			}
		}catch(Exception exc){
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			String projDir="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl";
			//String projDir="C:/Users/Masud/Dropbox/Masud and Mani Shared Folder/Assignment 6/Group G";
			ManageGarbage garbage=new ManageGarbage();
			//garbage.clear_target_folders(projDir);
			//garbage.clear_the_garbase(projDir);
			garbage.delete_all_files(projDir);
			System.out.println("Folder cleared successfully!");
			
		}catch(Exception exc){
		System.err.println(exc.getMessage());	
		}
		

	}

}

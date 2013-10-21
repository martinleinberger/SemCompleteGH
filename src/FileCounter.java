


//code for calculating files in a directory


import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.*;

import stats.MyDemoVisitor;
public class FileCounter {

	/**
	 * @param args
	 */
	int fileCount=0;
	int methodCount=0;
	int constructorCount=0;
	int lineCount=0;
	int ind_method=0;
	int ind_constructor=0;
	int ind_line_count=0;
	
	
	public FileCounter() {
		// TODO Auto-generated constructor stub
		this.fileCount=0;
		this.methodCount=0;
		this.lineCount=0;
		this.constructorCount=0;
		this.ind_method=0;
		this.ind_constructor=0;
		this.ind_line_count=0;
	}
	
	
	
	protected void get_all_methods(File file)
	{    
		try
		{
		CompilationUnit cu=null;
		try
		{
		FileInputStream fo=new FileInputStream(file);
		cu=JavaParser.parse(fo,"utf-8");
		
		}catch(Exception exc){
			System.err.println(exc.getMessage()+file.getAbsolutePath());
		}
		MyDemoVisitor visitor=new MyDemoVisitor();
		visitor.visit(cu, null);
		ind_method=visitor.method_count;
		ind_constructor=visitor.constructor_count;
		ind_line_count=visitor.line_count;
		visitor=null;
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	protected void reset_the_counts()
	{
		this.ind_method=0;
		this.ind_constructor=0;
	}
	
	protected void update_the_counts(String fileName)
	{
		//System.out.println("Explored:"+fileName);
		//System.out.println(this.ind_method + ","+this.ind_constructor);
		//update the countshis
		this.methodCount+=this.ind_method;
		this.constructorCount+=this.ind_constructor;
		this.lineCount+=this.ind_line_count;
		
		//System.out.println(this.methodCount + ","+this.constructorCount);
		
		
	}
	
	
	protected int get_projectfiles_count(String Directory_path)
	{
		int total_count=0;
		File fileDir=new File(Directory_path);
		if(fileDir.isDirectory())
		{
			File[] moreFiles=fileDir.listFiles();
			for(int i=0;i<moreFiles.length;i++)
			{
				File myFile=moreFiles[i];
				if(myFile.isDirectory())
				{
					int mycount=0;
					mycount=get_projectfiles_count(myFile.getAbsolutePath());
					total_count+=mycount;
					
				}else
				{
					if(myFile.getName().endsWith(".java"))
					{
					total_count+=1;
					//go for more exploration
					try
					{
					get_all_methods(myFile);
					update_the_counts(myFile.getAbsolutePath());
					reset_the_counts();
					}catch(Exception exc){}
					
					
					}
				}
			}
			
		}else
		{
			if(fileDir.getName().endsWith(".java"))
			{
			total_count+=1;	
			try
			{
			get_all_methods(fileDir);
			update_the_counts(fileDir.getAbsolutePath());
			reset_the_counts();
			}catch(Exception exc){}
			
			
			}
		}
		return total_count;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileCounter fc=new FileCounter(); 
		String project_directory="D:/My MSc/MyDataset/SemDataset/SProjects/dvsl";
		//String project_directory="C:/Users/Masud/guava-libraries";
		int file_count=fc.get_projectfiles_count(project_directory);
		System.out.println("Project containing :"+file_count+ " java files");
		System.out.println("Total methods "+fc.methodCount);
		System.out.println("Total constructor "+fc.constructorCount);
		System.out.println("Total lines (aprrox) "+fc.lineCount);

	}

}

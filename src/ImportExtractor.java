import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;

import java.io.*;
import java.util.List;
import java.util.Scanner;




public class ImportExtractor {

	//code for extracting import statement
	String projDir;
	FileWriter writer;
	int source_files=0;

	public ImportExtractor(String projDir, String outFile)
	{
		this.projDir=projDir;
		try
		{
			//creating the writer
			File file=new File(outFile);
			writer = new FileWriter(file);
		}catch(Exception exc){}
	}
	
	
	protected void extract_import_statements(String classFile)
	{
		//code for extracting the import statements
		try
		{
			File file=new File(classFile);
			InputStream in = null;
			in=new FileInputStream(file);
            CompilationUnit cu = JavaParser.parse(in);
            List<ImportDeclaration> imports=cu.getImports();
            for(ImportDeclaration imp:imports)
            {
            	writer.write(imp.getName()+"\n");
            	
            }
		}catch(Exception exc){}		
	}
	

	protected void manage_project_files(String projectDir )
	{
		//code for providing the java files
		File fileDir=new File(projectDir);
		if(fileDir.isDirectory())
		{
			File[] moreFiles=fileDir.listFiles();
			for(int i=0;i<moreFiles.length;i++)
			{
				File myFile=moreFiles[i];
				if(myFile.isDirectory())
				{
					int mycount=0;
					manage_project_files(myFile.getAbsolutePath());
					
					
					
				}else
				{
					if(myFile.getName().endsWith(".java"))
					{
						extract_import_statements(myFile.getAbsolutePath());
						source_files++;
					}	
				}
			}
		}else
		{
			if(fileDir.getName().endsWith(".java"))
			{
				extract_import_statements(fileDir.getAbsolutePath());
				source_files++;
			}
		}
	}
	public static void main(String args[])
	{
		//code for extracting the import statements
		try
		{
			String projDir="D:/My MSc/MyDataset/SemDataset/Projects/jomic";
			String outFile="D:/My MSc/MyDataset/SemDataset/Code Base/jomic.import";
			ImportExtractor imp_ext=new ImportExtractor(projDir,outFile);
			imp_ext.manage_project_files(imp_ext.projDir);
			imp_ext.writer.close();
			
			System.out.println("Import statements extracted successfully ");
			System.out.println("Source files consulted:"+imp_ext.source_files);
			
		}catch
		(Exception exc){
		
			System.err.println(exc.getMessage());
		}
		
		
	}
	
	
	
	
	
	
	
}

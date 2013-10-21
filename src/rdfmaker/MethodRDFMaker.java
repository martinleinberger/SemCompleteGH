package rdfmaker;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


//code for making method RDF maker

public class MethodRDFMaker {
	/**
	 * @param args
	 */
	int source_file=0;
	int method_considered=0;
	int fileNameCounter=1;
	
	
	protected void extract_method_pattern_single_class(String fileName, String outputDir)
	{
		//code for extracting pattern from single class
		InputStream in = null;
		//String fileName="D:\\My MSc\\Java Works\\ASTParseDemo\\src\\MySampleClass.java";
        CompilationUnit cu = null;
        try
        {
        	System.out.println("Exploring file:"+fileName);
        	in = new FileInputStream(fileName);
            cu = JavaParser.parse(in);
            MethodVisitor2 visitor = new MethodVisitor2(outputDir, this.fileNameCounter);
            visitor.visit(cu, null);
            //System.out.println("Total method extracted:"+(visitor.project_file_counter-1));
            this.source_file+=visitor.apis_occurred; //actually methods counter
            this.method_considered=visitor.api_considered-1;
            this.fileNameCounter=visitor.currFileNumber;
        }catch(Exception exc){
        	System.err.println(exc.getMessage());
        }
	}
	
	protected void extract_all_methods(String projDir,String outputDir)
	{
		//code for extracting methods from all files
		try
		{
			//code for providing the java files
			File fileDir=new File(projDir);
			if(fileDir.isDirectory())
			{
				File[] moreFiles=fileDir.listFiles();
				for(int i=0;i<moreFiles.length;i++)
				{
					File myFile=moreFiles[i];
					if(myFile.isDirectory())
					{
						int mycount=0;
						extract_all_methods(myFile.getAbsolutePath(),outputDir);
					}else
					{
						if(myFile.getName().endsWith(".java"))
						{
							extract_method_pattern_single_class(myFile.getAbsolutePath(),outputDir);
							//source_file++;
						}	
					}
				}
			}else
			{
				if(fileDir.getName().endsWith(".java"))
				{
					extract_method_pattern_single_class(fileDir.getAbsolutePath(), outputDir);
					//source_file++;
				}
			}
		}catch(Exception exc){
			System.err.println("Failed to extract methods -"+exc.getMessage());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String projDir="./data/projects/log4j-1.2.15";
		//String projDir="C:/Users/Masud/guava-libraries";
		MethodRDFMaker maker=new MethodRDFMaker();
		String outputDir="./data/outputs/log4j-1.2.15";
		maker.extract_all_methods(projDir,outputDir);
		//String fileName="D:/My MSc/Java Works/SemComplete/src/FileWriterDemo.java";
		//String fileName="D:/My MSc/Java Works/ASTParseDemo/src/MySampleClass.java";
		//maker.extract_method_pattern_single_class(fileName,outputDir);
		System.out.println("Total APIs occurred :"+maker.source_file);
		System.out.println("Total APIs considered :"+(maker.fileNameCounter-1));
	}
}

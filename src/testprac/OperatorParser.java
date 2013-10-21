package testprac;

import java.io.File;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;


public class OperatorParser {

	/**
	 * @param args
	 */
	
	protected void parseOperator()
	{
		//code for parsing different operator
		try
		{
			String fileName="D:/My MSc/MyDataset/SemDataset/RDFs/Ops.txt";
			File file=new File(fileName);
			CompilationUnit cu=JavaParser.parse(file);
			OpeatorVisitor visitor=new OpeatorVisitor();
			visitor.visit(cu, null);
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OperatorParser parser=new OperatorParser();
		parser.parseOperator();

	}

}

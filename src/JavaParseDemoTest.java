
//code for Java parse 
import japa.parser.ASTHelper;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.expr.NameExpr;

import java.io.*;
public class JavaParseDemoTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		InputStream in = null;
		String fileName="D:\\My MSc\\Java Works\\ASTParseDemo\\src\\MySampleClass.java";
        CompilationUnit cu = null;
        try
        {
        	in = new FileInputStream(fileName);
            cu = JavaParser.parse(in);
            MethodVisitor visitor = new MethodVisitor();
            visitor.visit(cu, null);
            
        }catch(Exception exc){
        	
        	System.err.println(exc.getMessage());
        }
	}
}

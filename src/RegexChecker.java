
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
public class RegexChecker {

	/**
	 * @param args
	 */
	
	
	protected void analyze_method_content(String content)
	{
		//code for analyzing method content
		try
		{
			String[] lines=content.split("\n");
			System.out.println(lines[0]+" has "+lines.length+" lines");
		}catch(Exception exc)
		{
			System.err.println(exc.getMessage());
			
		}		
	}
	
	
	
	
	protected void extract_xml_content(String xml_file_name)
	{
		try
		{
		//code for extracting XML content
		File xmlFile=new File(xml_file_name);
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder builder=factory.newDocumentBuilder();
		Document xmldoc=builder.parse(xmlFile);
		xmldoc.getDocumentElement().normalize();
		NodeList nodeList=xmldoc.getElementsByTagName("source");
		int row_added=0;
		for(int i=0;i<nodeList.getLength();i++)
		{
			//now I got each method
			Element e=(Element)nodeList.item(i);
			String method_content=e.getTextContent();
			analyze_method_content(method_content);
			
		}
		}catch(Exception exc)
		{
			System.out.println(exc.getMessage());	
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RegexChecker chkr=new RegexChecker();
		String xml_file_name="D:/My MSc/MyDataset/SemDataset/Code Base/aoi_functions.xml";
		chkr.extract_xml_content(xml_file_name);
		
		
	}

}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class FileWriterDemo {

	/**
	 * @param args
	 */
	
	protected void writeContent()
	{
		try
		{
		String cities[]={"Saskatoon","Regina","Montreal"};
		File file=new File("cities.txt");
		FileWriter writer=new FileWriter(file);
		BufferedWriter bwriter=new BufferedWriter(writer);
		for(String city:cities)
		{
			bwriter.write(city+"\n");
		}
		bwriter.close();
		//writer.close();
		}catch(Exception exc){
			
		}	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileWriterDemo demo=new FileWriterDemo();
		demo.writeContent();
		
	}

}

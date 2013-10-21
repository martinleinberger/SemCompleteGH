package rdfcompare;

import java.io.File;


//code for choosing appropriate thresholds
public class ThresholdManager {

	/**
	 * @param args
	 */
	protected int choose_threshold_for_class(String classFolder)
	{
		int rdf_node_threshold=7;
		try
		{
			File file=new File(classFolder);
			if(file.isDirectory())
			{
				String[] files=file.list();
				for(String fileName:files)
				{
				}
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
		return rdf_node_threshold;
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

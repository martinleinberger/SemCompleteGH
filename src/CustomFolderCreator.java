import java.io.File;


public class CustomFolderCreator {

	/**
	 * @param args
	 */
	
	protected void create_custom_folders(String HomeDir)
	{
		try
		{
			File file=new File(HomeDir);
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				int count=0;
				for(File file2:files)
				{
					if(file2.isDirectory())
					{
						String folderName=file2.getAbsolutePath();
						//pattern
						String pattFolder=folderName+"/"+"patt";
						File pfile=new File(pattFolder);
						if(!pfile.exists())
						{
							pfile.mkdir();
						}
						//skeleton
						String skelFolder=folderName+"/"+"skel";
						File sfile=new File(skelFolder);
						if(!sfile.exists())
						{
							sfile.mkdir();
						}
						//sequence
						String masterFolder=folderName+"/"+"master";
						File mfile=new File(masterFolder);
						if(!mfile.exists())
						{
							mfile.mkdir();
						}
						count++;
					}
					
				}
				System.out.println("Total folder created:"+count);
				
			}
			
		
		}catch(Exception exc){
			System.err.println("Failed to create folders");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CustomFolderCreator cfc=new CustomFolderCreator();
		String HomeDir="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl";
		cfc.create_custom_folders(HomeDir);
	}

}

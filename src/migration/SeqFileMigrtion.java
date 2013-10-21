package migration;

import java.io.File;

import com.google.common.io.Files;

public class SeqFileMigrtion {

	/**
	 * @param args
	 */
	
	protected void seq_file_migration(String folder)
	{
		try
		{
		//code for sequence file migration
		File file=new File(folder);
		if(file.isDirectory())
		{
			File[] folders=file.listFiles();
			for(File folder1:folders)
			{
				String folderPath=folder1.getAbsolutePath();
				String sequencePath=folderPath+"/sequence";
				File seqFolder=new File(sequencePath);
				if(seqFolder.isDirectory())
				{
					File[] seqs=seqFolder.listFiles();
					for(File seq:seqs)
					{
						String completefileName=seq.getAbsolutePath();
						String fileName=seq.getName();
						String onlyName=fileName.split("\\.")[0];
						String projName=onlyName.split("_")[0];
						String fileNumName=onlyName.split("_")[1];
						String projSource="D:/My MSc/MyDataset/SemDataset/RDFCollNier";
						String sourceseqfile=projSource+"/"+projName+"/sequence/"+fileNumName+".nt";
						//now delete the current file
						if(seq.delete())
						{
							Files.copy(new File(sourceseqfile), new File(completefileName));
						}
					}
				}
				System.out.println("Completing :"+folder1.getName());
			}
		}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}	
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			SeqFileMigrtion migrator=new SeqFileMigrtion();
			String folder="D:/My MSc/MyDataset/SemDataset/PatternsNier/PatternColl";
			migrator.seq_file_migration(folder);

		}catch(Exception exc){
			
		}

	}

}

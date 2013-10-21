package lcs;
import java.util.HashSet;
import com.hp.hpl.jena.rdf.model.Statement;

public class LowestCommonSubSeq {
	
	
	public Statement[] find_lowest_common_subsequence(HashSet<Statement> set1, HashSet<Statement> set2)
	{
		 	//String x = StdIn.readString();
	        //String y = StdIn.readString();
	        int M = set1.size();
	        int N = set2.size();
	        Statement[] commons=new Statement[M];
	        int count=0; //count of statements
	        

	        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
	        int[][] opt = new int[M+1][N+1];

	        // compute length of LCS and all subproblems via dynamic programming
	        for (int i = M-1; i >= 0; i--) {
	            for (int j = N-1; j >= 0; j--) {
	                if (set1.toArray()[i] == set2.toArray()[j])
	                    opt[i][j] = opt[i+1][j+1] + 1;
	                else 
	                    opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
	            }
	        }

	        // recover LCS itself and print it to standard output
	        int i = 0, j = 0;
	        while(i < M && j < N) {
	            if (set1.toArray()[i] == set2.toArray()[j]) {
	                //System.out.print(set1.toArray()[i]);
	                commons[count++]=(Statement)set1.toArray()[i];
	                
	                i++;
	                j++;
	            }
	            else if (opt[i+1][j] >= opt[i][j+1]) i++;
	            else                                 j++;
	        }
	        System.out.println();
		
		
	    //returning statement
	    return commons;
		
	}
}

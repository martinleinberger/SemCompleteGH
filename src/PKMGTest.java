import java.util.Set;

import com.hp.hpl.jena.graph.impl.AllCapabilities;



public class PKMGTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//code for package manager test
		PackageListManager pkg=new PackageListManager();
		String packageName="org.eclipse.swt";
		Set<Class<? extends Object>> allClasses=pkg.get_all_classes(packageName);
		System.out.println("Total class:"+allClasses.size());
		for(Class myclass:allClasses)
		{
			System.out.println(myclass.getName());
			System.out.println(myclass.getMethods().length);
			
		}

	}

}

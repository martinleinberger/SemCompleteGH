

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Member;
import com.google.common.base.Predicate;
import java.util.*;
import javax.lang.model.element.PackageElement;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.w3c.*;


import static java.lang.System.out;
public class PackageListManager {

	/**
	 * @param args
	 */
	
	
	
	public Set get_all_classes(String packageName) {
		// TODO Auto-generated method stub
		
		//get the list of all classes under a pacakge
		Set<Class<? extends Object>> allClasses=null;
		try
		{

			
			List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
			classLoadersList.add(ClasspathHelper.getContextClassLoader());
			classLoadersList.add(ClasspathHelper.getStaticClassLoader());

			Reflections reflections = new Reflections(new ConfigurationBuilder()
			    .setScanners(new SubTypesScanner(), new ResourcesScanner())
			    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[2])))
			    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));

		
		
//	            Reflections reflections = new Reflections(new ConfigurationBuilder()
//                .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
//                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))); /*forPackage(basePackageName))); */
//                /* and maybe */
//                /*.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(basePackageName))));*/
//			
	            
	            
			//Reflections reflections = new Reflections("java.io",new ResourcesScanner());
			 allClasses = 
			     reflections.getSubTypesOf(Object.class);
			 
			 //System.out.println("Class retrieved:"+allClasses.size()); 
			 
			 int showed=0;
			 for(Class myclass:allClasses)
			 {
				 try
				 {
//				 System.out.print(myclass.getCanonicalName());
//				 Member[] methods=myclass.getMethods();
//				 System.out.print(" "+methods.length);
//				 System.out.println();
				 showed++;
				 }catch(Exception exc){}
			 }
			 //System.out.println("Successfully showed:"+showed);
			
	
			
			
		}catch(Exception exc){
			//System.out.println("Failed to load all classes-"+exc.getMessage());
			exc.printStackTrace();
		}
		
		//returning the set
		return allClasses;
	}

}

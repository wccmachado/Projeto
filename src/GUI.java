import java.io.IOException;

import net.sf.javabdd.BDD;

/* Implementation of the algorithms that performs backward planning, using regression.
 * The method proposed by [Fourmann, 2000] and the method proposed  by "[Ritanen, 2008]" */

public class GUI {
	
	/* The main method receives a file containing the description of the planning  domain\problem 
	 * and calls the backward search. */
	public static void main(String[] args) throws IOException{
		//File containing the description of the planning domain-problem 
		//String fileName = "AlwaysExampleFile.txt";//args[0];
		String fileName = "SometimeExampleFile.txt";
		String type = "propplan";//"ritanen" or "propplan" args[1] = 
		int nodenum = 999999; //Integer.parseInt(args[2]);
		int cachesize =  999999; //Integer.parseInt(args[3]);
		
		ModelReader model = new ModelReader();
		model.fileReader(fileName, type, nodenum, cachesize);
		
		System.out.println(fileName.substring(fileName.lastIndexOf("/") + 1,fileName.lastIndexOf(".")));
		
		Regression r = new Regression(model);
		System.out.println("Começa a Busca");
		BDD result = r.run();
		System.out.println(result);
	}
}
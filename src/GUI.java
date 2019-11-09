import java.io.File;
import java.io.IOException;

import net.sf.javabdd.BDD;

/* Implementation of the algorithms that performs backward planning, using regression.
 * The method proposed by [Fourmann, 2000] and the method proposed  by "[Ritanen, 2008]" */

public class GUI {
	
	//private static final String PATH = "../TCC/instances/rovers-actl/";
	private static final int QTT = 1;
	/* The main method receives a file containing the description of the planning  domain\problem 
	 * and calls the backward search. */
	public static void main(String[] args) throws IOException {
		//File containing the description of the planning domain-problem 
		//String fileName = "AlwaysExampleFile.txt";//args[0];
		String fileName = "rovers-01-GROUNDED3.txt";
		//String fileName = "";
		//for(int i = 1; i <= QTT; i++) {
			//if(i < 10) {
			//	fileName = "p0" + i;
			//} else {
				//fileName = "p" + i;
			//}
			//int cont = 0;
			//while(cont <= 1) {
				String fileCanonicalPath = new File(/*PATH +*/ fileName /*+ "_" + cont + ".txt"*/).getCanonicalPath();
				String type = "propplan";//"ritanen" or "propplan" args[1] = 
				int nodenum = 999999; //Integer.parseInt(args[2]);
				int cachesize =  999999; //Integer.parseInt(args[3]);
				
				ModelReader model = new ModelReader();
				model.fileReader(fileCanonicalPath, type, nodenum, cachesize);
				
				System.out.println("File: " + 
				fileCanonicalPath.substring(fileCanonicalPath.lastIndexOf("/") + 1, fileCanonicalPath.lastIndexOf(".")));
				
				ModelChecker r = new ModelChecker(model);
				
				try {
					long start = System.currentTimeMillis();
					BDD result = r.run();
					long elapsed = System.currentTimeMillis() - start;
					System.out.println("Tempo de execução: " + elapsed + "ms");
					System.out.println("BDD resultado: " + result);
					//cont++;
				} catch(OutOfMemoryError e) {
					System.out.println("Deu ruim: " + e.getMessage());
				}
			//}
		//}
	}
}
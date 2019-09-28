import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.javabdd.BDD;

/* Read the input file and call the methods of class BDDCreator */
public class ModelReader{
	
	private BDDCreator cre;
	private String type;
	private Preference preference;
	
	//read the lines of the fileName. pType: model-checking or planner
	public void fileReader(String fileName, String pType, int nodenum, int cachesize){
		
		type = pType;
		cre = new BDDCreator(nodenum, cachesize);
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			String propositionsLine = "";
			String initialStateLine = "";
			String constraintsLine = "";
			String goalLine = "";
			String preferencesLine = "";
			String actionName = "";
			String actionPre = "";
			String actionEff = "";
				
			while (in.ready()) {
				line = in.readLine();
				
				// read the lines of the fileName corresponding the variables
				if(line.equals("<predicates>")){
					line = in.readLine(); //read next line containing the propositions
					propositionsLine = line;
					line = in.readLine(); //read <\predicates>
					cre.initializeVarTable(propositionsLine);
				}
				
					
				if(line.equals("<constraints>")){
					while(line.equals("<\\constraints>") == false){
						line = in.readLine(); //<constraints><\constraints>
						if(line.equals("<\\constraints>")) break;
						constraintsLine = line;
						cre.createConstraintBDD(constraintsLine);	
					}
				}
				
				// read the lines corresponding to the initial state
				if(line.equals("<initial>")){
					//System.out.println("initial begin");
					line = in.readLine(); //read next line containing the initial state specification
					initialStateLine = line;
					line = in.readLine(); // read the line <\initial>
					cre.createInitialStateBdd(initialStateLine);
				}
				
				// read the lines corresponding to the planning goal
				if(line.equals("<goal>")){
					line = in.readLine(); //read next line
					goalLine = line;
					cre.createGoalBdd(goalLine);
					line = in.readLine();
				}
				
				if(line.equals("<preferences>")) {
					line = in.readLine(); //read next line
					preferencesLine = line;
					
					/* Pattern definition:
					 * ALWAYS-<proposition label>
					 * SOMETIME-<proposition label>
					 */
					
					if(preferencesLine.startsWith("ALWAYS")) {
						preference = new Preference(Operator.ALWAYS, 
								cre.createPreferenceBdd(preferencesLine.substring(7)));
					} else {
						preference = new Preference(Operator.SOMETIME, 
								cre.createPreferenceBdd(preferencesLine.substring(9)));
					}
					
					line = in.readLine();
				}
				
				// read the lines corresponding to the actions
				if(line.equals("<actionsSet>")){
					line = in.readLine();
					while(line.equals("<\\actionsSet>") == false){
						if(line.trim().equals("<action>")){
							line = in.readLine(); //<name><\name>
							actionName = line.substring(line.indexOf(">")+1, line.indexOf("\\") - 1);
							//System.out.println(actionName);
							line = in.readLine(); //<pre><\pre>
							actionPre = line.substring(line.indexOf(">")+1, line.indexOf("\\") - 1);
							line = in.readLine(); //<pos><\pos>
							actionEff = line.substring(line.indexOf(">")+1, line.indexOf("\\") - 1);
							Action action = new Action(actionName,actionPre,actionEff,cre, type);
							cre.addAction(action);							
							line = in.readLine(); //<\action>
							line = in.readLine(); //<action>
						}
					}
				}
			}
			in.close();
			
		} catch (Exception e) {
			System.out.println("catch");
			e.getMessage();
		}		
	}
	
	public String getType() {
		return type;
	}
	
	public Hashtable<String,Integer> getVarTable() {
		return cre.getVarTable();
	}
	
	public Hashtable<Integer,String> getVarTable2() {
		return cre.getVarTable2();
	}
	
	public int getPropNum(){
		return cre.getPropNum();
	}
	
	public BDD getInitialStateBDD(){
		return cre.getInitiaStateBDD();
	}
	
	public Vector<Action> getActionSet(){
		return cre.getActionsSet();
	}
	
	public BDD getGoalSpec(){
		return cre.getGoalBDD();
	}
	
	public BDD getConstraints(){
		return cre.getConstraintBDD();
	}
	
	// Method that returns preference formulae
	public Preference getPreference(){
		return preference;
	}
	
	// Method that returns an auxiliar BDD to use in EG method
	public BDD getAuxiliarBDD() {
		return cre.fac.one();
	}
	
}
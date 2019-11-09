import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;
import net.sf.javabdd.JFactory;

public class BDDCreator {
	//Number of propositions (including variables v and v')
	private int propNum = 0;
	private transient BDD initialStateBDD;
	private transient BDD goalBDD;
	private transient BDD constraintBDD = null;
	private Vector<Integer> propGoal = new Vector<Integer>();
			
	//Associates the name of the variable to its position in the BDD VariableSet
	private transient Hashtable<String,Integer> varTable = new Hashtable<String,Integer>();
	private transient Hashtable<Integer,String> varTable2 = new Hashtable<Integer,String>();
	private Vector<Action> actionsSet = new Vector<Action>();
	BDDFactory fac;
	
	public BDDCreator(int nodenum, int cachesize){
		//fac = JFactory.init(9000000, 9000000);	
		fac = JFactory.init(nodenum, cachesize);
	}
	
	/***** Get Methods *******/	
	public int getPropNum(){
		return propNum;
	}
	
	public Hashtable<String,Integer> getVarTable(){
		return varTable;
	}
	
	public Hashtable<Integer, String> getVarTable2() {
		return varTable2;
	}
	
	public BDD getInitiaStateBDD(){
		return initialStateBDD;
	}
	
	public BDD getGoalBDD(){
		return goalBDD;
	}
	
	public Vector<Action> getActionsSet() {
		return actionsSet;
	}
	
	public BDD getConstraintBDD() {
		return constraintBDD;
	}
	
	/*[input: just the actions] Initializes the BDD variables table with the propositions, propositions primed and actions*/
	public void initializeVarTable(String propLine) throws IOException {
		StringTokenizer tknProp = new StringTokenizer(propLine, ",");	
		propNum = tknProp.countTokens(); //Propositions
		fac.setVarNum(propNum);
		
		//Filling the table positions corresponding to the propositions
		String propName = "";
		for (int i = 0; i < propNum; i++) {
			propName = tknProp.nextToken();
			varTable.put(propName,i);
			varTable2.put(i,propName);
		}
		
	}

	/**Creates a BDD representing the initial state.*/
	public void createInitialStateBdd(String readLine){
		initialStateBDD = createAndBdd(readLine);
	}
	
	/**Creates a BDD representing the goal **/
	public void createGoalBdd(String readLine){
		goalBDD = createAndBdd(readLine);
		getGoalPropositions(readLine);
	}
	
	public BDD createPreferenceBdd(String proposition) {
		int index = varTable.get(proposition);
		System.out.println("index: " + index);
		return fac.ithVar(index);
	}
			
	/**Creates a vector of propositions indexes in the goal **/
	public void getGoalPropositions(String readLine){
		StringTokenizer tkn = new StringTokenizer(readLine, ",");
		String tknPiece = ""; 
		String prop; 
		int index;
	
		while(tkn.hasMoreTokens()) {
			tknPiece = tkn.nextToken().trim();
			if(tknPiece.startsWith("~")){
				prop = tknPiece.substring(1);  // without the signal ~
				index = varTable.get(prop);
			}else{
				index = varTable.get(tknPiece);
			}
			propGoal.add(index);		
		}
	}
	
	/** Create a BDD representing the conjunction of the propositions in readLine */
	public BDD createAndBdd(String readLine) {
		//System.out.println("linha: " + readLine);
		StringTokenizer tkn = new StringTokenizer(readLine, ",");
		String tknPiece = tkn.nextToken().trim();
		String prop; 
		int index;
		BDD bdd = null;
		
		//System.out.println("tknPiece: " + tknPiece);
		if(tknPiece.startsWith("~")) {
			prop = tknPiece.substring(1); // without the signal ~
			index = varTable.get(prop);
			bdd = fac.nithVar(index);
		} else {
			index = varTable.get(tknPiece);
			bdd = fac.ithVar(index);
		}
		while(tkn.hasMoreTokens()) {
			tknPiece = tkn.nextToken();
			//System.out.println("tknPiece: " + tknPiece);
			if(tknPiece.startsWith("~")) {
				prop = tknPiece.substring(1);  // without the signal ~
				index = varTable.get(prop);
				bdd.andWith(fac.nithVar(index));
			} else {
				index = varTable.get(tknPiece);
				bdd.andWith(fac.ithVar(index));
			}
		}
		//System.out.println("bdd: " + bdd);
		return bdd;
	}
	
	/** Create a BDD representing the conjunction of the propositions in readLine */
	public Vector<BDD> createBddVector(String readLine) {
		Vector<BDD> bddVector = new Vector<BDD>();
		String tknPiece;
		String prop; 
		int index;
		BDD bdd = null;
		StringTokenizer tkn = new StringTokenizer(readLine, ",");
	
		while(tkn.hasMoreTokens()){
			tknPiece = tkn.nextToken().trim();
			if(tknPiece.startsWith("~")){
				prop = tknPiece.substring(1); // without the signal ~
				index = varTable.get(prop);
				bdd = fac.nithVar(index);
			}else{
				index = varTable.get(tknPiece);
				bdd = fac.ithVar(index);
			}
			bddVector.add(bdd);
		}
		return bddVector;
	}
	
	
	/** Create a BDD representing the conjunction of the propositions in readLine */
	public BDD createOrBdd(String readLine) {
		StringTokenizer tkn = new StringTokenizer(readLine, ",");
		String tknPiece = tkn.nextToken().trim(); 
		int index;
		BDD bdd = null;
		
		index = varTable.get(tknPiece);
		bdd = fac.ithVar(index);
		
		while(tkn.hasMoreTokens()) {
			tknPiece = tkn.nextToken();
			index = varTable.get(tknPiece);
			bdd.orWith(fac.ithVar(index));
		}
		return bdd;
	}
	
	/*Creates a BDD that represents an exclusive or*/
	public BDD createExclusiveOrBdd(String line){
		StringTokenizer tkn = new StringTokenizer(line, ",");
		String tknPiece; 

		String beforeTkn, afterTkn;
		int init, end, index;
		BDD bdd, aux;
		BDD returnedBdd = null;
		
		while(tkn.hasMoreTokens()){
			tknPiece = tkn.nextToken();
			index = varTable.get(tknPiece);
			bdd = fac.ithVar(index);
			
			init = line.indexOf(tknPiece);
			end = init + tknPiece.length() + 1;
			
			beforeTkn = line.substring(0,init);
			afterTkn = line.substring(end);
			
			if(beforeTkn.equals("")){
				aux = createOrBdd(afterTkn).not();
				bdd.andWith(aux);
			}else if(afterTkn.equals("")){
				aux = createOrBdd(beforeTkn).not();
				bdd.andWith(aux);
			}else {	
				aux = createOrBdd(beforeTkn).not();
				bdd.andWith(aux);
				
				aux = createOrBdd(afterTkn).not();
				bdd.andWith(aux);
				 
			}			
	
			if(returnedBdd == null){
				returnedBdd = bdd;
			}else{
				returnedBdd.orWith(bdd);
			}
		}
		return returnedBdd;
	}
	
	public void addAction(Action action){
		actionsSet.add(action);
	}
	
	/*Creates the constraint bdd */
	public void createConstraintBDD(String line){
		BDD constr = createExclusiveOrBdd(line);
		if(constraintBDD == null){
			constraintBDD = constr; 
		}else{
			constraintBDD.andWith(constr);
		}
	}	

	
}
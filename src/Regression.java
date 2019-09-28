import java.util.Vector;

import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDFactory;

public class Regression{
	
	private Preference preference;
	private Vector<Action> actionSet;
	//private Hashtable<Integer,String> varTable;
	private BDD goalState;
	//private BDD initialState;
	private BDD constraints;
	private BDD auxiliar;

	/* Constructor */
	public Regression(ModelReader model) {
		this.actionSet = model.getActionSet();
		//this.initialState = model.getInitialStateBDD();
		this.goalState = model.getGoalSpec();		
		this.constraints = model.getConstraints();
		this.preference = model.getPreference();
		this.auxiliar = model.getAuxiliarBDD();
	}
	
	public BDD run() {
		if(preference.getOperator() == Operator.ALWAYS) {
			return satEU(satEG(preference.getBddProposition()), this.goalState);
		}
		return satEU(satEF(preference.getBddProposition()), this.goalState);
	}
	
	public BDD satEF(BDD phi) {
		BDD reached = phi;
		BDD Z = reached.id(); // Only new states reached	
		BDD aux;	
		int i = 0;
		
		while(Z.isZero() == false){
			System.out.println(i);
			//aux = Z.and(initialState.id());	
			/*if (aux.equals(initialState.id())) {
				System.out.println("The problem is solvable.");	
				return true;
			}*/
			//aux.free();
			aux = Z;					
			Z = regression(Z); 
			aux.free();
			
			aux = Z;
			Z = Z.apply(reached, BDDFactory.diff); // The new reachable states in this layer
			//excusesVec.add(i,Z.id());		
			aux.free();
			
			aux = reached;
			reached = reached.or(Z); //Union with the new reachable states
			aux.free();			
			
			aux = reached;
			reached = reached.and(constraints);
			aux.free();
			
			i++;				
		}
		return reached;
	}
	
	public BDD satEG(BDD phi) {
		BDD X = phi;
		BDD Y = auxiliar; //One BDD -- any initialization
		BDD reg;
		while(X.equals(Y) == false) {
			Y = X;
			reg = regression(X); 
			if(reg == null) {
				return X;
			} else {
				X = X.and(reg);
			}
		}
		return X;
	}
	
	public BDD satEU(BDD phi, BDD psi) {
		BDD W = phi; 
		BDD Y = psi; 
		BDD X = null; // -- valor empty (constante).
		BDD reg;
		while((X == null) || (X.equals(Y) == false)) {
			X = Y;
			reg = regression(Y);
			if(reg == null) {
				return Y;
			} else {
				Y = Y.or(W.and(reg));
			}
		}
		return Y;
	}
		
	/* Deterministic Regression of a formula by a set of actions */
	public BDD regression(BDD formula) {
		BDD reg = null;	
		BDD teste = null;
		BDD aux = null;
		
		for (Action a : actionSet) {
			teste = regressionQbf(formula,a);
			
			aux = teste;
			teste = teste.and(constraints);
			aux.free();
			
			if(reg == null){
				reg = teste;
			}else{
				reg.orWith(teste);
			}	
		}
		
		return reg;
	}
	
	/* Propplan regression based on action: Qbf based computation */
	public BDD regressionQbf(BDD Y, Action a) {
		BDD reg, aux;
		reg = Y.and(a.getEffect()); //(Y ^ effect(a))
		
		if(reg.isZero() == false) {
			//System.out.println(a.getName());
			aux = reg;
			reg = reg.exist(a.getChange()); //qbf computation
			aux.free();
				
			aux = reg;
			reg = reg.and(a.getPrecondition()); //precondition(a) ^ E changes(a). test
			aux.free();
			
			/*if(reg.toString().equals("") == false){
				System.out.println(a.getName());
			}*/
				
			aux = reg;
			reg = reg.and(constraints);
			aux.free();
		}
		
		return  reg;
 	}
	
}
	

public enum Operator {

	ALWAYS(0), SOMETIME(1);
	
	private int valueOp;
    
	Operator(int valueOp) {
        this.valueOp = valueOp;
    }
	
	public int getValueOp() {
		return valueOp;
	}
	
}

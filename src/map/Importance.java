package map;

public enum Importance {
	CRITICAL(4),
	CRUCIAL(3),
	IMPORTANT(2),
	SECONDARY(1);
	
	int value;
	
	private Importance(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}

package troops;

public class Operation implements java.io.Serializable{
	private OperationType type;
	private Object value;
	
	public OperationType getType() {
		return type;
	}
	public void setType(OperationType type) {
		this.type = type;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}

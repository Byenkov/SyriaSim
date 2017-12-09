package troops;

import java.io.Serializable;

public class CallForHelp implements Serializable {
	private int severity;
	private String location;
	public int getSeverity() {
		return severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}

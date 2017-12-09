package troops;

import java.io.Serializable;

public class StatusReport implements Serializable{
	private int allied;
	private int neutral;
	private int enemy;
	private int situation;
	private String location;
	
	public int getAllied() {
		return allied;
	}
	public void setAllied(int allied) {
		this.allied = allied;
	}
	public int getNeutral() {
		return neutral;
	}
	public void setNeutral(int neutral) {
		this.neutral = neutral;
	}
	public int getEnemy() {
		return enemy;
	}
	public void setEnemy(int enemy) {
		this.enemy = enemy;
	}
	public int getSituation() {
		return situation;
	}
	public void setSituation(int situation) {
		this.situation = situation;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

}

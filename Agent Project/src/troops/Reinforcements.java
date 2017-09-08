package troops;

import java.io.Serializable;

public class Reinforcements implements Serializable {
	private Double manpower;
	private Double equipment;
	public Double getManpower() {
		return manpower;
	}
	public void setManpower(Double manpower) {
		this.manpower = manpower;
	}
	public Double getEquipment() {
		return equipment;
	}
	public void setEquipment(Double equipment) {
		this.equipment = equipment;
	}
}

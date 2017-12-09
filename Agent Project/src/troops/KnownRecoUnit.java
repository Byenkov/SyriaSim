package troops;

import java.io.Serializable;
import java.util.List;

import jade.core.AID;

public class KnownRecoUnit implements Serializable{
	private AID aid;
	private Allignment allignment;
	private Orders orders;
	private List<KnownUnit> allies;
	private List<KnownUnit> neutrals;
	private List<KnownUnit> enemies;
	
	public AID getAid() {
		return aid;
	}
	public void setAid(AID aid) {
		this.aid = aid;
	}
	public List<KnownUnit> getAllies() {
		return allies;
	}
	public void setAllies(List<KnownUnit> allies) {
		this.allies = allies;
	}
	public List<KnownUnit> getEnemies() {
		return enemies;
	}
	public void setEnemies(List<KnownUnit> enemies) {
		this.enemies = enemies;
	}
	public List<KnownUnit> getNeutrals() {
		return neutrals;
	}
	public void setNeutrals(List<KnownUnit> neutrals) {
		this.neutrals = neutrals;
	}
	
	public Allignment getAllignment() {
		return allignment;
	}
	public void setAllignment(Allignment allignment) {
		this.allignment = allignment;
	}
	public Orders getOrders() {
		return orders;
	}
	public void setOrders(Orders orders) {
		this.orders = orders;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof KnownRecoUnit){
			KnownRecoUnit oth = (KnownRecoUnit) obj;
			return this.aid == oth.getAid();
		}
		return false;
	}

}

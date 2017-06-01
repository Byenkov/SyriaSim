package troops;

import java.util.ArrayList;
import java.util.List;

import jade.core.AID;

public class KnownUnit {
	private AID aid;
	private DivisionInfo divisionInfo;
	private List<KnownUnit> allies;
	private List<KnownUnit> neutrals;
	private List<KnownUnit> enemies;
	
	public KnownUnit(){
		allies = new ArrayList<KnownUnit>();
		neutrals = new ArrayList<KnownUnit>();
		enemies = new ArrayList<KnownUnit>();
	}
	
	public AID getAid() {
		return aid;
	}
	public void setAid(AID aid) {
		this.aid = aid;
	}
	public DivisionInfo getDivisionInfo() {
		return divisionInfo;
	}
	public void setDivisionInfo(DivisionInfo divisionInfo) {
		this.divisionInfo = divisionInfo;
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
	public void addAlly(KnownUnit unit){
		allies.add(unit);
	}
	public void addEnemy(KnownUnit unit){
		enemies.add(unit);
	}
	public void addNeutral(KnownUnit unit){
		neutrals.add(unit);
	}
	public void removeAlly(KnownUnit unit){
		allies.remove(unit);
	}
	public void removeEnemy(KnownUnit unit){
		enemies.remove(unit);
	}
	public void removeNeutral(KnownUnit neutral){
		neutrals.remove(neutral);
	}
	
	public boolean equals(KnownUnit unit){
		return this.aid==unit.getAid();
	}

}

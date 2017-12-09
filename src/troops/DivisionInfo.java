package troops;

import java.io.Serializable;

import jade.core.AID;

public class DivisionInfo implements Serializable{
	/**
	 * 
	 */
	private AID aid;
	private Allignment allignment;
	private double manpower;
	private double equipment;
	private double experience;
	private boolean fortified;
	
	private int phases;
	
	public DivisionInfo(){
		this.manpower =	1000;
		this.equipment = 1000;
		this.experience = 1000;
	}
	
	public double getStrength(){
		return Math.round(((manpower*0.70)+(equipment*1.25)+(experience*1.2)));
	}
	
	public int getSpeed(){
		return Math.round((((int) ((manpower*5)+(equipment*3)-experience*2)/5)));
	}
	
//	public double getInfluencedStrength(double enemyRecoSkill){
//		double randomEvent = 0 + (double)(Math.random() * 0.3); //e.g. weather
//		return getStrength()*(enemyRecoSkill+randomEvent);
//	}
	
	public double getManpower() {
		return manpower;
	}
	public void setManpower(double manpower) {
		this.manpower = (manpower < 0) ? 0 : Math.round(manpower);
	}
	public double getEquipment() {
		return equipment;
	}
	public void setEquipment(double equipment) {
		this.equipment = (equipment < 0) ? 0 : Math.round(equipment);
	}
	public double getExperience() {
		return experience;
	}
	public void setExperience(double experience) {
		 this.experience = (experience < 0) ? 0 :Math.round(experience);
	}
	
	@Override
	public String toString(){
		return "Division: "+ aid.getLocalName()+" of "+allignment +" Manpower: " + manpower + " Equipment: " + equipment + " Experience: " + experience + " Strength: " + getStrength(); 
	}

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}

	public Allignment getAllignment() {
		return allignment;
	}

	public void setAllignment(Allignment allignment2) {
		this.allignment = allignment2;
	}

	public int getPhases() {
		return phases;
	}

	public void setPhases(int phases) {
		this.phases = phases;
	}

	public boolean isFortified() {
		return fortified;
	}

	public void setFortified(boolean fortified) {
		this.fortified = fortified;
	}
	
	
}

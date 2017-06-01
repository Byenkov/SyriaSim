package troops;

import java.io.Serializable;

public class DivisionInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double manpower;
	private double equipment;
	private double experience;
	
	public DivisionInfo(){
		this.manpower =	1000;
		this.equipment = 1000;
		this.experience = 1000;
	}
	
	public DivisionInfo(String manpower, String equipment, String experience, String allignment){
		this.manpower =	Double.parseDouble(manpower);
		this.equipment = Double.parseDouble(equipment);
		this.experience = Double.parseDouble(experience);
	}
	
	public double getStrength(){
		return (manpower*1)+(equipment*0.9)+(experience*0.9);
	}
	
	public int getSpeed(){
		return ((int) ((manpower*5)+(equipment*3)-experience*2)/5);
	}
	
//	public double getInfluencedStrength(double enemyRecoSkill){
//		double randomEvent = 0 + (double)(Math.random() * 0.3); //e.g. weather
//		return getStrength()*(enemyRecoSkill+randomEvent);
//	}
	
	public double getManpower() {
		return manpower;
	}
	public void setManpower(double manpower) {
		this.manpower = manpower;
	}
	public double getEquipment() {
		return equipment;
	}
	public void setEquipment(double equipment) {
		this.equipment = equipment;
	}
	public double getExperience() {
		return experience;
	}
	public void setExperience(double experience) {
		this.experience = experience;
	}
	
	@Override
	public String toString(){
		return " Manpower: " + manpower + " Equipment: " + equipment + " Experience: " + experience; 
	}
	
	
}

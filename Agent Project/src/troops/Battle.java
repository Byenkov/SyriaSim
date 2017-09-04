package troops;

import java.util.Random;

public class Battle {
	private DivisionInfo attacker;
	private DivisionInfo defender;
	private String provinceName;
	
	private double attackerStartingManpower;
	private double defenderStartingManpower;
	
	private double [] attackerStats;
	private double [] defenderStats;

	private int phases;
	
	public Battle(String provinceName, DivisionInfo attacker, DivisionInfo defender){
		this.provinceName = provinceName;
		this.attacker = attacker;
		this.defender = defender;
		this.phases = attacker.getPhases();
		
		attackerStats = new double[3];
		defenderStats = new double[3];
		
		attackerStartingManpower = attacker.getManpower();
		defenderStartingManpower = defender.getManpower();
	}
	
	public void runBattle(){
		int n = (defender.isFortified()) ? 10 : 1;
		phase(3.60*n,3.5*n);
		if (phases > 1) phase(4.55*n,4.58*n);
		if (phases > 2) phase(4.15*n,4.10*n);
	}
	
	private void phase(double attackerBonus, double defenderBonus){
		if (attacker.getStrength() > 0 && defender.getStrength() > 0){
			init();
			
			double attackerPower = attacker.getStrength()/(attackerBonus + new Random().nextDouble());
			double defenderPower = defender.getStrength()/(defenderBonus + new Random().nextDouble());
			
			attacker.setManpower(attacker.getManpower()-defenderPower);
			defender.setManpower(defender.getManpower()-attackerPower);
			
			attacker.setEquipment(attackerStats[1] - (attackerStats[0] - attacker.getManpower())*(1.5 + new Random().nextDouble()));
			defender.setEquipment(defenderStats[1] - (defenderStats[0] - defender.getManpower())*(1.5 + new Random().nextDouble()));
			
			attacker.setExperience(attacker.getExperience()+10);
			defender.setExperience(defender.getExperience()+10);
			
		}
	}

	
	private void init(){
		attackerStats[0] = attacker.getManpower();
		attackerStats[1] = attacker.getEquipment();
		attackerStats[2] = attacker.getExperience();
		defenderStats[0] = defender.getManpower();
		defenderStats[1] = defender.getEquipment();
		defenderStats[2] = defender.getExperience();
	}
	
	
	public DivisionInfo getAttackerResult(){ return attacker; }
	public DivisionInfo getDefenderResult(){ return defender; }
	
	public String getBattleReport(){
		String name = "Warfare in ";
		if (phases == 1) name = "Encounter in ";
		if (phases == 2) name = "Skirmish in ";
		if (phases == 3) name = "Battle of ";
		return 	"\n------------"+ name + provinceName+"------------\n"
				+ "  Attacker: "+attacker.getAid().getLocalName()+":\n"
					+ "    Quantity: " + attackerStartingManpower+"\n"
					+ "    Remaining: "+ ((attacker.getManpower() > 0) ? attacker.getManpower() : 0)+"\n"
				+ "  Defender: "+defender.getAid().getLocalName()+"\n"
					+ "    Quantity: " + defenderStartingManpower +"\n"
					+ "    Remaining: "+ ((defender.getManpower() > 0) ? defender.getManpower() : 0) +"\n"
				+"-----------------------------------------\n";
	}
	
	

}
